/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

/**
 * @author linyun
 *
 */
public abstract class DependencyAction extends DirectOrientedAction{
	public String getTagActionName(){
		return "<b>" + "Prerequiste: " + actionName + "</b>";
	}
}
