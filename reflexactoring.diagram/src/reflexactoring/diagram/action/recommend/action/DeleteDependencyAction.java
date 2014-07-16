/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

import reflexactoring.diagram.bean.ModuleLinkWrapper;
import reflexactoring.diagram.bean.SuggestionObject;
import reflexactoring.diagram.util.GEFDiagramUtil;

/**
 * @author linyun
 *
 */
public class DeleteDependencyAction extends LinkAction {
	public DeleteDependencyAction(){
		this.actionName = RefactoringAction.DEL;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.action.RefactoringAction#execute(reflexactoring.diagram.bean.SuggestionObject)
	 */
	@Override
	public void execute(SuggestionObject suggestionObj) {
		if(suggestionObj instanceof ModuleLinkWrapper){
			ModuleLinkWrapper dependencyWrapper = (ModuleLinkWrapper)suggestionObj;
			
			GEFDiagramUtil.removeModuleDependency(dependencyWrapper.getSourceModule(), 
					dependencyWrapper.getTargetModule());
		}
		
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.action.RefactoringAction#undoExecute(reflexactoring.diagram.bean.SuggestionObject)
	 */
	@Override
	public void undoExecute(SuggestionObject suggestionObj) {
		if(suggestionObj instanceof ModuleLinkWrapper){
			ModuleLinkWrapper dependencyWrapper = (ModuleLinkWrapper)suggestionObj;
			
			GEFDiagramUtil.addModuleDependency(dependencyWrapper.getSourceModule(), 
					dependencyWrapper.getTargetModule());
		}
		
	}
}
