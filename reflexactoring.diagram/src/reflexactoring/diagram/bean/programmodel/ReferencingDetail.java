/**
 * 
 */
package reflexactoring.diagram.bean.programmodel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used to indicate how one node (unit, module) reference other node. Contrary to ProgramReference, it
 * is used in a higher level of model, much more away from source code details.
 * 
 * @author linyun
 *
 */
public class ReferencingDetail {
	
	public static int ALL = 0;
	public static int NEW = 1;
	public static int REFER = 2;
	public static int UNKNOWN = 3;
	
	private ArrayList<ProgramReference> detailReferenceList = new ArrayList<>();
	
	/**
	 * Indicating which type has been referenced by how many times.
	 */
	HashMap<Integer, Integer> map = new HashMap<>();
	
	public ReferencingDetail() {
		
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for(Integer key: map.keySet()){
			Integer value = map.get(key);
			if(key == ReferencingDetail.NEW){
				buffer.append("initialize ");
				
			}
			else if(key == ReferencingDetail.REFER){
				buffer.append("call ");
			}
			buffer.append(value);
			buffer.append(" times");
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	public void addOneReference(int type, ProgramReference reference){
		if(reference != null){
			detailReferenceList.add(reference);			
		}
		
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

	/**
	 * @return the detailReferenceList
	 */
	public ArrayList<ProgramReference> getDetailReferenceList() {
		return detailReferenceList;
	}

	/**
	 * @param detailReferenceList the detailReferenceList to set
	 */
	public void setDetailReferenceList(ArrayList<ProgramReference> detailReferenceList) {
		this.detailReferenceList = detailReferenceList;
	}
	
	
}
