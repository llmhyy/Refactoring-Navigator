/**
 * 
 */
package reflexactoring.diagram.suboptimal;

import java.util.ArrayList;

import reflexactoring.diagram.action.recommend.suboptimal.Crossoverer;
import reflexactoring.diagram.action.recommend.suboptimal.FitnessComputingFactor;
import reflexactoring.diagram.action.recommend.suboptimal.Genotype;
import reflexactoring.diagram.action.recommend.suboptimal.OriginOrientedSeedGenerator;
import reflexactoring.diagram.action.recommend.suboptimal.Population;
import reflexactoring.diagram.action.recommend.suboptimal.RandomWalkerCrossoverer;
import reflexactoring.diagram.action.recommend.suboptimal.SeedGenerator;
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
	
	public void run(int highLevelNum, int lowLevelNum, int iterNum, int populationNum, double alpha, double beta){
		FitnessComputingFactor computingFactor = buildComputingFactor(highLevelNum, lowLevelNum);
		computingFactor.setMode(FitnessComputingFactor.EXPERIMENT_MODE);
		computingFactor.setAlpha(alpha);
		computingFactor.setBeta(beta);
		
		SeedGenerator seedGenerator = new OriginOrientedSeedGenerator(computingFactor);
		
		Population population = generatePopulation(computingFactor, seedGenerator, populationNum);
		System.out.println(population.getOptimalGene().getFitness());
		
		for(int i=0; i<iterNum; i++){
			population = generateNextGeneration(population, computingFactor, null);
			System.out.println(population.getOptimalGene().getFitness());
		}
	}
	
	private FitnessComputingFactor buildComputingFactor(int highLevelNum, int lowLevelNum){
		SparseDoubleMatrix2D highLevelMatrix = generateRandomGraphMatrix(highLevelNum);
		SparseDoubleMatrix2D lowLevelMatrix = generateRandomGraphMatrix(lowLevelNum);
		SparseDoubleMatrix2D relationMatrix = generateRelationMatrix(highLevelNum, lowLevelNum);
		
		SparseDoubleMatrix1D x0Vector = generateX0Vector(highLevelMatrix, lowLevelMatrix, relationMatrix);
		SparseDoubleMatrix1D weightVector = generateRandomWeight(x0Vector);
		
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
			
			Genotype gene = new Genotype(seed);
			gene.computeFitness(computingFactor);
			
			population.add(gene);
		}
		
		population.updateOptimalGene();
		return population;
	}
	
	private SparseDoubleMatrix1D generateX0Vector(SparseDoubleMatrix2D highLevelMatrix, 
			SparseDoubleMatrix2D lowLevelMatrix, SparseDoubleMatrix2D relationMatrix){
		SparseDoubleMatrix1D x0Vector = new SparseDoubleMatrix1D(highLevelMatrix.rows()*lowLevelMatrix.rows());
		
		for(int j=0; j<lowLevelMatrix.rows(); j++){
			int rowNo = (int)(Math.random()*highLevelMatrix.rows());
			
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
	
	public static void main(String[] args){
		Problem problem = new Problem();
		problem.run(4, 25, 2000, 20, 0.5, 0.5);
	}
}
