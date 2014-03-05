/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

import reflexactoring.diagram.action.semantic.TokenExtractor;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class MethodWrapper extends UnitMemberWrapper {
	
	private MethodDeclaration method;
	
	public MethodWrapper(MethodDeclaration method, ICompilationUnitWrapper unitWrapper){
		super(unitWrapper);
		this.setMethod(method);
		
		String content = new TokenExtractor(unitWrapper).extractTokens(method);
		content = content + generateTitle();
		this.extractTermFrequency(content);
		
		System.currentTimeMillis();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof MethodWrapper){
			MethodWrapper methodWrapper = (MethodWrapper)obj;
			if(methodWrapper.getName().equals(this.getName())
					&& methodWrapper.getUnitWrapper().equals(this.getUnitWrapper())){
				return this.hasSameParameters(methodWrapper);
			};
		}
		
		return false;
	}
	
	public ArrayList<String> getParameterTypes(){
		ArrayList<String> parameterTypes = new ArrayList<>();
		
		Object obj = this.getMethod().getStructuralProperty(MethodDeclaration.PARAMETERS_PROPERTY);
		List list = (List)obj;
		for(Object node: list){
			if(node instanceof ASTNode){
				ASTNode astNode = (ASTNode)node;
				SingleVariableDeclaration svd = (SingleVariableDeclaration)astNode;
				Type type = svd.getType();
				if(type instanceof SimpleType){
					String typeName = ((SimpleType)type).getName().getFullyQualifiedName();
					parameterTypes.add(typeName);
				}
			}
		}
		
		return parameterTypes;
	}
	
	private boolean hasSameParameters(MethodWrapper methodWrapper){
		ArrayList<String> thisParameters = getParameterTypes();
		ArrayList<String> thatParameters = methodWrapper.getParameterTypes();
		
		if(thisParameters.size() != thatParameters.size()){
			return false;
		}
		
		Collections.sort(thisParameters);
		Collections.sort(thatParameters);
		
		for(int i=0; i<thisParameters.size(); i++){
			if(!thisParameters.get(i).equals(thatParameters.get(i))){
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String getName() {
		return this.method.getName().getIdentifier();
	}
	
	public String toString(){
		return getName();
	}
	
	public boolean isConstructor(){
		return this.method.resolveBinding().isConstructor();
	}

	/**
	 * @return the method
	 */
	public MethodDeclaration getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(MethodDeclaration method) {
		this.method = method;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.UnitMemberWrapper#getJavaMember()
	 */
	@Override
	public IMember getJavaMember() {
		return (IMember)this.method.resolveBinding().getJavaElement();
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.SuggestionObject#getType()
	 */
	@Override
	public String getTypeName() {
		return "method";
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.SuggestionObject#getNameWithTag()
	 */
	@Override
	public String getNameWithTag() {
		return "<a href=\"Method\">" + getName() + "</a>";
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.UnitMemberWrapper#getJavaElement()
	 */
	@Override
	protected ASTNode getJavaElement() {
		return getMethod();
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.Document#getDocName()
	 */
	@Override
	protected String getDocName() {
		return getName();
	}

}
