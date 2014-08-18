/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;

/**
 * @author linyun
 *
 */
public abstract class RefactoringPrecondition {
	private ArrayList<ModuleWrapper> moduleList;
	public abstract ArrayList<RefactoringOpportunity> detectOpportunities(ProgramModel model);
	public abstract boolean checkLegal(ProgramModel model);
	
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
