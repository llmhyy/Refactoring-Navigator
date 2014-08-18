/**
 * 
 */
package reflexactoring.diagram.bean.heuristics;

import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;

/**
 * @author Adi
 *
 */
public class HeuristicModulePartFixMemberMap {
	private ModuleWrapper module;
	private UnitMemberWrapper member;
	
	/**
	 * @param modoule
	 * @param member
	 */
	public HeuristicModulePartFixMemberMap(ModuleWrapper modoule, UnitMemberWrapper member) {
		super();
		this.module = modoule;
		this.member = member;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HeuristicModulePartFixMemberMap other = (HeuristicModulePartFixMemberMap) obj;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		if (member == null) {
			if (other.member != null)
				return false;
		} else if (!member.equals(other.member))
			return false;
		return true;
	}



	/**
	 * @return the modoule
	 */
	public ModuleWrapper getModule() {
		return module;
	}
	/**
	 * @param modoule the modoule to set
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
