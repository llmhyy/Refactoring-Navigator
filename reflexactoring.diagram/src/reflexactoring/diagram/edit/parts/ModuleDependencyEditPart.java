package reflexactoring.diagram.edit.parts;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

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
	 * @generated
	 */
	protected Connection createConnectionFigure() {
		return new ModuleDependencyFigure();
	}

	/**
	 * @generated
	 */
	public ModuleDependencyFigure getPrimaryShape() {
		return (ModuleDependencyFigure) getFigure();
	}

	/**
	 * @generated
	 */
	public class ModuleDependencyFigure extends PolylineConnectionEx {

		/**
		 * @generated
		 */
		public ModuleDependencyFigure() {
			this.setLineWidth(2);
			this.setForegroundColor(THIS_FORE);

			setTargetDecoration(createTargetDecoration());
		}

		/**
		 * @generated
		 */
		private RotatableDecoration createTargetDecoration() {
			PolylineDecoration df = new PolylineDecoration();
			df.setLineWidth(2);
			return df;
		}
		
		public void setOriginStyle(){
			this.setLineStyle(SWT.LINE_SOLID);
			this.setForegroundColor(THIS_FORE);
		}

		public void setConformanceStyle(){
			this.setLineStyle(SWT.LINE_SOLID);
			this.setForegroundColor(COMFORMANCE);
		}
		
		public void setAbsenceStyle(){
			this.setLineStyle(SWT.LINE_DOT);
			this.setForegroundColor(ABSENCE);
		}
		
		public void setDivergneceStyle(){
			this.setLineStyle(SWT.LINE_DASH);
			this.setForegroundColor(DIVERGENCE);
		}
	}

	/**
	 * @generated
	 */
	static final Color THIS_FORE = new Color(null, 0, 0, 0);

	static final Color COMFORMANCE = new Color(null, 85, 138, 37);
	static final Color ABSENCE = new Color(null, 210, 180, 140);
	static final Color DIVERGENCE = new Color(null, 255, 92, 72);
}
