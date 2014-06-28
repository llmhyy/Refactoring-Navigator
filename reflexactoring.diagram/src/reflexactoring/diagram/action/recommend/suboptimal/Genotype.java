/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * 
 * 
 * @author linyun
 * 
 */
public class Genotype {
	private int[] DNA;
	private int[] originalDNA;
	private double fitness;
	
	private FitnessEvaluator evaluator;
	/**
	 * @param DNA
	 */
	public Genotype(int[] DNA, int[] originalDNA, FitnessEvaluator evaluator) {
		super();
		this.DNA = DNA;
		this.setOriginalDNA(originalDNA);
		this.setEvaluator(evaluator);
	}

	/**
	 * @return the DNA
	 */
	public int[] getDNA() {
		return DNA;
	}

	public void setDNA(int[] DNA) {
		this.DNA = DNA;
	}

	public int getLength() {
		return DNA.length;
	}

	/**
	 * @return the fitness
	 */
	public double getFitness() {
		
		if(fitness == 0){
			fitness = evaluator.computeFitness(this);
		}
		
		return fitness;
	}
	
	public boolean isFeasible(){
		return this.evaluator.isFeasible();
	}

	/**
	 * @param fitness
	 *            the fitness to set
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * @return the originalDNA
	 */
	public int[] getOriginalDNA() {
		return originalDNA;
	}

	/**
	 * @param originalDNA the originalDNA to set
	 */
	public void setOriginalDNA(int[] originalDNA) {
		this.originalDNA = originalDNA;
	}

	/**
	 * @return the evaluator
	 */
	public FitnessEvaluator getEvaluator() {
		return evaluator;
	}

	/**
	 * @param evaluator the evaluator to set
	 */
	public void setEvaluator(FitnessEvaluator evaluator) {
		this.evaluator = evaluator;
	}
}
