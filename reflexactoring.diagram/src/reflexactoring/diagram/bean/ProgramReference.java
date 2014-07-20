/**
 * 
 */
package reflexactoring.diagram.bean;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * This class stands for a reference in program, for example method invocation, field access
 * and etc.
 * 
 * @author linyun
 *
 */
public class ProgramReference {
	public static final int METHOD_INVOCATION = 1;
	public static final int FIELD_ACCESS = 2;
	
	private int referenceType;
	private ASTNode originalASTNode;
	
	private UnitMemberWrapper referer;
	private UnitMemberWrapper referee;
	
	public ProgramReference(UnitMemberWrapper referer, UnitMemberWrapper referee, ASTNode originalASTNode){
		this.referer = referer;
		this.referee = referee;
		this.originalASTNode = originalASTNode;
		
		if(referee instanceof MethodWrapper){
			this.referenceType = ProgramReference.METHOD_INVOCATION;
		}
		else if(referee instanceof FieldWrapper){
			this.referenceType = ProgramReference.FIELD_ACCESS;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((referee == null) ? 0 : referee.hashCode());
		result = prime * result + referenceType;
		result = prime * result + ((referer == null) ? 0 : referer.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProgramReference other = (ProgramReference) obj;
		if (referee == null) {
			if (other.referee != null)
				return false;
		} else if (!referee.equals(other.referee))
			return false;
		if (referenceType != other.referenceType)
			return false;
		if (referer == null) {
			if (other.referer != null)
				return false;
		} else if (!referer.equals(other.referer))
			return false;
		return true;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(referer.toString()  + " ");
		if(referenceType == ProgramReference.FIELD_ACCESS){
			buffer.append("access field ");
		}
		else if(referenceType == ProgramReference.METHOD_INVOCATION){
			buffer.append("invoke method ");
		}
		buffer.append(referee.toString());
		
		return buffer.toString();
	}
	
	/**
	 * @return the referenceType
	 */
	public int getReferenceType() {
		return referenceType;
	}
	/**
	 * @param referenceType the referenceType to set
	 */
	public void setReferenceType(int referenceType) {
		this.referenceType = referenceType;
	}
	/**
	 * @return the originalASTNode
	 */
	public ASTNode getASTNode() {
		return originalASTNode;
	}
	/**
	 * @param originalASTNode the originalASTNode to set
	 */
	public void setOriginalASTNode(ASTNode originalASTNode) {
		this.originalASTNode = originalASTNode;
	}
	/**
	 * @return the referer
	 */
	public UnitMemberWrapper getReferer() {
		return referer;
	}
	/**
	 * @param referer the referer to set
	 */
	public void setReferer(UnitMemberWrapper referer) {
		this.referer = referer;
	}
	/**
	 * @return the referee
	 */
	public UnitMemberWrapper getReferee() {
		return referee;
	}
	/**
	 * @param referee the referee to set
	 */
	public void setReferee(UnitMemberWrapper referee) {
		this.referee = referee;
	}
	
	
	
}
