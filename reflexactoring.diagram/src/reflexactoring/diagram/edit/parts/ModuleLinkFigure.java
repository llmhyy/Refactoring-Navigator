/**
 * 
 */
package reflexactoring.diagram.edit.parts;

import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * 
 * This class is created to abstract both module dependency and module extend figures.
 * @author linyun
 *
 */
public class ModuleLinkFigure extends PolylineConnectionEx{
	/**
	 * @generated
	 */
	static final Color THIS_FORE = new Color(null, 0, 0, 0);

	/**
	 * @not generated
	 */
	static final Color COMFORMANCE = new Color(null, 85, 138, 37);
	static final Color ABSENCE = new Color(null, 210, 180, 140);
	static final Color DIVERGENCE = new Color(null, 255, 92, 72);
	
	public void setOriginStyle() {
		this.setLineStyle(SWT.LINE_SOLID);
		this.setForegroundColor(THIS_FORE);
	}

	public void setConformanceStyle() {
		this.setLineStyle(SWT.LINE_SOLID);
		this.setForegroundColor(COMFORMANCE);
	}

	public void setAbsenceStyle() {
		this.setLineStyle(SWT.LINE_DOT);
		this.setForegroundColor(ABSENCE);
	}

	public void setDivergneceStyle() {
		this.setLineStyle(SWT.LINE_DASH);
		this.setForegroundColor(DIVERGENCE);
	}
}
