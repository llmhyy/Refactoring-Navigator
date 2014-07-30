/**
 * 
 */
package reflexactoring.diagram.refactoring.test;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.MoveMethodOpportunity;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ProgramModel;

/**
 * @author linyun
 *
 */
public class MoveMethodOperatorTest {
	public ProgramModel test(ProgramModel model){
		
		MethodWrapper objectMethod = (MethodWrapper)model.getScopeMemberList().get(26);
		ICompilationUnitWrapper targetUnit = model.getScopeCompilationUnitList().get(0);
		
		MoveMethodOpportunity opp = new MoveMethodOpportunity(objectMethod, targetUnit);
	
		ProgramModel newModel = opp.simulate(model);
		
		return newModel;
	}
}
