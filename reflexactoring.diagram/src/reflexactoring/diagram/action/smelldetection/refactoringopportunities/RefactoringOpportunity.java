/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;



/**
 * @author linyun
 *
 */
public abstract class RefactoringOpportunity {
	
	/**
	 * this API (getModuleList) is very time consuming.
	 */
	protected ArrayList<ModuleWrapper> moduleList;
	
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
	/**
	 * @return the moduleList
	 */
	public ArrayList<ModuleWrapper> getModuleList() {
		return moduleList;
	}
	/**
	 * @param moduleList the moduleList to set
	 */
	public void setModuleList(ArrayList<ModuleWrapper> moduleList) {
		this.moduleList = moduleList;
	}
	
	
}
