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
		String content = "test" + RecordParameters.scopeDecisionTime 
				+ RecordParameters.mapTime + 
				RecordParameters.typeSuggestion + 
				RecordParameters.memberSuggestion
				 + RecordParameters.rejectTime + RecordParameters.applyTime +
				 RecordParameters.referenceCheck + 
				 RecordParameters.freezeTime +
				 RecordParameters.fixAllMember
				 + RecordParameters.fixPartMember;
		
		/**
		 * Hi Adi,
		 * parse them into readable string, that will be fine.
		 */
		
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
