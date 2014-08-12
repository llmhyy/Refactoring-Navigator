/**
 * 
 */
package reflexactoring.diagram.view;

import java.util.ArrayList;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.popup.ReferenceDetailMap;
import reflexactoring.diagram.action.recommend.Suggestion;
import reflexactoring.diagram.action.recommend.SuggestionMove;
import reflexactoring.diagram.bean.ModuleCreationConfidenceTable;
import reflexactoring.diagram.bean.ModuleDependencyConfidenceTable;
import reflexactoring.diagram.bean.ModuleExtendConfidenceTable;
import reflexactoring.diagram.bean.ModuleUnitsSimilarityTable;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;

/**
 * @author linyun
 *
 */
public class ViewUpdater {
	public void updateView(String viewId, Object inputData, boolean isNeedReveal){
		if(viewId.equals(ReflexactoringPerspective.DEPENDENCY_CONSTRAINT_CONFIDENCE_VIEW)){
			DependencyConstraintConfidenceView view = (DependencyConstraintConfidenceView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.refreshUI((ModuleDependencyConfidenceTable) inputData);
		}
		else if(viewId.equals(ReflexactoringPerspective.EXTEND_CONSTRAINT_CONFIDENCE_VIEW)){
			ExtendConstraintConfidenceView view = (ExtendConstraintConfidenceView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.refreshUI((ModuleExtendConfidenceTable) inputData);
		}
		else if(viewId.equals(ReflexactoringPerspective.CREATION_CONSTRAINT_CONFIDENCE_VIEW)){
			CreationConstraintConfidenceView view = (CreationConstraintConfidenceView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.refreshUI((ModuleCreationConfidenceTable) inputData);
		}
		else if(viewId.equals(ReflexactoringPerspective.MODULE_MEMBER_FORBIDDEN_VIEW)){
			ModuleMemberForbiddenView view = (ModuleMemberForbiddenView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.getViewer().setInput(inputData);
			view.getViewer().refresh();
		}
		else if(viewId.equals(ReflexactoringPerspective.MODULE_UNIT_FORBIDDEN_VIEW)){
			ModuleTypeForbiddenView view = (ModuleTypeForbiddenView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
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
		else if(viewId.equals(ReflexactoringPerspective.MEMBER_MAPPING_FIX_BY_CLASS_VIEW)){
			AllMemberInClassMappingFixView view = (AllMemberInClassMappingFixView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.getViewer().setInput(inputData);
			view.getViewer().refresh();
		}
		else if(viewId.equals(ReflexactoringPerspective.MEMBER_MAPING_FIX_VIEW)){
			MemberFixingView view = (MemberFixingView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
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
			view.getViewer().setLabelProvider(view.new DetailLabelProvider((ReferenceDetailMap) inputData));
			view.getViewer().setInput(inputData);
			view.getViewer().refresh();
		}
		else if(viewId.equals(ReflexactoringPerspective.REFACTORING_SUGGESTION)){
			RefactoringSuggestionView view = (RefactoringSuggestionView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.refreshSuggestionsOnUI((ArrayList<Suggestion>) inputData);
		}
		else if(viewId.equals(ReflexactoringPerspective.FORBIDDEN_REFACTORING_OPP_VIEW)){
			RefactoringOppForbiddenView view = (RefactoringOppForbiddenView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.getViewer().setInput(inputData);
			view.getViewer().refresh();
		}
		else if(viewId.equals(ReflexactoringPerspective.APPROVED_REFACTORING_OPP_VIEW)){
			RefactoringOppApprovedView view = (RefactoringOppApprovedView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(viewId);
			view.getViewer().setInput(inputData);
			view.getViewer().refresh();
		}
		if(isNeedReveal){
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
			} catch (PartInitException e) {
				e.printStackTrace();
			}			
		}
	}
}
