/**
 * 
 */
package reflexactoring.diagram.bean;

/**
 * @author Adi
 *
 */
public class HeuristicModuleUnitStopMap {
	private ModuleWrapper module;
	private ICompilationUnitWrapper unit;
	
	/**
	 * @param module
	 * @param unit
	 */
	public HeuristicModuleUnitStopMap(ModuleWrapper module,
			ICompilationUnitWrapper unit) {
		super();
		this.module = module;
		this.unit = unit;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof HeuristicModuleMemberStopMap){
			HeuristicModuleMemberStopMap stopMap = (HeuristicModuleMemberStopMap)obj;
			return stopMap.getMember().equals(this.getModule()) 
					&& stopMap.getMember().equals(this.getUnit());
		}
		
		return false;
	}
	
	public String toString(){
		return this.module.getName() + "><" + unit.getName();
	}
	
	/**
	 * @return the module
	 */
	public ModuleWrapper getModule() {
		return module;
	}
	/**
	 * @param module the module to set
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
	public void setMember(ICompilationUnitWrapper unit) {
		this.unit = unit;
	}
	
	

}
