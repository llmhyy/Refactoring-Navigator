/**
 * 
 */
package reflexactoring.diagram.bean;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import reflexactoring.diagram.action.semantic.TokenExtractor;

/**
 * @author linyun
 *
 */
public class FieldWrapper extends UnitMemberWrapper{
	
	private FieldDeclaration field;
	private String name;
	private String type;
	
	public FieldWrapper(String name, String type, ICompilationUnitWrapper unitWrapper){
		super(unitWrapper);
		this.type = type;
		this.name = name;
	}
	
	public FieldWrapper(FieldDeclaration field, ICompilationUnitWrapper unitWrapper){
		super(unitWrapper);
		this.setField(field);
		
		this.name = ((VariableDeclaration)field.fragments().get(0)).getName().getIdentifier();
		this.type = field.getType().toString();
		
		String content = new TokenExtractor(unitWrapper).extractTokens(field);
		content = content + " " + generateTitle();
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
			
			boolean isSameType = true;
			FieldDeclaration thatField = fieldWrapper.getField();
			if(thatField != null && this.field != null){
				String thatTypeName = thatField.getType().toString();
				String thisTypeName = this.field.getType().toString();
				isSameType = thatTypeName.equals(thisTypeName);
			}
			
			return isSameType && fieldWrapper.getName().equals(this.getName());
		}
		return false;
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

	

	
}
