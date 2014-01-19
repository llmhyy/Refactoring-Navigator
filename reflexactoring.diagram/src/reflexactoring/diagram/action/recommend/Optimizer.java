/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

import reflexactoring.diagram.action.ModelMapper;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;

/**
 * @author linyun
 *
 */
public class Optimizer {
	
	public ArrayList<Suggestion> getSuggestionsByOptimization(ArrayList<ICompilationUnitWrapper> units, ArrayList<ModuleWrapper> modules){
		
		optimize(units, modules);
		
		return null;
	}
	
	private void optimize(ArrayList<ICompilationUnitWrapper> units, ArrayList<ModuleWrapper> modules){
		double[][] similarityTable = new ModelMapper().generateMappingRelation(modules, units);
		
		
	}
}
