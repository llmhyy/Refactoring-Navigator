/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.bean;

import java.util.ArrayList;

import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;

/**
 * @author linyun
 *
 */
public class CloneSet {
	
	private String id;
	private ArrayList<CloneInstance> instances = new ArrayList<>();
	
	/**
	 * @param id
	 */
	public CloneSet(String id) {
		super();
		this.id = id;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for(CloneInstance instance: this.instances){
			buffer.append(instance.toString() + "\n");
		}
		
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof CloneSet){
			CloneSet set = (CloneSet)obj;
			return set.getId().equals(getId());
		}
		
		return false;
	}

	/**
	 * @return the instances
	 */
	public ArrayList<CloneInstance> getInstances() {
		return instances;
	}

	/**
	 * @param instances the instances to set
	 */
	public void setInstances(ArrayList<CloneInstance> instances) {
		this.instances = instances;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isAllInstancesLocatedInSameUnit(){
		ICompilationUnitWrapper unit = getInstances().get(0).getMember().getUnitWrapper();
		for(int i=1; i<getInstances().size(); i++){
			CloneInstance instance = getInstances().get(i);
			if(!instance.getMember().getUnitWrapper().equals(unit)){
				return false;
			}
		}
		return true;
	}
}
