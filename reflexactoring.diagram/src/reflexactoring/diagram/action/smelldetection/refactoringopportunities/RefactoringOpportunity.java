/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.AdvanceEvaluatorAdapter;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
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
	
	/**
	 * @param newUnit
	 * @return
	 */
	protected ModuleWrapper calculateBestMappingModule(ProgramModel model,
			ICompilationUnitWrapper newUnit) {
		
		ModuleWrapper module = null;
		double fitness = 0;
		
		for(ModuleWrapper m: moduleList){
			newUnit.setMappingModule(m);
			double f = new AdvanceEvaluatorAdapter().computeFitness(model, moduleList);
			if(module == null){
				module = m;
				fitness = f;
			}
			else{
				if(f > fitness){
					module = m;
					fitness = f;
				}
			}
		}
		
		return module;
	}
}
