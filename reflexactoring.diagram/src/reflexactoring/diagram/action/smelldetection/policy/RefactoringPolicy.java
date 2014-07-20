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
public abstract class RefactoringPolicy {
	public abstract ArrayList<? extends RefactoringOpportunity> detectOpportunities(ArrayList<ICompilationUnitWrapper> list);
}
