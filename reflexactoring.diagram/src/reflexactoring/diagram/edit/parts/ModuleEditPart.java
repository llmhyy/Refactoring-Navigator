package reflexactoring.diagram.edit.parts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.RoundedRectangle;
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

import reflexactoring.diagram.edit.policies.ModuleItemSemanticEditPolicy;
import reflexactoring.diagram.part.ReflexactoringVisualIDRegistry;
import reflexactoring.diagram.providers.ReflexactoringElementTypes;

/**
 * @generated
 */
public class ModuleEditPart extends ShapeNodeEditPart {

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 2003;

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
	public ModuleEditPart(View view) {
		super(view);
	}

	/**
	 * @generated
	 */
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE,
				new ModuleItemSemanticEditPolicy());
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
		return primaryShape = new ModuleFigure();
	}

	/**
	 * @generated
	 */
	public ModuleFigure getPrimaryShape() {
		return (ModuleFigure) primaryShape;
	}

	/**
	 * @not generated
	 */
	protected boolean addFixedChild(EditPart childEditPart) {
		if (childEditPart instanceof ModuleNameEditPart) {
			((ModuleNameEditPart) childEditPart).setLabel(getPrimaryShape()
					.getFigureModuleNameFigure());
			return true;
		}
		if (childEditPart instanceof ModuleTypeContainerCompartmentEditPart) {
			IFigure pane = getPrimaryShape().getFigureTypeContainer();
			//setupContentPane(pane); // FIXME each comparment should handle his content pane in his own way 

			pane.setLayoutManager(new GridLayout());
			GridData gridData = new GridData();
			gridData.verticalAlignment = GridData.FILL;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.horizontalIndent = 0;
			gridData.horizontalSpan = 1;
			gridData.verticalSpan = 1;
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;

			pane.add(((ModuleTypeContainerCompartmentEditPart) childEditPart)
					.getFigure(), gridData);
			return true;
		}
		return false;
	}

	/**
	 * @generated
	 */
	protected boolean removeFixedChild(EditPart childEditPart) {
		if (childEditPart instanceof ModuleNameEditPart) {
			return true;
		}
		if (childEditPart instanceof ModuleTypeContainerCompartmentEditPart) {
			IFigure pane = getPrimaryShape().getFigureTypeContainer();
			pane.remove(((ModuleTypeContainerCompartmentEditPart) childEditPart)
					.getFigure());
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
		if (editPart instanceof ModuleTypeContainerCompartmentEditPart) {
			return getPrimaryShape().getFigureTypeContainer();
		}
		return getContentPane();
	}

	/**
	 * @generated
	 */
	protected NodeFigure createNodePlate() {
		DefaultSizeNodeFigure result = new DefaultSizeNodeFigure(300, 200);
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
				.getType(ModuleNameEditPart.VISUAL_ID));
	}

	/**
	 * @generated
	 */
	public List<IElementType> getMARelTypesOnSource() {
		ArrayList<IElementType> types = new ArrayList<IElementType>(2);
		types.add(ReflexactoringElementTypes.ModuleDependency_4001);
		types.add(ReflexactoringElementTypes.ModuleExtend_4006);
		return types;
	}

	/**
	 * @generated
	 */
	public List<IElementType> getMARelTypesOnSourceAndTarget(
			IGraphicalEditPart targetEditPart) {
		LinkedList<IElementType> types = new LinkedList<IElementType>();
		if (targetEditPart instanceof reflexactoring.diagram.edit.parts.ModuleEditPart) {
			types.add(ReflexactoringElementTypes.ModuleDependency_4001);
		}
		if (targetEditPart instanceof reflexactoring.diagram.edit.parts.ModuleEditPart) {
			types.add(ReflexactoringElementTypes.ModuleExtend_4006);
		}
		return types;
	}

	/**
	 * @generated
	 */
	public List<IElementType> getMATypesForTarget(IElementType relationshipType) {
		LinkedList<IElementType> types = new LinkedList<IElementType>();
		if (relationshipType == ReflexactoringElementTypes.ModuleDependency_4001) {
			types.add(ReflexactoringElementTypes.Module_2003);
		} else if (relationshipType == ReflexactoringElementTypes.ModuleExtend_4006) {
			types.add(ReflexactoringElementTypes.Module_2003);
		}
		return types;
	}

	/**
	 * @generated
	 */
	public List<IElementType> getMARelTypesOnTarget() {
		ArrayList<IElementType> types = new ArrayList<IElementType>(2);
		types.add(ReflexactoringElementTypes.ModuleDependency_4001);
		types.add(ReflexactoringElementTypes.ModuleExtend_4006);
		return types;
	}

	/**
	 * @generated
	 */
	public List<IElementType> getMATypesForSource(IElementType relationshipType) {
		LinkedList<IElementType> types = new LinkedList<IElementType>();
		if (relationshipType == ReflexactoringElementTypes.ModuleDependency_4001) {
			types.add(ReflexactoringElementTypes.Module_2003);
		} else if (relationshipType == ReflexactoringElementTypes.ModuleExtend_4006) {
			types.add(ReflexactoringElementTypes.Module_2003);
		}
		return types;
	}

	/**
	 * @generated
	 */
	public class ModuleFigure extends RoundedRectangle {

		/**
		 * @generated
		 */
		private WrappingLabel fFigureModuleNameFigure;
		/**
		 * @generated
		 */
		private RectangleFigure fFigureTypeContainer;

		/**
		 * @generated
		 */
		public ModuleFigure() {

			GridLayout layoutThis = new GridLayout();
			layoutThis.numColumns = 1;
			layoutThis.makeColumnsEqualWidth = true;
			this.setLayoutManager(layoutThis);

			this.setCornerDimensions(new Dimension(getMapMode().DPtoLP(8),
					getMapMode().DPtoLP(8)));
			this.setForegroundColor(THIS_FORE);
			this.setBackgroundColor(THIS_BACK);
			this.setPreferredSize(new Dimension(getMapMode().DPtoLP(300),
					getMapMode().DPtoLP(200)));
			createContents();
		}

		/**
		 * @generated
		 */
		private void createContents() {

			fFigureModuleNameFigure = new WrappingLabel();

			fFigureModuleNameFigure.setText("");

			this.add(fFigureModuleNameFigure);

			fFigureTypeContainer = new RectangleFigure();

			GridData constraintFFigureTypeContainer = new GridData();
			constraintFFigureTypeContainer.verticalAlignment = GridData.FILL;
			constraintFFigureTypeContainer.horizontalAlignment = GridData.FILL;
			constraintFFigureTypeContainer.horizontalIndent = 0;
			constraintFFigureTypeContainer.horizontalSpan = 1;
			constraintFFigureTypeContainer.verticalSpan = 1;
			constraintFFigureTypeContainer.grabExcessHorizontalSpace = true;
			constraintFFigureTypeContainer.grabExcessVerticalSpace = true;
			this.add(fFigureTypeContainer, constraintFFigureTypeContainer);

			GridLayout layoutFFigureTypeContainer = new GridLayout();
			layoutFFigureTypeContainer.numColumns = 1;
			layoutFFigureTypeContainer.makeColumnsEqualWidth = true;
			fFigureTypeContainer.setLayoutManager(layoutFFigureTypeContainer);

		}

		/**
		 * @generated
		 */
		public WrappingLabel getFigureModuleNameFigure() {
			return fFigureModuleNameFigure;
		}

		/**
		 * @generated
		 */
		public RectangleFigure getFigureTypeContainer() {
			return fFigureTypeContainer;
		}

		/**
		 * @NOT generated
		 */
		public void setFrozenBackgroundColor() {
			this.setBackgroundColor(FROZEN_COLOR);
		}

		public void setNormalBackgroundColor() {
			this.setBackgroundColor(THIS_BACK);
		}

	}

	/**
	 * @generated
	 */
	static final Color THIS_FORE = new Color(null, 0, 0, 0);

	/**
	 * @generated
	 */
	static final Color THIS_BACK = new Color(null, 250, 237, 216);

	/**
	 * @not generated
	 */
	static final Color FROZEN_COLOR = new Color(null, 163, 227, 231);
}
