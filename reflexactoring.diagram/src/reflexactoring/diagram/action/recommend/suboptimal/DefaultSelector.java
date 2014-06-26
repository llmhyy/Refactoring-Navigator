/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Find the top half and the third half as the next generation.
 * 
 * @author linyun
 *
 */
public class DefaultSelector implements Selector {

	@Override
	public Population selectPopulation(Population population, Population newPop) {
		ArrayList<Genotype> geneList = new ArrayList<>();
		geneList.addAll(population.getList());
		geneList.addAll(newPop.getList());
		
		Collections.sort(geneList, new GenoTypeComparator());
		
		Population pop = new Population();
		for(int i=0; i<pop.getSize()*2; i++){
			if(i<pop.getSize()/2 || (i>=pop.getSize() && i<pop.getSize()+pop.getSize()/2)){
				pop.add(geneList.get(i));
			}
		}
		
		return pop;
	}

}
