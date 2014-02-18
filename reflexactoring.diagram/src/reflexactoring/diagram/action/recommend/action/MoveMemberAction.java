/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.bean.FieldWrapper;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.SuggestionObject;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class MoveMemberAction extends MoveAction {
	
	private ICompilationUnitWrapper targetUnit;

	@Override
	public void execute(SuggestionObject suggestionObj) {
		if(ReflexactoringUtil.getUnitListFromModule(this.getDestination()).size() != 0){
			SelectTargetUnitDialog dialog = new SelectTargetUnitDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), this.getDestination());
			dialog.create();
			if(dialog.open() == Window.OK){
				ICompilationUnitWrapper target = dialog.getTargetUnit();
				targetUnit = target;
				
				//Move!
				try {
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					IProject project = root.getProject(ReflexactoringUtil.getTargetProjectName());
					project.open(null);					
					IJavaProject javaProject = JavaCore.create(project);
					
					if(suggestionObj instanceof UnitMemberWrapper){
						IMember member = ((UnitMemberWrapper) suggestionObj).getJavaMember();						
						IType targetType = javaProject.findType(target.getFullQualifiedName());
						member.move(targetType, null, null, false, null);
					}					
				} catch (Exception e) {
					e.printStackTrace();
				}			
				
			}			
		}
	}

	@Override
	public void undoExecute(SuggestionObject suggestionObj) {
		
		if(targetUnit != null){
			//Move back!
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject(ReflexactoringUtil.getTargetProjectName());
			try {
				project.open(null);
			} catch (CoreException e) {
				e.printStackTrace();
			}					
			IJavaProject javaProject = JavaCore.create(project);
			
			if(suggestionObj instanceof UnitMemberWrapper){
				try {
					IType sourceType = javaProject.findType(targetUnit.getFullQualifiedName());
					UnitMemberWrapper unit = (UnitMemberWrapper) suggestionObj;				
					IMember member = null;
					if(suggestionObj instanceof MethodWrapper){
						ITypeBinding[] bindings = (((MethodWrapper) unit).getMethod()).resolveBinding().getParameterTypes();
						String[] signatures = new String[bindings.length];
						for(int i = 0; i < signatures.length; i++){
							signatures[i] = Signature.createTypeSignature(bindings[i].getName(), false);
						}
						member = sourceType.getMethod(unit.getName(), signatures);
					}else if(suggestionObj instanceof FieldWrapper){
						member = sourceType.getField(unit.getName());
					}					
					IType targetType = javaProject.findType(unit.getUnitWrapper().getFullQualifiedName());
					
					member.move(targetType, null, null, false, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			targetUnit = null;
		}
	}

	class SelectTargetUnitDialog extends TitleAreaDialog{
		
		private Combo unitCombo;
		private ICompilationUnitWrapper unit;
		private ModuleWrapper module;
		
		/**
		 * @param parentShell
		 */
		public SelectTargetUnitDialog(Shell parentShell, ModuleWrapper module) {
			super(parentShell);
			// TODO Auto-generated constructor stub
			this.module = module;
		}
		
		public void create() {
			setHelpAvailable(false);
			super.create();
			
			setTitle("Select a target class");
			setMessage("Please select a class as destination.", IMessageProvider.INFORMATION);
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			
			//parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
			Composite workArea = new Composite(parent, SWT.NONE);
			workArea.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
			GridLayout workAreaLayout = new GridLayout();
			workAreaLayout.numColumns = /*1*/1;
			workAreaLayout.horizontalSpacing = 10;
			workAreaLayout.marginHeight = 10;
			workAreaLayout.marginBottom = 10;
			workAreaLayout.marginLeft = 10;
			workAreaLayout.marginRight = 10;
			workAreaLayout.verticalSpacing = 10;
			workArea.setLayout(workAreaLayout);
			
			// The text fields will grow with the size of the dialog

			Label unitLabel = new Label(workArea, SWT.NONE);
			unitLabel.setText("Select class");
			unitLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

			unitCombo = new Combo(workArea, SWT.BORDER | SWT.READ_ONLY);
			unitCombo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
			for(ICompilationUnitWrapper unit: ReflexactoringUtil.getUnitListFromModule(module)){
				unitCombo.add(unit.getFullQualifiedName());
				unitCombo.setData(unit.getFullQualifiedName(), unit);
			}
			
			return workArea;
		}
		
		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			GridData parentGridData = new GridData();
			parentGridData.grabExcessHorizontalSpace = true;
			parentGridData.grabExcessVerticalSpace = true;
			parentGridData.horizontalAlignment = GridData.FILL;
			parentGridData.verticalAlignment = GridData.FILL;
			parent.setData(parentGridData);

			parent.setLayoutData(parentGridData);
			// Create Add button
			// Own method as we need to overview the SelectionAdapter
			createOkButton(parent, OK, "Ok", true);
			// Add a SelectionListener

			// Create Cancel button
			Button cancelButton = 
					createButton(parent, CANCEL, "Cancel", false);
			// Add a SelectionListener
			cancelButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setReturnCode(CANCEL);
					close();
				}
			});
		}
		
		protected Button createOkButton(Composite parent, int id, String label, boolean defaultButton) {
			// increment the number of columns in the button bar
			((GridLayout) parent.getLayout()).numColumns++;
			Button button = new Button(parent, SWT.PUSH);
			button.setText(label);
			button.setFont(JFaceResources.getDialogFont());
			button.setData(new Integer(id));
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					okPressed();
				}
			});
			if (defaultButton) {
				Shell shell = parent.getShell();
				if (shell != null) {
					shell.setDefaultButton(button);
				}
			}
			setButtonLayoutData(button);
			return button;
		}
		
		@Override
		protected void okPressed() {
			this.unit = (ICompilationUnitWrapper)unitCombo.getData(unitCombo.getText());
			super.okPressed();
		}
		
		public ICompilationUnitWrapper getTargetUnit(){
			return this.unit;
		}
	}
}
