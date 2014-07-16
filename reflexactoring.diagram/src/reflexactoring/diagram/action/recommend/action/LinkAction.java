/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

/**
 * @author linyun
 *
 */
public abstract class LinkAction extends DirectOrientedAction{
	public String getTagActionName(){
		return "<b>" + "Prerequiste: " + actionName + "</b>";
	}
}
