/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.core.commands.DeleteCommand;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.ui.commands.DeferredCreateConnectionViewAndElementCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest.ConnectionViewAndElementDescriptor;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;

import reflexactoring.Module;
import reflexactoring.ModuleDependency;
import reflexactoring.Reflexactoring;
import reflexactoring.diagram.bean.ModuleLinkWrapper;
import reflexactoring.diagram.bean.SuggestionObject;
import reflexactoring.diagram.part.ReflexactoringDiagramEditorPlugin;
import reflexactoring.diagram.providers.ReflexactoringElementTypes;
import reflexactoring.diagram.util.GEFDiagramUtil;

/**
 * @author adi
 *
 */
public class AddExtendAction extends ExtendAction {
	
	public AddExtendAction(){
		this.actionName = RefactoringAction.ADD;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.action.RefactoringAction#execute(reflexactoring.diagram.bean.SuggestionObject)
	 */
	@Override
	public void execute(SuggestionObject suggestionObj) {
		if(suggestionObj instanceof ModuleLinkWrapper){
			/*ModuleLinkWrapper dependencyWrapper = (ModuleLinkWrapper)suggestionObj;
			
			GEFDiagramUtil.addModuleDependency(dependencyWrapper.getSourceModule(), 
					dependencyWrapper.getTargetModule());*/
			
			//TODO
		}
		
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.action.RefactoringAction#undoExecute(reflexactoring.diagram.bean.SuggestionObject)
	 */
	@Override
	public void undoExecute(SuggestionObject suggestionObj) {
		if(suggestionObj instanceof ModuleLinkWrapper){
			/*ModuleLinkWrapper dependencyWrapper = (ModuleLinkWrapper)suggestionObj;
			
			GEFDiagramUtil.removeModuleDependency(dependencyWrapper.getSourceModule(), 
					dependencyWrapper.getTargetModule());*/
			
			//TODO
		}
		
	}

	
	
	
}
