/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.bean;

import java.util.ArrayList;

import reflexactoring.diagram.action.recommend.suboptimal.Violation;
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
	private ArrayList<Violation> violationList;
	/**
	 * used to record the position of this element in the whole refactoring sequence.
	 */
	private int position;

	/**
	 * @param opportunity
	 * @param consequenceModel
	 */
	public RefactoringSequenceElement(RefactoringOpportunity opportunity,
			ProgramModel consequenceModel, double fitnessValue, ArrayList<Violation> violationList) {
		super();
		this.opportunity = opportunity;
		this.consequenceModel = consequenceModel;
		this.fitnessValue = fitnessValue;
		this.violationList = violationList;
		//this.position = position;
	}
	
	@Override
	public String toString(){
		return opportunity.toString();
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
	
	/**
	 * @return the violationList
	 */
	public ArrayList<Violation> getViolationList() {
		return violationList;
	}

	/**
	 * @param violationList the violationList to set
	 */
	public void setViolationList(ArrayList<Violation> violationList) {
		this.violationList = violationList;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}
}
