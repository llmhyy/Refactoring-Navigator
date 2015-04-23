/**
 * 
 */
package datamining.test;

import java.util.ArrayList;

import org.junit.Test;

import datamining.cluster.IClusterElement;
import datamining.cluster.hierarchical.HierarchicalCluster;
import datamining.cluster.hierarchical.HierarchicalClustering;

/**
 * @author linyun
 *
 */
public class HierarchicalTest {

	@Test
	public void test() {
		int[] array = new int[]{1, 2, 3, 10, 11, 13, 16, 17, 99, 100};
		ArrayList<IClusterElement> list = transfer(array);
		HierarchicalClustering clustering = new HierarchicalClustering(list, HierarchicalClustering.averageLinkage, 0);
		HierarchicalCluster cluster = clustering.buildDendrogram();
		
		System.out.println();
	}

	/**
	 * @param array
	 * @return
	 */
	private ArrayList<IClusterElement> transfer(int[] array) {
		ArrayList<IClusterElement> list = new ArrayList<>();
		for(int i=0; i<array.length; i++){
			IClusterElement element = new HNum(array[i]);
			list.add(element);
		}
		
		return list;
	}

}
