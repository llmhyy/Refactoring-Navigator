/**
 * 
 */
package reflexactoring.diagram.bean.programmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;

import reflexactoring.diagram.bean.LowLevelGraphNode;
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
			MethodDeclaration method, boolean isAbstract, String modifier){
		super(unitWrapper);
		this.name = name;
		this.setReturnType(returnType);
		this.parameters = parameters;
		this.isConstructor = isConstructor;
		this.termFrequency = termFrequency;
		this.description = description;
		this.method = method;
		this.isAbstract = isAbstract;
		this.modifier = modifier;
	}
	
	public MethodWrapper(MethodDeclaration method, ICompilationUnitWrapper unitWrapper){
		super(unitWrapper);
		this.setMethod(method);
		
		this.name = this.method.getName().getIdentifier();
		this.parameters = getParameterTypes();
		this.isConstructor = this.method.resolveBinding().isConstructor();
		Type type = this.method.getReturnType2();
		this.returnType = (type == null)? null : type.toString();
		
		int modifierFlag = method.getModifiers();
		this.modifier = ModifierWrapper.parseSecurityModifer(modifierFlag);
		
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
				parameterTypes.add(type.toString());
				/*if(type instanceof SimpleType){
					String typeName = ((SimpleType)type).getName().getFullyQualifiedName();
					parameterTypes.add(typeName);
				}
				else if(type instanceof PrimitiveType){
					String typeName = ((PrimitiveType)type).toString();
					parameterTypes.add(typeName);
				}*/
			}
		}
		
		return parameterTypes;
	}
	
	public void removeParameter(ICompilationUnitWrapper targetUnit){
		boolean isParamsContainsTargetUnit = false;
		
		Iterator<String> strIter = this.parameters.iterator();
		while(strIter.hasNext()){
			String paramType = strIter.next();
			if(paramType.equals(targetUnit.getName())){
				strIter.remove();
				isParamsContainsTargetUnit = true;
				break;
			}
		}
		
		Iterator<ProgramReference> refIter = this.getRefereePointList().iterator();
		while(refIter.hasNext()){
			ProgramReference reference = refIter.next(); 
			if(reference.getReferenceType() == ProgramReference.PARAMETER_ACCESS){
				LowLevelGraphNode node = reference.getReferee();
				if(node instanceof ICompilationUnitWrapper){
					if(targetUnit.equals(node)){
						refIter.remove();
					}
				}
			}
		}
		
		if(isParamsContainsTargetUnit){
			for(ProgramReference reference: this.getRefererPointList()){
				
				double bestSim = 0;
				ReferenceInflucencedDetail refDetail0 = null;
				DeclarationInfluencingDetail decDetail0 = null;
				
				for(ReferenceInflucencedDetail refDetail: reference.getVariableDeclarationList()){
					if(refDetail.getType() == DeclarationInfluencingDetail.PARAMETER){
						ICompilationUnitWrapper paramType = refDetail.getDeclaration().getUnitWrapper();
						
						double sim = paramType.computeSimilarityWith(targetUnit);
						if(sim >= bestSim){
							bestSim = sim;
							refDetail0 = refDetail;
							
							for(DeclarationInfluencingDetail decDetail: refDetail.getDeclaration().getInfluencedReferenceList()){
								if(decDetail.getReference() == reference){
									decDetail0 = decDetail;
									break;
								}
							}
						}
					}
				}
				
				if(refDetail0 != null){
					refDetail0.setType(DeclarationInfluencingDetail.ACCESS_OBJECT);
					decDetail0.setType(DeclarationInfluencingDetail.ACCESS_OBJECT);					
				}
				
			}
			
		}
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
	
	public ArrayList<String> cloneParameters(){
		ArrayList<String> params = new ArrayList<>();
		for(String param: this.parameters){
			params.add(param);
		}
		
		return params;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(ArrayList<String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * This method only consider the method signature.
	 */
	@Override
	public boolean hasSameSignatureWith(UnitMemberWrapper member) {
		if(member instanceof MethodWrapper){
			MethodWrapper methodWrapper = (MethodWrapper)member;
			
			boolean isSameReturnType = isWithSameReturnType(methodWrapper);
			
			return isSameReturnType && methodWrapper.getName().equals(this.getName()) &&
					isWithSameParameter(methodWrapper.getParameters());
		}
		return false;
	}
	
	@Override
	public double computeSimilarityForBeingPulledUp(UnitMemberWrapper otherMember){
		if(otherMember instanceof MethodWrapper){
			MethodWrapper thatMethod = (MethodWrapper)otherMember;
			
			
			
			if(!this.isWithSameParameter(thatMethod.getParameters())){
				return 0;
			}
			
			double returnTypeSimilarity = ReflexactoringUtil.
					compareStringSimilarity(getReturnType(), thatMethod.getReturnType());
			
			double nameSimilarity = ReflexactoringUtil.compareStringSimilarity(this.getName(), thatMethod.getName());
			
			double sim = (returnTypeSimilarity + nameSimilarity)/2;
			
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
			double paramSimilarity = isWithSameParameter(thatMethod.getParameters())? 1 : 0;
			
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
	
	public boolean isWithSameParameter(ArrayList<String> params){
		if(this.parameters.size() != params.size()){
			return false;
		}
		else if(params.size() == 0){
			return true;
		}
		
		Collections.sort(this.parameters);
		Collections.sort(params);
		
		for(int i=0; i<params.size(); i++){
			if(!this.parameters.get(i).equals(params.get(i))){
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
	
	public boolean needDelegation(){
		for(ProgramReference ref: this.getRefererPointList()){
			UnitMemberWrapper member = ref.getReferer();
			if(!member.getUnitWrapper().equals(this.getUnitWrapper())){
				return true;
			}
		}
		
		return false;
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

	@Override
	public void setJavaElement(ASTNode node) {
		this.method = (MethodDeclaration) node;
		
	}
}
