/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

/**
 * @author linyun
 *
 */
public class Suggestion extends ArrayList<SuggestionMove>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 633724196273839939L;

	private boolean isFeasible;

	/**
	 * @return the isFeasible
	 */
	public boolean isFeasible() {
		return isFeasible;
	}

	/**
	 * @param isFeasible the isFeasible to set
	 */
	public void setFeasible(boolean isFeasible) {
		this.isFeasible = isFeasible;
	}
	
	
}
