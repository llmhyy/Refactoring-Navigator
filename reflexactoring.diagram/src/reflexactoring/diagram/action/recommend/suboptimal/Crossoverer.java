/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

/**
 * @author linyun
 *
 */
public interface Crossoverer {

	/**
	 * @param population
	 * @return
	 */
	Population deriveGeneration(Population population);

}
