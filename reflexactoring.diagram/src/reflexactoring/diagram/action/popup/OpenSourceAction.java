package reflexactoring.diagram.action.popup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.PartInitException;

import reflexactoring.Type;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.edit.parts.Class2EditPart;
import reflexactoring.diagram.edit.parts.ClassEditPart;
import reflexactoring.diagram.edit.parts.Interface2EditPart;
import reflexactoring.diagram.edit.parts.InterfaceEditPart;
import reflexactoring.diagram.util.Settings;

public class OpenSourceAction extends AbstractActionDelegate implements IObjectActionDelegate{

	public OpenSourceAction() {
	}

	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		IStructuredSelection selection = getStructuredSelection();
		Object selEditPart = selection.getFirstElement();
		
		if(selEditPart instanceof ClassEditPart ||
				selEditPart instanceof Class2EditPart ||
				selEditPart instanceof InterfaceEditPart ||
				selEditPart instanceof Interface2EditPart){
			ShapeNodeEditPart editPart = (ShapeNodeEditPart)selEditPart;
			Type type = (Type)editPart.resolveSemanticElement();
			
			ICompilationUnitWrapper unitWrapper = Settings.scope.findUnit(type);
			ICompilationUnit unit = unitWrapper.getCompilationUnit();
			IEditorPart javaEditor;
			try {
				javaEditor = JavaUI.openInEditor(unit);
				JavaUI.revealInEditor(javaEditor,
						(IJavaElement) unit);
			} catch (PartInitException e) {
				e.printStackTrace();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		
	}

}
