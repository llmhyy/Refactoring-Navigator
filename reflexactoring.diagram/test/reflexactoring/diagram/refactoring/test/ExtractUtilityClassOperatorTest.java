/**
 * 
 */
package reflexactoring.diagram.refactoring.test;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.bean.CloneSet;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.ExtractUtilityClassOpportunity;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class ExtractUtilityClassOperatorTest {
	public ProgramModel test(ProgramModel model){
		CloneSet set = model.getCloneSets().get(0);
		
		ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
		
		ExtractUtilityClassOpportunity opp = new ExtractUtilityClassOpportunity(set, moduleList);
	
		ProgramModel newModel = opp.simulate(model);
		
		return newModel;
	}
}
