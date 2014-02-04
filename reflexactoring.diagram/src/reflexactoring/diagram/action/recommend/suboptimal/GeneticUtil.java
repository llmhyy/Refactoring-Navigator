/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import Jama.Matrix;

/**
 * @author linyun
 *
 */
public class GeneticUtil {
	public static Matrix convertRowVectorToMatrix(ArrayList<Double> list){
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
	}
}
