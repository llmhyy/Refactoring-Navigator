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
	
	private ArrayList<ProgramReference> referenceList = new ArrayList<>();
	
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
		this.setKey(astNode.resolveBinding().getKey());
	}
	
	public VariableDeclarationWrapper(ICompilationUnitWrapper unitWrapper,
			String variableName, VariableDeclaration astNode, String key) {
		super();
		this.unitWrapper = unitWrapper;
		this.variableName = variableName;
		this.originalASTNode = astNode;
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
	 * @return the referenceList
	 */
	public ArrayList<ProgramReference> getReferenceList() {
		return referenceList;
	}

	/**
	 * @param referenceList the referenceList to set
	 */
	public void setReferenceList(ArrayList<ProgramReference> referenceList) {
		this.referenceList = referenceList;
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
	
	
}
