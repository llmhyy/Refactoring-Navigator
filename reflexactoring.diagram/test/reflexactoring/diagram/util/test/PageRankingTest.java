/**
 * 
 */
package reflexactoring.diagram.util.test;

import static org.junit.Assert.*;

import org.junit.Test;

import reflexactoring.diagram.action.PageRanker;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import fudan.se.pageranking.PageRanking;

/**
 * @author linyun
 *
 */
public class PageRankingTest {

	@Test
	public void testPageRanking() {
		
		MWNumericArray iMatrix = null;
		MWNumericArray jMatrix = null;
		MWNumericArray nMatrix = null;
		MWNumericArray x0Matrix = null;
		Object[] inputs = null;
		Object[] results = null;
		try {
			
			int[] i = {1, 2, 2, 3, 4, 4, 4, 5, 5, 6, 6, 7, 7, 7, 8};
			int[] j = {3, 1, 4, 1, 1, 2, 3, 2, 6, 7, 8, 4, 5, 8, 6};
			int[] n = {8};
			int[] x0 = {1, 1, 1, 1, 1, 1, 1, 1};
			
			/**
			 * The first parameter int[] specify the dimensions of a matrix.
			 */
			iMatrix = MWNumericArray.newInstance(new int[]{1, i.length}, MWClassID.DOUBLE, MWComplexity.REAL);
			jMatrix = MWNumericArray.newInstance(new int[]{1, j.length}, MWClassID.DOUBLE, MWComplexity.REAL);
			
			for(int k=1; k<=i.length; k++){
				iMatrix.set(new int[]{1, k}, i[k-1]);
				jMatrix.set(new int[]{1, k}, j[k-1]);
			}
			
			nMatrix = MWNumericArray.newInstance(new int[]{1, 1}, MWClassID.DOUBLE, MWComplexity.REAL);
			nMatrix.set(new int[]{1, 1}, 8);
			
			x0Matrix = MWNumericArray.newInstance(new int[]{x0.length, 1}, MWClassID.DOUBLE, MWComplexity.REAL);
			for(int k=1; k<=x0.length; k++){
				x0Matrix.set(new int[]{k, 1}, 0.125);
			}
			
			PageRanking pageRanking = new PageRanking();
			
			inputs = new Object[4];
			inputs[0] = iMatrix;
			inputs[1] = jMatrix;
			inputs[2] = nMatrix;
			inputs[3] = x0Matrix;
			
			results = pageRanking.pageRanking(1, inputs);
			
			//pageRanking.pageRanking(inputs, results);
			System.out.println(results[0]);
			
			MWNumericArray resultMatrix = (MWNumericArray)results[0];
			Double value = (Double)resultMatrix.get(new int[]{1, 1});
			
			assertEquals(value, 0.0304, 0.0001);
			
		} catch (MWException e) {
			e.printStackTrace();
		} finally {
			MWArray.disposeArray(iMatrix);
			MWArray.disposeArray(jMatrix);
			MWArray.disposeArray(nMatrix);
			MWArray.disposeArray(x0Matrix);
			MWArray.disposeArray(inputs);
			MWArray.disposeArray(results);
		}
	}
	
	@Test
	public void testPageRanking2(){
		Integer[] calleeNodeIndexes = {1, 2, 2, 3, 4, 4, 4, 5, 5, 6, 6, 7, 7, 7, 8};
		Integer[] callerNodeIndexes = {3, 1, 4, 1, 1, 2, 3, 2, 6, 7, 8, 4, 5, 8, 6};
		int graphSize = 8;
		double[] x0 = {0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125, 0.125};
		
		double[] expectedResult = {0.03, 0.053, 0.027, 0.061, 0.162, 0.283, 0.241, 0.139};
		
		PageRanker ranker = new PageRanker();
		double[] resultVector = ranker.getPageRankingResultVector(calleeNodeIndexes, callerNodeIndexes, graphSize, x0);
		
		assertArrayEquals(expectedResult, resultVector, 0.001);
		
	}

}
