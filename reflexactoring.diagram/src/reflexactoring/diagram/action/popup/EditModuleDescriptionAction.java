package reflexactoring.diagram.action.popup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.emf.type.core.commands.SetValueCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import reflexactoring.Module;
import reflexactoring.ReflexactoringPackage;
import reflexactoring.diagram.edit.parts.ModuleEditPart;


public class EditModuleDescriptionAction extends AbstractActionDelegate implements IObjectActionDelegate{

	public EditModuleDescriptionAction() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		super.setActivePart(action, targetPart);
		
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
			
			String userInputDesc = "test";
			
			SetRequest setNameReq = new SetRequest(module, ReflexactoringPackage.eINSTANCE.getModule_Description(), userInputDesc);
			SetValueCommand setNameCommand = new SetValueCommand(setNameReq);
			part.getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(setNameCommand));
			
		} 
		
	}

}
