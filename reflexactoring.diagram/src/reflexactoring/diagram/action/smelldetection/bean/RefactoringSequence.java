/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.bean;

import java.util.ArrayList;

import reflexactoring.diagram.action.recommend.SuggestionMove;
import reflexactoring.diagram.action.smelldetection.AdvanceEvaluatorAdapter;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;

/**
 * @author linyun
 *
 */
public class RefactoringSequence extends ArrayList<RefactoringSequenceElement>{

	private Double currentFitnessValue;
	private ProgramModel originModel;

	private ArrayList<SuggestionMove> prerequisite;
	
	
	public RefactoringSequence(ProgramModel originModel, ArrayList<ModuleWrapper> moduleList){
		AdvanceEvaluatorAdapter evaluator = new AdvanceEvaluatorAdapter();
		this.setOriginModel(originModel);
		this.currentFitnessValue = evaluator.computeFitness(originModel, moduleList);
	}
	
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

	/**
	 * @return the originModel
	 */
	public ProgramModel getOriginModel() {
		return originModel;
	}

	/**
	 * @param originModel the originModel to set
	 */
	public void setOriginModel(ProgramModel originModel) {
		this.originModel = originModel;
	}

}
