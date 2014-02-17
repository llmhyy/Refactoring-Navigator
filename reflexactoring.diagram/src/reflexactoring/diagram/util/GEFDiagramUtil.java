/**
 * 
 */
package reflexactoring.diagram.util;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import reflexactoring.Module;
import reflexactoring.ModuleDependency;
import reflexactoring.Reflexactoring;
import reflexactoring.Type;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.edit.parts.ModuleDependencyEditPart;
import reflexactoring.diagram.edit.parts.ReflexactoringEditPart;
import reflexactoring.diagram.part.ReflexactoringDiagramEditor;

/**
 * @author linyun
 *
 */
public class GEFDiagramUtil {
	
	public static DiagramRootEditPart getRootEditPart(){
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ReflexactoringDiagramEditor editor = (ReflexactoringDiagramEditor)workbenchPage.getActiveEditor();
		DiagramGraphicalViewer diagram = (DiagramGraphicalViewer)editor.getDiagramGraphicalViewer();
		
		RootEditPart root = diagram.getRootEditPart();
		DiagramRootEditPart diagramRoot = (DiagramRootEditPart)root;
		
		return diagramRoot;
	}
	
	public static ReflexactoringEditPart getRootEditPart(DiagramRootEditPart diagramRoot){
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				return rootEditPart; 
			}
		}
		
		return null;
	}
	
	public static View findViewOfSpecificModule(DiagramRootEditPart diagramRoot, Module module){
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				List views = ((View)rootEditPart.getModel()).getChildren();
				for(Object objView: views){
					View view = (View)objView;
					EObject eObj = view.getElement();
					if(eObj instanceof Module){
						Module m = (Module)eObj;
						if(m.getName().equals(module.getName()) && m.getDescription().equals(module.getDescription())){
							return view;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public static View findViewOfSpecificModuleDependency(DiagramRootEditPart diagramRoot, ModuleDependency dependency){
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				List<?> connectionList = rootEditPart.getConnections();
				for(Object connObj: connectionList){
					ModuleDependencyEditPart dependencyPart = (ModuleDependencyEditPart)connObj;
					EObject eObj = dependencyPart.resolveSemanticElement();
					if(eObj instanceof ModuleDependency){
						ModuleDependency d = (ModuleDependency)eObj;
						if(d.getName().equals(dependency.getName()) &&
								d.getDestination().getDescription().equals(dependency.getDestination().getDescription()) &&
								d.getOrigin().getDescription().equals(dependency.getOrigin().getDescription())){
							return dependencyPart.getPrimaryView();
						}
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * @param diagramRoot
	 * @param type
	 * @return
	 */
	public static View findViewOfSepcificType(DiagramRootEditPart diagramRoot,
			Type type) {
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart editPart = (ReflexactoringEditPart)obj;
				
				List<?> editPartList = editPart.getChildren();
				
				for(int i=0; i<editPartList.size(); i++){
					EditPart part = (EditPart) editPartList.get(i);
					View view = (View)part.getModel();
					EObject eObj = view.getElement();
					if(eObj instanceof Type){
						Type t = (Type)eObj;
						if(t.getName().equals(type.getName())
								&& t.getPackageName().equals(type.getPackageName())){
							return view;
						}
					}
					else if(eObj instanceof Module){
						List<?> subEditPartList = part.getChildren();
						for(Object typePart: ((EditPart) subEditPartList.get(1)).getChildren()){
							EditPart subPart = (EditPart)typePart;
							View subView = (View)subPart.getModel();
							EObject eSubObj = subView.getElement();
							if(eSubObj instanceof Type){
								Type t = (Type)eSubObj;
								if(t.getName().equals(type.getName())
										&& t.getPackageName().equals(type.getPackageName())){
									return subView;
								}
							}
							
						}
					}
				}
			}
		}
		
		
		return null;
	}

	public static Module findModule(DiagramRootEditPart diagramRoot, Module module){
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
	
	
	
	public static Type findType(DiagramRootEditPart diagramRoot, ICompilationUnitWrapper unit){
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
	
	public static Reflexactoring findReflexactoring(DiagramRootEditPart diagramRoot){
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				return (Reflexactoring)rootEditPart.resolveSemanticElement();
			}
		}
		
		return null;
	}
}
