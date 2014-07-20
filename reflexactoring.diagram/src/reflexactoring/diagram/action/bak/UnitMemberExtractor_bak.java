/**
 * 
 */
package reflexactoring.diagram.action.bak;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.corext.callhierarchy.CallerMethodWrapper;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.bean.FieldWrapper;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapperList;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class UnitMemberExtractor_bak {
	
	public void extract(ArrayList<ICompilationUnitWrapper> units, IProgressMonitor monitor, int scale){
		/**
		 * initialize the members
		 */
		final ArrayList<UnitMemberWrapper> memberList = new ArrayList<>();
		
		for(final ICompilationUnitWrapper unitWrapper: units){
			monitor.worked(scale);
			
			
			CompilationUnit unit = unitWrapper.getJavaUnit();
			unit.accept(new ASTVisitor() {
				public boolean visit(FieldDeclaration fd){
					FieldWrapper fieldWrapper = new FieldWrapper(fd, unitWrapper);
					unitWrapper.getMembers().add(fieldWrapper);
					memberList.add(fieldWrapper);
					
					return false;
				}
				
				public boolean visit(MethodDeclaration md){
					MethodWrapper methodWrapper = new MethodWrapper(md, unitWrapper);
					unitWrapper.getMembers().add(methodWrapper);
					memberList.add(methodWrapper);
					
					return false;
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
		UnitMemberWrapperList members = constructRelations(memberList, monitor, scale, units.size());
		Settings.scope.setScopeMemberList(members);
	}

	/**
	 * @param memberList
	 * @return
	 */
	private UnitMemberWrapperList constructRelations(ArrayList<UnitMemberWrapper> memberList, IProgressMonitor monitor, int scale, int typeNum) {
		final UnitMemberWrapperList members = new UnitMemberWrapperList();
		if(typeNum == 0){
			return members;
		}
		
		for(UnitMemberWrapper wrapper: memberList){
			members.add(wrapper);
		}
		
		int unitScale = 2*scale*typeNum/members.size();
		
		for(final UnitMemberWrapper member: members){
			monitor.worked(unitScale);
			if(monitor.isCanceled()){
				break;
			}
			
			/*if(member.getName().contains("createOperationPanel")){
				System.currentTimeMillis();
			}*/
			
			if(member instanceof MethodWrapper){
				MethodWrapper methodWrapper = (MethodWrapper)member;
				
				methodWrapper.getMethod().accept(new ASTVisitor() {
					/*public boolean visit(FieldAccess access){
						
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
					}*/
					public boolean visit(SimpleName name){
						IBinding binding = name.resolveBinding();
						IJavaElement element = binding.getJavaElement();
						if(element != null && (binding instanceof IMethodBinding || binding instanceof IVariableBinding)){
							for(UnitMemberWrapper calleeMember: members){
								if(element.equals(calleeMember.getJavaMember()) && !member.equals(calleeMember)){
									//member.addCallee(calleeMember);
									//calleeMember.addCaller(member);
								}
							}							
						}
						
						return false;
					}
					
					public boolean visit(ClassInstanceCreation creation){
						
						IMethodBinding methodBinding = creation.resolveConstructorBinding();
						//IJavaElement element = methodBinding.getJavaElement();
						methodBinding.getDeclaringClass();
						String key = methodBinding.getKey();
						
						for(UnitMemberWrapper calleeMember: members){
							if(calleeMember instanceof MethodWrapper){
								MethodWrapper methodWrapper = (MethodWrapper)calleeMember;
								String methodKey = methodWrapper.getMethod().resolveBinding().getKey();
								
								if(key.equals(methodKey)){
									//member.addCallee(calleeMember);
									//calleeMember.addCaller(member);
								}
							}
						}
						
						return true;
					}
				});
				
			}
			else if(member instanceof FieldWrapper){
				((FieldWrapper) member).getField().accept(new ASTVisitor() {
					public boolean visit(SimpleName name){
						IBinding binding = name.resolveBinding();
						IJavaElement element = binding.getJavaElement();
						if(element != null && (binding instanceof IMethodBinding || binding instanceof IVariableBinding)){
							for(UnitMemberWrapper calleeMember: members){
								if(element.equals(calleeMember.getJavaMember()) && !member.equals(calleeMember)){
									//member.addCallee(calleeMember);
									//calleeMember.addCaller(member);
								}
							}							
						}
						
						return false;
					}
					public boolean visit(QualifiedName name){
						/*IBinding binding = name.resolveBinding();
						IJavaElement element = binding.getJavaElement();
						if(element != null && (binding instanceof IMethodBinding || binding instanceof IVariableBinding)){
							for(UnitMemberWrapper calleeMember: members){
								if(element.equals(calleeMember.getJavaMember()) && !member.equals(calleeMember)){
									member.addCallee(calleeMember);
									calleeMember.addCaller(member);
								}
							}							
						}*/
						
						return false;
					}
					public boolean visit(ClassInstanceCreation creation){
						
						IMethodBinding methodBinding = creation.resolveConstructorBinding();
						//IJavaElement element = methodBinding.getJavaElement();
						methodBinding.getDeclaringClass();
						String key = methodBinding.getKey();
						
						//System.currentTimeMillis();
						
						for(UnitMemberWrapper calleeMember: members){
							if(calleeMember instanceof MethodWrapper){
								MethodWrapper methodWrapper = (MethodWrapper)calleeMember;
								String methodKey = methodWrapper.getMethod().resolveBinding().getKey();
								
								if(key.equals(methodKey)){
									//member.addCallee(calleeMember);
									//calleeMember.addCaller(member);
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
