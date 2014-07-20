/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;

import reflexactoring.Type;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class ProgramModel{
	private ArrayList<ICompilationUnitWrapper> scopeCompilationUnitList = new ArrayList<>();
	private UnitMemberWrapperList scopeMemberList = new UnitMemberWrapperList();
	private ArrayList<ProgramReference> referenceList = new ArrayList<>();
	
	public ProgramModel clone(){
		ProgramModel clonedModel = new ProgramModel();
		ArrayList<ICompilationUnitWrapper> unitList = cloneUnits();
		
		clonedModel.setScopeCompilationUnitList(unitList);
		cloneUnitRelations(clonedModel, this);
		
		cloneMembers();
		cloneMemberRelations();
		
		cloneReferenceList();
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
		ArrayList<ICompilationUnitWrapper> clonedUnits = clonedModel.scopeCompilationUnitList;
		ArrayList<ICompilationUnitWrapper> units = model.scopeCompilationUnitList;
		for(int i=0; i<units.size(); i++){
			ICompilationUnitWrapper unit = units.get(i);
			ICompilationUnitWrapper clonedUnit = clonedUnits.get(i);
			/**
			 * clone super class relation
			 */
			ICompilationUnitWrapper superClass = unit.getSuperClass();
			if(null != superClass){
				int index = model.getICompilationUnitIndex(superClass);
				ICompilationUnitWrapper clonedSuperclass = clonedModel.scopeCompilationUnitList.get(index);
				clonedUnit.setSuperClass(clonedSuperclass);
			}
			
			/**
			 * clone interface relation
			 */
			ArrayList<ICompilationUnitWrapper> interfaceList = unit.getSuperInterfaceList();
			for(ICompilationUnitWrapper interf: interfaceList){
				int index = model.getICompilationUnitIndex(interf);
				ICompilationUnitWrapper clonedInterface = clonedModel.scopeCompilationUnitList.get(index);
				clonedUnit.addSuperInterface(clonedInterface);
			}
			
			/**
			 * clone parent list
			 */
			ArrayList<ICompilationUnitWrapper> parentList = (ArrayList<ICompilationUnitWrapper>) unit.getParentList();
			for(ICompilationUnitWrapper parent: parentList){
				int index = model.getICompilationUnitIndex(parent);
				ICompilationUnitWrapper clonedParent = clonedModel.scopeCompilationUnitList.get(index);
				clonedUnit.addParent(clonedParent);
			}
			
			/**
			 * clone child list
			 */
			ArrayList<ICompilationUnitWrapper> childList = (ArrayList<ICompilationUnitWrapper>) unit.getChildList();
			for(ICompilationUnitWrapper child: childList){
				int index = model.getICompilationUnitIndex(child);
				ICompilationUnitWrapper clonedChild = clonedModel.scopeCompilationUnitList.get(index);
				clonedUnit.addChild(clonedChild);
			}
			/**
			 * clone caller list
			 */
			ArrayList<ICompilationUnitWrapper> callerList = unit.getCallerCompilationUnitList();
			for(ICompilationUnitWrapper caller: callerList){
				int index = model.getICompilationUnitIndex(caller);
				ICompilationUnitWrapper clonedCaller = clonedModel.scopeCompilationUnitList.get(index);
				clonedUnit.addCaller(clonedCaller);
			}
			/**
			 * clone callee list
			 */
			ArrayList<ICompilationUnitWrapper> calleeList = unit.getCalleeCompilationUnitList();
			for(ICompilationUnitWrapper caller: calleeList){
				int index = model.getICompilationUnitIndex(caller);
				ICompilationUnitWrapper clonedCallee = clonedModel.scopeCompilationUnitList.get(index);
				clonedUnit.addCallee(clonedCallee);
			}
		}
	}

	public ProgramReference findReference(ProgramReference reference){
		for(ProgramReference ref: referenceList){
			if(ref.equals(reference)){
				return ref;
			}
		}
		
		return null;
	}
	
	public void updateUnitCallingRelationByMemberRelations(){
		for(ProgramReference reference: Settings.scope.getReferenceList()){
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
}
