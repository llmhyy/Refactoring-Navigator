/**
 * 
 */
package reflexactoring.diagram.util.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import reflexactoring.diagram.action.semantic.TFIDF;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class DoucmentSimilarityTest {

	@Test
	public void testDoucmentSimilarity() {
		String file1 = "The module is used to show different calculator panels including register panel number panel control panel op panel and function panel";
		String file2 = "calculator panel current model strict number panel register panel macro load function";
		String file3 = "strict bigdecimal type math strict add or not xor multiply substract";
		
		ArrayList<String> list = new ArrayList<>();
		list.add(file1);
		list.add(file2);
		list.add(file3);
		
		TFIDF tfidf = new TFIDF(false);
		List<double[]> vectors = tfidf.getTFIDFfromString(list);
		
		double[] moduleVector = vectors.get(0);
		double[] classVector1 = vectors.get(1);
		double[] classVector2 = vectors.get(2);
		
		double similarity1 = ReflexactoringUtil.computeEuclideanSimilarity(moduleVector, classVector1);
		double similarity2 = ReflexactoringUtil.computeEuclideanSimilarity(moduleVector, classVector2);
		
		assertTrue(similarity1 > similarity2);
	}
	
	@Test
	public void testStemming(){
		String file1 = "calculator panel current modeling strict numbers panel register panel macro load function";
		String file2 = "calculators panels currents model strict number panel register panels macro load function";
		
		String stemFile1 = ReflexactoringUtil.performStemming(file1);
		String stemFile2 = ReflexactoringUtil.performStemming(file2);
		
		assertTrue(stemFile1.equals(stemFile2));
	}
	
	@Test
	public void testRemovingDelimit(){
		String content = "The module; is\\? used??{to show different calculator panels including register panel,number panel, control, [panel] ()op panel and function panel";
		content = ReflexactoringUtil.removeDelimit(content);
		
		String result = "The module is used to show different calculator panels including register panel number panel control panel op panel and function panel";
		
		assertTrue(content.equals(result));
	}

}
