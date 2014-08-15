/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.HashMap;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import reflexactoring.diagram.action.semantic.TokenExtractor;
import reflexactoring.diagram.util.DefaultComparator;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class FieldWrapper extends UnitMemberWrapper{
	
	private FieldDeclaration field;
	private String name;
	private String type;
	
	public FieldWrapper(String name, String type, ICompilationUnitWrapper unitWrapper, 
			HashMap<String, Integer> termFrequency, String description, FieldDeclaration field){
		super(unitWrapper);
		this.type = type;
		this.name = name;
		this.termFrequency = termFrequency;
		this.description = description;
		this.field = field;
	}
	
	public FieldWrapper(FieldDeclaration field, ICompilationUnitWrapper unitWrapper){
		super(unitWrapper);
		this.setField(field);
		
		this.name = ((VariableDeclaration)field.fragments().get(0)).getName().getIdentifier();
		this.type = field.getType().toString();
		
		String content = new TokenExtractor(unitWrapper).extractTokens(field);
		content = content + " " + generateTitle().toLowerCase();
		this.extractTermFrequency(content);
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof FieldWrapper){
			FieldWrapper fieldWrapper = (FieldWrapper)obj;
			return fieldWrapper.getName().equals(this.getName())
					&& fieldWrapper.getUnitWrapper().equals(this.getUnitWrapper());
		}
		
		return false;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public void setName(String name){
		this.name = name;
	}
	
	public String toString(){
		return this.unitWrapper.getSimpleName() + "." + getName();
	}
	

	/**
	 * @return the field
	 */
	public FieldDeclaration getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(FieldDeclaration field) {
		this.field = field;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.UnitMemberWrapper#getJavaMember()
	 */
	@Override
	public IMember getJavaMember() {
		VariableDeclaration node = (VariableDeclaration) this.field.fragments().get(0);
		return (IMember)node.resolveBinding().getJavaElement();
		
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.SuggestionObject#getType()
	 */
	@Override
	public String getTypeName() {
		return "field";
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.SuggestionObject#getNameWithTag()
	 */
	@Override
	public String getNameWithTag() {
		return "<a href=\"Field\">" + getName() + "</a>";
	}

	@Override
	public ASTNode getJavaElement() {
		return getField();
	}

	@Override
	protected String getDocName() {
		return getName();
	}

	@Override
	public boolean hasSameSignatureWith(UnitMemberWrapper member) {
		if(member instanceof FieldWrapper){
			FieldWrapper fieldWrapper = (FieldWrapper)member;
			
			boolean isSameType = isSameType(fieldWrapper);
			
			return isSameType && fieldWrapper.getName().equals(this.getName());
		}
		return false;
	}
	
	@Override
	public double computeSimilarityForBeingPulledUp(UnitMemberWrapper otherMember){
		if(otherMember instanceof FieldWrapper){
			FieldWrapper thatField = (FieldWrapper)otherMember;
			if(!isSameType(thatField)){
				return 0;
			}
			String[] words1 = ReflexactoringUtil.splitCamelString(this.getName());
			String[] words2 = ReflexactoringUtil.splitCamelString(thatField.getName());
			
			Object[] commonWords = ReflexactoringUtil.generateCommonNodeList(words1, words2, new DefaultComparator());
			double sim = 2d*commonWords.length/(words1.length+words2.length);

			return sim;
		}
		
		return 0;
	}
	
	@Override
	public double computeSimilarityWith(Object obj){
		if(obj instanceof FieldWrapper){
			FieldWrapper thatField = (FieldWrapper)obj;
			
			double typeSimilarity = ReflexactoringUtil.
					compareStringSimilarity(getType(), thatField.getType());
			double nameSimilarity = ReflexactoringUtil.
					compareStringSimilarity(getName(), thatField.getName());
			
			return (typeSimilarity + nameSimilarity )/2;
		}
		
		return 0;
	}
	
	private boolean isSameType(FieldWrapper thatFieldWrapper){
		boolean isSameType = true;
		
		FieldDeclaration thatField = thatFieldWrapper.getField();
		if(thatField != null && this.field != null){
			String thatTypeName = thatField.getType().toString();
			String thisTypeName = this.field.getType().toString();
			isSameType = thatTypeName.equals(thisTypeName);
		}
		
		return isSameType;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setJavaElement(ASTNode node) {
		this.field = (FieldDeclaration) node;		
	}

	

	
}
