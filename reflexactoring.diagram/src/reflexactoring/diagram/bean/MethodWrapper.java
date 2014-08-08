/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;

import reflexactoring.diagram.action.semantic.TokenExtractor;
import reflexactoring.diagram.util.DefaultComparator;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class MethodWrapper extends UnitMemberWrapper {
	
	private MethodDeclaration method;
	private String name;
	private ArrayList<String> parameters;
	private boolean isConstructor;
	private String returnType;
	
	public MethodWrapper(String name, String returnType, ArrayList<String> parameters, boolean isConstructor, 
			ICompilationUnitWrapper unitWrapper, HashMap<String, Integer> termFrequency, String description, 
			MethodDeclaration method){
		super(unitWrapper);
		this.name = name;
		this.setReturnType(returnType);
		this.parameters = parameters;
		this.isConstructor = isConstructor;
		this.termFrequency = termFrequency;
		this.description = description;
		this.method = method;
	}
	
	public MethodWrapper(MethodDeclaration method, ICompilationUnitWrapper unitWrapper){
		super(unitWrapper);
		this.setMethod(method);
		
		this.name = this.method.getName().getIdentifier();
		this.parameters = getParameterTypes();
		this.isConstructor = this.method.resolveBinding().isConstructor();
		Type type = this.method.getReturnType2();
		this.returnType = (type == null)? null : type.toString();
		
		//String content = new TokenExtractor(unitWrapper).extractTokens(method);
		//content = content + generateTitle();
		String content = generateTitle().toLowerCase();
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
		List<?> list = (List<?>)obj;
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
			
			boolean isSameReturnType = isWithSameReturnType(methodWrapper);
			
			return isSameReturnType && methodWrapper.getName().equals(this.getName()) &&
					isWithSameParameter(methodWrapper.getParameters(), this.getParameters());
		}
		return false;
	}
	
	@Override
	public double computeSimilarityForBeingPulledUp(UnitMemberWrapper otherMember){
		if(otherMember instanceof MethodWrapper){
			MethodWrapper thatMethod = (MethodWrapper)otherMember;
			if(!this.isWithSameReturnType(thatMethod)){
				return 0;
			}
			if(!this.isWithSameParameter(this.getParameters(), thatMethod.getParameters())){
				return 0;
			}
			
			double sim = ReflexactoringUtil.compareStringSimilarity(this.getName(), thatMethod.getName());
			
			return sim;
		}
		
		return 0;
	}
	
	@Override
	public double computeSimilarityWith(Object obj){
		if(obj instanceof MethodWrapper){
			MethodWrapper thatMethod = (MethodWrapper)obj;
			
			double returnTypeSimilarity = ReflexactoringUtil.
					compareStringSimilarity(getReturnType(), thatMethod.getReturnType());
			double nameSimilarity = ReflexactoringUtil.
					compareStringSimilarity(getName(), thatMethod.getName());
			double paramSimilarity = isWithSameParameter(getParameters(), thatMethod.getParameters())? 1 : 0;
			
			return (returnTypeSimilarity + nameSimilarity + paramSimilarity)/3;
		}
		
		return 0;
	}
	
	private boolean isWithSameReturnType(MethodWrapper thatMethod){
		boolean isSameReturnType = true;
		if(thatMethod.getMethod() != null && this.getMethod() != null){
			Type returnType1 = thatMethod.getMethod().getReturnType2();
			Type returnType2 = this.getMethod().getReturnType2();
			
			if(returnType1 == null && returnType2 == null){
				isSameReturnType = true;
			}
			else if(returnType1 != null && returnType2 != null){
				String thatTypeName = returnType1.toString();
				String thisTypeName = this.getMethod().getReturnType2().toString();
				
				isSameReturnType = thatTypeName.equals(thisTypeName);					
			}
			else{
				isSameReturnType = false;
			}
			
		}
		
		return isSameReturnType;
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

	public boolean isGetter(){
		boolean isGetter = fitGetOrSetPrefix("get");
		return isGetter;
	}
	
	public boolean isSetter(){
		boolean isSetter = fitGetOrSetPrefix("set");
		return isSetter;
	}
	
	private boolean fitGetOrSetPrefix(String prefixName){
		String[] words = ReflexactoringUtil.mixedSplitting(name);
		if(words[0].equals(prefixName)){
			StringBuffer buffer = new StringBuffer();
			for(int i=1; i<words.length; i++){
				buffer.append(words[i]);
			}
			String fieldName = buffer.toString();
			
			ICompilationUnitWrapper unit = this.getUnitWrapper();
			for(UnitMemberWrapper member: unit.getMembers()){
				if(member instanceof FieldWrapper){
					if(member.getName().toLowerCase().equals(fieldName.toLowerCase())){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean isCallingSuperMember(){
		for(ProgramReference reference: this.refereePointList){
			LowLevelGraphNode calleeNode = reference.getReferee();
			if(calleeNode instanceof UnitMemberWrapper){
				UnitMemberWrapper calleeMember = (UnitMemberWrapper)calleeNode;
				for(GraphNode node: this.getUnitWrapper().getParentList()){
					ICompilationUnitWrapper superUnit = (ICompilationUnitWrapper)node;
					if(superUnit.getMembers().contains(calleeMember)){
						return true;
					}
				}
			}
			
		}
		
		return false;
	}
	
	/**
	 * this member assign some fields inside the declaring class.
	 * @return
	 */
	public boolean isAssignFieldInUnit(){
		for(ProgramReference reference: this.refereePointList){
			LowLevelGraphNode calleeNode = reference.getReferee();
			if(calleeNode instanceof UnitMemberWrapper){
				UnitMemberWrapper calleeMember = (UnitMemberWrapper)calleeNode;
				if(calleeMember instanceof FieldWrapper && this.getUnitWrapper().getMembers().contains(calleeMember)){
					ASTNode node = reference.getASTNode();
					if(null == node){
						return true;
					}
					else{
						boolean isAssignment = false;
						while(!(node instanceof Statement) && node != null){
							if(node instanceof Assignment){
								isAssignment = true;
								break;
							}
							
							node = node.getParent();
						}
						
						if(isAssignment){
							return true;
						}
					}
				}
			}
			
		}
		
		return false;
	}
	
	public boolean isLegalMethodToBeMoved(){
		return !isConstructor() && 
				!(isGetter() || isSetter()) &&
				!isOverrideSuperMember() &&
				!isCallingSuperMember() &&
				!isAssignFieldInUnit();
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
}
