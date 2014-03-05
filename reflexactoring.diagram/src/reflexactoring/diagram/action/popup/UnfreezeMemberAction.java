package reflexactoring.diagram.action.popup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;

import reflexactoring.Module;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.edit.parts.ModuleEditPart.ModuleFigure;
import reflexactoring.diagram.util.Settings;

public class UnfreezeMemberAction extends AbstractActionDelegate implements IObjectActionDelegate{

	public UnfreezeMemberAction() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		IStructuredSelection selection = getStructuredSelection();
		Object selEditPart = selection.getFirstElement();
		
		if (selEditPart instanceof ModuleEditPart){
			ModuleEditPart part = (ModuleEditPart)selEditPart;
			Module module = (Module) part.resolveSemanticElement();
			
			ModuleWrapper moduleWrapper = new ModuleWrapper(module);
			Settings.frozenModules.remove(moduleWrapper);	
			
			ModuleFigure figure = (ModuleFigure)part.getFigure().getChildren().get(0);
			figure.setNormalBackgroundColor();
		}
		
	}

}
