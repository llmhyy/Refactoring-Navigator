/**
 * 
 */
package reflexactoring.diagram.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import reflexactoring.Activator;
import reflexactoring.diagram.action.recommend.Suggestion;
import reflexactoring.diagram.action.smelldetection.BadSmellDetector;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMethodToInterfaceOpportunity;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapperList;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.preferences.ProjectInfoPage;
import reflexactoring.diagram.util.RecordParameters;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;
import reflexactoring.diagram.view.HeuristicMappingView;
import reflexactoring.diagram.view.ViewUpdater;

/**
 * @author linyun
 *
 */
public class DecideScopeAction implements IWorkbenchWindowActionDelegate {	
	private Object[] previousSelections = null;
	
	class ProgramStructureExtractor implements IRunnableWithProgress{

		private int totalWork;
		
		public ProgramStructureExtractor(int totalWork){
			this.totalWork = totalWork;
		}
		
		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			int scale = 50;
			monitor.beginTask("build structural information", 4*totalWork*scale);
			new ClassStructureBuilder().buildStructuralDependency(Settings.scope.getScopeCompilationUnitList(), monitor, scale);
			//new UnitMemberExtractor().extract(Settings.scope.getScopeCompilationUnitList(), monitor, scale);
		}
		
	}
	
	@Override
	public void run(IAction action) {
		final SelectionDialog scopeDialog = new SelectionDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				new ScopeLabelProvider(), new ScopeContentProvider());
		
		Object[] projects = new Object[1];
		projects[0] = JavaCore.create(ReflexactoringUtil.getSpecificJavaProjectInWorkspace());
		scopeDialog.setInput(projects);
		
		scopeDialog.setContainerMode(true);
		if(this.previousSelections != null){
			scopeDialog.setExpandedElements(previousSelections);
			//scopeDialog.setInitialSelections(previousSelections);
			scopeDialog.setInitialElementSelections(Settings.scope.getScopeRawCompilationUnitList());			
		}
		//scopeDialog.setExpandedElements(projects);
		//scopeDialog.setInitialElementSelections(Settings.scopeCompilationUnitList);
		scopeDialog.setTitle("Define Refactoring Scope");
		scopeDialog.setMessage("Please select the refactoring scope.");
		if(scopeDialog.open() == Window.OK){
			RecordParameters.scopeDecisionTime++;
			/**
			 * Build dependencies amongst java types and its corresponding members in scope.
			 */
			final int totalWorks = scopeDialog.getResult().length;
			Job job = new Job("Building program structure") {
				
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					int totalWork = totalWorks;
					int scale = 50;
					monitor.beginTask("preserve the refactoring scope", totalWork*scale);
					Settings.scope.getScopeCompilationUnitList().clear();
					Object[] selectedObjects = scopeDialog.getResult();
					previousSelections = selectedObjects;
					for(int i=0; i<selectedObjects.length; i++){
						if(selectedObjects[i] instanceof ICompilationUnit){
							
							monitor.worked(scale);
							
							ICompilationUnit unit = (ICompilationUnit)selectedObjects[i];
							Settings.scope.getScopeCompilationUnitList().add(new ICompilationUnitWrapper(unit));
						}
						//Settings.scopeCompilationUnitList.add(selectedObjects[i]);
					}
					monitor.beginTask("build class structural information", 2*totalWork*scale);
					new ClassStructureBuilder().buildStructuralDependency(Settings.scope.getScopeCompilationUnitList(), monitor, scale);
					
					new BadSmellDetector().detect(Settings.scope);
					//monitor.beginTask("build method/field structure inforamtion", 2*totalWork*scale);
					//new UnitMemberExtractor().extract(Settings.scope.getScopeCompilationUnitList(), monitor, scale);
					
					Display.getDefault().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							/**
							 * Merging previous user input into the new reflexion model.
							 */
							UserInputMerger inputMerger = new UserInputMerger();
							inputMerger.mergeHeuristicMappingTable();
							inputMerger.mergeHeuristicFixMemberMappingTable();
							inputMerger.mergeForbiddenModuleMemberTable();
							inputMerger.mergeDependencyConfidenceTable();
							inputMerger.mergeHeuristicFixPartMemberMappingTable();
							/**
							 * refresh above four view
							 */
							ViewUpdater viewUpdater = new ViewUpdater();
							viewUpdater.updateView(ReflexactoringPerspective.HEURISTIC_MAPPING_VIEW, Settings.heuristicModuleUnitFixList, false);
							viewUpdater.updateView(ReflexactoringPerspective.MEMBER_MAPPING_FIX_BY_CLASS_VIEW, Settings.heuristicModuleUnitMemberFixList, false);
							viewUpdater.updateView(ReflexactoringPerspective.MODULE_MEMBER_FORBIDDEN_VIEW, Settings.heuristicModuleMemberStopMapList, false);
							viewUpdater.updateView(ReflexactoringPerspective.MODULE_UNIT_FORBIDDEN_VIEW, Settings.heuristicModuleUnitStopMapList, false);
							viewUpdater.updateView(ReflexactoringPerspective.DEPENDENCY_CONSTRAINT_CONFIDENCE_VIEW, Settings.dependencyConfidenceTable, false);
							viewUpdater.updateView(ReflexactoringPerspective.EXTEND_CONSTRAINT_CONFIDENCE_VIEW, Settings.extendConfidenceTable, false);
							viewUpdater.updateView(ReflexactoringPerspective.MEMBER_MAPING_FIX_VIEW, Settings.heuristicModuleMemberPartFixList, false);
							viewUpdater.updateView(ReflexactoringPerspective.REFACTORING_SUGGESTION, new ArrayList<Suggestion>(), false);
							
							AutoMappingAction mappingAction = new AutoMappingAction();
							mappingAction.run(null);						
						}
					});
					
					return Status.OK_STATUS;
				}
			};
			job.schedule();
			
			
			
			Settings.isCompliationUnitChanged = true;
			Settings.isNeedClearCache = true;
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	@Override
	public void dispose() {
		

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	public void init(IWorkbenchWindow window) {
		

	}
	
	class ScopeContentProvider implements ITreeContentProvider{

		@Override
		public void dispose() {
			
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof Object[]){
				return (Object[])parentElement;
			}
			if(parentElement instanceof IJavaProject){
				IJavaProject project = (IJavaProject)parentElement;
				try {
					IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
					for(int i=0; i<roots.length; i++){
						if(roots[i] instanceof PackageFragmentRoot && !(roots[i] instanceof JarPackageFragmentRoot)){
							PackageFragmentRoot root = (PackageFragmentRoot)roots[i];
							return root.getChildren();
						}
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
			else if(parentElement instanceof IPackageFragment){
				IPackageFragment pack = (IPackageFragment)parentElement;
				try {
					return pack.getCompilationUnits();
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			Object[] objects = getChildren(element);
			
			if(objects == null || objects.length == 0){
				return false;
			}
			else{
				return true;				
			}
			
		}
		
	}
	
	class ScopeLabelProvider implements ILabelProvider{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void addListener(ILabelProviderListener listener) {
			
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		@Override
		public void dispose() {
			
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		@Override
		public boolean isLabelProperty(Object element, String property) {
			
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void removeListener(ILabelProviderListener listener) {
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element) {
			if(element instanceof IJavaProject)
				return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASSFOLDER);
			else if(element instanceof IPackageFragment)
				return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_PACKAGE);
			else if(element instanceof ICompilationUnit){
				ICompilationUnit unit = (ICompilationUnit)element;
				try {
					if(unit.getTypes()[0].isClass()){
						return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS);
					}
					else if(unit.getTypes()[0].isInterface()){
						return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_INTERFACE);
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}

			return null;
				
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if(element instanceof IJavaProject){
				IJavaProject project = (IJavaProject)element;
				return project.getElementName();
			}
			else if(element instanceof IPackageFragment){
				IPackageFragment pack = (IPackageFragment)element;
				return pack.getElementName();
			}
			else if(element instanceof ICompilationUnit){
				ICompilationUnit unit = (ICompilationUnit)element;
				return unit.getElementName();
			}
			return null;
		}
		
	}

}
