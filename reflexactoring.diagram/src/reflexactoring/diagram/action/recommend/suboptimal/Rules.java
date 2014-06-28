/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.ui.PartInitException;

import reflexactoring.diagram.bean.HeuristicModuleMemberStopMap;
import reflexactoring.diagram.bean.HeuristicModulePartFixMemberMap;
import reflexactoring.diagram.bean.HeuristicModuleUnitFixMemberMap;
import reflexactoring.diagram.bean.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapper;
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
			
			int moduleIndex = ReflexactoringUtil.getModuleIndex(ReflexactoringUtil.getModuleList(Settings.diagramPath), module);
			int unitIndex = Settings.scope.getICompilationUnitIndex(unit);
			
			map.put(unitIndex, moduleIndex);
		}
		
		return map;
	}
	
	private HashMap<Integer, ArrayList<Integer>> deriveUnitModuleStopList(){
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
		
		//TODO for Adi
		
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
			
			int moduleIndex = ReflexactoringUtil.getModuleIndex(ReflexactoringUtil.getModuleList(Settings.diagramPath), module);
			for(UnitMemberWrapper member: unit.getMembers()){
				int memberIndex = Settings.scope.getUnitMemberIndex(member);
				map.put(memberIndex, moduleIndex);
			}
		}
		
		for(HeuristicModulePartFixMemberMap m: Settings.heuristicModuleMemberPartFixList){
			ModuleWrapper module = m.getModule();
			UnitMemberWrapper member = m.getMember();
			
			int moduleIndex = ReflexactoringUtil.getModuleIndex(ReflexactoringUtil.getModuleList(Settings.diagramPath), module);
			int memberIndex = Settings.scope.getUnitMemberIndex(member);
			map.put(memberIndex, moduleIndex);
		}
		
		int count=0;
		for(int i=0; i<Settings.scope.getScopeMemberList().size(); i++){
			UnitMemberWrapper member = Settings.scope.getScopeMemberList().get(i);
			ModuleWrapper module = member.getMappingModule();
			
			if(Settings.frozenModules.contains(module)){
				int moduleIndex = ReflexactoringUtil.getModuleIndex(ReflexactoringUtil.getModuleList(Settings.diagramPath), module);
				int memberIndex = Settings.scope.getUnitMemberIndex(member);
				
				map.put(memberIndex, moduleIndex);
				
				count++;
			}
		}
		
		return map;
	}
	
	private HashMap<Integer, ArrayList<Integer>> deriveMemberModuleStopList(){
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
		
		for(HeuristicModuleMemberStopMap m: Settings.heuristicModuleMemberStopMapList){
			ModuleWrapper module = m.getModule();
			UnitMemberWrapper member = m.getMember();
			
			int moduleIndex = ReflexactoringUtil.getModuleIndex(ReflexactoringUtil.getModuleList(Settings.diagramPath), module);
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
