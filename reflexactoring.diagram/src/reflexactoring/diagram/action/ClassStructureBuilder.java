/**
 * 
 */
package reflexactoring.diagram.action;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class ClassStructureBuilder {
	/**
	 * Identifying the reference relations in classes/interfaces in scope.
	 * @param compilationUnitList
	 * 
	 */
	public void buildStructuralDependency(final ArrayList<ICompilationUnitWrapper> compilationUnitList,
			IProgressMonitor monitor, int scale){
		
		for(final ICompilationUnitWrapper refererCompilationUnit: compilationUnitList){
			monitor.worked(scale);
			
			final CompilationUnit compilationUnit = refererCompilationUnit.getJavaUnit();
			compilationUnit.accept(new ASTVisitor() {
				/**
				 * Currently, I just simplify the problem by considering only SimpleType. It could be extended to
				 * other subclasses of Type.
				 */
				public boolean visit(SimpleType type){
					String typeName = type.getName().getFullyQualifiedName();
					for(ICompilationUnitWrapper refereeCompilationUnit: compilationUnitList){
						if(typeName.equals(refereeCompilationUnit.getSimpleName()) && !refererCompilationUnit.equals(refereeCompilationUnit)){
							refererCompilationUnit.addCalleeCompilationUnit(refereeCompilationUnit);
							refereeCompilationUnit.addCallerCompilationUnit(refererCompilationUnit);
							
							refererCompilationUnit.putReferringDetail(refereeCompilationUnit, type);
						}
					}
					
					return true;
				}
				
				public boolean visit(ImportDeclaration declaration){
					String fullQulifiedName = declaration.getName().getFullyQualifiedName();
					for(ICompilationUnitWrapper refereeCompilationUnit: compilationUnitList){
						if(fullQulifiedName.equals(refereeCompilationUnit.getFullQualifiedName()) && !refererCompilationUnit.equals(refereeCompilationUnit)){
							refererCompilationUnit.addCalleeCompilationUnit(refereeCompilationUnit);
							refereeCompilationUnit.addCallerCompilationUnit(refererCompilationUnit);
							
							refererCompilationUnit.putReferringDetail(refereeCompilationUnit, declaration);
						}
					}
					
					return true;
				}
				
				public boolean visit(ClassInstanceCreation creation){
					Type type = creation.getType();
					if(type instanceof SimpleType){
						String typeName = ((SimpleType)type).getName().getFullyQualifiedName();
						for(ICompilationUnitWrapper refereeCompilationUnit: compilationUnitList){
							if(typeName.equals(refereeCompilationUnit.getSimpleName()) && !refererCompilationUnit.equals(refereeCompilationUnit)){
								refererCompilationUnit.addCalleeCompilationUnit(refereeCompilationUnit);
								refereeCompilationUnit.addCallerCompilationUnit(refererCompilationUnit);
								
								refererCompilationUnit.putReferringDetail(refereeCompilationUnit, type);
							}
						}
					}
					
					return true;
				}
				
				public boolean visit(MethodInvocation invocation){
					Expression expression = invocation.getExpression();
					if(null != expression && expression instanceof SimpleName){
						SimpleName name = (SimpleName)expression;
						IBinding binding = name.resolveBinding();
						if(binding instanceof ITypeBinding){
							ITypeBinding typeBinding = (ITypeBinding)binding;
							String typeName = typeBinding.getName();
							for(ICompilationUnitWrapper refereeCompilationUnit: compilationUnitList){
								if(typeName.equals(refereeCompilationUnit.getSimpleName()) && !refererCompilationUnit.equals(refereeCompilationUnit)){
									refererCompilationUnit.addCalleeCompilationUnit(refereeCompilationUnit);
									refereeCompilationUnit.addCallerCompilationUnit(refererCompilationUnit);
									
									refererCompilationUnit.putReferringDetail(refereeCompilationUnit, invocation);
									
									System.currentTimeMillis();
								}
							}
						}
					}
					/*String typeName = ((SimpleType)type).getName().getFullyQualifiedName();
					for(ICompilationUnitWrapper refereeCompilationUnit: compilationUnitList){
						if(typeName.equals(refereeCompilationUnit.getSimpleName()) && !refererCompilationUnit.equals(refereeCompilationUnit)){
							refererCompilationUnit.addCalleeCompilationUnit(refereeCompilationUnit);
							refereeCompilationUnit.addCallerCompilationUnit(refererCompilationUnit);
							
							refererCompilationUnit.putReferringDetail(refereeCompilationUnit, type);
						}
					}*/
					
					return true;
				}
			});
		}
		
		Settings.scope.setScopeCompilationUnitList(compilationUnitList);
		//return compilationUnitList;
	}
}
