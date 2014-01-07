/**
 * 
 */
package reflexactoring.diagram.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.ui.commands.CreateCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.DeferredCreateConnectionViewAndElementCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditDomain;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramEditDomain;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest.ViewAndElementDescriptor;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.emf.type.core.commands.SetValueCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import reflexactoring.Module;
import reflexactoring.Reflexactoring;
import reflexactoring.ReflexactoringPackage;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.edit.commands.Class2CreateCommand;
import reflexactoring.diagram.edit.commands.ClassCreateCommand;
import reflexactoring.diagram.edit.commands.Interface2CreateCommand;
import reflexactoring.diagram.edit.commands.InterfaceCreateCommand;
import reflexactoring.diagram.edit.parts.ReflexactoringEditPart;
import reflexactoring.diagram.part.ReflexactoringDiagramEditor;
import reflexactoring.diagram.part.ReflexactoringDiagramEditorPlugin;
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
	private void generateLowLevelModel(DiagramRootEditPart diagramRoot,
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
	
	private void setTypeValue(EObject targetObject, ICompilationUnitWrapper unitWrapper, IDiagramEditDomain diagramEditDomain){
		SetRequest setReq = new SetRequest(targetObject, ReflexactoringPackage.eINSTANCE.getType_Name(), unitWrapper.getUniqueName());
		SetValueCommand setClassNameCommand = new SetValueCommand(setReq);
		diagramEditDomain.getDiagramCommandStack().execute(new ICommandProxy(setClassNameCommand));
	}

	/**
	 * @param diagramRoot
	 * @param compilationUnitWrapperList
	 */
	private void generateLowLevelConnection(DiagramRootEditPart diagramRoot,
			ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param diagramRoot
	 * @param moduleList
	 * @param compilationUnitWrapperList
	 */
	private void showModelConformance(DiagramRootEditPart diagramRoot,
			ArrayList<ModuleWrapper> moduleList,
			ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList) {
		// TODO Auto-generated method stub
		
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
						return module.getDescription().equals(((Module)eObj).getDescription())? view : null;
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
						if(module.getDescription().equals(currentModule.getDescription())){
							return currentModule;
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
