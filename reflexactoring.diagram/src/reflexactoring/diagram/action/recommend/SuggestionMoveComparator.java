/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.Comparator;


/**
 * @author linyun
 *
 */
public class SuggestionMoveComparator implements Comparator<SuggestionMove> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(SuggestionMove sug1, SuggestionMove sug2) {
		double confidence1 = sug1.computeConfidence();
		double confidence2 = sug2.computeConfidence();
		
		return (confidence1>confidence2 ? -1 : (confidence1==confidence2 ? 0 : 1));
	}

}
