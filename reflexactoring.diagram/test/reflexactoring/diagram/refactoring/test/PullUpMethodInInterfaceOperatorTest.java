/**
 * 
 */
package reflexactoring.diagram.refactoring.test;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMethodToInterfaceOpportunity;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.UnitMemberWrapper;

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
		
		PullUpMethodToInterfaceOpportunity opp = new PullUpMethodToInterfaceOpportunity();
		opp.setToBePulledMethodList(toBePulledList);
		ProgramModel newModel = opp.simulate(model);
		
		return newModel;
	}
}
