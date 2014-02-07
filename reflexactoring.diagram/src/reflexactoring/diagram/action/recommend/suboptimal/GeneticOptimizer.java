/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jdt.core.dom.CompilationUnit;

import Jama.Matrix;
import reflexactoring.diagram.action.ModelMapper;
import reflexactoring.diagram.bean.GraphNode;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapperList;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class GeneticOptimizer {
	
	private Matrix weightVector;
	private Matrix x0Vector;
	
	/**
	 * The below two variables stand for the map relation between a solution and its corresponding module-unit pair.
	 * A solution actually specify how a unit/member will be mapped to a module. For example, a solution will be
	 * like [0, 1, 0, 1, 0], which means that there are five possible maps between some modules an some units. More
	 * specific, there are two modules A, B and three units a, b and c. The five possible maps are A->a, A->b, B->a,
	 * B->b, B->c. And the above vector stands for A->b and B->b are chosen while others are not. And now, the below
	 * two variables stands for which maps are a component in the vector stands for.
	 * 
	 * The mapping relation is between the index of a component and a pair of index (one is for index of module; one is
	 * for the index of unit). By this means, I am able to correspond the relation.
	 */
	private ArrayList<int[]> relationMap;
	private HashMap<ModuleUnitCorrespondence, Integer> reverseRelationMap;
	
	public class ModuleUnitCorrespondence{
		private int moduleIndex;
		private int unitIndex;
		
		
		/**
		 * @param moduleIndex
		 * @param unitIndex
		 */
		public ModuleUnitCorrespondence(int moduleIndex, int unitIndex) {
			super();
			this.moduleIndex = moduleIndex;
			this.unitIndex = unitIndex;
		}
		
		
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + moduleIndex;
			result = prime * result + unitIndex;
			return result;
		}



		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ModuleUnitCorrespondence other = (ModuleUnitCorrespondence) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (moduleIndex != other.moduleIndex)
				return false;
			if (unitIndex != other.unitIndex)
				return false;
			return true;
		}



		/**
		 * @return the moduleIndex
		 */
		public int getModuleIndex() {
			return moduleIndex;
		}
		/**
		 * @param moduleIndex the moduleIndex to set
		 */
		public void setModuleIndex(int moduleIndex) {
			this.moduleIndex = moduleIndex;
		}
		/**
		 * @return the unitIndex
		 */
		public int getUnitIndex() {
			return unitIndex;
		}
		/**
		 * @param unitIndex the unitIndex to set
		 */
		public void setUnitIndex(int unitIndex) {
			this.unitIndex = unitIndex;
		}



		private GeneticOptimizer getOuterType() {
			return GeneticOptimizer.this;
		}
		
		
	}
	
	public ArrayList<int[]> getRelationMap(){
		return this.relationMap;
	}
	
	/**
	 * this method should be invoked after calling optimize().
	 * @return
	 */
	public int[] getX0(){
		int[] x0 = new int[x0Vector.getRowDimension()];
		
		for(int i=0; i<x0Vector.getRowDimension(); i++){
			x0[i] = (int) x0Vector.get(i, 0);
		}
		
		return x0;
	}

	public Genotype optimize(ArrayList<ICompilationUnitWrapper> units, ArrayList<ModuleWrapper> modules){
		//double[][] similarityTable = new ModelMapper().computeSimilarityTableWithRegardToHeurisitcRules(modules, units);
		double[][] similarityTable;
		
		if(ReflexactoringUtil.isNeedReComputeSimilarity()){
			similarityTable = new ModelMapper().computeSimilarityTableWithRegardToHeurisitcRules(modules, units);
		}
		else{
			similarityTable	= ReflexactoringUtil.
					convertModuleUnitsSimilarityTableToRawTable(Settings.similarityTable);
			
		}
		
		FitnessComputingFactor computingFactor = buildComputingFactor(similarityTable, modules, units);
		
		Genotype gene = computeOptimalResult(computingFactor, modules, units, similarityTable);
		
		return gene;
	}
	
	public Genotype optimize(UnitMemberWrapperList members, ArrayList<ModuleWrapper> modules){
		double[][] similarityTable = new double[modules.size()][members.size()];
		
		for(int i=0; i<modules.size(); i++){
			for(int j=0; j<members.size(); j++){
				similarityTable[i][j] = 1;
			}
		}
		
		FitnessComputingFactor computingFactor = buildComputingFactor(similarityTable, modules, members);
		
		Genotype gene = computeOptimalResult(computingFactor, modules, members, similarityTable);
		
		return gene;
	}
	
	private FitnessComputingFactor buildComputingFactor(double[][] similarityTable, ArrayList<ModuleWrapper> modules, 
			ArrayList<? extends LowLevelGraphNode> lowLevelUnits){
		/**
		 * In the method as extractRelation, the weight vector and x0 vector will be initialized as well.
		 */
		Matrix relationMatrix = extractRelation(similarityTable, modules, lowLevelUnits);
		
		System.out.println("The variable number is: " + this.weightVector.getColumnDimension());
		
		Matrix highLevelMatrix = extractGraph(modules);
		Matrix lowLevelMatrix = extractGraph(lowLevelUnits);
		
		FitnessComputingFactor computingFactor = new FitnessComputingFactor();
		computingFactor.setHighLevelMatrix(highLevelMatrix);
		computingFactor.setLowLevelMatrix(lowLevelMatrix);
		computingFactor.setRelationMatrix(relationMatrix);
		computingFactor.setWeightVector(weightVector);
		computingFactor.setX0Vector(x0Vector);
		
		return computingFactor;
	}
	
	/**
	 * 
	 */
	private Genotype computeOptimalResult(FitnessComputingFactor computingFactor,
			ArrayList<ModuleWrapper> modules, ArrayList<? extends LowLevelGraphNode> lowLevelNodes, double[][] similarityTable) {
		
		Population population = generatePopulation(computingFactor, modules, lowLevelNodes, similarityTable);
		System.out.println(population.getOptimalGene().getFitness());
		
		for(int i=0; i<Integer.valueOf(ReflexactoringUtil.getIterationNumber()); i++){
			population = generateNextGeneration(population, computingFactor);
			System.out.println(population.getOptimalGene().getFitness());
		}
		
		return population.getOptimalGene();
	}
	
	private Population generatePopulation(FitnessComputingFactor computingFactor,
			ArrayList<ModuleWrapper> modules, ArrayList<? extends LowLevelGraphNode> lowLevelNodes, double[][] similarityTable){
		
		int dimension = computingFactor.getX0Vector().getRowDimension();
		
		Population population = new Population();
		for(int i=0; i<Integer.valueOf(ReflexactoringUtil.getPopulationSize()); i++){
			
			//int[] seed = randomSeed(dimension);
			
			int[] seed = randomSeedConformedToHardConstraints(dimension, lowLevelNodes, modules, similarityTable);
			
			Genotype gene = new Genotype(seed);
			gene.setFitness(gene.computeFitness(computingFactor));
			
			population.add(gene);
		}
		
		population.updateOptimalGene();
		
		return population;
	}
	
	private int[] randomSeedConformedToHardConstraints(int dimension, ArrayList<? extends LowLevelGraphNode> lowLevelNodes, 
			ArrayList<ModuleWrapper> modules, double[][] similarityTable){
		int[] seed = new int[dimension];
		
		@SuppressWarnings("unchecked")
		ArrayList<LowLevelGraphNode>[] buckets = new ArrayList[modules.size()];
		for(int i=0; i<buckets.length; i++){
			buckets[i] = new ArrayList<>();
		}
		
		do{
			seed = new int[dimension];
			for(int i=0; i<buckets.length; i++){
				buckets[i].clear();
			}
			
			
			for(int q=0; q<lowLevelNodes.size(); q++){
				ArrayList<Integer> validModuleIndex = new ArrayList<>();
				
				/**
				 * find all the valid modules to assign units
				 */
				for(int p=0; p<modules.size(); p++){
					if(similarityTable[p][q] > Double.valueOf(ReflexactoringUtil.getMappingThreshold())){
						validModuleIndex.add(p);
					}
				}
				assert(validModuleIndex.size() > 0);
				
				/**
				 * In all the buckets (modules), if there are some buckets which have not been assigned
				 * any units (low level nodes), the algorithm will favor them some priority to be assigned
				 * first.
				 */
				if(isSomeBucketEmpty(buckets)){
					Iterator<Integer> iterator = validModuleIndex.iterator();
					while(iterator.hasNext()){
						Integer moduleIndex = iterator.next();
						if(buckets[moduleIndex].size() != 0){
							iterator.remove();
						}
					}
				}
				assert(validModuleIndex.size() > 0);
				
				double random = Math.random();
				
				int randomIndex = (int)(random*validModuleIndex.size());
				int chosenModule = validModuleIndex.get(randomIndex);
				int chosenUnit = q;
				
				ModuleUnitCorrespondence chosenPair = new ModuleUnitCorrespondence(chosenModule, chosenUnit);
				buckets[chosenModule].add(lowLevelNodes.get(chosenUnit));
				
				Integer componentIndex = this.reverseRelationMap.get(chosenPair);
				seed[componentIndex] = 1;
			}
		}while(isSomeBucketEmpty(buckets));
		
		
		return seed;
	}
	
	private boolean isSomeBucketEmpty(ArrayList<? extends LowLevelGraphNode>[] buckets){
		for(ArrayList<? extends LowLevelGraphNode> bucket: buckets){
			if(bucket.size() == 0){
				return true;
			}
		}
		
		return false;
	}
	
	private int[] randomSeed(int dimension){
		int[] seed = new int[dimension];
		for(int j=0; j<dimension; j++){
			if(Math.random()>=0.5){
				seed[j] = 1;
			}
			else{
				seed[j] = 0;
			}
		}
		
		return seed;
	}
	
	/**
	 * assume that the number of population is even.
	 * @param population
	 * @return
	 */
	private Population generateNextGeneration(Population population, FitnessComputingFactor computingFactor){
		
		/**
		 * selector: roulette wheel selection
		 */
		Population selectedPopulation = selectPopulation(population, computingFactor.getHighLevelMatrix().getRowDimension(), computingFactor.getLowLevelMatrix().getRowDimension());
		
		/**
		 * crossover and mutate
		 */
		Population crossedPopulation = crossoverAndMutate(selectedPopulation, computingFactor);
		
		
		return crossedPopulation;
	}
	
	/**
	 * @param selectedPopulation
	 * @return
	 */
	private Population crossoverAndMutate(Population selectedPopulation, FitnessComputingFactor computingFactor) {
		
		Population crosssoverPopulation = new Population();
		crosssoverPopulation.setOptimalGene(selectedPopulation.getOptimalGene());
		
		for(int i=0; i<selectedPopulation.size(); i=i+2){
			Genotype gene1 = selectedPopulation.get(i);
			Genotype gene2 = selectedPopulation.get(i+1);
			
			int[] DNA1 = gene1.getDNA();
			int[] DNA2 = gene2.getDNA();
			
			int[] childDNA1 = new int[DNA1.length];
			int[] childDNA2 = new int[DNA2.length];
			
			for(int j=0; j<DNA1.length; j++){
				if(DNA1[j] == DNA2[j]){
					childDNA1[j] = DNA1[j];
					childDNA2[j] = DNA2[j];
				}
				else{
					double fitness1 = normalizeFitnessValue(gene1.getFitness(), 
							computingFactor.getHighLevelMatrix().getRowDimension(), computingFactor.getLowLevelMatrix().getRowDimension());
					double fitness2 = normalizeFitnessValue(gene2.getFitness(), 
							computingFactor.getHighLevelMatrix().getRowDimension(), computingFactor.getLowLevelMatrix().getRowDimension());
					
					double flipPoint = fitness1/(fitness1 + fitness2);
					childDNA1[j] = (Math.random()<=flipPoint)? 1 : 0;
					childDNA2[j] = (Math.random()>flipPoint)? 1 : 0;
				}
				
				/**
				 * mutate
				 */
				if(Math.random() <= Settings.mutationRate){
					childDNA1[j] = flip(childDNA1[j]);
				}
				if(Math.random() <= Settings.mutationRate){
					childDNA2[j] = flip(childDNA2[j]);
				}
			}
			
			Genotype subGene1 = new Genotype(childDNA1);
			subGene1.setFitness(subGene1.computeFitness(computingFactor));
			Genotype subGene2 = new Genotype(childDNA2);
			subGene2.setFitness(subGene2.computeFitness(computingFactor));
			
			crosssoverPopulation.add(subGene1);
			crosssoverPopulation.add(subGene2);
		}
		
		crosssoverPopulation.updateOptimalGene();
		
		return crosssoverPopulation;
	}

	private int flip(int code){
		if(code == 0){
			return 1;
		}
		else{
			return 0;
		}
	}


	/**
	 * roulette wheel selection
	 * @param population
	 * @return
	 */
	private Population selectPopulation(Population population, int highNum, int lowNum){
		Population selectedPopluation = new Population();
		selectedPopluation.setOptimalGene(population.getOptimalGene());

		double[] bin = new double[population.size()+1];
		double sum = 0;
		bin[0] = 0;
		for(int i=0; i<population.size(); i++){
			double fitness = population.get(i).getFitness();
			sum += normalizeFitnessValue(fitness, highNum, lowNum);
			bin[i+1] = sum;
		}
		
		for(int i=0; i<bin.length; i++){
			bin[i] = bin[i]/sum;
		}
		
		for(int i=0; i<population.size(); i++){
			double random = Math.random();
			
			int index = 0;
			for(int j=0; j<bin.length-1; j++){
				if(bin[j] < random && bin[j+1] >= random){
					index = j;
					break;
				}
			}
			
			selectedPopluation.add(population.get(index));
		}
		
		return selectedPopluation;
	}
	
	/**
	 * since the fitness function will be negative, therefore, I normalize the value
	 * to positive by adding the number of all constraints, that is, h+l+(h^2-h)=l+h^2.
	 * 
	 * By this means, all the fitness value will be positive.
	 * 
	 * @param fitness
	 * @param highNum
	 * @param lowNum
	 * @return
	 */
	private double normalizeFitnessValue(double fitness, int highNum, int lowNum){
		return fitness + highNum*highNum + lowNum;
	}
	
	

	/**
	 * In this method, the weight vector and x0 vector will be initialized as well. This may not
	 * be well from an engineering point of view, but may save some computation.
	 * @param similarityTable
	 * @param modules
	 * @param units
	 * @return
	 */
	private Matrix extractRelation(double[][] similarityTable, ArrayList<ModuleWrapper> modules, ArrayList<? extends LowLevelGraphNode> lowLevelNodes){
		int highLevelNumber = modules.size();
		int lowLevelNumber = lowLevelNodes.size();
		
		Matrix relationMatrix = new Matrix(highLevelNumber, lowLevelNumber);
		
		ArrayList<Double> weightVectorList = new ArrayList<>();
		ArrayList<Integer> x0VectorList = new ArrayList<>();
		ArrayList<int[]> relationMap = new ArrayList<>();
		HashMap<ModuleUnitCorrespondence, Integer> reverseRelationMap = new HashMap<>();
		
		Integer mapIndex = 0;
		for(int i=0; i<highLevelNumber; i++){
			for(int j=0; j<lowLevelNumber; j++){
				if(similarityTable[i][j] >= Double.valueOf(ReflexactoringUtil.getMappingThreshold())){
					relationMatrix.set(i, j, 1);
					
					int[] moduleUnitPair = new int[]{i, j};
					relationMap.add(moduleUnitPair);
					reverseRelationMap.put(new ModuleUnitCorrespondence(i, j), mapIndex++);
					/**
					 * initial weight
					 */
					weightVectorList.add(similarityTable[i][j]);
					/**
					 * initial x0
					 */
					LowLevelGraphNode node = lowLevelNodes.get(j);
					ModuleWrapper module = modules.get(i);
					if(module.equals(node.getMappingModule())){
						x0VectorList.add(1);
					}
					else{
						x0VectorList.add(0);
					}
				}
				else{
					relationMatrix.set(i, j, 0);
				}
			}
		}
		
		this.weightVector = GeneticUtil.convertRowVectorToMatrix(weightVectorList);
		this.x0Vector = GeneticUtil.convertColumnVectorToMatrx(x0VectorList);
		this.relationMap = relationMap;
		this.reverseRelationMap = reverseRelationMap;
		
		return relationMatrix;
	}
	
	/*private Matrix extractMemberLevelRelation(double[][] similarityTable, ArrayList<ModuleWrapper> modules, ArrayList<UnitMemberWrapper> members){
		int highLevelNumber = modules.size();
		int lowLevelNumber = members.size();
		
		Matrix relationMatrix = new Matrix(highLevelNumber, lowLevelNumber);
		
		ArrayList<Double> weightVectorList = new ArrayList<>();
		ArrayList<Integer> x0VectorList = new ArrayList<>();
		ArrayList<int[]> relationMap = new ArrayList<>();
		
		for(int i=0; i<highLevelNumber; i++){
			for(int j=0; j<lowLevelNumber; j++){
				relationMatrix.set(i, j, 1);
				relationMap.add(new int[]{i, j});
				*//**
				 * initial weight
				 *//*
				weightVectorList.add(similarityTable[i][j]);
				*//**
				 * initial x0
				 *//*
				ICompilationUnitWrapper unit = members.get(j).getUnitWrapper();
				ModuleWrapper module = modules.get(i);
				if(module.equals(unit.getMappingModule())){
					x0VectorList.add(1);
				}
				else{
					x0VectorList.add(0);
				}
			}
		}
		
		this.weightVector = GeneticUtil.convertRowVectorToMatrix(weightVectorList);
		this.x0Vector = GeneticUtil.convertColumnVectorToMatrx(x0VectorList);
		this.relationMap = relationMap;
		
		return relationMatrix;
	}*/
	
	private Matrix extractGraph(ArrayList<? extends GraphNode> nodes){
		
		int dimension = nodes.size();
		Matrix graphMatrix = new Matrix(dimension, dimension);
		
		for(int i=0; i<dimension; i++){
			for(int j=0; j<dimension; j++){
				if(i != j){
					GraphNode nodeI = nodes.get(i);
					GraphNode nodeJ = nodes.get(j);
					
					if(nodeI.getCalleeList().contains(nodeJ)){
						graphMatrix.set(i, j, 1);
					}
					else{
						graphMatrix.set(i, j, 0);
					}
				}
			}
		}
		
		return graphMatrix;
	}
}
