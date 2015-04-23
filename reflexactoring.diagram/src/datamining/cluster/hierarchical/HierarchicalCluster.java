/**
 * 
 */
package datamining.cluster.hierarchical;

import java.util.ArrayList;

import datamining.cluster.Cluster;

/**
 * @author linyun
 *
 */
public class HierarchicalCluster extends Cluster {
	private HierarchicalCluster parent;
	private ArrayList<HierarchicalCluster> children = new ArrayList<>();
	
	private double mergePointValue = -1;
	
	public int size(){
		return this.getElements().size();
	}
	
	public String toString(){
		return this.getElements().toString();
	}
	
	/**
	 * @return the mergePointValue
	 */
	public double getMergePointValue() {
		return mergePointValue;
	}
	/**
	 * @param mergePointValue the mergePointValue to set
	 */
	public void setMergePointValue(double mergePointValue) {
		this.mergePointValue = mergePointValue;
	}
	/**
	 * @return the parent
	 */
	public HierarchicalCluster getParent() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(HierarchicalCluster parent) {
		this.parent = parent;
	}
	/**
	 * @return the children
	 */
	public ArrayList<HierarchicalCluster> getChildren() {
		return children;
	}
	/**
	 * @param children the children to set
	 */
	public void setChildren(ArrayList<HierarchicalCluster> children) {
		this.children = children;
	}

	/**
	 * @param clusterM
	 * @param clusterN
	 * @return
	 */
	public static HierarchicalCluster merge(HierarchicalCluster clusterM,
			HierarchicalCluster clusterN) {
		HierarchicalCluster cluster = new HierarchicalCluster();
		cluster.getElements().addAll(clusterM.getElements());
		cluster.getElements().addAll(clusterN.getElements());
		return cluster;
	}

	/**
	 * @param clusterM
	 */
	public void addChild(HierarchicalCluster cluster) {
		this.children.add(cluster);
	}
	
}
