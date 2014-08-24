/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.window.Window;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.popup.RenameMembersDialog;
import reflexactoring.diagram.action.recommend.gencode.JavaClassCreator;
import reflexactoring.diagram.action.smelldetection.bean.CloneSet;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.PullUpMemberPrecondition;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.FieldWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public class PullUpMemberToNewClassOpportunity  extends PullUpMemberOpportunity {

	/**
	 * @param toBePulledMemberList
	 */
	public PullUpMemberToNewClassOpportunity(
			ArrayList<UnitMemberWrapper> toBePulledMemberList, ArrayList<ModuleWrapper> moduleList) {
		super(toBePulledMemberList, moduleList);
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append(" to newly created super class");
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof PullUpMemberToNewClassOpportunity){
			PullUpMemberToNewClassOpportunity thatOpp = (PullUpMemberToNewClassOpportunity)obj;
			if(isHavingSameMemberList(thatOpp.getToBePulledMemberList())){
				return true;
			}
		}
		
		return false;
	} 

	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		/**
		 * remove relevant clone set
		 */
		ArrayList<CloneSet> setList = newModel.findCloneSet(toBePulledMemberList);
		for(CloneSet set: setList){
			newModel.getCloneSets().remove(set);			
		}
		
		/**
		 * create a new class
		 */
		ICompilationUnitWrapper newSuperClassUnit = createNewUnit(newModel, false);

		/**
		 * create a new method in the parent class and change reference
		 */
		createNewMemberInSuperClass(newModel, newSuperClassUnit);
		
		/**
		 * delete the to-be-pulled members in model
		 */
		for(UnitMemberWrapper oldMember: toBePulledMemberList){
			UnitMemberWrapper newMember = newModel.findMember(oldMember);
			newModel.removeMember(newMember);
		}
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		/**
		 * may calculate which module is proper to hold the newly created super class
		 */
		ModuleWrapper bestMappingModule = calculateBestMappingModule(newModel, newSuperClassUnit);
		newSuperClassUnit.setMappingModule(bestMappingModule);
		
		this.targetUnit = newSuperClassUnit;
		
		return newModel;
	}

	@Override
	public boolean apply(int position, RefactoringSequence sequence) {
		JavaClassCreator javaCreator = new JavaClassCreator();
		ICompilationUnitWrapper parentClass = javaCreator.createClass();	
		if(parentClass == null){
			return false;
		}

		//get all members to be pulled
		ArrayList<UnitMemberWrapper> memberList = this.getToBePulledMemberList();
		IMember[] members = new IMember[memberList.size()];
		String[] methodNames = new String[memberList.size()];
		for(UnitMemberWrapper memberWrapper : memberList){
			members[memberList.indexOf(memberWrapper)] = memberWrapper.getJavaMember();	
			methodNames[memberList.indexOf(memberWrapper)] = memberWrapper.getUnitWrapper().getName() + "." + memberWrapper.getName();
		}
		
		//show a wizard to rename all the funcions into one name
		String newMethodName = "";
		RenameMembersDialog dialog = new RenameMembersDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, methodNames);
		dialog.create();
		if(dialog.open() == Window.OK){
			newMethodName = dialog.getNewMemberName();								
		}else{
			return false;
		}
		
		//Create an method in parentClass
		ICompilationUnit parentClassUnit = parentClass.getCompilationUnit();
		try{
			parentClassUnit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
			IBuffer parentClassBuffer = parentClassUnit.getBuffer();		
			
			CompilationUnit parentClassCompilationUnit = RefactoringOppUtil.parse(parentClassUnit);
			parentClassCompilationUnit.recordModifications();
			
			MethodDeclaration mdOfMemberToPull = (MethodDeclaration) memberList.get(0).getJavaElement();								
			MethodDeclaration md = (MethodDeclaration) ASTNode.copySubtree(parentClassCompilationUnit.getAST(), mdOfMemberToPull);
			
			((TypeDeclaration) parentClassCompilationUnit.types().get(0)).bodyDeclarations().add(md);
			md.setName(parentClassCompilationUnit.getAST().newSimpleName(newMethodName));
			Block block = parentClassCompilationUnit.getAST().newBlock();
			md.setBody(block);
			
			Document parentClassDocument = new Document(parentClassUnit.getSource());
			TextEdit parentClassTextEdit = parentClassCompilationUnit.rewrite(parentClassDocument, null);
			parentClassTextEdit.apply(parentClassDocument);
			
			parentClassBuffer.setContents(parentClassDocument.get());	
			
			JavaModelUtil.reconcile(parentClassUnit);
			parentClassUnit.commitWorkingCopy(true, new NullProgressMonitor());
			parentClassUnit.discardWorkingCopy();
		} catch (JavaModelException e1) {
			e1.printStackTrace();
			return false;
		} catch (MalformedTreeException e1) {
			e1.printStackTrace();
			return false;
		} catch (BadLocationException e1) {
			e1.printStackTrace();
			return false;
		}							
		
		//make every child class extends the superclass	
		for(UnitMemberWrapper member : this.getToBePulledMemberList()){
			ICompilationUnit unit = member.getUnitWrapper().getCompilationUnit();
			try {
				unit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
				IBuffer buffer = unit.getBuffer();									
				
				CompilationUnit compilationUnit = RefactoringOppUtil.parse(unit);
				compilationUnit.recordModifications();
				
				TypeDeclaration td = (TypeDeclaration)compilationUnit.types().get(0);	
				Name name = td.getAST().newSimpleName(parentClass.getName());
				Type type = td.getAST().newSimpleType(name);
				td.setSuperclassType(type);
				
				Name qualifiedName = RefactoringOppUtil.createQualifiedName(td.getAST(), parentClass.getFullQualifiedName());
				ImportDeclaration importDeclaration = td.getAST().newImportDeclaration();
				importDeclaration.setName(qualifiedName);
				importDeclaration.setOnDemand(false);
				compilationUnit.imports().add(importDeclaration);
				
				Document document = new Document(unit.getSource());
				TextEdit textEdit = compilationUnit.rewrite(document, null);
				textEdit.apply(document);
				
				buffer.setContents(document.get());	
				
				JavaModelUtil.reconcile(unit);
				unit.commitWorkingCopy(true, new NullProgressMonitor());
				unit.discardWorkingCopy();
				
			} catch (JavaModelException e1) {
				e1.printStackTrace();
				return false;
			} catch (MalformedTreeException e1) {
				e1.printStackTrace();
				return false;
			} catch (BadLocationException e1) {
				e1.printStackTrace();
				return false;
			}
		}	
									
		//rename each method
		for(UnitMemberWrapper memberWrapper : memberList){	
			try {									
				IMethod methodToRename = (IMethod) memberWrapper.getJavaMember();
				
				RenameSupport support = RenameSupport.create(methodToRename, newMethodName, RenameSupport.UPDATE_REFERENCES);
				support.perform(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				
			} catch (CoreException e1) {
				e1.printStackTrace();
				return false;
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
				return false;
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				return false;
			}								
								
		}
		
		return true;
	}

	@Override
	protected boolean checkLegal(ProgramModel model) {
		//TODO
		return true;
	}
	
	@Override
	public String getRefactoringName() {
		return "Pull Up Member to Created Super Class";
	}
	
	@Override
	public ArrayList<String> getRefactoringDetails(){
		ArrayList<String> refactoringDetails = new ArrayList<>();
		String step1 = "Create a super class for ";
		StringBuffer buffer1 = new StringBuffer();
		for(UnitMemberWrapper member: toBePulledMemberList){
			buffer1.append(member.getUnitWrapper().getSimpleName() + ",");
		}
		String str = buffer1.toString();
		str = str.substring(0, str.length()-1);
		step1 += str;
		
		refactoringDetails.add(step1);
		
		String step2 = "Pull the member " + toBePulledMemberList.get(0).getName() + " in subclasses to " + targetUnit.getName();
		refactoringDetails.add(step2);
		
		String step3 = "Those methods refer to ";
		StringBuffer buffer2 = new StringBuffer();
		for(UnitMemberWrapper member: toBePulledMemberList){
			buffer2.append(member.toString()+ ",");
		}
		String memberString = buffer2.toString();
		memberString = memberString.substring(0, memberString.length()-1);
		step3 += memberString;
		step3 += " now refer to the " + toBePulledMemberList.get(0).getName() + " in "  + targetUnit.getName(); 
		refactoringDetails.add(step3);
		
		String step4 = "Move the superclass " + this.targetUnit.getName() + " to module " + this.targetUnit.getMappingModule().getName();
		refactoringDetails.add(step4);
		
		return refactoringDetails;
	};
}
