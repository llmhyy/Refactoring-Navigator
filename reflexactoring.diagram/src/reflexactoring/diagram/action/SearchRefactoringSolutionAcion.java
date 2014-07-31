package reflexactoring.diagram.action;

import java.util.ArrayList;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.smelldetection.AdvanceEvaluatorAdapter;
import reflexactoring.diagram.action.smelldetection.BadSmellDetector;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequenceElement;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;
import reflexactoring.diagram.view.RefactoringSuggestionsView;

public class SearchRefactoringSolutionAcion implements
		IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		// TODO Lin Yun
		ArrayList<RefactoringSequence> suggestionList = new ArrayList<>();
		ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
		ProgramModel model = Settings.scope;
		BadSmellDetector smellDetector = new BadSmellDetector(moduleList);
		
		RefactoringSequence sequence = new RefactoringSequence();
		ArrayList<RefactoringOpportunity> oppList = smellDetector.detect(model);
		
		int resursiveNum = 10;
		
		for(int i=0; i<resursiveNum; i++){
			RefactoringSequenceElement element = findBestOpportunity(oppList, model, moduleList);
			if(sequence.isAnImprovement(element)){
				sequence.addElement(element);
				model = element.getConsequenceModel();
				oppList = smellDetector.detect(model);
			}
			else{
				break;
			}
			
		}
		
		suggestionList.add(sequence);
		
		RefactoringSuggestionsView view = (RefactoringSuggestionsView)PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().findView(ReflexactoringPerspective.REFACTORING_SUGGESTIONS);
		view.refreshSuggestionsOnUI(suggestionList);
		
		System.currentTimeMillis();
	}

	/**
	 * @param oppList
	 * @param model
	 * @return
	 */
	private RefactoringSequenceElement findBestOpportunity(
			ArrayList<RefactoringOpportunity> oppList, ProgramModel model, ArrayList<ModuleWrapper> moduleList) {
		AdvanceEvaluatorAdapter evaluator = new AdvanceEvaluatorAdapter();
		Double fitnessValue = null;
		RefactoringOpportunity bestOpp = null;
		ProgramModel consequenceModel = null;
		
		for(RefactoringOpportunity opp: oppList){
			ProgramModel testModel = opp.simulate(model);
			double value = evaluator.computeFitness(testModel, moduleList);
			
			if(fitnessValue == null){
				fitnessValue = value;
				bestOpp = opp;
				consequenceModel = testModel;
			}
			else{
				if(value > fitnessValue){
					fitnessValue = value;
					bestOpp = opp;
					consequenceModel = testModel;
				}
			}
		}
		
		return new RefactoringSequenceElement(bestOpp, consequenceModel, fitnessValue);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void init(IWorkbenchWindow window) {

	}

}
