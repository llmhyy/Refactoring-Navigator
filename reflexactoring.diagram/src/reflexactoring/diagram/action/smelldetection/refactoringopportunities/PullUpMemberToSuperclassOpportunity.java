/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public class PullUpMemberToSuperclassOpportunity extends PullUpMemberOpportunity{

	/**
	 * @param toBePulledMemberList
	 */
	public PullUpMemberToSuperclassOpportunity(
			ArrayList<UnitMemberWrapper> toBePulledMemberList, ArrayList<ModuleWrapper> moduleList, 
			ICompilationUnitWrapper targetUnit) {
		super(toBePulledMemberList, moduleList);
		this.targetUnit = targetUnit;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append(" to super class " + targetUnit.toString());
		return buffer.toString();
	}

	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		
		/**
		 * create a new method in the parent class and change reference
		 */
		createNewMember(newModel, this.targetUnit);
		
		/**
		 * delete the to-be-pulled members in model
		 */
		for(UnitMemberWrapper member: toBePulledMemberList){
			newModel.removeMember(member);
		}
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		return newModel;
	}
	
	@Override
	public void apply() {
		// TODO Auto-generated method stub
		
	}

}
