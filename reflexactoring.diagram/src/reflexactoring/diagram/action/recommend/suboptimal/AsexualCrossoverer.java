/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

/**
 * @author linyun
 *
 */
public interface AsexualCrossoverer extends Crossoverer {
	public Genotype produceOffSpring(Genotype gene);
}
