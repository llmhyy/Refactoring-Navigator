/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import reflexactoring.diagram.util.Settings;

/**
 * This class is used for DNA, or gene, for genetic algorithm.
 * 
 * @author linyun
 *
 */
public class Genotype {
	int[] DNA;
	double fitness;
	
	public Genotype(){
		
	}

	/**
	 * @param dNA
	 */
	public Genotype(int[] dNA) {
		super();
		DNA = dNA;
	}

	/**
	 * @param dNA
	 * @param fitness
	 */
	public Genotype(int[] dNA, double fitness) {
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
	
	public boolean isFeasible(){
		return fitness >= 0;
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
	
	public double computeFitness(FitnessComputingFactor computingFactor){
		SparseDoubleMatrix1D weightVector = computingFactor.getWeightVector();
		SparseDoubleMatrix1D x0Vector = computingFactor.getX0Vector();
		
		
		
		double objectiveValue = getObjectiveValue(weightVector, x0Vector);
		int voilatedNum = getViolatedConstraintsNumber(computingFactor);
		
		return objectiveValue - voilatedNum;
	}
	
	private int getViolatedConstraintsNumber(FitnessComputingFactor computingFactor){
		
		SparseDoubleMatrix2D relationMatrix = computingFactor.getRelationMatrix();
		SparseDoubleMatrix2D lowLevelMatrix = computingFactor.getLowLevelMatrix();
		SparseDoubleMatrix2D highLevelMatrix = computingFactor.getHighLevelMatrix();
		
		int violatedNum = 0;
		
		SparseDoubleMatrix1D x = GeneticUtil.convertArrayToVector(this.getDNA());
		
		SparseDoubleMatrix2D edgeVertexMatrix;
		SparseDoubleMatrix2D A_h;
		SparseDoubleMatrix2D A_l;
		
		if(computingFactor.getEdgeVertexMatrix() == null){
			edgeVertexMatrix = convertToEdgeVertexMatrix(relationMatrix);
			int highLevelNum = relationMatrix.rows();
			//int lowLevelNum = relationMatrix.getColumnDimension();
			
			Algebra alg = new Algebra();
			A_h = (SparseDoubleMatrix2D) alg.subMatrix(edgeVertexMatrix, 0, highLevelNum-1, 0, edgeVertexMatrix.columns()-1);
			A_l = (SparseDoubleMatrix2D) alg.subMatrix(edgeVertexMatrix, highLevelNum, edgeVertexMatrix.rows()-1, 0, edgeVertexMatrix.columns()-1);
			//A_h = edgeVertexMatrix.getMatrix(0, highLevelNum-1, 0, edgeVertexMatrix.columns()-1);
			//A_l = edgeVertexMatrix.getMatrix(highLevelNum, edgeVertexMatrix.getRowDimension()-1, 0, edgeVertexMatrix.getColumnDimension()-1);
			
			computingFactor.setEdgeVertexMatrix(edgeVertexMatrix);
			computingFactor.setA_h(A_h);
			computingFactor.setA_l(A_l);
		}
		else{
			edgeVertexMatrix = computingFactor.getEdgeVertexMatrix();
			A_h = computingFactor.getA_h();
			A_l = computingFactor.getA_l();
		}
		
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
		
		int count = 0;
		SparseDoubleMatrix2D mappingMatrix = new SparseDoubleMatrix2D(relationMatrix.rows(), relationMatrix.columns());
		for(int i=0; i<relationMatrix.rows(); i++){
			for(int j=0; j<relationMatrix.columns(); j++){
				if(relationMatrix.get(i, j) != 0){
					mappingMatrix.set(i, j, x.get(count++));
				}
			}
		}
		
		/**
		 * R*L*R' ~ H
		 */
		
		SparseDoubleMatrix2D tmp = new SparseDoubleMatrix2D(mappingMatrix.rows(), lowLevelMatrix.columns());
		tmp = (SparseDoubleMatrix2D) mappingMatrix.zMult(lowLevelMatrix, tmp, 1, 0, false, false);
		
		SparseDoubleMatrix2D softConstaintResult = new SparseDoubleMatrix2D(mappingMatrix.rows(), mappingMatrix.rows());
		softConstaintResult = (SparseDoubleMatrix2D)tmp.zMult(alg.transpose(mappingMatrix), softConstaintResult, 1, 0, false, false);	
				
		for(int i=0; i<softConstaintResult.rows(); i++){
			for(int j=0; j<softConstaintResult.columns(); j++){
				if(i!=j){
					if(highLevelMatrix.get(i, j) == 0 && softConstaintResult.get(i, j) != 0){
						violatedNum++;
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
	 * @param relationMatrix
	 * @return
	 */
	private SparseDoubleMatrix2D convertToEdgeVertexMatrix(SparseDoubleMatrix2D relationMatrix) {
		int highNum = relationMatrix.rows();
		int lowNum = relationMatrix.columns();
		
		ArrayList<int[]> map = new ArrayList<>(); 
		for(int i=0; i<relationMatrix.rows(); i++){
			for(int j=0; j<relationMatrix.columns(); j++){
				if(relationMatrix.get(i, j) != 0){
					map.add(new int[]{i, j});
				}
			}
		}
		
		SparseDoubleMatrix2D edgeVertexMatrix = new SparseDoubleMatrix2D(highNum+lowNum, map.size());
		for(int j=0; j<map.size(); j++){
			int moduleIndex = map.get(j)[0];
			int unitIndex = map.get(j)[1] + highNum;
			
			edgeVertexMatrix.set(moduleIndex, j, 1);
			edgeVertexMatrix.set(unitIndex, j, 1);
		}
		
		return edgeVertexMatrix;
	}




	private double getObjectiveValue(SparseDoubleMatrix1D weightVector, SparseDoubleMatrix1D x0Vector){
		
		int length = this.getDNA().length;
		
		double totalWeight = weightVector.zDotProduct(x0Vector);
		
		double euclideanDis = computeEuclideanDistance(GeneticUtil.convertArrayToVector(this.getDNA()), x0Vector);
		
		double objectiveValue = Settings.alpha*totalWeight/length
				+ Settings.beta*(1-euclideanDis/Math.sqrt(length));
		
		return objectiveValue;
	}
	
	private double computeEuclideanDistance(SparseDoubleMatrix1D v1, SparseDoubleMatrix1D v2){
		
		double sum = 0;
		for(int i=0; i<v1.size(); i++){
			sum += (v1.get(i)-v2.get(i))*(v1.get(i)-v2.get(i));
		}
		
		return Math.sqrt(sum);
		
	}
}
