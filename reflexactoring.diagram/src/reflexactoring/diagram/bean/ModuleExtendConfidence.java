package reflexactoring.diagram.bean;

import java.util.ArrayList;

/**
 * @author Adi
 * 
 */
public class ModuleExtendConfidence {
	private ModuleWrapper module;
	private ArrayList<ModuleWrapper> moduleList;
	private double[] confidenceList;

	/**
	 * @param module
	 * @param moduleList
	 * @param confidenceList
	 */
	public ModuleExtendConfidence(ModuleWrapper module,
			ArrayList<ModuleWrapper> moduleList, double[] confidenceList) {
		super();
		this.module = module;
		this.moduleList = moduleList;
		this.confidenceList = confidenceList;
	}

	/**
	 * @return the module
	 */
	public ModuleWrapper getModule() {
		return module;
	}

	/**
	 * @param module
	 *            the module to set
	 */
	public void setModule(ModuleWrapper module) {
		this.module = module;
	}

	/**
	 * @return the moduleList
	 */
	public ArrayList<ModuleWrapper> getModuleList() {
		return moduleList;
	}

	/**
	 * @param moduleList
	 *            the moduleList to set
	 */
	public void setModuleList(ArrayList<ModuleWrapper> moduleList) {
		this.moduleList = moduleList;
	}

	/**
	 * @return the confidenceList
	 */
	public double[] getConfidenceList() {
		return confidenceList;
	}

	/**
	 * @param confidenceList
	 *            the confidenceList to set
	 */
	public void setConfidenceList(double[] confidenceList) {
		this.confidenceList = confidenceList;
	}

}
