/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;

/**
 * @author linyun
 *
 */
public class PullUpMemberToInterfaceOpportunity extends PullUpMemberOpportunity {
	
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
		createNewMethod(newModel, newInterfaceUnit);
		
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
