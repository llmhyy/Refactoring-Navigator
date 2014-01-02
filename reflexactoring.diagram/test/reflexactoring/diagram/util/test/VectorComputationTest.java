/**
 * 
 */
package reflexactoring.diagram.util.test;

import static org.junit.Assert.*;

import org.junit.Test;

import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class VectorComputationTest {

	@Test
	public void testVectorCosineTest() {
		double[] vector1 = {0, 1, 2};
		double[] vector2 = {1, 2, 0};
		
		double vaule = ReflexactoringUtil.computeCosine(vector1, vector2);
		assertEquals(0.4, vaule, 0.001);
	}
	
	@Test
	public void testVectorEuclideanTest() {
		double[] vector1 = {0, 1, 2};
		double[] vector2 = {1, 2, 0};
		
		double vaule = ReflexactoringUtil.computeEuclideanSimilarity(vector1, vector2);
		assertEquals(0.408, vaule, 0.001);
	}

}
