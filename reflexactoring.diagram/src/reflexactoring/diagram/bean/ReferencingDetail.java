/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.HashMap;

/**
 * @author linyun
 *
 */
public class ReferencingDetail {
	
	public static int ALL = 0;
	public static int NEW = 1;
	public static int REFER = 2;
	public static int UNKNOWN = 3;
	
	/**
	 * Indicating which type has been referenced by how many times.
	 */
	HashMap<Integer, Integer> map = new HashMap<>();
	
	public ReferencingDetail() {
		
	}
	
	public void addOneReference(int type){
		if(this.map.keySet().contains(type)){
			int value = this.map.get(type);
			value++;
			this.map.put(type, value);
		}
		else{
			this.map.put(type, 1);
		}
	}

	@SuppressWarnings("unchecked")
	protected ReferencingDetail clone() {
		ReferencingDetail detail = new ReferencingDetail();
		detail.setMap((HashMap<Integer, Integer>) this.getMap().clone());
		return detail;
	}

	/**
	 * @return the map
	 */
	public HashMap<Integer, Integer> getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(HashMap<Integer, Integer> map) {
		this.map = map;
	}
	
	
}
