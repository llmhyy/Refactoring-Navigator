/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;

/**
 * @author linyun
 *
 */
public class ModuleDependencyConfidenceTable extends ArrayList<ModuleDependencyConfidence> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -989959841245002172L;

	public int getRowNumber(){
		return this.size();
	}
	
	public int getColumnNumber(){
		
		if(this.get(0) == null){
			return 0;
		}
		else{
			return this.get(0).getModuleList().size();
		}
		
	}
	
	public double[][] convertToRawTable(){
		int rows = getRowNumber();
		int columns = getColumnNumber();
		double[][] table = new double[rows][columns];
		for(int i=0; i<rows; i++){
			for(int j=0; j<columns; j++){
				table[i][j] = this.get(i).getConfidenceList()[j];
			}
		}
		
		return table;
	}
}
