/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;


/**
 * @author linyun
 *
 */
public interface FitnessEvaluator {
	public double computeFitness(Genotype gene);
	/**
	 * this method should be computed after {@link #computeFitness(Genotype)}
	 * @return
	 */
	public boolean isFeasible();
}
