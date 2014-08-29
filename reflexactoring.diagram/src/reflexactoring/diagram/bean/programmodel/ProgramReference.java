/**
 * 
 */
package reflexactoring.diagram.bean.programmodel;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTNode;

import reflexactoring.diagram.bean.LowLevelGraphNode;

/**
 * This class stands for a reference in program, for example method invocation, field access
 * and etc.
 * 
 * @author linyun
 *
 */
public class ProgramReference {
	/**
	 * The following types are used between members.
	 */
	public static final int METHOD_INVOCATION = 1;
	public static final int FIELD_ACCESS = 2;
	/**
	 * The following types are used between member and unit.
	 */
	/**
	 * return type for method and type for field
	 */
	public static final int TYPE_DECLARATION = 3;
	public static final int PARAMETER_ACCESS = 4;
	public static final int NEW_DEFAULT_CONSTRUCTOR = 5;
	//public static final int SIMPLE_VARIABLE_DECLARATION = 6;
	
	private ASTNode originalASTNode;
	
	private UnitMemberWrapper referer;
	private LowLevelGraphNode referee;
	private int referenceType;
	
	private ArrayList<ReferenceInflucencedDetail> variableDeclarationList = new ArrayList<>();
	
	public ProgramReference(UnitMemberWrapper referer, LowLevelGraphNode referee, ASTNode originalASTNode, 
			int referenceType, ArrayList<ReferenceInflucencedDetail> variableDeclarationList){
		this.referer = referer;
		this.referee = referee;
		this.originalASTNode = originalASTNode;
		this.referenceType = referenceType;
		this.variableDeclarationList = variableDeclarationList;
		
		if(referee instanceof MethodWrapper){
			this.referenceType = ProgramReference.METHOD_INVOCATION;
		}
		else if(referee instanceof FieldWrapper){
			this.referenceType = ProgramReference.FIELD_ACCESS;
		}
	}
	
	public void removeDominantDeclaration(VariableDeclarationWrapper dec, int influenceType){
		Iterator<ReferenceInflucencedDetail> iter = this.variableDeclarationList.iterator();
		while(iter.hasNext()){
			ReferenceInflucencedDetail refDetail = iter.next();
			if(refDetail.getDeclaration().equals(dec) && refDetail.getType() == influenceType){
				iter.remove();
				break;
			}
		}
	}
	
	public ArrayList<VariableDeclarationWrapper> findVariableDeclaratoins(int influenceType){
		ArrayList<VariableDeclarationWrapper> decList = new ArrayList<>();
		for(ReferenceInflucencedDetail refDetail: variableDeclarationList){
			if(refDetail.getType() == influenceType){
				decList.add(refDetail.getDeclaration());
			}
		}
		
		return decList;
	}
	
	public ProgramReference(UnitMemberWrapper referer, LowLevelGraphNode referee, ASTNode originalASTNode, 
			int referenceType){
		this.referer = referer;
		this.referee = referee;
		this.originalASTNode = originalASTNode;
		this.referenceType = referenceType;
		
		if(referee instanceof MethodWrapper){
			this.referenceType = ProgramReference.METHOD_INVOCATION;
		}
		else if(referee instanceof FieldWrapper){
			this.referenceType = ProgramReference.FIELD_ACCESS;
		}
	}
	
	public boolean isInvokedByAccessingObject(){
		for(ReferenceInflucencedDetail detail: this.variableDeclarationList){
			if(detail.getType() == DeclarationInfluencingDetail.ACCESS_OBJECT){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isInokedByParameter(){
		for(ReferenceInflucencedDetail detail: this.variableDeclarationList){
			if(detail.getType() == DeclarationInfluencingDetail.PARAMETER){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return the variableDeclarationList
	 */
	public ArrayList<ReferenceInflucencedDetail> getVariableDeclarationList() {
		return variableDeclarationList;
	}

	/**
	 * @param variableDeclarationList the variableDeclarationList to set
	 */
	public void setVariableDeclarationList(
			ArrayList<ReferenceInflucencedDetail> variableDeclarationList) {
		this.variableDeclarationList = variableDeclarationList;
	}

	public boolean isInvocationOrAccess(){
		return (this.referenceType == ProgramReference.METHOD_INVOCATION) ||
				(this.referenceType == ProgramReference.FIELD_ACCESS);
	}
	
	public boolean isTypeOrParameter(){
		return (this.referenceType == ProgramReference.PARAMETER_ACCESS) ||
				(this.referenceType == ProgramReference.TYPE_DECLARATION);
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
		else if(referenceType == ProgramReference.PARAMETER_ACCESS){
			buffer.append("use parameter of ");
		}
		else if(referenceType == ProgramReference.TYPE_DECLARATION){
			buffer.append("of type ");
		}
		else if(referenceType == ProgramReference.NEW_DEFAULT_CONSTRUCTOR){
			buffer.append("invoke default constructor of ");
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
	public LowLevelGraphNode getReferee() {
		return referee;
	}
	/**
	 * @param referee the referee to set
	 */
	public void setReferee(LowLevelGraphNode referee) {
		this.referee = referee;
	}
	
	
	
}
