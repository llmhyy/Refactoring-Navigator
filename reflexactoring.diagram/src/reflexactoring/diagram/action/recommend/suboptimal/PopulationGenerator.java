/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapperList;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * Generate population for genetic algorithm based on current map.
 * 
 * @author linyun
 *
 */
public class PopulationGenerator {
	
	public Population createPopulation(ArrayList<ICompilationUnitWrapper> unitList, ArrayList<ModuleWrapper> moduleList){
		/**
		 * one map is a solution, current mapping solution on graph is original DNA.
		 */
		int[] originalDNA = new int[unitList.size()];
		for(int i=0; i<unitList.size(); i++){
			ICompilationUnitWrapper unit = unitList.get(i);
			ModuleWrapper module = unit.getMappingModule();
			int index = ReflexactoringUtil.getModuleIndex(moduleList, module);
			
			originalDNA[i] = index;
		}
		
		/**
		 * 
		 */
		
		return null;
	}
	
	public Population createPopulation(UnitMemberWrapperList memberList, ArrayList<ModuleWrapper> moduleList){
		
		return null;
	}
	
	
}
