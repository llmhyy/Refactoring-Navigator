/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import reflexactoring.diagram.bean.HeuristicModuleMemberStopMap;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 * 
 */
public class MemberModuleValidityExaminer {
	
	
	public boolean isValid(UnitMemberWrapper memberWrapper,
			ModuleWrapper moduleWrapper) {
		return contructorCannotMove(memberWrapper, moduleWrapper) &&
				cannotContainedInForbiddenList(memberWrapper, moduleWrapper);
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
