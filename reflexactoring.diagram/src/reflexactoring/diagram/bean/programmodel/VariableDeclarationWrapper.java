/**
 * 
 */
package reflexactoring.diagram.bean.programmodel;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jdt.core.dom.VariableDeclaration;

/**
 * @author linyun
 *
 */
public class VariableDeclarationWrapper {
	private ICompilationUnitWrapper variableType;
	private String variableName;
	private String key;
	private boolean isField;
	private boolean isParameter;
	//private FieldWrapper correspondingField;
	
	private ArrayList<DeclarationInfluencingDetail> influencedReferenceList = new ArrayList<>();
	
	private VariableDeclaration originalASTNode;

	/**
	 * @param unitWrapper
	 * @param variableName
	 * @param astNode
	 */
	public VariableDeclarationWrapper(ICompilationUnitWrapper unitWrapper,
			String variableName, VariableDeclaration astNode) {
		super();
		this.variableType = unitWrapper;
		this.variableName = variableName;
		this.originalASTNode = astNode;
		this.isField = astNode.resolveBinding().isField();
		this.setParameter(astNode.resolveBinding().isParameter());
		this.setKey(astNode.resolveBinding().getKey());
		
		/*if(isField){
			String qualifiedName = astNode.resolveBinding().getDeclaringClass().getQualifiedName();
			FieldWrapper field = Settings.scope.findField(qualifiedName, variableName);
			setCorrespondingField(field);
			field.setVariableDeclaration(this);
		}*/
	}
	
	public VariableDeclarationWrapper(ICompilationUnitWrapper unitWrapper,
			String variableName, VariableDeclaration astNode, String key, 
			boolean isField, boolean isParameter) {
		super();
		this.variableType = unitWrapper;
		this.variableName = variableName;
		this.originalASTNode = astNode;
		this.isField = isField;
		//this.correspondingField = correspodingField;
		this.setParameter(isParameter);
		this.setKey(key);
	}
	
	public void removeReference(ProgramReference reference, int type){
		Iterator<DeclarationInfluencingDetail> iter = this.influencedReferenceList.iterator();
		while(iter.hasNext()){
			DeclarationInfluencingDetail detail = iter.next();
			if(detail.getReference() == reference && detail.getType() == type){
				iter.remove();
				break;
			}
		}
	}
	
	@Override 
	public int hashCode(){
		return this.key.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof VariableDeclarationWrapper){
			VariableDeclarationWrapper vdw = (VariableDeclarationWrapper)obj;
			return vdw.getKey().equals(getKey());
		}
		
		return false;
	}
	
	@Override
	public String toString(){
		return variableType.getName() + " " + variableName;
	}

	/**
	 * @return the unitWrapper
	 */
	public ICompilationUnitWrapper getVariableType() {
		return variableType;
	}

	/**
	 * @param unitWrapper the unitWrapper to set
	 */
	public void setVariableType(ICompilationUnitWrapper unitWrapper) {
		this.variableType = unitWrapper;
	}

	/**
	 * @return the variableName
	 */
	public String getVariableName() {
		return variableName;
	}

	/**
	 * @param variableName the variableName to set
	 */
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	/**
	 * @return the astNode
	 */
	public VariableDeclaration getAstNode() {
		return originalASTNode;
	}

	/**
	 * @param astNode the astNode to set
	 */
	public void setAstNode(VariableDeclaration astNode) {
		this.originalASTNode = astNode;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the influencedReferenceList
	 */
	public ArrayList<DeclarationInfluencingDetail> getInfluencedReferenceList() {
		return influencedReferenceList;
	}

	/**
	 * @param influencedReferenceList the influencedReferenceList to set
	 */
	public void setInfluencedReferenceList(ArrayList<DeclarationInfluencingDetail> influencedReferenceList) {
		this.influencedReferenceList = influencedReferenceList;
	}

	/**
	 * @return the isField
	 */
	public boolean isField() {
		return isField;
	}

	/**
	 * @param isField the isField to set
	 */
	public void setField(boolean isField) {
		this.isField = isField;
	}

	/**
	 * @return the isParameter
	 */
	public boolean isParameter() {
		return isParameter;
	}

	/**
	 * @param isParameter the isParameter to set
	 */
	public void setParameter(boolean isParameter) {
		this.isParameter = isParameter;
	}

	/**
	 * Note that, if a program reference with type of FIELD_ACCESS is influenced by
	 * a variable declaration, the referee of this program reference MUST be this field
	 * corresponding to such a variable declaration. 
	 * 
	 * This method possibly wrong in that a field is accessed directly, e.g., a.b is 
	 * influenced by 'A a'. In this case, b rather than a could be returned. FIXME
	 * 
	 * @param model
	 * @return
	 */
	public FieldWrapper findCorrespondingFieldWrapper() {
		for(DeclarationInfluencingDetail detail: this.influencedReferenceList){
			ProgramReference reference = detail.getReference();
			if(reference.getReferenceType() == ProgramReference.FIELD_ACCESS){
				FieldWrapper field = (FieldWrapper) reference.getReferee();
				return field;
			}
		}
		
		return null;
	}
	
	
}
