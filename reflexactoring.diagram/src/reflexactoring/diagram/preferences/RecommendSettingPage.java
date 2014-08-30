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
	public static final String MAPPING_ITERATION_NUMBER = "mappingIterationNumber";
	public static final String CLIMB_ITERATION_NUMBER = "climbIterationNumber";
	public static final String POPULATION_SIZE = "populationSize";
	public static final String ALPHA = "alpha";
	public static final String BETA = "beta";
	public static final String SUGGESTION_NUMBER = "suggestionNumber";
	public static final String REWARD_RATE = "awardRate";
	public static final String PENALTY_RATE = "penaltyRate";
	
	
	private Text mappingIterationNumberText;
	private Text climbIterationNumberText;
	private Text populationSizeText;
	private Text mutationRateText;
	private Text alphaText;
	private Text betaText;
	private Text suggestionNumberText;
	private Text rewardRateText;
	private Text penaltyRateText;
	
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
		
		Label mappingIterationNumberLabel = new Label(composite, SWT.NONE);
		mappingIterationNumberLabel.setText("Mapping Iteration Number");
		mappingIterationNumberText = new Text(composite, SWT.BORDER);
		mappingIterationNumberText.setText(ReflexactoringUtil.getMappingIterationNumber());
		GridData mappingIterationNumberData = new GridData(SWT.FILL, SWT.FILL, true, false);
		mappingIterationNumberData.horizontalSpan = 2;
		mappingIterationNumberText.setLayoutData(mappingIterationNumberData);
		
		Label climpIterationNumberLabel = new Label(composite, SWT.NONE);
		climpIterationNumberLabel.setText("Climb Iteration Number");
		climbIterationNumberText = new Text(composite, SWT.BORDER);
		climbIterationNumberText.setText(ReflexactoringUtil.getClimbIterationNumber());
		GridData climpIterationNumberData = new GridData(SWT.FILL, SWT.FILL, true, false);
		climpIterationNumberData.horizontalSpan = 2;
		climbIterationNumberText.setLayoutData(climpIterationNumberData);
		
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
		mutationRateData.horizontalSpan = 2;
		mutationRateText.setLayoutData(mutationRateData);
		
		Label alphaLabel = new Label(composite, SWT.NONE);
		alphaLabel.setText("Lexical Similarity");
		alphaText = new Text(composite, SWT.BORDER);
		alphaText.setText(ReflexactoringUtil.getAlpha());
		GridData alphaData = new GridData(SWT.FILL, SWT.FILL, true, false);
		alphaData.horizontalSpan = 2;
		alphaText.setLayoutData(alphaData);
		
		Label betaLabel = new Label(composite, SWT.NONE);
		betaLabel.setText("Effort Consideration");
		betaText = new Text(composite, SWT.BORDER);
		betaText.setText(ReflexactoringUtil.getBeta());
		GridData betaData = new GridData(SWT.FILL, SWT.FILL, true, false);
		betaData.horizontalSpan = 2;
		betaText.setLayoutData(betaData);
		
		Label suggestionNumberLabel = new Label(composite, SWT.NONE);
		suggestionNumberLabel.setText("Suggestion Number");
		suggestionNumberText = new Text(composite, SWT.BORDER);
		suggestionNumberText.setText(ReflexactoringUtil.getSuggestionNumber());
		GridData suggestionNumberData = new GridData(SWT.FILL, SWT.FILL, true, false);
		suggestionNumberData.horizontalSpan = 2;
		suggestionNumberText.setLayoutData(suggestionNumberData);
		
		Label rewardRateLabel = new Label(composite, SWT.NONE);
		rewardRateLabel.setText("Reward Rate");
		rewardRateText = new Text(composite, SWT.BORDER);
		rewardRateText.setText(ReflexactoringUtil.getRewardRate());
		GridData rewardRateData = new GridData(SWT.FILL, SWT.FILL, true, false);
		rewardRateData.horizontalSpan = 2;
		rewardRateText.setLayoutData(rewardRateData);
		
		Label penaltyRateLabel = new Label(composite, SWT.NONE);
		penaltyRateLabel.setText("Penalty Rate");
		penaltyRateText = new Text(composite, SWT.BORDER);
		penaltyRateText.setText(ReflexactoringUtil.getPenaltyRate());
		GridData penaltyRateData = new GridData(SWT.FILL, SWT.FILL, true, false);
		penaltyRateData.horizontalSpan = 2;
		penaltyRateText.setLayoutData(penaltyRateData);
		
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
		preferences.put(MAPPING_ITERATION_NUMBER, this.mappingIterationNumberText.getText());
		preferences.put(CLIMB_ITERATION_NUMBER, this.climbIterationNumberText.getText());
		preferences.put(POPULATION_SIZE, this.populationSizeText.getText());
		preferences.put(MUTATION_RATE, this.mutationRateText.getText());
		preferences.put(ALPHA, this.alphaText.getText());
		preferences.put(BETA, this.betaText.getText());
		preferences.put(SUGGESTION_NUMBER, this.suggestionNumberText.getText());
		preferences.put(REWARD_RATE, this.rewardRateText.getText());
		preferences.put(PENALTY_RATE, this.penaltyRateText.getText());
		
		ReflexactoringUtil.setMappingIterationNumber(this.mappingIterationNumberText.getText());
		ReflexactoringUtil.setClimbIterationNumber(this.climbIterationNumberText.getText());
		ReflexactoringUtil.setPopulationSize(this.populationSizeText.getText());
		ReflexactoringUtil.setMutationRate(this.mutationRateText.getText());
		ReflexactoringUtil.setAlpha(this.alphaText.getText());
		ReflexactoringUtil.setBeta(this.betaText.getText());
		ReflexactoringUtil.setSuggestionNumber(this.suggestionNumberText.getText());
		ReflexactoringUtil.setRewardRate(this.rewardRateText.getText());
		ReflexactoringUtil.setPenaltyRate(this.penaltyRateText.getText());
		
		return true;
	}

}
