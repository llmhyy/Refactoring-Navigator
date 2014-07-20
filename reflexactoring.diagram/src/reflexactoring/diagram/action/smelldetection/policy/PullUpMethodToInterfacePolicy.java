/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.policy;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;

/**
 * @author linyun
 *
 */
public class PullUpMethodToInterfacePolicy extends RefactoringPolicy{

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.smelldetection.policy.RefactoringPolicy#detectOpportunities(java.util.ArrayList)
	 */
	@Override
	public ArrayList<? extends RefactoringOpportunity> detectOpportunities(
			ArrayList<ICompilationUnitWrapper> list) {
		ArrayList<? extends RefactoringOpportunity> opportunityList = new ArrayList<>();
		//TODO
		
		return opportunityList;
	}

	

}
