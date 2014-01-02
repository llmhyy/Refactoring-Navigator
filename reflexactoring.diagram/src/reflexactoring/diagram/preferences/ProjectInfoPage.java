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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
	
	private Combo projectCombo;
	private Text mappingThresholdText;
	
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
		
		//Activator.getDefault().getPreferenceStore().putValue(TARGET_PORJECT, this.projectCombo.getText());
		ReflexactoringUtil.setTargetProjectName(this.projectCombo.getText());
		ReflexactoringUtil.setMappingThreshold(this.mappingThresholdText.getText());
		return true;
	}
}
