package reflexactoring.diagram.action.popup;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ui.stackview.TreeContentProvider;
import org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.PlatformUI;

import reflexactoring.Type;
import reflexactoring.diagram.action.SelectionDialog;
import reflexactoring.diagram.bean.FieldWrapper;
import reflexactoring.diagram.bean.HeuristicModulePartFixMemberMap;
import reflexactoring.diagram.bean.HeuristicModulePartFixMemberMapList;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.edit.parts.Class2EditPart;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.util.RecordParameters;
import reflexactoring.diagram.util.Settings;
import reflexactoring.diagram.view.ViewUpdater;

public class FixPartOfMappingMemberAction extends AbstractActionDelegate implements IObjectActionDelegate{

	public FixPartOfMappingMemberAction() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gmf.runtime.common.ui.action.AbstractActionDelegate#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void doRun(IProgressMonitor progressMonitor) {
		IStructuredSelection selection = getStructuredSelection();
		Object selEditPart = selection.getFirstElement();
		
		if(selEditPart instanceof Class2EditPart){
			RecordParameters.fixPartMember++;
			
			Class2EditPart editPart = (Class2EditPart)selEditPart;
			Type type = (Type) editPart.resolveSemanticElement();
			
			ICompilationUnitWrapper unitWrapper = Settings.scope.findUnit(type);
			ModuleWrapper moduleWrapper = unitWrapper.getMappingModule();
			
			SelectionDialog memberDialog = new SelectionDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					new ScopeLabelProvider(), new ScopeContentProvider());
			memberDialog.setInput(unitWrapper);
			memberDialog.setTitle("Fix Member");
			memberDialog.setMessage("Please fix the members to module " + moduleWrapper.getName());
			HeuristicModulePartFixMemberMapList mapList = Settings.heuristicModuleMemberPartFixList.findMap(unitWrapper);
			if(mapList.size() > 0){
				ArrayList<UnitMemberWrapper> memWrapperInMapList = new ArrayList<UnitMemberWrapper>();
				for(HeuristicModulePartFixMemberMap map: mapList){
					memWrapperInMapList.add(map.getMember());
				}
				memberDialog.setInitialSelections(memWrapperInMapList.toArray(new UnitMemberWrapper[0]));
			}
			if(memberDialog.open() == Window.OK){
				for(HeuristicModulePartFixMemberMap map: mapList){
					Settings.heuristicModuleMemberPartFixList.removeMap(map);
				}
				for(Object memberWrapper: memberDialog.getResult()){
					HeuristicModulePartFixMemberMap map = new HeuristicModulePartFixMemberMap(moduleWrapper, (UnitMemberWrapper) memberWrapper);
					Settings.heuristicModuleMemberPartFixList.add(map);
				}
			}
			ViewUpdater viewUpdater = new ViewUpdater();
			viewUpdater.updateView(ReflexactoringPerspective.MEMBER_MAPING_FIX_VIEW, Settings.heuristicModuleMemberPartFixList, true);
		}
		
	}

	
	public class ScopeLabelProvider implements ILabelProvider{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element) {
			if(element instanceof UnitMemberWrapper){
				UnitMemberWrapper member = (UnitMemberWrapper)element;
				if(member instanceof FieldWrapper){
					return JavaPluginImages.get(JavaPluginImages.IMG_FIELD_PUBLIC);
				}
				else if(member instanceof MethodWrapper){
					return JavaPluginImages.get(JavaPluginImages.IMG_MISC_PUBLIC);
				}
			}

			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if(element instanceof UnitMemberWrapper){
				UnitMemberWrapper member = (UnitMemberWrapper)element;
				return member.getName();
			}

			return null;
		}
		
	}
	
	public class ScopeContentProvider implements ITreeContentProvider{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof ICompilationUnitWrapper){
				ICompilationUnitWrapper unitWrapper = (ICompilationUnitWrapper)inputElement;
				return unitWrapper.getMembers().toArray(new UnitMemberWrapper[0]);
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object parentElement) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
