package reflexactoring.diagram.view;

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramGraphicalViewer;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import reflexactoring.Activator;
import reflexactoring.Module;
import reflexactoring.diagram.action.recommend.Suggestion;
import reflexactoring.diagram.action.recommend.action.MoveMemberAction;
import reflexactoring.diagram.action.recommend.action.MoveTypeAction;
import reflexactoring.diagram.action.recommend.action.RefactoringAction;
import reflexactoring.diagram.action.recommend.gencode.JavaClassCreator;
import reflexactoring.diagram.bean.FieldWrapper;
import reflexactoring.diagram.bean.HeuristicModuleMemberStopMap;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.SuggestionObject;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.edit.parts.ReflexactoringEditPart;
import reflexactoring.diagram.part.ReflexactoringDiagramEditor;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.util.GEFDiagramUtil;
import reflexactoring.diagram.util.ReflexactoringUtil;
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
		
		//hookActionsOnToolBar();
	}
	
	public void refreshSuggestionsOnUI(ArrayList<Suggestion> suggestions){
		for(Control control: form.getBody().getChildren()){
			control.dispose();
		}
		
		FormText headerText = toolkit.createFormText(form.getBody(), true);
		headerText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		headerText.setText(refactoringDesc, false, false);
		
		
		if(suggestions == null){
			return;
		}
		
		generateSuggestionsOnUI(suggestions);
		
		form.reflow(false);
	}
	
	private void generateSuggestionsOnUI(ArrayList<Suggestion> list){
		for(Suggestion suggestion: list){
			final FormText text = toolkit.createFormText(form.getBody(), true);
			text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("<form>");
			buffer.append("<li>");
			buffer.append(suggestion.generateTagedText());
			buffer.append("<b>[</b><a href=\"Exec\">Execute</a> ");
			buffer.append("<a href=\"Undo\">Undo</a> ");
			buffer.append("<b>]</b>");
			if(suggestion.getAction() instanceof MoveMemberAction){
				buffer.append("<b>[</b>");
				buffer.append("<a href=\"Forbid\">Forbid</a> ");	
				buffer.append("<a href=\"Allow\">Allow</a>");	
				buffer.append("<b>]</b>");
			}
			buffer.append("</li>");			
			
			buffer.append("</form>");
			
			text.setText(buffer.toString(), true, false);
			text.setData(suggestion);
			text.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
					Suggestion suggestion = (Suggestion) text.getData();
					if(e.getHref().equals("Module")){
						RefactoringAction action = suggestion.getAction();
						if(action instanceof MoveTypeAction){
							MoveTypeAction moveTypeAction = (MoveTypeAction)action;
							ModuleWrapper sourceModule = moveTypeAction.getOrigin();
							ModuleWrapper targetModule = moveTypeAction.getDestination();
							
							if(sourceModule.getName().equals(e.getLabel()) || targetModule.getName().equals(e.getLabel())){
								GEFDiagramUtil.focusModuleOnGraph(e.getLabel());
							}
						}
					}
					else if(e.getHref().equals("Type")){
						SuggestionObject obj = suggestion.getSuggeestionObject();
						if(obj instanceof ICompilationUnitWrapper){
							ICompilationUnitWrapper unitWrapper = (ICompilationUnitWrapper)obj;
							//unitWrapper.openInEditor();
							GEFDiagramUtil.focusTypeOnGraph(unitWrapper.getFullQualifiedName());
						}
						else if(obj instanceof UnitMemberWrapper){
							UnitMemberWrapper memberWrapper = (UnitMemberWrapper)obj;
							memberWrapper.getUnitWrapper().openInEditor();
						}
					}
					else if(e.getHref().equals("Method")){
						SuggestionObject obj = suggestion.getSuggeestionObject();
						if(obj instanceof MethodWrapper){
							MethodWrapper methodWrapper = (MethodWrapper)obj;
							methodWrapper.openInEditor();
						}
						
					}
					else if(e.getHref().equals("Field")){
						SuggestionObject obj = suggestion.getSuggeestionObject();
						if(obj instanceof FieldWrapper){
							FieldWrapper fieldWrapper = (FieldWrapper)obj;
							fieldWrapper.openInEditor();
						}
					}
					else if(e.getHref().equals("Forbid")){
						SuggestionObject obj = suggestion.getSuggeestionObject();
						RefactoringAction action = suggestion.getAction();
						if(obj instanceof UnitMemberWrapper && action instanceof MoveMemberAction){
							UnitMemberWrapper memberWrapper = (UnitMemberWrapper)obj;
							MoveMemberAction moveMemberAction = (MoveMemberAction)action;
							HeuristicModuleMemberStopMap stopMap = new HeuristicModuleMemberStopMap(moveMemberAction.getDestination(), memberWrapper);
							
							Settings.heuristicStopMapList.add(stopMap);
							ViewUpdater updater = new ViewUpdater();
							updater.updateView(ReflexactoringPerspective.FORBIDDEN_VIEW, Settings.heuristicStopMapList, false);
							/*ForbiddenView view = (ForbiddenView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
									getActivePage().findView(ReflexactoringPerspective.FORBIDDEN_VIEW);
							view.getViewer().setInput(Settings.heuristicStopMapList);
							view.getViewer().refresh();*/
						}
						FormText t = (FormText) e.getSource();
						FormColors colors = toolkit.getColors();
						colors.createColor("gray", new RGB(207,207,207));
						colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
						t.setBackground(colors.getColor("gray"));
						t.setForeground(colors.getColor("white"));
					}
					else if(e.getHref().equals("Exec")){
						suggestion.apply();
						FormText t = (FormText) e.getSource();
						FormColors colors = toolkit.getColors();
						colors.createColor("green", new RGB(154,205,50));
						t.setForeground(colors.getColor("green"));
						colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
						t.setBackground(colors.getColor("white"));
					}
					else if(e.getHref().equals("Undo")){
						suggestion.undoApply();
						FormText t = (FormText) e.getSource();
						FormColors colors = toolkit.getColors();
						colors.createColor("black", colors.getSystemColor(SWT.COLOR_BLACK));
						t.setForeground(colors.getColor("black"));
						colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
						t.setBackground(colors.getColor("white"));
					}
					else if(e.getHref().equals("Allow")){
						SuggestionObject obj = suggestion.getSuggeestionObject();
						RefactoringAction action = suggestion.getAction();
						if(obj instanceof UnitMemberWrapper && action instanceof MoveMemberAction){
							UnitMemberWrapper memberWrapper = (UnitMemberWrapper)obj;
							MoveMemberAction moveMemberAction = (MoveMemberAction)action;
							HeuristicModuleMemberStopMap stopMap = new HeuristicModuleMemberStopMap(moveMemberAction.getDestination(), memberWrapper);
							
							Settings.heuristicStopMapList.removeMap(stopMap);
							ForbiddenView view = (ForbiddenView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
									getActivePage().findView(ReflexactoringPerspective.FORBIDDEN_VIEW);
							view.getViewer().setInput(Settings.heuristicStopMapList);
							view.getViewer().refresh();
						}
						FormText t = (FormText) e.getSource();
						FormColors colors = toolkit.getColors();
						colors.createColor("black", colors.getSystemColor(SWT.COLOR_BLACK));
						t.setForeground(colors.getColor("black"));
						colors.createColor("white", colors.getSystemColor(SWT.COLOR_WHITE));
						t.setBackground(colors.getColor("white"));
					}
				}
			});
			text.getParent().layout();
			text.getParent().redraw();
		}
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
