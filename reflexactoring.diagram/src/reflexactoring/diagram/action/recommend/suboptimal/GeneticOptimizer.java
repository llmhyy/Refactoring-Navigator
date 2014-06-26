/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;


/**
 * @author linyun
 *
 */
public class GeneticOptimizer {
	
	private Population population;
	private Selector selector;
	private Crossoverer crossoverer;
	private Mutator mutator;
	
	private int iterationNum;
	/**
	 * @param population
	 * @param selector
	 * @param crossoverer
	 * @param mutator
	 */
	public GeneticOptimizer(Population population, Selector selector,
			Crossoverer crossoverer, Mutator mutator) {
		super();
		this.population = population;
		this.selector = selector;
		this.crossoverer = crossoverer;
		this.mutator = mutator;
	}
	
	public Population optimize(){
		Population pop = null;
		for(int i=0; i<iterationNum; i++){			
			/**
			 * derive a new generation.
			 */
			Population newPop = this.crossoverer.deriveGeneration(this.population);
			/**
			 * mutate some of the genotypes.
			 */
			pop = this.mutator.mutate(pop);		
			/**
			 * select the best ones of both generations.
			 */
			pop = this.selector.selectPopulation(this.population, newPop);
		}
		
		return pop;
	}

	/**
	 * @param population the population to set
	 */
	public void setPopulation(Population population) {
		this.population = population;
	}

	/**
	 * @param selector the selector to set
	 */
	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	/**
	 * @param crossoverer the crossoverer to set
	 */
	public void setCrossoverer(Crossoverer crossoverer) {
		this.crossoverer = crossoverer;
	}

	/**
	 * @return the mutator
	 */
	public Mutator getMutator() {
		return mutator;
	}

	/**
	 * @param mutator the mutator to set
	 */
	public void setMutator(Mutator mutator) {
		this.mutator = mutator;
	}

	/**
	 * @param iterationNum the iterationNum to set
	 */
	public void setIterationNum(int iterationNum) {
		this.iterationNum = iterationNum;
	}
	
	
}
