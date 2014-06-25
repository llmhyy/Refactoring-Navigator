/**
 * 
 */
package reflexactoring.diagram.suboptimal;

import java.util.ArrayList;
import java.util.Arrays;

import reflexactoring.diagram.action.recommend.suboptimalold_representation.Crossoverer;
import reflexactoring.diagram.action.recommend.suboptimalold_representation.FitnessComputingFactor;
import reflexactoring.diagram.action.recommend.suboptimalold_representation.Genotype;
import reflexactoring.diagram.action.recommend.suboptimalold_representation.OriginOrientedSeedGenerator;
import reflexactoring.diagram.action.recommend.suboptimalold_representation.Population;
import reflexactoring.diagram.action.recommend.suboptimalold_representation.RandomWalkerCrossoverer;
import reflexactoring.diagram.action.recommend.suboptimalold_representation.SeedGenerator;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 * This class is used for testing the efficiency of random walk optimization.
 * 
 * @author linyun
 *
 */
public class Problem {
	
	public Problem(/*int highLevelNum, int lowLevelNum, int iterNum, int populationNum*/){
		//run(highLevelNum, lowLevelNum, iterNum, populationNum);
	}
	
	public double runExact(FitnessComputingFactor computingFactor){
		
		
		int dimension = computingFactor.getHighLevelMatrix().rows()*computingFactor.getLowLevelMatrix().rows();
		
		ArrayList<ArrayList<Integer>> previousLevelList = null;
		int level = 1;
		for(int i=1; i<=dimension; i++){
			ArrayList<Integer> array1 = new ArrayList<>();
			ArrayList<Integer> array2 = new ArrayList<>();
			array1.add(0);
			array2.add(1);
			if(level == 1){
				ArrayList<ArrayList<Integer>> list = new ArrayList<>();
				list.add(array1);
				list.add(array2);
				previousLevelList = list;
			}
			else{
				ArrayList<ArrayList<Integer>> list = new ArrayList<>();
				for(ArrayList<Integer> previousList: previousLevelList){
					ArrayList<Integer> thisList1 = new ArrayList<>();
					thisList1.add(0);
					thisList1.addAll(previousList);
					ArrayList<Integer> thisList2 = new ArrayList<>();
					thisList2.add(1);
					thisList2.addAll(previousList);
					
					list.add(thisList1);
					list.add(thisList2);
				}
				
				previousLevelList = list;
			}
			level++;
		}
		
		
		System.out.println(previousLevelList.size());
		Double fitness = null; 
		Solution bestSolution = null;
		Solution previousSolution = null;
		for(ArrayList<Integer> solution: previousLevelList){
			
			int[] DNA = convert(solution);
			if(fitness == null){
				Solution sol = new Solution(DNA);
				sol.computeFitness(computingFactor);
				
				fitness = sol.getFitness();
				previousSolution = sol;
			}
			else{
				Solution sol = previousSolution.clone();
				sol.setDNA(DNA);
				sol.computeFitness(computingFactor);
				if(sol.getFitness() > fitness){
					fitness = sol.getFitness();
					bestSolution = sol;
				}
			}
			//System.out.println(fitness);
		}
		System.out.println(bestSolution);
		return fitness;
	}
	
	private int[] convert(ArrayList<Integer> solution){
		int[] DNA = new int[solution.size()];
		for(int i=0; i<DNA.length; i++){
			DNA[i] = solution.get(i);
		}
		
		return DNA;
	}
	
	public double runMetaHeuristic(int iterNum, int populationNum, FitnessComputingFactor computingFactor, int STOP){

		
		SeedGenerator seedGenerator = new OriginOrientedSeedGenerator(computingFactor);
		Population population = generatePopulation(computingFactor, seedGenerator, populationNum);
		System.out.println(population.get(0).getFitness());
		
		double fitness = population.get(0).getFitness();
		int stopDuration = STOP;
		
		for(int i=0; i<iterNum; i++){
			population = generateNextGeneration(population, computingFactor, null);
			System.out.println(population.get(0).getFitness());
			
			if(population.get(0).getFitness() == fitness){
				stopDuration--;
			}
			else{
				fitness = population.get(0).getFitness();
				stopDuration = STOP;
			}
			
			if(stopDuration == 0){
				System.out.println("Convergence Number: " + (i+1-STOP));
				break;
			}
		}
		System.out.println(population.get(0));
		return population.get(0).getFitness();
	}
	
	private FitnessComputingFactor buildComputingFactor(int highLevelNum, int lowLevelNum){
		//SparseDoubleMatrix2D highLevelMatrix = generateRandomGraphMatrix(highLevelNum);
		//SparseDoubleMatrix2D lowLevelMatrix = generateRandomGraphMatrix(lowLevelNum);
		SparseDoubleMatrix2D highLevelMatrix = generateConstantGraphMatrix(highLevelNum);
		SparseDoubleMatrix2D lowLevelMatrix = generateConstantGraphMatrix(lowLevelNum);
		SparseDoubleMatrix2D relationMatrix = generateRelationMatrix(highLevelNum, lowLevelNum);
		
		//SparseDoubleMatrix1D x0Vector = generateRandomX0Vector(highLevelMatrix, lowLevelMatrix, relationMatrix);
		//SparseDoubleMatrix1D weightVector = generateRandomWeight(x0Vector);
		SparseDoubleMatrix1D x0Vector = generateConstantX0Vector(highLevelMatrix, lowLevelMatrix, relationMatrix);
		SparseDoubleMatrix1D weightVector = generateConstantWeight(x0Vector);
		
		FitnessComputingFactor computingFactor = new FitnessComputingFactor();
		computingFactor.setHighLevelMatrix(highLevelMatrix);
		computingFactor.setLowLevelMatrix(lowLevelMatrix);
		computingFactor.setRelationMatrix(relationMatrix);
		computingFactor.setWeightVector(weightVector);
		computingFactor.setX0Vector(x0Vector);
		computingFactor.initializeEdgeVertexMatrix();
		
		return computingFactor;
	}

	private Population generateNextGeneration(Population population, 
			FitnessComputingFactor computingFactor, ArrayList<int[]> relationMap){
		//long t1 = System.currentTimeMillis();
		//Crossoverer crossoverer = new RandomCrossoverer(computingFactor);
		//Crossoverer crossoverer = new SinglePointCrossoverer(computingFactor);
		Crossoverer crossoverer = new RandomWalkerCrossoverer(computingFactor, relationMap);
		//Crossoverer crossoverer = new ViolationReductionOrientedCrossoverer(computingFactor);
				
		Population crossedPopulation = crossoverer.crossoverAndMutate(population, computingFactor);
		//Population crossedPopulation = crossoverAndMutate(population, computingFactor);
		//long t2 = System.currentTimeMillis();
		//System.out.println(t2-t1);
		return crossedPopulation;
	}
	
	private Population generatePopulation(FitnessComputingFactor computingFactor, 
			SeedGenerator seedGenerator, int populationNum){
		
		Population population = new Population();
		for(int i=0; i<Integer.valueOf(populationNum); i++){
			int[] seed = seedGenerator.generateSeed();
			
			Solution gene = new Solution(seed);
			gene.computeFitness(computingFactor);
			
			population.add(gene);
		}
		
		//population.updateOptimalGene();
		return population;
	}
	
	private SparseDoubleMatrix1D generateRandomX0Vector(SparseDoubleMatrix2D highLevelMatrix, 
			SparseDoubleMatrix2D lowLevelMatrix, SparseDoubleMatrix2D relationMatrix){
		SparseDoubleMatrix1D x0Vector = new SparseDoubleMatrix1D(highLevelMatrix.rows()*lowLevelMatrix.rows());
		
		for(int j=0; j<lowLevelMatrix.rows(); j++){
			int rowNo = (int)(Math.random()*highLevelMatrix.rows());
			
			x0Vector.set(rowNo*lowLevelMatrix.rows()+j, 1);	
		}
		//System.currentTimeMillis();
		return x0Vector;
	}
	
	private SparseDoubleMatrix1D generateConstantX0Vector(SparseDoubleMatrix2D highLevelMatrix, 
			SparseDoubleMatrix2D lowLevelMatrix, SparseDoubleMatrix2D relationMatrix){
		SparseDoubleMatrix1D x0Vector = new SparseDoubleMatrix1D(highLevelMatrix.rows()*lowLevelMatrix.rows());
		
		for(int j=0; j<lowLevelMatrix.rows(); j++){
			int rowNo = j%highLevelMatrix.rows();
			
			x0Vector.set(rowNo*lowLevelMatrix.rows()+j, 1);	
		}
		//System.currentTimeMillis();
		return x0Vector;
	}
	
	private SparseDoubleMatrix1D generateRandomWeight(SparseDoubleMatrix1D x0){
		SparseDoubleMatrix1D weights = new SparseDoubleMatrix1D(x0.size());
		for(int i=0; i<weights.size(); i++){
			weights.set(i, Math.random());
		}
		
		return weights;
	}
	
	private SparseDoubleMatrix1D generateConstantWeight(SparseDoubleMatrix1D x0){
		SparseDoubleMatrix1D weights = new SparseDoubleMatrix1D(x0.size());
		for(int i=0; i<weights.size(); i++){
			weights.set(i, 0.3);
		}
		
		return weights;
	}
	
	private SparseDoubleMatrix2D generateRelationMatrix(int m, int n){
		SparseDoubleMatrix2D matrix = new SparseDoubleMatrix2D(m, n);
		for(int i=0; i<m; i++){
			for(int j=0; j<n; j++){
				matrix.set(i, j, 1);
			}
		}
		
		return matrix;
	}
	
	private SparseDoubleMatrix2D generateRandomGraphMatrix(int n){
		SparseDoubleMatrix2D matrix = new SparseDoubleMatrix2D(n, n);
		for(int i=0; i<n; i++){
			for(int j=0; j<n; j++){
				if(i != j){
					double value = Math.random();
					value = (value > 0.5)? 1: 0;
					matrix.set(i, j, value);
				}
			}
		}
		
		return matrix;
	}
	
	private SparseDoubleMatrix2D generateConstantGraphMatrix(int n){
		SparseDoubleMatrix2D matrix = new SparseDoubleMatrix2D(n, n);
		for(int i=0; i<n; i++){
			for(int j=0; j<n; j++){
				if(i != j){
					int value = i+j;
					value = (value%2 == 0)? 1: 0;
					matrix.set(i, j, value);
				}
			}
		}
		
		return matrix;
	}
	
	public static void main(String[] args){
		Problem problem = new Problem();
		System.out.println("Started");
		FitnessComputingFactor computingFactor = problem.buildComputingFactor(3, 110);
		computingFactor.setMode(FitnessComputingFactor.EXPERIMENT_MODE);
		computingFactor.setAlpha(0.5);
		computingFactor.setBeta(0);
		
		long t11 = System.currentTimeMillis();
		double subOptimal = problem.runMetaHeuristic(1000, 50, computingFactor, 200);
		long t12 = System.currentTimeMillis();
		
		/*long t21 = System.currentTimeMillis();
		double optimal = problem.runExact(computingFactor);
		long t22 = System.currentTimeMillis();*/
		
		System.out.println("Suboptimal: " + subOptimal + ", Consumed time: " + (t12-t11));
		//System.out.println("Optimal: " + optimal + ", Consumed time: " + (t22-t21));
	}
}
