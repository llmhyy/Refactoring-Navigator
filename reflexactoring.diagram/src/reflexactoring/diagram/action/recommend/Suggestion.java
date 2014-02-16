/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import reflexactoring.diagram.action.recommend.action.RefactoringAction;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.LowLevelSuggestionObject;
import reflexactoring.diagram.bean.SuggestionObject;
import reflexactoring.diagram.bean.UnitMemberWrapper;

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
		
		String suggestionObjType = "";
		if(suggeestionObject instanceof LowLevelSuggestionObject){
			suggestionObjType = " " + ((LowLevelSuggestionObject)suggeestionObject).getTypeName();
		}
		
		String suggestionObjName = (this.suggeestionObject == null)? "modules ": this.suggeestionObject.getName();
		
		return this.action.getActionName() + 
				suggestionObjType + " " + suggestionObjName + " " +
				this.action.getDetailedDescription();
	}
	
	public String generateTagedText(){
		String suggestionObjType = "";
		if(suggeestionObject instanceof LowLevelSuggestionObject){
			suggestionObjType = " " + ((LowLevelSuggestionObject)suggeestionObject).getTypeName();
		}
		
		String suggestionObjName = (this.suggeestionObject == null)? "modules ": this.suggeestionObject.getNameWithTag();
		
		if(suggeestionObject instanceof UnitMemberWrapper){
			UnitMemberWrapper member = (UnitMemberWrapper)suggeestionObject;
			ICompilationUnitWrapper type = member.getUnitWrapper();
			String location = "(" + type.getNameWithTag() + ") ";
			suggestionObjName += location;
		}
		
		return this.action.getActionName() + 
				suggestionObjType + " " + suggestionObjName + " " +
				this.action.getDetailedDescription() + " ";
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
	
	public void apply(){
		
	}
	
	public void undoApply(){
		
	}
}
