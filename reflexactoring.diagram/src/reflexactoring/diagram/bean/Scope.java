/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;

import reflexactoring.Type;

/**
 * @author linyun
 *
 */
public class Scope{
	private ArrayList<ICompilationUnitWrapper> scopeCompilationUnitList = new ArrayList<>();
	private UnitMemberWrapperList scopeMemberList = new UnitMemberWrapperList();
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
}
