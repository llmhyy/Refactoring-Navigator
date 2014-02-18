/**
 * 
 */
package reflexactoring.diagram.edit.policies;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GroupEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ShapeCompartmentDropEditPolicy;
import org.eclipse.ui.PlatformUI;

import reflexactoring.Module;
import reflexactoring.Type;
import reflexactoring.diagram.bean.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.edit.parts.Class2EditPart;
import reflexactoring.diagram.edit.parts.ClassEditPart;
import reflexactoring.diagram.edit.parts.Interface2EditPart;
import reflexactoring.diagram.edit.parts.InterfaceEditPart;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.util.Settings;
import reflexactoring.diagram.view.HeuristicMappingView;

/**
 * @author linyun
 *
 */
public class ModuleTypeContainerDropEditPolicy extends
		ShapeCompartmentDropEditPolicy {
	
	/**
	 * Overriden to ensure that we don't drag the top level shape inside the shape compartment itself since this precipitates 
	 * difficult movement behavior of the top level shape.
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.editpolicies.DragDropEditPolicy#getDropCommand(org.eclipse.gef.requests.ChangeBoundsRequest)
	 */
	protected Command getDropCommand(ChangeBoundsRequest request) {
		List editparts = request.getEditParts();
		for(int i=0; i<editparts.size(); i++) {
			Object obj = editparts.get(i);
			if (obj instanceof EditPart) {
				EditPart requestEP = (EditPart)obj;
				if (getHost() instanceof IGraphicalEditPart) {
					IGraphicalEditPart gep = (IGraphicalEditPart)getHost();
					if (gep.getTopGraphicEditPart().equals(requestEP)) {
						return null;
					} else if (requestEP.getParent() instanceof GroupEditPart) {
                       // Dragging shapes outside the group will cause the
                       // group to grow and thus should not reparent.
                       return null;
                    }
					
					ModuleEditPart modulePart = (ModuleEditPart)gep.getTopGraphicEditPart();
					Module module = (Module)modulePart.resolveSemanticElement();
					
					Type type = getSemanticType(requestEP);
					
					ICompilationUnitWrapper unitWrapper = Settings.scope.findUnit(type);
					unitWrapper.setMappingModule(new ModuleWrapper(module));
					
					if(type != null){
						ModuleWrapper moduleWrapper = insertMappingRelation(module, type);
						refreshHeuristicView();		
						
						unitWrapper.setMappingModule(moduleWrapper);
					}
				}
			}
		}
		
		return super.getDropCommand(request);
	}
	
	private void removeMappingRelation(Module module, Type type){
		String packageName = type.getPackageName();
		String typeName = type.getName();
		String identifier = packageName + "." + typeName;
		
		ICompilationUnitWrapper unitWrapper = Settings.scope.findUnit(identifier);
		if(null != unitWrapper){
			HeuristicModuleUnitMap extantMap = Settings.heuristicModuleUnitMapList.findHeuristicMapping(identifier);
			if(extantMap != null){
				Settings.heuristicModuleUnitMapList.remove(extantMap);
			}			
		}
	}
	
	private Type getSemanticType(EditPart requestEP){
		
		Type type = null;
		
		if(requestEP instanceof Class2EditPart){
			Class2EditPart typePart = (Class2EditPart)requestEP;
			type = (Type)typePart.resolveSemanticElement();
		}
		else if(requestEP instanceof ClassEditPart){
			ClassEditPart typePart = (ClassEditPart)requestEP;
			type = (Type)typePart.resolveSemanticElement();
		}
		else if(requestEP instanceof Interface2EditPart){
			Interface2EditPart typePart = (Interface2EditPart)requestEP;
			type = (Type)typePart.resolveSemanticElement();
		}
		else if(requestEP instanceof InterfaceEditPart){
			InterfaceEditPart typePart = (InterfaceEditPart)requestEP;
			type = (Type)typePart.resolveSemanticElement();
		}
		
		return type;
	}
	
	private void refreshHeuristicView(){
		HeuristicMappingView view = (HeuristicMappingView)PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().findView(ReflexactoringPerspective.HEURISTIC_MAPPING_VIEW);
		view.getViewer().setInput(Settings.heuristicModuleUnitMapList);
		view.getViewer().refresh();	
	}
	
	
	
	private ModuleWrapper insertMappingRelation(Module module, Type type){
		String packageName = type.getPackageName();
		String typeName = type.getName();
		String identifier = packageName + "." + typeName;
		
		ICompilationUnitWrapper unitWrapper = Settings.scope.findUnit(identifier);
		if(null != unitWrapper){
			HeuristicModuleUnitMap extantMap = Settings.heuristicModuleUnitMapList
					.findHeuristicMapping(identifier);
			if (extantMap != null) {
				Settings.heuristicModuleUnitMapList.remove(extantMap);
			}
			
			ModuleWrapper moduleWrapper = new ModuleWrapper(module);
			HeuristicModuleUnitMap map = new HeuristicModuleUnitMap(moduleWrapper, unitWrapper);
			Settings.heuristicModuleUnitMapList.addMap(map);	
			
			return moduleWrapper;
		}
		
		return null;
	}
}
