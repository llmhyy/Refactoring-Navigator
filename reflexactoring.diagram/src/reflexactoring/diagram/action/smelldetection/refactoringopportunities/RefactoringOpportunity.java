/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import reflexactoring.diagram.bean.ProgramModel;



/**
 * @author linyun
 *
 */
public abstract class RefactoringOpportunity {
	/**
	 * Given a program model, this method is used to simulate the effect of applying
	 * the specific refactoring.
	 * @param model
	 * @return
	 */
	public abstract ProgramModel simulate(ProgramModel model);
	/**
	 * this method is used to apply refactoring on real code
	 */
	public abstract void apply();
	
	protected boolean isValid(ProgramModel model){
		//TODO 
		return false;
	}
}
