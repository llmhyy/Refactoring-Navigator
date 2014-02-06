/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

import reflexactoring.diagram.action.recommend.action.MoveAction;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.SuggestionObject;
import reflexactoring.diagram.bean.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public class Suggester {
	
	public ArrayList<Suggestion> generateSuggestions(ArrayList<? extends SuggestionObject> suggestionObjects,
			ArrayList<ModuleWrapper> modules, int[] bestSolution, int[] initialSolution,
			ArrayList<int[]> relationMap){
		
		ArrayList<Suggestion> suggestions = new ArrayList<>();
		
		
		for(int i=0; i<initialSolution.length; i++){
			/**
			 * indicate optimal result is 1 and x0 is 0, implying
			 */
			if(bestSolution[i] > initialSolution[i]){
				int moduleIndex = relationMap.get(i)[0];
				int unitIndex = relationMap.get(i)[1];
				
				ModuleWrapper tobeMappedmodule = modules.get(moduleIndex);
				SuggestionObject suggestionObject = suggestionObjects.get(unitIndex);
				
				MoveAction action = new MoveAction();
				
				if(suggestionObject instanceof ICompilationUnitWrapper){
					action.setOrigin(((ICompilationUnitWrapper)suggestionObject).getMappingModule());					
				}
				else if(suggestionObject instanceof UnitMemberWrapper){
					action.setOrigin(((UnitMemberWrapper)suggestionObject).getUnitWrapper().getMappingModule());
				}
				
				action.setDestination(tobeMappedmodule);
				
				Suggestion suggestion = new Suggestion(suggestionObject, action);
				suggestions.add(suggestion);
			}
			/**
			 * indicate optimal result is 0 and x0 is 1
			 */
			/*else if(optimalResult[i] < x0Result[i]){
				
			}*/
		}
		
		return suggestions;
	}
}