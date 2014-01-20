package reflexactoring.diagram.action.popup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.emf.type.core.commands.SetValueCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import reflexactoring.Module;
import reflexactoring.ReflexactoringPackage;
import reflexactoring.diagram.action.MappingDialog;
import reflexactoring.diagram.bean.HeuristicModuleUnitMap;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.util.Settings;


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
			
			String userInputDesc = "";
			EditModuleDescriptionDialog dialog = new EditModuleDescriptionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), module.getDescription());
			dialog.create();
			if(dialog.open() == Window.OK){
				String des = dialog.getEditModuleDescription();
				if(des != null){
					userInputDesc = des;
				}
				String userInputName = "test";
				
				/**
				 * set module name.
				 */
				SetRequest setNameReq = new SetRequest(module, ReflexactoringPackage.eINSTANCE.getModule_Name(), userInputName);
				SetValueCommand setNameCommand = new SetValueCommand(setNameReq);
				part.getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(setNameCommand));
				
				
				/**
				 * set module description
				 */
				SetRequest setDescReq = new SetRequest(module, ReflexactoringPackage.eINSTANCE.getModule_Description(), userInputDesc);
				SetValueCommand setDescCommand = new SetValueCommand(setDescReq);
				part.getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(setDescCommand));
				
			}
			
		} 
		
	}

}
