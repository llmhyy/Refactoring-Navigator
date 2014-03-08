/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

import reflexactoring.diagram.bean.SuggestionObject;

/**
 * @author linyun
 *
 */
public abstract class RefactoringAction {
	
	protected static String ADD = "Add";
	protected static String DEL = "Delete";
	protected static String MOVE = "Move";
	
	protected String actionName;
	
	public RefactoringAction() {
	}

	/**
	 * @return the actionName
	 */
	public String getActionName() {
		return actionName;
	}
	
	public String getTagActionName(){
		return "<b>" + actionName + "</b>";
	}

	/**
	 * @param actionName the actionName to set
	 */
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	public abstract String getDetailedDescription();
	public abstract void execute(SuggestionObject suggestionObj);
	public abstract void undoExecute(SuggestionObject suggestionObj);
}
