/**
 * 
 */
package reflexactoring.diagram.action;

import java.util.ArrayList;
import java.util.HashMap;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;

/**
 * @author linyun
 *
 */
public class HeuristicPageRanker {
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
		
		/**
		 * preparation for the input of page ranking code.
		 */
		HashMap<ICompilationUnitWrapper, Integer> indexMap = new HashMap<>();
		for(int i=0; i<compilationUnitList.size(); i++){
			indexMap.put(compilationUnitList.get(i), i);
		}
		
		SparseDoubleMatrix1D x0 = new SparseDoubleMatrix1D(initialVector);
		
		SparseDoubleMatrix2D A = new SparseDoubleMatrix2D(compilationUnitList.size(), compilationUnitList.size());
		for(ICompilationUnitWrapper calleeUnitWrapper: compilationUnitList){
			int denominator = calleeUnitWrapper.getCallerCompilationUnitList().size();
			for(ICompilationUnitWrapper callerUnitWrapper: calleeUnitWrapper.getCallerCompilationUnitList().keySet()){
				int calleeNodeIndex = indexMap.get(calleeUnitWrapper); 
				int callerNodeIndex = indexMap.get(callerUnitWrapper);
				
				A.set(callerNodeIndex, calleeNodeIndex, 1d/denominator);
			}
		}
		
		Algebra alg = new Algebra();
		DoubleMatrix1D result = alg.mult(A, x0);
		
		return result.toArray();
	}
}
