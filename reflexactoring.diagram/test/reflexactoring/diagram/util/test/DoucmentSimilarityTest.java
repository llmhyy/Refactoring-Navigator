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
	public void testLexicalSimilarity(){
		String moduleDesc = "add and or integer big decimal xor not substract multiply divide mod min max pow calculate";
		String typeDesc = "calculator calculator ui util lambda layout java awt java awt event java io io exception java net url swing swing event swing text html about dialog dialog navable editor pane editor scroll pane scrollpane nav nav url initial url about dialog frame owner string title string mime type string content nav owner title editor editor pane mime type content init nav about dialog frame owner string title url content nav io exception owner title editor editor pane content initial url content init nav init use nav editor set editable editor add hyperlink listen link listen bord layout layout bord layout panel panel panel layout scrollpane scroll pane editor panel add scrollpane bord layout center button ok btn button ok btn add action listen action listen action perform action event ae set visible dispose panel btn panel panel use nav btn panel set layout lambda layout nav nav btn panel add nav btn panel add ok btn initial url nav update initial url btn panel add ok btn panel add btn panel bord layout south set content pane panel pack set size dimension set visible visible editor caret set dot scrollpane viewport scroll rect to visible rectangle set visible visible set position object ref ref url editor set page url ref exception ref string editor scroll to reference string ref link listen hyperlink listen cursor hand cursor cursor predefin cursor cursor hand cursor cursor default cursor cursor predefin cursor cursor default cursor hyperlink update hyperlink event editor pane pane editor pane source event type hyperlink event event type activate html frame hyperlink event html frame hyperlink event evt html frame hyperlink event html document doc html document pane document doc process html frame hyperlink event evt java net url url url url pane set page url nav nav update url string desc description desc desc substr pane scroll to reference desc nav nav update desc throwable option pane show message dialog about dialog option pane error message event type hyperlink event event type enter pane set cursor hand cursor event type hyperlink event event type exit pane set cursor default cursor main string arg frame frame frame about dialog about about dialog frame about set visible";
		
		ArrayList<String> list = new ArrayList<>();
		list.add(moduleDesc);
		list.add(typeDesc);
		
		TFIDF tfidf = new TFIDF(true);
		List<double[]> vectors = tfidf.getTFIDFfromString(list);
		
		double[] moduleVector = vectors.get(0);
		double[] classVector = vectors.get(1);
		
		//double similarity = ReflexactoringUtil.computeEuclideanSimilarity(moduleVector, classVector);
		double similarity = ReflexactoringUtil.computeCosine(classVector, moduleVector);
		System.out.println(similarity);
		assertTrue(similarity < 0.7);
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
