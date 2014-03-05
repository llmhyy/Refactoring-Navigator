/**
 * 
 */
package reflexactoring.diagram.action.recommend.action;

import java.util.ArrayList;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.SuggestionObject;

/**
 * @author linyun
 *
 */
public class AddModuleAction extends RefactoringAction {
	
	
	private ArrayList<ICompilationUnitWrapper> unMappedUnits;
	
	public AddModuleAction(ArrayList<ICompilationUnitWrapper> unMappedUnits){
		this.actionName = RefactoringAction.ADD;
		
		this.unMappedUnits = unMappedUnits;
	}

	/**
	 * @return the unMappedUnits
	 */
	public ArrayList<ICompilationUnitWrapper> getUnMappedUnits() {
		return unMappedUnits;
	}

	/**
	 * @param unMappedUnits the unMappedUnits to set
	 */
	public void setUnMappedUnits(ArrayList<ICompilationUnitWrapper> unMappedUnits) {
		this.unMappedUnits = unMappedUnits;
	}
	
	@Override
	public String getDetailedDescription(){
		StringBuffer buffer = new StringBuffer();
		String desc = "to contain ";
		buffer.append(desc);
		for(ICompilationUnitWrapper wrapper: unMappedUnits){
			buffer.append(wrapper.getSimpleName() + ", ");
		}
		
		return buffer.toString();
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
