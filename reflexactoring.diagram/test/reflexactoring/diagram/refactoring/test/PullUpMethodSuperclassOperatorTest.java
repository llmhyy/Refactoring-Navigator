/**
 * 
 */
package reflexactoring.diagram.refactoring.test;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMemberToSuperclassOpportunity;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class PullUpMethodSuperclassOperatorTest {
	public ProgramModel test(ProgramModel model){
		ArrayList<UnitMemberWrapper> toBePulledList = new ArrayList<>();
		toBePulledList.add(model.getScopeMemberList().get(0));
		toBePulledList.add(model.getScopeMemberList().get(3));
		toBePulledList.add(model.getScopeMemberList().get(5));
		
		ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
		
		PullUpMemberToSuperclassOpportunity opp = null;//new PullUpMemberToSuperclassOpportunity(toBePulledList, moduleList);
		
		ProgramModel newModel = opp.simulate(model);
		
		return newModel;
	}
}
