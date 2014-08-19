/**
 * 
 */
package reflexactoring.diagram.bean.programmodel;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.VariableDeclaration;

/**
 * @author linyun
 *
 */
public class VariableDeclarationWrapper {
	private ICompilationUnitWrapper unitWrapper;
	private String variableName;
	private String key;
	private boolean isField;
	private boolean isParameter;
	
	private ArrayList<DeclarationInfluenceDetail> influencedReferenceList = new ArrayList<>();
	
	private VariableDeclaration originalASTNode;

	/**
	 * @param unitWrapper
	 * @param variableName
	 * @param astNode
	 */
	public VariableDeclarationWrapper(ICompilationUnitWrapper unitWrapper,
			String variableName, VariableDeclaration astNode) {
		super();
		this.unitWrapper = unitWrapper;
		this.variableName = variableName;
		this.originalASTNode = astNode;
		this.isField = astNode.resolveBinding().isField();
		this.setParameter(astNode.resolveBinding().isParameter());
		this.setKey(astNode.resolveBinding().getKey());
	}
	
	public VariableDeclarationWrapper(ICompilationUnitWrapper unitWrapper,
			String variableName, VariableDeclaration astNode, String key, 
			boolean isField, boolean isParameter) {
		super();
		this.unitWrapper = unitWrapper;
		this.variableName = variableName;
		this.originalASTNode = astNode;
		this.isField = isField;
		this.setParameter(isParameter);
		this.setKey(key);
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
		return unitWrapper.getName() + " " + variableName;
	}

	/**
	 * @return the unitWrapper
	 */
	public ICompilationUnitWrapper getUnitWrapper() {
		return unitWrapper;
	}

	/**
	 * @param unitWrapper the unitWrapper to set
	 */
	public void setUnitWrapper(ICompilationUnitWrapper unitWrapper) {
		this.unitWrapper = unitWrapper;
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
	public ArrayList<DeclarationInfluenceDetail> getInfluencedReferenceList() {
		return influencedReferenceList;
	}

	/**
	 * @param influencedReferenceList the influencedReferenceList to set
	 */
	public void setInfluencedReferenceList(ArrayList<DeclarationInfluenceDetail> influencedReferenceList) {
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
	
	
}
