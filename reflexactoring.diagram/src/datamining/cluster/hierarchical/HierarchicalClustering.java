/**
 * 
 */
package datamining.cluster.hierarchical;

import java.util.ArrayList;

import datamining.cluster.IClusterElement;

/**
 * @author linyun
 *
 */
public class HierarchicalClustering {
	public static final int completeLinkage = 0;
	public static final int averageLinkage = 1;
	public static final int singleLinkage = 2;
	
	
	private ArrayList<IClusterElement> elements;
	private int linkageType;
	private double threshold;
	
	/**
	 * @param elements
	 * @param linkageType
	 * @param threshold
	 */
	public HierarchicalClustering(ArrayList<IClusterElement> elements,
			int linkageType, double threshold) {
		super();
		this.elements = elements;
		this.linkageType = linkageType;
		this.threshold = threshold;
	}
	
	public ArrayList<HierarchicalCluster> produceClusters(){
		ArrayList<HierarchicalCluster> clusterList = new ArrayList<>();
		HierarchicalCluster root = buildDendrogram();
		retrieveQulifiedClusters(root, clusterList);
		
		return clusterList;
	}
	
	/**
	 * @param root
	 * @param clusterList
	 * @param threshold
	 */
	private void retrieveQulifiedClusters(HierarchicalCluster parent,
			ArrayList<HierarchicalCluster> clusterList) {
		if(parent.getMergePointValue() > this.threshold){
			for(HierarchicalCluster cluster: parent.getChildren()){
				retrieveQulifiedClusters(cluster, clusterList);
			}
		}
		else{
			clusterList.add(parent);
		}
		
	}

	public HierarchicalCluster buildDendrogram(){
		/**
		 * initialize the clusters with single element
		 */
		ArrayList<HierarchicalCluster> clusterList = new ArrayList<>();
		for(IClusterElement element: elements){
			HierarchicalCluster cluster = new HierarchicalCluster();
			cluster.addIClusterElement(element);
			clusterList.add(cluster);
		} 
		
		double[][] hierarchicalTable = new double[elements.size()][elements.size()];
		/**
		 * initialize the hierarchical table, each entry is the distance between two elements.
		 */
		for(int i=0; i<elements.size(); i++){
			for(int j=0; j<elements.size(); j++){
				if(i > j){
					hierarchicalTable[i][j] = elements.get(i).computeClusteringDistance(elements.get(j));
				}
				else{
					/**
					 * I only care about half of the table
					 */
					hierarchicalTable[i][j] = -1;
				}
			}
		}
		
		Double shortestDistance = null;
		do{
			/**
			 * find two clusters with shortest distance
			 */
			shortestDistance = null;
			int m = 0;
			int n = 0;
			for(int i=0; i<elements.size(); i++){
				for(int j=0; j<i; j++){
					if(shortestDistance == null && hierarchicalTable[i][j] != -1){
						m = j;
						n = i;
						shortestDistance = hierarchicalTable[i][j];
					}
					else{
						if(shortestDistance != null && hierarchicalTable[i][j] < shortestDistance && hierarchicalTable[i][j] != -1){
							m = j;
							n = i;
							shortestDistance = hierarchicalTable[i][j];
						}
					}
					
				}
			}
			
			if(shortestDistance == null){
				break;
			}
			
			/**
			 * merge the two clusters
			 */
			HierarchicalCluster clusterM = getLargestClusterContainingClusterIndex(clusterList, m);
			HierarchicalCluster clusterN = getLargestClusterContainingClusterIndex(clusterList, n);
			HierarchicalCluster cluster = HierarchicalCluster.merge(clusterM, clusterN);
			cluster.addChild(clusterM);
			cluster.addChild(clusterN);
			cluster.setMergePointValue(shortestDistance);
			clusterM.setParent(cluster);
			clusterM.setParent(cluster);
			clusterList.add(cluster);
			
			/**
			 * the merged cluster is represented by the cluster with smaller index, and then,
			 * update the distance between other clusters and the merged one
			 */
			for(int k=0; k<elements.size(); k++){
				if(k < m){
					hierarchicalTable[m][k] = computeDistanceConsideringLinkage(k, clusterList, cluster, hierarchicalTable);
				}
				else if (k > m){
					hierarchicalTable[k][m] = computeDistanceConsideringLinkage(k, clusterList, cluster, hierarchicalTable);
				}
			}
			/**
			 * remove the cluster with larger index in table
			 */
			for(int k=0; k<elements.size(); k++){
				if(k < n){
					hierarchicalTable[n][k] = -1;					
				}
				else{
					hierarchicalTable[k][n] = -1;
				}
			}
			
			
		}while(shortestDistance != null);
		
		HierarchicalCluster rootCluster = clusterList.get(clusterList.size()-1);
		return rootCluster;
	}

	/**
	 * @param k
	 * @param cluster
	 * @return
	 */
	private double computeDistanceConsideringLinkage(int k, ArrayList<HierarchicalCluster> clusterList,
			HierarchicalCluster cluster, double[][] hierarchialTable) {
		if(!isRemoved(hierarchialTable, k)){
			HierarchicalCluster targetCluster = getLargestClusterContainingClusterIndex(clusterList, k);
			
			
			switch(linkageType){
			case HierarchicalClustering.singleLinkage:
				return minimumDistance(cluster, targetCluster);
			case HierarchicalClustering.completeLinkage:
				return maximumDistance(cluster, targetCluster);
			case HierarchicalClustering.averageLinkage:
				return averageDistance(cluster, targetCluster);
			}
		}
		
		return -1;
	}
	
	/**
	 * @param hierarchialTable
	 * @param k
	 * @return
	 */
	private boolean isRemoved(double[][] hierarchialTable, int k) {
		for(int i=0; i<hierarchialTable.length; i++){
			if(i < k){
				if(hierarchialTable[k][i] != -1){
					return false;
				}
			}
			else {
				if(hierarchialTable[i][k] != -1){
					return false;
				}
			}
		}
		return true;
	}

	private double minimumDistance(HierarchicalCluster cluster, HierarchicalCluster targetCluster){
		double dis = -1;
		for(IClusterElement ele1: cluster.getElements()){
			for(IClusterElement ele2: targetCluster.getElements()){
				double distance = ele1.computeClusteringDistance(ele2);
				if(dis == -1){
					dis = distance;
				}
				else{
					if(dis > distance){
						dis = distance;
					}
				}
			}
		}
		return dis;
	}
	
	private double maximumDistance(HierarchicalCluster cluster, HierarchicalCluster targetCluster){
		double dis = -1;
		for(IClusterElement ele1: cluster.getElements()){
			for(IClusterElement ele2: targetCluster.getElements()){
				double distance = ele1.computeClusteringDistance(ele2);
				if(dis == -1){
					dis = distance;
				}
				else{
					if(dis < distance){
						dis = distance;
					}
				}
			}
		}
		return dis;
	}
	
	private double averageDistance(HierarchicalCluster cluster, HierarchicalCluster targetCluster){
		double dis = 0;
		int count = 0;
		for(IClusterElement ele1: cluster.getElements()){
			for(IClusterElement ele2: targetCluster.getElements()){
				double distance = ele1.computeClusteringDistance(ele2);
				dis += distance;
				count++;
			}
		}
		
		return dis/count;
	}

	/**
	 * @param clusterList
	 * @param m
	 * @return
	 */
	private HierarchicalCluster getLargestClusterContainingClusterIndex(
			ArrayList<HierarchicalCluster> clusterList, int m) {
		IClusterElement element = this.elements.get(m);
		HierarchicalCluster resultCluster = null;
		for(HierarchicalCluster cluster: clusterList){
			if(cluster.getElements().contains(element)){
				if(resultCluster == null){
					resultCluster = cluster;
				}
				else{
					if(resultCluster.size() < cluster.size()){
						resultCluster = cluster;
					}
				}
			}
		}
		
		return resultCluster;
	}

	/**
	 * @return the elements
	 */
	public ArrayList<IClusterElement> getElements() {
		return elements;
	}
	/**
	 * @param elements the elements to set
	 */
	public void setElements(ArrayList<IClusterElement> elements) {
		this.elements = elements;
	}
	/**
	 * @return the linkageType
	 */
	public int getLinkageType() {
		return linkageType;
	}
	/**
	 * @param linkageType the linkageType to set
	 */
	public void setLinkageType(int linkageType) {
		this.linkageType = linkageType;
	}
	/**
	 * @return the threshold
	 */
	public double getThreshold() {
		return threshold;
	}
	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	
	
}
