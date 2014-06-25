/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimalold_representation;

/**
 * @author linyun
 *
 */
public interface Crossoverer {
	public Population crossoverAndMutate(Population selectedPopulation, FitnessComputingFactor computingFactor);
}
