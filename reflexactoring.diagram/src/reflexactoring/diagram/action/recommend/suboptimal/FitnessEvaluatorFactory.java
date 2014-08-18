/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import reflexactoring.diagram.action.recommend.RecommendUtil;
import reflexactoring.diagram.bean.GraphRelationType;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.ReferencingDetail;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class FitnessEvaluatorFactory {
	
	public static FitnessEvaluator createFitnessEvaluator(ProgramModel model, int type){
		double[][] similarityTable = Settings.similarityTable.convertModuleUnitsSimilarityTableToRawTable();
		
		ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
		
		double[][] highLevelNodeDependencyMatrix = RecommendUtil.extractGraph(moduleList, 
				GraphRelationType.GRAPH_DEPENDENCY, ReferencingDetail.REFER);
		double[][] highLevelNodeInheritanceMatrix = RecommendUtil.extractGraph(moduleList, 
				GraphRelationType.GRAPH_INHERITANCE, ReferencingDetail.ALL);
		double[][] highLevelNodeCreationMatrix = RecommendUtil.extractGraph(moduleList, 
				GraphRelationType.GRAPH_CREATION, ReferencingDetail.NEW);
		
		double[][] lowLevelNodeDependencyMatrix = RecommendUtil.extractGraph(model.getScopeCompilationUnitList(), 
				GraphRelationType.GRAPH_DEPENDENCY, ReferencingDetail.REFER);
		double[][] lowLevelNodeInheritanceMatrix = RecommendUtil.extractGraph(model.getScopeCompilationUnitList(), 
				GraphRelationType.GRAPH_INHERITANCE, ReferencingDetail.ALL);
		double[][] lowLevelNodeCreationMatrix = RecommendUtil.extractGraph(model.getScopeCompilationUnitList(), 
				GraphRelationType.GRAPH_CREATION, ReferencingDetail.NEW);
		
		switch(type){
		case FitnessEvaluator.ADVANCED_EVALUATOR:
			return new AdvancedFitnessEvaluator(similarityTable, highLevelNodeDependencyMatrix, lowLevelNodeDependencyMatrix, 
					highLevelNodeInheritanceMatrix, lowLevelNodeInheritanceMatrix, highLevelNodeCreationMatrix, 
					lowLevelNodeCreationMatrix);
		case FitnessEvaluator.BALANCED_EVALUATOR:
			return new BalancedFitnessEvaulator(similarityTable, highLevelNodeDependencyMatrix, lowLevelNodeDependencyMatrix, 
					highLevelNodeInheritanceMatrix, lowLevelNodeInheritanceMatrix, highLevelNodeCreationMatrix, 
					lowLevelNodeCreationMatrix);
		}
		
		return null;
	}
}
