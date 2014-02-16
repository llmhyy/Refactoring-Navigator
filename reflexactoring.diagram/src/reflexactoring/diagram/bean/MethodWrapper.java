/**
 * 
 */
package reflexactoring.diagram.bean;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import reflexactoring.diagram.action.semantic.TokenExtractor;

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
		this.extractTermFrequency(content);
		
		System.currentTimeMillis();
	}
	
	@Override
	public String getName() {
		return this.method.getName().getIdentifier();
	}
	
	public String toString(){
		return getName();
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

}
