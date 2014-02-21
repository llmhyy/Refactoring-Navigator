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
import org.eclipse.ui.progress.UIJob;

import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;
import reflexactoring.diagram.view.RefactoringSuggestionView;

public class MemberRecommendAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				Settings.isNeedClearCache = true;
				final RefactoringSuggestionView view = (RefactoringSuggestionView)PlatformUI.getWorkbench().
						getActiveWorkbenchWindow().getActivePage().findView(ReflexactoringPerspective.REFACTORING_SUGGESTION);
				
				Job job = new Job("Searching for solutions by moving methods"){
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						
						Double iterNum = Double.valueOf(ReflexactoringUtil.getIterationNumber());
						monitor.beginTask("Searching Solution...", iterNum.intValue());
						
						RefactoringRecommender recommender = new RefactoringRecommender();
						final ArrayList<Suggestion> suggestions = recommender.recommendStartByMember(monitor);
						
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
		});
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}
