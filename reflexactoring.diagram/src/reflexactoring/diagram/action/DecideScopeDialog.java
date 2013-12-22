/**
 * 
 */
package reflexactoring.diagram.action;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author linyun
 *
 */
public class DecideScopeDialog extends CheckedTreeSelectionDialog {

	/**
	 * @param parent
	 * @param labelProvider
	 * @param contentProvider
	 */
	public DecideScopeDialog(Shell parent, ILabelProvider labelProvider,
			ITreeContentProvider contentProvider) {
		super(parent, labelProvider, contentProvider);
		// TODO Auto-generated constructor stub
	}

}
