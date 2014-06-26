/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

/**
 * @author linyun
 *
 */
public class Population {
	private ArrayList<Genotype> list = new ArrayList<>();

	/**
	 * @return the list
	 */
	public ArrayList<Genotype> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(ArrayList<Genotype> list) {
		this.list = list;
	}
	
	public void add(Genotype genotype){
		list.add(genotype);
	}
	
	public int getSize(){
		return list.size();
	}
}
