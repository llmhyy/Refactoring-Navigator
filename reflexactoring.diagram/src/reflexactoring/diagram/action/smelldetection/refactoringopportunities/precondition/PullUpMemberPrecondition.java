/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.CompilationUnit;

import reflexactoring.diagram.action.smelldetection.bean.CloneInstance;
import reflexactoring.diagram.action.smelldetection.bean.CloneSet;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.CreateSuperclassAndPullUpMemberOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMemberToInterfaceOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMemberToSuperclassOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.bean.FieldWrapper;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.ProgramReference;
import reflexactoring.diagram.bean.ReferencingDetail;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class PullUpMemberPrecondition extends RefactoringPrecondition{
	/**
	 * 
	 */
	public PullUpMemberPrecondition(ArrayList<ModuleWrapper> moduleList) {
		setModuleList(moduleList);
	}
	
	@Override
	public ArrayList<RefactoringOpportunity> detectOpportunities(ProgramModel model) {
		ArrayList<ArrayList<UnitMemberWrapper>> refactoringPlaceList = detectCounterMembers(model);
		
		ArrayList<RefactoringOpportunity> opportunities = 
				detectPullingUpOpportunities(model, refactoringPlaceList, getModuleList());
		
		return opportunities;
	}
	
	/**
	 * This method is used to identify the "counter member" across the refactoring scope, that is, 
	 * 1) those members are with the same signature across different classes
	 * 2) those members do not override some member in super class or interface
	 * 
	 * Note that, current version only identify counter members in class instead of interface.
	 * 
	 * @param model
	 * @return
	 */
	protected ArrayList<ArrayList<UnitMemberWrapper>> detectCounterMembers(ProgramModel model) {
		ArrayList<ArrayList<UnitMemberWrapper>> refactoringPlaceList = new ArrayList<>();
		ArrayList<UnitMemberWrapper> markedMemberList = new ArrayList<>();
		for(ICompilationUnitWrapper unit: model.getScopeCompilationUnitList()){
			if(unit.isInterface())continue;
			
			ArrayList<ICompilationUnitWrapper> otherUnits = model.findOtherUnits(unit);
			for(UnitMemberWrapper member: unit.getMembers()){
				if(markedMemberList.contains(member))continue;
				
				ArrayList<UnitMemberWrapper> counterMemberList = new ArrayList<>();
				if(!member.isOverrideSuperMember()){
					/**
					 * constructor is not considered counter here.
					 */
					if(member instanceof MethodWrapper){
						if(((MethodWrapper)member).isConstructor()){
							continue;
						}
					}
					
					counterMemberList.add(member);					
					for(ICompilationUnitWrapper otherUnit: otherUnits){
						if(otherUnit.isInterface())continue;
						
						UnitMemberWrapper bestMatchingMember = findBestMatchingMember(member, 
								otherUnit, markedMemberList, counterMemberList);
						if(null != bestMatchingMember){
							counterMemberList.add(bestMatchingMember);
						}
					}
				}
				
				if(counterMemberList.size() >= 2){
					refactoringPlaceList.add(counterMemberList);
					markedMemberList.addAll(counterMemberList);
				}
			}
		}
		
		return refactoringPlaceList;
	}
	
	/**
	 * Find the most similar methods in other unit.
	 * @param member
	 * @param otherUnit
	 * @param markedMemberList
	 * @return
	 */
	private UnitMemberWrapper findBestMatchingMember(UnitMemberWrapper member, ICompilationUnitWrapper otherUnit,
			ArrayList<UnitMemberWrapper> markedMemberList, ArrayList<UnitMemberWrapper> counterMemberList) {
		UnitMemberWrapper matchingMember = null;
		double matchingValue = 0d;
		
		for(UnitMemberWrapper otherMember: otherUnit.getMembers()){
			if(markedMemberList.contains(otherMember))continue;
			
			if(!otherMember.isOverrideSuperMember() && 
					!isWithCounterCallingRelation(counterMemberList, otherMember)){
				if(member.hasSameSignatureWith(otherMember)){
					return otherMember;
				}
				else{
					/*if(member.getName().contains("read") && otherMember.getName().contains("read")){
						System.currentTimeMillis();
					}*/
					
					double sim = member.computeSimilarityForBeingPulledUp(otherMember);
					if(sim > matchingValue && sim >= Settings.counterMethodSimilarity){
						matchingValue = sim;
						matchingMember = otherMember;
					}
				}
				
			}
		}
		
		return matchingMember;
	}

	/**
	 * the members inside a counter member list should not call with each other, including a method call the
	 * other one's super member.
	 * @param counterMemberList
	 * @param member
	 * @return
	 */
	private boolean isWithCounterCallingRelation(ArrayList<UnitMemberWrapper> counterMemberList, UnitMemberWrapper member){
		for(UnitMemberWrapper mem: counterMemberList){
			if(mem.getCalleeList(ReferencingDetail.ALL).keySet().contains(member)
					|| member.getCalleeList(ReferencingDetail.ALL).keySet().contains(mem)
					|| mem.findOverridedSuperMember().contains(member)
					|| member.findOverridedSuperMember().contains(mem)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * If those methods occupy the whole (sufficient) method body, it is a (create-and-)pull-up-to-super-class opportunity,
	 * otherwise, i.e., if those methods do not share code clones, it is a pull-up-to-new-interface opportunity.<p>
	 * 
	 * Noteworthy, if the declaring classes of those counter members share the same common ancestor, I create a pull-up-to-superclass
	 * opportunity, if the declaring classes of them share no super class, I create a create-and-pull-up-to-superclass
	 * opportunity. In other cases, e.g., some of the declaring classes share super class while others not, I will not consider
	 * such cases as pull-up-to-superclass opportunity.<p>
	 * 
	 * Moreover, for each (create-and-)pull-up-to-superclass opportunity of counter methods, there will always be a pull-up-to-interface 
	 * opportunity, of course, they conflicts with each other, which means the search algorithm should know to remove some 
	 * opportunities after applying some others.
	 */
	protected ArrayList<RefactoringOpportunity> detectPullingUpOpportunities(ProgramModel model, ArrayList<ArrayList<UnitMemberWrapper>> refactoringPlaceList,
			ArrayList<ModuleWrapper> moduleList) {
		ArrayList<RefactoringOpportunity> opportunities = new ArrayList<>();
		
		for(ArrayList<UnitMemberWrapper> refactoringPlace: refactoringPlaceList){
			ICompilationUnitWrapper commonAncestor = findCommonAncestor(refactoringPlace);
			boolean isWithoutAnySuperclass = isWithoutAnySuperclass(refactoringPlace);
			boolean isRelyOnOtherMemberInDeclaringClass = isRelyOnOtherMemberInDeclaringClass(refactoringPlace);
			boolean isWithSimilarBody = isWithSimilarBody(model, refactoringPlace);
			UnitMemberWrapper member = refactoringPlace.get(0);
			
			if((isWithSimilarBody || (member instanceof FieldWrapper)) && !isRelyOnOtherMemberInDeclaringClass &&
					((commonAncestor != null) || (isWithoutAnySuperclass))){
				if(commonAncestor != null){
					PullUpMemberToSuperclassOpportunity opp = 
							new PullUpMemberToSuperclassOpportunity(refactoringPlace, moduleList, commonAncestor);
					opportunities.add(opp);
				}
				else{
					CreateSuperclassAndPullUpMemberOpportunity opp = 
							new CreateSuperclassAndPullUpMemberOpportunity(refactoringPlace, moduleList);
					opportunities.add(opp);
				}
			}
			
			if(member instanceof MethodWrapper){
				PullUpMemberToInterfaceOpportunity opportunity = 
						new PullUpMemberToInterfaceOpportunity(refactoringPlace, moduleList);
				opportunities.add(opportunity);				
			}
		}
		
		return opportunities;
	}
	
	/**
	 * @param refactoringPlace
	 * @return
	 */
	protected ICompilationUnitWrapper findCommonAncestor(ArrayList<UnitMemberWrapper> refactoringPlace) {
		int smallestSize = -1;
		
		ArrayList<ArrayList<ICompilationUnitWrapper>> list = new ArrayList<>();
		for(UnitMemberWrapper member: refactoringPlace){
			/**
			 * achieve all the parents of the first element in order
			 */
			ArrayList<ICompilationUnitWrapper> ancestorList = new ArrayList<>();
			ICompilationUnitWrapper parent = member.getUnitWrapper().getSuperClass();
			if(parent == null){
				return null;
			}
			else{
				ancestorList.add(parent);
				while(parent.getSuperClass() != null){
					parent = parent.getSuperClass();
					ancestorList.add(parent);
				}
			}
			
			ArrayList<ICompilationUnitWrapper> reverseList = new ArrayList<>();
			for(int i=0; i<ancestorList.size(); i++){
				reverseList.add(ancestorList.get(ancestorList.size()-1-i));
			}
			list.add(reverseList);
			
			if(smallestSize == -1){
				smallestSize = reverseList.size();
			}
			else{
				smallestSize = (smallestSize < reverseList.size())?smallestSize:reverseList.size();
			}
		}
		
		ICompilationUnitWrapper commonAncestor = null;
		for(int i=0; i<smallestSize; i++){
			ICompilationUnitWrapper unit = list.get(0).get(i);
			for(int j=1; j<list.size(); j++){
				ICompilationUnitWrapper otherUnit = list.get(j).get(i);
				if(!unit.equals(otherUnit)){
					return commonAncestor;
				}
			}
			commonAncestor = unit;
		}
		
		return commonAncestor;
	}

	/**
	 * @param refactoringPlace
	 * @return
	 */
	protected boolean isWithoutAnySuperclass(
			ArrayList<UnitMemberWrapper> refactoringPlace) {
		for(UnitMemberWrapper member: refactoringPlace){
			ICompilationUnitWrapper unitWrapper = member.getUnitWrapper();
			if(unitWrapper.getSuperClass() != null){
				return false;
			}
		}
		return true;
	}

	/**
	 * @param refactoringPlace
	 * @return
	 */
	protected boolean isWithSimilarBody(ProgramModel model, ArrayList<UnitMemberWrapper> refactoringPlace) {
		for(CloneSet set: model.getCloneSets()){
			boolean isAllCloneInstanceSufficientlyOccupyMemberBody = true;
			for(CloneInstance instance: set.getInstances()){
				if(!isCloneInstanceSufficientlyOccupyOneOfTheMember(instance, refactoringPlace)){
					isAllCloneInstanceSufficientlyOccupyMemberBody = false;
					break;
				}
			}
			
			if(isAllCloneInstanceSufficientlyOccupyMemberBody){
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean isCloneInstanceSufficientlyOccupyOneOfTheMember(CloneInstance instance, 
			ArrayList<UnitMemberWrapper> refactoringPlace){
		for(UnitMemberWrapper member: refactoringPlace){
			if(instance.getMember().equals(member)){
				CompilationUnit cu = member.getUnitWrapper().getJavaUnit();
				if(cu != null){
					int startPosition = member.getJavaElement().getStartPosition();
					int endPosition = startPosition + member.getJavaElement().getLength();

					int cloneCoverLength = instance.getLength();
					int memberLength = cu.getLineNumber(endPosition) - cu.getLineNumber(startPosition);
					
					if(cloneCoverLength > 0.8*memberLength){
						return true;
					}
				}
				else{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * If a member to be pulled up relies on other member in its declaring class, this member cannot be pulled
	 * up. Otherwise, it will cause compilation errors. 
	 * 
	 * @param refactoringPlace
	 * @return
	 */
	protected boolean isRelyOnOtherMemberInDeclaringClass(
			ArrayList<UnitMemberWrapper> refactoringPlace) {
		for(UnitMemberWrapper member: refactoringPlace){
			for(ProgramReference reference: member.getRefereePointList()){
				LowLevelGraphNode refereeNode = reference.getReferee();
				if(refereeNode instanceof UnitMemberWrapper){
					UnitMemberWrapper calleeMember = (UnitMemberWrapper)refereeNode;
					if(calleeMember.getUnitWrapper().equals(member.getUnitWrapper())){
						return true;
					}
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean checkLegal(ProgramModel model) {
		return true;
	}
}
