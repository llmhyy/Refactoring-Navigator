package reflexactoring.diagram.action.popup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.ui.IObjectActionDelegate;

import reflexactoring.Reflexactoring;
import reflexactoring.TypeDependency;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart;
import reflexactoring.diagram.util.GEFDiagramUtil;
import reflexactoring.diagram.util.Settings;

public class HideAllTypeDependencies extends AbstractActionDelegate implements IObjectActionDelegate{

	public HideAllTypeDependencies() {
	}

	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		Settings.enableVisibility = false;
		
		DiagramRootEditPart diagramRoot = GEFDiagramUtil.getRootEditPart();
		
		Reflexactoring reflexactoring = GEFDiagramUtil.findReflexactoring(diagramRoot);
		for(TypeDependency dep: reflexactoring.getTypeDependencies()){
			TypeDependencyEditPart part = GEFDiagramUtil.findCorrespondingTypeDependencyEditPart(diagramRoot, dep);
			part.getFigure().setVisible(false);
		}
		//ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
		//new DiagramUpdater().generateReflexionModel(moduleList, Settings.scope.getScopeCompilationUnitList());
	}

}
