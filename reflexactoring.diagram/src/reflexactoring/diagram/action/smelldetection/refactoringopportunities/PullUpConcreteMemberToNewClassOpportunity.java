/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.popup.RenameMembersDialog;
import reflexactoring.diagram.action.recommend.gencode.JavaClassCreator;
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
public class PullUpConcreteMemberToNewClassOpportunity  extends PullUpMemberOpportunity {

	/**
	 * @param toBePulledMemberList
	 */
	public PullUpConcreteMemberToNewClassOpportunity(
			ArrayList<UnitMemberWrapper> toBePulledMemberList, ArrayList<ModuleWrapper> moduleList) {
		super(toBePulledMemberList, moduleList);
	}
	
	@Override
	public double computeSimilarityWith(RefactoringOpportunity opp){
		if(opp instanceof PullUpConcreteMemberToNewClassOpportunity){
			PullUpConcreteMemberToNewClassOpportunity thatOpp = (PullUpConcreteMemberToNewClassOpportunity)opp;
			
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
		buffer.append(" to newly created super class");
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof PullUpConcreteMemberToNewClassOpportunity){
			PullUpConcreteMemberToNewClassOpportunity thatOpp = (PullUpConcreteMemberToNewClassOpportunity)obj;
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
//		ArrayList<CloneSet> setList = newModel.findCloneSet(toBePulledMemberList);
//		for(CloneSet set: setList){
//			newModel.getCloneSets().remove(set);			
//		}
		
		/**
		 * create a new class
		 */
		ICompilationUnitWrapper newSuperClassUnit = createNewUnit(newModel, false);

		/**
		 * create a new method in the parent class and change reference
		 */
		createNewMemberInSuperUnit(newModel, newSuperClassUnit, false);
		
		/**
		 * delete the to-be-pulled members in model
		 */
		for(UnitMemberWrapper oldMember: toBePulledMemberList){
			UnitMemberWrapper newMember = newModel.findMember(oldMember);
			newModel.removeMember(newMember);
		}
		
		newModel.updateUnitCallingRelationByMemberRelations();
		newModel.updateUnitDescription();
		
		//System.currentTimeMillis();
		
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
		//create new class
		JavaClassCreator javaCreator = new JavaClassCreator();
		ICompilationUnitWrapper parentClass = javaCreator.createClass();	
		if(parentClass == null){
			return false;
		}		
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

		//make every child class extends the class	
		if(!addSubClassExtends(parentClass, memberList)){
			return false;
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
		return super.checkLegal();
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
