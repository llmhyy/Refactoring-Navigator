package reflexactoring.diagram.edit.parts;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import reflexactoring.ModuleDependency;
import reflexactoring.diagram.action.DiagramUpdater;
import reflexactoring.diagram.bean.ModuleLinkWrapper;
import reflexactoring.diagram.edit.policies.ModuleDependencyItemSemanticEditPolicy;

/**
 * @generated
 */
public class ModuleDependencyEditPart extends ConnectionNodeEditPart implements
		ITreeBranchEditPart {

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 4001;

	/**
	 * @generated
	 */
	public ModuleDependencyEditPart(View view) {
		super(view);
	}

	/**
	 * @generated
	 */
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE,
				new ModuleDependencyItemSemanticEditPolicy());
	}

	/**
	 * Creates figure for this edit part.
	 * 
	 * Body of this method does not depend on settings in generation model
	 * so you may safely remove <i>generated</i> tag and modify it.
	 * 
	 * @not generated
	 */
	protected Connection createConnectionFigure() {
		ModuleDependencyFigure figure = new ModuleDependencyFigure();
		EObject eObject = this.resolveSemanticElement();
		if (eObject instanceof ModuleDependency) {
			ModuleDependency dependency = (ModuleDependency) eObject;
			if (dependency.getName().equals(ModuleLinkWrapper.ABSENCE)) {
				figure.setAbsenceStyle();
			} else if (dependency.getName()
					.equals(ModuleLinkWrapper.DIVERGENCE)) {
				figure.setDivergneceStyle();
			} else if (dependency.getName().equals(
					ModuleLinkWrapper.CONFORMANCE)) {
				figure.setConformanceStyle();
			}
		}
		return figure;
	}

	/**
	 * @generated
	 */
	public ModuleDependencyFigure getPrimaryShape() {
		return (ModuleDependencyFigure) getFigure();
	}

	/**
	 * @not generated
	 */
	public class ModuleDependencyFigure extends ModuleLinkFigure {

		/**
		 * @generated
		 */
		public ModuleDependencyFigure() {
			this.setLineWidth(2);
			this.setForegroundColor(THIS_FORE);

			setTargetDecoration(createTargetDecoration());
		}

		/**
		 * @not generated
		 */
		private RotatableDecoration createTargetDecoration() {
			PolylineDecoration df = new PolylineDecoration();
			df.setLineWidth(2);
			return df;
		}
	}

	/**
	 * @generated
	 */
	static final Color THIS_FORE = new Color(null, 0, 0, 0);

}
