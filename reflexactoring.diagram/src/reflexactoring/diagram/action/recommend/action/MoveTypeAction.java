/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer;
import org.eclipse.gmf.runtime.emf.type.core.commands.MoveElementsCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.MoveRequest;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import reflexactoring.Module;
import reflexactoring.Type;
import reflexactoring.diagram.bean.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.SuggestionObject;
import reflexactoring.diagram.edit.parts.ReflexactoringEditPart;
import reflexactoring.diagram.part.ReflexactoringDiagramEditor;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.util.Settings;
import reflexactoring.diagram.view.HeuristicMappingView;

/**
 * @author linyun
 *
 */
public class MoveTypeAction extends MoveAction {

	@Override
	public void execute(SuggestionObject suggestionObj) {
		
		if(suggestionObj instanceof ICompilationUnitWrapper){
			ICompilationUnitWrapper unitWrapper = (ICompilationUnitWrapper)suggestionObj;
			
			move(unitWrapper, this.getDestination());
			refreshHeuristicMapUI(unitWrapper, this.getDestination());	
		}
	}

	@Override
	public void undoExecute(SuggestionObject suggestionObj) {
		if(suggestionObj instanceof ICompilationUnitWrapper){
			ICompilationUnitWrapper unitWrapper = (ICompilationUnitWrapper)suggestionObj;
			
			move(unitWrapper, this.getOrigin());
			refreshHeuristicMapUI(unitWrapper, this.getOrigin());			
		}

	}
	
	private void move(ICompilationUnitWrapper unitWrapper, ModuleWrapper targetModuleWrapper){
		
		DiagramRootEditPart diagramRoot = getRootEditPart();
		Module targetContainer = findModule(diagramRoot, targetModuleWrapper.getModule());
		Type elementToMove = findType(diagramRoot, unitWrapper);

		MoveRequest moveRequest = new MoveRequest(targetContainer, elementToMove);
		MoveElementsCommand moveCommand = new MoveElementsCommand(moveRequest);
		CompoundCommand comCommand = new CompoundCommand();
		comCommand.add(new ICommandProxy(moveCommand));
		getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(comCommand);
	}
	
	private void refreshHeuristicMapUI(ICompilationUnitWrapper unitWrapper, ModuleWrapper targetModuleWrapper){
		HeuristicModuleUnitMap map = new HeuristicModuleUnitMap(targetModuleWrapper, unitWrapper);
		Settings.heuristicModuleUnitMapList.addMap(map);
		
		HeuristicMappingView view = (HeuristicMappingView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().findView(ReflexactoringPerspective.HEURISTIC_MAPPING_VIEW);
		view.getViewer().setInput(Settings.heuristicModuleUnitMapList);
		view.getViewer().refresh();
	}
	
	private DiagramRootEditPart getRootEditPart(){
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ReflexactoringDiagramEditor editor = (ReflexactoringDiagramEditor)workbenchPage.getActiveEditor();
		DiagramGraphicalViewer diagram = (DiagramGraphicalViewer)editor.getDiagramGraphicalViewer();
		
		RootEditPart root = diagram.getRootEditPart();
		DiagramRootEditPart diagramRoot = (DiagramRootEditPart)root;
		
		return diagramRoot;
	}
	
	private Module findModule(DiagramRootEditPart diagramRoot, Module module){
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				List views = ((View)rootEditPart.getModel()).getChildren();
				for(Object objView: views){
					View view = (View)objView;
					EObject eObj = view.getElement();
					if(eObj instanceof Module){
						Module currentModule = (Module)eObj;
						if(module.getName().equals(currentModule.getName())){
							return currentModule;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	
	
	private Type findType(DiagramRootEditPart diagramRoot, ICompilationUnitWrapper unit){
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				List views = ((View)rootEditPart.getModel()).getChildren();
				for(Object objView: views){
					View view = (View)objView;
					EObject eObj = view.getElement();
					if(eObj instanceof Type){
						Type type = (Type)eObj;
						
						String fullName = type.getPackageName() + "." + type.getName();
						if(unit.getFullQualifiedName().equals(fullName)){
							return type;
						}
					}
					else if(eObj instanceof Module){
						Module module = (Module)eObj;
						for(Type type: module.getMappingTypes()){
							String fullName = type.getPackageName() + "." + type.getName();
							if(unit.getFullQualifiedName().equals(fullName)){
								return type;
							}
						}
					}
				}
			}
			
		}
		
		return null;
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
