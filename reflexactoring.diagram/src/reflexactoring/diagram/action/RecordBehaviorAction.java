package reflexactoring.diagram.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import reflexactoring.diagram.util.RecordParameters;
import reflexactoring.diagram.util.ReflexactoringUtil;

public class RecordBehaviorAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
		IFile file = project.getFile("/record_data" + (++RecordParameters.recordTime));
		String content = "Behavior Record: \n" + 
				"Scope Decision Time: " + RecordParameters.scopeDecisionTime + "\n" +
				"Proceed Time: " + RecordParameters.proceedTime + "\n" +
//				"Type Suggestion: " + RecordParameters.typeSuggestion + "\n" +
//				"Member Suggestion: " + RecordParameters.memberSuggestion + "\n" +v
				"Check Hint Time: " + RecordParameters.checkHintTime + "\n" +
				"Reject Time: " + RecordParameters.rejectTime + "\n" +
				"Undo Reject Time: " + RecordParameters.undoRejectTime + "\n" +
//				"Approve Time: " + RecordParameters.approveTime + "\n" +
//				"Undo Approve Time: " + RecordParameters.undoApproveTime + "\n" +
				"Apply Time: " + RecordParameters.applyTime + "\n" +
				"Undo Apply Time: " + RecordParameters.undoApplyTime + "\n" +
				"Simulate Time: " + RecordParameters.simulateTime + "\n" +
				"Undo Simulate Time: " + RecordParameters.undoSimulateTime + "\n" +
				"Reference Check: " + RecordParameters.referenceCheck + "\n" ;
//				"Freeze Time: " + RecordParameters.freezeTime + "\n" +
//				"Fix All Member: " + RecordParameters.fixAllMember + "\n" +
//				"Fix Part Member: " + RecordParameters.fixPartMember;
		
		content += "Manual Maps: \n";
		for(String map : RecordParameters.manualMaps){
			content += map + "\n";
		}
		
		InputStream source = new ByteArrayInputStream(content.getBytes());
		try {
			file.create(source, false, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		RecordParameters.clear();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		
	}

}
