/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

import reflexactoring.diagram.bean.SuggestionObject;

/**
 * @author linyun
 *
 */
public class AddDependencyAction extends DependencyAction {
	
	public AddDependencyAction(){
		this.actionName = RefactoringAction.ADD;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.action.RefactoringAction#execute(reflexactoring.diagram.bean.SuggestionObject)
	 */
	@Override
	public void execute(SuggestionObject suggestionObj) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.action.RefactoringAction#undoExecute(reflexactoring.diagram.bean.SuggestionObject)
	 */
	@Override
	public void undoExecute(SuggestionObject suggestionObj) {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
