/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMember;

/**
 * @author linyun
 *
 */
public abstract class UnitMemberWrapper implements LowLevelSuggestionObject, GraphNode {
	protected ICompilationUnitWrapper unitWrapper;
	protected ArrayList<UnitMemberWrapper> callerList = new ArrayList<>();
	protected ArrayList<UnitMemberWrapper> calleeList = new ArrayList<>();

	/**
	 * get the the java element they wrap
	 * @return
	 */
	public abstract IMember getJavaMember();
	
	/**
	 * @return the unitWrapper
	 */
	public ICompilationUnitWrapper getUnitWrapper() {
		return unitWrapper;
	}

	/**
	 * @param unitWrapper the unitWrapper to set
	 */
	public void setUnitWrapper(ICompilationUnitWrapper unitWrapper) {
		this.unitWrapper = unitWrapper;
	}

	@Override
	public List<? extends GraphNode> getCallerList() {
		return this.callerList;
	}

	
	@Override
	public List<? extends GraphNode> getCalleeList() {
		return this.calleeList;
	}
	
	public void addCaller(UnitMemberWrapper member){
		for(UnitMemberWrapper m: callerList){
			if(m.getJavaMember().equals(member.getJavaMember())){
				return;
			}
		}
		
		this.callerList.add(member);
	}
	
	public void addCallee(UnitMemberWrapper member){
		for(UnitMemberWrapper m: calleeList){
			if(m.getJavaMember().equals(member.getJavaMember())){
				return;
			}
		}
		
		this.calleeList.add(member);
	}
	
}
