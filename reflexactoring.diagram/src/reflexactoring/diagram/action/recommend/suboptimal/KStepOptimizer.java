/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.ModuleWrapper;

/**
 * @author linyun
 *
 */
public class KStepOptimizer extends Suboptimizer{

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.suboptimal.Suboptimizer#computeOptimalResult(reflexactoring.diagram.action.recommend.suboptimal.FitnessComputingFactor, java.util.ArrayList, java.util.ArrayList, double[][])
	 */
	@Override
	protected Genotype computeOptimalResult(FitnessComputingFactor computingFactor,
			ArrayList<ModuleWrapper> modules, ArrayList<? extends LowLevelGraphNode> lowLevelNodes,
			double[][] similarityTable) {

		return null;
		
	}
	
}
