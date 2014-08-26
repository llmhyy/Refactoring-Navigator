package reflexactoring.diagram.action.recommend;

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

import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.util.RecordParameters;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;
import reflexactoring.diagram.view.RefactoringSuggestionView;

public class ClassRecommendAction implements
		IWorkbenchWindowActionDelegate {
	
	@Override
	public void run(IAction action) {
		RecordParameters.typeSuggestion++;
		
		Settings.isNeedClearCache = true;
		final RefactoringSuggestionView view = (RefactoringSuggestionView)PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().findView(ReflexactoringPerspective.REFACTORING_SUGGESTION);
		
		
		Job job = new Job("Searching for solutions by moving types"){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				Double iterNum = Double.valueOf(ReflexactoringUtil.getMappingIterationNumber());
				monitor.beginTask("Searching Solution...", iterNum.intValue());
				
				//RefactoringRecommenderForOldRepresentation recommender = new RefactoringRecommenderForOldRepresentation();
				RefactoringRecommender recommender = new RefactoringRecommender();
				final ArrayList<Suggestion> suggestions = recommender.recommendStartByClass(monitor);
				
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						view.refreshSuggestionsOnUI(suggestions);
						
					}
				});
				
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();
		
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
