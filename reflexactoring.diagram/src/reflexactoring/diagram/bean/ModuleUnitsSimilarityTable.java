/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;

/**
 * @author linyun
 *
 */
public class ModuleUnitsSimilarityTable extends ArrayList<ModuleUnitsSimilarity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7640249307675978287L;
	
	public int getRowNumber(){
		return this.size();
	}
	
	public int getColumnNumber(){
		
		if(this.get(0) == null){
			return 0;
		}
		else{
			return this.get(0).getUnits().size();
		}
		
	}
}
