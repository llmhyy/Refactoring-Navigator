package reflexactoring.diagram.edit.parts;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Color;

import reflexactoring.ModuleCreation;
import reflexactoring.diagram.bean.ModuleLinkWrapper;
import reflexactoring.diagram.edit.policies.ModuleCreationItemSemanticEditPolicy;

/**
 * @generated
 */
public class ModuleCreationEditPart extends ModuleLinkEditPart {

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 4007;

	/**
	 * @generated
	 */
	public ModuleCreationEditPart(View view) {
		super(view);
	}

	/**
	 * @generated
	 */
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE,
				new ModuleCreationItemSemanticEditPolicy());
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
		ModuleCreationFigure figure = new ModuleCreationFigure();
		EObject eObject = this.resolveSemanticElement();
		if (eObject instanceof ModuleCreation) {
			ModuleCreation creation = (ModuleCreation) eObject;
			if (creation.getName().equals(ModuleLinkWrapper.ABSENCE)) {
				figure.setAbsenceStyle();
			} else if (creation.getName()
					.equals(ModuleLinkWrapper.DIVERGENCE)) {
				figure.setDivergneceStyle();
			} else if (creation.getName().equals(
					ModuleLinkWrapper.CONFORMANCE)) {
				figure.setConformanceStyle();
			}
		}
		return figure;
		//return new ModuleCreationFigure();
	}

	/**
	 * @generated
	 */
	public ModuleCreationFigure getPrimaryShape() {
		return (ModuleCreationFigure) getFigure();
	}

	/**
	 * @generated
	 */
	public class ModuleCreationFigure extends ModuleLinkFigure {

		/**
		 * @generated
		 */
		public ModuleCreationFigure() {
			this.setLineWidth(2);
			this.setForegroundColor(THIS_FORE);

			setTargetDecoration(createTargetDecoration());
		}

		/**
		 * @not generated
		 */
		private RotatableDecoration createTargetDecoration() {
			/*PolygonDecoration df = new PolygonDecoration();
			df.setFill(true);
			df.setLineWidth(2);
			df.setForegroundColor(DF_FORE);
			df.setBackgroundColor(ColorConstants.white);
			PointList pl = new PointList();
			pl.addPoint(getMapMode().DPtoLP(-2), getMapMode().DPtoLP(2));
			pl.addPoint(getMapMode().DPtoLP(-4), getMapMode().DPtoLP(2));
			pl.addPoint(getMapMode().DPtoLP(-2), getMapMode().DPtoLP(0));
			pl.addPoint(getMapMode().DPtoLP(-4), getMapMode().DPtoLP(-2));
			pl.addPoint(getMapMode().DPtoLP(-2), getMapMode().DPtoLP(-2));
			pl.addPoint(getMapMode().DPtoLP(0), getMapMode().DPtoLP(0));
			df.setTemplate(pl);
			df.setScale(getMapMode().DPtoLP(5), getMapMode().DPtoLP(3));
			return df;*/
						
			PolylineDecoration df = new PolylineDecoration();
			df.setLineWidth(2);
			return df;
		}

	}

	/**
	 * @generated
	 */
	static final Color THIS_FORE = new Color(null, 0, 0, 0);
	/**
	 * @generated
	 */
	static final Color DF_FORE = new Color(null, 0, 0, 0);

}
