/**
 * 
 */
package reflexactoring.diagram.refactoring.test;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.CreateSuperclassAndPullUpMethodOpportunity;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class PullUpMethodNewSuperclassOperatorTest {
	public ProgramModel test(ProgramModel model){
		ArrayList<UnitMemberWrapper> toBePulledList = new ArrayList<>();
		toBePulledList.add(model.getScopeMemberList().get(0));
		toBePulledList.add(model.getScopeMemberList().get(2));
		toBePulledList.add(model.getScopeMemberList().get(3));
		
		ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
		
		CreateSuperclassAndPullUpMethodOpportunity opp = new CreateSuperclassAndPullUpMethodOpportunity();
		
		opp.setModuleList(moduleList);
		opp.setToBePulledMethodList(toBePulledList);
		ProgramModel newModel = opp.simulate(model);
		
		return newModel;
	}
}
