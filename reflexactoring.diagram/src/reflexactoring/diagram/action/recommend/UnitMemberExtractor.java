/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;

import reflexactoring.diagram.bean.FieldWrapper;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapperList;

/**
 * @author linyun
 *
 */
public class UnitMemberExtractor {
	
	public UnitMemberWrapperList extract(ArrayList<ICompilationUnitWrapper> units){
		/**
		 * initialize the members
		 */
		final ArrayList<UnitMemberWrapper> memberList = new ArrayList<>();
		
		for(final ICompilationUnitWrapper unitWrapper: units){
			CompilationUnit unit = unitWrapper.getJavaUnit();
			
			unit.accept(new ASTVisitor() {
				public boolean visit(FieldDeclaration fd){
					FieldWrapper fieldWrapper = new FieldWrapper(fd, unitWrapper);
					memberList.add(fieldWrapper);
					
					return false;
				}
				
				public boolean visit(MethodDeclaration md){
					MethodWrapper methodWrapper = new MethodWrapper(md, unitWrapper);
					memberList.add(methodWrapper);
					
					return true;
				}
			});
			
			/*try {
				for(IType type: iunit.getTypes()){
					for(IField field: type.getFields()){
						FieldWrapper fieldWrapper = new FieldWrapper(field, unitWrapper);
						memberList.add(fieldWrapper);
					}
					
					for(IMethod method: type.getMethods()){
						MethodWrapper methodWrapper = new MethodWrapper(method, unitWrapper);
						memberList.add(methodWrapper);
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}*/
			
		}
		
		/**
		 * build their relationships
		 */
		UnitMemberWrapperList members = constructRelations(memberList);
		
		return members;
	}

	/**
	 * @param memberList
	 * @return
	 */
	private UnitMemberWrapperList constructRelations(ArrayList<UnitMemberWrapper> memberList) {
		final UnitMemberWrapperList members = new UnitMemberWrapperList();
		for(UnitMemberWrapper wrapper: memberList){
			members.add(wrapper);
		}
		
		for(final UnitMemberWrapper member: members){
			if(member instanceof MethodWrapper){
				MethodWrapper methodWrapper = (MethodWrapper)member;
				
				methodWrapper.getMethod().accept(new ASTVisitor() {
					public boolean visit(FieldAccess access){
						
						IVariableBinding fieldBinding = access.resolveFieldBinding();
						IJavaElement element = fieldBinding.getJavaElement();
						
						if(element != null){
							for(UnitMemberWrapper calleeMember: members){
								if(calleeMember instanceof FieldWrapper){
									FieldWrapper fieldWrapper = (FieldWrapper)calleeMember;
									if(element.equals(fieldWrapper.getJavaMember())){
										member.addCallee(calleeMember);
										calleeMember.addCaller(member);
									}
								}
							}
							
						}
						
						return true;
					}
					
					public boolean visit(MethodInvocation invocation){
						
						IMethodBinding methodBinding = invocation.resolveMethodBinding();
						IJavaElement element = methodBinding.getJavaElement();
						
						for(UnitMemberWrapper calleeMember: members){
							if(calleeMember instanceof MethodWrapper){
								MethodWrapper methodWrapper = (MethodWrapper)calleeMember;
								if(element.equals(methodWrapper.getJavaMember())){
									member.addCallee(calleeMember);
									calleeMember.addCaller(member);
								}
							}
						}
						
						return true;
					}
				});
				
			}
		}
		
		return members;
	}
}
