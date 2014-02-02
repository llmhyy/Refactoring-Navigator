/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import reflexactoring.diagram.action.ModelMapper;
import reflexactoring.diagram.action.recommend.Suggestion;
import reflexactoring.diagram.action.recommend.optimal.Debugger;
import reflexactoring.diagram.action.recommend.optimal.GlobalOptimizer.OptimalResults;
import reflexactoring.diagram.action.recommend.optimal.GlobalOptimizer.SparseVectors;
import reflexactoring.diagram.bean.GraphNode;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class GeneticOptimizer {
	
	private Double[] weightVector;
	private Integer[] x0Vector;
	
	private OptimalResults optimize(ArrayList<ICompilationUnitWrapper> units, ArrayList<ModuleWrapper> modules){
		//double[][] similarityTable = new ModelMapper().computeSimilarityTableWithRegardToHeurisitcRules(modules, units);
		double[][] similarityTable;
		
		if(ReflexactoringUtil.isNeedReComputeSimilarity()){
			similarityTable = new ModelMapper().computeSimilarityTableWithRegardToHeurisitcRules(modules, units);
		}
		else{
			similarityTable	= ReflexactoringUtil.
					convertModuleUnitsSimilarityTableToRawTable(Settings.similarityTable);
			
		}
		int highLevelNumber = modules.size();
		int lowLevelNumber = units.size();
		
		/**
		 * In this method, the weight vector and x0 vector will be initialized as well.
		 */
		SparseVectors relationVectors = extractRelation(similarityTable, modules, units);
		
		System.out.println("The variable number is: " + this.weightVector.length);
		
		SparseVectors highLevelVectors = extractGraph(modules);
		SparseVectors lowLevelVectors = extractGraph(units);
		
		//System.out.println(Debugger.printInputValues(weightVector, highLevelNumber, lowLevelNumber, highLevelVectors, lowLevelVectors, relationVectors, x0Vector));
		
		
		return null;
	}
	
	
	
	
	/**
	 * @param weightVector2
	 * @param highLevelNumber
	 * @param lowLevelNumber
	 * @param highLevelVectors
	 * @param lowLevelVectors
	 * @param relationVectors
	 * @param x0Vector
	 * @return
	 */
	private Results computeOptimalResult(Double[] weightVector2,
			int highLevelNumber, int lowLevelNumber,
			SparseVectors highLevelVectors, SparseVectors lowLevelVectors,
			SparseVectors relationVectors, Integer[] x0Vector) {

		ArrayList<int[]> population = generatePopulation(x0Vector.length);
		Results results = null;
		for(int i=0; i<Settings.geneticIterationNum; i++){
			results = crossoverAndMutate(population);
			population = results.getPopluation();
		}
		
		return results;
	}
	
	private ArrayList<int[]> generatePopulation(int dimension){
		ArrayList<int[]> population = new ArrayList<>();
		for(int i=0; i<Settings.populationSize; i++){
			int[] seed = new int[dimension];
			for(int j=0; j<dimension; j++){
				if(Math.random()>=0.5){
					seed[j] = 1;
				}
				else{
					seed[j] = 0;
				}
			}
			population.add(seed);
		}
		return population;
	}
	
	/**
	 * assume that the number of population is even.
	 * @param population
	 * @return
	 */
	private Results crossoverAndMutate(ArrayList<int[]> population){
		
		// selector: roulette wheel selection
		
		/**
		 * crossover
		 */
		
		/**
		 * mutate
		 */
		
		return null;
	}
	
	public class Results{
		private ArrayList<int[]> popluation;
		private double optimalResult;
		private int[] bestSolution;
		/**
		 * @return the popluation
		 */
		public ArrayList<int[]> getPopluation() {
			return popluation;
		}
		/**
		 * @param popluation the popluation to set
		 */
		public void setPopluation(ArrayList<int[]> popluation) {
			this.popluation = popluation;
		}
		/**
		 * @return the optimalResult
		 */
		public double getOptimalResult() {
			return optimalResult;
		}
		/**
		 * @param optimalResult the optimalResult to set
		 */
		public void setOptimalResult(double optimalResult) {
			this.optimalResult = optimalResult;
		}
		/**
		 * @return the bestSolution
		 */
		public int[] getBestSolution() {
			return bestSolution;
		}
		/**
		 * @param bestSolution the bestSolution to set
		 */
		public void setBestSolution(int[] bestSolution) {
			this.bestSolution = bestSolution;
		}
		
		
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
		ArrayList<Integer> x0VectorList = new ArrayList<>();
		
		ArrayList<Integer> iList = new ArrayList<>();
		ArrayList<Integer> jList = new ArrayList<>();
		
		for(int i=0; i<highLevelNumber; i++){
			for(int j=0; j<lowLevelNumber; j++){
				if((i != j) && similarityTable[i][j] >= Double.valueOf(ReflexactoringUtil.getMappingThreshold())){
					iList.add((int)(i));
					jList.add((int)(j));
					
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
						x0VectorList.add(1);
					}
					else{
						x0VectorList.add(0);
					}
				}
			}
		}
		
		Integer[] i = iList.toArray(new Integer[0]);
		Integer[] j = jList.toArray(new Integer[0]);
		
		this.weightVector = weightVectorList.toArray(new Double[0]);
		this.x0Vector = x0VectorList.toArray(new Integer[0]);
		
		return new SparseVectors(i, j);
	}
	
	private SparseVectors extractGraph(ArrayList<? extends GraphNode> nodes){
		ArrayList<Integer> iList = new ArrayList<>();
		ArrayList<Integer> jList = new ArrayList<>();
		
		for(int i=0; i<nodes.size(); i++){
			for(int j=0; j<nodes.size(); j++){
				if(i != j){
					GraphNode nodeI = nodes.get(i);
					GraphNode nodeJ = nodes.get(j);
					
					if(nodeI.getCalleeList().contains(nodeJ)){
						iList.add((int) (i));
						jList.add((int) (j));
					}
				}
			}
		}
		
		Integer[] i = iList.toArray(new Integer[0]);
		Integer[] j = jList.toArray(new Integer[0]);
		
		return new SparseVectors(i, j);
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
		private Integer[] i;
		private Integer[] j;
		
		/**
		 * @param i
		 * @param j
		 */
		public SparseVectors(Integer[] i, Integer[] j) {
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
		public Integer[] getI() {
			return i;
		}
		/**
		 * @param i the i to set
		 */
		public void setI(Integer[] i) {
			this.i = i;
		}
		/**
		 * @return the j
		 */
		public Integer[] getJ() {
			return j;
		}
		/**
		 * @param j the j to set
		 */
		public void setJ(Integer[] j) {
			this.j = j;
		}
	}
}
