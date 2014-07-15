package reflexactoring.diagram.edit.parts;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Color;

import reflexactoring.ModuleDependency;
import reflexactoring.ModuleExtend;
import reflexactoring.diagram.bean.ModuleLinkWrapper;
import reflexactoring.diagram.edit.parts.ModuleDependencyEditPart.ModuleDependencyFigure;
import reflexactoring.diagram.edit.policies.ModuleExtendItemSemanticEditPolicy;

/**
 * @generated
 */
public class ModuleExtendEditPart extends ModuleLinkEditPart{

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 4006;

	/**
	 * @generated
	 */
	public ModuleExtendEditPart(View view) {
		super(view);
	}

	/**
	 * @generated
	 */
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE,
				new ModuleExtendItemSemanticEditPolicy());
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
		ModuleExtendFigure figure = new ModuleExtendFigure();
		EObject eObject = this.resolveSemanticElement();
		if (eObject instanceof ModuleExtend) {
			ModuleExtend extend = (ModuleExtend) eObject;
			if (extend.getName().equals(ModuleLinkWrapper.ABSENCE)) {
				figure.setAbsenceStyle();
			} else if (extend.getName()
					.equals(ModuleLinkWrapper.DIVERGENCE)) {
				figure.setDivergneceStyle();
			} else if (extend.getName().equals(
					ModuleLinkWrapper.CONFORMANCE)) {
				figure.setConformanceStyle();
			}
		}
		return figure;
	}

	/**
	 * @generated
	 */
	public ModuleExtendFigure getPrimaryShape() {
		return (ModuleExtendFigure) getFigure();
	}

	/**
	 * @not generated
	 */
	public class ModuleExtendFigure extends ModuleLinkFigure {

		/**
		 * @generated
		 */
		public ModuleExtendFigure() {
			this.setLineWidth(2);
			this.setForegroundColor(THIS_FORE);

			setTargetDecoration(createTargetDecoration());
		}

		/**
		 * @generated
		 */
		private RotatableDecoration createTargetDecoration() {
			PolygonDecoration df = new PolygonDecoration();
			df.setFill(true);
			df.setLineWidth(2);
			df.setForegroundColor(DF_FORE);
			df.setBackgroundColor(ColorConstants.white);
			PointList pl = new PointList();
			pl.addPoint(getMapMode().DPtoLP(-2), getMapMode().DPtoLP(2));
			pl.addPoint(getMapMode().DPtoLP(-2), getMapMode().DPtoLP(-2));
			pl.addPoint(getMapMode().DPtoLP(0), getMapMode().DPtoLP(0));
			df.setTemplate(pl);
			df.setScale(getMapMode().DPtoLP(7), getMapMode().DPtoLP(3));
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
