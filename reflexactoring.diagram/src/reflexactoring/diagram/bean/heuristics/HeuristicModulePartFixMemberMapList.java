/**
 * 
 */
package reflexactoring.diagram.bean.heuristics;

import java.util.ArrayList;

import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;

/**
 * @author Adi
 *
 */
public class HeuristicModulePartFixMemberMapList extends ArrayList<HeuristicModulePartFixMemberMap>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HeuristicModulePartFixMemberMap findMap(ModuleWrapper module, UnitMemberWrapper member){
		for(HeuristicModulePartFixMemberMap map: this){
			if(map.getMember().equals(member) && map.getModule().equals(module)){
				return map;
			}
		}
		
		return null;
	}
	
	public HeuristicModulePartFixMemberMap findMap(UnitMemberWrapper member){
		for(HeuristicModulePartFixMemberMap map: this){
			if(map.getMember().equals(member)){
				return map;
			}
		}
		
		return null;
	}
	
	public HeuristicModulePartFixMemberMapList findMap(ICompilationUnitWrapper unit){
		HeuristicModulePartFixMemberMapList list = new HeuristicModulePartFixMemberMapList();
		for(HeuristicModulePartFixMemberMap map: this){
			if(map.getMember().getUnitWrapper().equals(unit)){
				list.add(map);
			}
		}
		
		return list;
	}
	
	public boolean add(HeuristicModulePartFixMemberMap map){
		if(!this.contains(map)){
			
			HeuristicModulePartFixMemberMap conflictMap = findMap(map.getMember());
			if(conflictMap != null){
				this.remove(conflictMap);
			}
			
			super.add(map);
		}
		
		return true;
	}
	
	public void removeMap(HeuristicModulePartFixMemberMap map){
		if(this.contains(map)){
			this.remove(map);
		}
	}

}
