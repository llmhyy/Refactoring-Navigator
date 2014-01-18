package reflexactoring.diagram.view;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import reflexactoring.diagram.action.recommend.Suggestion;

public class RefactoringSuggestionView extends ViewPart {

	private FormToolkit toolkit;
	private ScrolledForm form;
	private FormText text; 
	private Composite parent;
	
	private static final String initialDesc = "Refactoring with Fun! ^_^";
	private static final String refactoringDesc = "The following are suggestions to improve the consistency:";
	
	
	
	public RefactoringSuggestionView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		
		this.parent = parent;
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText("Suggestions");
		form.getBody().setLayout(new TableWrapLayout());
		text = toolkit.createFormText(form.getBody(), true);
		text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		text.setText("<form>" + initialDesc +"<p></p><p></p><p></p><p></p><p></p><p></p></form>", true, false);
	}
	
	public void refreshSuggestionsOnUI(ArrayList<Suggestion> suggestions){
		
		
		String content = generateCSS(suggestions);
		
		text.setText(content, true, false);
		text.getParent().layout();
		text.getParent().redraw();
		//form.getBody().layout();
		//form.getBody().redraw();
	}
	
	private String generateCSS(ArrayList<Suggestion> list){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>");
		buffer.append("<p>"+ refactoringDesc /*+ "hello "*/ + "</p>");
		for(Suggestion suggestion: list){
			
			buffer.append("<li>");
			buffer.append(suggestion.toString());
			buffer.append("</li>");
			
		}
		
		buffer.append("</form>");
		
		return buffer.toString();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
