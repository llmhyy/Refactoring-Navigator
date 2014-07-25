/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.List;

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
	
	public FieldWrapper(FieldDeclaration field, ICompilationUnitWrapper unitWrapper){
		super(unitWrapper);
		this.setField(field);
		
		this.name = ((VariableDeclaration)field.fragments().get(0)).getName().getIdentifier();
		
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

	

	
}
