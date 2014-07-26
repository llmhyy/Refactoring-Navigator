/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ProgramModel;

/**
 * @author linyun
 *
 */
public class PullUpMemberToSuperclassOpportunity extends PullUpMemberOpportunity{

	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		/**
		 * get the parent class
		 */
		ICompilationUnitWrapper superClassUnit = getSuperClass(newModel);
		
		/**
		 * create a new method in the parent class and change reference
		 */
		createNewMethod(newModel, superClassUnit);
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		return newModel;
	}

	/**
	 * Find the super class of the method referring class.
	 * 
	 * @param newModel
	 * @return
	 */
	private ICompilationUnitWrapper getSuperClass(ProgramModel newModel) {
		MethodWrapper methodWrapper = (MethodWrapper)toBePulledMemberList.get(0);
		ICompilationUnitWrapper referringUnit = methodWrapper.getUnitWrapper();
		ICompilationUnitWrapper subClassUnit = newModel.findUnit(referringUnit.getFullQualifiedName());
		ICompilationUnitWrapper superClassUnit = subClassUnit.getSuperClass();
		
		return superClassUnit;
	}
	
	@Override
	public void apply() {
		// TODO Auto-generated method stub
		
	}

}
