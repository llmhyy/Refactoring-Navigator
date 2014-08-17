/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;


/**
 * @author linyun
 *
 */
public interface FitnessEvaluator {
	public static final int ADVANCED_EVALUATOR = 1;
	public static final int BALANCED_EVALUATOR = 2;
	
	
	public double computeFitness(Genotype gene);
	
	/**
	 * In this project, fitness evaluator should also be responsible for providing some violations
	 * @return
	 */
	public abstract ArrayList<Violation> getViolationList();
	
	/**
	 * this method should be computed after {@link #computeFitness(Genotype)}
	 * @return
	 */
	public boolean isFeasible();
}
