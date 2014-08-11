/**
 * 
 */
package reflexactoring.diagram.action.bak;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
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
import org.eclipse.jdt.core.dom.TypeDeclaration;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ReferencingDetail;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class ClassStructureBuilder_bak {
	/**
	 * Identifying the reference relations in classes/interfaces in scope.
	 * @param compilationUnitList
	 * 
	 */
	public void buildStructuralDependency(final ArrayList<ICompilationUnitWrapper> compilationUnitList,
			IProgressMonitor monitor, int scale){
		
		buildExtendingAndImplementingRelations(compilationUnitList);
		
		//System.currentTimeMillis();
		
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
							buildCallRelation(refererCompilationUnit, type, refereeCompilationUnit);
						}
					}
					
					return true;
				}

				public boolean visit(ImportDeclaration declaration){
					String fullQulifiedName = declaration.getName().getFullyQualifiedName();
					for(ICompilationUnitWrapper refereeCompilationUnit: compilationUnitList){
						if(fullQulifiedName.equals(refereeCompilationUnit.getFullQualifiedName()) && !refererCompilationUnit.equals(refereeCompilationUnit)){
							buildCallRelation(refererCompilationUnit, declaration, refereeCompilationUnit);
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
								buildCallRelation(refererCompilationUnit, type, refereeCompilationUnit);
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
									buildCallRelation(refererCompilationUnit, invocation, refereeCompilationUnit);
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

				/**
				 * @param refererCompilationUnit
				 * @param node
				 * @param refereeCompilationUnit
				 */
				private void buildCallRelation(
						final ICompilationUnitWrapper refererCompilationUnit,
						ASTNode node,
						ICompilationUnitWrapper refereeCompilationUnit) {
					
					/*if(refererCompilationUnit.toString().contains("Data") && refereeCompilationUnit.toString().contains("AbstractData")){
						System.currentTimeMillis();
					}*/
					
					if(!refererCompilationUnit.hasSuperCompilationUnit(refereeCompilationUnit)){
						refererCompilationUnit.addCallee(refereeCompilationUnit, ReferencingDetail.ALL);
						refereeCompilationUnit.addCaller(refererCompilationUnit, ReferencingDetail.ALL);
						
						refererCompilationUnit.putReferringDetail(refereeCompilationUnit, node);
					}
				}
			});
		}
		
		Settings.scope.setScopeCompilationUnitList(compilationUnitList);
		//return compilationUnitList;
	}
	
	private void buildExtendingAndImplementingRelations(ArrayList<ICompilationUnitWrapper> compilationUnitList){
		for(ICompilationUnitWrapper refererUnit: compilationUnitList){
			for(ICompilationUnitWrapper refereeUnit: compilationUnitList){
				if(!refererUnit.equals(refereeUnit)){
					System.currentTimeMillis();
					
					TypeDeclaration refererType = (TypeDeclaration) refererUnit.getJavaUnit().types().get(0);
					TypeDeclaration refereeType = (TypeDeclaration) refereeUnit.getJavaUnit().types().get(0);
					
					ITypeBinding superType = refererType.resolveBinding().getSuperclass();
					ITypeBinding[] interfaceList = refererType.resolveBinding().getInterfaces();
					
					ITypeBinding referee = refereeType.resolveBinding();
					
					if(superType != null && superType.getJavaElement().equals(referee.getJavaElement())){
						refererUnit.setSuperClass(refereeUnit);
						refererUnit.addParent(refereeUnit);
						refereeUnit.addChild(refererUnit);
					}
					else{
						for(int i=0; i<interfaceList.length; i++){
							if(referee.toString().contains("Data") && refererType.toString().contains("WeatherData")){
								System.currentTimeMillis();
							}
							if(referee.getJavaElement().equals(interfaceList[i].getJavaElement())){
								refererUnit.addSuperInterface(refereeUnit);
								refererUnit.addParent(refereeUnit);
								refereeUnit.addChild(refererUnit);
							}
						}
					}
				}
			}
		}
	}
}
