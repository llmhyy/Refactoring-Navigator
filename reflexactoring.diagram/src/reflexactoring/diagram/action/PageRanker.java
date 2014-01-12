/**
 * 
 */
package reflexactoring.diagram.action;

import java.util.ArrayList;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;

/**
 * @author linyun
 *
 */
public class PageRanker {
	/**
	 * This method could spread the weight (similarity) amongst different unit.
	 * For example, there is a concept named as "draw", we can match the java class
	 * "DrawFigure" is possibly relevant, however, the class "Canvas" may be regarded
	 * as irrelevant because there is no keyword shared between "draw" and "canvas".
	 * Therefore, I intend to spread the similarity if there is a dependency between
	 * class "Canvas" and "DrawFigure". The spreading algorithm is Page Ranking algorithm.
	 * 
	 * The initialVector is the initial similarity between a module and many compilation units.
	 * 
	 * 
	 * @param initialVector
	 * @param compilationUnitList
	 * @return
	 */
	public double[] generateResultVector(double[] initialVector, 
			ArrayList<ICompilationUnitWrapper> compilationUnitList){
		
		
		
		return initialVector;
	}
}
