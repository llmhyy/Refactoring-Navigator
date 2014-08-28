/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;

import reflexactoring.diagram.action.smelldetection.NameGernationCounter;
import reflexactoring.diagram.action.smelldetection.bean.CloneInstance;
import reflexactoring.diagram.action.smelldetection.bean.CloneSet;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.RefactoringPrecondition;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.MethodWrapper;
import reflexactoring.diagram.bean.programmodel.ModifierWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.ProgramReference;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class ExtractUtilityClassOpportunity extends RefactoringOpportunity{

	private CloneSet cloneSet;
	private ICompilationUnitWrapper utilityClass;
	private MethodWrapper utilityMethod;
	
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
	public boolean equals(Object obj){
		if(obj instanceof ExtractUtilityClassOpportunity){
			ExtractUtilityClassOpportunity thatOpp = (ExtractUtilityClassOpportunity)obj;
			if(thatOpp.getCloneSet().equals(getCloneSet())){
				return true;
			}
			return false;
		}
		
		return false;
	} 
	
	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		this.cloneSet = newModel.findCloneSet(this.cloneSet.getId());
		/**
		 * create a new utility class
		 */
		String className = "UtilityClass" + NameGernationCounter.retrieveNumber();
		String packageName = cloneSet.getInstances().get(0).getMember().getUnitWrapper().getPackageName();
		ICompilationUnitWrapper utilityClass = new ICompilationUnitWrapper(null, false, className, packageName, new HashMap<String, Integer>(), 
				"Utility", false, ModifierWrapper.PUBLIC);
		newModel.getScopeCompilationUnitList().add(utilityClass);
		/**
		 * create a new utility method
		 */
		String methodName = "utilityMethod" + NameGernationCounter.retrieveNumber();
		MethodWrapper utilityMethod = new MethodWrapper(methodName, "void", new ArrayList<String>(), false, 
				utilityClass, null, "utility", null, null, false, ModifierWrapper.PUBLIC);
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
			
			ProgramReference newReference = new ProgramReference(member, utilityMethod, 
					member.getJavaElement(), ProgramReference.METHOD_INVOCATION, null);
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
		
		this.utilityMethod = utilityMethod;
		this.utilityClass = utilityClass;
		
		return newModel;
	}
	
	@Override
	public double computeSimilarityWith(RefactoringOpportunity opp){
		if(opp instanceof ExtractUtilityClassOpportunity){
			ExtractUtilityClassOpportunity thatOpp = (ExtractUtilityClassOpportunity)opp;
			
			double memberSim = ReflexactoringUtil.computeSetSimilarity(getExtractedMembers(), thatOpp.getExtractedMembers());
			double unitSim = ReflexactoringUtil.computeSetSimilarity(getExtractedUnits(), thatOpp.getExtractedUnits());
			
			return (memberSim + unitSim)/2;
			
		}
		
		return 0;
	}
	
	public ArrayList<UnitMemberWrapper> getExtractedMembers(){
		ArrayList<UnitMemberWrapper> extractedMembers = new ArrayList<>();
		for(CloneInstance instance: getCloneSet().getInstances()){
			extractedMembers.add(instance.getMember());
		}
		
		return extractedMembers;
	}
	
	public ArrayList<ICompilationUnitWrapper> getExtractedUnits(){
		ArrayList<ICompilationUnitWrapper> extractedUnits = new ArrayList<>();
		for(CloneInstance instance: getCloneSet().getInstances()){
			ICompilationUnitWrapper unit = instance.getMember().getUnitWrapper();
			if(!extractedUnits.contains(unit)){
				extractedUnits.add(unit);				
			}
		}
		
		return extractedUnits;
	}
	
	@Override
	public ArrayList<ASTNode> getHints() {
		ArrayList<ASTNode> hints = new ArrayList<>();
		for(ProgramReference reference: utilityMethod.getRefererPointList()){
			ASTNode node = reference.getASTNode();
			hints.add(node);
		}
		return hints;
	}

	@Override
	public boolean apply(int position, RefactoringSequence sequence) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean checkLegal() {
		//TODO
		return false;
	}

	@Override
	public String getRefactoringName() {
		return "Extract Clones to A Utility Class";
	}
	
	@Override
	public ArrayList<String> getRefactoringDetails(){
		ArrayList<String> refactoringDetails = new ArrayList<>();
		
		String step1 = "Extract the cloned code in to newly created " + utilityClass.getName();
		refactoringDetails.add(step1);
		
		String step2 = "Move " + utilityClass.getName() + " to the module " + utilityClass.getMappingModule().getName();
		refactoringDetails.add(step2);
		
		return refactoringDetails;
	};

	/**
	 * @return the utilityMethod
	 */
	public MethodWrapper getUtilityMethod() {
		return utilityMethod;
	}

	/**
	 * @param utilityMethod the utilityMethod to set
	 */
	public void setUtilityMethod(MethodWrapper utilityMethod) {
		this.utilityMethod = utilityMethod;
	}

	/**
	 * @return the utilityClass
	 */
	public ICompilationUnitWrapper getUtilityClass() {
		return utilityClass;
	}

	/**
	 * @param utilityClass the utilityClass to set
	 */
	public void setUtilityClass(ICompilationUnitWrapper utilityClass) {
		this.utilityClass = utilityClass;
	}

	/**
	 * @return the cloneSet
	 */
	public CloneSet getCloneSet() {
		return cloneSet;
	}

	/**
	 * @param cloneSet the cloneSet to set
	 */
	public void setCloneSet(CloneSet cloneSet) {
		this.cloneSet = cloneSet;
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
