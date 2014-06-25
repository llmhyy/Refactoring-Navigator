/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimalold_representation;

import java.util.ArrayList;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;

/**
 * @author linyun
 *
 */
public class GeneticUtil {
	
	public static SparseDoubleMatrix1D convertVectorToMatrix(ArrayList<Double> list){
		SparseDoubleMatrix1D matrix = new SparseDoubleMatrix1D(list.size());
		for(int i=0; i<list.size(); i++){
			
			double entry = list.get(i);
			
			if(entry != 0){
				matrix.set(i, entry);
			}
			
		}
		
		return matrix;
	}
	
	public static SparseDoubleMatrix1D convertArrayToVector(int[] list){
		SparseDoubleMatrix1D matrix = new SparseDoubleMatrix1D(list.length);
		for(int i=0; i<list.length; i++){
			double entry = list[i];
			
			if(entry != 0){
				matrix.set(i, entry);
			}
		}
		
		return matrix;
	}
	
	/*public static Matrix convertRowVectorToMatrix(ArrayList<Double> list){
		Matrix matrix = new Matrix(1, list.size());
		for(int i=0; i<list.size(); i++){
			matrix.set(0, i, list.get(i));
		}
		
		return matrix;
	}
	
	public static Matrix convertColumnVectorToMatrx(ArrayList<Integer> list){
		Matrix matrix = new Matrix(list.size(), 1);
		for(int i=0; i<list.size(); i++){
			matrix.set(i, 0, list.get(i));
		}
		
		return matrix;
	}
	
	public static Matrix convertColumnVectorToMatrx(int[] list){
		Matrix matrix = new Matrix(list.length, 1);
		for(int i=0; i<list.length; i++){
			matrix.set(i, 0, list[i]);
		}
		
		return matrix;
	}*/
}
