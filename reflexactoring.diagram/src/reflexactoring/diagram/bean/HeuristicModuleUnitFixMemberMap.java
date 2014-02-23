/**
 * 
 */
package reflexactoring.diagram.bean;

/**
 * @author linyun
 *
 */
public class HeuristicModuleUnitFixMemberMap {
	private ModuleWrapper module;
	private ICompilationUnitWrapper unit;
	/**
	 * @param modoule
	 * @param unit
	 */
	public HeuristicModuleUnitFixMemberMap(ModuleWrapper modoule,
			ICompilationUnitWrapper unit) {
		super();
		this.module = modoule;
		this.unit = unit;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HeuristicModuleUnitFixMemberMap other = (HeuristicModuleUnitFixMemberMap) obj;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		return true;
	}



	/**
	 * @return the modoule
	 */
	public ModuleWrapper getModule() {
		return module;
	}
	/**
	 * @param modoule the modoule to set
	 */
	public void setModule(ModuleWrapper module) {
		this.module = module;
	}
	/**
	 * @return the unit
	 */
	public ICompilationUnitWrapper getUnit() {
		return unit;
	}
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(ICompilationUnitWrapper unit) {
		this.unit = unit;
	}
	
	
	
}
