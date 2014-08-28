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
import reflexactoring.diagram.action.smelldetection.bean.CloneSet;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.FieldWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class PullUpConcreteMemberToExistingClassOpportunity extends PullUpMemberOpportunity{

	/**
	 * @param toBePulledMemberList
	 */
	public PullUpConcreteMemberToExistingClassOpportunity(
			ArrayList<UnitMemberWrapper> toBePulledMemberList, ArrayList<ModuleWrapper> moduleList, 
			ICompilationUnitWrapper targetUnit) {
		super(toBePulledMemberList, moduleList);
		this.targetUnit = targetUnit;
	}
	
	@Override
	public double computeSimilarityWith(RefactoringOpportunity opp){
		if(opp instanceof PullUpConcreteMemberToExistingClassOpportunity){
			PullUpConcreteMemberToExistingClassOpportunity thatOpp = (PullUpConcreteMemberToExistingClassOpportunity)opp;
			
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
		buffer.append(" to super class " + targetUnit.toString());
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof PullUpConcreteMemberToExistingClassOpportunity){
			PullUpConcreteMemberToExistingClassOpportunity thatOpp = (PullUpConcreteMemberToExistingClassOpportunity)obj;
			if(thatOpp.isHavingSameMemberList(toBePulledMemberList) && 
					thatOpp.getTargetSuperclass().equals(getTargetSuperclass())){
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
		 * create a new method in the parent class and change reference
		 */
		ICompilationUnitWrapper newSuperclass = newModel.findUnit(this.targetUnit.getFullQualifiedName());
		createNewMemberInSuperUnit(newModel, newSuperclass, false);
		
		/**
		 * delete the to-be-pulled members in model
		 */
		for(UnitMemberWrapper member: toBePulledMemberList){
			newModel.removeMember(member);
		}
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		this.targetUnit = newSuperclass;
		
		return newModel;
	}
	
	@Override
	public boolean apply(int position, RefactoringSequence sequence) {		
		ICompilationUnitWrapper parentClass = this.targetUnit;
		boolean isField = this.getToBePulledMemberList().get(0) instanceof FieldWrapper;

		//get all members to be pulled
		ArrayList<UnitMemberWrapper> memberList = this.getToBePulledMemberList();
		String[] memberNames = new String[memberList.size()];
		for(UnitMemberWrapper memberWrapper : memberList){
			memberNames[memberList.indexOf(memberWrapper)] = memberWrapper.getUnitWrapper().getName() + "." + memberWrapper.getName();
		}
		
		//show a wizard to rename all the members into one name
		String newMemberName = "";
		RenameMembersDialog dialog = new RenameMembersDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, memberNames);
		dialog.create();
		if(dialog.open() == Window.OK){
			newMemberName = dialog.getNewMemberName();								
		}else{
			return false;
		}
		
		//create member in parent class 
		if(isField){
			//create the declaration and initialization of field in parent class
			if(!createConcreteFieldInParent(parentClass, memberList, newMemberName)){
				return false;
			}
		}else{
			//Create a concrete method in parent class and set corresponding imports
			if(!createConcreteMethodInParent(parentClass, memberList, newMemberName)){
				return false;
			}
		}
									
		//rename each member
		if(!renameMembers(memberList, newMemberName)){
			return false;
		}
		
		//remove the declaration in child classes
		for(UnitMemberWrapper member : memberList){
			if(!removeConcreteMemberInChild(member, newMemberName)){
				return false;
			}
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
	
	@Override
	public String getRefactoringName() {
		return "Pull Up Member to Existing Class";
	}
	
	public ICompilationUnitWrapper getTargetSuperclass(){
		return this.targetUnit;
	}
	
	@Override
	public ArrayList<String> getRefactoringDetails(){
		ArrayList<String> refactoringDetails = new ArrayList<>();
		
		String step1 = "Pull the member " + toBePulledMemberList.get(0).getName() + " in subclasses to class" + this.targetUnit.getName();
		refactoringDetails.add(step1);
		
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
	};
}
