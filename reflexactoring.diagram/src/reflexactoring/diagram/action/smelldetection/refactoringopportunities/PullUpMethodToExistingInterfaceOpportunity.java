/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.popup.RenameMembersDialog;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMemberOpportunity.ASTNodeInfo;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public class PullUpMethodToExistingInterfaceOpportunity extends PullUpMemberOpportunity{

	/**
	 * @param toBePulledMemberList
	 * @param moduleList
	 */
	public PullUpMethodToExistingInterfaceOpportunity(
			ArrayList<UnitMemberWrapper> toBePulledMemberList, ICompilationUnitWrapper targetUnit,
			ArrayList<ModuleWrapper> moduleList) {
		super(toBePulledMemberList, moduleList);
		this.targetUnit = targetUnit;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append(" to interface ");
		buffer.append(this.targetUnit.getName());
		return buffer.toString();
	}

	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		
		/**
		 * create a new method in the interface and change reference
		 */
		ICompilationUnitWrapper newInterface = newModel.findUnit(this.targetUnit.getFullQualifiedName());
		createNewMemberInSuperUnit(newModel, newInterface, true);
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		return newModel;
	}

	@Override
	public String getRefactoringName() {
		return "Pull Up Method to Existing Interface";
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
		ICompilationUnitWrapper parentInterface = this.targetUnit;

		//get all members to be pulled
		ArrayList<UnitMemberWrapper> memberList = this.getToBePulledMemberList();
		IMember[] members = new IMember[memberList.size()];
		String[] memberNames = new String[memberList.size()];
		for(UnitMemberWrapper memberWrapper : memberList){
			members[memberList.indexOf(memberWrapper)] = memberWrapper.getJavaMember();	
			memberNames[memberList.indexOf(memberWrapper)] = memberWrapper.getUnitWrapper().getName() + "." + memberWrapper.getName();
		}
		
		//show a wizard to rename all the funcions into one name
		String newMemberName = "";
		RenameMembersDialog dialog = new RenameMembersDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, memberNames);
		dialog.create();
		if(dialog.open() == Window.OK){
			newMemberName = dialog.getNewMemberName();								
		}else{
			return false;
		}
		
		//Create an abstract method in parentInterface and set corresponding imports
		if(!createAbstractMethodInInterface(parentInterface, memberList, newMemberName)){
			return false;
		}
		
		//rename each member
		if(!renameMembers(memberList, newMemberName)){
			return false;
		}

		//cast corresponding variable into parent interface, summarize a map out first		
		HashMap<ICompilationUnit, ArrayList<ASTNodeInfo>> modificationMap = summarizeCastMap(parentInterface, memberList);
		
		//do modifications: add or remove casting
		for(ICompilationUnit icu : modificationMap.keySet()){
			if(!this.modifyCastExpression(modificationMap.get(icu))){
				return false;
			}
		}

		//refresh the model
		refreshModel(position, sequence, parentInterface, memberList, newMemberName);
		
		return true;
	}

	@Override
	protected boolean checkLegal(ProgramModel model) {
		// TODO Auto-generated method stub
		return false;
	}

}
