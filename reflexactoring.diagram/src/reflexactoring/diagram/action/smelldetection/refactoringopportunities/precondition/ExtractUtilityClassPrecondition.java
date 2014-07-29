/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.bean.CloneSet;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.ExtractUtilityClassOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;

/**
 * @author linyun
 *
 */
public class ExtractUtilityClassPrecondition extends RefactoringPrecondition{

	/**
	 * 
	 */
	public ExtractUtilityClassPrecondition(ArrayList<ModuleWrapper> moduleList) {
		setModuleList(moduleList);
	}
	
	@Override
	public ArrayList<RefactoringOpportunity> detectOpportunities(ProgramModel model) {
		ArrayList<RefactoringOpportunity> opportunities = new ArrayList<>();
		
		for(CloneSet set: model.getCloneSets()){
			ExtractUtilityClassOpportunity opp = new ExtractUtilityClassOpportunity(set, getModuleList());
			opportunities.add(opp);
		}
		
		return opportunities;
	}

}
