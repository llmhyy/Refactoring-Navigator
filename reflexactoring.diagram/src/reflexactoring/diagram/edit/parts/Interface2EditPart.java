package reflexactoring.diagram.edit.parts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.ConstrainedToolbarLayout;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Color;

import reflexactoring.diagram.edit.policies.Interface2ItemSemanticEditPolicy;
import reflexactoring.diagram.part.ReflexactoringVisualIDRegistry;
import reflexactoring.diagram.providers.ReflexactoringElementTypes;

/**
 * @generated
 */
public class Interface2EditPart extends ShapeNodeEditPart {

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 3002;

	/**
	 * @generated
	 */
	protected IFigure contentPane;

	/**
	 * @generated
	 */
	protected IFigure primaryShape;

	/**
	 * @generated
	 */
	public Interface2EditPart(View view) {
		super(view);
	}

	/**
	 * @generated
	 */
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE,
				new Interface2ItemSemanticEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, createLayoutEditPolicy());
		// XXX need an SCR to runtime to have another abstract superclass that would let children add reasonable editpolicies
		// removeEditPolicy(org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles.CONNECTION_HANDLES_ROLE);
	}

	/**
	 * @generated
	 */
	protected LayoutEditPolicy createLayoutEditPolicy() {
		org.eclipse.gmf.runtime.diagram.ui.editpolicies.LayoutEditPolicy lep = new org.eclipse.gmf.runtime.diagram.ui.editpolicies.LayoutEditPolicy() {

			protected EditPolicy createChildEditPolicy(EditPart child) {
				EditPolicy result = child
						.getEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE);
				if (result == null) {
					result = new NonResizableEditPolicy();
				}
				return result;
			}

			protected Command getMoveChildrenCommand(Request request) {
				return null;
			}

			protected Command getCreateCommand(CreateRequest request) {
				return null;
			}
		};
		return lep;
	}

	/**
	 * @generated
	 */
	protected IFigure createNodeShape() {
		return primaryShape = new InterfaceFigure();
	}

	/**
	 * @generated
	 */
	public InterfaceFigure getPrimaryShape() {
		return (InterfaceFigure) primaryShape;
	}

	/**
	 * @generated
	 */
	protected boolean addFixedChild(EditPart childEditPart) {
		if (childEditPart instanceof InterfaceName2EditPart) {
			((InterfaceName2EditPart) childEditPart).setLabel(getPrimaryShape()
					.getFigureInterfaceNameFigure());
			return true;
		}
		return false;
	}

	/**
	 * @generated
	 */
	protected boolean removeFixedChild(EditPart childEditPart) {
		if (childEditPart instanceof InterfaceName2EditPart) {
			return true;
		}
		return false;
	}

	/**
	 * @generated
	 */
	protected void addChildVisual(EditPart childEditPart, int index) {
		if (addFixedChild(childEditPart)) {
			return;
		}
		super.addChildVisual(childEditPart, -1);
	}

	/**
	 * @generated
	 */
	protected void removeChildVisual(EditPart childEditPart) {
		if (removeFixedChild(childEditPart)) {
			return;
		}
		super.removeChildVisual(childEditPart);
	}

	/**
	 * @generated
	 */
	protected IFigure getContentPaneFor(IGraphicalEditPart editPart) {
		return getContentPane();
	}

	/**
	 * @generated
	 */
	protected NodeFigure createNodePlate() {
		DefaultSizeNodeFigure result = new DefaultSizeNodeFigure(100, 50);
		return result;
	}

	/**
	 * Creates figure for this edit part.
	 * 
	 * Body of this method does not depend on settings in generation model
	 * so you may safely remove <i>generated</i> tag and modify it.
	 * 
	 * @generated
	 */
	protected NodeFigure createNodeFigure() {
		NodeFigure figure = createNodePlate();
		figure.setLayoutManager(new StackLayout());
		IFigure shape = createNodeShape();
		figure.add(shape);
		contentPane = setupContentPane(shape);
		return figure;
	}

	/**
	 * Default implementation treats passed figure as content pane.
	 * Respects layout one may have set for generated figure.
	 * @param nodeShape instance of generated figure class
	 * @generated
	 */
	protected IFigure setupContentPane(IFigure nodeShape) {
		if (nodeShape.getLayoutManager() == null) {
			ConstrainedToolbarLayout layout = new ConstrainedToolbarLayout();
			layout.setSpacing(5);
			nodeShape.setLayoutManager(layout);
		}
		return nodeShape; // use nodeShape itself as contentPane
	}

	/**
	 * @generated
	 */
	public IFigure getContentPane() {
		if (contentPane != null) {
			return contentPane;
		}
		return super.getContentPane();
	}

	/**
	 * @generated
	 */
	protected void setForegroundColor(Color color) {
		if (primaryShape != null) {
			primaryShape.setForegroundColor(color);
		}
	}

	/**
	 * @generated
	 */
	protected void setBackgroundColor(Color color) {
		if (primaryShape != null) {
			primaryShape.setBackgroundColor(color);
		}
	}

	/**
	 * @generated
	 */
	protected void setLineWidth(int width) {
		if (primaryShape instanceof Shape) {
			((Shape) primaryShape).setLineWidth(width);
		}
	}

	/**
	 * @generated
	 */
	protected void setLineType(int style) {
		if (primaryShape instanceof Shape) {
			((Shape) primaryShape).setLineStyle(style);
		}
	}

	/**
	 * @generated
	 */
	public EditPart getPrimaryChildEditPart() {
		return getChildBySemanticHint(ReflexactoringVisualIDRegistry
				.getType(InterfaceName2EditPart.VISUAL_ID));
	}

	/**
	 * @generated
	 */
	public List<IElementType> getMARelTypesOnSource() {
		ArrayList<IElementType> types = new ArrayList<IElementType>(2);
		types.add(ReflexactoringElementTypes.TypeDependency_4003);
		types.add(ReflexactoringElementTypes.InterfaceExtend_4004);
		return types;
	}

	/**
	 * @generated
	 */
	public List<IElementType> getMARelTypesOnSourceAndTarget(
			IGraphicalEditPart targetEditPart) {
		LinkedList<IElementType> types = new LinkedList<IElementType>();
		if (targetEditPart instanceof ClassEditPart) {
			types.add(ReflexactoringElementTypes.TypeDependency_4003);
		}
		if (targetEditPart instanceof InterfaceEditPart) {
			types.add(ReflexactoringElementTypes.TypeDependency_4003);
		}
		if (targetEditPart instanceof Class2EditPart) {
			types.add(ReflexactoringElementTypes.TypeDependency_4003);
		}
		if (targetEditPart instanceof reflexactoring.diagram.edit.parts.Interface2EditPart) {
			types.add(ReflexactoringElementTypes.TypeDependency_4003);
		}
		if (targetEditPart instanceof InterfaceEditPart) {
			types.add(ReflexactoringElementTypes.InterfaceExtend_4004);
		}
		if (targetEditPart instanceof reflexactoring.diagram.edit.parts.Interface2EditPart) {
			types.add(ReflexactoringElementTypes.InterfaceExtend_4004);
		}
		return types;
	}

	/**
	 * @generated
	 */
	public List<IElementType> getMATypesForTarget(IElementType relationshipType) {
		LinkedList<IElementType> types = new LinkedList<IElementType>();
		if (relationshipType == ReflexactoringElementTypes.TypeDependency_4003) {
			types.add(ReflexactoringElementTypes.Class_2001);
			types.add(ReflexactoringElementTypes.Interface_2002);
			types.add(ReflexactoringElementTypes.Class_3001);
			types.add(ReflexactoringElementTypes.Interface_3002);
		} else if (relationshipType == ReflexactoringElementTypes.InterfaceExtend_4004) {
			types.add(ReflexactoringElementTypes.Interface_2002);
			types.add(ReflexactoringElementTypes.Interface_3002);
		}
		return types;
	}

	/**
	 * @generated
	 */
	public List<IElementType> getMARelTypesOnTarget() {
		ArrayList<IElementType> types = new ArrayList<IElementType>(3);
		types.add(ReflexactoringElementTypes.TypeDependency_4003);
		types.add(ReflexactoringElementTypes.InterfaceExtend_4004);
		types.add(ReflexactoringElementTypes.Implement_4005);
		return types;
	}

	/**
	 * @generated
	 */
	public List<IElementType> getMATypesForSource(IElementType relationshipType) {
		LinkedList<IElementType> types = new LinkedList<IElementType>();
		if (relationshipType == ReflexactoringElementTypes.TypeDependency_4003) {
			types.add(ReflexactoringElementTypes.Class_2001);
			types.add(ReflexactoringElementTypes.Interface_2002);
			types.add(ReflexactoringElementTypes.Class_3001);
			types.add(ReflexactoringElementTypes.Interface_3002);
		} else if (relationshipType == ReflexactoringElementTypes.InterfaceExtend_4004) {
			types.add(ReflexactoringElementTypes.Interface_2002);
			types.add(ReflexactoringElementTypes.Interface_3002);
		} else if (relationshipType == ReflexactoringElementTypes.Implement_4005) {
			types.add(ReflexactoringElementTypes.Class_2001);
			types.add(ReflexactoringElementTypes.Class_3001);
		}
		return types;
	}

	/**
	 * @generated
	 */
	public class InterfaceFigure extends RectangleFigure {

		/**
		 * @generated
		 */
		private WrappingLabel fFigureInterfaceNameFigure;

		/**
		 * @generated
		 */
		public InterfaceFigure() {
			this.setForegroundColor(THIS_FORE);
			this.setBackgroundColor(THIS_BACK);
			this.setPreferredSize(new Dimension(getMapMode().DPtoLP(100),
					getMapMode().DPtoLP(50)));
			createContents();
		}

		/**
		 * @generated
		 */
		private void createContents() {

			fFigureInterfaceNameFigure = new WrappingLabel();

			fFigureInterfaceNameFigure.setText("<...>");

			this.add(fFigureInterfaceNameFigure);

		}

		/**
		 * @generated
		 */
		public WrappingLabel getFigureInterfaceNameFigure() {
			return fFigureInterfaceNameFigure;
		}

	}

	/**
	 * @generated
	 */
	static final Color THIS_FORE = new Color(null, 0, 0, 0);

	/**
	 * @generated
	 */
	static final Color THIS_BACK = new Color(null, 215, 102, 203);

}
