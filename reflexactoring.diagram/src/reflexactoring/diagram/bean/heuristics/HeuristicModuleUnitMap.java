/**
 * 
 */
package reflexactoring.diagram.bean.heuristics;

import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;

/**
 * This class stands for a module-unit mapping relation.
 * 
 * @author linyun
 *
 */
public class HeuristicModuleUnitMap {
	private ModuleWrapper module;
	private ICompilationUnitWrapper unit;
	/**
	 * @param module
	 * @param unit
	 */
	public HeuristicModuleUnitMap(ModuleWrapper module,
			ICompilationUnitWrapper unit) {
		super();
		this.module = module;
		this.unit = unit;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof HeuristicModuleUnitMap){
			HeuristicModuleUnitMap map = (HeuristicModuleUnitMap)obj;
			return map.getModule().getName().equals(this.module.getName()) 
					&& map.getUnit().getFullQualifiedName().equals(this.unit.getFullQualifiedName());
		}
		
		return false;
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
	public void setUnit(ICompilationUnitWrapper unit) {
		this.unit = unit;
	}
	
	
}
