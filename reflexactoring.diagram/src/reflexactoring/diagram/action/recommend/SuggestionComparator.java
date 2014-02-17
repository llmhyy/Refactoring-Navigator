/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.Comparator;


/**
 * @author linyun
 *
 */
public class SuggestionComparator implements Comparator<Suggestion> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Suggestion sug1, Suggestion sug2) {
		double confidence1 = sug1.computeConfidence();
		double confidence2 = sug2.computeConfidence();
		
		return (confidence1>confidence2 ? -1 : (confidence1==confidence2 ? 0 : 1));
	}

}
