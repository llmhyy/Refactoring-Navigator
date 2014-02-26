/**
 * 
 */
package reflexactoring.diagram.util;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.core.commands.DeleteCommand;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.ui.commands.DeferredCreateConnectionViewAndElementCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest.ConnectionViewAndElementDescriptor;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import reflexactoring.Module;
import reflexactoring.ModuleDependency;
import reflexactoring.Reflexactoring;
import reflexactoring.Type;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.edit.parts.ModuleDependencyEditPart;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.edit.parts.ModuleTypeContainerCompartmentEditPart;
import reflexactoring.diagram.edit.parts.ReflexactoringEditPart;
import reflexactoring.diagram.part.ReflexactoringDiagramEditor;
import reflexactoring.diagram.part.ReflexactoringDiagramEditorPlugin;
import reflexactoring.diagram.providers.ReflexactoringElementTypes;

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
	
	public static void deleteElementOnCanvas(DiagramRootEditPart diagramRoot, EObject eObj){
		DestroyElementRequest destroyRequest = new DestroyElementRequest(eObj, false);
		DestroyElementCommand destroyCommand = new DestroyElementCommand(destroyRequest);
		GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(destroyCommand));
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
					if(connObj instanceof ModuleDependencyEditPart){
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
		}
		
		return null;
	}
	
	/**
	 * @param diagramRoot
	 * @param type
	 * @return
	 */
	public static View findViewOfSpecificType(DiagramRootEditPart diagramRoot,
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
	
	public static void addModuleDependency(ModuleWrapper sourceModuleWrapper, ModuleWrapper targetModuleWrapper){
		DiagramRootEditPart diagramRoot = GEFDiagramUtil.getRootEditPart();
		Module sourceModule = GEFDiagramUtil.findModule(diagramRoot, sourceModuleWrapper.getModule());
		Module targetModule = GEFDiagramUtil.findModule(diagramRoot, targetModuleWrapper.getModule());
		
		IElementType relationType = ReflexactoringElementTypes.ModuleDependency_4001;
		
		CreateRelationshipRequest req = new CreateRelationshipRequest(sourceModule, targetModule, relationType);
		
		ConnectionViewAndElementDescriptor viewDescriptor = new ConnectionViewAndElementDescriptor(
				new CreateElementRequestAdapter(req),
				((IHintedType) relationType).getSemanticHint(),
				ReflexactoringDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
		
		CreateConnectionViewAndElementRequest request = new CreateConnectionViewAndElementRequest(
				viewDescriptor);
		
		View callerView = GEFDiagramUtil.findViewOfSpecificModule(diagramRoot, sourceModule);
		View calleeView = GEFDiagramUtil.findViewOfSpecificModule(diagramRoot, targetModule);
		
		ICommand createRelationCommand = new DeferredCreateConnectionViewAndElementCommand(
				request, new EObjectAdapter(callerView),
				new EObjectAdapter(calleeView),
				diagramRoot.getViewer());
		CompoundCommand c = new CompoundCommand();
		c.add(new ICommandProxy(createRelationCommand));
		GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(c);	
	}
	
	public static void removeModuleDependency(ModuleWrapper sourceModuleWrapper, ModuleWrapper targetModuleWrapper){
		DiagramRootEditPart diagramRoot = GEFDiagramUtil.getRootEditPart();
		Reflexactoring reflexactoring = GEFDiagramUtil.findReflexactoring(diagramRoot);
		
		int count = reflexactoring.getModuleDenpencies().size();
		for(int i=0; i<count; i++){
			ModuleDependency dependency = reflexactoring.getModuleDenpencies().get(i);
			if(dependency.getOrigin().getName().equals(sourceModuleWrapper.getName())
					&& dependency.getDestination().getName().equals(targetModuleWrapper.getName())){
				
				Edge edge = (Edge)GEFDiagramUtil.findViewOfSpecificModuleDependency(diagramRoot, dependency);
				
				DestroyElementRequest destroyRequest = new DestroyElementRequest(dependency, false);
				DestroyElementCommand destroyCommand = new DestroyElementCommand(destroyRequest);
				DeleteCommand deleteCommand = new DeleteCommand(GEFDiagramUtil.getRootEditPart(diagramRoot).getEditingDomain(), edge);
				
				CompoundCommand comCommand = new CompoundCommand();
				comCommand.add(new ICommandProxy(destroyCommand));
				comCommand.add(new ICommandProxy(deleteCommand));
				
				GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(comCommand);
				
			}
		}
	}
	
	public static void focusModuleOnGraph(String moduleName){
		DiagramRootEditPart rootPart = GEFDiagramUtil.getRootEditPart();
		for(Object partObj: rootPart.getChildren()){
			if(partObj instanceof ReflexactoringEditPart){
				for(Object modulePart: ((ReflexactoringEditPart)partObj).getChildren()){
					if(modulePart instanceof ModuleEditPart){
						ModuleEditPart moduleEditPart = (ModuleEditPart)modulePart;
						Module module = (Module)moduleEditPart.resolveSemanticElement();
						if(module.getName().equals(moduleName)){
							IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							ReflexactoringDiagramEditor editor = (ReflexactoringDiagramEditor)workbenchPage.getActiveEditor();
							editor.setFocus();
							
							moduleEditPart.setSelected(EditPart.SELECTED_PRIMARY);
							moduleEditPart.setFocus(true);
							moduleEditPart.getViewer().setFocus(moduleEditPart);
							moduleEditPart.getViewer().select(moduleEditPart);
							//moduleEditPart.getViewer().setSelection(new StructuredSelection(moduleEditPart));
							moduleEditPart.getViewer().reveal(moduleEditPart);
						}
					}
				}
			}
		}
	}
	
	public static void focusTypeOnGraph(String identifier){
		DiagramRootEditPart rootPart = GEFDiagramUtil.getRootEditPart();
		for(Object partObj: rootPart.getChildren()){
			if(partObj instanceof ReflexactoringEditPart){
				for(Object modulePart: ((ReflexactoringEditPart)partObj).getChildren()){
					if(modulePart instanceof ModuleEditPart){
						ModuleEditPart moduleEditPart = (ModuleEditPart)modulePart;
						for(Object containerObj: moduleEditPart.getChildren()){
							if(containerObj instanceof ModuleTypeContainerCompartmentEditPart){
								ModuleTypeContainerCompartmentEditPart containerPart = (ModuleTypeContainerCompartmentEditPart)containerObj;
								for(Object typeObj: containerPart.getChildren()){
									ShapeEditPart typePart = (ShapeEditPart)typeObj;
									
									Type type = (Type) typePart.resolveSemanticElement();
									String fullTypeName = type.getPackageName() + "." + type.getName();
									if(fullTypeName.equals(identifier)){
										IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
										ReflexactoringDiagramEditor editor = (ReflexactoringDiagramEditor)workbenchPage.getActiveEditor();
										editor.setFocus();
										
										typePart.setSelected(EditPart.SELECTED_PRIMARY);
										//typePart.setFocus(true);
										//typePart.getViewer().setFocus(moduleEditPart);
										//typePart.getViewer().select(moduleEditPart);
										//moduleEditPart.getViewer().setSelection(new StructuredSelection(moduleEditPart));
										typePart.getViewer().reveal(typePart);
									}
									
								}
							}
							
						}
					}
				}
			}
		}
	}
}
