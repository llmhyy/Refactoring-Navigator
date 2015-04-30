/**
 * 
 */
package datamining.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import datamining.cluster.IClusterElement;
import datamining.cluster.hierarchical.HierarchicalCluster;
import datamining.cluster.hierarchical.HierarchicalClustering;
import datamining.test.bean.HNum;

/**
 * @author linyun
 *
 */
@RunWith(Parameterized.class)
public class TestHierarchicalClustering extends TestCase{

	private ArrayList<IClusterElement> list;
	
	private int linkageType;
	private double threshold;
	private String expectedResult;
	
	/**
	 * @param linkageType
	 * @param threshold
	 * @param expectedResult
	 */
	public TestHierarchicalClustering(int linkageType, double threshold,
			String expectedResult) {
		super();
		this.linkageType = linkageType;
		this.threshold = threshold;
		this.expectedResult = expectedResult;
	}
	
	@Before
	public void init(){
		list = transfer(new int[]{1, 2, 3, 10, 11, 13, 16, 17, 99, 100});	
	}
	
	@SuppressWarnings("rawtypes")
	@Parameterized.Parameters
	public static Collection primeNumbers() {
	    return Arrays.asList(new Object[][] {
	         { HierarchicalClustering.completeLinkage, 2.5, "[[1, 2, 3], [10, 11], [13], [16, 17], [99, 100]]"},
	         { HierarchicalClustering.averageLinkage, 2.5, "[[1, 2, 3], [10, 11, 13], [16, 17], [99, 100]]"},
	         { HierarchicalClustering.averageLinkage, 3, "[[1, 2, 3], [10, 11, 13], [16, 17], [99, 100]]"},
	         { HierarchicalClustering.singleLinkage, 3, "[[1, 2, 3], [10, 11, 13, 16, 17], [99, 100]]"}
	    });
	}

	@Test
	public void testHierarchicalClustering() {
		
		HierarchicalClustering clustering = new HierarchicalClustering(list, linkageType, threshold);
		ArrayList<HierarchicalCluster> clusterList = clustering.produceClusters();
		
		assertEquals(clusterList.toString(), expectedResult);
		//HierarchicalCluster cluster = clustering.buildDendrogram();
		//System.currentTimeMillis();
		
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
