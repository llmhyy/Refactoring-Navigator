/**
 * 
 */
package reflexactoring.diagram.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.Preferences;

import reflexactoring.Activator;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class ProjectInfoPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public static final String TARGET_PORJECT = "projectName";
	public static final String MAPPING_THRESHOLD = "mappingThreshold";
	public static final String STOP_LIST = "stopList";
	public static final String DICT_PATH = "wordNetDictPath";
	
	private Combo projectCombo;
	private Text mappingThresholdText;
	private Text stopListText;
	
	private Text dictPathText;
	private Button dictPathButton;
	private DirectoryDialog dictPathDialog; 
	
	//private String defaultTargetProject;
	/**
	 * 
	 */
	public ProjectInfoPage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 */
	public ProjectInfoPage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param image
	 */
	public ProjectInfoPage(String title, ImageDescriptor image) {
		super(title, image);
		//defaultTargetProject = Activator.getDefault().getPreferenceStore().getString(ProjectInfoPage.TARGET_PORJECT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		composite.setLayout(layout);
		
		Label projectLabel = new Label(composite, SWT.NONE);
		projectLabel.setText("Target Project");
		
		projectCombo = new Combo(composite, SWT.BORDER);
		projectCombo.setItems(getProjectsInWorkspace());
		projectCombo.setText(ReflexactoringUtil.getTargetProjectName());
		GridData comboData = new GridData(SWT.FILL, SWT.FILL, true, false);
		comboData.horizontalSpan = 2;
		projectCombo.setLayoutData(comboData);
		/*projectCombo.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				String projectName = projectCombo.getText();
				
			}
		});*/
		Label mappingThresholdLabel = new Label(composite, SWT.NONE);
		mappingThresholdLabel.setText("Mapping Threshold");
		mappingThresholdText = new Text(composite, SWT.BORDER);
		mappingThresholdText.setText(ReflexactoringUtil.getMappingThreshold());
		GridData mappingThresholdData = new GridData(SWT.FILL, SWT.FILL, true, false);
		mappingThresholdData.horizontalSpan = 2;
		mappingThresholdText.setLayoutData(mappingThresholdData);
		
		Label stopListLabel = new Label(composite, SWT.NONE);
		stopListLabel.setText("Stop List");
		stopListText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		stopListText.setText(ReflexactoringUtil.getStopList());
		GridData stopListData = new GridData(SWT.FILL, SWT.FILL, false, false);
		stopListData.horizontalSpan = 2;
		stopListData.widthHint = 250;
		stopListData.heightHint = 100;
		stopListText.setLayoutData(stopListData);
		

		Label dictPathLabel = new Label(composite, SWT.NONE);
		dictPathLabel.setText("WordNetDict Path");
		dictPathText = new Text(composite, SWT.BORDER);
		dictPathText.setText(ReflexactoringUtil.getDictPath());
		GridData dictPathTextData = new GridData(SWT.FILL, SWT.FILL, true, false);
		dictPathTextData.horizontalSpan = 1;
		dictPathText.setLayoutData(dictPathTextData);
		dictPathDialog = new DirectoryDialog(getShell(), SWT.OPEN);		
		dictPathButton = new Button(composite, SWT.PUSH);
		dictPathButton.setText("...");
		dictPathButton.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent e) {
	          String path = dictPathDialog.open();
	          if (path != null)
	        	  dictPathText.setText(path);
	        }
	      });
		
		return composite;
	}
	
	private String[] getProjectsInWorkspace(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		
		String[] projectStrings = new String[projects.length];
		for(int i=0; i<projects.length; i++){
			projectStrings[i] = projects[i].getName();
		}
		
		return projectStrings;
	}

	
	public boolean performOk(){
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("Reflexactoring");
		preferences.put(TARGET_PORJECT, this.projectCombo.getText());
		preferences.put(MAPPING_THRESHOLD, this.mappingThresholdText.getText());
		preferences.put(STOP_LIST, this.stopListText.getText());
		preferences.put(DICT_PATH, this.dictPathText.getText());
		
		//Activator.getDefault().getPreferenceStore().putValue(TARGET_PORJECT, this.projectCombo.getText());
		ReflexactoringUtil.setTargetProjectName(this.projectCombo.getText());
		ReflexactoringUtil.setMappingThreshold(this.mappingThresholdText.getText());
		ReflexactoringUtil.setStopList(this.stopListText.getText());
		ReflexactoringUtil.setDictPath(this.dictPathText.getText());
		
		return true;
	}
}
