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
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart;
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

import reflexactoring.ClassExtend;
import reflexactoring.Implement;
import reflexactoring.InterfaceExtend;
import reflexactoring.Module;
import reflexactoring.ModuleCreation;
import reflexactoring.ModuleDependency;
import reflexactoring.ModuleExtend;
import reflexactoring.ModuleLink;
import reflexactoring.Reflexactoring;
import reflexactoring.Type;
import reflexactoring.TypeDependency;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleLinkWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.edit.parts.ClassExtendEditPart;
import reflexactoring.diagram.edit.parts.ImplementEditPart;
import reflexactoring.diagram.edit.parts.InterfaceExtendEditPart;
import reflexactoring.diagram.edit.parts.ModuleCreationEditPart;
import reflexactoring.diagram.edit.parts.ModuleDependencyEditPart;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.edit.parts.ModuleExtendEditPart;
import reflexactoring.diagram.edit.parts.ModuleLinkEditPart;
import reflexactoring.diagram.edit.parts.ModuleLinkFigure;
import reflexactoring.diagram.edit.parts.ModuleTypeContainerCompartmentEditPart;
import reflexactoring.diagram.edit.parts.ReflexactoringEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart;
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
	
	/*public static EditPart findTypeEditPart(Type type){
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ReflexactoringDiagramEditor editor = (ReflexactoringDiagramEditor)workbenchPage.getActiveEditor();
		DiagramGraphicalViewer diagram = (DiagramGraphicalViewer)editor.getDiagramGraphicalViewer();
		
		EditPart part = (EditPart)diagram.getEditPartRegistry().get(type);
		return part;
	}*/
	
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
	
	public static void deleteEdgeOnCanvas(DiagramRootEditPart diagramRoot, Edge edge, EObject eObj){
		DestroyElementRequest destroyRequest = new DestroyElementRequest(eObj, false);
		DestroyElementCommand destroyCommand = new DestroyElementCommand(destroyRequest);
		DeleteCommand deleteCommand = new DeleteCommand(GEFDiagramUtil.getRootEditPart(diagramRoot).getEditingDomain(), edge);
		
		CompoundCommand comCommand = new CompoundCommand();
		comCommand.add(new ICommandProxy(destroyCommand));
		comCommand.add(new ICommandProxy(deleteCommand));
		
		GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(comCommand);
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
	
	public static View findViewOfSpecificModuleDependency(DiagramRootEditPart diagramRoot, ModuleLink link){
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				List<?> connectionList = rootEditPart.getConnections();
				for(Object connObj: connectionList){
					if(connObj instanceof ModuleLinkEditPart){
						ModuleLinkEditPart linkPart = (ModuleLinkEditPart)connObj;
						EObject eObj = linkPart.resolveSemanticElement();
						if(eObj instanceof ModuleLink){
							ModuleLink d = (ModuleLink)eObj;
							if(d.getName().equals(link.getName()) &&
									d.getDestination().getDescription().equals(link.getDestination().getDescription()) &&
									d.getOrigin().getDescription().equals(link.getOrigin().getDescription())){
								return linkPart.getPrimaryView();
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
	
	public static void addModuleLink(ModuleWrapper sourceModuleWrapper, ModuleWrapper targetModuleWrapper, int linkType){
		DiagramRootEditPart diagramRoot = GEFDiagramUtil.getRootEditPart();
		Module sourceModule = GEFDiagramUtil.findModule(diagramRoot, sourceModuleWrapper.getModule());
		Module targetModule = GEFDiagramUtil.findModule(diagramRoot, targetModuleWrapper.getModule());
		
		IElementType relationType = null;
		if(linkType == ModuleLinkWrapper.MODULE_DEPENDENCY){
			relationType = ReflexactoringElementTypes.ModuleDependency_4001;
		}
		else if(linkType == ModuleLinkWrapper.MODULE_EXTEND){
			relationType = ReflexactoringElementTypes.ModuleExtend_4006;
		}
		else if(linkType == ModuleLinkWrapper.MODULE_CREATION){
			relationType = ReflexactoringElementTypes.ModuleCreation_4007;
		}
		
		ModuleLinkWrapper connection = new ModuleLinkWrapper(sourceModuleWrapper, targetModuleWrapper, linkType);
		ModuleLinkEditPart editPart = findCorrespondingDepedencyEditPart(diagramRoot, connection);
		
		if(editPart != null){
			ModuleLinkFigure existingDivergentEdge =  editPart.getPrimaryShape();
			existingDivergentEdge.setOriginStyle();
		}
		else{
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
		
			
	}

	public static void removeModuleLink(ModuleWrapper sourceModuleWrapper, ModuleWrapper targetModuleWrapper, int linkType){
		DiagramRootEditPart diagramRoot = GEFDiagramUtil.getRootEditPart();
		Reflexactoring reflexactoring = GEFDiagramUtil.findReflexactoring(diagramRoot);
		
		int count = reflexactoring.getModuleDenpencies().size();
		for(int i=0; i<count; i++){
			ModuleLink link = reflexactoring.getModuleDenpencies().get(i);
			if(link.getOrigin().getName().equals(sourceModuleWrapper.getName())
					&& link.getDestination().getName().equals(targetModuleWrapper.getName())){
				
				if((linkType == ModuleLinkWrapper.MODULE_DEPENDENCY && (link instanceof ModuleDependency))
						|| (linkType == ModuleLinkWrapper.MODULE_EXTEND && (link instanceof ModuleExtend))
						|| (linkType == ModuleLinkWrapper.MODULE_CREATION && (link instanceof ModuleCreation))){
					Edge edge = (Edge)GEFDiagramUtil.findViewOfSpecificModuleDependency(diagramRoot, link);
					
					DestroyElementRequest destroyRequest = new DestroyElementRequest(link, false);
					DestroyElementCommand destroyCommand = new DestroyElementCommand(destroyRequest);
					DeleteCommand deleteCommand = new DeleteCommand(GEFDiagramUtil.getRootEditPart(diagramRoot).getEditingDomain(), edge);
					
					CompoundCommand comCommand = new CompoundCommand();
					comCommand.add(new ICommandProxy(destroyCommand));
					comCommand.add(new ICommandProxy(deleteCommand));
					
					GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(comCommand);
					break;
				}

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

	public static View findViewOfSpecificDependency(
			DiagramRootEditPart diagramRoot, Implement implement) {
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				List<?> connectionList = rootEditPart.getConnections();
				for(Object connObj: connectionList){
					if(connObj instanceof ImplementEditPart){
						ImplementEditPart implementConnection = (ImplementEditPart)connObj;
						EObject eObj = implementConnection.resolveSemanticElement();
						if(eObj instanceof Implement){
							Implement d = (Implement)eObj;
							if(d.getName().equals(implement.getName()) &&
									d.getClass_().getName().equals(implement.getClass_().getName()) &&
									d.getInterface().getName().equals(implement.getInterface().getName())){
								return implementConnection.getPrimaryView();
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
	 * @param classExtend
	 * @return
	 */
	public static View findViewOfSpecificDependency(
			DiagramRootEditPart diagramRoot, ClassExtend classExtend) {
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				List<?> connectionList = rootEditPart.getConnections();
				for(Object connObj: connectionList){
					if(connObj instanceof ClassExtendEditPart){
						ClassExtendEditPart classExtendConnection = (ClassExtendEditPart)connObj;
						EObject eObj = classExtendConnection.resolveSemanticElement();
						if(eObj instanceof ClassExtend){
							ClassExtend d = (ClassExtend)eObj;
							if(d.getName().equals(classExtend.getName()) &&
									d.getSubClass().getName().equals(classExtend.getSubClass().getName()) &&
									d.getSuperClass().getName().equals(classExtend.getSuperClass().getName())){
								return classExtendConnection.getPrimaryView();
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
	 * @param interfaceExtend
	 * @return
	 */
	public static View findViewOfSpecificDependency(
			DiagramRootEditPart diagramRoot, InterfaceExtend interfaceExtend) {
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				List<?> connectionList = rootEditPart.getConnections();
				for(Object connObj: connectionList){
					if(connObj instanceof InterfaceExtendEditPart){
						InterfaceExtendEditPart interfaceExtendConnection = (InterfaceExtendEditPart)connObj;
						EObject eObj = interfaceExtendConnection.resolveSemanticElement();
						if(eObj instanceof InterfaceExtend){
							InterfaceExtend d = (InterfaceExtend)eObj;
							if(d.getName().equals(interfaceExtend.getName()) &&
									d.getSubInterface().getName().equals(interfaceExtend.getSubInterface().getName()) &&
									d.getSuperInterface().getName().equals(interfaceExtend.getSuperInterface().getName())){
								return interfaceExtendConnection.getPrimaryView();
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
	 * @param typeDependency
	 * @return
	 */
	public static View findViewOfSpecificDependency(
			DiagramRootEditPart diagramRoot, TypeDependency typeDependency) {
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				List<?> connectionList = rootEditPart.getConnections();
				for(Object connObj: connectionList){
					if(connObj instanceof TypeDependencyEditPart){
						TypeDependencyEditPart typeDependencyConnection = (TypeDependencyEditPart)connObj;
						EObject eObj = typeDependencyConnection.resolveSemanticElement();
						if(eObj instanceof TypeDependency){
							TypeDependency d = (TypeDependency)eObj;
							if(d.getName().equals(typeDependency.getName()) &&
									d.getDestination().getName().equals(typeDependency.getDestination().getName()) &&
									d.getOrigin().getName().equals(typeDependency.getOrigin().getName())){
								return typeDependencyConnection.getPrimaryView();
							}
						}
						
					}
					
				}
			}
		}
		
		return null;
	}
	
	public static ModuleEditPart findCorrespondingModuleEditPart(DiagramRootEditPart diagramRoot, ModuleWrapper moduleWrapper){
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				
				List editPartList = rootEditPart.getChildren();
				for(Object objEditPart: editPartList){
					if(objEditPart instanceof ModuleEditPart){
						ModuleEditPart editPart = (ModuleEditPart)objEditPart;
						ModuleWrapper modW = new ModuleWrapper((Module)editPart.resolveSemanticElement());
						if(modW.equals(moduleWrapper)){
							return editPart;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public static ModuleLinkEditPart findCorrespondingDepedencyEditPart(DiagramRootEditPart diagramRoot,
			ModuleLinkWrapper connection){
		
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				
				List editPartList = rootEditPart.getConnections();
				for(Object connectionObj: editPartList){
					ConnectionEditPart connectionPart = (ConnectionEditPart)connectionObj;
					if(connectionPart instanceof ModuleLinkEditPart){
						//ModuleDependencyEditPart dependencyPart = (ModuleDependencyEditPart)connectionPart;
						ModuleEditPart sourceEditPart = (ModuleEditPart)connectionPart.getSource();
						ModuleEditPart targetEditPart = (ModuleEditPart)connectionPart.getTarget();
						
						ModuleWrapper sourceModule = new ModuleWrapper((Module)sourceEditPart.resolveSemanticElement());
						ModuleWrapper targetModule = new ModuleWrapper((Module)targetEditPart.resolveSemanticElement());
						
						if(connectionPart instanceof ModuleDependencyEditPart){
							ModuleLinkWrapper connectionWrapper = new ModuleLinkWrapper(sourceModule, targetModule, ModuleLinkWrapper.MODULE_DEPENDENCY);
							if(connectionWrapper.equals(connection)){
								return (ModuleLinkEditPart)connectionPart;
							}
						}
						else if(connectionPart instanceof ModuleExtendEditPart){
							ModuleLinkWrapper connectionWrapper = new ModuleLinkWrapper(sourceModule, targetModule, ModuleLinkWrapper.MODULE_EXTEND);
							if(connectionWrapper.equals(connection)){
								return (ModuleLinkEditPart)connectionPart;
							}
						}
						else if(connectionPart instanceof ModuleCreationEditPart){
							ModuleLinkWrapper connectionWrapper = new ModuleLinkWrapper(sourceModule, targetModule, ModuleLinkWrapper.MODULE_CREATION);
							if(connectionWrapper.equals(connection)){
								return (ModuleLinkEditPart)connectionPart;
							}
						}
						
					}
				}
			}
		}
		
		return null;
	}
}
