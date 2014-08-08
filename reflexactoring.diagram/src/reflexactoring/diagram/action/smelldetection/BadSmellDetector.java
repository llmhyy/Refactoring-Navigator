/**
 * 
 */
package reflexactoring.diagram.action.smelldetection;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.ExtractUtilityClassOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.MoveMethodOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.PullUpMemberPrecondition;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.RefactoringPrecondition;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class BadSmellDetector {
	
	private ArrayList<RefactoringPrecondition> preconditionList = new ArrayList<>();
	
	public BadSmellDetector(ArrayList<ModuleWrapper> moduleList){
		//preconditionList.add(new PullUpMemberToInterfaceOpportunity(null,null).new Precondition(moduleList));
		preconditionList.add(new PullUpMemberPrecondition(moduleList));	
		preconditionList.add(new ExtractUtilityClassOpportunity(null,null).new Precondition(moduleList));
		preconditionList.add(new MoveMethodOpportunity(null, null).new Precodition());
	}
	
	public ArrayList<RefactoringOpportunity> detect(ProgramModel model){
		ArrayList<RefactoringOpportunity> opporuntities = new ArrayList<>();
		
		model.detectClone(model);
		
		for(RefactoringPrecondition precondition: preconditionList){
			ArrayList<RefactoringOpportunity> oppList = precondition.detectOpportunities(model);
			opporuntities.addAll(oppList);
		}
		
		return opporuntities;
	}
}
