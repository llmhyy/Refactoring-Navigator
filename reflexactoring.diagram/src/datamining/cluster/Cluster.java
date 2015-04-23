/**
 * 
 */
package datamining.cluster;

import java.util.ArrayList;

/**
 * @author linyun
 *
 */
public class Cluster {
	protected ArrayList<IClusterElement> elements = new ArrayList<>();

	/**
	 * @return the clusters
	 */
	public ArrayList<IClusterElement> getElements() {
		return elements;
	}

	/**
	 * @param clusters the clusters to set
	 */
	public void setElements(ArrayList<IClusterElement> clusters) {
		this.elements = clusters;
	}
	
	public void addIClusterElement(IClusterElement element){
		this.elements.add(element);
	}
}
