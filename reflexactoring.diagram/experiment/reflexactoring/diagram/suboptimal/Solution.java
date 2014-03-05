/**
 * 
 */
package reflexactoring.diagram.suboptimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import reflexactoring.diagram.action.recommend.suboptimal.FitnessComputingFactor;
import reflexactoring.diagram.action.recommend.suboptimal.GeneticUtil;
import reflexactoring.diagram.action.recommend.suboptimal.Genotype;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * This class is used for DNA, or gene, for genetic algorithm.
 * 
 * @author linyun
 *
 */
public class Solution extends Genotype{
	
	private int[] DNA;
	
	/**
	 * The previous R in R*L*R'~H.
	 */
	//private SparseDoubleMatrix2D previousMappingMatrix;
	//private SparseDoubleMatrix2D previousTmpMatrix;
	
	private SparseDoubleMatrix2D mappingMatrix;
	private SparseDoubleMatrix2D tmpMatrix;
	
	private double fitness;
	
	public Solution(){
		
	}

	/**
	 * @param dNA
	 */
	public Solution(int[] dNA) {
		super();
		DNA = dNA;
	}

	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(DNA);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Solution other = (Solution) obj;
		if (!Arrays.equals(DNA, other.DNA))
			return false;
		return true;
	}
	
	@Override
	public Solution clone(){
		Solution clonedGene = new Solution(this.DNA);
		
		SparseDoubleMatrix2D tmpMatrix = this.getTmpMatrix();
		SparseDoubleMatrix2D mappingMatrix = this.getMappingMatrix();
		
		clonedGene.setTmpMatrix(tmpMatrix);
		clonedGene.setMappingMatrix(mappingMatrix);
		clonedGene.setFitness(this.fitness);
		
		return clonedGene;
	}

	/**
	 * @param dNA
	 * @param fitness
	 */
	public Solution(int[] dNA, double fitness) {
		super();
		DNA = dNA;
		this.fitness = fitness;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<DNA.length; i++){
			buffer.append(DNA[i]);
		}
		return buffer.toString();
	}

	/**
	 * @return the dNA
	 */
	public int[] getDNA() {
		return DNA;
	}

	/**
	 * @param dNA the dNA to set
	 */
	public void setDNA(int[] dNA) {
		DNA = dNA;
	}

	/**
	 * @return the fitness
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * @param fitness the fitness to set
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	/**
	 * The consequence of invoking this method:
	 * 1) the fitness of gene will be set.
	 * 2) the tmpMatrix and mappingMatrix will be set as well.
	 * @param computingFactor
	 */
	public void computeFitness(FitnessComputingFactor computingFactor){
		SparseDoubleMatrix1D weightVector = computingFactor.getWeightVector();
		SparseDoubleMatrix1D x0Vector = computingFactor.getX0Vector();
		
		double objectiveValue = getObjectiveValue(weightVector, x0Vector, computingFactor);
		double voilatedNum = getViolatedConstraintsNumber(computingFactor);
		this.fitness = objectiveValue - voilatedNum;
		
	}
	
	private double getViolatedConstraintsNumber(FitnessComputingFactor computingFactor){
		
		SparseDoubleMatrix2D relationMatrix = computingFactor.getRelationMatrix();
		SparseDoubleMatrix2D lowLevelMatrix = computingFactor.getLowLevelMatrix();
		SparseDoubleMatrix2D highLevelMatrix = computingFactor.getHighLevelMatrix();
		
		double violatedNum = 0;
		
		SparseDoubleMatrix1D x = GeneticUtil.convertArrayToVector(this.getDNA());
		
		//SparseDoubleMatrix2D edgeVertexMatrix = computingFactor.getEdgeVertexMatrix();
		SparseDoubleMatrix2D A_h = computingFactor.getA_h();
		SparseDoubleMatrix2D A_l = computingFactor.getA_l();
		
		Algebra alg = new Algebra();
		/**
		 * A_h.x >= 1
		 */
		//result = A_h.times(x);
		//SparseDoubleMatrix1D result = new SparseDoubleMatrix1D(A_h.rows());
		//result = (SparseDoubleMatrix1D) A_h.zMult(x, result, 1, 0, false);
		DoubleMatrix1D result_h = alg.mult(A_h, x);
		for(int i=0; i<result_h.size(); i++){
			if(result_h.get(i) < 1){
				violatedNum++;
			}
		}
		
		/**
		 * A_l.x = 1
		 */
		//result = A_l.times(x);
		//result = new SparseDoubleMatrix1D(A_l.rows());
		//result = (SparseDoubleMatrix1D) A_l.zMult(x, result, 1, 0, false);
		DoubleMatrix1D result_l = alg.mult(A_l, x);
		for(int i=0; i<result_l.size(); i++){
			if(result_l.get(i) != 1){
				violatedNum++;
			}
		}
		
		/*if(violatedNum > 0){
			System.err.println("should not violate hard constraints here");
		}*/
		
		
		ArrayList<HighLevelLowLevelMap> maps = new ArrayList<>();
		int count = 0;
		SparseDoubleMatrix2D mappingMatrix = new SparseDoubleMatrix2D(relationMatrix.rows(), relationMatrix.columns());
		for(int i=0; i<relationMatrix.rows(); i++){
			for(int j=0; j<relationMatrix.columns(); j++){
				if(relationMatrix.get(i, j) != 0){
					mappingMatrix.set(i, j, x.get(count++));
					
					/**
					 * If we have recorded the previous mapping matrix, we need to compute the delta between new and previous
					 * mapping in order to compute the fitness function more efficiently.
					 */
					if(this.mappingMatrix != null){
						if(this.mappingMatrix.get(i, j) != mappingMatrix.get(i, j)){
							int type = (this.mappingMatrix.get(i, j) > mappingMatrix.get(i, j))? 
									HighLevelLowLevelMap.REMOVED : HighLevelLowLevelMap.ADDED ;
							
							HighLevelLowLevelMap map = new HighLevelLowLevelMap(i, j, type);
							maps.add(map);
						}
					}
				}
			}
		}
		
		/**
		 * R*L*R' ~ H
		 */
		/*long t11 = System.currentTimeMillis();
		SparseDoubleMatrix2D tmp = new SparseDoubleMatrix2D(lowLevelMatrix.columns(), mappingMatrix.rows());
		tmp = (SparseDoubleMatrix2D) alg.transpose(lowLevelMatrix).zMult(alg.transpose(mappingMatrix), tmp, 1, 0, false, false);
		tmp = (SparseDoubleMatrix2D) alg.transpose(tmp);
		long t12 = System.currentTimeMillis();
		System.out.println(t12-t11);*/
		
		/*SparseDoubleMatrix2D tmp = new SparseDoubleMatrix2D(mappingMatrix.rows(), lowLevelMatrix.columns());
		tmp = (SparseDoubleMatrix2D) mappingMatrix.zMult(lowLevelMatrix, tmp, 1, 0, false, false);*/
		
		SparseDoubleMatrix2D tmp; 
		if(this.tmpMatrix == null || this.mappingMatrix == null){
			tmp = new SparseDoubleMatrix2D(mappingMatrix.rows(), lowLevelMatrix.columns());
			tmp = (SparseDoubleMatrix2D) mappingMatrix.zMult(lowLevelMatrix, tmp, 1, 0, false, false);
		}
		/**
		 * perform incremental computation.
		 */
		else{
			
			/*long t11 = System.currentTimeMillis();
			SparseDoubleMatrix2D tmp0 = new SparseDoubleMatrix2D(mappingMatrix.rows(), lowLevelMatrix.columns());
			tmp0 = (SparseDoubleMatrix2D) mappingMatrix.zMult(lowLevelMatrix, tmp0, 1, 0, false, false);
			long t12 = System.currentTimeMillis();
			System.out.println(t12-t11);*/
			
			//long t21 = System.currentTimeMillis();
			tmp = incrementalCompute(this.tmpMatrix, lowLevelMatrix, maps);
			//long t22 = System.currentTimeMillis();
			//System.out.println(t22-t21);
			
			//System.currentTimeMillis();
			
			/*for(int i=0; i<tmp.rows(); i++){
				for(int j=0; j<tmp.columns(); j++){
					if(tmp0.get(i, j) != tmp.get(i, j)){
						System.currentTimeMillis();
					}
				}
			}*/
			
			//tmp = incrementalCompute(previousTmpMatrix, lowLevelMatrix, maps);
			
		}
		
		
		this.tmpMatrix = tmp;
		this.mappingMatrix = mappingMatrix;
		
		SparseDoubleMatrix2D softConstaintResult = new SparseDoubleMatrix2D(mappingMatrix.rows(), mappingMatrix.rows());
		softConstaintResult = (SparseDoubleMatrix2D)tmp.zMult(alg.transpose(mappingMatrix), softConstaintResult, 1, 0, false, false);	
		
		for(int i=0; i<softConstaintResult.rows(); i++){
			for(int j=0; j<softConstaintResult.columns(); j++){
				if(i!=j){
					if(highLevelMatrix.get(i, j) == 0 && softConstaintResult.get(i, j) != 0){
						
						double vio = softConstaintResult.get(i, j);
						
						violatedNum += vio;
					}
					else if(highLevelMatrix.get(i, j) != 0 && softConstaintResult.get(i, j) == 0){
						violatedNum++;
					}
				}
			}
		}
		return violatedNum;
	}
	
	/**
	 * @param tmpMatrix2
	 * @param maps
	 * @return
	 */
	private SparseDoubleMatrix2D incrementalCompute(
			SparseDoubleMatrix2D tmpMatrix, SparseDoubleMatrix2D lowLevelMatrix,
			ArrayList<HighLevelLowLevelMap> maps) {
		if(maps.size() == 0){
			return tmpMatrix;
		}
		else{
			for(int i=0; i<tmpMatrix.rows(); i++){
				ArrayList<HighLevelLowLevelMap> changingEntryInThisRow = new ArrayList<>();
				for(HighLevelLowLevelMap map: maps){
					if(map.i == i){
						changingEntryInThisRow.add(map);
					}
				}
				
				if(changingEntryInThisRow.size() == 0){
					continue;
				}
				
				for(int j=0; j<tmpMatrix.columns(); j++){
					int deltaSum = 0;
					for(HighLevelLowLevelMap changingMap: changingEntryInThisRow){
						double valueInTmpMatrix = lowLevelMatrix.get(changingMap.j, j);
						if(valueInTmpMatrix == 1){
							int delta = (changingMap.type == HighLevelLowLevelMap.ADDED)?
									1 : -1;
							deltaSum += delta;
						}
					}
					
					double value = tmpMatrix.get(i, j);
					tmpMatrix.set(i, j, value+deltaSum);
				}
			}
			
			return tmpMatrix;
		}
	}

	class HighLevelLowLevelMap{
		public int i;
		public int j;
		public int type;
		
		public final static int REMOVED = 1;
		public final static int ADDED = 2;
		/**
		 * @param i
		 * @param j
		 * @param type
		 */
		public HighLevelLowLevelMap(int i, int j, int type) {
			super();
			this.i = i;
			this.j = j;
			this.type = type;
		}
	}

	private double getObjectiveValue(SparseDoubleMatrix1D weightVector, SparseDoubleMatrix1D x0Vector,
			FitnessComputingFactor computingFactor){
		
		int length = this.getDNA().length;
		
		SparseDoubleMatrix1D xVector = computingFactor.convertToVectorMatrix(this.getDNA());
		
		double totalWeight = weightVector.zDotProduct(xVector);
		
		double euclideanDis = computeEuclideanDistance(GeneticUtil.convertArrayToVector(this.getDNA()), x0Vector);
		
		double objectiveValue = computingFactor.getAlpha()*totalWeight/length
				+ computingFactor.getBeta()*(1-euclideanDis/Math.sqrt(length));
		
		return objectiveValue;
	}
	
	private double computeEuclideanDistance(SparseDoubleMatrix1D v1, SparseDoubleMatrix1D v2){
		
		double sum = 0;
		for(int i=0; i<v1.size(); i++){
			sum += (v1.get(i)-v2.get(i))*(v1.get(i)-v2.get(i));
		}
		
		return Math.sqrt(sum);
		
	}


	/**
	 * @return the tmpMatrix
	 */
	public SparseDoubleMatrix2D getTmpMatrix() {
		
		if(tmpMatrix == null)return null;
		
		SparseDoubleMatrix2D matrix = new SparseDoubleMatrix2D(tmpMatrix.rows(), tmpMatrix.columns());
		for(int i=0; i<matrix.rows(); i++){
			for(int j=0; j<matrix.columns(); j++){
				matrix.set(i, j, tmpMatrix.get(i, j));
			}
		}
		return matrix;
	}
	
	private void setTmpMatrix(SparseDoubleMatrix2D tmpMatrix){
		this.tmpMatrix = tmpMatrix;
	}
	
	/**
	 * @return the mappingMatrix
	 */
	public SparseDoubleMatrix2D getMappingMatrix() {
		if(mappingMatrix == null)return null;
		
		SparseDoubleMatrix2D matrix = new SparseDoubleMatrix2D(mappingMatrix.rows(), mappingMatrix.columns());
		for(int i=0; i<matrix.rows(); i++){
			for(int j=0; j<matrix.columns(); j++){
				matrix.set(i, j, mappingMatrix.get(i, j));
			}
		}
		return matrix;
	}
	
	private void setMappingMatrix(SparseDoubleMatrix2D mappingMatrix){
		this.mappingMatrix = mappingMatrix;
	}

	/**
	 * @param previousMappingMatrix the previousMappingMatrix to set
	 *//*
	public void setPreviousMappingMatrix(SparseDoubleMatrix2D previousMappingMatrix) {
		this.previousMappingMatrix = previousMappingMatrix;
	}
	
	*//**
	 * @param previoustTmpMatrix the previoustTmpMatrix to set
	 *//*
	public void setPreviousTmpMatrix(SparseDoubleMatrix2D previoustTmpMatrix) {
		this.previousTmpMatrix = previoustTmpMatrix;
	}*/

	
	
	
}
