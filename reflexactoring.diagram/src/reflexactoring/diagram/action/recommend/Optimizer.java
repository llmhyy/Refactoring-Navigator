/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.mathworks.toolbox.javabuilder.MWStructArray;

import fudan.se.graphmatching.optimization.OptimalGraphMatcher;
import reflexactoring.diagram.action.ModelMapper;
import reflexactoring.diagram.bean.GraphNode;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class Optimizer {
	
	private Double[] weightVector;
	private Double[] x0Vector;
	
	public ArrayList<Suggestion> getSuggestionsByOptimization(ArrayList<ICompilationUnitWrapper> units, ArrayList<ModuleWrapper> modules){
		
		OptimalResults optimalResults = optimize(units, modules);
		/**
		 * if there is a possible solution to just move classes.
		 */
		if(optimalResults.getExitFlag() == 1){
			ArrayList<Suggestion> suggestions = generateSuggestions(optimalResults, units, modules);
			return suggestions;
		}
		else if(optimalResults.getExitFlag() == -2){
			System.out.println("may change edges");
		}
		/**
		 * go down to break classes
		 */
		else{
			System.out.println("need to break classes");
		}
		
		return new ArrayList<Suggestion>();
	}
	
	private ArrayList<Suggestion> generateSuggestions(OptimalResults optimalResults, ArrayList<ICompilationUnitWrapper> units, ArrayList<ModuleWrapper> modules){
		
		ArrayList<Suggestion> suggestions = new ArrayList<>();
		
		Double[] optimalResult = optimalResults.getOptimalResult();
		Double[] x0Result = optimalResults.getX0Vector();
		SparseVectors relationVectors = optimalResults.getRelationVectors();
		
		for(int i=0; i<optimalResult.length; i++){
			/**
			 * indicate optimal result is 1 and x0 is 0, implying
			 */
			if(optimalResult[i] > x0Result[i]){
				double moduleIndex = relationVectors.getI()[i];
				double unitIndex = relationVectors.getJ()[i];
				
				ModuleWrapper tobeMappedmodule = modules.get((int)moduleIndex);
				ICompilationUnitWrapper unit = units.get((int)unitIndex);
				
				MoveAction action = new MoveAction();
				action.setOrigin(unit.getMappingModule());
				action.setDestination(tobeMappedmodule);
				
				Suggestion suggestion = new Suggestion(unit, action);
				suggestions.add(suggestion);
			}
			/**
			 * indicate optimal result is 0 and x0 is 1
			 */
			/*else if(optimalResult[i] < x0Result[i]){
				
			}*/
		}
		
		return suggestions;
	}
	
	private OptimalResults optimize(ArrayList<ICompilationUnitWrapper> units, ArrayList<ModuleWrapper> modules){
		double[][] similarityTable = new ModelMapper().computeSimilarityTableWithRegardToHeurisitcRules(modules, units);
		
		int highLevelNumber = modules.size();
		int lowLevelNumber = units.size();
		
		/**
		 * In this method, the weight vector and x0 vector will be initialized as well.
		 */
		SparseVectors relationVectors = extractRelation(similarityTable, modules, units);
		
		SparseVectors highLevelVectors = extractGraph(modules);
		SparseVectors lowLevelVectors = extractGraph(units);
		
		OptimalResults optimalResults = computeOptimalResult(weightVector, highLevelNumber, lowLevelNumber, 
				highLevelVectors, lowLevelVectors, relationVectors, x0Vector);
		
		return optimalResults;
	}
	
	private OptimalResults computeOptimalResult(Double[] weightVector, int h, int l,
			SparseVectors highLevelVectors, SparseVectors lowLevelVectors, SparseVectors relationVectors,
			Double[] x0Vector){
		
		OptimalResults optimalResults = null;
		
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
			/**
			 * The first parameter int[] specify the dimensions of a matrix.
			 */
			weightMatrix = MWNumericArray.newInstance(new int[]{1, weightVector.length}, MWClassID.DOUBLE, MWComplexity.REAL);
			x0Matrix = MWNumericArray.newInstance(new int[]{x0Vector.length, 1}, MWClassID.DOUBLE, MWComplexity.REAL);
			
			for(int k=1; k<=weightVector.length; k++){
				weightMatrix.set(new int[]{1, k}, weightVector[k-1]);
				x0Matrix.set(new int[]{k, 1}, x0Vector[k-1]);
			}
			
			highLevelMatrix = MWNumericArray.newInstance(new int[]{1, 1}, MWClassID.DOUBLE, MWComplexity.REAL);
			highLevelMatrix.set(new int[]{1, 1}, h);
			lowLevelMatrix = MWNumericArray.newInstance(new int[]{1, 1}, MWClassID.DOUBLE, MWComplexity.REAL);
			lowLevelMatrix.set(new int[]{1, 1}, l);
			
			i_hMatrix = convert(highLevelVectors.getI());
			j_hMatrix = convert(highLevelVectors.getJ());
			
			i_lMatrix = convert(lowLevelVectors.getI());
			j_lMatrix = convert(lowLevelVectors.getJ());
			
			i_rMatrix = convert(relationVectors.getI());
			j_rMatrix = convert(relationVectors.getJ());
			
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
			results = matcher.compute_optimization(6, inputs);
			
			optimalResults = convertResults(results);
			optimalResults.setX0Vector(x0Vector);
			optimalResults.setRelationVectors(relationVectors);
			//MWNumericArray resultMatrix = (MWNumericArray)results[0];
			//System.out.println(resultMatrix);
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
		
		return optimalResults;
	}
	
	/**
	 * convert the matlab-related data type into plain java type. In addition,
	 * the results has length of seven, indicating that there are seven output
	 * parameters from the computation.
	 * 
	 * 
	 * @param results
	 * @return
	 */
	private OptimalResults convertResults(Object[] results){
		
		OptimalResults optimalResults = new OptimalResults();
		
		MWNumericArray optimalResultMatrix = (MWNumericArray)results[0];
		MWNumericArray fValMatrix = (MWNumericArray)results[1];
		MWNumericArray exitFlagMatrix = (MWNumericArray)results[2];
		//MWStructArray infoMatrix = (MWStructArray)results[3];
		MWNumericArray validityMatrix = (MWNumericArray)results[3];
		MWNumericArray programRowMatrix = (MWNumericArray)results[4];
		MWNumericArray programColumnMatrix = (MWNumericArray)results[5];
		
		Double validity = (Double)validityMatrix.get(new int[]{1, 1});
		/**
		 * The fact that validity equals one means the result is valid.
		 */
		if(validity == 1){
			int[] dimension = optimalResultMatrix.getDimensions();
			int length = dimension[0];
			
			Double[] optimalResult = new Double[length];
			for(int i=0; i<length; i++){
				Double value = (Double)optimalResultMatrix.get(new int[]{i+1, 1});
				if(value < 0.001){
					optimalResult[i] = 0d;
				}
				else if((value-1)<0.001 && (value-1)>-0.001){
					optimalResult[i] = 1d;
				}
				else{
					optimalResult[i] = value;
					System.err.println("a problem occur");
				}
			}
			
			Double optimalValue = (Double)fValMatrix.get(new int[]{1, 1});
			Double exitFlag = (Double)exitFlagMatrix.get(new int[]{1, 1});
			
			//String status = infoMatrix.getField("Status", 1).get(new int[]{1,1}).toString();
			
			optimalResults.setOptimalResult(optimalResult);
			optimalResults.setOptimalValue(optimalValue);
			//optimalResults.setInfo(status);
			optimalResults.setExitFlag(exitFlag);
		}
		else{
			/**
			 * may provide other suggestions
			 */
			optimalResults.setExitFlag(-2d);
		}
		
		return optimalResults;
	}
	
	private MWNumericArray convert(Double[] vector){
		MWNumericArray matrix = MWNumericArray.newInstance(new int[]{1, vector.length}, MWClassID.DOUBLE, MWComplexity.REAL);
		
		for(int k=1; k<=vector.length; k++){
			matrix.set(new int[]{1, k}, vector[k-1]+1);
		}
		
		return matrix;
	}
	
	/**
	 * In this method, the weight vector and x0 vector will be initialized as well.
	 * @param similarityTable
	 * @param modules
	 * @param units
	 * @return
	 */
	private SparseVectors extractRelation(double[][] similarityTable, ArrayList<ModuleWrapper> modules, ArrayList<ICompilationUnitWrapper> units){
		int highLevelNumber = modules.size();
		int lowLevelNumber = units.size();
		
		//double[][] relationTable = new double[highLevelNumber][lowLevelNumber];
		
		ArrayList<Double> weightVectorList = new ArrayList<>();
		ArrayList<Double> x0VectorList = new ArrayList<>();
		
		ArrayList<Double> iList = new ArrayList<>();
		ArrayList<Double> jList = new ArrayList<>();
		
		for(int i=0; i<highLevelNumber; i++){
			for(int j=0; j<lowLevelNumber; j++){
				if((i != j) && similarityTable[i][j] >= Double.valueOf(ReflexactoringUtil.getMappingThreshold())){
					iList.add((double)(i));
					jList.add((double)(j));
					
					/**
					 * initial weight
					 */
					weightVectorList.add(similarityTable[i][j]);
					/**
					 * initial x0
					 */
					ICompilationUnitWrapper unit = units.get(j);
					ModuleWrapper module = modules.get(i);
					if(module.equals(unit.getMappingModule())){
						x0VectorList.add(1d);
					}
					else{
						x0VectorList.add(0d);
					}
				}
			}
		}
		
		Double[] i = iList.toArray(new Double[0]);
		Double[] j = jList.toArray(new Double[0]);
		
		this.weightVector = weightVectorList.toArray(new Double[0]);
		this.x0Vector = x0VectorList.toArray(new Double[0]);
		
		return new SparseVectors(i, j);
	}
	
	private SparseVectors extractGraph(ArrayList<? extends GraphNode> nodes){
		ArrayList<Double> iList = new ArrayList<>();
		ArrayList<Double> jList = new ArrayList<>();
		
		for(int i=0; i<nodes.size(); i++){
			for(int j=0; j<nodes.size(); j++){
				if(i != j){
					GraphNode nodeI = nodes.get(i);
					GraphNode nodeJ = nodes.get(j);
					
					if(nodeI.getCalleeList().contains(nodeJ)){
						iList.add((double) (i));
						jList.add((double) (j));
					}
				}
			}
		}
		
		Double[] i = iList.toArray(new Double[0]);
		Double[] j = jList.toArray(new Double[0]);
		
		return new SparseVectors(i, j);
	}
	
	public class OptimalResults{
		private Double[] optimalResult;
		private Double optimalValue;
		private Double exitFlag;
		private Double validity;
		private Double programRow;
		private Double programColumn;
		
		private Double[] x0Vector;
		private SparseVectors relationVectors;
		
		public OptimalResults(){}
		
		/**
		 * @param optimalResult
		 * @param optimalValue
		 * @param exitFlag
		 * @param info
		 * @param validity
		 * @param programRow
		 * @param programColumn
		 */
		public OptimalResults(Double[] optimalResult, Double optimalValue,
				Double exitFlag, Double validity, Double programRow,
				Double programColumn) {
			super();
			this.optimalResult = optimalResult;
			this.optimalValue = optimalValue;
			this.exitFlag = exitFlag;
			this.validity = validity;
			this.programRow = programRow;
			this.programColumn = programColumn;
		}
		/**
		 * @return the optimalResult
		 */
		public Double[] getOptimalResult() {
			return optimalResult;
		}
		/**
		 * @param optimalResult the optimalResult to set
		 */
		public void setOptimalResult(Double[] optimalResult) {
			this.optimalResult = optimalResult;
		}
		/**
		 * @return the optimalValue
		 */
		public Double getOptimalValue() {
			return optimalValue;
		}
		/**
		 * @param optimalValue the optimalValue to set
		 */
		public void setOptimalValue(Double optimalValue) {
			this.optimalValue = optimalValue;
		}
		/**
		 * @return the exitFlag
		 */
		public Double getExitFlag() {
			return exitFlag;
		}
		/**
		 * @param exitFlag the exitFlag to set
		 */
		public void setExitFlag(Double exitFlag) {
			this.exitFlag = exitFlag;
		}
		
		/**
		 * @return the validity
		 */
		public Double getValidity() {
			return validity;
		}
		/**
		 * @param validity the validity to set
		 */
		public void setValidity(Double validity) {
			this.validity = validity;
		}
		/**
		 * @return the programRow
		 */
		public Double getProgramRow() {
			return programRow;
		}
		/**
		 * @param programRow the programRow to set
		 */
		public void setProgramRow(Double programRow) {
			this.programRow = programRow;
		}
		/**
		 * @return the programColumn
		 */
		public Double getProgramColumn() {
			return programColumn;
		}
		/**
		 * @param programColumn the programColumn to set
		 */
		public void setProgramColumn(Double programColumn) {
			this.programColumn = programColumn;
		}

		/**
		 * @return the x0Vector
		 */
		public Double[] getX0Vector() {
			return x0Vector;
		}

		/**
		 * @param x0Vector the x0Vector to set
		 */
		public void setX0Vector(Double[] x0Vector) {
			this.x0Vector = x0Vector;
		}

		/**
		 * @return the relationVectors
		 */
		public SparseVectors getRelationVectors() {
			return relationVectors;
		}

		/**
		 * @param relationVectors the relationVectors to set
		 */
		public void setRelationVectors(SparseVectors relationVectors) {
			this.relationVectors = relationVectors;
		}
		
		
	}
	
	
	/**
	 * This class is to be adapted to the data type in matlab.  For example,
	 * i=[1 2 1], j=[1 0 2] mean that the points (1, 1), (2, 0) and (1, 2) are
	 * non-zero entries.
	 * 
	 * @author linyun
	 *
	 */
	public class SparseVectors{
		private Double[] i;
		private Double[] j;
		
		/**
		 * @param i
		 * @param j
		 */
		public SparseVectors(Double[] i, Double[] j) {
			super();
			this.i = i;
			this.j = j;
		}
		
		public String toString(){
			StringBuffer buffer = new StringBuffer();
			
			for(int k=0; k<i.length; k++){
				buffer.append(i[k]+ " ");
			}
			buffer.append("\n");
			for(int k=0; k<i.length; k++){
				buffer.append(j[k]+ " ");
			}
			
			return buffer.toString();
		}
		
		/**
		 * @return the i
		 */
		public Double[] getI() {
			return i;
		}
		/**
		 * @param i the i to set
		 */
		public void setI(Double[] i) {
			this.i = i;
		}
		/**
		 * @return the j
		 */
		public Double[] getJ() {
			return j;
		}
		/**
		 * @param j the j to set
		 */
		public void setJ(Double[] j) {
			this.j = j;
		}
	}
}
