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
public class PullUpAbstractMethodToNewClassOpportunity extends PullUpMemberOpportunity{

	/**
	 * @param toBePulledMemberList
	 * @param moduleList
	 */
	public PullUpAbstractMethodToNewClassOpportunity(
			ArrayList<UnitMemberWrapper> toBePulledMemberList,
			ArrayList<ModuleWrapper> moduleList) {
		super(toBePulledMemberList, moduleList);
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append(" (signature) to new class ");
		return buffer.toString();
	}

	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		/**
		 * create a new method in the parent class and change reference
		 */
		ICompilationUnitWrapper newClass = createNewUnit(newModel, false);
		createNewMemberInSuperUnit(newModel, newClass, true);
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		return newModel;
	}

	@Override
	public String getRefactoringName() {
		return "Pull Up Abstract Method to A New Class";
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
