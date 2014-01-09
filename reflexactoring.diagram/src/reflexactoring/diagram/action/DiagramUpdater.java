/**
 * 
 */
package reflexactoring.diagram.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.commands.CreateCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.DeferredCreateConnectionViewAndElementCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditDomain;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramEditDomain;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest.ConnectionViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest.ViewAndElementDescriptor;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.emf.type.core.commands.SetValueCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.internal.RealSystem;

import reflexactoring.Module;
import reflexactoring.ModuleDependency;
import reflexactoring.Reflexactoring;
import reflexactoring.ReflexactoringPackage;
import reflexactoring.Type;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleConnectionWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.edit.commands.Class2CreateCommand;
import reflexactoring.diagram.edit.commands.ClassCreateCommand;
import reflexactoring.diagram.edit.commands.Interface2CreateCommand;
import reflexactoring.diagram.edit.commands.InterfaceCreateCommand;
import reflexactoring.diagram.edit.commands.ModuleDependencyCreateCommand;
import reflexactoring.diagram.edit.commands.TypeDependencyCreateCommand;
import reflexactoring.diagram.edit.parts.ModuleDependencyEditPart;
import reflexactoring.diagram.edit.parts.ReflexactoringEditPart;
import reflexactoring.diagram.edit.parts.ModuleDependencyEditPart.ModuleDependencyFigure;
import reflexactoring.diagram.part.ReflexactoringDiagramEditor;
import reflexactoring.diagram.part.ReflexactoringDiagramEditorPlugin;
import reflexactoring.diagram.part.ReflexactoringDiagramEditorUtil;
import reflexactoring.diagram.providers.ReflexactoringElementTypes;
import reflexactoring.impl.ReflexactoringImpl;
import reflexactoring.provider.ReflexactoringEditPlugin;

/**
 * This class is used to update the diagram according to corresponding computation,
 * for example, show the results of mapping or reflexing.
 * 
 * @author linyun
 *
 */
public class DiagramUpdater {
	/**
	 * Create compilation units and their relations on diagram.
	 * 
	 * @param moduleList
	 * @param compilationUnitWrapperList
	 */
	public void generateReflexionModel(ArrayList<ModuleWrapper> moduleList,
			ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList) {
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ReflexactoringDiagramEditor editor = (ReflexactoringDiagramEditor)workbenchPage.getActiveEditor();
		DiagramGraphicalViewer diagram = (DiagramGraphicalViewer)editor.getDiagramGraphicalViewer();
		
		RootEditPart root = diagram.getRootEditPart();
		DiagramRootEditPart diagramRoot = (DiagramRootEditPart)root;
		
		try {
			generateLowLevelModel(diagramRoot, compilationUnitWrapperList);
			generateLowLevelConnection(diagramRoot, compilationUnitWrapperList);
			
			showModelConformance(diagramRoot, moduleList, compilationUnitWrapperList);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param diagramRoot
	 * @param compilationUnitWrapperList
	 * @throws JavaModelException 
	 */
	protected void generateLowLevelModel(DiagramRootEditPart diagramRoot,
			ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList) throws JavaModelException {
		for(ICompilationUnitWrapper unitWrapper: compilationUnitWrapperList){
			ModuleWrapper mappingModuleWrapper = unitWrapper.getMappingModule();
			ICompilationUnit unit = unitWrapper.getCompilationUnit();
			IType type = unit.getTypes()[0];
			
			if(mappingModuleWrapper != null){
				Module module = findModule(diagramRoot, mappingModuleWrapper.getModule());
				IElementType elementType = type.isClass() ? 
						ReflexactoringElementTypes.Class_3001 : ReflexactoringElementTypes.Interface_3002;
				
				CreateElementRequest req = new CreateElementRequest(module, elementType);
				
				if(type.isClass()){
					Class2CreateCommand createTypeCommand = new Class2CreateCommand(req);
					getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(createTypeCommand));					
				}
				else{
					Interface2CreateCommand createTypeCommand = new Interface2CreateCommand(req);
					getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(createTypeCommand));
				}
				
				setTypeValue(req.getNewElement(), unitWrapper, getRootEditPart(diagramRoot).getDiagramEditDomain());
				
			}
			/**
			 * such a compilation unit belongs to no module, so I draw it on original canvas.
			 */
			else{
				Reflexactoring reflexactoring = findReflexactoring(diagramRoot);
				IElementType elementType = type.isClass() ? 
						ReflexactoringElementTypes.Class_2001 : ReflexactoringElementTypes.Interface_2002;
				CreateElementRequest req = new CreateElementRequest(reflexactoring, elementType);
				if(type.isClass()){
					ClassCreateCommand createTypeCommand = new ClassCreateCommand(req);
					getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(createTypeCommand));					
				}
				else{
					InterfaceCreateCommand createTypeCommand = new InterfaceCreateCommand(req);
					getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(createTypeCommand));
				}
				
				setTypeValue(req.getNewElement(), unitWrapper, getRootEditPart(diagramRoot).getDiagramEditDomain());
			}
		}
	}
	
	/**
	 * @param diagramRoot
	 * @param compilationUnitWrapperList
	 */
	protected void generateLowLevelConnection(DiagramRootEditPart diagramRoot,
			ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList) {
		for(ICompilationUnitWrapper callerWrapper: compilationUnitWrapperList){
			for(ICompilationUnitWrapper calleeWrapper: callerWrapper.getCalleeCompilationUnitList().keySet()){
				if(callerWrapper != calleeWrapper){
					Type callerType = findType(diagramRoot, callerWrapper);
					Type calleeType = findType(diagramRoot, calleeWrapper);
					
					IElementType relationType = ReflexactoringElementTypes.TypeDependency_4003;
					CreateRelationshipRequest req = new CreateRelationshipRequest(callerType, calleeType, relationType);
					
					ConnectionViewAndElementDescriptor viewDescriptor = new ConnectionViewAndElementDescriptor(
							new CreateElementRequestAdapter(req),
							((IHintedType) relationType).getSemanticHint(),
							ReflexactoringDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
					
					CreateConnectionViewAndElementRequest request = new CreateConnectionViewAndElementRequest(
							viewDescriptor);
					
					View callerView = findViewOfSepcificType(diagramRoot, callerType);
					View calleeView = findViewOfSepcificType(diagramRoot, calleeType);
					
					ICommand createRelationCommand = new DeferredCreateConnectionViewAndElementCommand(
							request, new EObjectAdapter(callerView),
							new EObjectAdapter(calleeView),
							diagramRoot.getViewer());
					CompoundCommand c = new CompoundCommand();
					c.add(new ICommandProxy(createRelationCommand));
					getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(c);	
				}
			}
		}
		
	}

	/**
	 * @param diagramRoot
	 * @param moduleList
	 * @param compilationUnitWrapperList
	 */
	protected void showModelConformance(DiagramRootEditPart diagramRoot,
			ArrayList<ModuleWrapper> moduleList,
			ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList) {
		/**
		 * Prepare the comparing material -- two sets of connections, one for conceived connections while the 
		 * other one for realistic connections.
		 */
		Reflexactoring root = findReflexactoring(diagramRoot);
		HashSet<ModuleConnectionWrapper> conceivedConnectionList = new HashSet<>();
		for(ModuleDependency connection: root.getModuleDenpencies()){
			ModuleWrapper sourceModule = new ModuleWrapper(connection.getOrigin());
			ModuleWrapper targetModule = new ModuleWrapper(connection.getDestination());
			ModuleConnectionWrapper connectionWrapper = new ModuleConnectionWrapper(sourceModule, targetModule);
			
			conceivedConnectionList.add(connectionWrapper);
		}
		
		HashSet<ModuleConnectionWrapper> realisticConnectionList = recoverRealisticConnectionList(compilationUnitWrapperList);
		
		/**
		 * Start comparing the module conformance between high and low level module.
		 */

		/**
		 * First, identify matched connections from two sets.
		 */
		Iterator<ModuleConnectionWrapper> iterator = conceivedConnectionList.iterator();
		while(iterator.hasNext()){
			ModuleConnectionWrapper connection = iterator.next();
			if(realisticConnectionList.contains(connection)){
				iterator.remove();
				realisticConnectionList.remove(connection);
				
				ModuleDependencyFigure connectionFigure = findCorrespondingDepedencyFigure(diagramRoot, connection);
				connectionFigure.setConformanceStyle();
			}
		}
		
		/**
		 * Second, deal with missing dependency (absence connection), i.e., the dependencies expected by user 
		 * while not exist in code.
		 * 
		 */
		for(ModuleConnectionWrapper conceivedConnection: conceivedConnectionList){
			ModuleDependencyFigure connectionFigure = createDependency(diagramRoot, conceivedConnection);
			connectionFigure.setAbsenceStyle();
		}
		
		/**
		 * Third, deal with wrong dependencies (divergent connection), i.e., the dependencies unexpected by user
		 * while exist in code.
		 */
		for(ModuleConnectionWrapper divergentConnection: realisticConnectionList){
			ModuleDependencyFigure connectionFigure = createDependency(diagramRoot, divergentConnection);
			connectionFigure.setDivergneceStyle();
		}
	}
	
	private ModuleDependencyFigure createDependency(DiagramRootEditPart diagramRoot, ModuleConnectionWrapper connection){
		Module sourceModule = findModule(diagramRoot, connection.getSourceModule().getModule());
		Module targetModule = findModule(diagramRoot, connection.getTargetModule().getModule());
		
		IElementType connectionType = ReflexactoringElementTypes.ModuleDependency_4001;
		CreateRelationshipRequest req = new CreateRelationshipRequest(sourceModule, targetModule, connectionType);
		
		ConnectionViewAndElementDescriptor viewDescriptor = new ConnectionViewAndElementDescriptor(
				new CreateElementRequestAdapter(req),
				((IHintedType) connectionType).getSemanticHint(),
				ReflexactoringDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
		
		CreateConnectionViewAndElementRequest request = new CreateConnectionViewAndElementRequest(
				viewDescriptor);
		
		
		View callerView = findViewOfSpecificModule(diagramRoot, sourceModule);
		View calleeView = findViewOfSpecificModule(diagramRoot, targetModule);
		
		ICommand createRelationCommand = new DeferredCreateConnectionViewAndElementCommand(
				request, new EObjectAdapter(callerView),
				new EObjectAdapter(calleeView),
				diagramRoot.getViewer());
		CompoundCommand c = new CompoundCommand();
		c.add(new ICommandProxy(createRelationCommand));
		
		getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(c);	
		
		ModuleDependencyFigure connectionFigure = findCorrespondingDepedencyFigure(diagramRoot, connection);
		
		return connectionFigure;
	}
	
	private ModuleDependencyFigure findCorrespondingDepedencyFigure(DiagramRootEditPart diagramRoot,
			ModuleConnectionWrapper connection){
		
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				
				List editPartList = rootEditPart.getConnections();
				for(Object connectionObj: editPartList){
					ConnectionEditPart connectionPart = (ConnectionEditPart)connectionObj;
					if(connectionPart instanceof ModuleDependencyEditPart){
						ModuleDependencyEditPart dependencyPart = (ModuleDependencyEditPart)connectionPart;
						ModuleDependency dependency = (ModuleDependency)dependencyPart.resolveSemanticElement();
						ModuleWrapper sourceModule = new ModuleWrapper(dependency.getOrigin());
						ModuleWrapper targetModule = new ModuleWrapper(dependency.getDestination());
						ModuleConnectionWrapper connectionWrapper = new ModuleConnectionWrapper(sourceModule, targetModule);
						
						if(connectionWrapper.equals(connection)){
							return dependencyPart.getPrimaryShape();
						}
					}
				}
			}
		}
		
		return null;
	}
	
	private HashSet<ModuleConnectionWrapper> recoverRealisticConnectionList(
			ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList){
		HashSet<ModuleConnectionWrapper> realisticConnectionList = new HashSet<>();
		for(ICompilationUnitWrapper callerUnit: compilationUnitWrapperList){
			for(ICompilationUnitWrapper calleeUnit: callerUnit.getCalleeCompilationUnitList().keySet()){
				ModuleWrapper callerModule = callerUnit.getMappingModule();
				ModuleWrapper calleeModule = calleeUnit.getMappingModule();
				
				if(callerModule != null && calleeModule != null && !callerModule.equals(calleeModule)){
					ModuleConnectionWrapper connection = new ModuleConnectionWrapper(callerModule, calleeModule);
					realisticConnectionList.add(connection);
				}
			}
		}
		
		return realisticConnectionList;
	}
	
	private void setTypeValue(EObject targetObject, ICompilationUnitWrapper unitWrapper, IDiagramEditDomain diagramEditDomain){
		SetRequest setNameReq = new SetRequest(targetObject, ReflexactoringPackage.eINSTANCE.getType_Name(), unitWrapper.getSimpleName());
		SetValueCommand setNameCommand = new SetValueCommand(setNameReq);
		diagramEditDomain.getDiagramCommandStack().execute(new ICommandProxy(setNameCommand));
		
		SetRequest setPackageReq = new SetRequest(targetObject, ReflexactoringPackage.eINSTANCE.getType_PackageName(), unitWrapper.getPackageName());
		SetValueCommand setPackageCommand = new SetValueCommand(setPackageReq);
		diagramEditDomain.getDiagramCommandStack().execute(new ICommandProxy(setPackageCommand));
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
	
	private View findViewOfSpecificModule(DiagramRootEditPart diagramRoot, Module module){
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
	
	/**
	 * @param diagramRoot
	 * @param type
	 * @return
	 */
	private View findViewOfSepcificType(DiagramRootEditPart diagramRoot,
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
						if(module.getDescription().equals(currentModule.getDescription())
								&& module.getName().equals(currentModule.getName())){
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
	
	private Reflexactoring findReflexactoring(DiagramRootEditPart diagramRoot){
		for(Object obj: diagramRoot.getChildren()){
			if(obj instanceof ReflexactoringEditPart){
				ReflexactoringEditPart rootEditPart = (ReflexactoringEditPart)obj;
				return (Reflexactoring)rootEditPart.resolveSemanticElement();
			}
		}
		
		return null;
	}
}
