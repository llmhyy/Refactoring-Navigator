/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import Jama.Matrix;
import reflexactoring.diagram.action.ModelMapper;
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
	
	private Matrix weightVector;
	private Matrix x0Vector;
	
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
		
		/**
		 * In this method, the weight vector and x0 vector will be initialized as well.
		 */
		Matrix relationMatrix = extractRelation(similarityTable, modules, units);
		
		System.out.println("The variable number is: " + this.weightVector.getColumnDimension());
		
		Matrix highLevelMatrix = extractGraph(modules);
		Matrix lowLevelMatrix = extractGraph(units);
		
		FitnessComputingFactor computingFactor = new FitnessComputingFactor();
		computingFactor.setHighLevelMatrix(highLevelMatrix);
		computingFactor.setLowLevelMatrix(lowLevelMatrix);
		computingFactor.setRelationMatrix(relationMatrix);
		computingFactor.setWeightVector(weightVector);
		computingFactor.setX0Vector(x0Vector);
		
		Genotype gene = computeOptimalResult(computingFactor);
		
		return gene;
	}
	
	/**
	 * 
	 */
	private Genotype computeOptimalResult(FitnessComputingFactor computingFactor) {
		
		Population population = generatePopulation(computingFactor);
		
		for(int i=0; i<Settings.geneticIterationNum; i++){
			population = generateNextGeneration(population, computingFactor);
			population.updateOptimalGene();
		}
		
		return population.getOptimalGene();
	}
	
	private Population generatePopulation(FitnessComputingFactor computingFactor){
		int dimension = computingFactor.getX0Vector().getRowDimension();
		
		Population population = new Population();
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
			subGene1.setFitness(subGene2.computeFitness(computingFactor));
			
			crosssoverPopulation.add(subGene1);
			crosssoverPopulation.add(subGene2);
		}
		
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
	private Matrix extractRelation(double[][] similarityTable, ArrayList<ModuleWrapper> modules, ArrayList<ICompilationUnitWrapper> units){
		int highLevelNumber = modules.size();
		int lowLevelNumber = units.size();
		
		Matrix relationMatrix = new Matrix(highLevelNumber, lowLevelNumber);
		
		ArrayList<Double> weightVectorList = new ArrayList<>();
		ArrayList<Integer> x0VectorList = new ArrayList<>();
		
		for(int i=0; i<highLevelNumber; i++){
			for(int j=0; j<lowLevelNumber; j++){
				if((i != j) && similarityTable[i][j] >= Double.valueOf(ReflexactoringUtil.getMappingThreshold())){
					relationMatrix.set(i, j, 1);
					
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
				else{
					relationMatrix.set(i, j, 0);
				}
			}
		}
		
		this.weightVector = GeneticUtil.convertRowVectorToMatrix(weightVectorList);
		this.x0Vector = GeneticUtil.convertColumnVectorToMatrx(x0VectorList);
		
		return relationMatrix;
	}
	
	
	
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
