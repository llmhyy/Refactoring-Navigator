/**
 * 
 */
package reflexactoring.diagram.action.recommend;

/**
 * @author linyun
 *
 */
public abstract class RefactoringAction {
	
	protected static String ADD = "add";
	protected static String DEL = "delete";
	protected static String MOVE = "move";
	
	protected String actionName;
	
	public RefactoringAction() {
	}

	/**
	 * @return the actionName
	 */
	public String getActionName() {
		return actionName;
	}

	/**
	 * @param actionName the actionName to set
	 */
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	public abstract String getDetailedDescription();
}
