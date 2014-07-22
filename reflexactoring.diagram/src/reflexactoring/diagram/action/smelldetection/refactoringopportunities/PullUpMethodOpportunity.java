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
	protected ArrayList<UnitMemberWrapper> toBePulledMethodList = new ArrayList<>();
	protected ICompilationUnitWrapper targetUnit;
	public ArrayList<UnitMemberWrapper> getToBePulledMethodList() {
		return toBePulledMethodList;
	}
	public void setToBePulledMethodList(
			ArrayList<UnitMemberWrapper> toBePulledMethodList) {
		this.toBePulledMethodList = toBePulledMethodList;
	}
	
	
}
