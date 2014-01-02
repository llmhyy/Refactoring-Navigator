/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;

import reflexactoring.Module;

/**
 * @author linyun
 *
 */
public class ModuleWrapper {
	private Module module;
	private ArrayList<ICompilationUnitWrapper> mappingList = new ArrayList<>();
	
	/**
	 * @param module
	 */
	public ModuleWrapper(Module module) {
		super();
		this.module = module;
	}
	/**
	 * @return the module
	 */
	public Module getModule() {
		return module;
	}
	/**
	 * @param module the module to set
	 */
	public void setModule(Module module) {
		this.module = module;
	}
	/**
	 * @return the mappingList
	 */
	public ArrayList<ICompilationUnitWrapper> getMappingList() {
		return mappingList;
	}
	/**
	 * @param mappingList the mappingList to set
	 */
	public void setMappingList(ArrayList<ICompilationUnitWrapper> mappingList) {
		this.mappingList = mappingList;
	}
	
	
}
