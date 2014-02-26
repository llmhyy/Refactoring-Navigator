package reflexactoring.diagram.action.popup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.PlatformUI;

import reflexactoring.Type;
import reflexactoring.TypeDependency;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.util.Settings;
import reflexactoring.diagram.view.ReferenceDetailView;
import reflexactoring.diagram.view.ViewUpdater;

public class ShowReferenceDetailAction extends AbstractActionDelegate implements IObjectActionDelegate{

	public ShowReferenceDetailAction() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		IStructuredSelection selection = getStructuredSelection();
		Object selEditPart = selection.getFirstElement();
		
		if(selEditPart instanceof TypeDependencyEditPart){
			TypeDependencyEditPart dependencyPart = (TypeDependencyEditPart)selEditPart;
			TypeDependency dependency = (TypeDependency) dependencyPart.resolveSemanticElement();
			
			Type sourceType = dependency.getOrigin();
			Type targetType = dependency.getDestination();
			
			ICompilationUnitWrapper sourceUnit = Settings.scope.findUnit(sourceType);
			ICompilationUnitWrapper targetUnit = Settings.scope.findUnit(targetType);
			
			if(sourceUnit != null && targetUnit != null){
				ReferenceDetailMap map = new ReferenceDetailMap(sourceUnit, targetUnit, sourceUnit.getReferingDetails().get(targetUnit));
				ViewUpdater updater = new ViewUpdater();
				updater.updateView(ReflexactoringPerspective.REFERENCE_DETAIL_VIEW, map, true);
				
				/*ReferenceDetailMap map = new ReferenceDetailMap(sourceUnit, targetUnit, sourceUnit.getReferingDetails().get(targetUnit));
				ReferenceDetailView view = (ReferenceDetailView)PlatformUI.getWorkbench().
						getActiveWorkbenchWindow().getActivePage().findView(ReflexactoringPerspective.REFERENCE_DETAIL_VIEW);
				view.getViewer().setLabelProvider(view.new DetailLabelProvider(map));
				view.getViewer().setInput(map);
				view.getViewer().refresh();*/
			}
			
		}
		
	}

}
