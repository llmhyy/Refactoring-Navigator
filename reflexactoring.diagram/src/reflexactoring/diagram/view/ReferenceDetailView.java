package reflexactoring.diagram.view;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import reflexactoring.diagram.action.popup.ReferenceDetailMap;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.util.RefactoringOppUtil;
import reflexactoring.diagram.util.RecordParameters;
import reflexactoring.diagram.view.annotation.ReferenceAnnotation;

public class ReferenceDetailView extends ViewPart {

	private TreeViewer viewer;
	
	public ReferenceDetailView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new DetailContentProvider());
		viewer.setLabelProvider(new DetailLabelProvider(null));
		
		viewer.getTree().addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				
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
				ITextEditor sourceEditor;
				try {
					sourceEditor = (ITextEditor)JavaUI.openInEditor(cu.getJavaElement());
					sourceEditor.setHighlightRange(node.getStartPosition(), node.getLength(), true);
				} catch (PartInitException | JavaModelException e) {
					e.printStackTrace();
				}
				
				/*ITextEditor sourceEditor;
				try {
					sourceEditor = (ITextEditor)JavaUI.openInEditor(cu.getJavaElement());
					AnnotationModel annotationModel = (AnnotationModel)sourceEditor.getDocumentProvider().getAnnotationModel(sourceEditor.getEditorInput());
					@SuppressWarnings("unchecked")
					Iterator<Annotation> annotationIterator = annotationModel.getAnnotationIterator();
					while(annotationIterator.hasNext()) {
						Annotation currentAnnotation = annotationIterator.next();
						annotationModel.removeAnnotation(currentAnnotation);
					}
					ReferenceAnnotation annotation = new ReferenceAnnotation(false, "You may need to change this part");
					Position position = new Position(node.getStartPosition(), node.getLength());
					
					annotationModel.addAnnotation(annotation, position);
					sourceEditor.setHighlightRange(node.getStartPosition(), node.getLength(), true);
				} catch (PartInitException e) {
					e.printStackTrace();
				} catch (JavaModelException e) {
					e.printStackTrace();
				}*/
				
				/*int lineNumber = cu.getLineNumber(node.getStartPosition());
				
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
				}*/
			}
		});
	}

	@Override
	public void setFocus() {
		
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
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
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
			
		}

		@Override
		public void dispose() {
			
		}

		
		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		
		@Override
		public void removeListener(ILabelProviderListener listener) {
			
		}

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

		@Override
		public String getText(Object element) {
			if(element instanceof ReferenceDetailMap){
				//ReferenceDetailMap map = (ReferenceDetailMap)element;
				return "The references:" /*+ map.getCallerUnit().getSimpleName()*/;
			}
			else if(element instanceof ASTNode){
				ASTNode node0 = (ASTNode)element;
				
				ICompilationUnit iunit = (ICompilationUnit) ((CompilationUnit) node0.getRoot()).getJavaElement();
				CompilationUnit unit = RefactoringOppUtil.parse(iunit);
				ASTNode node = RefactoringOppUtil.findCorrespondingNode(unit, node0);
				
				//ICompilationUnitWrapper callerUnitWrapper = this.map.getCallerUnit();
				//CompilationUnit unit = callerUnitWrapper.getJavaUnit();
				//CompilationUnit unit = (CompilationUnit)node.getRoot();
				//ICompilationUnit iunit = (ICompilationUnit)unit.getJavaElement();
				
				String source;
				try {
					source = iunit.getSource();
					int lineNumber = unit.getLineNumber(node.getStartPosition());
					int startPosition = unit.getPosition(lineNumber, 0);
					int endPosition = unit.getPosition(lineNumber+1, 0)-1;
					
					String content = source.substring(startPosition, endPosition);
					content = content.trim();
					
					if(content.length() > 50){
						content = content.substring(0, 50);
					}
					
					return "In line " + lineNumber + " of " + iunit.getElementName() + ": " + content;
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				
				
				
			}
			
			return null;
		}
		
	}

}
