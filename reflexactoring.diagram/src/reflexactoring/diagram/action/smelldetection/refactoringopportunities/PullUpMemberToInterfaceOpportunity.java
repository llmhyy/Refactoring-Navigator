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
public class PullUpMemberToInterfaceOpportunity extends PullUpMemberOpportunity {
	
	/**
	 * @param toBePulledMemberList
	 */
	public PullUpMemberToInterfaceOpportunity(
			ArrayList<UnitMemberWrapper> toBePulledMemberList, ArrayList<ModuleWrapper> moduleList) {
		super(toBePulledMemberList, moduleList);
	}
	
	@Override
	public String toString(){
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
		 * create a new method in the newly created interface and change reference
		 */
		createNewMember(newModel, newInterfaceUnit);
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		/**
		 * may calculate which module is proper to hold the newly created interface
		 */
		ModuleWrapper bestMappingModule = calculateBestMappingModule(newModel, newInterfaceUnit);
		newInterfaceUnit.setMappingModule(bestMappingModule);
		
		return newModel;
	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub
		
	}

}
