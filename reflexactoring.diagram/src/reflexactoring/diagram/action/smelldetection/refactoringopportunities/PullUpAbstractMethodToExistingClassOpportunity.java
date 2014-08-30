/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.popup.RenameMembersDialog;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class PullUpAbstractMethodToExistingClassOpportunity extends
		PullUpMemberOpportunity {

	/**
	 * @param toBePulledMemberList
	 * @param moduleList
	 */
	public PullUpAbstractMethodToExistingClassOpportunity(
			ArrayList<UnitMemberWrapper> toBePulledMemberList, ICompilationUnitWrapper targetUnit,
			ArrayList<ModuleWrapper> moduleList) {
		super(toBePulledMemberList, moduleList);
		this.targetUnit = targetUnit;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof PullUpAbstractMethodToExistingClassOpportunity){
			PullUpAbstractMethodToExistingClassOpportunity thatOpp = (PullUpAbstractMethodToExistingClassOpportunity)obj;
			if(thatOpp.isHavingSameMemberList(toBePulledMemberList)){
				return true;
			}
		}
		
		return false;
	} 
	
	@Override
	public double computeSimilarityWith(RefactoringOpportunity opp){
		if(opp instanceof PullUpAbstractMethodToExistingClassOpportunity){
			PullUpAbstractMethodToExistingClassOpportunity thatOpp = (PullUpAbstractMethodToExistingClassOpportunity)opp;
			
			double memberSim = ReflexactoringUtil.computeSetSimilarity(toBePulledMemberList, thatOpp.getToBePulledMemberList());
			double unitSim = ReflexactoringUtil.computeSetSimilarity(getUnitsOfToBePulledMembers(), thatOpp.getUnitsOfToBePulledMembers());
			
			return (memberSim + unitSim)/2;
		}
		
		return 0;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append(" (signature) to class ");
		buffer.append(this.targetUnit.getName());
		return buffer.toString();
	}

	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		/**
		 * create a new method in the parent class and change reference
		 */
		ICompilationUnitWrapper newClass = newModel.findUnit(this.targetUnit.getFullQualifiedName());
		createNewMemberInSuperUnit(newModel, newClass, true);
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		return newModel;
	}

	@Override
	public String getRefactoringName() {
		return "Pull Up Abstract Method to Existing Class";
	}

	@Override
	public ArrayList<String> getRefactoringDetails() {
		ArrayList<String> refactoringDetails = new ArrayList<>();
		
		StringBuffer buffer1 = new StringBuffer();
		String str1 = "Pull the abstract method for ";
		buffer1.append(str1);
		for(UnitMemberWrapper member: toBePulledMemberList){
			buffer1.append(member.toString()+ ",");
		}
		String str2 = " in subclasses to class " + this.targetUnit.getName();
		buffer1.append(str2);
		refactoringDetails.add(buffer1.toString());
		
		String step2 = "Those methods refer to ";
		StringBuffer buffer2 = new StringBuffer();
		for(UnitMemberWrapper member: toBePulledMemberList){
			buffer2.append(member.toString()+ ",");
		}
		String memberString = buffer2.toString();
		memberString = memberString.substring(0, memberString.length()-1);
		step2 += memberString;
		step2 += " now refer to the " + toBePulledMemberList.get(0).getName() + " in " + this.targetUnit.getName(); 
		refactoringDetails.add(step2);
		
		return refactoringDetails;
	}

	@Override
	public boolean apply(int position, RefactoringSequence sequence) {
		ICompilationUnitWrapper parentClass = this.targetUnit;

		//get all members to be pulled
		ArrayList<UnitMemberWrapper> memberList = this.getToBePulledMemberList();
		String[] memberNames = new String[memberList.size()];
		for(UnitMemberWrapper memberWrapper : memberList){
			memberNames[memberList.indexOf(memberWrapper)] = memberWrapper.getUnitWrapper().getName() + "." + memberWrapper.getName();
		}

		//show a wizard to rename all the funcions into one name
		String newMemberName = "";
		RenameMembersDialog dialog = new RenameMembersDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, memberNames);
		dialog.create();
		if(dialog.open() == Window.OK){
			newMemberName = dialog.getNewMemberName();								
		}else{
			return false;
		}
		
		//Create an abstract method in parentClass and set corresponding imports
		if(!createAbstractMethodInParent(parentClass, memberList, newMemberName)){
			return false;
		}
		
		//rename each member
		if(!renameMembers(memberList, newMemberName)){
			return false;
		}

		//cast corresponding variable into parent class, summarize a map out first		
		HashMap<ICompilationUnit, ArrayList<ASTNodeInfo>> modificationMap = summarizeCastMap(parentClass, memberList);
		
		//do modifications: add or remove casting
		for(ICompilationUnit icu : modificationMap.keySet()){
			if(!this.modifyCastExpression(modificationMap.get(icu))){
				return false;
			}
		}

		//refresh the model
		refreshModel(position, sequence, parentClass, memberList, newMemberName);
		
		return true;
	}

	@Override
	public boolean checkLegal() {		
		try {
			IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
			project.open(null);
			IJavaProject javaProject = JavaCore.create(project);			
			
			//check whether targetUnit exists or not
			IType targetType = javaProject.findType(targetUnit.getFullQualifiedName());	
			if(targetType == null){
				return false;
			}
			ICompilationUnit targetUnit = targetType.getCompilationUnit();		
			if(targetUnit == null){
				return false;
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		
		return super.checkLegal();
	}
}
