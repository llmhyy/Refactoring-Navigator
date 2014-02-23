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
	
	public double[][] convertModuleUnitsSimilarityTableToRawTable(){
		if(this == null || this.size() == 0){
			return new double[0][0];
		}
		
		int width = this.get(0).getValues().length;
		double[][] similarityTable = new double[this.size()][width];
		
		for(int i=0; i<this.size(); i++){
			ModuleUnitsSimilarity similarity = this.get(i);
			for(int j=0; j<similarity.getValues().length; j++){
				similarityTable[i][j] = similarity.getValues()[j];
			}
		}
		
		return similarityTable;
	}
}
