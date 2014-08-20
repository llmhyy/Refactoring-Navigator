/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.window.Window;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.popup.RenameMethodsDialog;
import reflexactoring.diagram.action.recommend.gencode.JavaClassCreator;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.PullUpMemberPrecondition;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.DeclarationInfluenceDetail;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.MethodWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.ProgramReference;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.bean.programmodel.VariableDeclarationWrapper;

/**
 * @author linyun
 * 
 */
public class PullUpMemberToInterfaceOpportunity extends PullUpMemberOpportunity {

	/**
	 * @param toBePulledMemberList
	 */
	public PullUpMemberToInterfaceOpportunity(
			ArrayList<UnitMemberWrapper> toBePulledMemberList,
			ArrayList<ModuleWrapper> moduleList) {
		super(toBePulledMemberList, moduleList);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append(" to created newly interface");
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof PullUpMemberToInterfaceOpportunity){
			PullUpMemberToInterfaceOpportunity thatOpp = (PullUpMemberToInterfaceOpportunity)obj;
			if(thatOpp.isHavingSameMemberList(toBePulledMemberList)){
				return true;
			}
		}
		
		return false;
	} 

	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		/**
		 * create a new interface
		 */
		ICompilationUnitWrapper newInterfaceUnit = createNewUnit(newModel, true);

		/**
		 * create a new method in the newly created interface and change
		 * reference
		 */
		createNewMemberInSuperClass(newModel, newInterfaceUnit);

		newModel.updateUnitCallingRelationByMemberRelations();

		/**
		 * may calculate which module is proper to hold the newly created
		 * interface
		 */
		ModuleWrapper bestMappingModule = calculateBestMappingModule(newModel,
				newInterfaceUnit);
		newInterfaceUnit.setMappingModule(bestMappingModule);
		
		this.targetUnit = newInterfaceUnit;

		return newModel;
	}

	@Override
	public boolean apply() {
		JavaClassCreator javaCreator = new JavaClassCreator();
		ICompilationUnitWrapper parentInterface = javaCreator.createInterface();	
		if(parentInterface == null){
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
		RenameMethodsDialog dialog = new RenameMethodsDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, methodNames);
		dialog.create();
		if(dialog.open() == Window.OK){
			newMethodName = dialog.getNewMethodName();								
		}else{
			return false;
		}
		
		//Create an method in parentInterface
		ICompilationUnit interfaceUnit = parentInterface.getCompilationUnit();
		try{
			interfaceUnit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
			IBuffer interfaceBuffer = interfaceUnit.getBuffer();		
			
			CompilationUnit interfaceCompilationUnit = parse(interfaceUnit);
			interfaceCompilationUnit.recordModifications();
			
			MethodDeclaration mdOfMemberToPull = (MethodDeclaration) memberList.get(0).getJavaElement();								
			MethodDeclaration md = (MethodDeclaration) ASTNode.copySubtree(interfaceCompilationUnit.getAST(), mdOfMemberToPull);
			
			((TypeDeclaration) interfaceCompilationUnit.types().get(0)).bodyDeclarations().add(md);
			md.setName(interfaceCompilationUnit.getAST().newSimpleName(newMethodName));
			md.modifiers().add(interfaceCompilationUnit.getAST().newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
			md.setBody(null);
			
			Document interfaceDocument = new Document(interfaceUnit.getSource());
			TextEdit interfaceTextEdit = interfaceCompilationUnit.rewrite(interfaceDocument, null);
			interfaceTextEdit.apply(interfaceDocument);
			
			interfaceBuffer.setContents(interfaceDocument.get());	
			
			JavaModelUtil.reconcile(interfaceUnit);
			interfaceUnit.commitWorkingCopy(true, new NullProgressMonitor());
			interfaceUnit.discardWorkingCopy();
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
		
		//make every child class implements the interface	
		for(UnitMemberWrapper member : memberList){
			ICompilationUnit unit = member.getUnitWrapper().getCompilationUnit();
			try {
				unit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
				IBuffer buffer = unit.getBuffer();									
				
				CompilationUnit compilationUnit = parse(unit);
				compilationUnit.recordModifications();
				
				TypeDeclaration td = (TypeDeclaration)compilationUnit.types().get(0);	
				Name name = td.getAST().newSimpleName(parentInterface.getName());
				Type type = td.getAST().newSimpleType(name);
				td.superInterfaceTypes().add(type);
				
				Name qualifiedName = createQualifiedName(td.getAST(), parentInterface.getFullQualifiedName());
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
		
		//cast method invocation variable into parent interface
		for(UnitMemberWrapper member : memberList){
			for(ProgramReference reference : member.getRefererPointList()){
				for(VariableDeclarationWrapper variable : reference.getVariableDeclarationList()){
					//cast the declaration into parent interface //TODO
					VariableDeclaration variableAST = variable.getAstNode();
					
					Type currentVariableType = null;
					
					if(variableAST instanceof SingleVariableDeclaration){
						currentVariableType = ((SingleVariableDeclaration) variableAST).getType();
					}else if(variableAST instanceof VariableDeclarationFragment){
						if(((VariableDeclarationFragment) variableAST).getParent() instanceof FieldDeclaration){
							currentVariableType = ((FieldDeclaration) ((VariableDeclarationFragment) variableAST).getParent()).getType();
						}						
					}
					
					
					//cast other references' declaration into child class
					for(DeclarationInfluenceDetail detail : variable.getInfluencedReferenceList()){
						ProgramReference influencedReference = detail.getReference();
						
						if(reference.equals(influencedReference)){
							
							if(reference.getReferenceType() == ProgramReference.METHOD_INVOCATION && influencedReference.getASTNode() instanceof MethodInvocation){
								MethodInvocation invocation = (MethodInvocation) influencedReference.getASTNode();
								//for current pulled method's reference, if casted, remove current casting
								if(detail.getType() == DeclarationInfluenceDetail.ACCESS_OBJECT){
									if(invocation.getParent().getNodeType() == ASTNode.CAST_EXPRESSION){
										this.removeCastExpression(member, invocation.getParent());
									}
								}
								//for current pulled method's reference, if parameter casted, remove current casting
								else if(detail.getType() == DeclarationInfluenceDetail.PARAMETER){
									List<Expression> arguments = invocation.arguments();
									for(Expression args : arguments){
										if(args.getNodeType() == ASTNode.CAST_EXPRESSION){
											this.removeCastExpression(member, args);
										}
									}
								}
								
							}
							//for current pulled method's reference, if assignment casted, remove current casting
							else if(reference.getReferenceType() == ProgramReference.FIELD_ACCESS && influencedReference.getASTNode() instanceof Assignment){
								Assignment assignment = (Assignment) influencedReference.getASTNode();
								if(assignment.getRightHandSide().getNodeType() == ASTNode.CAST_EXPRESSION){
									this.removeCastExpression(member, assignment.getRightHandSide());
								}
							}
												
							
						}else if(!reference.equals(influencedReference) && influencedReference.getASTNode() instanceof MethodInvocation){
							
							MethodInvocation invocation = (MethodInvocation) influencedReference.getASTNode();
							//if variableAST belongs to parent interface, it's not the first time to update, then do nothing
							//otherwise, it's the first time, cast all references to sub class
							if(currentVariableType != null && ! currentVariableType.equals(parentInterface)){
								//TODO
							}
							
						}
						
					}
				}
			}
		}
		
		//call Eclipse API to pull up
//		try {
//			System.out.println(RefactoringAvailabilityTester.isPullUpAvailable(membersToPull));
//			System.out.println(ActionUtil.isEditable(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), membersToPull[0]));
//			if (RefactoringAvailabilityTester.isPullUpAvailable(membersToPull) && ActionUtil.isEditable(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), membersToPull[0]))
//				RefactoringExecutionStarter.startPullUpRefactoring(membersToPull, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
//		} catch (JavaModelException jme) {
//			ExceptionHandler.handle(jme, RefactoringMessages.OpenRefactoringWizardAction_refactoring, RefactoringMessages.OpenRefactoringWizardAction_exception);
//		}	
		
		return true;
	}

	@Override
	protected boolean checkLegal(ProgramModel model) {
		Precondition precondition = new Precondition(getModuleList());
		return precondition.checkLegal(model);
	}
	
	@Override
	public String getRefactoringName() {
		return "Pull Up Member to New Interface";
	}
	
	public ICompilationUnitWrapper getTargetInterface(){
		return this.targetUnit;
	}
	
	@Override
	public ArrayList<String> getRefactoringDetails(){
		ArrayList<String> refactoringDetails = new ArrayList<>();
		String step1 = "Create an interface for ";
		StringBuffer buffer1 = new StringBuffer();
		for(UnitMemberWrapper member: toBePulledMemberList){
			buffer1.append(member.getUnitWrapper().getSimpleName() + ",");
		}
		String str = buffer1.toString();
		str = str.substring(0, str.length()-1);
		step1 += str;
		
		refactoringDetails.add(step1);
		
		String step2 = "create the member " + toBePulledMemberList.get(0).getName() + " in interface";
		refactoringDetails.add(step2);
		
		String step3 = "Those methods refer to ";
		StringBuffer buffer2 = new StringBuffer();
		for(UnitMemberWrapper member: toBePulledMemberList){
			buffer2.append(member.toString()+ ",");
		}
		String memberString = buffer2.toString();
		memberString = memberString.substring(0, memberString.length()-1);
		step3 += memberString;
		step3 += " now refer to the " + toBePulledMemberList.get(0).getName() + " in super class"; 
		refactoringDetails.add(step3);
		
		String step4 = "Move the interface " + this.targetUnit.getName() + " to module " + this.targetUnit.getMappingModule().getName();
		refactoringDetails.add(step4);
		
		return refactoringDetails;
	};

	public class Precondition extends
			PullUpMemberPrecondition {

		/**
		 * @param moduleList
		 */
		public Precondition(ArrayList<ModuleWrapper> moduleList) {
			super(moduleList);
		}

		@Override
		protected ArrayList<RefactoringOpportunity> detectPullingUpOpportunities(ProgramModel model, ArrayList<ArrayList<UnitMemberWrapper>> refactoringPlaceList,
				ArrayList<ModuleWrapper> moduleList) {
			ArrayList<RefactoringOpportunity> opportunities = new ArrayList<>();
			
			for(ArrayList<UnitMemberWrapper> refactoringPlace: refactoringPlaceList){
				UnitMemberWrapper member = refactoringPlace.get(0);
				
				if(member instanceof MethodWrapper){
					PullUpMemberToInterfaceOpportunity opportunity = 
							new PullUpMemberToInterfaceOpportunity(refactoringPlace, moduleList);
					opportunities.add(opportunity);				
				}
			}
			
			return opportunities;
		}
		
		/**
		 * Given data input by a refactoring opportunity, check whether such opportunity still validates.
		 * 
		 * @param model
		 * @param refactoringPlace
		 * @return
		 */
		@Override
		public boolean checkLegal(ProgramModel model) {
			ArrayList<UnitMemberWrapper> newTBPMemberList = new ArrayList<>();
			for(UnitMemberWrapper oldMember: toBePulledMemberList){
				UnitMemberWrapper newMember = model.findMember(oldMember);
				if(newMember != null){
					newTBPMemberList.add(newMember);
				}
			}
			
			if(newTBPMemberList.size() >= 2){
				toBePulledMemberList = newTBPMemberList;
				return true;
			}
			
			return false;
		}
	}
	
	private boolean removeCastExpression(UnitMemberWrapper member, ASTNode node){
		ICompilationUnit unit = member.getUnitWrapper().getCompilationUnit();
		try {
			unit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
			IBuffer buffer = unit.getBuffer();									
			
			CompilationUnit compilationUnit = parse(unit);
			TypeDeclaration td = (TypeDeclaration)compilationUnit.types().get(0);	

			AST ast = td.getAST();
			ASTRewrite rewrite= ASTRewrite.create(ast);								
												
			CastExpression castExpression= (CastExpression) node;
			Expression expression = castExpression.getExpression();
			
			rewrite.replace(node, expression, null);					
			
			Document document = new Document(unit.getSource());
			TextEdit textEdit = rewrite.rewriteAST(document, null);
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
		return true;
	}
}
