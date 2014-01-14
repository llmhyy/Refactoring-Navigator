/**
 * 
 */
package reflexactoring.diagram.action;

import java.util.ArrayList;
import java.util.HashMap;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import fudan.se.pageranking.PageRanking;
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
		
		/**
		 * preparation for the input of page ranking code.
		 */
		HashMap<ICompilationUnitWrapper, Integer> indexMap = new HashMap<>();
		for(int i=0; i<compilationUnitList.size(); i++){
			indexMap.put(compilationUnitList.get(i), i);
		}
		
		ArrayList<Integer> calleeNodeIndexes = new ArrayList<>();
		ArrayList<Integer> callerNodeIndexes = new ArrayList<>();
		
		for(ICompilationUnitWrapper callerUnitWrapper: compilationUnitList){
			for(ICompilationUnitWrapper calleeUnitWrapper: callerUnitWrapper.getCalleeCompilationUnitList().keySet()){
				int calleeNodeIndex = indexMap.get(callerUnitWrapper); 
				int callerNodeIndex = indexMap.get(calleeUnitWrapper);
				
				/**
				 * In matlab computation, the array index starts at 1 rather than 0.
				 */
				calleeNodeIndexes.add(calleeNodeIndex+1);
				callerNodeIndexes.add(callerNodeIndex+1);
			}
		}
		
		double[] resultVector = getPageRankingResultVector(calleeNodeIndexes.toArray(new Integer[0]), 
				callerNodeIndexes.toArray(new Integer[0]), 
				compilationUnitList.size(), initialVector);
		
		return resultVector;
	}
	
	public double[] getPageRankingResultVector(Integer[] calleeNodeIndexArray, Integer[] callerNodeIndexArray, int graphSize, double[] initialVector){
		
		double[] resultVector = null;
		
		MWNumericArray iMatrix = null;
		MWNumericArray jMatrix = null;
		MWNumericArray nMatrix = null;
		MWNumericArray x0Matrix = null;
		Object[] inputs = null;
		Object[] results = null;
		try {
			
			/**
			 * The first parameter int[] specify the dimensions of a matrix.
			 */
			iMatrix = MWNumericArray.newInstance(new int[]{1, calleeNodeIndexArray.length}, MWClassID.DOUBLE, MWComplexity.REAL);
			jMatrix = MWNumericArray.newInstance(new int[]{1, callerNodeIndexArray.length}, MWClassID.DOUBLE, MWComplexity.REAL);
			
			for(int k=1; k<=calleeNodeIndexArray.length; k++){
				iMatrix.set(new int[]{1, k}, calleeNodeIndexArray[k-1]);
				jMatrix.set(new int[]{1, k}, callerNodeIndexArray[k-1]);
			}
			
			nMatrix = MWNumericArray.newInstance(new int[]{1, 1}, MWClassID.DOUBLE, MWComplexity.REAL);
			nMatrix.set(new int[]{1, 1}, graphSize);
			
			x0Matrix = MWNumericArray.newInstance(new int[]{initialVector.length, 1}, MWClassID.DOUBLE, MWComplexity.REAL);
			for(int k=1; k<=initialVector.length; k++){
				x0Matrix.set(new int[]{k, 1}, initialVector[k-1]);
			}
			
			PageRanking pageRanking = new PageRanking();
			
			inputs = new Object[4];
			inputs[0] = iMatrix;
			inputs[1] = jMatrix;
			inputs[2] = nMatrix;
			inputs[3] = x0Matrix;
			
			/**
			 * Perform pageRanking algorithm.
			 */
			results = pageRanking.pageRanking(1, inputs);
			
			MWNumericArray resultMatrix = (MWNumericArray)results[0];
			resultVector = new double[initialVector.length];
			for(int i=0; i<initialVector.length; i++){
				resultVector[i] = (Double) resultMatrix.get(new int[]{i+1, 1});
			}
			
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
		
		return resultVector;
	}
}
