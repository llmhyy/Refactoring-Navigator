/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.popup.RenameMembersDialog;
import reflexactoring.diagram.action.recommend.gencode.JavaClassCreator;
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
		//create new class
		JavaClassCreator javaCreator = new JavaClassCreator();
		ICompilationUnitWrapper parentClass = javaCreator.createClass();	
		if(parentClass == null){
			return false;
		}

		//get all members to be pulled
		ArrayList<UnitMemberWrapper> memberList = this.getToBePulledMemberList();
		String[] memberNames = new String[memberList.size()];
		for(UnitMemberWrapper memberWrapper : memberList){
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
		
		//Create an abstract method in parentClass and set corresponding imports
		if(!createAbstractMethodInParent(parentClass, memberList, newMemberName)){
			return false;
		}
		
		//make every child class extends the class	
		if(!addSubClassExtends(parentClass, memberList)){
			return false;
		}
									
		//rename each member
		if(!renameMembers(memberList, newMemberName)){
			return false;
		}

		//cast corresponding variable into parent interface, summarize a map out first		
		HashMap<ICompilationUnit, ArrayList<ASTNodeInfo>> modificationMap = summarizeCastMap(parentClass, memberList);
		
		//do modifications: add or remove casting
		for(ICompilationUnit icu : modificationMap.keySet()){
			if(!this.modifyCastExpression(modificationMap.get(icu))){
				return false;
			}
		}
		
		//refresh the model
		refreshModel(position, sequence, parentClass, memberList, newMemberName);
				
		return true;
	}

	@Override
	public boolean checkLegal() {
		return super.checkLegal();
	}


}
