/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public abstract class PullUpMethodOpportunity extends RefactoringOpportunity{
	protected ArrayList<UnitMemberWrapper> toBePulledMethodList;
	protected ICompilationUnitWrapper targetUnit;
	
}
