package reflexactoring.diagram.view;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import reflexactoring.diagram.action.popup.ReferenceDetailMap;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.util.JavaCodeUtil;
import reflexactoring.diagram.util.RecordParameters;

public class ReferenceDetailView extends ViewPart {

	private TreeViewer viewer;
	//private ICompilationUnitWrapper callerCompilationUnit;
	
	public ReferenceDetailView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new DetailContentProvider());
		viewer.setLabelProvider(new DetailLabelProvider(null));
		
		viewer.getTree().addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent me) {
				RecordParameters.referenceCheck++;
				
				Tree tree = (Tree)me.getSource();
				TreeItem item = tree.getItem(new Point(me.x, me.y));
				ASTNode node = (ASTNode) item.getData();
				ASTNode parent = node.getParent();
				while(parent.getParent() != null){
					parent = parent.getParent();
				}
				CompilationUnit cu = (CompilationUnit)parent;
				int lineNumber = cu.getLineNumber(node.getStartPosition());
				
				ICompilationUnit unit = (ICompilationUnit) cu.getJavaElement();
				IEditorPart javaEditor;
				try {
					javaEditor = JavaUI.openInEditor(unit);
					JavaUI.revealInEditor(javaEditor,
							(IJavaElement) unit);
					JavaCodeUtil.goToLine(javaEditor, lineNumber, lineNumber+1);
				} catch (PartInitException e) {
					e.printStackTrace();
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the viewer
	 */
	public TreeViewer getViewer() {
		return viewer;
	}
	
	/**
	 * @return the callerCompilationUnit
	 *//*
	public ICompilationUnitWrapper getCallerCompilationUnit() {
		return callerCompilationUnit;
	}

	*//**
	 * @param callerCompilationUnit the callerCompilationUnit to set
	 *//*
	public void setCallerCompilationUnit(ICompilationUnitWrapper callerCompilationUnit) {
		this.callerCompilationUnit = callerCompilationUnit;
	}*/

	public class DetailContentProvider implements ITreeContentProvider{
		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof ReferenceDetailMap){
				ReferenceDetailMap map = (ReferenceDetailMap)parentElement;
				ArrayList<ASTNode> list = map.getReferences();
				return list.toArray(new ASTNode[0]);
			}
			
			return null;
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if(element instanceof ReferenceDetailMap){
				return true;
			}
			
			return false;
		}
		
	}
	
	public class DetailLabelProvider implements ILabelProvider{

		private ReferenceDetailMap map;
		
		public DetailLabelProvider(ReferenceDetailMap map){
			this.map = map;
		}
		
		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element) {
			if(element instanceof ReferenceDetailMap){
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);		
			}
			else if(element instanceof ASTNode){
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);				
			}
			
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if(element instanceof ReferenceDetailMap){
				ReferenceDetailMap map = (ReferenceDetailMap)element;
				return "The references to " + map.getCalleeUnit().getSimpleName();
			}
			else if(element instanceof ASTNode){
				ASTNode node = (ASTNode)element;
				
				ICompilationUnitWrapper callerUnitWrapper = this.map.getCallerUnit();
				CompilationUnit unit = callerUnitWrapper.getJavaUnit();
				
				
				String source;
				try {
					source = callerUnitWrapper.getCompilationUnit().getSource();
					int lineNumber = unit.getLineNumber(node.getStartPosition());
					int startPosition = unit.getPosition(lineNumber, 0);
					int endPosition = unit.getPosition(lineNumber+1, 0)-1;
					
					String content = source.substring(startPosition, endPosition);
					content = content.trim();
					
					if(content.length() > 50){
						content = content.substring(0, 50);
					}
					
					return "In line " + lineNumber + " of " + callerUnitWrapper.getName() + ": " + content;
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				
				
				
			}
			
			return null;
		}
		
	}

}
