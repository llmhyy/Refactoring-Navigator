/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.UnitMemberWrapper;

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
		createNewMember(newModel, superClassUnit);
		
		/**
		 * delete the to-be-pulled members in model
		 */
		for(UnitMemberWrapper member: toBePulledMemberList){
			newModel.removeMember(member);
		}
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		return newModel;
	}

	/**
	 * Find the super class of the member referring class.
	 * 
	 * @param newModel
	 * @return
	 */
	private ICompilationUnitWrapper getSuperClass(ProgramModel newModel) {
		UnitMemberWrapper memberWrapper = toBePulledMemberList.get(0);
		ICompilationUnitWrapper referringUnit = memberWrapper.getUnitWrapper();
		ICompilationUnitWrapper subClassUnit = newModel.findUnit(referringUnit.getFullQualifiedName());
		ICompilationUnitWrapper superClassUnit = subClassUnit.getSuperClass();
		
		return superClassUnit;
	}
	
	@Override
	public void apply() {
		// TODO Auto-generated method stub
		
	}

}
