package reflexactoring.diagram.action;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.recommend.RefactoringRecommender;
import reflexactoring.diagram.action.recommend.SuggestionMove;
import reflexactoring.diagram.action.recommend.suboptimal.Violation;
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
		
		Job job = new Job("Searching for solutions by moving types"){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				//monitor.beginTask("Searching Solution...", iterNum.intValue());
				final ArrayList<RefactoringSequence> suggestionList = new ArrayList<>();
				ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
				ProgramModel model = Settings.scope;
				BadSmellDetector smellDetector = new BadSmellDetector(moduleList);
				
				RefactoringSequence sequence = new RefactoringSequence();
				ArrayList<RefactoringOpportunity> oppList = smellDetector.detect(model);
				
				int resursiveNum = 20;
				
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
				
				/**
				 * prepare prerequisite
				 */
				ArrayList<Violation> violations = sequence.get(sequence.size()-1).getViolationList();
				RefactoringRecommender recommender = new RefactoringRecommender();
				ArrayList<SuggestionMove> prerequisite = recommender.findHighLevelModificationSuggestion(violations, moduleList);
				
				sequence.setPrerequisite(prerequisite);
				
				suggestionList.add(sequence);
				
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						RefactoringSuggestionsView view = (RefactoringSuggestionsView)PlatformUI.getWorkbench().
								getActiveWorkbenchWindow().getActivePage().findView(ReflexactoringPerspective.REFACTORING_SUGGESTIONS);
						view.refreshSuggestionsOnUI(suggestionList);
						
					}
				});
				
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();
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
		ArrayList<Violation> violationList = null;
		
		for(RefactoringOpportunity opp: oppList){
			ProgramModel testModel = opp.simulate(model);
			double value = evaluator.computeFitness(testModel, moduleList);
			
			if(fitnessValue == null){
				fitnessValue = value;
				bestOpp = opp;
				consequenceModel = testModel;
				violationList = evaluator.getViolationList();
			}
			else{
				if(value > fitnessValue){
					fitnessValue = value;
					bestOpp = opp;
					consequenceModel = testModel;
					violationList = evaluator.getViolationList();
				}
			}
		}
		
		return new RefactoringSequenceElement(bestOpp, consequenceModel, fitnessValue, violationList);
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
