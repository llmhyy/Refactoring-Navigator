/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.bean.CloneSet;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.ExtractUtilityClassOpportunity.Precondition;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.PullUpMemberPrecondition;
import reflexactoring.diagram.bean.FieldWrapper;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
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
		 * create a new class
		 */
		ICompilationUnitWrapper newSuperClassUnit = createNewUnit(newModel, false);

		/**
		 * create a new method in the parent class and change reference
		 */
		createNewMember(newModel, newSuperClassUnit);
		
		/**
		 * delete the to-be-pulled members in model
		 */
		for(UnitMemberWrapper member: toBePulledMemberList){
			newModel.removeMember(member);
		}
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		/**
		 * remove relevant clone set
		 */
		CloneSet set = newModel.findCloneSet(toBePulledMemberList.get(0));
		newModel.getCloneSets().remove(set);
		
		/**
		 * may calculate which module is proper to hold the newly created super class
		 */
		ModuleWrapper bestMappingModule = calculateBestMappingModule(newModel, newSuperClassUnit);
		newSuperClassUnit.setMappingModule(bestMappingModule);
		
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
			boolean isWithSimilarBody = isWithSimilarBody(model, refactoringPlace);
			UnitMemberWrapper member = refactoringPlace.get(0);
			
			if((isWithSimilarBody || (member instanceof FieldWrapper)) && ((commonAncestor != null) || (isWithoutAnySuperclass))){
				if(commonAncestor == null){
					return true;
				}
			}
			
			return false;
		}
	}
}
