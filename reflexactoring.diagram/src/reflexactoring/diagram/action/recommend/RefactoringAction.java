/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import reflexactoring.diagram.bean.ModuleWrapper;

/**
 * @author linyun
 *
 */
public class RefactoringAction {
	private String actionName;
	private ModuleWrapper origin;
	private ModuleWrapper destination;
	
	/**
	 * @param actionName
	 * @param origin
	 * @param destination
	 */
	public RefactoringAction(String actionName, ModuleWrapper origin,
			ModuleWrapper destination) {
		super();
		this.actionName = actionName;
		this.origin = origin;
		this.destination = destination;
	}

	/**
	 * @return the actionName
	 */
	public String getActionName() {
		return actionName;
	}

	/**
	 * @param actionName the actionName to set
	 */
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	/**
	 * @return the origin
	 */
	public ModuleWrapper getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(ModuleWrapper origin) {
		this.origin = origin;
	}

	/**
	 * @return the destination
	 */
	public ModuleWrapper getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(ModuleWrapper destination) {
		this.destination = destination;
	}
	
	
}
