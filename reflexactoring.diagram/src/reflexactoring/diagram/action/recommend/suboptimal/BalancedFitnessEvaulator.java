/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;


/**
 * This class is used to balance lexical and structure fitness. 
 * 
 * @author linyun
 *
 */
public class BalancedFitnessEvaulator extends DefaultFitnessEvaluator {

	/**
	 * @param similarityTable
	 * @param highLevelNodeDependencyMatrix
	 * @param lowLevelNodeDependencyMatrix
	 * @param highLevelNodeInheritanceMatrix
	 * @param lowLevelNodeInheritanceMatrix
	 * @param highLevelNodeCreationMatrix
	 * @param lowLevelNodeCreationMatrix
	 */
	public BalancedFitnessEvaulator(double[][] similarityTable,
			double[][] highLevelNodeDependencyMatrix,
			double[][] lowLevelNodeDependencyMatrix,
			double[][] highLevelNodeInheritanceMatrix,
			double[][] lowLevelNodeInheritanceMatrix,
			double[][] highLevelNodeCreationMatrix,
			double[][] lowLevelNodeCreationMatrix) {
		super(similarityTable, highLevelNodeDependencyMatrix,
				lowLevelNodeDependencyMatrix, highLevelNodeInheritanceMatrix,
				lowLevelNodeInheritanceMatrix, highLevelNodeCreationMatrix,
				lowLevelNodeCreationMatrix);
	}

	@Override
	public double computeFitness(Genotype gene){
		violationList.clear();
		/**
		 * compute violations
		 */
		computeStructureDependencyViolation(gene);
		computeStructureInheritanceViolation(gene);
		computeStructureCreationViolation(gene);
		
		double lexicalSimilarity = computeLexicalSimilarity(gene);
		
		double depViolationNum = 0;
		double inheritViolationNum = 0;
		double creationViolationNum = 0;
		
		for(Violation violation: this.violationList){
			switch(violation.getType()){
			case Violation.DEPENDENCY_ABSENCE: 
				depViolationNum++;
				break;
			case Violation.DEPENDENCY_DIVERGENCE:
				depViolationNum++;
				break;
			case Violation.INHERITANCE_ABSENCE:
				inheritViolationNum++;
				break;
			case Violation.INHERITANCE_DIVERGENCE:
				inheritViolationNum++;
				break;
			case Violation.CREATION_ABSENCE:
				creationViolationNum++;
				break;
			case Violation.CREATION_DIVERGENCE:
				creationViolationNum++;
				break;
			}
		}
		
		double base = this.highLevelNodeDependencyMatrix.length * (this.highLevelNodeDependencyMatrix.length-1);
		
		double structureSim = 1 - (depViolationNum + inheritViolationNum + creationViolationNum)/(3*base); 
		
		return (lexicalSimilarity + structureSim)/2;
		//return lexicalSimilarity;
	}


}
