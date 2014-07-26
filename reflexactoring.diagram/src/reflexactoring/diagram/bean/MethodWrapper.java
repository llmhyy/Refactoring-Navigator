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

/**
 * @author linyun
 *
 */
public class MethodWrapper extends UnitMemberWrapper {
	
	private MethodDeclaration method;
	private String name;
	private ArrayList<String> parameters;
	private boolean isConstructor;
	
	public MethodWrapper(String name, ArrayList<String> parameters, boolean isConstructor, ICompilationUnitWrapper unitWrapper){
		super(unitWrapper);
		this.name = name;
		this.parameters = parameters;
		this.isConstructor = isConstructor;
	}
	
	public MethodWrapper(MethodDeclaration method, ICompilationUnitWrapper unitWrapper){
		super(unitWrapper);
		this.setMethod(method);
		
		this.name = this.method.getName().getIdentifier();
		this.parameters = getParameterTypes();
		this.isConstructor = this.method.resolveBinding().isConstructor();
		
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
		
		ArrayList<String> thatParameters = methodWrapper.getParameters();
		
		if(this.parameters.size() != thatParameters.size()){
			return false;
		}
		
		Collections.sort(this.parameters);
		Collections.sort(thatParameters);
		
		for(int i=0; i<this.parameters.size(); i++){
			if(!this.parameters.get(i).equals(thatParameters.get(i))){
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String toString(){
		return this.unitWrapper.getSimpleName() + "." + getName();
	}
	
	public boolean isConstructor(){
		return this.isConstructor;
	}
	
	public void setConstructor(boolean isConstructor){
		this.isConstructor = isConstructor;
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
	public ASTNode getJavaElement() {
		return getMethod();
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.Document#getDocName()
	 */
	@Override
	protected String getDocName() {
		return getName();
	}

	/**
	 * @return the parameters
	 */
	public ArrayList<String> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(ArrayList<String> parameters) {
		this.parameters = parameters;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.UnitMemberWrapper#hasSameSignatureWith(reflexactoring.diagram.bean.UnitMemberWrapper)
	 */
	@Override
	public boolean hasSameSignatureWith(UnitMemberWrapper member) {
		if(member instanceof MethodWrapper){
			MethodWrapper methodWrapper = (MethodWrapper)member;
			
			boolean isSameReturnType = true;
			if(methodWrapper.getMethod() != null && this.getMethod() != null){
				String thatTypeName = methodWrapper.getMethod().getReturnType2().toString();
				String thisTypeName = methodWrapper.getMethod().getReturnType2().toString();
				
				isSameReturnType = thatTypeName.equals(thisTypeName);
			}
			
			return isSameReturnType && methodWrapper.getName().endsWith(this.getName()) &&
					isWithSameParameter(methodWrapper.getParameters(), this.getParameters());
		}
		return false;
	}
	
	private boolean isWithSameParameter(ArrayList<String> params1, ArrayList<String> params2){
		if(params1.size() != params2.size()){
			return false;
		}
		else if(params1.size() == 0){
			return true;
		}
		
		Collections.sort(params1);
		Collections.sort(params2);
		
		for(int i=0; i<params1.size(); i++){
			if(!params1.get(i).equals(params2.get(i))){
				return false;
			}
		}
		
		return true;
	}

}
