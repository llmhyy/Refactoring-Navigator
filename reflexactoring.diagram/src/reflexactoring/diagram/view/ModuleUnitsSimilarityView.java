package reflexactoring.diagram.view;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import reflexactoring.diagram.bean.heuristics.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.heuristics.ModuleUnitsSimilarity;
import reflexactoring.diagram.bean.heuristics.ModuleUnitsSimilarityTable;
import reflexactoring.diagram.util.ReflexactoringUtil;
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

		if (Settings.similarityTable != null
				&& Settings.similarityTable.size() > 0) {
			refreshUI(Settings.similarityTable);
		}
	}

	public void refreshUI(ModuleUnitsSimilarityTable similarityTable) {

		for (Control control : this.composite.getChildren()) {
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

		Menu pop = new Menu(this.composite.getShell(), SWT.POP_UP);
		MenuItem item = new MenuItem(pop, SWT.PUSH);
		item.setText("Refresh");
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setInput(Settings.similarityTable);;
				tableViewer.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
			
		});
		tableViewer.getTable().setMenu(pop); 
		
		this.composite.redraw();
		this.composite.layout();
	}

	private void createColumns(final Composite parent, final TableViewer viewer) {

		String[] titles = new String[Settings.similarityTable.getColumnNumber() + 1];
		int[] bounds = new int[Settings.similarityTable.getColumnNumber() + 1];
		titles[0] = "Module";
		bounds[0] = 100;
		ModuleUnitsSimilarity moduleSimilarity = Settings.similarityTable
				.get(0);
		for (int i = 1; i < titles.length; i++) {
			titles[i] = moduleSimilarity.getUnits().get(i - 1).getSimpleName();
			bounds[i] = 150;
		}

		/**
		 * create column for describing module.
		 */
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ModuleUnitsSimilarity moduleSimilarity = (ModuleUnitsSimilarity) element;
				return moduleSimilarity.getModule().getName();
			}
		});

		/**
		 * create column for describing types.
		 */
		for (int i = 1; i < titles.length; i++) {
			final int index = i;
			col = createTableViewerColumn(titles[i], bounds[i], i);
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					ModuleUnitsSimilarity moduleSimilarity = (ModuleUnitsSimilarity) element;
					return String.valueOf(moduleSimilarity.getValues()[index - 1]);
				}
				
				@Override
				public void update(ViewerCell cell) {
					super.update(cell);
					if(Double.valueOf(cell.getText()) >= Double.valueOf(ReflexactoringUtil.getMappingThreshold())){
						cell.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
						cell.setFont(new Font(Display.getCurrent(), "Arial", 12, SWT.BOLD));
					}
					
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

		viewerColumn.setEditingSupport(new SimilarityTableEditingSupport(
				tableViewer, colNumber));

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

	public class SimilarityTableEditingSupport extends EditingSupport {
		private CellEditor editor;
		private TableViewer viewer;
		private int columnNo;

		public SimilarityTableEditingSupport(TableViewer viewer, int columnNo) {
			super(viewer);
			this.columnNo = columnNo;
			this.viewer = viewer;
			this.editor = new TextCellEditor(viewer.getTable());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.
		 * Object)
		 */
		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/**
		 * if column number is 0, it stands for the name of module which cannot
		 * be edited.
		 */
		@Override
		protected boolean canEdit(Object element) {
			return columnNo != 0;
		}

		@Override
		protected Object getValue(Object element) {
			if (element instanceof ModuleUnitsSimilarity) {
				ModuleUnitsSimilarity moduleUnitsSimilarity = (ModuleUnitsSimilarity) element;

				if (0 == columnNo) {
					return moduleUnitsSimilarity.getModule().getName();
				} else {
					return String
							.valueOf(moduleUnitsSimilarity.getValues()[columnNo - 1]);
				}
			}

			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (element instanceof ModuleUnitsSimilarity) {
				ModuleUnitsSimilarity moduleUnitsSimilarity = (ModuleUnitsSimilarity) element;

				if (columnNo > 0) {
					/**
					 * need a check for string value.
					 */
					String numberStr = (String) value;
					if (ReflexactoringUtil.checkNumber(numberStr)) {
						double similarity = Double.valueOf(numberStr);
						moduleUnitsSimilarity.getValues()[columnNo - 1] = similarity;
						viewer.update(element, null);
					}
					viewer.refresh();
				}
			}
		}

	}
}
