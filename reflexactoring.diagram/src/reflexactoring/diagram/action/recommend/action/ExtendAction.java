/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

/**
 * @author Adi
 *
 */
public abstract class ExtendAction extends LinkAction {
	public String getTagActionName(){
		return "<b>" + "Extend Prerequiste: " + actionName + "</b>";	}

}
