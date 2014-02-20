package reflexactoring.diagram.view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import reflexactoring.diagram.bean.HeuristicModuleMemberStopMap;
import reflexactoring.diagram.bean.HeuristicModuleMemberStopMapList;
import reflexactoring.diagram.util.Settings;

public class ForbiddenView extends ViewPart {

	private Text searchText;
	private TableViewer tableViewer;
	
	public ForbiddenView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {

		GridLayout gridLayout = new GridLayout(2, false);
		parent.setLayout(gridLayout);

		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Search: ");
		searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));

		searchText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
						
			}  
		    @Override  
		    public void keyReleased(KeyEvent e) {
		    	if (e.keyCode == 13) {  
		    		String searchString = searchText.getText().toLowerCase();
		    		if(searchString == null || searchString.trim().equals("")){
		    			tableViewer.setInput(Settings.heuristicStopMapList);
						tableViewer.refresh();
		    		}else{
		    			HeuristicModuleMemberStopMapList filteredHeuristicStopMapList
		    			= new HeuristicModuleMemberStopMapList();
		    			for(HeuristicModuleMemberStopMap map: Settings.heuristicStopMapList){
		    				if(map.getMember().getName().toLowerCase().indexOf(searchString) != -1 ||
		    						map.getModule().getName().toLowerCase().indexOf(searchString) != -1){
			    				filteredHeuristicStopMapList.add(map);		    					
		    				}
		    			}
		    			tableViewer.setInput(filteredHeuristicStopMapList);
						tableViewer.refresh();
		    		}
		    	}  
		    }
		});
		
		createTableViewer(parent);
		
		hookActionsOnBars();
	}

	private void createTableViewer(Composite parent) {
		tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);
		
		createColumns(parent, tableViewer);
		
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(Settings.heuristicStopMapList);

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);
		
		//Add context menu for DELETE action
		Menu pop = new Menu(parent.getShell(), SWT.POP_UP);
		MenuItem item = new MenuItem(pop, SWT.PUSH);
		item.setText("Delete");
		item.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Object o = e.getSource();
				if (o instanceof MenuItem) {
					TableItem[] ti = tableViewer.getTable().getSelection();
					if (ti != null && ti.length > 0) { 
						for(int i = 0; i < ti.length; i++){
							Settings.heuristicStopMapList.removeMap((HeuristicModuleMemberStopMap) ti[i].getData());							
						}						
						tableViewer.setInput(Settings.heuristicStopMapList);
						tableViewer.refresh();
					}else{
						
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			} 
		}); 
		tableViewer.getTable().setMenu(pop); 
		
	}

	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Module", "Member",};
		int[] bounds = { 200, 500 };

		/**
		 * create column for describing module.
		 */
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				HeuristicModuleMemberStopMap map = (HeuristicModuleMemberStopMap)element;
				return map.getModule().getName();
			}
		});

		/**
		 * create column for describing type.
		 */
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				HeuristicModuleMemberStopMap map = (HeuristicModuleMemberStopMap)element;
				return map.getMember().getUnitWrapper().getFullQualifiedName()+"."+map.getMember();
			}
		});

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound,
			final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(
				this.tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	public TableViewer getViewer() {
		return this.tableViewer;
	}

	@Override
	public void setFocus() {
		

	}
	
	private void hookActionsOnBars(){
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		
		Action createMapAction = new Action("Stop a heuristic map") {
			public void run(){
				//TODO
//				StopMappingDialog dialog = new StopMappingDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
//				dialog.create();
//				if(dialog.open() == Window.OK){
//					HeuristicModuleMemberStopMap map = dialog.getHeuristicModuleMemberStopMap();
//					if(map != null){
//						Settings.heuristicStopMapList.add(map);
//						tableViewer.setInput(Settings.heuristicStopMapList);
//						tableViewer.refresh();
//					}
//				}
			}
		};
		createMapAction.setImageDescriptor(
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().
						getSharedImages().getImage(ISharedImages.IMG_TOOL_UP_HOVER))
		);
		
		toolBar.add(createMapAction);
	}

}
