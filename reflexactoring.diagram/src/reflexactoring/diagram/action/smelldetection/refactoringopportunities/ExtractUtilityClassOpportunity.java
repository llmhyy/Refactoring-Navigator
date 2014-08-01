/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.NameGernationCounter;
import reflexactoring.diagram.action.smelldetection.bean.CloneInstance;
import reflexactoring.diagram.action.smelldetection.bean.CloneSet;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.RefactoringPrecondition;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.ProgramReference;
import reflexactoring.diagram.bean.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public class ExtractUtilityClassOpportunity extends RefactoringOpportunity{

	private CloneSet cloneSet;
	
	public ExtractUtilityClassOpportunity(CloneSet cloneSet, ArrayList<ModuleWrapper> moduleList){
		this.cloneSet = cloneSet;
		this.moduleList = moduleList;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("extract the cloned code ");
		for(CloneInstance instance: cloneSet.getInstances()){
			buffer.append(instance.toString() + ",");
		}
		buffer.append("to a utility class");
		return buffer.toString();
	}
	
	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		/**
		 * create a new utility class
		 */
		String className = "UtilityClass" + NameGernationCounter.retrieveNumber();
		String packageName = cloneSet.getInstances().get(0).getMember().getUnitWrapper().getPackageName();
		ICompilationUnitWrapper utilityClass = new ICompilationUnitWrapper(null, false, className, packageName);
		newModel.getScopeCompilationUnitList().add(utilityClass);
		/**
		 * create a new utility method
		 */
		String methodName = "utilityMethod" + NameGernationCounter.retrieveNumber();
		MethodWrapper utilityMethod = new MethodWrapper(methodName, "void", new ArrayList<String>(), false, utilityClass);
		newModel.getScopeMemberList().add(utilityMethod);
		/**
		 * build declaring relation between utility class and utility method
		 */
		utilityClass.addMember(utilityMethod);
		
		/**
		 * change references
		 */
		for(CloneInstance instance: this.cloneSet.getInstances()){
			UnitMemberWrapper member = newModel.findMember(instance.getMember());
			CloneInstance newInstance = newModel.findCloneInstance(instance);
			for(ProgramReference reference: newInstance.getCoveringReferenceList()){
				/**
				 * new member's referee
				 */
				if(reference.getReferer().equals(member)){
					member.getRefereePointList().remove(reference);
					reference.setReferer(utilityMethod);
					utilityMethod.addProgramReferee(reference);
				}
			}
			
			ProgramReference newReference = new ProgramReference(member, utilityMethod, member.getJavaElement());
			newModel.getReferenceList().add(newReference);
			member.addProgramReferee(newReference);
			utilityMethod.addProgramReferer(newReference);
		}
		
		/**
		 * remove the corresponding clones
		 */
		CloneSet cloneSet = newModel.findCloneSet(this.cloneSet.getId());
		newModel.getCloneSets().remove(cloneSet);
		
		/**
		 * update new model
		 */
		newModel.updateUnitCallingRelationByMemberRelations();
		
		/**
		 * may calculate which module is proper to hold the newly created super class
		 */
		ModuleWrapper bestMappingModule = calculateBestMappingModule(newModel, utilityClass);
		utilityClass.setMappingModule(bestMappingModule);
		
		return newModel;
	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected boolean checkLegal(ProgramModel model) {
		Precondition precondition = new Precondition(getModuleList());
		return precondition.checkLegal(model);
	}

	public class Precondition extends RefactoringPrecondition{
		/**
		 * @param moduleList
		 */
		public Precondition(ArrayList<ModuleWrapper> moduleList) {
			setModuleList(moduleList);
		}

		@Override
		public ArrayList<RefactoringOpportunity> detectOpportunities(ProgramModel model) {
			ArrayList<RefactoringOpportunity> opportunities = new ArrayList<>();
			
			for(CloneSet set: model.getCloneSets()){
				if(!set.isAllInstancesLocatedInSameUnit()){
					ExtractUtilityClassOpportunity opp = new ExtractUtilityClassOpportunity(set, getModuleList());
					opportunities.add(opp);					
				}
			}
			
			return opportunities;
		}
		
		public boolean checkLegal(ProgramModel model){
			return model.findCloneSet(cloneSet.getId()) != null;
		}
	}
}
