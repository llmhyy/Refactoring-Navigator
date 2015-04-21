/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;
import java.util.HashMap;

import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.heuristics.HeuristicModuleMemberStopMap;
import reflexactoring.diagram.bean.heuristics.HeuristicModulePartFixMemberMap;
import reflexactoring.diagram.bean.heuristics.HeuristicModuleUnitFixMemberMap;
import reflexactoring.diagram.bean.heuristics.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.heuristics.HeuristicModuleUnitStopMap;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * This class defines the fix-list and stop-list for module, unit and member.
 * 
 * @author linyun
 *
 */
public class Rules {
	
	private HashMap<Integer, Integer> unitModuleFixList;
	private HashMap<Integer, ArrayList<Integer>> unitModuleStopList;
	private HashMap<Integer, Integer> memberModuleFixList;
	private HashMap<Integer, ArrayList<Integer>> memberModuleStopList;
	
	private ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
	
	/**
	 * Create a new Rules instance will get all the available fix/stop lists.
	 */
	public Rules(){
		this.unitModuleFixList = deriveUnitModuleFixList();
		this.unitModuleStopList = deriveUnitModuleStopList();
		this.memberModuleFixList = deriveMemberModuleFixList();
		this.memberModuleStopList = deriveMemberModuleStopList();
	}
	
	private HashMap<Integer, Integer> deriveUnitModuleFixList(){
		HashMap<Integer, Integer> map = new HashMap<>();
		
		for(HeuristicModuleUnitMap m: Settings.heuristicModuleUnitFixList){
			ModuleWrapper module = m.getModule();
			ICompilationUnitWrapper unit = m.getUnit();
			
			int moduleIndex = ReflexactoringUtil.getModuleIndex(moduleList, module);
			int unitIndex = Settings.scope.getICompilationUnitIndex(unit);
			
			map.put(unitIndex, moduleIndex);
		}
		
		return map;
	}
	
	private HashMap<Integer, ArrayList<Integer>> deriveUnitModuleStopList(){
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
		
		for(HeuristicModuleUnitStopMap m: Settings.heuristicModuleUnitStopMapList){
			ModuleWrapper module = m.getModule();
			ICompilationUnitWrapper unit = m.getUnit();
			
			int moduleIndex = ReflexactoringUtil.getModuleIndex(moduleList, module);
			int unitIndex = Settings.scope.getICompilationUnitIndex(unit);
			
			ArrayList<Integer> moduleIndexList;
			if(map.get(unitIndex) != null){
				moduleIndexList = map.get(unitIndex);
			}
			else{
				moduleIndexList = new ArrayList<>();
			}
			moduleIndexList.add(moduleIndex);
			map.put(unitIndex, moduleIndexList);
		}
		
		/**
		 * for frozen modules.
		 */
		for(int i=0; i<Settings.scope.getOutmostTypesInScope().size(); i++){
			ICompilationUnitWrapper unit = Settings.scope.getOutmostTypesInScope().get(i);
			int unitIndex = Settings.scope.getICompilationUnitIndex(unit);
			
			ModuleWrapper module = unit.getMappingModule();
			
			for(ModuleWrapper frozenModule: Settings.frozenModules){
				if(!frozenModule.equals(module)){
					
					int frozenModuleIndex = ReflexactoringUtil.getModuleIndex(moduleList, frozenModule);
					
					ArrayList<Integer> frozenModuleIndexList;
					if(map.get(unitIndex) != null){
						frozenModuleIndexList = map.get(unitIndex);
					}
					else{
						frozenModuleIndexList = new ArrayList<>();
					}
					frozenModuleIndexList.add(frozenModuleIndex);
					map.put(unitIndex, frozenModuleIndexList);
				}
			}
		}
		
		return map;
	}
	
	private HashMap<Integer, Integer> deriveMemberModuleFixList(){
		HashMap<Integer, Integer> map = new HashMap<>();
		/**
		 * Handle both fix-all-member constraints and fix-part-member constraints.
		 */
		for(HeuristicModuleUnitFixMemberMap m: Settings.heuristicModuleUnitMemberFixList){
			ModuleWrapper module = m.getModule();
			ICompilationUnitWrapper unit = m.getUnit();
			
			int moduleIndex = ReflexactoringUtil.getModuleIndex(moduleList, module);
			for(UnitMemberWrapper member: unit.getMembers()){
				int memberIndex = Settings.scope.getUnitMemberIndex(member);
				map.put(memberIndex, moduleIndex);
			}
		}
		
		for(HeuristicModulePartFixMemberMap m: Settings.heuristicModuleMemberPartFixList){
			ModuleWrapper module = m.getModule();
			UnitMemberWrapper member = m.getMember();
			
			int moduleIndex = ReflexactoringUtil.getModuleIndex(moduleList, module);
			int memberIndex = Settings.scope.getUnitMemberIndex(member);
			map.put(memberIndex, moduleIndex);
		}
		
		for(int i=0; i<Settings.scope.getScopeMemberList().size(); i++){
			UnitMemberWrapper member = Settings.scope.getScopeMemberList().get(i);
			ModuleWrapper module = member.getMappingModule();
			
			if(Settings.frozenModules.contains(module)){
				int moduleIndex = ReflexactoringUtil.getModuleIndex(moduleList, module);
				int memberIndex = Settings.scope.getUnitMemberIndex(member);
				
				map.put(memberIndex, moduleIndex);
			}
		}
		
		return map;
	}
	
	private HashMap<Integer, ArrayList<Integer>> deriveMemberModuleStopList(){
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
		
		for(HeuristicModuleMemberStopMap m: Settings.heuristicModuleMemberStopMapList){
			ModuleWrapper module = m.getModule();
			UnitMemberWrapper member = m.getMember();
			
			int moduleIndex = ReflexactoringUtil.getModuleIndex(moduleList, module);
			int memberIndex = Settings.scope.getUnitMemberIndex(member);
			
			ArrayList<Integer> moduleIndexList;
			if(map.get(memberIndex) != null){
				moduleIndexList = map.get(memberIndex);
			}
			else{
				moduleIndexList = new ArrayList<>();
			}
			moduleIndexList.add(moduleIndex);
			map.put(memberIndex, moduleIndexList);
		}
		
		/**
		 * for frozen modules.
		 */
		for(int i=0; i<Settings.scope.getScopeMemberList().size(); i++){
			UnitMemberWrapper member = Settings.scope.getScopeMemberList().get(i);
			int memberIndex = Settings.scope.getUnitMemberIndex(member);
			
			ModuleWrapper module = member.getMappingModule();
			
			for(ModuleWrapper frozenModule: Settings.frozenModules){
				if(!frozenModule.equals(module)){
					
					int frozenModuleIndex = ReflexactoringUtil.getModuleIndex(moduleList, frozenModule);
					
					ArrayList<Integer> frozenModuleIndexList;
					if(map.get(memberIndex) != null){
						frozenModuleIndexList = map.get(memberIndex);
					}
					else{
						frozenModuleIndexList = new ArrayList<>();
					}
					frozenModuleIndexList.add(frozenModuleIndex);
					map.put(memberIndex, frozenModuleIndexList);
				}
			}
		}
		
		return map;
	}

	/**
	 * @return the moduleUnitFixList
	 */
	public HashMap<Integer, Integer> getUnitModuleFixList() {
		return unitModuleFixList;
	}

	/**
	 * @return the moduleUnitStopList
	 */
	public HashMap<Integer, ArrayList<Integer>> getUnitModuleStopList() {
		return unitModuleStopList;
	}

	/**
	 * @return the moduleMemberFixList
	 */
	public HashMap<Integer, Integer> getMemberModuleFixList() {
		return memberModuleFixList;
	}

	/**
	 * @return the moduleMemberStopList
	 */
	public HashMap<Integer, ArrayList<Integer>> getMemberModuleStopList() {
		return memberModuleStopList;
	}
}
