/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

/**
 * @author linyun
 *
 */
public class Violation {
	public final static int ABSENCE = 1;
	public final static int DISONANCE = 2;
	
	private int sourceModuleIndex;
	private int destModuleIndex;
	private int type;
	/**
	 * @param sourceModuleIndex
	 * @param destModuleIndex
	 * @param type
	 */
	public Violation(int sourceModuleIndex, int destModuleIndex, int type) {
		super();
		this.sourceModuleIndex = sourceModuleIndex;
		this.destModuleIndex = destModuleIndex;
		this.type = type;
	}
	
	public String toString(){
		return sourceModuleIndex + "->" + destModuleIndex;
	}
	
	/**
	 * @return the sourceModuleIndex
	 */
	public int getSourceModuleIndex() {
		return sourceModuleIndex;
	}
	/**
	 * @param sourceModuleIndex the sourceModuleIndex to set
	 */
	public void setSourceModuleIndex(int sourceModuleIndex) {
		this.sourceModuleIndex = sourceModuleIndex;
	}
	/**
	 * @return the destModuleIndex
	 */
	public int getDestModuleIndex() {
		return destModuleIndex;
	}
	/**
	 * @param destModuleIndex the destModuleIndex to set
	 */
	public void setDestModuleIndex(int destModuleIndex) {
		this.destModuleIndex = destModuleIndex;
	}
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
}
