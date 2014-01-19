/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

import org.eclipse.ui.PartInitException;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * This class is used to recommend refactoring suggestions to help user reconcile the conflicts between
 * high-level and low-level models. 
 * 
 * First, it will check whether there is any unmapped java types on
 * graph, if yes, it will first suggest the user to 1) add new module and 2) manually map them to other
 * modules. 
 * 
 * Second, it will search an optimal mapping on class level, i.e., it will try find an optimal 
 * movement for class. If it is not possible to move class to make two level consistent, it will provide
 * two choices: 1) modify the relation of high-level modules, if the user insists on maintain the high-level
 * module relations, 2)this class will try to break the classes and find an optimal mapping for methods and 
 * fields. 
 * 
 * Third, if it is still not possible to find an mapping conforming to the consistency of both module, there
 * will be two choices: 1) modify the relation of high-level modules, if the user insists on maintain the 
 * high-level module relations, 2)this class will try to break some methods with heuristic rules.
 * 
 * @author linyun
 *
 */
public class RefactoringRecommender {
	
	public RefactoringRecommender(){
		
	}
	
	public ArrayList<Suggestion> recommend(){
		
		ArrayList<ICompilationUnitWrapper> unmappedUnits = checkUnmappedCompilationUnits();
		if(unmappedUnits.size() != 0){
			ArrayList<Suggestion> suggestions = generateSuggestionForUnmappedUnits(unmappedUnits);
			return suggestions;
		}
		
		try {
			ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
			/**
			 * need to gain the dependencies amongst modules
			 */
			
			Optimizer optimizer = new Optimizer();
			ArrayList<Suggestion> suggestions = optimizer.getSuggestionsByOptimization(Settings.scope.getScopeCompilationUnitList(), moduleList);
			
			return suggestions;
			
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<Suggestion>();
	}
	
	
	
	private ArrayList<Suggestion> generateSuggestionForUnmappedUnits(ArrayList<ICompilationUnitWrapper> unmappedUnits){
		
		AddModuleAction action = new AddModuleAction(unmappedUnits);
		
		Suggestion suggestion = new Suggestion(null, action);
		
		ArrayList<Suggestion> suggestions = new ArrayList<>();
		suggestions.add(suggestion);
		
		return suggestions;
	}
	
	private ArrayList<ICompilationUnitWrapper> checkUnmappedCompilationUnits(){
		ArrayList<ICompilationUnitWrapper> list = new ArrayList<>();
		for(ICompilationUnitWrapper unit: Settings.scope.getScopeCompilationUnitList()){
			if(unit.getMappingModule() == null){
				list.add(unit);
			}
		}
		
		return list;
	}
	
}
