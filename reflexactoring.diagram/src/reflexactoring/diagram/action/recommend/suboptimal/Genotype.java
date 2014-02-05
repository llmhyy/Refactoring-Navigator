/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import reflexactoring.diagram.util.Settings;
import Jama.Matrix;

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
		Matrix weightVector = computingFactor.getWeightVector();
		Matrix x0Vector = computingFactor.getX0Vector();
		
		
		
		double objectiveValue = getObjectiveValue(weightVector, x0Vector);
		int voilatedNum = getViolatedConstraintsNumber(computingFactor);
		
		return objectiveValue - voilatedNum;
	}
	
	private int getViolatedConstraintsNumber(FitnessComputingFactor computingFactor){
		
		Matrix relationMatrix = computingFactor.getRelationMatrix();
		Matrix lowLevelMatrix = computingFactor.getLowLevelMatrix();
		Matrix highLevelMatrix = computingFactor.getHighLevelMatrix();
		
		int violatedNum = 0;
		
		Matrix x = GeneticUtil.convertColumnVectorToMatrx(this.getDNA());
		
		Matrix edgeVertexMatrix;
		Matrix A_h;
		Matrix A_l;
		
		if(computingFactor.getEdgeVertexMatrix() == null){
			edgeVertexMatrix = convertToEdgeVertexMatrix(relationMatrix);
			int highLevelNum = relationMatrix.getRowDimension();
			//int lowLevelNum = relationMatrix.getColumnDimension();
			
			A_h = edgeVertexMatrix.getMatrix(0, highLevelNum-1, 0, edgeVertexMatrix.getColumnDimension()-1);
			A_l = edgeVertexMatrix.getMatrix(highLevelNum, edgeVertexMatrix.getRowDimension()-1, 0, edgeVertexMatrix.getColumnDimension()-1);
			
			computingFactor.setEdgeVertexMatrix(edgeVertexMatrix);
			computingFactor.setA_h(A_h);
			computingFactor.setA_l(A_l);
		}
		else{
			edgeVertexMatrix = computingFactor.getEdgeVertexMatrix();
			A_h = computingFactor.getA_h();
			A_l = computingFactor.getA_l();
		}
		
		/**
		 * A_h.x >= 1
		 */
		Matrix result = A_h.times(x);
		for(int i=0; i<result.getRowDimension(); i++){
			if(result.get(i, 0) < 1){
				violatedNum++;
			}
		}
		
		/**
		 * A_l.x = 1
		 */
		result = A_l.times(x);
		for(int i=0; i<result.getRowDimension(); i++){
			if(result.get(i, 0) != 1){
				violatedNum++;
			}
		}
		
		int count = 0;
		Matrix mappingMatrix = new Matrix(relationMatrix.getRowDimension(), relationMatrix.getColumnDimension());
		for(int i=0; i<relationMatrix.getRowDimension(); i++){
			for(int j=0; j<relationMatrix.getColumnDimension(); j++){
				if(relationMatrix.get(i, j) != 0){
					mappingMatrix.set(i, j, x.get(count++, 0));
				}
			}
		}
		
		/**
		 * R*L*R' ~ H
		 */
		result = mappingMatrix.times(lowLevelMatrix).times(mappingMatrix.transpose());
		for(int i=0; i<result.getRowDimension(); i++){
			for(int j=0; j<result.getColumnDimension(); j++){
				if(i!=j){
					if(highLevelMatrix.get(i, j) == 0 && result.get(i, j) != 0){
						violatedNum++;
					}
					else if(highLevelMatrix.get(i, j) != 0 && result.get(i, j) == 0){
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
	private Matrix convertToEdgeVertexMatrix(Matrix relationMatrix) {
		int highNum = relationMatrix.getRowDimension();
		int lowNum = relationMatrix.getColumnDimension();
		
		ArrayList<int[]> map = new ArrayList<>(); 
		for(int i=0; i<relationMatrix.getRowDimension(); i++){
			for(int j=0; j<relationMatrix.getColumnDimension(); j++){
				if(relationMatrix.get(i, j) != 0){
					map.add(new int[]{i, j});
				}
			}
		}
		
		Matrix edgeVertexMatrix = new Matrix(highNum+lowNum, map.size());
		for(int j=0; j<map.size(); j++){
			int moduleIndex = map.get(j)[0];
			int unitIndex = map.get(j)[1] + highNum;
			
			edgeVertexMatrix.set(moduleIndex, j, 1);
			edgeVertexMatrix.set(unitIndex, j, 1);
		}
		
		return edgeVertexMatrix;
	}




	private double getObjectiveValue(Matrix weightVector, Matrix x0Vector){
		
		int length = this.getDNA().length;
		
		Matrix x = GeneticUtil.convertColumnVectorToMatrx(this.getDNA());
		Matrix totalWeight = weightVector.times(x);
		
		double euclideanDis = computeEuclideanDistance(x, x0Vector);
		
		double objectiveValue = Settings.alpha*totalWeight.get(0, 0)/length
				+ Settings.beta*(1-euclideanDis/Math.sqrt(length));
		
		return objectiveValue;
	}
	
	private double computeEuclideanDistance(Matrix v1, Matrix v2){
		double[][] vector1 = v1.getArray();
		double[][] vector2 = v2.getArray();
		
		double sum = 0;
		for(int i=0; i<vector1.length; i++){
			sum += (vector1[i][0]-vector2[i][0])*(vector1[i][0]-vector2[i][0]);
		}
		
		return Math.sqrt(sum);
		
	}
}
