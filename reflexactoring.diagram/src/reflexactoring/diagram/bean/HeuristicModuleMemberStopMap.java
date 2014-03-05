/**
 * 
 */
package reflexactoring.diagram.bean;

/**
 * @author linyun
 *
 */
public class HeuristicModuleMemberStopMap {
	private ModuleWrapper module;
	private UnitMemberWrapper member;
	/**
	 * @param module
	 * @param member
	 */
	public HeuristicModuleMemberStopMap(ModuleWrapper module,
			UnitMemberWrapper member) {
		super();
		this.module = module;
		this.member = member;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof HeuristicModuleMemberStopMap){
			HeuristicModuleMemberStopMap stopMap = (HeuristicModuleMemberStopMap)obj;
			return stopMap.getMember().equals(this.getModule()) 
					&& stopMap.getMember().equals(this.getMember());
		}
		
		return false;
	}
	
	public String toString(){
		return this.module.getName() + "><" + member.getName();
	}
	
	/**
	 * @return the module
	 */
	public ModuleWrapper getModule() {
		return module;
	}
	/**
	 * @param module the module to set
	 */
	public void setModule(ModuleWrapper module) {
		this.module = module;
	}
	/**
	 * @return the member
	 */
	public UnitMemberWrapper getMember() {
		return member;
	}
	/**
	 * @param member the member to set
	 */
	public void setMember(UnitMemberWrapper member) {
		this.member = member;
	}
	
	
}
