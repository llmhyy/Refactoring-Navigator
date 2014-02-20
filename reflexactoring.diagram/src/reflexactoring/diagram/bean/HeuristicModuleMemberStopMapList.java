/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author linyun
 *
 */
public class HeuristicModuleMemberStopMapList extends ArrayList<HeuristicModuleMemberStopMap>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5029370930310829347L;
	
	public HeuristicModuleMemberStopMap findStopMap(HeuristicModuleMemberStopMap stopMap){
		return findStopMap(stopMap.getModule(), stopMap.getMember());
	}
	
	public HeuristicModuleMemberStopMap findStopMap(ModuleWrapper module, UnitMemberWrapper member){
		for(HeuristicModuleMemberStopMap stopMap: this){
			if(stopMap.getModule().equals(module) 
					&& stopMap.getMember().equals(member)){
				return stopMap;
			}
		}
		
		return null;
	}
	
	@Override
	public boolean add(HeuristicModuleMemberStopMap stopMap){
		HeuristicModuleMemberStopMap map = findStopMap(stopMap);
		if(map == null){
			super.add(stopMap);
		}
		
		return true;
	}

	/**
	 * @param data
	 */
	public void removeMap(HeuristicModuleMemberStopMap map) {
		Iterator<HeuristicModuleMemberStopMap> iterator = this.iterator();
		while(iterator.hasNext()){
			HeuristicModuleMemberStopMap stopMap = iterator.next();
			if(stopMap.getModule().equals(map.getModule()) 
					&& stopMap.getMember().equals(map.getMember())){
				iterator.remove();
			}
		}
	}
}
