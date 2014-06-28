/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import reflexactoring.diagram.action.recommend.suboptimal.Violation;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class DefaultFitnessEvaluator implements FitnessEvaluator {
	
	private ArrayList<Violation> violationList = new ArrayList<>();
	private double[][] similarityTable;
	
	private double[][] highLevelNodeMatrix;
	private double[][] lowLevelNodeMatrix;
	
	public DefaultFitnessEvaluator(double[][] similarityTable, double[][] highLevelNodeMatrix, double[][] lowLevelNodeMatrix){
		this.setSimilarityTable(similarityTable);
		this.setHighLevelNodeMatrix(highLevelNodeMatrix);
		this.setLowLevelNodeMatrix(lowLevelNodeMatrix);
	}
	
	public double computeFitness(Genotype gene){
		double structureViolation = computeStructureViolation(gene);
		double lexicalSimilarity = computeLexicalSimilarity(gene);
		double refactoringEffort = computeRefactoringEffort(gene);
		return Double.valueOf(ReflexactoringUtil.getAlpha())*lexicalSimilarity
				+ Double.valueOf(ReflexactoringUtil.getBeta())*refactoringEffort
				- structureViolation;
	}
	
	private double computeStructureViolation(Genotype gene){
		double result = 0;
		
		double[][] confidenceTable = Settings.confidenceTable.convertToRawTable();
		
		for(int i=0; i<highLevelNodeMatrix.length; i++){
			for(int j=0; j<highLevelNodeMatrix.length; j++){
				if(i != j){
					/**
					 * Detect divergence violation
					 */
					if(highLevelNodeMatrix[i][j] == 0){
						int violationNum = countDivergenceViolation(gene, i, j);
						if(violationNum != 0){
							Violation violation = new Violation(i, j, Violation.DIVERGENCE);
							violationList.add(violation);
						}
						result += confidenceTable[i][j] * violationNum;
					}
					/**
					 * Detect absence violation
					 */
					else{
						int violationNum = countAbsenceViolation(gene, i, j);
						if(violationNum != 0){
							Violation violation = new Violation(i, j, Violation.ABSENCE);
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
	private int countAbsenceViolation(Genotype gene, int callerModuleIndex, int calleeModuleIndex) {
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
	private int countDivergenceViolation(Genotype gene, int callerModuleIndex, int calleeModuleIndex) {
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
							num++;
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
	
	private double computeRefactoringEffort(Genotype gene){
		double result = 0;
		for(int i=0; i<gene.getLength(); i++){
			if(gene.getDNA()[i] == gene.getOriginalDNA()[i]){
				result++;
			}
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
	 * @param highLevelNodeMatrix the highLevelNodeMatrix to set
	 */
	public void setHighLevelNodeMatrix(double[][] highLevelNodeMatrix) {
		this.highLevelNodeMatrix = highLevelNodeMatrix;
	}

	/**
	 * @param lowLevelNodeMatrix the lowLevelNodeMatrix to set
	 */
	public void setLowLevelNodeMatrix(double[][] lowLevelNodeMatrix) {
		this.lowLevelNodeMatrix = lowLevelNodeMatrix;
	}

	/**
	 * @return the highLevelNodeMatrix
	 */
	public double[][] getHighLevelNodeMatrix() {
		return highLevelNodeMatrix;
	}

	/**
	 * @return the lowLevelNodeMatrix
	 */
	public double[][] getLowLevelNodeMatrix() {
		return lowLevelNodeMatrix;
	}
	
	

}
