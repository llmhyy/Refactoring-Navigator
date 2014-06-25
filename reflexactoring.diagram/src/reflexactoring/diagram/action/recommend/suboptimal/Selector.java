/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

/**
 * @author linyun
 *
 */
public interface Selector {

	/**
	 * @param population
	 * @param newPop
	 * @return
	 */
	Population selectPopulation(Population population, Population newPop);

}
