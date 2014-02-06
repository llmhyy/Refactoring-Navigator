package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.view.RefactoringSuggestionView;

public class ClassRecommendAction implements
		IWorkbenchWindowActionDelegate {
	
	@Override
	public void run(IAction action) {
		
		RefactoringRecommender recommender = new RefactoringRecommender();
		
		ArrayList<Suggestion> suggestions = recommender.recommendStartByClass();
		
		
		RefactoringSuggestionView view = (RefactoringSuggestionView)PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().findView(ReflexactoringPerspective.REFACTORING_SUGGESTION);
		view.refreshSuggestionsOnUI(suggestions);
		
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
