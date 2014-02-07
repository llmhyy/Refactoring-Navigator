/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

/**
 * @author linyun
 * 
 */
public class RandomSeedGenerator implements SeedGenerator {

	private int dimension;

	/**
	 * @param dimension
	 */
	public RandomSeedGenerator(int dimension) {
		super();
		this.dimension = dimension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * reflexactoring.diagram.action.recommend.suboptimal.PopulationGenerator
	 * #generatePopulation()
	 */
	@Override
	public int[] generateSeed() {

		int[] seed = new int[dimension];
		for (int j = 0; j < dimension; j++) {
			if (Math.random() >= 0.5) {
				seed[j] = 1;
			} else {
				seed[j] = 0;
			}
		}

		return seed;
	}

	/**
	 * @return the dimension
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * @param dimension
	 *            the dimension to set
	 */
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

}
