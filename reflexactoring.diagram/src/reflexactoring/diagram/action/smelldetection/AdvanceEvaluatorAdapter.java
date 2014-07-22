/**
 * 
 */
package reflexactoring.diagram.action.smelldetection;

import java.util.ArrayList;

import reflexactoring.diagram.action.recommend.RecommendUtil;
import reflexactoring.diagram.action.recommend.suboptimal.AdvancedFitnessEvaluator;
import reflexactoring.diagram.action.recommend.suboptimal.Genotype;
import reflexactoring.diagram.bean.GraphRelationType;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class AdvanceEvaluatorAdapter {
	
	public double computeFitness(ProgramModel model, ArrayList<ModuleWrapper> moduleList){
		double[][] highLevelDependencyMatrix = RecommendUtil.extractGraph(moduleList, GraphRelationType.GRAPH_DEPENDENCY);
		double[][] highLevelInheritanceMatrix = RecommendUtil.extractGraph(moduleList, GraphRelationType.GRAPH_INHERITANCE);
		double[][] lowLevelDependencyMatrix = RecommendUtil.extractGraph(model.getScopeCompilationUnitList(), GraphRelationType.GRAPH_DEPENDENCY);
		double[][] lowLevelInheritanceMatrix = RecommendUtil.extractGraph(model.getScopeCompilationUnitList(), GraphRelationType.GRAPH_INHERITANCE);
		double[][] similarityTable = new double[moduleList.size()][model.getScopeCompilationUnitList().size()];
		AdvancedFitnessEvaluator evaluator = new AdvancedFitnessEvaluator(similarityTable, highLevelDependencyMatrix, lowLevelDependencyMatrix, 
				highLevelInheritanceMatrix, lowLevelInheritanceMatrix);
		
		int[] DNA = constructDNA(model, moduleList);
		
		Genotype gene = new Genotype(DNA, null, evaluator);
		
		return gene.getFitness();
	}

	/**
	 * @param model
	 * @param moduleList
	 * @return
	 */
	private int[] constructDNA(ProgramModel model,
			ArrayList<ModuleWrapper> moduleList) {
		
		int[] DNA = new int[model.getScopeCompilationUnitList().size()];
		for(int i=0; i<DNA.length; i++){
			ModuleWrapper module = model.getScopeCompilationUnitList().get(i).getMappingModule();
			int index = ReflexactoringUtil.getModuleIndex(moduleList, module);
			DNA[i] = index;
		}
		
		return DNA;
		
	}
	
}
