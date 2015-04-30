/**
 * 
 */
package datamining.test.bean;

import datamining.cluster.IClusterElement;

/**
 * @author linyun
 *
 */
public class HNum implements IClusterElement {

	private int num;
	
	public HNum(int num){
		this.num = num;
	}
	
	@Override
	public double computeClusteringDistance(IClusterElement element) {
		if(element instanceof HNum){
			HNum n = (HNum)element;
			return Math.abs(this.num-n.getNum());
		}
		
		return 0;
	}

	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}

	/**
	 * @param num the num to set
	 */
	public void setNum(int num) {
		this.num = num;
	}

	public String toString(){
		return this.num + "";
	}
	
}
