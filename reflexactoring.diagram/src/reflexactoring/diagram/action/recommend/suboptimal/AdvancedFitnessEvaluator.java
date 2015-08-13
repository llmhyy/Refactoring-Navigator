package reflexactoring.diagram.action.recommend.suboptimal;

import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author Adi
 *
 */
public class AdvancedFitnessEvaluator extends DefaultFitnessEvaluator {
	private double lexicalSim = 0;
	
	/**
	 * @param similarityTable
	 * @param highLevelNodeDependencyMatrix
	 * @param lowLevelNodeDependencyMatrix
	 * @param highLevelNodeCreationMatrix
	 * @param lowLevelNodeCreationMatrix
	 * @param highLevelNodeInheritanceMatrix
	 * @param lowLevelNodeInheritanceMatrix
	 */
	public AdvancedFitnessEvaluator(double[][] similarityTable,
			double[][] highLevelNodeDependencyMatrix,
			double[][] lowLevelNodeDependencyMatrix,
			double[][] highLevelNodeInheritanceMatrix,
			double[][] lowLevelNodeInheritanceMatrix,
			double[][] highLevelNodeCreationMatrix,
			double[][] lowLevelNodeCreationMatrix) {
		super(similarityTable, highLevelNodeDependencyMatrix, lowLevelNodeDependencyMatrix, 
				highLevelNodeInheritanceMatrix, lowLevelNodeInheritanceMatrix, highLevelNodeCreationMatrix, 
				lowLevelNodeCreationMatrix);
	}

	@Override
	public double computeFitness(Genotype gene){
		violationList.clear();
		
		double structureDependencyViolation = computeStructureDependencyViolation(gene);
		double structureInheritanceViolation = computeStructureInheritanceViolation(gene);
		double structureCreationViolation = computeStructureCreationViolation(gene);
		double structureEmptyModuleViolation = computeEmptyModuleViolation(gene);
		
		System.currentTimeMillis();
		
		double lexicalSimilarity = computeLexicalSimilarity(gene);
		this.lexicalSim = lexicalSimilarity;
		
		return /*Double.valueOf(ReflexactoringUtil.getAlpha())**/lexicalSimilarity
				- structureDependencyViolation 
				- structureInheritanceViolation 
				- structureCreationViolation 
				- structureEmptyModuleViolation;
	}

	/**
	 * @return the lexicalSim
	 */
	public double getLexicalSim() {
		return lexicalSim;
	}
	
	

}
