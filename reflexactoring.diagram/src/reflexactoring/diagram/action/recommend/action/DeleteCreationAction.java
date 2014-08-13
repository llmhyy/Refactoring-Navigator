/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

import reflexactoring.diagram.bean.ModuleLinkWrapper;
import reflexactoring.diagram.bean.SuggestionObject;
import reflexactoring.diagram.util.GEFDiagramUtil;

/**
 * @author Adi
 *
 */
public class DeleteCreationAction extends CreationAction {

	public DeleteCreationAction() {
		this.actionName = RefactoringAction.DEL;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.action.RefactoringAction#execute(reflexactoring.diagram.bean.SuggestionObject)
	 */
	@Override
	public void execute(SuggestionObject suggestionObj) {
		if(suggestionObj instanceof ModuleLinkWrapper){
			ModuleLinkWrapper dependencyWrapper = (ModuleLinkWrapper)suggestionObj;
			
			GEFDiagramUtil.removeModuleLink(dependencyWrapper.getSourceModule(), 
					dependencyWrapper.getTargetModule(), ModuleLinkWrapper.MODULE_CREATION);
		}

	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.action.RefactoringAction#undoExecute(reflexactoring.diagram.bean.SuggestionObject)
	 */
	@Override
	public void undoExecute(SuggestionObject suggestionObj) {
		if(suggestionObj instanceof ModuleLinkWrapper){
			ModuleLinkWrapper dependencyWrapper = (ModuleLinkWrapper)suggestionObj;
			
			GEFDiagramUtil.addModuleLink(dependencyWrapper.getSourceModule(), 
					dependencyWrapper.getTargetModule(), ModuleLinkWrapper.MODULE_CREATION);
			
		}

	}

}