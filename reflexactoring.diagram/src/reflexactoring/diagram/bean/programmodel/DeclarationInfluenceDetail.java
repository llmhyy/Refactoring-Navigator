/**
 * 
 */
package reflexactoring.diagram.bean.programmodel;

/**
 * @author linyun
 *
 */
public class DeclarationInfluenceDetail {
	public static final int OTHER = 0;
	public static final int ACCESS_OBJECT = 1;
	public static final int PARAMETER = 2;
	
	private ProgramReference reference;
	private int type;
	/**
	 * @param reference
	 * @param type
	 */
	public DeclarationInfluenceDetail(ProgramReference reference, int type) {
		super();
		this.reference = reference;
		this.type = type;
	}
	
	@Override
	public String toString(){
		String typeString = "";
		switch(type){
		case DeclarationInfluenceDetail.ACCESS_OBJECT:
			typeString = "access object";
			break;
		case DeclarationInfluenceDetail.PARAMETER:
			typeString = "influence parameter";
			break;
		}
		
		return reference.toString() + "(" + typeString + ")";
	}
	
	/**
	 * @return the reference
	 */
	public ProgramReference getReference() {
		return reference;
	}
	/**
	 * @param reference the reference to set
	 */
	public void setReference(ProgramReference reference) {
		this.reference = reference;
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
