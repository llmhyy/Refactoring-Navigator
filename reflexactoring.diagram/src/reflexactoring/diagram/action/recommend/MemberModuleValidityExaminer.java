/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import reflexactoring.diagram.bean.HeuristicModuleMemberStopMap;
import reflexactoring.diagram.bean.HeuristicModulePartFixMemberMap;
import reflexactoring.diagram.bean.HeuristicModuleUnitFixMemberMap;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.util.Settings;

/**
 * 
 * This class is aimed to check whether a member and a module could be mapped or not.
 * @author linyun
 * 
 */
public class MemberModuleValidityExaminer {
	
	
	public boolean isValid(UnitMemberWrapper memberWrapper,
			ModuleWrapper moduleWrapper) {
		return contructorCannotMove(memberWrapper, moduleWrapper) &&
				cannotContainedInForbiddenList(memberWrapper, moduleWrapper) &&
				parentUnitWrapperFixedToRightModule(memberWrapper, moduleWrapper) &&
				isMemberMapFixedToRightModule(memberWrapper, moduleWrapper);
	}
	
	private boolean isMemberMapFixedToRightModule(UnitMemberWrapper memberWrapper,
			ModuleWrapper moduleWrapper){
		for(HeuristicModulePartFixMemberMap map: Settings.fixedPartMemberModuleList){
			if(map.getMember().equals(memberWrapper)){
				return map.getModule().equals(moduleWrapper);
			}
		}
		
		return true;
	}

	/**
	 * @param memberWrapper
	 * @param moduleWrapper
	 * @return
	 */
	private boolean parentUnitWrapperFixedToRightModule(
			UnitMemberWrapper memberWrapper, ModuleWrapper moduleWrapper) {
		HeuristicModuleUnitFixMemberMap map = Settings.fixedMemberModuleUnitList.findMap(memberWrapper.getUnitWrapper());
		
		if(map == null){
			return true;
		}
		else{
			return moduleWrapper.equals(map.getModule());
		}
	}

	private boolean contructorCannotMove(UnitMemberWrapper memberWrapper,
			ModuleWrapper moduleWrapper) {
		if (memberWrapper instanceof MethodWrapper) {
			MethodWrapper methodWrapper = (MethodWrapper) memberWrapper;
			/**
			 * we cannot move constructor out of a class.
			 */
			if (methodWrapper.isConstructor()) {
				String moduleName = moduleWrapper.getName();
				String correspondModuleName = methodWrapper.getUnitWrapper()
						.getMappingModule().getName();
				if (!moduleName.equals(correspondModuleName)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean cannotContainedInForbiddenList(
			UnitMemberWrapper memberWrapper, ModuleWrapper moduleWrapper) {
		HeuristicModuleMemberStopMap stopMap = Settings.heuristicStopMapList
				.findStopMap(moduleWrapper, memberWrapper);
		return stopMap == null;
	}
}
