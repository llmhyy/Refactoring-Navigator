/**
 * 
 */
package reflexactoring.diagram.bean.heuristics;

import java.util.ArrayList;

import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;

/**
 * @author linyun
 *
 */
public class HeuristicModuleUnitFixMemberMapList extends ArrayList<HeuristicModuleUnitFixMemberMap>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6717113623144952685L;
	
	public HeuristicModuleUnitFixMemberMap findMap(ModuleWrapper module, ICompilationUnitWrapper unit){
		for(HeuristicModuleUnitFixMemberMap map: this){
			if(map.getUnit().equals(unit) && map.getModule().equals(module)){
				return map;
			}
		}
		
		return null;
	}
	
	public HeuristicModuleUnitFixMemberMap findMap(ICompilationUnitWrapper unit){
		for(HeuristicModuleUnitFixMemberMap map: this){
			if(map.getUnit().equals(unit)){
				return map;
			}
		}
		
		return null;
	}
	
	public boolean add(HeuristicModuleUnitFixMemberMap map){
		if(!this.contains(map)){
			
			HeuristicModuleUnitFixMemberMap conflictMap = findMap(map.getUnit());
			if(conflictMap != null){
				this.remove(conflictMap);
			}
			
			super.add(map);
		}
		
		return true;
	}
	
	public void removeMap(HeuristicModuleUnitFixMemberMap map){
		if(this.contains(map)){
			this.remove(map);
		}
	}
}
