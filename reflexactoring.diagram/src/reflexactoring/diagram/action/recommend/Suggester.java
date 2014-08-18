/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;
import java.util.Collections;

import reflexactoring.diagram.action.recommend.action.MoveAction;
import reflexactoring.diagram.action.recommend.action.MoveMemberAction;
import reflexactoring.diagram.action.recommend.action.MoveTypeAction;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.SuggestionObject;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public class Suggester {
	
	public Suggestion generateSuggestion(ArrayList<? extends SuggestionObject> suggestionObjects,
			ArrayList<ModuleWrapper> modules, int[] bestSolution, int[] initialSolution){
		
		Suggestion suggestion = new Suggestion();
		
		for(int i=0; i<initialSolution.length; i++){
			int lowLevelNodeIndex = i;
			int moduleIndex = bestSolution[i];
			
			if(bestSolution[i] != initialSolution[i]){
				SuggestionMove move = generateMoveSuggestion(suggestionObjects,
						modules, lowLevelNodeIndex, moduleIndex);
				suggestion.add(move);				
			}
		}
		
		Collections.sort(suggestion, new SuggestionMoveComparator());
		
		return suggestion;
	}

	/**
	 * @param suggestionObjects
	 * @param modules
	 * @param lowLevelNodeIndex
	 * @param moduleIndex
	 * @return
	 */
	private SuggestionMove generateMoveSuggestion(
			ArrayList<? extends SuggestionObject> suggestionObjects,
			ArrayList<ModuleWrapper> modules, int lowLevelNodeIndex,
			int moduleIndex) {
		ModuleWrapper tobeMappedmodule = modules.get(moduleIndex);
		SuggestionObject suggestionObject = suggestionObjects.get(lowLevelNodeIndex);
		
		MoveAction action = null;
		
		if(suggestionObject instanceof ICompilationUnitWrapper){
			action = new MoveTypeAction();
			action.setOrigin(((ICompilationUnitWrapper)suggestionObject).getMappingModule());					
		}
		else if(suggestionObject instanceof UnitMemberWrapper){
			action = new MoveMemberAction();
			action.setOrigin(((UnitMemberWrapper)suggestionObject).getMappingModule());
		}
		
		action.setDestination(tobeMappedmodule);
		
		SuggestionMove move = new SuggestionMove(suggestionObject, action);
		return move;
	}
	
	public Suggestion generateSuggestion(ArrayList<? extends SuggestionObject> suggestionObjects,
			ArrayList<ModuleWrapper> modules, int[] bestSolution, int[] initialSolution,
			ArrayList<int[]> relationMap){
		
		Suggestion suggestion = new Suggestion();
		
		
		for(int i=0; i<initialSolution.length; i++){
			/**
			 * indicate optimal result is 1 and x0 is 0, implying
			 */
			if(bestSolution[i] > initialSolution[i]){
				int moduleIndex = relationMap.get(i)[0];
				int lowLevelNodeIndex = relationMap.get(i)[1];
				
				SuggestionMove move = generateMoveSuggestion(suggestionObjects,
						modules, lowLevelNodeIndex, moduleIndex);
				suggestion.add(move);
			}
			/**
			 * indicate optimal result is 0 and x0 is 1
			 */
			/*else if(optimalResult[i] < x0Result[i]){
				
			}*/
		}
		
		Collections.sort(suggestion, new SuggestionMoveComparator());
		
		return suggestion;
	}
}
