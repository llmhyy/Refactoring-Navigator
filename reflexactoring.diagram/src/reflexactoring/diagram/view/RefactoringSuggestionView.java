package reflexactoring.diagram.view;

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import reflexactoring.Activator;
import reflexactoring.diagram.action.recommend.Suggestion;
import reflexactoring.diagram.util.Settings;

public class RefactoringSuggestionView extends ViewPart {

	private FormToolkit toolkit;
	private ScrolledForm form;
	private FormText text; 
	
	private static final String initialDesc = "Refactoring with Fun! ^_^";
	private static final String refactoringDesc = "The following are suggestions to improve the consistency:";
	
	
	
	public RefactoringSuggestionView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText("Suggestions");
		form.getBody().setLayout(new TableWrapLayout());
		text = toolkit.createFormText(form.getBody(), true);
		text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		text.setText("<form>" + initialDesc +"</form>", true, false);
		
		hookActionsOnToolBar();
	}
	
	public void refreshSuggestionsOnUI(ArrayList<Suggestion> suggestions){
		
		
		if(suggestions == null){
			return;
		}
		
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

	@SuppressWarnings("restriction")
	private void hookActionsOnToolBar(){
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		
		Action skipUnmappedTypeAction = new Action("Ignore Unmapped Types", IAction.AS_CHECK_BOX){
			public void run(){
				if(this.isChecked()){
					Settings.isSkipUnMappedTypes = true;
				}
				else{
					Settings.isSkipUnMappedTypes = false;
				}
			}
		};
		
		skipUnmappedTypeAction.setImageDescriptor(JavaPluginImages.DESC_OBJS_NLS_SKIP);
		
		toolBar.add(skipUnmappedTypeAction);
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
