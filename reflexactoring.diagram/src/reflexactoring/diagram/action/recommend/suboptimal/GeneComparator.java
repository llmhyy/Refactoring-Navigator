/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.Comparator;

/**
 * @author linyun
 *
 */
public class GeneComparator implements Comparator<Genotype> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Genotype gene1, Genotype gene2) {
		double fitness1 = gene1.getFitness();
		double fitness2 = gene2.getFitness();
		
		return (fitness1>fitness2 ? -1 : (fitness1==fitness2 ? 0 : 1));
	}

}
