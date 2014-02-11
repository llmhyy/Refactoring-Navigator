/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jdt.core.dom.CompilationUnit;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
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
	
	private SparseDoubleMatrix1D weightVector;
	private SparseDoubleMatrix1D x0Vector;
	
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
	
	
	
	public ArrayList<int[]> getRelationMap(){
		return this.relationMap;
	}
	
	/**
	 * this method should be invoked after calling optimize().
	 * @return
	 */
	public int[] getX0(){
		int[] x0 = new int[x0Vector.size()];
		
		for(int i=0; i<x0Vector.size(); i++){
			x0[i] = (int) x0Vector.get(i);
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
		SparseDoubleMatrix2D relationMatrix = extractRelation(similarityTable, modules, lowLevelUnits);
		
		System.out.println("The variable number is: " + this.weightVector.size());
		
		SparseDoubleMatrix2D highLevelMatrix = extractGraph(modules);
		SparseDoubleMatrix2D lowLevelMatrix = extractGraph(lowLevelUnits);
		
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
		
		int dimension = computingFactor.getX0Vector().size();
		SeedGenerator seedGenerator = new ConstrainedSeedGenerator(dimension, modules, lowLevelNodes, 
				similarityTable, reverseRelationMap);
		
		Population population = generatePopulation(computingFactor, seedGenerator);
		System.out.println(population.getOptimalGene().getFitness());
		
		//Integer.valueOf(ReflexactoringUtil.getIterationNumber())
		for(int i=0; i<Integer.valueOf(ReflexactoringUtil.getIterationNumber()); i++){
			population = generateNextGeneration(population, computingFactor);
			System.out.println(population.getOptimalGene().getFitness());
		}
		
		return population.getOptimalGene();
	}
	
	private Population generatePopulation(FitnessComputingFactor computingFactor, SeedGenerator seedGenerator){
		
		//int dimension = computingFactor.getX0Vector().getRowDimension();
		
		Population population = new Population();
		for(int i=0; i<Integer.valueOf(ReflexactoringUtil.getPopulationSize()); i++){
			
			//int[] seed = randomSeed(dimension);
			
			int[] seed = seedGenerator.generateSeed();
			
			Genotype gene = new Genotype(seed);
			gene.setFitness(gene.computeFitness(computingFactor));
			
			population.add(gene);
		}
		
		population.updateOptimalGene();
		
		return population;
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
		//Population selectedPopulation = selectPopulation(population, computingFactor);
		
		/**
		 * crossover and mutate
		 */
		//long t1 = System.currentTimeMillis();
		Population crossedPopulation = crossoverAndMutate(population, computingFactor);
		//long t2 = System.currentTimeMillis();
		//System.out.println(t2-t1);
		
		return crossedPopulation;
	}
	
	/**
	 * @param selectedPopulation
	 * @return
	 */
	private Population crossoverAndMutate(Population selectedPopulation, FitnessComputingFactor computingFactor) {
		
		Crossoverer crossoverer = new RandomCrossoverer(computingFactor);
		//Crossoverer crossoverer = new SinglePointCrossoverer(computingFactor);
		//Crossoverer crossoverer = new RandomWalkerCrossoverer(computingFactor);
		//Crossoverer crossoverer = new ViolationReductionOrientedCrossoverer(computingFactor);
		
		
		Population crosssoverPopulation = new Population();
		
		
		for(int i=0; i<selectedPopulation.size(); i=i+2){
			Genotype gene1 = selectedPopulation.get(i);
			Genotype gene2 = selectedPopulation.get(i+1);
			
			GenoTypePair pair = new GenoTypePair(gene1, gene2);
			
			//long t1 = System.currentTimeMillis();
			GenoTypePair subPair = crossoverer.crossNewPair(pair);
			//long t2 = System.currentTimeMillis();
			//System.out.println(t2-t1);
			
			
			crosssoverPopulation.add(subPair.getGene1());
			crosssoverPopulation.add(subPair.getGene2());
		}
		
		//Collections.sort(selectedPopulation, new GeneComparator());
		crosssoverPopulation.addAll(selectedPopulation);
		
		Collections.sort(crosssoverPopulation, new GeneComparator());
		
		Population newCrossoverPopulation = new Population();
		
		int quater = crosssoverPopulation.size()/4;
		for(int i=0; i<2*quater; i++){
			if(i<quater){
				newCrossoverPopulation.add(crosssoverPopulation.get(i));
			}
			else{
				newCrossoverPopulation.add(crosssoverPopulation.get(quater+i));
			}
		}
		
		newCrossoverPopulation.setOptimalGene(selectedPopulation.getOptimalGene());
		newCrossoverPopulation.updateOptimalGene();
		
		return newCrossoverPopulation;
		
		
		/*crosssoverPopulation.setOptimalGene(selectedPopulation.getOptimalGene());
		crosssoverPopulation.updateOptimalGene();
		return crosssoverPopulation;*/
	}


	/**
	 * roulette wheel selection
	 * @param population
	 * @return
	 */
	private Population selectPopulation(Population population, FitnessComputingFactor computingFactor){
		
		int highNum = computingFactor.getHighLevelMatrix().rows(); 
		int lowNum = computingFactor.getLowLevelMatrix().rows();
		
		Population selectedPopluation = new Population();
		selectedPopluation.setOptimalGene(population.getOptimalGene());

		double[] bin = new double[population.size()+1];
		double sum = 0;
		bin[0] = 0;
		for(int i=0; i<population.size(); i++){
			double fitness = population.get(i).getFitness();
			sum += computingFactor.normalizeFitnessValue(fitness, highNum, lowNum);
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
	 * In this method, the weight vector and x0 vector will be initialized as well. This may not
	 * be well from an engineering point of view, but may save some computation.
	 * @param similarityTable
	 * @param modules
	 * @param units
	 * @return
	 */
	private SparseDoubleMatrix2D extractRelation(double[][] similarityTable, ArrayList<ModuleWrapper> modules, ArrayList<? extends LowLevelGraphNode> lowLevelNodes){
		int highLevelNumber = modules.size();
		int lowLevelNumber = lowLevelNodes.size();
		
		SparseDoubleMatrix2D relationMatrix = new SparseDoubleMatrix2D(highLevelNumber, lowLevelNumber);
		
		ArrayList<Double> weightVectorList = new ArrayList<>();
		ArrayList<Double> x0VectorList = new ArrayList<>();
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
						x0VectorList.add(1d);
					}
					else{
						x0VectorList.add(0d);
					}
				}
			}
		}
		
		this.weightVector = GeneticUtil.convertVectorToMatrix(weightVectorList);
		this.x0Vector = GeneticUtil.convertVectorToMatrix(x0VectorList);
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
	
	private SparseDoubleMatrix2D extractGraph(ArrayList<? extends GraphNode> nodes){
		
		int dimension = nodes.size();
		SparseDoubleMatrix2D graphMatrix = new SparseDoubleMatrix2D(dimension, dimension);
		
		for(int i=0; i<dimension; i++){
			for(int j=0; j<dimension; j++){
				if(i != j){
					GraphNode nodeI = nodes.get(i);
					GraphNode nodeJ = nodes.get(j);
					
					if(nodeI.getCalleeList().contains(nodeJ)){
						graphMatrix.set(i, j, 1);
					}
				}
			}
		}
		
		return graphMatrix;
	}
}
