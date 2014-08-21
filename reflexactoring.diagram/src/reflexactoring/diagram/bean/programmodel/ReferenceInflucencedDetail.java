/**
 * 
 */
package reflexactoring.diagram.bean.programmodel;

/**
 * @author linyun
 *
 */
public class ReferenceInflucencedDetail {
	private VariableDeclarationWrapper declaration;
	private int type;
	/**
	 * @param declaration
	 * @param type
	 */
	public ReferenceInflucencedDetail(VariableDeclarationWrapper declaration,
			int type) {
		super();
		this.declaration = declaration;
		this.type = type;
	}
	/**
	 * @return the declaration
	 */
	public VariableDeclarationWrapper getDeclaration() {
		return declaration;
	}
	/**
	 * @param declaration the declaration to set
	 */
	public void setDeclaration(VariableDeclarationWrapper declaration) {
		this.declaration = declaration;
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
