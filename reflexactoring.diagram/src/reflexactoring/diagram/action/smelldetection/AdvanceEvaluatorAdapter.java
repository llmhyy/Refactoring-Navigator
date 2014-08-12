/**
 * 
 */
package reflexactoring.diagram.action.smelldetection;

import java.util.ArrayList;

import reflexactoring.diagram.action.recommend.RecommendUtil;
import reflexactoring.diagram.action.recommend.suboptimal.AdvancedFitnessEvaluator;
import reflexactoring.diagram.action.recommend.suboptimal.Genotype;
import reflexactoring.diagram.action.recommend.suboptimal.Violation;
import reflexactoring.diagram.bean.GraphRelationType;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.ReferencingDetail;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class AdvanceEvaluatorAdapter {
	
	private ArrayList<Violation> violationList = new ArrayList<>();
	
	public double computeFitness(ProgramModel model, ArrayList<ModuleWrapper> moduleList){
		double[][] highLevelDependencyMatrix = RecommendUtil.extractGraph(moduleList, 
				GraphRelationType.GRAPH_DEPENDENCY, ReferencingDetail.REFER);
		double[][] highLevelInheritanceMatrix = RecommendUtil.extractGraph(moduleList, 
				GraphRelationType.GRAPH_INHERITANCE, ReferencingDetail.ALL);
		double[][] highLevelCreationMatrix = RecommendUtil.extractGraph(moduleList, 
				GraphRelationType.GRAPH_CREATION, ReferencingDetail.NEW);
		
		
		double[][] lowLevelDependencyMatrix = RecommendUtil.extractGraph(model.getScopeCompilationUnitList(), 
				GraphRelationType.GRAPH_DEPENDENCY, ReferencingDetail.REFER);
		double[][] lowLevelInheritanceMatrix = RecommendUtil.extractGraph(model.getScopeCompilationUnitList(), 
				GraphRelationType.GRAPH_INHERITANCE, ReferencingDetail.ALL);
		double[][] lowLevelCreationMatrix = RecommendUtil.extractGraph(model.getScopeCompilationUnitList(), 
				GraphRelationType.GRAPH_CREATION, ReferencingDetail.NEW);
		
		System.currentTimeMillis();
		
		double[][] similarityTable = new double[moduleList.size()][model.getScopeCompilationUnitList().size()];
		AdvancedFitnessEvaluator evaluator = new AdvancedFitnessEvaluator(similarityTable, highLevelDependencyMatrix, lowLevelDependencyMatrix, 
				highLevelInheritanceMatrix, lowLevelInheritanceMatrix, highLevelCreationMatrix, lowLevelCreationMatrix);
		
		int[] DNA = constructDNA(model, moduleList);
		
		Genotype gene = new Genotype(DNA, null, evaluator);
		
		double structureAndLexicalFitness = gene.getFitness();
		double CBO = model.computeNormalizedCBOMetrics();
		double LCOM = model.computeNormalizedLCOMMetrics();
		
		this.violationList = ((AdvancedFitnessEvaluator)gene.getEvaluator()).getViolationList();
		
		double fitness = (1-CBO) + (1-LCOM) + structureAndLexicalFitness;
		return fitness;
	}

	/**
	 * @return the violationList
	 */
	public ArrayList<Violation> getViolationList() {
		return violationList;
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
