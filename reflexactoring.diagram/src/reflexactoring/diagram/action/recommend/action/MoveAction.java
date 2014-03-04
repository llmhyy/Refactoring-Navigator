/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

/**
 * @author linyun
 *
 */
public abstract class MoveAction extends DirectOrientedAction {
	public MoveAction(){
		this.actionName = RefactoringAction.MOVE;
	}
}
