/**
 * 
 */
package reflexactoring.diagram.bean;

import reflexactoring.Type;
import reflexactoring.TypeDependency;

/**
 * @author linyun
 *
 */
public class DependencyWrapper {
	private Type origin;
	private Type destination;
	/**
	 * @param origin
	 * @param destination
	 */
	public DependencyWrapper(Type origin, Type destination) {
		super();
		this.origin = origin;
		this.destination = destination;
	}
	
	public DependencyWrapper(TypeDependency dep){
		this.origin = dep.getOrigin();
		this.destination = dep.getDestination();
	}
	
	@Override
	public String toString(){
		return origin.getName() + "->" + destination.getName();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof DependencyWrapper){
			DependencyWrapper thatDep = (DependencyWrapper)obj;
			
			String thatOrigin = thatDep.getOrigin().getPackageName() + "." + thatDep.getOrigin().getName();
			String thatDestination = thatDep.getDestination().getPackageName() + "." + thatDep.getDestination().getName();
			
			String thisOrigin = getOrigin().getPackageName() + "." + getOrigin().getName();
			String thisDestination = getDestination().getPackageName() + "." + getDestination().getName();
			
			if(thatOrigin.equals(thisOrigin) && thatDestination.equals(thisDestination)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return the origin
	 */
	public Type getOrigin() {
		return origin;
	}
	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Type origin) {
		this.origin = origin;
	}
	/**
	 * @return the destination
	 */
	public Type getDestination() {
		return destination;
	}
	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Type destination) {
		this.destination = destination;
	}
	
	
}
