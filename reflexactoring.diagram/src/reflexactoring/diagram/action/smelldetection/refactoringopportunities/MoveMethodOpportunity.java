/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.ProgramReference;
import reflexactoring.diagram.bean.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public class MoveMethodOpportunity extends RefactoringOpportunity {

	private UnitMemberWrapper objectMethod;
	private ICompilationUnitWrapper targetUnit;
	
	public MoveMethodOpportunity(UnitMemberWrapper objectMethod, ICompilationUnitWrapper targetUnit){
		this.objectMethod = objectMethod;
		this.targetUnit = targetUnit;
	}
	
	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		
		UnitMemberWrapper objMethod = newModel.findMember(this.objectMethod);
		ICompilationUnitWrapper tarUnit = newModel.findUnit(this.targetUnit.getFullQualifiedName());
		
		/**
		 * change containing relations
		 */
		objMethod.getUnitWrapper().getMembers().remove(objMethod);
		objMethod.setUnitWrapper(tarUnit);
		tarUnit.addMember(objMethod);
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		return newModel;
	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean checkLegal(ProgramModel model) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return the objectMethod
	 */
	public UnitMemberWrapper getObjectMethod() {
		return objectMethod;
	}

	/**
	 * @param objectMethod the objectMethod to set
	 */
	public void setObjectMethod(UnitMemberWrapper objectMethod) {
		this.objectMethod = objectMethod;
	}

	/**
	 * @return the targetUnit
	 */
	public ICompilationUnitWrapper getTargetUnit() {
		return targetUnit;
	}

	/**
	 * @param targetUnit the targetUnit to set
	 */
	public void setTargetUnit(ICompilationUnitWrapper targetUnit) {
		this.targetUnit = targetUnit;
	}

}
