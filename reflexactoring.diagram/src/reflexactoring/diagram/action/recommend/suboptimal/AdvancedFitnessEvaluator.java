package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author Adi
 *
 */
public class AdvancedFitnessEvaluator implements FitnessEvaluator {
	
	private ArrayList<Violation> violationList = new ArrayList<>();
	private double[][] similarityTable;
	
	private double[][] highLevelNodeDependencyMatrix;
	private double[][] lowLevelNodeDependencyMatrix;
	private double[][] highLevelNodeInheritanceMatrix;
	private double[][] lowLevelNodeInheritanceMatrix;
	
	public AdvancedFitnessEvaluator(double[][] similarityTable, double[][] highLevelNodeDependencyMatrix, double[][] lowLevelNodeDependencyMatrix, 
			double[][] highLevelNodeInheritanceMatrix, double[][] lowLevelNodeInheritanceMatrix){
		this.setSimilarityTable(similarityTable);
		this.setHighLevelNodeDependencyMatrix(highLevelNodeDependencyMatrix);
		this.setLowLevelNodeDependencyMatrix(lowLevelNodeDependencyMatrix);
		this.setHighLevelNodeInheritanceMatrix(highLevelNodeInheritanceMatrix);
		this.setLowLevelNodeInheritanceMatrix(lowLevelNodeInheritanceMatrix);
	}
	
	public double computeFitness(Genotype gene){
		double structureDependencyViolation = computeStructureDependencyViolation(gene);
		double structureInheritanceViolation = computeStructureInheritanceViolation(gene);
		double lexicalSimilarity = computeLexicalSimilarity(gene);
		return Double.valueOf(ReflexactoringUtil.getAlpha())*lexicalSimilarity
				- structureDependencyViolation - structureInheritanceViolation;
	}
	
	private double computeStructureDependencyViolation(Genotype gene){
		double result = 0;
		
		double[][] confidenceTable = Settings.dependencyConfidenceTable.convertToRawTable();
		
		for(int i=0; i<highLevelNodeDependencyMatrix.length; i++){
			for(int j=0; j<highLevelNodeDependencyMatrix.length; j++){
				if(i != j){
					/**
					 * Detect divergence violation
					 */
					if(highLevelNodeDependencyMatrix[i][j] == 0){
						int violationNum = countDivergenceViolation(gene, i, j, lowLevelNodeDependencyMatrix);
						if(violationNum != 0){
							Violation violation = new Violation(i, j, Violation.DEPENDENCY_DIVERGENCE);
							violationList.add(violation);
						}
						result += confidenceTable[i][j] * violationNum;
					}
					/**
					 * Detect absence violation
					 */
					else{
						int violationNum = countAbsenceViolation(gene, i, j, lowLevelNodeDependencyMatrix);
						if(violationNum != 0){
							Violation violation = new Violation(i, j, Violation.DEPENDENCY_ABSENCE);
							violationList.add(violation);
						}
						result += confidenceTable[i][j] * violationNum;
					}
				}
			}
		}
		
		return result;
	}
	
	private double computeStructureInheritanceViolation(Genotype gene){
		double result = 0;
		
		double[][] confidenceTable = Settings.extendConfidenceTable.convertToRawTable();
		
		for(int i=0; i<highLevelNodeInheritanceMatrix.length; i++){
			for(int j=0; j<highLevelNodeInheritanceMatrix.length; j++){
				if(i != j){
					/**
					 * Detect divergence violation
					 */
					if(highLevelNodeInheritanceMatrix[i][j] == 0){
						int violationNum = countDivergenceViolation(gene, i, j, lowLevelNodeInheritanceMatrix);
						if(violationNum != 0){
							Violation violation = new Violation(i, j, Violation.INHERITANCE_DIVERGENCE);
							violationList.add(violation);
						}
						result += confidenceTable[i][j] * violationNum;
					}
					/**
					 * Detect absence violation
					 */
					else{
						int violationNum = countAbsenceViolation(gene, i, j, lowLevelNodeInheritanceMatrix);
						if(violationNum != 0){
							Violation violation = new Violation(i, j, Violation.INHERITANCE_ABSENCE);
							violationList.add(violation);
						}
						result += confidenceTable[i][j] * violationNum;
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * There should be some dependency between caller module and callee module.
	 * 
	 * @param gene
	 * @param i
	 * @param j
	 * @return
	 */
	private int countAbsenceViolation(Genotype gene, int callerModuleIndex, int calleeModuleIndex, double[][] lowLevelNodeMatrix) {
		for(int i=0; i<gene.getLength(); i++){
			/**
			 * find any low level node i, which is mapped to caller module.
			 */
			if(gene.getDNA()[i] == callerModuleIndex){
				/**
				 * find any the low level node, j, called by node i.
				 */
				for(int j=0; j<lowLevelNodeMatrix.length; j++){
					if(lowLevelNodeMatrix[i][j] != 0){
						/**
						 * if node j is mapped to callee module, it means there is no violation.
						 */
						if(gene.getDNA()[j] == calleeModuleIndex){
							return 0;
						}
					}
				}
			}
		}
		/**
		 * find all the possible maps, there does not exsit such dependency, return 1 to mean a violatoin.
		 */
		return 1;
	}

	/**
	 * There should not be any dependency between caller module and callee module
	 * @param gene 
	 * @return
	 */
	private int countDivergenceViolation(Genotype gene, int callerModuleIndex, 
			int calleeModuleIndex, double[][] lowLevelNodeMatrix) {
		int num = 0;
		for(int i=0; i<gene.getLength(); i++){
			/**
			 * find any low level node i, which is mapped to caller module.
			 */
			if(gene.getDNA()[i] == callerModuleIndex){
				/**
				 * find any the low level node, j, called by node i.
				 */
				for(int j=0; j<lowLevelNodeMatrix.length; j++){
					if(lowLevelNodeMatrix[i][j] != 0){
						/**
						 * if node j is mapped to callee module, there is a violation.
						 */
						if(gene.getDNA()[j] == calleeModuleIndex){
							num += lowLevelNodeMatrix[i][j];
							//num++;
						}
					}
				}
			}
		}
		return num;
	}

	private double computeLexicalSimilarity(Genotype gene){
		double result = 0;
		
		for(int lowLevelNodeIndex=0; lowLevelNodeIndex<gene.getLength(); lowLevelNodeIndex++){
			int highLevelNodeIndex = gene.getDNA()[lowLevelNodeIndex];
			result += similarityTable[highLevelNodeIndex][lowLevelNodeIndex];
		}
		
		return result/gene.getLength();
	}

	@Override
	public boolean isFeasible() {
		return this.violationList.size() == 0;
	}

	/**
	 * @return the violationList
	 */
	public ArrayList<Violation> getViolationList() {
		return violationList;
	}

	/**
	 * @param violationList the violationList to set
	 */
	public void setViolationList(ArrayList<Violation> violationList) {
		this.violationList = violationList;
	}

	/**
	 * @return the similarityTable
	 */
	public double[][] getSimilarityTable() {
		return similarityTable;
	}

	/**
	 * @param similarityTable the similarityTable to set
	 */
	public void setSimilarityTable(double[][] similarityTable) {
		this.similarityTable = similarityTable;
	}

	/**
	 * @return the highLevelNodeDependencyMatrix
	 */
	public double[][] getHighLevelNodeDependencyMatrix() {
		return highLevelNodeDependencyMatrix;
	}

	/**
	 * @param highLevelNodeDependencyMatrix the highLevelNodeDependencyMatrix to set
	 */
	public void setHighLevelNodeDependencyMatrix(
			double[][] highLevelNodeDependencyMatrix) {
		this.highLevelNodeDependencyMatrix = highLevelNodeDependencyMatrix;
	}

	/**
	 * @return the lowLevelNodeDependencyMatrix
	 */
	public double[][] getLowLevelNodeDependencyMatrix() {
		return lowLevelNodeDependencyMatrix;
	}

	/**
	 * @param lowLevelNodeDependencyMatrix the lowLevelNodeDependencyMatrix to set
	 */
	public void setLowLevelNodeDependencyMatrix(
			double[][] lowLevelNodeDependencyMatrix) {
		this.lowLevelNodeDependencyMatrix = lowLevelNodeDependencyMatrix;
	}

	/**
	 * @return the highLevelNodeInheritanceMatrix
	 */
	public double[][] getHighLevelNodeInheritanceMatrix() {
		return highLevelNodeInheritanceMatrix;
	}

	/**
	 * @param highLevelNodeInheritanceMatrix the highLevelNodeInheritanceMatrix to set
	 */
	public void setHighLevelNodeInheritanceMatrix(
			double[][] highLevelNodeInheritanceMatrix) {
		this.highLevelNodeInheritanceMatrix = highLevelNodeInheritanceMatrix;
	}

	/**
	 * @return the lowLevelNodeInheritanceMatrix
	 */
	public double[][] getLowLevelNodeInheritanceMatrix() {
		return lowLevelNodeInheritanceMatrix;
	}

	/**
	 * @param lowLevelNodeInheritanceMatrix the lowLevelNodeInheritanceMatrix to set
	 */
	public void setLowLevelNodeInheritanceMatrix(
			double[][] lowLevelNodeInheritanceMatrix) {
		this.lowLevelNodeInheritanceMatrix = lowLevelNodeInheritanceMatrix;
	}

		

}
