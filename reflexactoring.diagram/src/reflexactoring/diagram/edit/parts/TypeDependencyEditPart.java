package reflexactoring.diagram.edit.parts;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.notation.View;

import reflexactoring.diagram.edit.policies.TypeDependencyItemSemanticEditPolicy;

/**
 * @generated
 */
public class TypeDependencyEditPart extends ConnectionNodeEditPart implements
		ITreeBranchEditPart {

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 4003;

	/**
	 * @generated
	 */
	public TypeDependencyEditPart(View view) {
		super(view);
	}

	/**
	 * @generated
	 */
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE,
				new TypeDependencyItemSemanticEditPolicy());
	}

	/**
	 * Creates figure for this edit part.
	 * 
	 * Body of this method does not depend on settings in generation model
	 * so you may safely remove <i>generated</i> tag and modify it.
	 * 
	 * @generated
	 */
	protected Connection createConnectionFigure() {
		return new TypeDependencyFigure();
	}

	/**
	 * @generated
	 */
	public TypeDependencyFigure getPrimaryShape() {
		return (TypeDependencyFigure) getFigure();
	}

	/**
	 * @not generated
	 */
	public class TypeDependencyFigure extends PolylineConnectionEx {

		private PolylineDecoration polylineDecoration;

		/**
		 * @not generated
		 */
		public TypeDependencyFigure() {
			this.setLineWidth(1);

			setTargetDecoration(createTargetDecoration());
		}

		/**
		 * @not generated
		 */
		private RotatableDecoration createTargetDecoration() {
			PolylineDecoration df = new PolylineDecoration();
			df.setLineWidth(1);

			this.setPolylineDecoration(df);

			return df;
		}

		/**
		 * @return the polylineDecoration
		 */
		public PolylineDecoration getPolylineDecoration() {
			return polylineDecoration;
		}

		/**
		 * @param polylineDecoration the polylineDecoration to set
		 */
		public void setPolylineDecoration(PolylineDecoration polylineDecoration) {
			this.polylineDecoration = polylineDecoration;
		}
	}

}
