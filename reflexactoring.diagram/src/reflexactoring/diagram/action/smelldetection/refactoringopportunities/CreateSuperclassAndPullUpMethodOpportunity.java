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
public class CreateSuperclassAndPullUpMethodOpportunity  extends PullUpMemberOpportunity {

	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		/**
		 * create a new interface
		 */
		ICompilationUnitWrapper newSuperClassUnit = createNewUnit(newModel, false);

		/**
		 * create a new method in the parent class and change reference
		 */
		createNewMethod(newModel, newSuperClassUnit);
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
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

}
