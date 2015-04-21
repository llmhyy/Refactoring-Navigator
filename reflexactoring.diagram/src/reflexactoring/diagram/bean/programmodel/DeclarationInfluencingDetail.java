/**
 * 
 */
package reflexactoring.diagram.bean.programmodel;

/**
 * Given a variable declaration, this class shows the program reference influenced by such 
 * declaration. For example, given statement "A a;", a program reference influenced by it
 * may be "a.m1()", "a.attr1", etc.
 * 
 * Each relation between a declaration and a program reference has several types, i.e., 
 * access_obj, parameter, and other. access_obj means some unit member is called through
 * object access, e.g., a.m1(); parameter means some unit member is involved in the invocation
 * through parameter transition, e.g., m1(a);
 * 
 * @author linyun
 *
 */
public class DeclarationInfluencingDetail {
	public static final int OTHER = 0;
	public static final int ACCESS_OBJECT = 1;
	public static final int PARAMETER = 2;
	
	private ProgramReference reference;
	private int type;
	/**
	 * @param reference
	 * @param type
	 */
	public DeclarationInfluencingDetail(ProgramReference reference, int type) {
		super();
		this.reference = reference;
		this.type = type;
	}
	
	@Override
	public String toString(){
		String typeString = "";
		switch(type){
		case DeclarationInfluencingDetail.ACCESS_OBJECT:
			typeString = "access object";
			break;
		case DeclarationInfluencingDetail.PARAMETER:
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
