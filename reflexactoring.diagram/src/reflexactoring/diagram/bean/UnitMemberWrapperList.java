/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;

/**
 * @author linyun
 *
 */
public class UnitMemberWrapperList extends ArrayList<UnitMemberWrapper>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -346629208478952006L;
	
	public UnitMemberWrapper findMember(UnitMemberWrapper member){
		for(UnitMemberWrapper m: this){
			if(m.equals(member)){
				return m;
			}
		}
		
		return null;
	}
}
