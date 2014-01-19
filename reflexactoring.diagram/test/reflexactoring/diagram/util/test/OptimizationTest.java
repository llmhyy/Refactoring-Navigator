/**
 * 
 */
package reflexactoring.diagram.util.test;

import org.junit.Test;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import fudan.se.graphmatching.optimization.OptimalGraphMatcher;

/**
 * @author linyun
 *
 */
public class OptimizationTest {

	@Test
	public void test() {
		MWNumericArray weightMatrix = null;
		MWNumericArray x0Matrix = null;
		MWNumericArray highLevelMatrix = null;
		MWNumericArray lowLevelMatrix = null;
		
		MWNumericArray i_hMatrix = null;
		MWNumericArray j_hMatrix = null;
		MWNumericArray i_lMatrix = null;
		MWNumericArray j_lMatrix = null;
		MWNumericArray i_rMatrix = null;
		MWNumericArray j_rMatrix = null;
		
		Object[] inputs = null;
		Object[] results = null;
		
		
		try{
			double[] weightVector = {0.5, 0.7, 0.3, 0.5, 0.5, 0.7, 0.6};
			double[] initialVector = {0, 0, 0, 0, 0, 0, 0};
			int h = 3;
			int l = 4;
			
			double[] i_h = {1};
			double[] j_h = {2};
			double[] i_l = {1, 2};
			double[] j_l = {2, 3};
			double[] i_r = {1, 1, 1, 2, 2, 3, 3};
			double[] j_r = {1, 2, 3, 2, 3, 3, 4};
			
			/**
			 * The first parameter int[] specify the dimensions of a matrix.
			 */
			weightMatrix = MWNumericArray.newInstance(new int[]{1, weightVector.length}, MWClassID.DOUBLE, MWComplexity.REAL);
			x0Matrix = MWNumericArray.newInstance(new int[]{initialVector.length, 1}, MWClassID.DOUBLE, MWComplexity.REAL);
			
			for(int k=1; k<=weightVector.length; k++){
				weightMatrix.set(new int[]{1, k}, weightVector[k-1]);
				x0Matrix.set(new int[]{k, 1}, initialVector[k-1]);
			}
			
			highLevelMatrix = MWNumericArray.newInstance(new int[]{1, 1}, MWClassID.DOUBLE, MWComplexity.REAL);
			highLevelMatrix.set(new int[]{1, 1}, h);
			lowLevelMatrix = MWNumericArray.newInstance(new int[]{1, 1}, MWClassID.DOUBLE, MWComplexity.REAL);
			lowLevelMatrix.set(new int[]{1, 1}, l);
			
			i_hMatrix = convert(i_h);
			j_hMatrix = convert(j_h);
			
			i_lMatrix = convert(i_l);
			j_lMatrix = convert(j_l);
			i_rMatrix = convert(i_r);
			j_rMatrix = convert(j_r);
			
			inputs = new Object[10];
			inputs[0] = weightMatrix;
			inputs[1] = highLevelMatrix;
			inputs[2] = lowLevelMatrix;
			inputs[3] = i_hMatrix;
			inputs[4] = j_hMatrix;
			inputs[5] = i_lMatrix;
			inputs[6] = j_lMatrix;
			inputs[7] = i_rMatrix;
			inputs[8] = j_rMatrix;
			inputs[9] = x0Matrix;
			
			OptimalGraphMatcher matcher = new OptimalGraphMatcher();
			results = matcher.compute_optimization(7, inputs);
			
			
			System.out.println();
		}
		catch(MWException e){
			e.printStackTrace();
		}
		finally{
			MWArray.disposeArray(weightMatrix);
			MWArray.disposeArray(highLevelMatrix);
			MWArray.disposeArray(lowLevelMatrix);
			MWArray.disposeArray(i_hMatrix);
			MWArray.disposeArray(j_hMatrix);
			MWArray.disposeArray(i_lMatrix);
			MWArray.disposeArray(j_lMatrix);
			MWArray.disposeArray(i_rMatrix);
			MWArray.disposeArray(j_rMatrix);
			MWArray.disposeArray(x0Matrix);
			MWArray.disposeArray(inputs);
			MWArray.disposeArray(results);
		}
		
	}
	
	private MWNumericArray convert(double[] vector){
		MWNumericArray matrix = MWNumericArray.newInstance(new int[]{1, vector.length}, MWClassID.DOUBLE, MWComplexity.REAL);
		
		for(int k=1; k<=vector.length; k++){
			matrix.set(new int[]{1, k}, vector[k-1]);
		}
		
		return matrix;
	}

}
