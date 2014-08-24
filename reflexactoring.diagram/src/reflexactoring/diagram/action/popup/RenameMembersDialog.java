/**
 * 
 */
package reflexactoring.diagram.action.popup;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Adi
 *
 */
public class RenameMembersDialog extends TitleAreaDialog {

	private Text newMemberNameText;
	
	private String newMemberName;
	private String[] oldMemberNames;
	
	/**
	 * @param parentShell
	 */
	public RenameMembersDialog(Shell parentShell, String newMemberName, String[] oldMemberNames) {
		super(parentShell);
		this.newMemberName = newMemberName;
		this.oldMemberNames = oldMemberNames;
		// TODO Auto-generated constructor stub
	}
	
	public void create() {
		setHelpAvailable(false);
		super.create();
		
		setTitle("Set new name for members to be pulled up.");
		
		String methods = "";
		for(int i = 0; i < oldMemberNames.length; i++){
			methods += oldMemberNames[i];
			if(i != oldMemberNames.length - 1){
				methods += " , ";
			}
		}
		
		setMessage("Please set the new name for members: " + methods, IMessageProvider.INFORMATION);
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		
		//parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		Composite workArea = new Composite(parent, SWT.NONE);
		workArea.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		GridLayout workAreaLayout = new GridLayout();
		workAreaLayout.numColumns = 1;
		workAreaLayout.horizontalSpacing = 10;
		workAreaLayout.marginHeight = 10;
		workAreaLayout.marginBottom = 10;
		workAreaLayout.marginLeft = 10;
		workAreaLayout.marginRight = 10;
		workAreaLayout.verticalSpacing = 10;
		workArea.setLayout(workAreaLayout);

		// The text fields will grow with the size of the dialog
		Label nameLabel = new Label(workArea, SWT.NONE);
		nameLabel.setText("New Method Name");
		nameLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		newMemberNameText = new Text(workArea, SWT.BORDER);
		GridData nameTextData = new GridData(SWT.FILL, SWT.FILL, false, false);
		nameTextData.widthHint = 460;
		nameTextData.heightHint = 20;
		newMemberNameText.setLayoutData(nameTextData);
		
		String content = (this.newMemberName == null)? oldMemberNames[0].split("\\.")[1] : this.newMemberName;
		newMemberNameText.setText(content);
		
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
		this.newMemberName = newMemberNameText.getText();
		super.okPressed();
	}
	
	public String getNewMemberName(){
		return this.newMemberName;
	}
}
