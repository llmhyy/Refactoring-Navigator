/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

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
	private HashMap<Integer, Integer> unitModuleStopList;
	private HashMap<Integer, Integer> memberModuleFixList;
	private HashMap<Integer, Integer> memberModuleStopList;
	
	
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
			
			try {
				int moduleIndex = ReflexactoringUtil.getModuleIndex(ReflexactoringUtil.getModuleList(Settings.diagramPath), module);
				int unitIndex = Settings.scope.getICompilationUnitIndex(unit);
				
				map.put(unitIndex, moduleIndex);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	private HashMap<Integer, Integer> deriveUnitModuleStopList(){
		HashMap<Integer, Integer> map = new HashMap<>();
		
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
			
			try {
				int moduleIndex = ReflexactoringUtil.getModuleIndex(ReflexactoringUtil.getModuleList(Settings.diagramPath), module);
				for(UnitMemberWrapper member: unit.getMembers()){
					int memberIndex = Settings.scope.getUnitMemberIndex(member);
					map.put(memberIndex, moduleIndex);
				}
				
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		
		for(HeuristicModulePartFixMemberMap m: Settings.heuristicModuleMemberPartFixList){
			ModuleWrapper module = m.getModule();
			UnitMemberWrapper member = m.getMember();
			
			try {
				int moduleIndex = ReflexactoringUtil.getModuleIndex(ReflexactoringUtil.getModuleList(Settings.diagramPath), module);
				int memberIndex = Settings.scope.getUnitMemberIndex(member);
				map.put(memberIndex, moduleIndex);
				
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	private HashMap<Integer, Integer> deriveMemberModuleStopList(){
		HashMap<Integer, Integer> map = new HashMap<>();
		
		for(HeuristicModuleMemberStopMap m: Settings.heuristicModuleMemberStopMapList){
			ModuleWrapper module = m.getModule();
			UnitMemberWrapper member = m.getMember();
			
			try {
				int moduleIndex = ReflexactoringUtil.getModuleIndex(ReflexactoringUtil.getModuleList(Settings.diagramPath), module);
				int memberIndex = Settings.scope.getUnitMemberIndex(member);
				map.put(memberIndex, moduleIndex);
				
			} catch (PartInitException e) {
				e.printStackTrace();
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
	public HashMap<Integer, Integer> getUnitModuleStopList() {
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
	public HashMap<Integer, Integer> getMemberModuleStopList() {
		return memberModuleStopList;
	}
}
