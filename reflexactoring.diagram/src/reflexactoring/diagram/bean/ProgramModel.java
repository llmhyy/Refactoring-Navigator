/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;

import reflexactoring.Type;
import reflexactoring.diagram.action.smelldetection.bean.CloneSet;

/**
 * @author linyun
 *
 */
public class ProgramModel{
	private ArrayList<ICompilationUnitWrapper> scopeCompilationUnitList = new ArrayList<>();
	private UnitMemberWrapperList scopeMemberList = new UnitMemberWrapperList();
	private ArrayList<ProgramReference> referenceList = new ArrayList<>();
	private ArrayList<CloneSet> cloneSets = new ArrayList<>();
	
	public ProgramModel clone(){
		ProgramModel clonedModel = new ProgramModel();
		
		ArrayList<ICompilationUnitWrapper> unitList = cloneUnits();		
		clonedModel.setScopeCompilationUnitList(unitList);
		cloneUnitRelations(clonedModel, this);
		
		UnitMemberWrapperList memberList = cloneMembers(clonedModel, this);
		clonedModel.setScopeMemberList(memberList);
		cloneMemberRelations(clonedModel, this);
		
		ArrayList<ProgramReference> prList = cloneReference(clonedModel, this);
		clonedModel.setReferenceList(prList);
		//cloneReferenceRelations(clonedModel, this);
		
		return clonedModel;
	}
	
	/**
	 * In this step, only clone mapping module and java icompilation unit.
	 * @return
	 */
	private ArrayList<ICompilationUnitWrapper> cloneUnits() {
		ArrayList<ICompilationUnitWrapper> clonedUnits = new ArrayList<>();
		for(ICompilationUnitWrapper unit: scopeCompilationUnitList){
			
			ICompilationUnitWrapper clonedUnit = new ICompilationUnitWrapper(unit.getCompilationUnit());			
			clonedUnit.setMappingModule(unit.getMappingModule());
			clonedUnit.setInterface(unit.isInterface());
			clonedUnit.setJavaUnit(unit.getJavaUnit());
			
			clonedUnits.add(clonedUnit);
		}
		
		return clonedUnits;
	}
	
	private void cloneUnitRelations(ProgramModel clonedModel, ProgramModel model){
		ArrayList<ICompilationUnitWrapper> clonedUnits = clonedModel.getScopeCompilationUnitList();
		ArrayList<ICompilationUnitWrapper> units = model.getScopeCompilationUnitList();
		for(int i=0; i<units.size(); i++){
			ICompilationUnitWrapper unit = units.get(i);
			ICompilationUnitWrapper clonedUnit = clonedUnits.get(i);
			/**
			 * clone super class relation
			 */
			ICompilationUnitWrapper superClass = unit.getSuperClass();
			if(null != superClass){
				int index = model.getICompilationUnitIndex(superClass);
				ICompilationUnitWrapper clonedSuperclass = clonedModel.getScopeCompilationUnitList().get(index);
				clonedUnit.setSuperClass(clonedSuperclass);
			}
			
			/**
			 * clone interface relation
			 */
			ArrayList<ICompilationUnitWrapper> interfaceList = unit.getSuperInterfaceList();
			for(ICompilationUnitWrapper interf: interfaceList){
				int index = model.getICompilationUnitIndex(interf);
				ICompilationUnitWrapper clonedInterface = clonedModel.getScopeCompilationUnitList().get(index);
				clonedUnit.addSuperInterface(clonedInterface);
			}
			
			/**
			 * clone parent list
			 */
			ArrayList<ICompilationUnitWrapper> parentList = (ArrayList<ICompilationUnitWrapper>) unit.getParentList();
			for(ICompilationUnitWrapper parent: parentList){
				int index = model.getICompilationUnitIndex(parent);
				ICompilationUnitWrapper clonedParent = clonedModel.getScopeCompilationUnitList().get(index);
				clonedUnit.addParent(clonedParent);
			}
			
			/**
			 * clone child list
			 */
			ArrayList<ICompilationUnitWrapper> childList = (ArrayList<ICompilationUnitWrapper>) unit.getChildList();
			for(ICompilationUnitWrapper child: childList){
				int index = model.getICompilationUnitIndex(child);
				ICompilationUnitWrapper clonedChild = clonedModel.getScopeCompilationUnitList().get(index);
				clonedUnit.addChild(clonedChild);
			}
			/**
			 * clone caller list
			 */
			ArrayList<ICompilationUnitWrapper> callerList = unit.getCallerCompilationUnitList();
			for(ICompilationUnitWrapper caller: callerList){
				int index = model.getICompilationUnitIndex(caller);
				ICompilationUnitWrapper clonedCaller = clonedModel.getScopeCompilationUnitList().get(index);
				clonedUnit.addCaller(clonedCaller);
			}
			/**
			 * clone callee list
			 */
			ArrayList<ICompilationUnitWrapper> calleeList = unit.getCalleeCompilationUnitList();
			for(ICompilationUnitWrapper caller: calleeList){
				int index = model.getICompilationUnitIndex(caller);
				ICompilationUnitWrapper clonedCallee = clonedModel.getScopeCompilationUnitList().get(index);
				clonedUnit.addCallee(clonedCallee);
			}
		}
	}
	
	/**
	 * In this step, clone the member list, including its field/method and its corresponding belonging ICompilationUnitWrapper
	 */
	private UnitMemberWrapperList cloneMembers(ProgramModel clonedModel, ProgramModel model){
		UnitMemberWrapperList clonedMembers = new UnitMemberWrapperList();
		for(UnitMemberWrapper member: model.getScopeMemberList()){			
			/**
			 * As the constructor method of Member need its ICompilationUnitWrapper, 
			 * we have to get the corresponding ICompilationUnitWrapper at the mean time.
			 */
			ICompilationUnitWrapper memberUnit = member.getUnitWrapper();
			int index = model.getICompilationUnitIndex(memberUnit);
			ICompilationUnitWrapper clonedMemberUnit = clonedModel.getScopeCompilationUnitList().get(index);						

			UnitMemberWrapper clonedMember = null;			
			if(member instanceof FieldWrapper){
				clonedMember = new FieldWrapper(((FieldWrapper) member).getField(), clonedMemberUnit);
			}else if(member instanceof MethodWrapper){
				clonedMember = new MethodWrapper(((MethodWrapper) member).getMethod(), clonedMemberUnit);
			}
			clonedMembers.add(clonedMember);
		}
		return clonedMembers;		
	}
	
	private void cloneMemberRelations(ProgramModel clonedModel, ProgramModel model){
		ArrayList<ICompilationUnitWrapper> clonedUnits = clonedModel.getScopeCompilationUnitList();
		ArrayList<ICompilationUnitWrapper> units = model.getScopeCompilationUnitList();
		for(int i=0; i<units.size(); i++){
			ICompilationUnitWrapper unit = units.get(i);
			ICompilationUnitWrapper clonedUnit = clonedUnits.get(i);
			
			/**
			 * clone member relation
			 */
			ArrayList<UnitMemberWrapper> memberList = unit.getMembers();
			for(UnitMemberWrapper member: memberList){
				int index = model.getUnitMemberIndex(member);
				UnitMemberWrapper clonedMember = clonedModel.getScopeMemberList().get(index);
				clonedUnit.addMember(clonedMember);
			}
		}
		
	}

	/**
	 * In this step, clone the reference list, including its type, ASTnode and its corresponding referer/referee member
	 */
	private ArrayList<ProgramReference> cloneReference(ProgramModel clonedModel, ProgramModel model){
		ArrayList<ProgramReference> clonedReferences = new ArrayList<ProgramReference>();
		for(ProgramReference reference: model.getReferenceList()){
			/**
			 * As the constructor method of ProgramReference need its referer and referee member, 
			 * we have to get the corresponding UnitMemberWrappers at the mean time.
			 */
			UnitMemberWrapper referer = reference.getReferer();
			UnitMemberWrapper referee = reference.getReferee();
			int refererIndex = model.getUnitMemberIndex(referer);
			int refereeIndex = model.getUnitMemberIndex(referee);
			UnitMemberWrapper clonedReferer = clonedModel.scopeMemberList.get(refererIndex);
			UnitMemberWrapper clonedReferee = clonedModel.scopeMemberList.get(refereeIndex);
			
			ProgramReference clonedReference = new ProgramReference(clonedReferer, clonedReferee, reference.getASTNode());
			clonedReference.setReferenceType(reference.getReferenceType());
			
			clonedReferences.add(clonedReference);
			clonedReferer.addProgramReferee(clonedReference);
			clonedReferee.addProgramReferer(clonedReference);
		}
		return clonedReferences;
	}
	
	/*private void cloneReferenceRelations(ProgramModel clonedModel, ProgramModel model){
		UnitMemberWrapperList clonedMembers = clonedModel.scopeMemberList;
		UnitMemberWrapperList members = model.scopeMemberList;
		for(int i=0; i<members.size(); i++){
			UnitMemberWrapper member = members.get(i);
			UnitMemberWrapper clonedMember = clonedMembers.get(i);
			
			*//**
			 * clone reference relation of referer
			 *//*
			ArrayList<ProgramReference> refererList = member.refererPointList;
			for(ProgramReference refererPoint: refererList){
				int index = model.getProgramReferenceIndex(refererPoint);
				ProgramReference clonedRefererPoint = clonedModel.referenceList.get(index);
				clonedMember.addProgramReferer(clonedRefererPoint);
			}

			*//**
			 * clone reference relation of referee
			 *//*
			ArrayList<ProgramReference> refereeList = member.refereePointList;
			for(ProgramReference refereePoint: refereeList){
				int index = model.getProgramReferenceIndex(refereePoint);
				ProgramReference clonedRefereePoint = clonedModel.referenceList.get(index);
				clonedMember.addProgramReferee(clonedRefereePoint);
			}
		}		
	}*/	

	public ProgramReference findReference(ProgramReference reference){
		for(ProgramReference ref: referenceList){
			if(ref.equals(reference)){
				return ref;
			}
		}
		
		return null;
	}
	
	public void updateUnitCallingRelationByMemberRelations(){
		/**
		 * clear original call relations between compilation unit.
		 */
		for(ICompilationUnitWrapper unitWrapper: this.scopeCompilationUnitList){
			unitWrapper.setCalleeCompilationUnitList(new ArrayList<ICompilationUnitWrapper>());
			unitWrapper.setCallerCompilationUnitList(new ArrayList<ICompilationUnitWrapper>());
		}
		
		for(ProgramReference reference: this.getReferenceList()){
			UnitMemberWrapper refererMember = reference.getReferer();
			ICompilationUnitWrapper refererUnit = refererMember.getUnitWrapper();
			
			UnitMemberWrapper refereeMember = reference.getReferee();
			ICompilationUnitWrapper refereeUnit = refereeMember.getUnitWrapper();
			
			if(!refererUnit.equals(refereeUnit)){
				refererUnit.addCallee(refereeUnit);
				refereeUnit.addCaller(refererUnit);
				
				refererUnit.putReferringDetail(refereeUnit, reference.getASTNode());
			}
		}
	}
	
	/**
	 * @return the scopeCompilationUnitList
	 */
	public ArrayList<ICompilationUnitWrapper> getScopeCompilationUnitList() {
		return scopeCompilationUnitList;
	}
	
	public ArrayList<ICompilationUnit> getScopeRawCompilationUnitList(){
		ArrayList<ICompilationUnit> units = new ArrayList<>();
		for(ICompilationUnitWrapper wrapper: this.scopeCompilationUnitList){
			units.add(wrapper.getCompilationUnit());
		}
		
		return units;
	}

	/**
	 * @param scopeCompilationUnitList the scopeCompilationUnitList to set
	 */
	public void setScopeCompilationUnitList(ArrayList<ICompilationUnitWrapper> scopeCompilationUnitList) {
		this.scopeCompilationUnitList = scopeCompilationUnitList;
	}
	
	public ICompilationUnitWrapper findUnit(String fullQualifiedName){
		for(ICompilationUnitWrapper unit: this.scopeCompilationUnitList){
			//ICompilationUnitWrapper wrapper = new ICompilationUnitWrapper(unit, false);
			if(unit.getFullQualifiedName().equals(fullQualifiedName)){
				return unit;
			}
		}
		
		return null;
	}
	
	public int getICompilationUnitIndex(ICompilationUnitWrapper u){
		for(int i=0; i<this.scopeCompilationUnitList.size(); i++){
			ICompilationUnitWrapper unit = this.scopeCompilationUnitList.get(i);
			if(unit.getFullQualifiedName().equals(u.getFullQualifiedName())){
				return i;
			}
		}
		
		return -1;

	}
	
	public int getUnitMemberIndex(UnitMemberWrapper u){
		for(int i=0; i<this.scopeMemberList.size(); i++){
			UnitMemberWrapper member = this.scopeMemberList.get(i);
			if(member.equals(u)){
				return i;
			}
		}
		
		return -1;

	}
	
	public int getProgramReferenceIndex(ProgramReference r){
		for(int i=0; i<this.referenceList.size(); i++){
			ProgramReference reference = this.referenceList.get(i);
			if(reference.equals(r)){
				return i;
			}
		}
		
		return -1;
	}
	
	public ICompilationUnitWrapper findUnit(Type type){
		String packageName = type.getPackageName();
		String typeName = type.getName();
		
		String identifier = packageName+"."+typeName;
		
		return findUnit(identifier);
	}
	
	public void removeUnit(ICompilationUnitWrapper toBeRemovedUnit){
		this.scopeCompilationUnitList.remove(toBeRemovedUnit);
		for(ICompilationUnitWrapper unit: this.scopeCompilationUnitList){
			unit.getCalleeCompilationUnitList().remove(toBeRemovedUnit);
			unit.getCallerCompilationUnitList().remove(toBeRemovedUnit);
		}
		
		/**
		 * remove corresponding method and dependency relation.
		 */
		ArrayList<UnitMemberWrapper> toBeRemovedOnes = new ArrayList<>();
		for(UnitMemberWrapper member: this.scopeMemberList){
			if(member.getUnitWrapper().equals(toBeRemovedUnit)){
				toBeRemovedOnes.add(member);
			}
		}
		
		for(UnitMemberWrapper member: toBeRemovedOnes){
			this.scopeMemberList.remove(member);
		}
		
		for(UnitMemberWrapper member: this.scopeMemberList){
			for(UnitMemberWrapper toBeRemovedMember: toBeRemovedOnes){
				member.getCalleeList().remove(toBeRemovedMember);
				member.getCallerList().remove(toBeRemovedMember);
			}
		}
	}
	
	public UnitMemberWrapper findMember(UnitMemberWrapper member){
		return getScopeMemberList().findMember(member);
	}
	
	public ArrayList<ProgramReference> findProgramReferenceByCallerMember(UnitMemberWrapper callerMember){
		ArrayList<ProgramReference> references = new ArrayList<>();
		for(ProgramReference reference: this.referenceList){
			if(reference.getReferer().equals(callerMember)){
				references.add(reference);
			}
		}
		
		return references;
	}

	/**
	 * @return the scopeMemberList
	 */
	public UnitMemberWrapperList getScopeMemberList() {
		return scopeMemberList;
	}

	/**
	 * @param scopeMemberList the scopeMemberList to set
	 */
	public void setScopeMemberList(UnitMemberWrapperList scopeMemberList) {
		this.scopeMemberList = scopeMemberList;
	}

	/**
	 * @return the referenceList
	 */
	public ArrayList<ProgramReference> getReferenceList() {
		return referenceList;
	}

	/**
	 * @param referenceList the referenceList to set
	 */
	public void setReferenceList(ArrayList<ProgramReference> referenceList) {
		this.referenceList = referenceList;
	}

	/**
	 * @return the cloneSets
	 */
	public ArrayList<CloneSet> getCloneSets() {
		return cloneSets;
	}

	/**
	 * @param cloneSets the cloneSets to set
	 */
	public void setCloneSets(ArrayList<CloneSet> cloneSets) {
		this.cloneSets = cloneSets;
	}
}
