package reflexactoring.diagram.action.popup;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;

import reflexactoring.diagram.action.DiagramUpdater;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

public class ShowAllTypeDependencies  extends AbstractActionDelegate implements IObjectActionDelegate{

	public ShowAllTypeDependencies() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		Settings.enableVisibility = true;
		ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
		new DiagramUpdater().generateReflexionModel(moduleList, Settings.scope.getScopeCompilationUnitList());
	}
}
