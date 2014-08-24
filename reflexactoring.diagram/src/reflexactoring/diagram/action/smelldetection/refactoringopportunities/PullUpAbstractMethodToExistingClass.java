/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public class PullUpAbstractMethodToExistingClass extends
		PullUpMemberOpportunity {

	/**
	 * @param toBePulledMemberList
	 * @param moduleList
	 */
	public PullUpAbstractMethodToExistingClass(
			ArrayList<UnitMemberWrapper> toBePulledMemberList, ICompilationUnitWrapper targetUnit,
			ArrayList<ModuleWrapper> moduleList) {
		super(toBePulledMemberList, moduleList);
		this.targetUnit = targetUnit;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity#simulate(reflexactoring.diagram.bean.programmodel.ProgramModel)
	 */
	@Override
	public ProgramModel simulate(ProgramModel model) {
		// TODO Auto-generated method stub
		return model;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity#getRefactoringName()
	 */
	@Override
	public String getRefactoringName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity#getRefactoringDetails()
	 */
	@Override
	public ArrayList<String> getRefactoringDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity#apply(int, reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence)
	 */
	@Override
	public boolean apply(int position, RefactoringSequence sequence) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity#checkLegal(reflexactoring.diagram.bean.programmodel.ProgramModel)
	 */
	@Override
	protected boolean checkLegal(ProgramModel model) {
		// TODO Auto-generated method stub
		return false;
	}

}
