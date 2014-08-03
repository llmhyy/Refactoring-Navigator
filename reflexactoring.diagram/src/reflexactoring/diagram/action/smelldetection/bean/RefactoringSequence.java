/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.bean;

import java.util.ArrayList;

import reflexactoring.diagram.action.recommend.SuggestionMove;

/**
 * @author linyun
 *
 */
public class RefactoringSequence extends ArrayList<RefactoringSequenceElement>{

	private Double currentFitnessValue;
	
	private ArrayList<SuggestionMove> prerequisite;
	
	/**
	 * @return the prerequisite
	 */
	public ArrayList<SuggestionMove> getPrerequisite() {
		return prerequisite;
	}

	/**
	 * @param prerequisite the prerequisite to set
	 */
	public void setPrerequisite(ArrayList<SuggestionMove> prerequisite) {
		this.prerequisite = prerequisite;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4636296201584085682L;

	/**
	 * @param element
	 */
	public void addElement(RefactoringSequenceElement element) {
		this.add(element);
		this.currentFitnessValue = element.getFitnessValue();
	}

	/**
	 * @param element
	 * @return
	 */
	public boolean isAnImprovement(RefactoringSequenceElement element) {
		if(currentFitnessValue == null){
			return true;
		}
		return currentFitnessValue < element.getFitnessValue();
	}

}
