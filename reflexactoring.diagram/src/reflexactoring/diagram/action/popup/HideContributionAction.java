package reflexactoring.diagram.action.popup;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;

import reflexactoring.ModuleDependency;
import reflexactoring.diagram.bean.ModuleDependencyWrapper;
import reflexactoring.diagram.edit.parts.ModuleDependencyEditPart;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart.TypeDependencyFigure;

public class HideContributionAction extends ContributionAction{

	public HideContributionAction() {
		
	}

	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		IStructuredSelection selection = getStructuredSelection();
		Object selEditPart = selection.getFirstElement();
		
		if(selEditPart instanceof ModuleDependencyEditPart){
			ModuleDependencyEditPart moduleDependencyEditPart = (ModuleDependencyEditPart)selEditPart;
			ModuleDependency moduleDependency = (ModuleDependency) moduleDependencyEditPart.resolveSemanticElement();
			if(!moduleDependency.getName().equals(ModuleDependencyWrapper.ABSENCE)){
				ModuleEditPart sourceModulePart = (ModuleEditPart)moduleDependencyEditPart.getSource();
				ModuleEditPart targetModulePart = (ModuleEditPart)moduleDependencyEditPart.getTarget();
				
				
				ArrayList<TypeDependencyEditPart> correspondingTypeDependencyParts = 
						findCorrespondingTypeDependencyParts(sourceModulePart, targetModulePart);
				
				for(TypeDependencyEditPart dependencyPart: correspondingTypeDependencyParts){
					TypeDependencyFigure depFigure = (TypeDependencyFigure)dependencyPart.getFigure();
					depFigure.setLineWidth(1);
					depFigure.getPolylineDecoration().setLineWidth(1);;
				}
			}
		}
		
	}

}
