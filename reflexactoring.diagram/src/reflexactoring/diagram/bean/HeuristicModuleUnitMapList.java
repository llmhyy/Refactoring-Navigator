/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;

/**
 * @author linyun
 *
 */
public class HeuristicModuleUnitMapList extends ArrayList<HeuristicModuleUnitMap>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5077806055903361433L;
	
	public void addMap(HeuristicModuleUnitMap map){
		if(!this.contains(map)){
			this.add(map);
		}
	}
}
