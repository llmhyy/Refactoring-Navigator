/**
 * 
 */
package datamining.cluster;

/**
 * @author linyun
 *
 */
public interface IClusterElement {
	/**
	 * The returned distance should be a value ranging from 0 to 1
	 * @param element
	 * @return
	 */
	public double computeClusteringDistance(IClusterElement element);
}
