package reflexactoring.diagram.view;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
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
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import reflexactoring.diagram.action.recommend.SuggestionMove;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequenceElement;

public class RefactoringSuggestionsView extends ViewPart {
	private Composite parent;
	
	GridLayout gridLayout = new GridLayout();
	
	public RefactoringSuggestionsView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		parent.setLayout(gridLayout);
	}
	
	public void refreshSuggestionsOnUI(ArrayList<RefactoringSequence> suggestions){
		for(Control control: parent.getChildren()){
			control.dispose();
		}
		
		//TODO
		Label text = new Label(parent, SWT.WRAP | SWT.TOP);
		text.setText("Refactoring Suggestions");
		text.setFont(new Font(Display.getCurrent(), "Arial", 14, SWT.BOLD));
		text.setForeground(new Color(Display.getCurrent(), 38, 81, 128));
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
			prerequisiteComposite.setBackground(new Color(Display.getCurrent(), 206, 237, 255));
			//Label for "Prerequisite"
			Label prerequisiteTitle = new Label(prerequisiteComposite, SWT.WRAP | SWT.TOP);
			prerequisiteTitle.setText("Prerequisite");
			prerequisiteTitle.setFont(new Font(Display.getCurrent(), "Arial", 14, SWT.NORMAL));
			prerequisiteTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			prerequisiteTitle.setForeground(new Color(Display.getCurrent(), 38, 81, 128));
			prerequisiteTitle.setBackground(new Color(Display.getCurrent(), 206, 237, 255));
			//form for prerequisite actions
			FormToolkit toolkitPre = new FormToolkit(parent.getDisplay());
			Composite formCompositePre = toolkitPre.createComposite(prerequisiteComposite);
			formCompositePre.setLayout(new TableWrapLayout());
			formCompositePre.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			FormText formTextPre = toolkitPre.createFormText(formCompositePre, true);
			formTextPre.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
			
			StringBuffer bufferPre = new StringBuffer();
			bufferPre.append("<form>");
			for(SuggestionMove prerequisite : sequence.getPrerequisite()){
				bufferPre.append("<li>");
				bufferPre.append(prerequisite.generateTagedText());
				bufferPre.append("</li>");	
				
				bufferPre.append("<li bindent=\"20\">");
				bufferPre.append("<b>[</b> <a href=\"Forbid\">Reject</a> ");
				bufferPre.append("<a href=\"Allow\">Undo</a> ");
				bufferPre.append("<b>]</b>");
				bufferPre.append("</li>");	
				bufferPre.append("<li bindent=\"20\">");
				bufferPre.append("<b>[</b> <a href=\"Exec\">Apply</a> ");
				bufferPre.append("<a href=\"Undo\">Undo</a> ");
				bufferPre.append("<b>]</b>");
				bufferPre.append("</li>");
			}			
			bufferPre.append("</form>");
			
			formTextPre.setText(bufferPre.toString(), true, false);
			//formTextPre.setData(move);
			formTextPre.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
//					SuggestionMove suggestion = (SuggestionMove) text.getData();
//					if(e.getHref().equals("Module")){
//						
//					}
				}
			});
			
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
				detailBar.setForeground(new Color(Display.getCurrent(), 38, 81, 128));
				detailBar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

				//Composite for details
				final Composite detailComposite = new Composite (detailBar, SWT.NONE);
				detailComposite.setLayout(gridLayout);
				detailComposite.setBackground(new Color(Display.getCurrent(), 206, 237, 255));
				
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
				
				//generate detail?
				for(int i = 0; i < element.getOpportunity().getRefactoringDetails().size(); i++){
					String detail = element.getOpportunity().getRefactoringDetails().get(i);
					Label testDetail = new Label(detailComposite, SWT.WRAP | SWT.TOP);
					testDetail.setText((i + 1) + ". " + detail +".");
					testDetail.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
					testDetail.setBackground(new Color(Display.getCurrent(), 206, 237, 255));
				}				
				ExpandItem detailItem = new ExpandItem (detailBar, SWT.NONE, 0);
				detailItem.setText("View Detail");
				//not able to get the actual height of detailComposite, only know the height difference between parent and the composite is about 65, use it to calculate the height...
				detailItem.setHeight(detailComposite.computeSize(parent.getBounds().width - 65, SWT.DEFAULT).y);
				//System.out.println(detailItem.getHeight() + " : " + detailComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + " : " + detailComposite.computeSize(parent.getBounds().width - 65, SWT.DEFAULT).y);
				detailItem.setControl(detailComposite);
				
				//Form for actions				
				FormToolkit toolkit = new FormToolkit(parent.getDisplay());
				Composite formComposite = toolkit.createComposite(elementComposite);
				formComposite.setLayout(new TableWrapLayout());
				formComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
				FormText formText = toolkit.createFormText(formComposite, true);
				formText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
				
				StringBuffer buffer = new StringBuffer();
				buffer.append("<form>");
				buffer.append("<li>");
				buffer.append("<b>[</b>");
				buffer.append(" <a href=\"Simulate\">Simulate</a> ");	
				buffer.append("<a href=\"UndoSimulate\">Undo</a> ");
				buffer.append("<b>]</b>");
				buffer.append("</li>");	
				buffer.append("<li>");
				buffer.append("<b>[</b> <a href=\"Forbid\">Reject</a> ");
				buffer.append("<a href=\"Allow\">Undo</a> ");
				buffer.append("<b>]</b>");
				buffer.append("</li>");	
				buffer.append("<li>");
				buffer.append("<b>[</b> <a href=\"Exec\">Apply</a> ");
				buffer.append("<a href=\"Undo\">Undo</a> ");
				buffer.append("<b>]</b>");
				buffer.append("</li>");
				buffer.append("</form>");
				
				formText.setText(buffer.toString(), true, false);
				//formText.setData(move);
				formText.addHyperlinkListener(new HyperlinkAdapter() {
					public void linkActivated(HyperlinkEvent e) {
//						SuggestionMove suggestion = (SuggestionMove) text.getData();
//						if(e.getHref().equals("Module")){
//							
//						}
					}
				});
						

				//test disable
				if((sequence.indexOf(element) + 1) >= 2){
					detailBar.setBackground(new Color(Display.getCurrent(), 240, 240, 240));
					formComposite.setBackground(new Color(Display.getCurrent(), 240, 240, 240));
					formText.setBackground(new Color(Display.getCurrent(), 240, 240, 240));
					//change hyper link color not work...
//					HyperlinkSettings grayLink = new HyperlinkSettings(Display.getCurrent());
//					grayLink.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_NEVER);
//					grayLink.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
//					grayLink.setActiveForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
//					formText.setHyperlinkSettings(grayLink);
					formText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
					elementComposite.setBackground(new Color(Display.getCurrent(), 240, 240, 240));
					elementComposite.setEnabled(false);
				}
				elementComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
				//elementComposite.setEnabled(true);
			}
			
			scrollComposite.setContent(composite);
			scrollComposite.setExpandVertical(true);
			scrollComposite.setExpandHorizontal(true);
			scrollComposite.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					Rectangle r = scrollComposite.getClientArea();
					scrollComposite.setMinSize(composite.computeSize(r.width, SWT.DEFAULT));
				}
			});
		}
		
		parent.layout();
	}

	@Override
	public void setFocus() {

	}

}
