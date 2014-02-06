package reflexactoring.diagram.preferences;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.Preferences;

import reflexactoring.diagram.util.ReflexactoringUtil;

public class RecommendSettingPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public static final String MUTATION_RATE = "mutationRate";
	public static final String ITERATION_NUMBER = "iterationNumber";
	public static final String POPULATION_SIZE = "populationSize";
	public static final String ALPHA = "alpha";
	public static final String BETA = "beta";
	
	
	private Text iterationNumberText;
	private Text populationSizeText;
	private Text mutationRateText;
	private Text alphaText;
	private Text betaText;
	
	public RecommendSettingPage() {
		// TODO Auto-generated constructor stub
	}

	public RecommendSettingPage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public RecommendSettingPage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		
		layout.numColumns = 3;
		
		composite.setLayout(layout);
		
		Label iterationNumberLabel = new Label(composite, SWT.NONE);
		iterationNumberLabel.setText("Iteration Number");
		iterationNumberText = new Text(composite, SWT.BORDER);
		iterationNumberText.setText(ReflexactoringUtil.getIterationNumber());
		GridData iterationNumberData = new GridData(SWT.FILL, SWT.FILL, true, false);
		iterationNumberData.horizontalSpan = 2;
		iterationNumberText.setLayoutData(iterationNumberData);
		
		Label populationSizeLabel = new Label(composite, SWT.NONE);
		populationSizeLabel.setText("Population Size");
		populationSizeText = new Text(composite, SWT.BORDER);
		populationSizeText.setText(ReflexactoringUtil.getPopulationSize());
		GridData populationSizeData = new GridData(SWT.FILL, SWT.FILL, true, false);
		populationSizeData.horizontalSpan = 2;
		populationSizeText.setLayoutData(populationSizeData);
		
		Label mutationRateLabel = new Label(composite, SWT.NONE);
		mutationRateLabel.setText("Mutation Rate");
		mutationRateText = new Text(composite, SWT.BORDER);
		mutationRateText.setText(ReflexactoringUtil.getMutationRate());
		GridData mutationRateData = new GridData(SWT.FILL, SWT.FILL, true, false);
		iterationNumberData.horizontalSpan = 2;
		mutationRateText.setLayoutData(mutationRateData);
		
		
		return composite;
		
	}
	
	/*private void createComponentUI(Composite composite, String labelName, Text text, String content){
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelName);
		text = new Text(composite, SWT.BORDER);
		text.setText(content);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 2;
		text.setLayoutData(data);
	}*/
	
	public boolean performOk(){
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("Reflexactoring");
		preferences.put(ITERATION_NUMBER, this.iterationNumberText.getText());
		preferences.put(POPULATION_SIZE, this.populationSizeText.getText());
		preferences.put(MUTATION_RATE, this.mutationRateText.getText());
		
		ReflexactoringUtil.setIterationNumber(this.iterationNumberText.getText());
		ReflexactoringUtil.setPopulationSize(this.populationSizeText.getText());
		ReflexactoringUtil.setMutationRate(this.mutationRateText.getText());
		
		return true;
	}

}