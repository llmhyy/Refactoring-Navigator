/**
 * 
 */
package reflexactoring.diagram.edit.parts;

import org.eclipse.draw2d.Connection;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.notation.View;

/**
 * This class is created to abstract both module dependency and module extend relations.
 * 
 * @author linyun
 *
 */
public abstract class ModuleLinkEditPart extends ConnectionNodeEditPart implements ITreeBranchEditPart {
	/**
	 * @param view
	 */
	public ModuleLinkEditPart(View view) {
		super(view);
		// TODO Auto-generated constructor stub
	}

	

	/* (non-Javadoc)
	 * @see org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart#createConnectionFigure()
	 */
	@Override
	protected abstract Connection createConnectionFigure();
	
	public abstract ModuleLinkFigure getPrimaryShape();
	
}
