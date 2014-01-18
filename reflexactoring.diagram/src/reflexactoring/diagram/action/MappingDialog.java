/**
 * 
 */
package reflexactoring.diagram.action;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;

import reflexactoring.diagram.bean.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class MappingDialog extends TitleAreaDialog {

	private Combo moduleCombo;
	private Combo unitCombo;
	
	private ModuleWrapper module;
	private ICompilationUnitWrapper unit;
	private HeuristicModuleUnitMap map;
	
	/**
	 * @param parentShell
	 */
	public MappingDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}
	
	public void create() {
		setHelpAvailable(false);
		super.create();
		
		setTitle("Mapping Relation");
		setMessage("Please select a mapping relation.", IMessageProvider.INFORMATION);
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
		
		Group group = new Group(workArea, SWT.NONE);
		group.setText("");
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 2;
		groupLayout.verticalSpacing = 10;
		group.setLayout(groupLayout);

		// The text fields will grow with the size of the dialog

		Label moduleLabel = new Label(group, SWT.NONE);
		moduleLabel.setText("Mapping Module");
		moduleLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		moduleCombo = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
		moduleCombo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		try {
			for(ModuleWrapper module: ReflexactoringUtil.getModuleList(Settings.diagramPath)){
				moduleCombo.add(module.getName());
				moduleCombo.setData(module.getName(), module);
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		Label unitLabel = new Label(group, SWT.NONE);
		unitLabel.setText("Mapping Type");
		unitLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		unitCombo = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
		unitCombo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		for(ICompilationUnitWrapper type: Settings.scope.getScopeCompilationUnitList()){
			//ICompilationUnitWrapper type = new ICompilationUnitWrapper(unit);
			
			if(Settings.heuristicModuleUnitMapList.findHeuristicMapping(type) == null){
				unitCombo.add(type.getFullQualifiedName());
				unitCombo.setData(type.getFullQualifiedName(), type);
			}			
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
		createOkButton(parent, OK, "Map", true);
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
				if (isValidInput()) {
					okPressed();
				}
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
	
	private boolean isValidInput() {
		boolean valid = true;
		
		
		
		return valid;
	}

	
	private void saveInput() {
		this.module = (ModuleWrapper) moduleCombo.getData(moduleCombo.getText());
		this.unit = (ICompilationUnitWrapper)unitCombo.getData(unitCombo.getText());
	}

	@Override
	protected void okPressed() {
		saveInput();
		if(this.module != null && this.unit != null){
			this.map = new HeuristicModuleUnitMap(module, unit);
		}
		super.okPressed();
	}
	
	public HeuristicModuleUnitMap getHeuristicModuleUnitMap(){
		return this.map;
	}
}
