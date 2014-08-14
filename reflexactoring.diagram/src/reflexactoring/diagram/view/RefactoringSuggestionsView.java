package reflexactoring.diagram.view;

import gr.uom.java.jdeodorant.refactoring.manipulators.MoveMethodRefactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringAvailabilityTester;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringExecutionStarter;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.actions.ActionUtil;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.ltk.core.refactoring.CreateChangeOperation;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;
import org.eclipse.ui.part.ViewPart;

import reflexactoring.diagram.action.DiagramUpdater;
import reflexactoring.diagram.action.popup.ReferenceDetailMap;
import reflexactoring.diagram.action.recommend.SuggestionMove;
import reflexactoring.diagram.action.recommend.action.CreationAction;
import reflexactoring.diagram.action.recommend.action.DependencyAction;
import reflexactoring.diagram.action.recommend.action.ExtendAction;
import reflexactoring.diagram.action.recommend.action.LinkAction;
import reflexactoring.diagram.action.recommend.action.RefactoringAction;
import reflexactoring.diagram.action.recommend.gencode.JavaClassCreator;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequenceElement;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.CreateSuperclassAndPullUpMemberOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.MoveMethodOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMemberOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMemberToInterfaceOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleCreationConfidence;
import reflexactoring.diagram.bean.ModuleDependencyConfidence;
import reflexactoring.diagram.bean.ModuleExtendConfidence;
import reflexactoring.diagram.bean.ModuleLinkWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.SuggestionObject;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.util.RecordParameters;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

public class RefactoringSuggestionsView extends ViewPart {
	private Composite parent;
	private GridLayout gridLayout = new GridLayout();
		
	private final Color DARK_BLUE = new Color(Display.getCurrent(), 38, 81, 128);
	private final Color LIGHT_BLUE = new Color(Display.getCurrent(), 206, 237, 255);
	private final Color LIGHT_GRAY = new Color(Display.getCurrent(), 240, 240, 240);
	private final Color LIGHT_GREEN = new Color(Display.getCurrent(), 193, 255, 193);
	private final Color DARK_GREEN = new Color(Display.getCurrent(), 155, 205, 155);	

	private ArrayList<RefactoringSequence> suggestions = null;
	private RefactoringSequenceElement currentElement = null;
	private boolean isUndo = false;
	private int currentHeight = 0;
	private PerformChangeOperation performOperation;
	
	/**
	 * @return the currentElement
	 */
	public RefactoringSequenceElement getCurrentElement() {
		return currentElement;
	}

	/**
	 * @param currentElement the currentElement to set
	 */
	public void setCurrentElement(RefactoringSequenceElement currentElement) {
		this.currentElement = currentElement;
	}

	/**
	 * @return the isUndo
	 */
	public boolean isUndo() {
		return isUndo;
	}

	/**
	 * @param isUndo the isUndo to set
	 */
	public void setUndo(boolean isUndo) {
		this.isUndo = isUndo;
	}

	public RefactoringSuggestionsView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		parent.setLayout(gridLayout);
	}
	
	public void refreshSuggestionsOnUI(ArrayList<RefactoringSequence> suggestions){
		this.suggestions = suggestions;
		this.currentHeight = 0;
		
		for(Control control: parent.getChildren()){
			control.dispose();
		}
		
		//TODO
		Label text = new Label(parent, SWT.WRAP | SWT.TOP);
		text.setText("Refactoring Suggestions");
		text.setFont(new Font(Display.getCurrent(), "Arial", 14, SWT.BOLD));
		text.setForeground(DARK_BLUE);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for (RefactoringSequence sequence : suggestions) {
			TabItem item = new TabItem (tabFolder, SWT.NONE);
			item.setText ("Suggestion " + (suggestions.indexOf(sequence) + 1));
			
			final ScrolledComposite scrollComposite = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
			scrollComposite.setLayout(gridLayout);
			item.setControl(scrollComposite);
			
			final Composite composite = new Composite(scrollComposite, SWT.FILL);
			composite.setLayout(gridLayout);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			
			//add prerequisite
			Composite prerequisiteComposite = new Composite(composite, SWT.BORDER);
			prerequisiteComposite.setLayout(gridLayout);
			prerequisiteComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			prerequisiteComposite.setBackground(LIGHT_BLUE);
			//Label for "Prerequisite"
			Label prerequisiteTitle = new Label(prerequisiteComposite, SWT.WRAP | SWT.TOP);
			prerequisiteTitle.setText("Prerequisite");
			prerequisiteTitle.setFont(new Font(Display.getCurrent(), "Arial", 14, SWT.NORMAL));
			prerequisiteTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			prerequisiteTitle.setForeground(DARK_BLUE);
			prerequisiteTitle.setBackground(LIGHT_BLUE);
			//form for prerequisite actions
			final FormToolkit toolkitPre = new FormToolkit(parent.getDisplay());
			Composite formCompositePre = toolkitPre.createComposite(prerequisiteComposite);
			formCompositePre.setLayout(new TableWrapLayout());
			formCompositePre.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
						
			//generatePrerequisiteUI(sequence, toolkitPre, formCompositePre);			

			//add suggestions
			for(RefactoringSequenceElement element : sequence){
			
				//Composite for single suggestion
				Composite elementComposite = new Composite(composite, SWT.BORDER);
				elementComposite.setLayout(gridLayout);
				elementComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
				
				//Label for "Step X"
				Label title = new Label(elementComposite, SWT.WRAP | SWT.TOP);
				title.setText("Step " + (sequence.indexOf(element) + 1) + " (" + element.getOpportunity().getRefactoringName() + ")");
				title.setFont(new Font(Display.getCurrent(), "Arial", 14, SWT.NORMAL));
				title.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
				
				//Label for description
				Label description = new Label(elementComposite, SWT.WRAP | SWT.TOP);
				description.setText("Description: " + element.getOpportunity().getRefactoringDescription());
				description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
				
				//ExpandBar for detail
				final ExpandBar detailBar = new ExpandBar (elementComposite, SWT.V_SCROLL);
				//detailBar.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				detailBar.setForeground(DARK_BLUE);
				detailBar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

				//Composite for details
				final Composite detailComposite = generateRefactoringDetailUI(detailBar);
				
				//generate detail?
				for(int i = 0; i < element.getOpportunity().getRefactoringDetails().size(); i++){
					String detail = element.getOpportunity().getRefactoringDetails().get(i);
					Label testDetail = new Label(detailComposite, SWT.WRAP | SWT.TOP);
					testDetail.setText((i + 1) + ". " + detail +".");
					testDetail.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
					testDetail.setBackground(LIGHT_BLUE);
				}				
				ExpandItem detailItem = new ExpandItem (detailBar, SWT.NONE, 0);
				detailItem.setText("View Detail");
				//not able to get the actual height of detailComposite, only know the height difference between parent and the composite is about 65, use it to calculate the height...
				detailItem.setHeight(detailComposite.computeSize(parent.getBounds().width - 65, SWT.DEFAULT).y);
				//System.out.println(detailItem.getHeight() + " : " + detailComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + " : " + detailComposite.computeSize(parent.getBounds().width - 65, SWT.DEFAULT).y);
				detailItem.setControl(detailComposite);
				
				//Form for actions	
				final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
				Composite formComposite = toolkit.createComposite(elementComposite);
				formComposite.setLayout(new TableWrapLayout());
				formComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
				final FormText formText = toolkit.createFormText(formComposite, true);
				formText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
				
				generateSuggestionActionUI(element, sequence, toolkit, formText);

				//disable the following suggestions
				int currentElementIndex = 0;
				if(currentElement != null && sequence.contains(currentElement)){
					if(!isUndo){
						currentElementIndex = sequence.indexOf(currentElement) + 1;
						if(currentHeight == 0){
							currentHeight = prerequisiteComposite.computeSize(parent.getBounds().width, SWT.DEFAULT).y + 20;
						}
						if(sequence.indexOf(element) < currentElementIndex){
							currentHeight += elementComposite.computeSize(parent.getBounds().width - 10, SWT.DEFAULT).y + 10;
						}
					}else{
						currentElementIndex = sequence.indexOf(currentElement);
						if(currentElementIndex == 0){
							currentHeight = 0;
						}else{
							if(currentHeight == 0){
								currentHeight = prerequisiteComposite.computeSize(parent.getBounds().width, SWT.DEFAULT).y + 20;
							}
							if(sequence.indexOf(element) < currentElementIndex){
								currentHeight += elementComposite.computeSize(parent.getBounds().width - 10, SWT.DEFAULT).y + 10;
							}
						}
					}
				}
				if(sequence.indexOf(element) < currentElementIndex){
					//make the former steps a little bit different					
					//the nearest one is able & color lighter
					if(sequence.indexOf(element) == currentElementIndex - 1){
						title.setBackground(LIGHT_GREEN);
						description.setBackground(LIGHT_GREEN);
						
						detailBar.setBackground(LIGHT_GREEN);
						formComposite.setBackground(LIGHT_GREEN);
						formText.setBackground(LIGHT_GREEN);
						elementComposite.setBackground(LIGHT_GREEN);
						elementComposite.setEnabled(true);
					}else{
						title.setBackground(DARK_GREEN);
						description.setBackground(DARK_GREEN);
						
						detailBar.setBackground(DARK_GREEN);
						formComposite.setBackground(DARK_GREEN);
						formText.setBackground(DARK_GREEN);
						elementComposite.setBackground(DARK_GREEN);
						//elementComposite.setEnabled(false);
						/**
						 * Leaving it true for my debugging -- LinYun
						 */
						elementComposite.setEnabled(true);
					}
				}else if(sequence.indexOf(element) > currentElementIndex){
					//disable the following steps
					detailBar.setBackground(LIGHT_GRAY);
					formComposite.setBackground(LIGHT_GRAY);
					formText.setBackground(LIGHT_GRAY);
					elementComposite.setBackground(LIGHT_GRAY);
					//elementComposite.setEnabled(false);
					/**
					 * Leaving it true for my debugging -- LinYun
					 */
					elementComposite.setEnabled(true);
				}
				elementComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			}
			
			scrollComposite.setContent(composite);
			scrollComposite.setExpandVertical(true);
			scrollComposite.setExpandHorizontal(true);
			scrollComposite.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					Rectangle r = scrollComposite.getClientArea();
					scrollComposite.setMinSize(composite.computeSize(r.width, SWT.DEFAULT));
					
					if(currentHeight == 0){
						scrollComposite.setOrigin(scrollComposite.getOrigin());
					}else{
						scrollComposite.setOrigin(0, currentHeight);
					}
				}
			});
		}
		
		parent.layout();
	}

	/**
	 * @param detailBar
	 * @return
	 */
	private Composite generateRefactoringDetailUI(final ExpandBar detailBar) {
		final Composite detailComposite = new Composite (detailBar, SWT.NONE);
		detailComposite.setLayout(gridLayout);
		detailComposite.setBackground(LIGHT_BLUE);
		
		detailBar.addExpandListener(new ExpandListener() {

		    public void itemCollapsed(ExpandEvent e) {
		    	if (e.item instanceof ExpandItem){
		            ExpandItem item = (ExpandItem)e.item;
		            detailBar.getParent().setSize(detailBar.getParent().getSize().x, detailBar.getParent().getSize().y - item.getHeight());

		            GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		            //gridData.widthHint = detailBar.getParent().getSize().x - detailBar.getSpacing();
		            gridData.heightHint = detailBar.getParent().getSize().y - detailBar.getSpacing();
		            detailBar.getParent().setLayoutData(gridData);
		            detailBar.getParent().getParent().layout();

		            detailBar.setSize(detailBar.getSize().x, detailBar.getSize().y - item.getHeight());	
		            
		            gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		            gridData.heightHint = detailBar.getSize().y;
		            detailBar.setLayoutData(gridData);
		            detailBar.getParent().layout();
		        }
		    }

		    public void itemExpanded(ExpandEvent e) {
		    	if (e.item instanceof ExpandItem){
		            ExpandItem item = (ExpandItem)e.item;
		            //not able to get the actual height of detailComposite, only know the height difference between parent and the composite is about 65, use it to calculate the height...
		            item.setHeight(detailComposite.computeSize(parent.getBounds().width - 65, SWT.DEFAULT).y);
		            
		            detailBar.getParent().setSize(detailBar.getParent().getSize().x, detailBar.getParent().getSize().y + item.getHeight());

		            GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		            //gridData.widthHint = detailBar.getParent().getSize().x - detailBar.getSpacing();
		            gridData.heightHint = detailBar.getParent().getSize().y - detailBar.getSpacing();
		            detailBar.getParent().setLayoutData(gridData);
		            detailBar.getParent().getParent().layout();
		            
		            detailBar.setSize(detailBar.getSize().x, detailBar.getSize().y + item.getHeight());	
		            
		            gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		            gridData.heightHint = detailBar.getSize().y;
		            detailBar.setLayoutData(gridData);
		            detailBar.getParent().layout();
		        }
		    }

		});
		return detailComposite;
	}

	/**
	 * @param element
	 * @param toolkit
	 * @param formText
	 * @param sequence 
	 */
	private void generateSuggestionActionUI(final RefactoringSequenceElement element, final RefactoringSequence sequence,
			final FormToolkit toolkit, final FormText formText) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>");
		buffer.append("<li>");
		buffer.append("<b>[</b>");
		buffer.append(" <a href=\"Simulate\">Simulate</a> ");	
		buffer.append("<a href=\"UndoSimulate\">Undo</a> ");
		buffer.append("<b>]</b> You can also <a href=\"Hint\">Check Hint</a>");
		buffer.append("</li>");	
		buffer.append("<li>");
		buffer.append("<b>[</b> <a href=\"Forbid\">Reject</a> ");
		buffer.append("<a href=\"Allow\">Undo</a> ");
		buffer.append("<b>]</b>");
		buffer.append("</li>");	
		buffer.append("<li>");
		buffer.append("<b>[</b> <a href=\"Exec\">Approve</a> ");
		buffer.append("<a href=\"Undo\">Undo</a> ");
		buffer.append("<b>]</b>");
		buffer.append("</li>");
		buffer.append("</form>");
		
		formText.setText(buffer.toString(), true, false);
		formText.setData(element.getOpportunity());
		formText.addHyperlinkListener(new HyperlinkAdapter() {
			@SuppressWarnings("restriction")
			public void linkActivated(HyperlinkEvent e) {
				RefactoringOpportunity opportunity = (RefactoringOpportunity) formText.getData();
				if(e.getHref().equals("Forbid")){
					if(!Settings.forbiddenOpps.contains(opportunity)){
						Settings.forbiddenOpps.add(opportunity);
					}
					
					ViewUpdater updater = new ViewUpdater();
					updater.updateView(ReflexactoringPerspective.FORBIDDEN_REFACTORING_OPP_VIEW, Settings.forbiddenOpps, true);
											
					FormText t = (FormText) e.getSource();
					FormColors colors = toolkit.getColors();
					colors.createColor("gray", new RGB(207,207,207));
					colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
					t.setBackground(colors.getColor("gray"));
					t.setForeground(colors.getColor("white"));
				}else if(e.getHref().equals("Allow")){
					
					Iterator<RefactoringOpportunity> iterator = Settings.forbiddenOpps.iterator();
					while(iterator.hasNext()){
						RefactoringOpportunity opp = iterator.next();
						if(opp.getRefactoringDescription().equals(opportunity.getRefactoringDescription()) 
								&& opp.getRefactoringName().equals(opportunity.getRefactoringName())){
							iterator.remove();
						}
					}
					
					ViewUpdater updater = new ViewUpdater();
					updater.updateView(ReflexactoringPerspective.FORBIDDEN_REFACTORING_OPP_VIEW, Settings.forbiddenOpps, true);
											
					FormText t = (FormText) e.getSource();
					FormColors colors = toolkit.getColors();
					colors.createColor("black", colors.getSystemColor(SWT.COLOR_BLACK));
					t.setForeground(colors.getColor("black"));
					colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
					t.setBackground(colors.getColor("white"));
				}
				else if(e.getHref().equals("Simulate")){
					ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
					ProgramModel model = element.getConsequenceModel();
					new DiagramUpdater().generateReflexionModel(moduleList, model.getScopeCompilationUnitList());
				}
				else if(e.getHref().equals("UndoSimulate")){
					ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
					ProgramModel model;
					if(element.getPosition() == 0){
						model = Settings.scope;
					}
					else{
						model = sequence.get(element.getPosition()-1).getConsequenceModel();
					}
					
					new DiagramUpdater().generateReflexionModel(moduleList, model.getScopeCompilationUnitList());
				}
				else if(e.getHref().equals("Hint")){
					ReferenceDetailMap map = new ReferenceDetailMap(null, null, element.getOpportunity().getHints());
					ViewUpdater updater = new ViewUpdater();
					updater.updateView(ReflexactoringPerspective.REFERENCE_DETAIL_VIEW, map, true);
				}
				else if(e.getHref().equals("Exec")){
					RefactoringSuggestionsView view = (RefactoringSuggestionsView)PlatformUI.getWorkbench().
							getActiveWorkbenchWindow().getActivePage().findView(ReflexactoringPerspective.REFACTORING_SUGGESTIONS);
					view.setCurrentElement(element);
					view.setUndo(false);
					view.refreshSuggestionsOnUI(suggestions);
					
					//TODO do execution
					if(opportunity instanceof MoveMethodOpportunity){						
						MoveMethodOpportunity moveMethodOpportunity = (MoveMethodOpportunity) opportunity;
						
						MethodDeclaration methodDeclaration = (MethodDeclaration) moveMethodOpportunity.getObjectMethod().getJavaElement();												
						CompilationUnit sourceCompilationUnit = moveMethodOpportunity.getSourceUnit().getJavaUnit();
						CompilationUnit targetCompilationUnit = moveMethodOpportunity.getTargetUnit().getJavaUnit();
						TypeDeclaration sTypeDeclaration = (TypeDeclaration) sourceCompilationUnit.types().get(0);
						TypeDeclaration tTypeDeclaration = (TypeDeclaration) targetCompilationUnit.types().get(0);
						
						Map<MethodInvocation, MethodDeclaration> additionalMethodsToBeMoved = new HashMap<MethodInvocation, MethodDeclaration>();
//						for(ProgramReference reference : moveMethodOpportunity.getObjectMethod().getRefererPointList()){
//							additionalMethodsToBeMoved.put((MethodInvocation)reference.getASTNode(), (MethodDeclaration)((UnitMemberWrapper) reference.getReferee()).getJavaElement());
//							if(!additionalMethodsToBeMoved.containsKey((MethodInvocation)reference.getASTNode())){
//								additionalMethodsToBeMoved.put((MethodInvocation)reference.getASTNode(), methodDeclaration);
//							}
//						}
						
						MoveMethodRefactoring refactoring = new MoveMethodRefactoring(sourceCompilationUnit, targetCompilationUnit,
								sTypeDeclaration, tTypeDeclaration, methodDeclaration,
								additionalMethodsToBeMoved, false, moveMethodOpportunity.getObjectMethod().getName());
						
						try {
							NullProgressMonitor monitor = new NullProgressMonitor();
							RefactoringStatus status = refactoring.checkAllConditions(monitor);
							CreateChangeOperation operation = new CreateChangeOperation(refactoring);			
							performOperation = new PerformChangeOperation(operation);
							performOperation.run(monitor);
						} catch (OperationCanceledException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CoreException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
//						MyRefactoringWizard wizard = new MyRefactoringWizard(refactoring, new TestAction());
//						RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard); 
//						try { 
//							String titleForFailedChecks = ""; //$NON-NLS-1$ 
//							op.run(getSite().getShell(), titleForFailedChecks); 
//						} catch(InterruptedException e1) {
//							e1.printStackTrace();
//						}
					}else if(opportunity instanceof PullUpMemberOpportunity){
						if(opportunity instanceof CreateSuperclassAndPullUpMemberOpportunity){
							JavaClassCreator javaCreator = new JavaClassCreator();
							ICompilationUnitWrapper parentClass = javaCreator.createClass();		
							//TODO make every child class extends the parent class
							ICompilationUnit unit = parentClass.getCompilationUnit();
							try {
								ITypeHierarchy hierarchy = unit.getAllTypes()[0].newSupertypeHierarchy(null);
								
								System.currentTimeMillis();
							} catch (JavaModelException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							CompilationUnit parentUnit = parse(parentClass.getCompilationUnit());
							
						}else if(opportunity instanceof PullUpMemberToInterfaceOpportunity){
							JavaClassCreator javaCreator = new JavaClassCreator();
							ICompilationUnitWrapper parentInterface = javaCreator.createInterface();		
							//TODO make every child class implements the interface	
							ICompilationUnit unit = parentInterface.getCompilationUnit();
							try {
								ITypeHierarchy hierarchy = unit.getAllTypes()[0].newSupertypeHierarchy(null);
								
								System.currentTimeMillis();
							} catch (JavaModelException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							
							
							try {
								unit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
								IBuffer buffer = unit.getBuffer();
								
								
								CompilationUnit parentUnit = parse(parentInterface.getCompilationUnit());
								TypeDeclaration td = (TypeDeclaration)parentUnit.types().get(0);
								Name n = td.getAST().newSimpleName("Record");
								Type t = td.getAST().newSimpleType(n);
								td.setSuperclassType(t);
								
								buffer.setContents(parentUnit.toString());
								
								
								JavaModelUtil.reconcile(unit);
								unit.commitWorkingCopy(true, new NullProgressMonitor());
								
//								if(unit != null){
//									unit.discardWorkingCopy();
//								}
								
								
								//unit.reconcile(ICompilationUnit.NO_AST, false, null, null);
							} catch (JavaModelException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							System.currentTimeMillis();
						}
						
						ArrayList<UnitMemberWrapper> memberList = ((PullUpMemberOpportunity) opportunity).getToBePulledMemberList();
						IMember[] members = new IMember[memberList.size()];
						for(UnitMemberWrapper memberWrapper : memberList){
							members[memberList.indexOf(memberWrapper)] = memberWrapper.getJavaMember();							
						}
						
						try {
							System.out.println(RefactoringAvailabilityTester.isPullUpAvailable(members));
							System.out.println(ActionUtil.isEditable(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), members[0]));
							if (RefactoringAvailabilityTester.isPullUpAvailable(members) && ActionUtil.isEditable(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), members[0]))
								RefactoringExecutionStarter.startPullUpRefactoring(members, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
						} catch (JavaModelException jme) {
							ExceptionHandler.handle(jme, RefactoringMessages.OpenRefactoringWizardAction_refactoring, RefactoringMessages.OpenRefactoringWizardAction_exception);
						}						
						
					}
					
					//do approved now
					if(!Settings.approvedOpps.contains(opportunity)){
						Settings.approvedOpps.add(opportunity);
					}
					ViewUpdater updater = new ViewUpdater();
					updater.updateView(ReflexactoringPerspective.APPROVED_REFACTORING_OPP_VIEW, Settings.approvedOpps, true);
											
				}
				else if(e.getHref().equals("Undo")){
					RefactoringSuggestionsView view = (RefactoringSuggestionsView)PlatformUI.getWorkbench().
							getActiveWorkbenchWindow().getActivePage().findView(ReflexactoringPerspective.REFACTORING_SUGGESTIONS);
					view.setCurrentElement(element);
					view.setUndo(true);
					view.refreshSuggestionsOnUI(suggestions);
					
					//TODO do undo
					if(opportunity instanceof MoveMethodOpportunity){	
						if(performOperation != null){
							try {
								NullProgressMonitor monitor = new NullProgressMonitor();		
								PerformChangeOperation performUndoOperation = new PerformChangeOperation(performOperation.getUndoChange());
								performUndoOperation.run(monitor);
							} catch (OperationCanceledException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (CoreException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}else{
						try {
							IWorkbenchOperationSupport operationSupport = PlatformUI.getWorkbench().getOperationSupport();
							IUndoContext context = operationSupport.getUndoContext();
							IOperationHistory operationHistory = operationSupport.getOperationHistory();  
							IStatus status = operationHistory.undo(context, null, null);
						} catch (ExecutionException ee) {
							ee.printStackTrace();
						}
					}
					
					//undo approved now
					Iterator<RefactoringOpportunity> iterator = Settings.approvedOpps.iterator();
					while(iterator.hasNext()){
						RefactoringOpportunity opp = iterator.next();
						if(opp.getRefactoringDescription().equals(opportunity.getRefactoringDescription()) 
								&& opp.getRefactoringName().equals(opportunity.getRefactoringName())){
							iterator.remove();
						}
					}
					
					ViewUpdater updater = new ViewUpdater();
					updater.updateView(ReflexactoringPerspective.APPROVED_REFACTORING_OPP_VIEW, Settings.approvedOpps, true);
						
				}
			}
		});
	}

	/**
	 * @param sequence
	 * @param toolkitPre
	 * @param formCompositePre
	 */
	private void generatePrerequisiteUI(RefactoringSequence sequence,
			final FormToolkit toolkitPre, Composite formCompositePre) {
		for(SuggestionMove prerequisite : sequence.getPrerequisite()){
			final FormText formTextPre = toolkitPre.createFormText(formCompositePre, true);
			formTextPre.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
			StringBuffer bufferPre = new StringBuffer();
			bufferPre.append("<form>");
			bufferPre.append("<li>");
			bufferPre.append(prerequisite.generateTagedText());
			bufferPre.append("</li>");	
			
			bufferPre.append("<li bindent=\"20\">");
			bufferPre.append("<b>[</b> ");
			if(prerequisite.getAction() instanceof LinkAction){
				if(prerequisite.getAction() instanceof DependencyAction){
					bufferPre.append("<a href=\"StickDependency\">Reject</a> ");	
					bufferPre.append("<a href=\"UnstickDependency\">Undo</a>");	
				}else if(prerequisite.getAction() instanceof ExtendAction){
					bufferPre.append("<a href=\"StickExtend\">Reject</a> ");	
					bufferPre.append("<a href=\"UnstickExtend\">Undo</a>");	
				}else if(prerequisite.getAction() instanceof CreationAction){
					bufferPre.append("<a href=\"StickCreation\">Reject</a> ");	
					bufferPre.append("<a href=\"UnstickCreation\">Undo</a>");	
				}
			}
			bufferPre.append("<b>]</b>");
			bufferPre.append("</li>");	
			bufferPre.append("<li bindent=\"20\">");
			bufferPre.append("<b>[</b> <a href=\"Exec\">Apply</a> ");
			bufferPre.append("<a href=\"Undo\">Undo</a> ");
			bufferPre.append("<b>]</b>");
			bufferPre.append("</li>");
			bufferPre.append("</form>");
			
			formTextPre.setText(bufferPre.toString(), true, false);
			formTextPre.setData(prerequisite);
			formTextPre.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
					SuggestionMove suggestion = (SuggestionMove) formTextPre.getData();
					if(e.getHref().equals("Exec")){
						RecordParameters.applyTime++;
						
						suggestion.apply();
						FormText t = (FormText) e.getSource();
						FormColors colors = toolkitPre.getColors();
						colors.createColor("green", new RGB(154,205,50));
						t.setForeground(colors.getColor("green"));
						colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
						t.setBackground(colors.getColor("white"));
					}
					else if(e.getHref().equals("Undo")){
						suggestion.undoApply();
						FormText t = (FormText) e.getSource();
						FormColors colors = toolkitPre.getColors();
						colors.createColor("black", colors.getSystemColor(SWT.COLOR_BLACK));
						t.setForeground(colors.getColor("black"));
						colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
						t.setBackground(colors.getColor("white"));
					}
					else if(e.getHref().equals("StickDependency")){
						RecordParameters.recordTime++;
						
						SuggestionObject obj = suggestion.getSuggeestionObject();
						RefactoringAction action = suggestion.getAction();
						if(obj instanceof ModuleLinkWrapper && action instanceof DependencyAction){
							LinkAction depAction =(LinkAction)action;
							for(ModuleDependencyConfidence confidence: Settings.dependencyConfidenceTable){
								if(confidence.getModule().getName().equals(depAction.getOrigin().getName())){
									for(int i=0; i<confidence.getModuleList().size(); i++){
										ModuleWrapper calleeModule = confidence.getModuleList().get(i);
										if(calleeModule.getName().equals(depAction.getDestination().getName())){
											confidence.getConfidenceList()[i] += 2;
										}
									}
								}
							}
							ViewUpdater updater = new ViewUpdater();
							updater.updateView(ReflexactoringPerspective.DEPENDENCY_CONSTRAINT_CONFIDENCE_VIEW, Settings.dependencyConfidenceTable, true);
						}
						
						FormText t = (FormText) e.getSource();
						FormColors colors = toolkitPre.getColors();
						colors.createColor("gray", new RGB(207,207,207));
						colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
						t.setBackground(colors.getColor("gray"));
						t.setForeground(colors.getColor("white"));
					}
					else if(e.getHref().equals("UnstickDependency")){
						SuggestionObject obj = suggestion.getSuggeestionObject();
						RefactoringAction action = suggestion.getAction();
						if(obj instanceof ModuleLinkWrapper && action instanceof DependencyAction){
							LinkAction depAction =(LinkAction)action;
							for(ModuleDependencyConfidence confidence: Settings.dependencyConfidenceTable){
								if(confidence.getModule().getName().equals(depAction.getOrigin().getName())){
									for(int i=0; i<confidence.getModuleList().size(); i++){
										ModuleWrapper calleeModule = confidence.getModuleList().get(i);
										if(calleeModule.getName().equals(depAction.getDestination().getName())){
											confidence.getConfidenceList()[i] = 0.5;
										}
									}
								}
							}
							ViewUpdater updater = new ViewUpdater();
							updater.updateView(ReflexactoringPerspective.DEPENDENCY_CONSTRAINT_CONFIDENCE_VIEW, Settings.dependencyConfidenceTable, true);
						}
						FormText t = (FormText) e.getSource();
						FormColors colors = toolkitPre.getColors();
						colors.createColor("black", colors.getSystemColor(SWT.COLOR_BLACK));
						t.setForeground(colors.getColor("black"));
						colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
						t.setBackground(colors.getColor("white"));
					}
					else if(e.getHref().equals("StickExtend")){
						RecordParameters.recordTime++;
						
						SuggestionObject obj = suggestion.getSuggeestionObject();
						RefactoringAction action = suggestion.getAction();
						if(obj instanceof ModuleLinkWrapper && action instanceof ExtendAction){
							LinkAction extAction =(LinkAction)action;
							for(ModuleExtendConfidence confidence: Settings.extendConfidenceTable){
								if(confidence.getModule().getName().equals(extAction.getOrigin().getName())){
									for(int i=0; i<confidence.getModuleList().size(); i++){
										ModuleWrapper parentModule = confidence.getModuleList().get(i);
										if(parentModule.getName().equals(extAction.getDestination().getName())){
											confidence.getConfidenceList()[i] += 2;
										}
									}
								}
							}
							ViewUpdater updater = new ViewUpdater();
							updater.updateView(ReflexactoringPerspective.EXTEND_CONSTRAINT_CONFIDENCE_VIEW, Settings.extendConfidenceTable, true);
						}
						
						FormText t = (FormText) e.getSource();
						FormColors colors = toolkitPre.getColors();
						colors.createColor("gray", new RGB(207,207,207));
						colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
						t.setBackground(colors.getColor("gray"));
						t.setForeground(colors.getColor("white"));
					}
					else if(e.getHref().equals("UnstickExtend")){
						SuggestionObject obj = suggestion.getSuggeestionObject();
						RefactoringAction action = suggestion.getAction();
						if(obj instanceof ModuleLinkWrapper && action instanceof ExtendAction){
							LinkAction extAction =(LinkAction)action;
							for(ModuleExtendConfidence confidence: Settings.extendConfidenceTable){
								if(confidence.getModule().getName().equals(extAction.getOrigin().getName())){
									for(int i=0; i<confidence.getModuleList().size(); i++){
										ModuleWrapper parentModule = confidence.getModuleList().get(i);
										if(parentModule.getName().equals(extAction.getDestination().getName())){
											confidence.getConfidenceList()[i] = 0.5;
										}
									}
								}
							}
							ViewUpdater updater = new ViewUpdater();
							updater.updateView(ReflexactoringPerspective.EXTEND_CONSTRAINT_CONFIDENCE_VIEW, Settings.extendConfidenceTable, true);
						}
						FormText t = (FormText) e.getSource();
						FormColors colors = toolkitPre.getColors();
						colors.createColor("black", colors.getSystemColor(SWT.COLOR_BLACK));
						t.setForeground(colors.getColor("black"));
						colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
						t.setBackground(colors.getColor("white"));
					}
					else if(e.getHref().equals("StickCreation")){
						RecordParameters.recordTime++;
						
						SuggestionObject obj = suggestion.getSuggeestionObject();
						RefactoringAction action = suggestion.getAction();
						if(obj instanceof ModuleLinkWrapper && action instanceof CreationAction){
							LinkAction crtAction =(LinkAction)action;
							for(ModuleCreationConfidence confidence: Settings.creationConfidenceTable){
								if(confidence.getModule().getName().equals(crtAction.getOrigin().getName())){
									for(int i=0; i<confidence.getModuleList().size(); i++){
										ModuleWrapper parentModule = confidence.getModuleList().get(i);
										if(parentModule.getName().equals(crtAction.getDestination().getName())){
											confidence.getConfidenceList()[i] += 2;
										}
									}
								}
							}
							ViewUpdater updater = new ViewUpdater();
							updater.updateView(ReflexactoringPerspective.CREATION_CONSTRAINT_CONFIDENCE_VIEW, Settings.creationConfidenceTable, true);
						}
						
						FormText t = (FormText) e.getSource();
						FormColors colors = toolkitPre.getColors();
						colors.createColor("gray", new RGB(207,207,207));
						colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
						t.setBackground(colors.getColor("gray"));
						t.setForeground(colors.getColor("white"));
					}
					else if(e.getHref().equals("UnstickCreation")){
						SuggestionObject obj = suggestion.getSuggeestionObject();
						RefactoringAction action = suggestion.getAction();
						if(obj instanceof ModuleLinkWrapper && action instanceof CreationAction){
							LinkAction crtAction =(LinkAction)action;
							for(ModuleCreationConfidence confidence: Settings.creationConfidenceTable){
								if(confidence.getModule().getName().equals(crtAction.getOrigin().getName())){
									for(int i=0; i<confidence.getModuleList().size(); i++){
										ModuleWrapper parentModule = confidence.getModuleList().get(i);
										if(parentModule.getName().equals(crtAction.getDestination().getName())){
											confidence.getConfidenceList()[i] = 0.5;
										}
									}
								}
							}
							ViewUpdater updater = new ViewUpdater();
							updater.updateView(ReflexactoringPerspective.CREATION_CONSTRAINT_CONFIDENCE_VIEW, Settings.creationConfidenceTable, true);
						}
						FormText t = (FormText) e.getSource();
						FormColors colors = toolkitPre.getColors();
						colors.createColor("black", colors.getSystemColor(SWT.COLOR_BLACK));
						t.setForeground(colors.getColor("black"));
						colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
						t.setBackground(colors.getColor("white"));
					}
				}
			});
		}
	}

	@Override
	public void setFocus() {

	}
	
	protected CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); 
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit); // set source
		parser.setResolveBindings(true); // we need bindings later on
		return (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
	}
}
