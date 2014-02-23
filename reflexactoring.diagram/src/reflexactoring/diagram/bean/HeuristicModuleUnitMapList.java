/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;

/**
 * @author linyun
 *
 */
public class HeuristicModuleUnitMapList extends ArrayList<HeuristicModuleUnitMap>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5077806055903361433L;
	
	/**
	 * when adding a map, this method will first automatically remove other maps corresponding to
	 * the unit in to-be-added map. And then, the new map will be added.
	 * @param map
	 */
	public boolean add(HeuristicModuleUnitMap map){
		if(!this.contains(map)){
			
			HeuristicModuleUnitMap conflictMap = findHeuristicMapping(map.getUnit());
			if(conflictMap != null){
				this.remove(conflictMap);
			}
			
			super.add(map);
		}
		
		return true;
	}
	
	public void removeMap(HeuristicModuleUnitMap map){
		if(this.contains(map)){
			this.remove(map);
		}
	}
	
	public HeuristicModuleUnitMap findHeuristicMapping(ICompilationUnitWrapper type){
		for(HeuristicModuleUnitMap map: this){
			if(map.getUnit().equals(type)){
				return map;
			}
		}
		
		return null;
	}
	
	public HeuristicModuleUnitMap findHeuristicMapping(String identifier){
		for(HeuristicModuleUnitMap map: this){
			if(map.getUnit().getFullQualifiedName().equals(identifier)){
				return map;
			}
		}
		
		return null;
	}
}
