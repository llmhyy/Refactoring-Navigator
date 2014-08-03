/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.PullUpMemberPrecondition;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.UnitMemberWrapper;

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
		return "Pull Up Member to New Interface";
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
}
