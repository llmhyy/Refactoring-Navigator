/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.bean;

import java.util.ArrayList;

/**
 * @author linyun
 *
 */
public class RefactoringSequence extends ArrayList<RefactoringSequenceElement>{

	private Double currentFitnessValue;
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
