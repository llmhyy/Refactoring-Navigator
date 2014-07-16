/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

/**
 * @author Adi
 *
 */
public abstract class DependencyAction extends LinkAction{
	public String getTagActionName(){
		return "<b>" + "Dependency Prerequiste: " + actionName + "</b>";
	}
}
