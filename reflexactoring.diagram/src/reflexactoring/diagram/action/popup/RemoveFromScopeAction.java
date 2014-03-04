package reflexactoring.diagram.action.popup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import reflexactoring.Type;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.edit.parts.Class2EditPart;
import reflexactoring.diagram.edit.parts.ClassEditPart;
import reflexactoring.diagram.edit.parts.Interface2EditPart;
import reflexactoring.diagram.edit.parts.InterfaceEditPart;
import reflexactoring.diagram.edit.parts.ReflexactoringEditPart;
import reflexactoring.diagram.part.ReflexactoringDiagramEditor;
import reflexactoring.diagram.util.Settings;

public class RemoveFromScopeAction extends AbstractActionDelegate implements IObjectActionDelegate{

	public RemoveFromScopeAction() {
		
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		super.setActivePart(action, targetPart);
		
	}

	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		IStructuredSelection selection = getStructuredSelection();
		Object selEditPart = selection.getFirstElement();
		
		if(selEditPart instanceof ClassEditPart ||
				selEditPart instanceof Class2EditPart ||
				selEditPart instanceof InterfaceEditPart ||
				selEditPart instanceof Interface2EditPart){
			
			/**
			 * remove it on graph.
			 */
			IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			ReflexactoringDiagramEditor editor = (ReflexactoringDiagramEditor)workbenchPage.getActiveEditor();
			DiagramGraphicalViewer diagram = (DiagramGraphicalViewer)editor.getDiagramGraphicalViewer();
			
			RootEditPart root = diagram.getRootEditPart();
			DiagramRootEditPart diagramRoot = (DiagramRootEditPart)root;
			
			ShapeNodeEditPart editPart = (ShapeNodeEditPart)selEditPart;
			
			Type type = (Type)editPart.resolveSemanticElement();
			
			DestroyElementRequest destroyRequest = new DestroyElementRequest(type, false);
			DestroyElementCommand destroyCommand = new DestroyElementCommand(destroyRequest);
			getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(destroyCommand));
			
			/**
			 * remove it on memory.
			 */
			ICompilationUnitWrapper unit = Settings.scope.findUnit(type);
			Settings.scope.removeUnit(unit);
			
		}
	}
	
	private ReflexactoringEditPart getRootEditPart(DiagramRootEditPart diagramRoot){
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				return rootEditPart; 
			}
		}
		
		return null;
	}

}
