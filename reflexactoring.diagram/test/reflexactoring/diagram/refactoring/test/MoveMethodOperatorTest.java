/**
 * 
 */
package reflexactoring.diagram.refactoring.test;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.MoveMethodOpportunity;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.MethodWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;

/**
 * @author linyun
 *
 */
public class MoveMethodOperatorTest {
	public ProgramModel test(ProgramModel model){
		
		MethodWrapper objectMethod = (MethodWrapper)model.getScopeMemberList().get(26);
		ICompilationUnitWrapper targetUnit = model.getOutmostTypesInScope().get(0);
		
		MoveMethodOpportunity opp = new MoveMethodOpportunity(objectMethod, targetUnit);
	
		ProgramModel newModel = opp.simulate(model);
		
		return newModel;
	}
}
