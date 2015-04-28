/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.popup.RenameMembersDialog;
import reflexactoring.diagram.action.recommend.gencode.JavaClassCreator;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 * 
 */
public class PullUpMethodToNewInterfaceOpportunity extends PullUpMemberOpportunity {

	/**
	 * @param toBePulledMemberList
	 */
	public PullUpMethodToNewInterfaceOpportunity(
			ArrayList<UnitMemberWrapper> toBePulledMemberList,
			ArrayList<ModuleWrapper> moduleList) {
		super(toBePulledMemberList, moduleList);
	}
	
	@Override
	public double computeSimilarityWith(RefactoringOpportunity opp){
		if(opp instanceof PullUpMethodToNewInterfaceOpportunity){
			PullUpMethodToNewInterfaceOpportunity thatOpp = (PullUpMethodToNewInterfaceOpportunity)opp;
			
			double memberSim = ReflexactoringUtil.computeSetSimilarity(toBePulledMemberList, thatOpp.getToBePulledMemberList());
			double unitSim = ReflexactoringUtil.computeSetSimilarity(getUnitsOfToBePulledMembers(), thatOpp.getUnitsOfToBePulledMembers());
			
			return (memberSim + unitSim)/2;
		}
		
		return 0;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append(" to created newly interface");
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof PullUpMethodToNewInterfaceOpportunity){
			PullUpMethodToNewInterfaceOpportunity thatOpp = (PullUpMethodToNewInterfaceOpportunity)obj;
			if(thatOpp.isHavingSameMemberList(toBePulledMemberList)){
				return true;
			}
		}
		
		return false;
	} 

	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		/**
		 * create a new interface
		 */
		ICompilationUnitWrapper newInterfaceUnit = createNewUnit(newModel, true);

		/**
		 * create a new method in the newly created interface and change
		 * reference
		 */
		createNewMemberInSuperUnit(newModel, newInterfaceUnit, true);

		newModel.updateUnitCallingRelationByMemberRelations();
		newModel.updateUnitDescription();

		/**
		 * may calculate which module is proper to hold the newly created
		 * interface
		 */
		ModuleWrapper bestMappingModule = calculateBestMappingModule(newModel,
				newInterfaceUnit);
		newInterfaceUnit.setMappingModule(bestMappingModule);
		
		this.targetUnit = newInterfaceUnit;

		return newModel;
	}
	
	/**
	 * It is empty because only the signature of those members are pulled.
	 */
	@Override
	protected void handleRefereesOfToBePulledMember(UnitMemberWrapper newToBePulledMember, UnitMemberWrapper newMember){
		
	}

	@Override
	public boolean apply(int position, RefactoringSequence sequence) {
		//create new interface
		JavaClassCreator javaCreator = new JavaClassCreator();
		ICompilationUnitWrapper parentInterface = javaCreator.createInterface();	
		if(parentInterface == null){
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
		
		//Create an abstract method in parentInterface and set corresponding imports
		if(!createAbstractMethodInParent(parentInterface, memberList, newMemberName)){
			return false;
		}
		
		//make every child class implements the interface	
		if(!addSubClassImplements(parentInterface, memberList)){
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
		
		//call Eclipse API to pull up
//		try {
//			System.out.println(RefactoringAvailabilityTester.isPullUpAvailable(membersToPull));
//			System.out.println(ActionUtil.isEditable(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), membersToPull[0]));
//			if (RefactoringAvailabilityTester.isPullUpAvailable(membersToPull) && ActionUtil.isEditable(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), membersToPull[0]))
//				RefactoringExecutionStarter.startPullUpRefactoring(membersToPull, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
//		} catch (JavaModelException jme) {
//			ExceptionHandler.handle(jme, RefactoringMessages.OpenRefactoringWizardAction_refactoring, RefactoringMessages.OpenRefactoringWizardAction_exception);
//		}	
				
		return true;
	}	

	@Override
	public boolean checkLegal() {
		return super.checkLegal();
	}
	
	@Override
	public String getRefactoringName() {
		return "Pull Up Method to New Interface";
	}
	
	public ICompilationUnitWrapper getTargetInterface(){
		return this.targetUnit;
	}
	
	@Override
	public ArrayList<String> getRefactoringDetails(){
		ArrayList<String> refactoringDetails = new ArrayList<>();
		String step1 = "Create an interface for ";
		StringBuffer buffer1 = new StringBuffer();
		for(UnitMemberWrapper member: toBePulledMemberList){
			buffer1.append(member.getUnitWrapper().getSimpleName() + ",");
		}
		String str = buffer1.toString();
		str = str.substring(0, str.length()-1);
		step1 += str;
		
		refactoringDetails.add(step1);
		
		String step2 = "create the member " + toBePulledMemberList.get(0).getName() + " in interface";
		refactoringDetails.add(step2);
		
		String step3 = "Those methods refer to ";
		StringBuffer buffer2 = new StringBuffer();
		for(UnitMemberWrapper member: toBePulledMemberList){
			buffer2.append(member.toString()+ ",");
		}
		String memberString = buffer2.toString();
		memberString = memberString.substring(0, memberString.length()-1);
		step3 += memberString;
		step3 += " now refer to the " + toBePulledMemberList.get(0).getName() + " in super class"; 
		refactoringDetails.add(step3);
		
		String step4 = "Move the interface " + this.targetUnit.getName() + " to module " + this.targetUnit.getMappingModule().getName();
		refactoringDetails.add(step4);
		
		return refactoringDetails;
	};
		
}
