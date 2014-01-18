/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import reflexactoring.diagram.bean.SuggestionObject;

/**
 * @author linyun
 *
 */
public class Suggestion {
	private SuggestionObject suggeestionObject;
	private RefactoringAction action;
	
	/**
	 * @param suggeestionObject
	 * @param action
	 */
	public Suggestion(SuggestionObject suggeestionObject, RefactoringAction action) {
		super();
		this.suggeestionObject = suggeestionObject;
		this.action = action;
	}
	
	public String toString(){
		
		String suggestionObjName = (this.suggeestionObject == null)? "modules ": this.suggeestionObject.getName();
		
		return this.action.getActionName() + " " + 
				suggestionObjName + 
				this.action.getDetailedDescription();
	}

	/**
	 * @return the suggeestionObject
	 */
	public SuggestionObject getSuggeestionObject() {
		return suggeestionObject;
	}

	/**
	 * @param suggeestionObject the suggeestionObject to set
	 */
	public void setSuggeestionObject(SuggestionObject suggeestionObject) {
		this.suggeestionObject = suggeestionObject;
	}

	/**
	 * @return the action
	 */
	public RefactoringAction getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(RefactoringAction action) {
		this.action = action;
	}
	
	
	
}
