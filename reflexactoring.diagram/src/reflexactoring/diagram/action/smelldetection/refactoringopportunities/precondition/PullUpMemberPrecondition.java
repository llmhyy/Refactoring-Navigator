/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jdt.core.dom.CompilationUnit;

import reflexactoring.diagram.action.smelldetection.bean.CloneInstance;
import reflexactoring.diagram.action.smelldetection.bean.CloneSet;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpAbstractMethodToExistingClassOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpAbstractMethodToNewClassOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMethodToExistingInterfaceOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpConcreteMemberToNewClassOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMethodToNewInterfaceOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpConcreteMemberToExistingClassOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.DeclarationInfluencingDetail;
import reflexactoring.diagram.bean.programmodel.FieldWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.MethodWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.ProgramReference;
import reflexactoring.diagram.bean.programmodel.ReferenceInflucencedDetail;
import reflexactoring.diagram.bean.programmodel.ReferencingDetail;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.bean.programmodel.VariableDeclarationWrapper;
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
		System.currentTimeMillis();
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
				
				if(member.toString().contains("getMessag")){
					System.currentTimeMillis();
				}
				
				if(!member.isOverrideSuperMember() && !member.isItsInvocationInfluencedByParameter() /*&& isNotCausingCompilationError(member)*/){
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
	 * One important post condition of pulling up member is to replace the sub-type declaration
	 * with the super-type declaration. Therefore, we must make sure that such replacement will
	 * not cause compilation error.
	 * 
	 * For this case, if the scope in client class invoking the to-be-pulled-up member, also invoke
	 * other member in the sub-type, it will be considered illegal to be pulled.
	 * 
	 * @param member
	 * @return
	 */
	private boolean isNotCausingCompilationError(UnitMemberWrapper member){
		//ICompilationUnitWrapper sourceUnit = member.getUnitWrapper();
		if(member.toString().contains("setMessage")){
			System.currentTimeMillis();
		}
		
		for(ProgramReference reference: member.getRefererPointList()){
			
			if(reference.getReferenceType() == ProgramReference.FIELD_ACCESS ||
					reference.getReferenceType() == ProgramReference.METHOD_INVOCATION){
				
				for(ReferenceInflucencedDetail refInDetail: reference.getVariableDeclarationList()){
					VariableDeclarationWrapper declaration = refInDetail.getDeclaration();
					for(DeclarationInfluencingDetail detail: declaration.getInfluencedReferenceList()){
						
						ProgramReference ref = detail.getReference();
						
						if(ref.getReferenceType() == ProgramReference.FIELD_ACCESS || 
								ref.getReferenceType() == ProgramReference.METHOD_INVOCATION){
							LowLevelGraphNode node = ref.getReferee();
							if(node instanceof UnitMemberWrapper){
								UnitMemberWrapper calleeMem = (UnitMemberWrapper)node;
								if(!(member.equals(calleeMem))){
									return false;
									/*if(calleeMem.getUnitWrapper().equals(sourceUnit)){
										return false;
									}*/
								}
							}
						}
					}
				}
			}
		}
		return true;
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
			
			if(member.getName().contains("setItem") && otherMember.getName().contains("setItem")){
				System.currentTimeMillis();
			}
			
			if(!otherMember.isOverrideSuperMember()  
					&& !isWithCounterCallingRelation(counterMemberList, otherMember) 
					&& !otherMember.isItsInvocationInfluencedByParameter()
					/*&& isNotCausingCompilationError(otherMember)*/){
				
				
				if(member.hasSameSignatureWith(otherMember)){
					return otherMember;
				}
				else{
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
			
			boolean isSuitableForPullingIntoNewUnit = isAValidatePullUpInTermsOfReference(refactoringPlace, null);
			boolean isSuitableForPullingIntoCommonSuperClass = isAValidatePullUpInTermsOfReference(refactoringPlace, commonAncestor);
			
			//System.currentTimeMillis();
			
			if((isWithSimilarBody || (member instanceof FieldWrapper)) && !isRelyOnOtherMemberInDeclaringClass &&
					/**
					 * either all the members or none of the members share(s) a common ancestor class.
					 */
					((commonAncestor != null) || (isWithoutAnySuperclass))){
				if(commonAncestor != null){
					if(isSuitableForPullingIntoCommonSuperClass){
						PullUpConcreteMemberToExistingClassOpportunity opp = 
								new PullUpConcreteMemberToExistingClassOpportunity(refactoringPlace, moduleList, commonAncestor);
						opportunities.add(opp);						
					}
				}
				else{
					if(isSuitableForPullingIntoNewUnit){
						PullUpConcreteMemberToNewClassOpportunity opp = 
								new PullUpConcreteMemberToNewClassOpportunity(refactoringPlace, moduleList);
						opportunities.add(opp);						
					}
				}
			}
			else if(commonAncestor != null){
				if(member instanceof MethodWrapper){
					if(isSuitableForPullingIntoCommonSuperClass){
						PullUpAbstractMethodToExistingClassOpportunity existingClassOpp = 
								new PullUpAbstractMethodToExistingClassOpportunity(refactoringPlace, commonAncestor, moduleList);
						opportunities.add(existingClassOpp);											
					}
				}
			}
			
			if(member instanceof MethodWrapper){
				if(isSuitableForPullingIntoNewUnit){
					PullUpMethodToNewInterfaceOpportunity newInterfaceOpp = 
							new PullUpMethodToNewInterfaceOpportunity(refactoringPlace, moduleList);
					opportunities.add(newInterfaceOpp);		
					
					
					PullUpAbstractMethodToNewClassOpportunity newClassOpp = new PullUpAbstractMethodToNewClassOpportunity(refactoringPlace, moduleList);
					opportunities.add(newClassOpp);
				}
				
				ArrayList<ICompilationUnitWrapper> commonInterfaceList = findCommonInterface(refactoringPlace);
				for(ICompilationUnitWrapper commonInterface: commonInterfaceList){
					if(isAValidatePullUpInTermsOfReference(refactoringPlace, commonInterface)){
						PullUpMethodToExistingInterfaceOpportunity existingInterfaceOpp =
								new PullUpMethodToExistingInterfaceOpportunity(refactoringPlace, commonInterface, moduleList);
						opportunities.add(existingInterfaceOpp);						
					}
				}
			}
		}
		
		return opportunities;
	}

	/**
	 * A member (e.g., a method m()) is invoked such as ((B)a).m(), if the "a" is declared like "A a = new B()",
	 * in this case, m() can only be pulled into A, not another interface.
	 * @return
	 */
	public boolean isAValidatePullUpInTermsOfReference(ArrayList<UnitMemberWrapper> refactoringPlace,
			ICompilationUnitWrapper targetUnit){
		for(UnitMemberWrapper pulledMember: refactoringPlace){
			for(ProgramReference reference: pulledMember.getRefererPointList()){
				for(ReferenceInflucencedDetail detail: reference.getVariableDeclarationList()){
					if(detail.getType() == DeclarationInfluencingDetail.ACCESS_OBJECT){
						ICompilationUnitWrapper declaredType = detail.getDeclaration().getUnitWrapper();
						ICompilationUnitWrapper existingSubclass = pulledMember.getUnitWrapper();
						if(existingSubclass.getAllAncestors().contains(declaredType)){
							if(targetUnit == null || !targetUnit.equals(declaredType)){
								return false;
							}
						} 
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * @param refactoringPlace
	 * @return
	 */
	private ArrayList<ICompilationUnitWrapper> findCommonInterface(
			ArrayList<UnitMemberWrapper> refactoringPlace) {
		ArrayList<ICompilationUnitWrapper> commonInterfaces = 
				refactoringPlace.get(0).getUnitWrapper().getAllAncesterSuperInterfaces();
		
		System.currentTimeMillis();
		
		for(int i=1; i<refactoringPlace.size(); i++){
			UnitMemberWrapper pulledMember = refactoringPlace.get(i);
			ArrayList<ICompilationUnitWrapper> interfaces = pulledMember.getUnitWrapper().getAllAncesterSuperInterfaces();
			
			Iterator<ICompilationUnitWrapper> interfIter = commonInterfaces.iterator();
			while(interfIter.hasNext()){
				ICompilationUnitWrapper interf = interfIter.next();
				if(!interfaces.contains(interf)){
					interfIter.remove();
				}
			}
		}
		
		return commonInterfaces;
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
