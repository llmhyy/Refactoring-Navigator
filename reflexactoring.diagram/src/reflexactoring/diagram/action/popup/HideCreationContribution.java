package reflexactoring.diagram.action.popup;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Color;

import reflexactoring.ModuleCreation;
import reflexactoring.TypeDependency;
import reflexactoring.diagram.action.DiagramUpdater;
import reflexactoring.diagram.bean.DependencyWrapper;
import reflexactoring.diagram.bean.ModuleLinkWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.edit.parts.ModuleCreationEditPart;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart.TypeDependencyFigure;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

public class HideCreationContribution extends ContributionAction{

	public HideCreationContribution() {
	}

	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		IStructuredSelection selection = getStructuredSelection();
		Object selEditPart = selection.getFirstElement();
		
		if(selEditPart instanceof ModuleCreationEditPart){
			ModuleCreationEditPart moduleCreationEditPart = (ModuleCreationEditPart)selEditPart;
			ModuleCreation moduleDependency = (ModuleCreation) moduleCreationEditPart.resolveSemanticElement();
			if(!moduleDependency.getName().equals(ModuleLinkWrapper.ABSENCE)){
				ModuleEditPart sourceModulePart = (ModuleEditPart)moduleCreationEditPart.getSource();
				ModuleEditPart targetModulePart = (ModuleEditPart)moduleCreationEditPart.getTarget();
				
				
				ArrayList<TypeDependencyEditPart> correspondingTypeDependencyParts = 
						findCorrespondingTypeCreationParts(sourceModulePart, targetModulePart);
				
				for(TypeDependencyEditPart dependencyPart: correspondingTypeDependencyParts){
					TypeDependencyFigure depFigure = (TypeDependencyFigure)dependencyPart.getFigure();
					TypeDependency dep = (TypeDependency)dependencyPart.resolveSemanticElement();
					if(dep != null){
						DependencyWrapper depWrapper = new DependencyWrapper(dep);
						Settings.highlightLinks.remove(depWrapper);						
					}
					
					depFigure.setForegroundColor(new Color(null, 195, 195, 195));
					depFigure.setLineWidth(1);
					depFigure.getPolylineDecoration().setLineWidth(1);;
				}
				
				//ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
				//new DiagramUpdater().generateReflexionModel(moduleList, Settings.scope.getScopeCompilationUnitList());
			}
		}
	}
	
	

}
