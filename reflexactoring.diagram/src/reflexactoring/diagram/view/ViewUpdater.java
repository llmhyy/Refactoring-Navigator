/**
 * 
 */
package reflexactoring.diagram.view;

import java.util.ArrayList;

import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.recommend.Suggestion;
import reflexactoring.diagram.bean.ModuleDependencyConfidenceTable;
import reflexactoring.diagram.bean.ModuleUnitsSimilarityTable;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;

/**
 * @author linyun
 *
 */
public class ViewUpdater {
	public void updateView(String viewId, Object inputData){
		if(viewId.equals(ReflexactoringPerspective.CONSTRAINT_CONFIDENCE_VIEW)){
			ConstraintConfidenceView view = (ConstraintConfidenceView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.refreshUI((ModuleDependencyConfidenceTable) inputData);
		}
		else if(viewId.equals(ReflexactoringPerspective.FORBIDDEN_VIEW)){
			ForbiddenView view = (ForbiddenView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.getViewer().setInput(inputData);
			view.getViewer().refresh();
		}
		else if(viewId.equals(ReflexactoringPerspective.HEURISTIC_MAPPING_VIEW)){
			HeuristicMappingView view = (HeuristicMappingView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.getViewer().setInput(inputData);
			view.getViewer().refresh();
		}
		else if(viewId.equals(ReflexactoringPerspective.MAPPING_FIX_VIEW)){
			MappingFixView view = (MappingFixView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.getViewer().setInput(inputData);
			view.getViewer().refresh();
		}
		else if(viewId.equals(ReflexactoringPerspective.MODULE_TYPE_SIMILARITY_VIEW)){
			ModuleUnitsSimilarityView view = (ModuleUnitsSimilarityView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.refreshUI((ModuleUnitsSimilarityTable) inputData);
		}
		else if(viewId.equals(ReflexactoringPerspective.REFERENCE_DETAIL_VIEW)){
			ReferenceDetailView view = (ReferenceDetailView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.getViewer().setInput(inputData);
			view.getViewer().refresh();
		}
		else if(viewId.equals(ReflexactoringPerspective.REFACTORING_SUGGESTION)){
			RefactoringSuggestionView view = (RefactoringSuggestionView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.refreshSuggestionsOnUI((ArrayList<Suggestion>) inputData);
		}
	}
}
