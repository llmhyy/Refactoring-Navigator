/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;

/**
 * This class contains the similarity values between a certain module and each
 * java type within scope.
 * 
 * @author linyun
 * 
 */
public class ModuleUnitsSimilarity {
	private ModuleWrapper module;
	private ArrayList<ICompilationUnitWrapper> units;
	private double[] values;

	/**
	 * @param module
	 * @param units
	 * @param values
	 */
	public ModuleUnitsSimilarity(ModuleWrapper module,
			ArrayList<ICompilationUnitWrapper> units, double[] values) {
		super();
		this.module = module;
		this.units = units;
		this.values = values;
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
	 * @return the units
	 */
	public ArrayList<ICompilationUnitWrapper> getUnits() {
		return units;
	}

	/**
	 * @param units
	 *            the units to set
	 */
	public void setUnits(ArrayList<ICompilationUnitWrapper> units) {
		this.units = units;
	}

	/**
	 * @return the values
	 */
	public double[] getValues() {
		return values;
	}

	/**
	 * @param values
	 *            the values to set
	 */
	public void setValues(double[] values) {
		this.values = values;
	}

}
