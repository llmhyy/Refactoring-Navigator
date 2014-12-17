/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

/**
 * @author linyun
 *
 */
public class Violation {
	/**
	 * a dependency exist in high level model but not in low level model.
	 */
	public final static int DEPENDENCY_ABSENCE = 1;
	/**
	 * a dependency exist in low level model but not in high level model.
	 */
	public final static int DEPENDENCY_DIVERGENCE = 2;
	
	/**
	 * an inheritance exist in high level model but not in low level model.
	 */
	public final static int INHERITANCE_ABSENCE = 3;
	/**
	 * an inheritance exist in low level model but not in high level model.
	 */
	public final static int INHERITANCE_DIVERGENCE = 4;
	
	/**
	 * a creation exist in high level model but not in low level model.
	 */
	public final static int CREATION_ABSENCE = 5;
	/**
	 * a creation exist in low level model but not in high level model.
	 */
	public final static int CREATION_DIVERGENCE = 6;
	/**
	 * 
	 */
	public final static int EMPTY_MODULE = 7;
	
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
		String typeName  = "";
		switch(type){
		case DEPENDENCY_ABSENCE: 
			typeName = "dependency absence";
			break;
		case DEPENDENCY_DIVERGENCE: 
			typeName = "dependency divergence";
			break;
		case INHERITANCE_ABSENCE: 
			typeName = "inheritance absence";
			break;
		case INHERITANCE_DIVERGENCE: 
			typeName = "inheritance divergence";
			break;
		case CREATION_ABSENCE: 
			typeName = "creation absence";
			break;
		case CREATION_DIVERGENCE: 
			typeName = "creation divergence";
			break;
		case EMPTY_MODULE: 
			typeName = "empty module";
			break;
		}
		return sourceModuleIndex + "->" + destModuleIndex + "(" + typeName + ")";
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
