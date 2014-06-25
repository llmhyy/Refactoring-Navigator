/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

/**
 * @author linyun
 *
 */
public class GeneticOptimzer {
	
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
	public GeneticOptimzer(Population population, Selector selector,
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
			Population newPop = this.crossoverer.deriveGeneration(this.population);
			pop = this.selector.selectPopulation(this.population, newPop);
			pop = this.mutator.mutate(pop);		
		}
		
		return pop;
	}

	/**
	 * @return the population
	 */
	public Population getPopulation() {
		return population;
	}

	/**
	 * @param population the population to set
	 */
	public void setPopulation(Population population) {
		this.population = population;
	}

	/**
	 * @return the selector
	 */
	public Selector getSelector() {
		return selector;
	}

	/**
	 * @param selector the selector to set
	 */
	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	/**
	 * @return the crossoverer
	 */
	public Crossoverer getCrossoverer() {
		return crossoverer;
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
	 * @return the iterationNum
	 */
	public int getIterationNum() {
		return iterationNum;
	}

	/**
	 * @param iterationNum the iterationNum to set
	 */
	public void setIterationNum(int iterationNum) {
		this.iterationNum = iterationNum;
	}
	
	
}
