/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
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
public class GeneticOptimizer extends Suboptimizer{
	/**
	 * 
	 */
	@Override
	protected Genotype computeOptimalResult(FitnessComputingFactor computingFactor,
			ArrayList<ModuleWrapper> modules, ArrayList<? extends LowLevelGraphNode> lowLevelNodes, 
			double[][] similarityTable, ArrayList<int[]> relationMap, IProgressMonitor monitor) {
		
		/*int dimension = computingFactor.getX0Vector().size();
		SeedGenerator seedGenerator = new ConstrainedSeedGenerator(dimension, modules, lowLevelNodes, 
				similarityTable, reverseRelationMap);*/
		SeedGenerator seedGenerator = new OriginOrientedSeedGenerator(computingFactor);
		
		Population population = generatePopulation(computingFactor, seedGenerator);
		System.out.println(population.getOptimalGene().getFitness());
		
		//Integer.valueOf(ReflexactoringUtil.getIterationNumber())
		for(int i=0; i<Integer.valueOf(ReflexactoringUtil.getIterationNumber()); i++){
			population = generateNextGeneration(population, computingFactor, relationMap);
			System.out.println(population.getOptimalGene().getFitness());
			
			monitor.worked(1);
			if(monitor.isCanceled()){
				break;
			}
		}
		
		return population.getOptimalGene();
	}
	
	
	private Population generatePopulation(FitnessComputingFactor computingFactor, 
			SeedGenerator seedGenerator){
		
		//int dimension = computingFactor.getX0Vector().getRowDimension();
		
		Population population = new Population();
		for(int i=0; i<Integer.valueOf(ReflexactoringUtil.getPopulationSize()); i++){
			
			//int[] seed = randomSeed(dimension);
			
			int[] seed = seedGenerator.generateSeed();
			
			Genotype gene = new Genotype(seed);
			gene.computeFitness(computingFactor);
			
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
	private Population generateNextGeneration(Population population, 
			FitnessComputingFactor computingFactor, ArrayList<int[]> relationMap){
		
		/**
		 * selector: roulette wheel selection
		 */
		//Population selectedPopulation = selectPopulation(population, computingFactor);
		
		/**
		 * crossover and mutate
		 */
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
}
