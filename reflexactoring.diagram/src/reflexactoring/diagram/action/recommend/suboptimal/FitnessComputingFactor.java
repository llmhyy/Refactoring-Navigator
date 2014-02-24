/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import reflexactoring.Activator;
import reflexactoring.diagram.util.ReflexactoringUtil;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

/**
 * @author linyun
 * 
 */
public class FitnessComputingFactor {
	
	public static int PLUGIN_MODE = 0;
	public static int EXPERIMENT_MODE = 1;
	
	private int mode = PLUGIN_MODE;
	
	private double alpha;
	private double beta;
	
	private SparseDoubleMatrix1D weightVector;
	private SparseDoubleMatrix1D x0Vector;
	private SparseDoubleMatrix2D relationMatrix;
	private SparseDoubleMatrix2D lowLevelMatrix;
	private SparseDoubleMatrix2D highLevelMatrix;

	private SparseDoubleMatrix2D edgeVertexMatrix;
	private SparseDoubleMatrix2D A_h;
	private SparseDoubleMatrix2D A_l;

	/**
	 * @return the weightVector
	 */
	public SparseDoubleMatrix1D getWeightVector() {
		return weightVector;
	}

	/**
	 * @param weightVector
	 *            the weightVector to set
	 */
	public void setWeightVector(SparseDoubleMatrix1D weightVector) {
		this.weightVector = weightVector;
	}

	/**
	 * @return the x0Vector
	 */
	public SparseDoubleMatrix1D getX0Vector() {
		return x0Vector;
	}

	/**
	 * @param x0Vector
	 *            the x0Vector to set
	 */
	public void setX0Vector(SparseDoubleMatrix1D x0Vector) {
		this.x0Vector = x0Vector;
	}

	/**
	 * @return the relationMatrix
	 */
	public SparseDoubleMatrix2D getRelationMatrix() {
		return relationMatrix;
	}

	/**
	 * @param relationMatrix
	 *            the relationMatrix to set
	 */
	public void setRelationMatrix(SparseDoubleMatrix2D relationMatrix) {
		this.relationMatrix = relationMatrix;
	}

	/**
	 * @return the lowLevelMatrix
	 */
	public SparseDoubleMatrix2D getLowLevelMatrix() {
		return lowLevelMatrix;
	}

	/**
	 * @param lowLevelMatrix
	 *            the lowLevelMatrix to set
	 */
	public void setLowLevelMatrix(SparseDoubleMatrix2D lowLevelMatrix) {
		this.lowLevelMatrix = lowLevelMatrix;
	}

	/**
	 * @return the highLevelMatrix
	 */
	public SparseDoubleMatrix2D getHighLevelMatrix() {
		return highLevelMatrix;
	}

	/**
	 * @param highLevelMatrix
	 *            the highLevelMatrix to set
	 */
	public void setHighLevelMatrix(SparseDoubleMatrix2D highLevelMatrix) {
		this.highLevelMatrix = highLevelMatrix;
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
	
	public void initializeEdgeVertexMatrix(){
		this.edgeVertexMatrix = convertToEdgeVertexMatrix(relationMatrix);
		int highLevelNum = relationMatrix.rows();
		
		Algebra alg = new Algebra();
		this.A_h = (SparseDoubleMatrix2D) alg.subMatrix(edgeVertexMatrix, 0, highLevelNum-1, 0, edgeVertexMatrix.columns()-1);
		this.A_l = (SparseDoubleMatrix2D) alg.subMatrix(edgeVertexMatrix, highLevelNum, edgeVertexMatrix.rows()-1, 0, edgeVertexMatrix.columns()-1);
	}

	/**
	 * @return the a_h
	 */
	public SparseDoubleMatrix2D getA_h() {
		return A_h;
	}

	/**
	 * @param a_h
	 *            the a_h to set
	 */
	public void setA_h(SparseDoubleMatrix2D a_h) {
		A_h = a_h;
	}

	/**
	 * @return the a_l
	 */
	public SparseDoubleMatrix2D getA_l() {
		return A_l;
	}

	/**
	 * @param a_l
	 *            the a_l to set
	 */
	public void setA_l(SparseDoubleMatrix2D a_l) {
		A_l = a_l;
	}

	
	/**
	 * since the fitness function will be negative, therefore, I normalize the value
	 * to positive by adding the number of all constraints, that is, h+l+(h^2-h)=l+h^2.
	 * 
	 * By this means, all the fitness value will be positive.
	 * 
	 * @param fitness
	 * @param highNum
	 * @param lowNum
	 * @return
	 */
	public double normalizeFitnessValue(double fitness, int highNum, int lowNum){
		return fitness + highNum*highNum + lowNum;
	}

	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	public double getAlpha(){
		if(Activator.getDefault() == null){
			return this.alpha;			
		}
		else{
			return Double.valueOf(ReflexactoringUtil.getAlpha());
		}
	}

	/**
	 * @return the beta
	 */
	public double getBeta() {
		if(Activator.getDefault() == null){
			return beta;			
		}
		else{
			return Double.valueOf(ReflexactoringUtil.getBeta());
		}
		
	}

	/**
	 * @param beta the beta to set
	 */
	public void setBeta(double beta) {
		this.beta = beta;
	}
}
