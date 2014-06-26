/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.Comparator;

/**
 * @author linyun
 *
 */
public class GenoTypeComparator implements Comparator<Genotype> {

	
	@Override
	public int compare(Genotype o1, Genotype o2) {
		if(o1.getFitness() == o2.getFitness()){
			return 0;
		}
		else if(o1.getFitness() > o2.getFitness()){
			return -1;
		}
		else{
			return 1;
		}
	}
}
