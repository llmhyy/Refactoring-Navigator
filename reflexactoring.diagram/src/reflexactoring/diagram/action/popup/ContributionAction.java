/**
 * 
 */
package reflexactoring.diagram.action.popup;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.ui.IObjectActionDelegate;

import reflexactoring.diagram.edit.parts.Class2EditPart;
import reflexactoring.diagram.edit.parts.Interface2EditPart;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.edit.parts.ModuleTypeContainerCompartmentEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart;

/**
 * @author linyun
 *
 */
public abstract class ContributionAction extends AbstractActionDelegate implements IObjectActionDelegate{
	
	protected ArrayList<TypeDependencyEditPart> findCorrespondingTypeDependencyParts(
			ModuleEditPart sourceModulePart,
			ModuleEditPart targetModulePart) {
		
		ArrayList<TypeDependencyEditPart> list = new ArrayList<>();
		
		for(Object objChild: sourceModulePart.getChildren()){
			
			if(objChild instanceof ModuleTypeContainerCompartmentEditPart){
				ModuleTypeContainerCompartmentEditPart containerEditPart = (ModuleTypeContainerCompartmentEditPart)objChild;
				
				for(Object obj: containerEditPart.getChildren()){
					ShapeNodeEditPart part = null;
					if(obj instanceof Class2EditPart){
						part = (Class2EditPart)obj;
					}
					else if(obj instanceof Interface2EditPart){
						part = (Interface2EditPart)obj;
					}
					
					if(part != null){
						for(Object dependencyObj: part.getSourceConnections()){
							if(dependencyObj instanceof TypeDependencyEditPart){
								TypeDependencyEditPart typeDependencyPart = (TypeDependencyEditPart)dependencyObj;
								
								EditPart targetPart = typeDependencyPart.getTarget();
								
								if(isModuleContains(targetModulePart, targetPart)){
									list.add(typeDependencyPart);
									continue;
								}
							}
						}
					}
				}
				
			}
			
			
		}
		
		return list;
	}
	
	private boolean isModuleContains(ModuleEditPart modulePart, EditPart typePart){
		for(Object objChild: modulePart.getChildren()){
			if(objChild instanceof ModuleTypeContainerCompartmentEditPart){
				ModuleTypeContainerCompartmentEditPart containerEditPart = (ModuleTypeContainerCompartmentEditPart)objChild;
				
				if(containerEditPart.getChildren().contains(typePart)){
					return true;
				}
			}
		}
		
		return false;
	}
}
