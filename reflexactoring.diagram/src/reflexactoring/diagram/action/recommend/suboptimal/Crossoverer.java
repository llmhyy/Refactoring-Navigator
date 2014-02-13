/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

/**
 * @author linyun
 *
 */
public interface Crossoverer {
	public Population crossoverAndMutate(Population selectedPopulation, FitnessComputingFactor computingFactor);
}
