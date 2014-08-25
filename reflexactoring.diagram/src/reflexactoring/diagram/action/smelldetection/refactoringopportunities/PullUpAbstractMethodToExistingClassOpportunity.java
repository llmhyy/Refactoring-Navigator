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
public class PullUpAbstractMethodToExistingClassOpportunity extends
		PullUpMemberOpportunity {

	/**
	 * @param toBePulledMemberList
	 * @param moduleList
	 */
	public PullUpAbstractMethodToExistingClassOpportunity(
			ArrayList<UnitMemberWrapper> toBePulledMemberList, ICompilationUnitWrapper targetUnit,
			ArrayList<ModuleWrapper> moduleList) {
		super(toBePulledMemberList, moduleList);
		this.targetUnit = targetUnit;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append(" (signature) to class ");
		buffer.append(this.targetUnit.getName());
		return buffer.toString();
	}

	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		/**
		 * create a new method in the parent class and change reference
		 */
		ICompilationUnitWrapper newClass = newModel.findUnit(this.targetUnit.getFullQualifiedName());
		createNewMemberInSuperUnit(newModel, newClass, true);
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		return newModel;
	}

	@Override
	public String getRefactoringName() {
		return "Pull Up Abstract Method to Existing Class";
	}

	@Override
	public ArrayList<String> getRefactoringDetails() {
		// TODO Auto-generated method stub
		ArrayList<String> details = new ArrayList<>();
		details.add(toString());
		return details;
	}

	@Override
	public boolean apply(int position, RefactoringSequence sequence) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkLegal(ProgramModel model) {
		// TODO Auto-generated method stub
		return false;
	}

}
