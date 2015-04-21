package reflexactoring.diagram.action.popup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;

import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.heuristics.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.util.RecordParameters;
import reflexactoring.diagram.util.Settings;
import reflexactoring.diagram.view.ViewUpdater;

public class FixMappingAction extends AbstractActionDelegate implements IObjectActionDelegate{

	public FixMappingAction() {
	}

	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		for(HeuristicModuleUnitMap map : Settings.heuristicModuleUnitFixList){
			RecordParameters.manualMaps.add("Unit: " + map.getUnit() + " , Module: " + map.getModule());
		}
		
		for(ICompilationUnitWrapper unit : Settings.scope.getOutmostTypesInScope()){
			ModuleWrapper module = unit.getMappingModule();
			
			HeuristicModuleUnitMap map = new HeuristicModuleUnitMap(module, unit);
			Settings.heuristicModuleUnitFixList.add(map);
		}
		ViewUpdater updater = new ViewUpdater();
		updater.updateView(ReflexactoringPerspective.HEURISTIC_MAPPING_VIEW, Settings.heuristicModuleUnitFixList, true);
	}

}
