package reflexactoring.diagram.action.popup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.PlatformUI;

import reflexactoring.Module;
import reflexactoring.Type;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.heuristics.HeuristicModuleUnitFixMemberMap;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.edit.parts.Class2EditPart;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.util.RecordParameters;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;
import reflexactoring.diagram.view.AllMemberInClassMappingFixView;
import reflexactoring.diagram.view.ViewUpdater;

public class FixMappingMemberAction extends AbstractActionDelegate implements IObjectActionDelegate{

	public FixMappingMemberAction() {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		IStructuredSelection selection = getStructuredSelection();
		Object selEditPart = selection.getFirstElement();
		
		if(selEditPart instanceof Class2EditPart){
			//RecordParameters.fixAllMember++;
			
			Class2EditPart editPart = (Class2EditPart)selEditPart;
			Type type = (Type) editPart.resolveSemanticElement();
			
			ICompilationUnitWrapper unitWrapper = Settings.scope.findUnit(type);
			ModuleWrapper moduleWrapper = unitWrapper.getMappingModule();
			
			HeuristicModuleUnitFixMemberMap fixMemberMap = new HeuristicModuleUnitFixMemberMap(moduleWrapper, unitWrapper);
			Settings.heuristicModuleUnitMemberFixList.add(fixMemberMap);
			
			ViewUpdater viewUpdater = new ViewUpdater();
			viewUpdater.updateView(ReflexactoringPerspective.MEMBER_MAPPING_FIX_BY_CLASS_VIEW, Settings.heuristicModuleUnitMemberFixList, true);
		}
		
	}

}
