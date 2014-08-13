package reflexactoring.diagram.action.popup;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Color;

import reflexactoring.ModuleDependency;
import reflexactoring.TypeDependency;
import reflexactoring.diagram.action.DiagramUpdater;
import reflexactoring.diagram.bean.DependencyWrapper;
import reflexactoring.diagram.bean.ModuleLinkWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.edit.parts.ModuleDependencyEditPart;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart.TypeDependencyFigure;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

public class HighlightDependencyContributionAction extends ContributionAction{

	public HighlightDependencyContributionAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		IStructuredSelection selection = getStructuredSelection();
		Object selEditPart = selection.getFirstElement();
		
		if(selEditPart instanceof ModuleDependencyEditPart){
			ModuleDependencyEditPart moduleDependencyEditPart = (ModuleDependencyEditPart)selEditPart;
			ModuleDependency moduleDependency = (ModuleDependency) moduleDependencyEditPart.resolveSemanticElement();
			if(!moduleDependency.getName().equals(ModuleLinkWrapper.ABSENCE)){
				ModuleEditPart sourceModulePart = (ModuleEditPart)moduleDependencyEditPart.getSource();
				ModuleEditPart targetModulePart = (ModuleEditPart)moduleDependencyEditPart.getTarget();
				
				
				ArrayList<TypeDependencyEditPart> correspondingTypeDependencyParts = 
						findCorrespondingTypeDependencyParts(sourceModulePart, targetModulePart);
				
				for(TypeDependencyEditPart dependencyPart: correspondingTypeDependencyParts){
					TypeDependency dep = (TypeDependency)dependencyPart.resolveSemanticElement();
					if(dep != null){
						DependencyWrapper depWrapper = new DependencyWrapper(dep);
						if(!Settings.highlightLinks.contains(depWrapper)){
							Settings.highlightLinks.add(depWrapper);							
						}
					}
				}
				
				//ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
				//new DiagramUpdater().generateReflexionModel(moduleList, Settings.scope.getScopeCompilationUnitList());	
				
				ArrayList<TypeDependencyEditPart> correspondingTypeDependencyParts0 = 
						findCorrespondingTypeDependencyParts(sourceModulePart, targetModulePart);
				for(TypeDependencyEditPart dependencyPart: correspondingTypeDependencyParts0){
					TypeDependencyFigure depFigure = (TypeDependencyFigure)dependencyPart.getFigure();
					depFigure.setForegroundColor(new Color(null, 66, 227, 227));
					depFigure.setLineWidth(2);
					depFigure.getPolylineDecoration().setLineWidth(2);
				}
				
			}
		}
		
	}
	
	
}
