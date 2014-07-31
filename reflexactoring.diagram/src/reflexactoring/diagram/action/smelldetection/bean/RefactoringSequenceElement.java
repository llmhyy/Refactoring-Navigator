/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.bean;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.bean.ProgramModel;

/**
 * @author linyun
 *
 */
public class RefactoringSequenceElement {
	private RefactoringOpportunity opportunity;
	private ProgramModel consequenceModel;
	private double fitnessValue;
	/**
	 * @param opportunity
	 * @param consequenceModel
	 */
	public RefactoringSequenceElement(RefactoringOpportunity opportunity,
			ProgramModel consequenceModel, double fitnessValue) {
		super();
		this.opportunity = opportunity;
		this.consequenceModel = consequenceModel;
		this.fitnessValue = fitnessValue;
	}

	/**
	 * @return the fitnessValue
	 */
	public double getFitnessValue() {
		return fitnessValue;
	}

	/**
	 * @param fitnessValue the fitnessValue to set
	 */
	public void setFitnessValue(double fitnessValue) {
		this.fitnessValue = fitnessValue;
	}

	/**
	 * @return the opportunity
	 */
	public RefactoringOpportunity getOpportunity() {
		return opportunity;
	}
	/**
	 * @param opportunity the opportunity to set
	 */
	public void setOpportunity(RefactoringOpportunity opportunity) {
		this.opportunity = opportunity;
	}
	/**
	 * @return the consequenceModel
	 */
	public ProgramModel getConsequenceModel() {
		return consequenceModel;
	}
	/**
	 * @param consequenceModel the consequenceModel to set
	 */
	public void setConsequenceModel(ProgramModel consequenceModel) {
		this.consequenceModel = consequenceModel;
	}
	
	
}
