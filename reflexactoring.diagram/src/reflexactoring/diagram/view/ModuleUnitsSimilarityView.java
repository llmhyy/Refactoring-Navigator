package reflexactoring.diagram.view;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import reflexactoring.diagram.bean.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.ModuleUnitsSimilarity;
import reflexactoring.diagram.bean.ModuleUnitsSimilarityTable;
import reflexactoring.diagram.util.Settings;

public class ModuleUnitsSimilarityView extends ViewPart {

	private TableViewer tableViewer;
	private Composite composite;
	
	public ModuleUnitsSimilarityView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		
		this.composite = parent;
		
		if(Settings.similarityTable != null && Settings.similarityTable.size() > 0){
			refreshUI(Settings.similarityTable);
		}
	}
	
	public void refreshUI(ModuleUnitsSimilarityTable similarityTable){
		
		for(Control control: this.composite.getChildren()){
			control.dispose();
		}
		
		tableViewer = new TableViewer(this.composite, SWT.MULTI | SWT.H_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);
		
		createColumns(this.composite, tableViewer);
		
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(similarityTable);

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);
		
		this.composite.redraw();
		this.composite.layout();
	}
	
	private void createColumns(final Composite parent, final TableViewer viewer) {
		
		String[] titles = new String[Settings.similarityTable.getColumnNumber()+1];
		int[] bounds = new int[Settings.similarityTable.getColumnNumber()+1];
		titles[0] = "Module";
		bounds[0] = 100;
		ModuleUnitsSimilarity moduleSimilarity = Settings.similarityTable.get(0);
		for(int i=1; i<titles.length; i++){
			titles[i] = moduleSimilarity.getUnits().get(i-1).getSimpleName();
			bounds[i] = 200;
		}
		
		/**
		 * create column for describing module.
		 */
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ModuleUnitsSimilarity moduleSimilarity = (ModuleUnitsSimilarity)element;
				return moduleSimilarity.getModule().getName();
			}
		});

		/**
		 * create column for describing types.
		 */
		for(int i=1; i<titles.length; i++){
			final int index = i;
			col = createTableViewerColumn(titles[i], bounds[i], i);
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					ModuleUnitsSimilarity moduleSimilarity = (ModuleUnitsSimilarity)element;
					return String.valueOf(moduleSimilarity.getValues()[index-1]);
				}
			});
		}
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
		// TODO Auto-generated method stub

	}

	/**
	 * @return the composite
	 */
	public Composite getComposite() {
		return composite;
	}

}
