/**
 * 
 */
package reflexactoring.diagram.bean;

/**
 * @author linyun
 *
 */
public class ReferencingDetail {
	
	public static int ALL = 0;
	public static int NEW = 1;
	public static int REFER = 2;
	public static int UNKNOWN = 3;
	
	private int referencingTimes;
	private int referencingType;
	
	/**
	 * @param referencingTimes
	 * @param referencingType
	 */
	public ReferencingDetail(int referencingTimes, int referencingType) {
		super();
		this.referencingTimes = referencingTimes;
		this.referencingType = referencingType;
	}

	protected ReferencingDetail clone() {
		ReferencingDetail detail = new ReferencingDetail(this.referencingTimes, this.referencingType);
		return detail;
	}

	/**
	 * @return the referencingTimes
	 */
	public int getReferencingTimes() {
		return referencingTimes;
	}
	/**
	 * @param referencingTimes the referencingTimes to set
	 */
	public void setReferencingTimes(int referencingTimes) {
		this.referencingTimes = referencingTimes;
	}
	/**
	 * @return the referencingType
	 */
	public int getReferencingType() {
		return referencingType;
	}
	/**
	 * @param referencingType the referencingType to set
	 */
	public void setReferencingType(int referencingType) {
		this.referencingType = referencingType;
	}
	
	
}
