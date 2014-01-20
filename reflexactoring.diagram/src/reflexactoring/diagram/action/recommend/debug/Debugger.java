/**
 * 
 */
package reflexactoring.diagram.action.recommend.debug;

import reflexactoring.diagram.action.recommend.Optimizer.SparseVectors;

/**
 * @author linyun
 *
 */
public class Debugger {
	
	private static final int ROW = 0;
	private static final int COLUMN = 1;
	
	public static String printInputValues(Double[] weightVector, int h, int l,
			SparseVectors highLevelVectors, SparseVectors lowLevelVectors, SparseVectors relationVectors,
			Integer[] x0Vector){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("w=");
		buffer.append(printDoubleVector(weightVector, ROW));
		buffer.append("\n");
		
		buffer.append("x0=");
		buffer.append(printIntegerVector(x0Vector, COLUMN));
		buffer.append("\n");
		
		buffer.append("h="+h+"\n");
		buffer.append("l="+l+"\n");
		
		buffer.append("i_h=");
		buffer.append(printSparseVector(highLevelVectors.getI(), ROW));
		buffer.append("\n");
		
		buffer.append("j_h=");
		buffer.append(printSparseVector(highLevelVectors.getJ(), ROW));
		buffer.append("\n");
		
		buffer.append("i_l=");
		buffer.append(printSparseVector(lowLevelVectors.getI(), ROW));
		buffer.append("\n");
		
		buffer.append("j_l=");
		buffer.append(printSparseVector(lowLevelVectors.getJ(), ROW));
		buffer.append("\n");
		
		buffer.append("i_r=");
		buffer.append(printSparseVector(relationVectors.getI(), ROW));
		buffer.append("\n");
		
		buffer.append("j_r=");
		buffer.append(printSparseVector(relationVectors.getJ(), ROW));
		buffer.append("\n");
		
		
		return buffer.toString();
	}
	
	private static String printDoubleVector(Double[] array, int style){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for(Double d: array){
			if(style == ROW){
				buffer.append(d + " ");				
			}
			else if(style == COLUMN){
				buffer.append(d + ";");
			}
		}
		buffer.append("]");
		return buffer.toString();
	}
	
	private static String printIntegerVector(Integer[] array, int style){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for(Integer d: array){
			if(style == ROW){
				buffer.append(d + " ");				
			}
			else if(style == COLUMN){
				buffer.append(d + ";");
			}
		}
		buffer.append("]");
		return buffer.toString();
	}
	
	private static String printSparseVector(Integer[] array, int style){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for(Integer d: array){
			if(style == ROW){
				buffer.append((d+1) + " ");				
			}
			else if(style == COLUMN){
				buffer.append((d+1) + ";");
			}
		}
		buffer.append("]");
		return buffer.toString();
	}
	
}
