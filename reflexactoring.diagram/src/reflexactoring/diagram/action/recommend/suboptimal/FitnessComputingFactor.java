/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import Jama.Matrix;

/**
 * @author linyun
 * 
 */
public class FitnessComputingFactor {
	private Matrix weightVector;
	private Matrix x0Vector;
	private Matrix relationMatrix;
	private Matrix lowLevelMatrix;
	private Matrix highLevelMatrix;

	private Matrix edgeVertexMatrix;
	private Matrix A_h;
	private Matrix A_l;

	/**
	 * @return the weightVector
	 */
	public Matrix getWeightVector() {
		return weightVector;
	}

	/**
	 * @param weightVector
	 *            the weightVector to set
	 */
	public void setWeightVector(Matrix weightVector) {
		this.weightVector = weightVector;
	}

	/**
	 * @return the x0Vector
	 */
	public Matrix getX0Vector() {
		return x0Vector;
	}

	/**
	 * @param x0Vector
	 *            the x0Vector to set
	 */
	public void setX0Vector(Matrix x0Vector) {
		this.x0Vector = x0Vector;
	}

	/**
	 * @return the relationMatrix
	 */
	public Matrix getRelationMatrix() {
		return relationMatrix;
	}

	/**
	 * @param relationMatrix
	 *            the relationMatrix to set
	 */
	public void setRelationMatrix(Matrix relationMatrix) {
		this.relationMatrix = relationMatrix;
	}

	/**
	 * @return the lowLevelMatrix
	 */
	public Matrix getLowLevelMatrix() {
		return lowLevelMatrix;
	}

	/**
	 * @param lowLevelMatrix
	 *            the lowLevelMatrix to set
	 */
	public void setLowLevelMatrix(Matrix lowLevelMatrix) {
		this.lowLevelMatrix = lowLevelMatrix;
	}

	/**
	 * @return the highLevelMatrix
	 */
	public Matrix getHighLevelMatrix() {
		return highLevelMatrix;
	}

	/**
	 * @param highLevelMatrix
	 *            the highLevelMatrix to set
	 */
	public void setHighLevelMatrix(Matrix highLevelMatrix) {
		this.highLevelMatrix = highLevelMatrix;
	}

	/**
	 * @return the edgeVertexMatrix
	 */
	public Matrix getEdgeVertexMatrix() {
		return edgeVertexMatrix;
	}

	/**
	 * @param edgeVertexMatrix
	 *            the edgeVertexMatrix to set
	 */
	public void setEdgeVertexMatrix(Matrix edgeVertexMatrix) {
		this.edgeVertexMatrix = edgeVertexMatrix;
	}

	/**
	 * @return the a_h
	 */
	public Matrix getA_h() {
		return A_h;
	}

	/**
	 * @param a_h
	 *            the a_h to set
	 */
	public void setA_h(Matrix a_h) {
		A_h = a_h;
	}

	/**
	 * @return the a_l
	 */
	public Matrix getA_l() {
		return A_l;
	}

	/**
	 * @param a_l
	 *            the a_l to set
	 */
	public void setA_l(Matrix a_l) {
		A_l = a_l;
	}

}
