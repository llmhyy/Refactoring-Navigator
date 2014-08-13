/**
 * 
 */
package reflexactoring.diagram.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.ui.commands.DeferredCreateConnectionViewAndElementCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramEditDomain;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest.ConnectionViewAndElementDescriptor;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.emf.type.core.commands.SetValueCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import reflexactoring.ClassExtend;
import reflexactoring.Implement;
import reflexactoring.InterfaceExtend;
import reflexactoring.Module;
import reflexactoring.ModuleCreation;
import reflexactoring.ModuleDependency;
import reflexactoring.ModuleExtend;
import reflexactoring.ModuleLink;
import reflexactoring.Reflexactoring;
import reflexactoring.ReflexactoringPackage;
import reflexactoring.Type;
import reflexactoring.TypeDependency;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleLinkWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ReferencingDetail;
import reflexactoring.diagram.edit.commands.Class2CreateCommand;
import reflexactoring.diagram.edit.commands.ClassCreateCommand;
import reflexactoring.diagram.edit.commands.Interface2CreateCommand;
import reflexactoring.diagram.edit.commands.InterfaceCreateCommand;
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
import reflexactoring.diagram.util.GEFDiagramUtil;
import reflexactoring.diagram.util.Settings;

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
		
		ReflexactoringDiagramEditor editor = chooseEditorPart(workbenchPage);
		if(editor != null){
			try {
				workbenchPage.openEditor(editor.getEditorInput(), ReflexactoringDiagramEditor.ID, true);
			} catch (PartInitException e1) {
				e1.printStackTrace();
			}
			
			DiagramGraphicalViewer diagram = (DiagramGraphicalViewer)editor.getDiagramGraphicalViewer();
			
			RootEditPart root = diagram.getRootEditPart();
			DiagramRootEditPart diagramRoot = (DiagramRootEditPart)root;
			//clearCanvas(diagramRoot);
			try {
				clearCanvas(diagramRoot);
				
				generateLowLevelModel(diagramRoot, compilationUnitWrapperList);
				generateLowLevelConnection(diagramRoot, compilationUnitWrapperList);
				
				showModelConformance(diagramRoot, moduleList, compilationUnitWrapperList);
				
				//layoutModuleContent(diagramRoot, moduleList);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void layoutModuleContent(DiagramRootEditPart diagramRoot, ArrayList<ModuleWrapper> moduleList){
		for(ModuleWrapper module: moduleList){
			ModuleEditPart moduleEditPart = GEFDiagramUtil.findCorrespondingModuleEditPart(diagramRoot, module);
			List list = moduleEditPart.getChildren();
			for(Object editPart: list){
				if(editPart instanceof ModuleTypeContainerCompartmentEditPart){
					ModuleTypeContainerCompartmentEditPart containerPart = (ModuleTypeContainerCompartmentEditPart)editPart;
					IFigure containerFigure = (IFigure) ((IFigure) ((IFigure) containerPart.getFigure().getChildren().get(1)).getChildren().get(0)).getChildren().get(0);
					containerFigure.getLayoutManager().layout(containerFigure);
				}
			}
			
			System.currentTimeMillis();
		}
	}
	
	private ReflexactoringDiagramEditor chooseEditorPart(IWorkbenchPage workbenchPage){
		for(IEditorReference reference: workbenchPage.getEditorReferences()){
			IEditorPart part = reference.getEditor(true);
			if(part instanceof ReflexactoringDiagramEditor){
				return (ReflexactoringDiagramEditor)part;
			}
		}
		
		return null;
	}
	
	

	protected void clearCanvas(DiagramRootEditPart diagramRoot){
		Reflexactoring reflexactoring = GEFDiagramUtil.findReflexactoring(diagramRoot);
		
		/**
		 * delete all the types first. Here, there will be a concurrent modification exception if you
		 * iterates all the elements in diagram. Therefore, I use array instead of iterator. Thus, when
		 * I delete an element, its successor will be in its place immediately.
		 */
		int count = reflexactoring.getTypes().size();
		for(int i=0; i<count; i++){
			Type type = reflexactoring.getTypes().get(0);
			GEFDiagramUtil.deleteElementOnCanvas(diagramRoot, type);
		}
		
		for(Module module: reflexactoring.getModules()){
			int innerCount = module.getMappingTypes().size();
			for(int j=0; j<innerCount; j++){
				Type type = module.getMappingTypes().get(0);
				GEFDiagramUtil.deleteElementOnCanvas(diagramRoot, type);
			}
		}
		
		/**
		 * delete all the implements
		 */
		count = reflexactoring.getImplements().size();
		for(int i=0; i<count; i++){
			Implement implement = reflexactoring.getImplements().get(0);
			Edge edge = (Edge)GEFDiagramUtil.findViewOfSpecificDependency(diagramRoot, implement);
			GEFDiagramUtil.deleteEdgeOnCanvas(diagramRoot, edge, implement);
		}
		
		/**
		 * delete all the class extends
		 */
		count = reflexactoring.getClassExtends().size();
		for(int i=0; i<count; i++){
			ClassExtend classExtend = reflexactoring.getClassExtends().get(0);
			Edge edge = (Edge)GEFDiagramUtil.findViewOfSpecificDependency(diagramRoot, classExtend);
			GEFDiagramUtil.deleteEdgeOnCanvas(diagramRoot, edge, classExtend);
		}
		
		/**
		 * delete all the interface extends
		 */
		count = reflexactoring.getInterfaceExtends().size();
		for(int i=0; i<count; i++){
			InterfaceExtend interfaceExtend = reflexactoring.getInterfaceExtends().get(0);
			Edge edge = (Edge)GEFDiagramUtil.findViewOfSpecificDependency(diagramRoot, interfaceExtend);
			GEFDiagramUtil.deleteEdgeOnCanvas(diagramRoot, edge, interfaceExtend);
		}
		
		/**
		 * delete all the type dependencies
		 */
		count = reflexactoring.getTypeDependencies().size();
		for(int i=0; i<count; i++){
			TypeDependency typeDep = reflexactoring.getTypeDependencies().get(0);
			Edge edge = (Edge)GEFDiagramUtil.findViewOfSpecificDependency(diagramRoot, typeDep);
			GEFDiagramUtil.deleteEdgeOnCanvas(diagramRoot, edge, typeDep);
		}
		
		/**
		 * delete divergent edges and restore conformance and absence edges
		 */
		count = reflexactoring.getModuleDenpencies().size();
		int index = 0;
		for(int i=0; i<count; i++){
			ModuleLink link = reflexactoring.getModuleDenpencies().get(index);
			if(link.getName().equals(ModuleLinkWrapper.DIVERGENCE)){
				Edge edge = (Edge)GEFDiagramUtil.findViewOfSpecificModuleDependency(diagramRoot, link);
				GEFDiagramUtil.deleteEdgeOnCanvas(diagramRoot, edge, link);
			}
			else{
				setDependencyType(diagramRoot, link, ModuleLinkWrapper.ORIGIN);
				index++;
			}
		}
		System.currentTimeMillis();
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
			
			
			if(mappingModuleWrapper != null){
				Module module = GEFDiagramUtil.findModule(diagramRoot, mappingModuleWrapper.getModule());
				IElementType elementType = unitWrapper.isInterface() ? 
						ReflexactoringElementTypes.Interface_3002: ReflexactoringElementTypes.Class_3001;
				
				CreateElementRequest req = new CreateElementRequest(module, elementType);
				
				if(unitWrapper.isInterface()){
					Interface2CreateCommand createTypeCommand = new Interface2CreateCommand(req);
					GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(createTypeCommand));
				}
				else{
					Class2CreateCommand createTypeCommand = new Class2CreateCommand(req);
					GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(createTypeCommand));	
				}
			
				setTypeValue(req.getNewElement(), unitWrapper, GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain());
				setToolTip((Type)req.getNewElement(), diagramRoot);
			}
			/**
			 * such a compilation unit belongs to no module, so I draw it on original canvas.
			 */
			else{
				Reflexactoring reflexactoring = GEFDiagramUtil.findReflexactoring(diagramRoot);
				IElementType elementType = unitWrapper.isInterface() ? 
						ReflexactoringElementTypes.Interface_2002 : ReflexactoringElementTypes.Class_2001;
				CreateElementRequest req = new CreateElementRequest(reflexactoring, elementType);
				if(unitWrapper.isInterface()){
					InterfaceCreateCommand createTypeCommand = new InterfaceCreateCommand(req);
					GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(createTypeCommand));
				}
				else{
					ClassCreateCommand createTypeCommand = new ClassCreateCommand(req);
					GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(createTypeCommand));
				}
				
				setTypeValue(req.getNewElement(), unitWrapper, GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain());
				setToolTip((Type)req.getNewElement(), diagramRoot);
			}
		}
	}
	
	private void setToolTip(Type type, DiagramRootEditPart diagramRoot){
		ShapeEditPart part = (ShapeEditPart) GEFDiagramUtil.getRootEditPart(diagramRoot).findEditPart(diagramRoot, type);
		String tooltipMsg = type.getPackageName() + "." + type.getName();
		Label tooltip = new Label(tooltipMsg);
		part.getFigure().setToolTip(tooltip);
	}
	
	/**
	 * @param diagramRoot
	 * @param compilationUnitWrapperList
	 */
	protected void generateLowLevelConnection(DiagramRootEditPart diagramRoot,
			ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList) {
		/**
		 * generate class extending relation
		 */
		for(ICompilationUnitWrapper unit: compilationUnitWrapperList){
			ICompilationUnitWrapper superClass = unit.getSuperClass();
			if(null != superClass){
				generateRelationsBetweenUnits(diagramRoot, unit, superClass, ReflexactoringElementTypes.ClassExtend_4002);
			}
		}
		
		/**
		 * generate implementing relation and interface extending relation
		 */
		for(ICompilationUnitWrapper unit: compilationUnitWrapperList){
			for(ICompilationUnitWrapper interfaceUnit: unit.getSuperInterfaceList()){
				if(unit.isInterface()){
					generateRelationsBetweenUnits(diagramRoot, unit, interfaceUnit, ReflexactoringElementTypes.InterfaceExtend_4004);
				}
				else{
					generateRelationsBetweenUnits(diagramRoot, unit, interfaceUnit, ReflexactoringElementTypes.Implement_4005);
				}
			}
		}
		
		/**
		 * generate call relations between caller unit and callee unit.
		 */
		for(ICompilationUnitWrapper callerWrapper: compilationUnitWrapperList){
			for(ICompilationUnitWrapper calleeWrapper: callerWrapper.getCalleeCompilationUnitList().keySet()){
				if(callerWrapper != calleeWrapper){
					generateRelationsBetweenUnits(diagramRoot, callerWrapper, calleeWrapper, ReflexactoringElementTypes.TypeDependency_4003);	
				}
			}
		}
		
		/**
		 * try making every dependency invisible at first
		 */
		//makeAllLowLevelConnectionInvisible(diagramRoot);
		
	}

	/**
	 * @param diagramRoot
	 * @param callerWrapper
	 * @param calleeWrapper
	 */
	private void generateRelationsBetweenUnits(DiagramRootEditPart diagramRoot,
			ICompilationUnitWrapper callerWrapper,
			ICompilationUnitWrapper calleeWrapper, IElementType relationType) {
		Type callerType = GEFDiagramUtil.findType(diagramRoot, callerWrapper);
		Type calleeType = GEFDiagramUtil.findType(diagramRoot, calleeWrapper);
		
		CreateRelationshipRequest req = new CreateRelationshipRequest(callerType, calleeType, relationType);
		
		ConnectionViewAndElementDescriptor viewDescriptor = new ConnectionViewAndElementDescriptor(
				new CreateElementRequestAdapter(req),
				((IHintedType) relationType).getSemanticHint(),
				ReflexactoringDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
		
		CreateConnectionViewAndElementRequest request = new CreateConnectionViewAndElementRequest(
				viewDescriptor);
		
		View callerView = GEFDiagramUtil.findViewOfSpecificType(diagramRoot, callerType);
		View calleeView = GEFDiagramUtil.findViewOfSpecificType(diagramRoot, calleeType);
		
		ICommand createRelationCommand = new DeferredCreateConnectionViewAndElementCommand(
				request, new EObjectAdapter(callerView),
				new EObjectAdapter(calleeView),
				diagramRoot.getViewer());
		CompoundCommand c = new CompoundCommand();
		c.add(new ICommandProxy(createRelationCommand));
		GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(c);
		
		
		
		System.currentTimeMillis();
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
		Reflexactoring root = GEFDiagramUtil.findReflexactoring(diagramRoot);
		HashSet<ModuleLinkWrapper> conceivedConnectionList = new HashSet<>();
		for(ModuleLink connection: root.getModuleDenpencies()){
			ModuleWrapper sourceModule = new ModuleWrapper(connection.getOrigin());
			ModuleWrapper targetModule = new ModuleWrapper(connection.getDestination());
			
			ModuleLinkWrapper connectionWrapper = null;
			if(connection instanceof ModuleExtend){
				connectionWrapper = new ModuleLinkWrapper(sourceModule, targetModule, ModuleLinkWrapper.MODULE_EXTEND);
			}
			else if(connection instanceof ModuleDependency){
				connectionWrapper = new ModuleLinkWrapper(sourceModule, targetModule, ModuleLinkWrapper.MODULE_DEPENDENCY);
			}
			else if(connection instanceof ModuleCreation){
				connectionWrapper = new ModuleLinkWrapper(sourceModule, targetModule, ModuleLinkWrapper.MODULE_CREATION);
			}
			
			if(null == connectionWrapper){
				System.err.println("unkown type of connection");
			}
			
			conceivedConnectionList.add(connectionWrapper);
		}
		
		HashSet<ModuleLinkWrapper> realisticConnectionList = recoverRealisticConnectionList(compilationUnitWrapperList);
		
		/**
		 * Start comparing the module conformance between high and low level module.
		 */

		/**
		 * First, identify matched connections from two sets.
		 */
		Iterator<ModuleLinkWrapper> iterator = conceivedConnectionList.iterator();
		while(iterator.hasNext()){
			ModuleLinkWrapper connection = iterator.next();
			if(realisticConnectionList.contains(connection)){
				iterator.remove();
				realisticConnectionList.remove(connection);
				
				ModuleLinkFigure connectionFigure = GEFDiagramUtil.findCorrespondingDepedencyEditPart(diagramRoot, 
						connection).getPrimaryShape();
				connectionFigure.setConformanceStyle();
				
				setDependencyType(diagramRoot, connection, ModuleLinkWrapper.CONFORMANCE);
			}
		}
		
		/**
		 * Second, deal with missing dependency (absence connection), i.e., the dependencies expected by user 
		 * while not exist in code.
		 * 
		 */
		for(ModuleLinkWrapper conceivedConnection: conceivedConnectionList){
			ModuleLinkEditPart linkPart = GEFDiagramUtil.findCorrespondingDepedencyEditPart(diagramRoot, conceivedConnection);
			ModuleLinkFigure connectionFigure = linkPart.getPrimaryShape();
			
			connectionFigure.setAbsenceStyle();
			setDependencyType(diagramRoot, conceivedConnection, ModuleLinkWrapper.ABSENCE);
		}
		
		/**
		 * Third, deal with wrong dependencies (divergent connection), i.e., the dependencies unexpected by user
		 * while exist in code.
		 */
		for(ModuleLinkWrapper divergentConnection: realisticConnectionList){
			ModuleLinkFigure connectionFigure = createDependency(diagramRoot, divergentConnection);
			connectionFigure.setDivergneceStyle();
			setDependencyType(diagramRoot, divergentConnection, ModuleLinkWrapper.DIVERGENCE);
		}
	}
	
	private ModuleLinkFigure createDependency(DiagramRootEditPart diagramRoot, ModuleLinkWrapper connection){
		Module sourceModule = GEFDiagramUtil.findModule(diagramRoot, connection.getSourceModule().getModule());
		Module targetModule = GEFDiagramUtil.findModule(diagramRoot, connection.getTargetModule().getModule());
		
		IElementType connectionType = null; 
		if(connection.getLinkType() == ModuleLinkWrapper.MODULE_DEPENDENCY){
			connectionType = ReflexactoringElementTypes.ModuleDependency_4001;
		}
		else if(connection.getLinkType() == ModuleLinkWrapper.MODULE_EXTEND){
			connectionType = ReflexactoringElementTypes.ModuleExtend_4006;
		}
		else if(connection.getLinkType() == ModuleLinkWrapper.MODULE_CREATION){
			connectionType = ReflexactoringElementTypes.ModuleCreation_4007;
		}
		
		CreateRelationshipRequest req = new CreateRelationshipRequest(sourceModule, targetModule, connectionType);
		
		ConnectionViewAndElementDescriptor viewDescriptor = new ConnectionViewAndElementDescriptor(
				new CreateElementRequestAdapter(req),
				((IHintedType) connectionType).getSemanticHint(),
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
		
		/*ModuleLinkEditPart part = findCorrespondingDepedencyEditPart(diagramRoot, connection);
		EObject obj = part.resolveSemanticElement();*/
		
		ModuleLinkFigure connectionFigure = GEFDiagramUtil.findCorrespondingDepedencyEditPart(diagramRoot, connection).getPrimaryShape();
		
		return connectionFigure;
	}
	
	
	
	private HashSet<ModuleLinkWrapper> recoverRealisticConnectionList(
			ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList){
		HashSet<ModuleLinkWrapper> realisticConnectionList = new HashSet<>();
		for(ICompilationUnitWrapper callerUnit: compilationUnitWrapperList){
			for(ICompilationUnitWrapper calleeUnit: compilationUnitWrapperList){
				if(!callerUnit.equals(calleeUnit)){
					ModuleWrapper callerModule = callerUnit.getMappingModule();
					ModuleWrapper calleeModule = calleeUnit.getMappingModule();
					
					if(callerUnit.toString().contains("WeatherData")){
						System.currentTimeMillis();
					}
					
					if(callerModule != null && calleeModule != null && !callerModule.equals(calleeModule)){	
						if(callerUnit.hasCalleeCompilationUnit(calleeUnit, ReferencingDetail.REFER)){
							ModuleLinkWrapper connection = new ModuleLinkWrapper(callerModule, calleeModule, ModuleLinkWrapper.MODULE_DEPENDENCY);
							realisticConnectionList.add(connection);
						}
						if(callerUnit.hasCalleeCompilationUnit(calleeUnit, ReferencingDetail.NEW)){
							ModuleLinkWrapper connection = new ModuleLinkWrapper(callerModule, calleeModule, ModuleLinkWrapper.MODULE_CREATION);
							realisticConnectionList.add(connection);
						}
						if(callerUnit.hasSuperCompilationUnit(calleeUnit)){
							ModuleLinkWrapper connection = new ModuleLinkWrapper(callerModule, calleeModule, ModuleLinkWrapper.MODULE_EXTEND);
							realisticConnectionList.add(connection);
						}
					}
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
	
	private void setDependencyType(DiagramRootEditPart diagramRoot, ModuleLinkWrapper connection, String connectionType){
		ModuleLinkEditPart part = GEFDiagramUtil.findCorrespondingDepedencyEditPart(diagramRoot, connection);
		ModuleLink link = (ModuleLink)part.resolveSemanticElement();
		setDependencyType(diagramRoot, link, connectionType);	
	}
	
	private void setDependencyType(DiagramRootEditPart diagramRoot, EObject link, String connectionType){
		SetRequest req = new SetRequest(link, ReflexactoringPackage.eINSTANCE.getModuleLink_Name(), connectionType);
		SetValueCommand setCommand = new SetValueCommand(req);
		GEFDiagramUtil.getRootEditPart(diagramRoot).getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(setCommand));
	}

	
}
