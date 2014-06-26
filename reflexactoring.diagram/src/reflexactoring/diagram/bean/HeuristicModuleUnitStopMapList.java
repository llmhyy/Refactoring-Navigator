/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Adi
 *
 */
public class HeuristicModuleUnitStopMapList extends ArrayList<HeuristicModuleUnitStopMap>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1947210989017674256L;

	public HeuristicModuleUnitStopMap findStopMap(HeuristicModuleUnitStopMap stopMap){
		return findStopMap(stopMap.getModule(), stopMap.getUnit());
	}
	
	public HeuristicModuleUnitStopMap findStopMap(ModuleWrapper module, ICompilationUnitWrapper unit){
		for(HeuristicModuleUnitStopMap stopMap: this){
			if(stopMap.getModule().equals(module) 
					&& stopMap.getUnit().equals(unit)){
				return stopMap;
			}
		}
		
		return null;
	}
	
	@Override
	public boolean add(HeuristicModuleUnitStopMap stopMap){
		HeuristicModuleUnitStopMap map = findStopMap(stopMap);
		if(map == null){
			super.add(stopMap);
		}
		
		return true;
	}

	/**
	 * @param data
	 */
	public void removeMap(HeuristicModuleUnitStopMap map) {
		Iterator<HeuristicModuleUnitStopMap> iterator = this.iterator();
		while(iterator.hasNext()){
			HeuristicModuleUnitStopMap stopMap = iterator.next();
			if(stopMap.getModule().equals(map.getModule()) 
					&& stopMap.getUnit().equals(map.getUnit())){
				iterator.remove();
			}
		}
	}

}
