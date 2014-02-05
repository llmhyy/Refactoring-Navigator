/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

/**
 * @author linyun
 *
 */
public class FieldWrapper extends UnitMemberWrapper{
	
	private FieldDeclaration field;
	
	public FieldWrapper(FieldDeclaration field, ICompilationUnitWrapper unitWrapper){
		this.setField(field);
		this.setUnitWrapper(unitWrapper);
	}
	
	@Override
	public String getName() {
		return ((VariableDeclaration)field.fragments().get(0)).getName().getIdentifier();
	}
	
	public String toString(){
		return getName();
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
}
