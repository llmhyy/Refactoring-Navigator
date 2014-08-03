/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.bean.CloneSet;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.PullUpMemberPrecondition;
import reflexactoring.diagram.bean.FieldWrapper;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public class CreateSuperclassAndPullUpMemberOpportunity  extends PullUpMemberOpportunity {

	/**
	 * @param toBePulledMemberList
	 */
	public CreateSuperclassAndPullUpMemberOpportunity(
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
	public void apply() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean checkLegal(ProgramModel model) {
		Precondition precondition = new Precondition(getModuleList());
		return precondition.checkLegal(model);
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
		
		return refactoringDetails;
	};

	public class Precondition extends PullUpMemberPrecondition{

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
				if(isLegal(model, refactoringPlace)){
					CreateSuperclassAndPullUpMemberOpportunity opp = 
							new CreateSuperclassAndPullUpMemberOpportunity(refactoringPlace, moduleList);
					opportunities.add(opp);
				}	
				
			}
			
			return opportunities;
		}
		
		public boolean checkLegal(ProgramModel model){
			ArrayList<UnitMemberWrapper> newTBPMemberList = new ArrayList<>();
			for(UnitMemberWrapper oldMember: toBePulledMemberList){
				UnitMemberWrapper newMember = model.findMember(oldMember);
				if(newMember != null){
					newTBPMemberList.add(newMember);
				}
			}
			
			if(newTBPMemberList.size() >= 2 && isLegal(model, newTBPMemberList)){
				toBePulledMemberList = newTBPMemberList;
				return true;
			}
			
			return false;
		}
		
		private boolean isLegal(ProgramModel model, ArrayList<UnitMemberWrapper> refactoringPlace){
			ICompilationUnitWrapper commonAncestor = findCommonAncestor(refactoringPlace);
			boolean isWithoutAnySuperclass = isWithoutAnySuperclass(refactoringPlace);
			boolean isRelyOnOtherMemberInDeclaringClass = isRelyOnOtherMemberInDeclaringClass(refactoringPlace);
			boolean isWithSimilarBody = isWithSimilarBody(model, refactoringPlace);
			UnitMemberWrapper member = refactoringPlace.get(0);
			
			if((isWithSimilarBody || (member instanceof FieldWrapper)) && !isRelyOnOtherMemberInDeclaringClass &&
					((commonAncestor != null) || (isWithoutAnySuperclass))){
				if(commonAncestor == null){
					return true;
				}
			}
			
			return false;
		}
	}
}
