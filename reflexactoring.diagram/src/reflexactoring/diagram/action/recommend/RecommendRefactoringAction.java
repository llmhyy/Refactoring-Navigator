package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class RecommendRefactoringAction implements
		IWorkbenchWindowActionDelegate {
	
	@Override
	public void run(IAction action) {
		
		RefactoringRecommender recommender = new RefactoringRecommender();
		
		ArrayList<Suggestion> suggestions = recommender.recommend();
		
		
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