/**
 * 
 */
package reflexactoring.diagram.refactoring.test;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMethodToInterfaceOpportunity;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class PullUpMethodInInterfaceOperatorTest {
	public ProgramModel test(ProgramModel model){
		ArrayList<UnitMemberWrapper> toBePulledList = new ArrayList<>();
		toBePulledList.add(model.getScopeMemberList().get(0));
		toBePulledList.add(model.getScopeMemberList().get(1));
		toBePulledList.add(model.getScopeMemberList().get(3));
		
		ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
		
		PullUpMethodToInterfaceOpportunity opp = new PullUpMethodToInterfaceOpportunity();
		
		opp.setModuleList(moduleList);
		opp.setToBePulledMethodList(toBePulledList);
		ProgramModel newModel = opp.simulate(model);
		
		return newModel;
	}
}
