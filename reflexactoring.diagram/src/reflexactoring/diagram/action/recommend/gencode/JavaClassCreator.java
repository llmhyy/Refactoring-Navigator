/**
 * 
 */
package reflexactoring.diagram.action.recommend.gencode;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jdt.internal.ui.wizards.NewInterfaceCreationWizard;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jdt.ui.wizards.NewInterfaceWizardPage;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class JavaClassCreator {
	
	public ICompilationUnitWrapper createClass(){
		NewClassWizardPage page =  new NewClassWizardPage();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
		IProject project = root.getProject(ReflexactoringUtil.getTargetProjectName());
		IJavaProject jProject = JavaCore.create(project);
		
		if (jProject.exists()) {
			IPackageFragmentRoot initRoot = null;
			IPackageFragmentRoot[] roots;
			try {
				roots = jProject.getPackageFragmentRoots();
				for (int i= 0; i < roots.length; i++) {
					if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
						initRoot = roots[i];
						page.setPackageFragmentRoot(initRoot, true);

						break;
					}
				}
			} catch (JavaModelException javaExp) {
				javaExp.printStackTrace();
			}
		}
		
		NewClassCreationWizard wizard = new NewClassCreationWizard(page, false);
		wizard.init(PlatformUI.getWorkbench(), null);
		WizardDialog wizardDialog = new WizardDialog(PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getShell(), wizard);
		
		wizardDialog.open();
		if(wizard.getCreatedElement() != null){
			return new ICompilationUnitWrapper(((IType)wizard.getCreatedElement()).getCompilationUnit());
		}
		return null;
	}
	
	public ICompilationUnitWrapper createInterface(){
		NewInterfaceWizardPage page =  new NewInterfaceWizardPage();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
		IProject project = root.getProject(ReflexactoringUtil.getTargetProjectName());
		IJavaProject jProject = JavaCore.create(project);
		
		if (jProject.exists()) {
			IPackageFragmentRoot initRoot = null;
			IPackageFragmentRoot[] roots;
			try {
				roots = jProject.getPackageFragmentRoots();
				for (int i= 0; i < roots.length; i++) {
					if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
						initRoot = roots[i];
						page.setPackageFragmentRoot(initRoot, true);

						break;
					}
				}
			} catch (JavaModelException javaExp) {
				javaExp.printStackTrace();
			}
		}
		
		NewInterfaceCreationWizard wizard = new NewInterfaceCreationWizard(page, false);
		wizard.init(PlatformUI.getWorkbench(), null);
		WizardDialog wizardDialog = new WizardDialog(PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getShell(), wizard);
		
		wizardDialog.open();
		if(wizard.getCreatedElement() != null){
			return new ICompilationUnitWrapper(((IType)wizard.getCreatedElement()).getCompilationUnit());
		}
		return null;
	}
}
