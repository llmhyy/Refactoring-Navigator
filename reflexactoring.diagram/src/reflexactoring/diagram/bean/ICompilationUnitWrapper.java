/**
 * 
 */
package reflexactoring.diagram.bean;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;

import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class ICompilationUnitWrapper {
	private ICompilationUnit compilationUnit;
	private ModuleWrapper mappingModule;
	private String description;
	
	/**
	 * @param compilationUnit
	 */
	public ICompilationUnitWrapper(ICompilationUnit compilationUnit) {
		super();
		this.compilationUnit = compilationUnit;
		
		CompilationUnit unit = AST.parseCompilationUnit(compilationUnit, false);
		
		final StringBuffer buffer = new StringBuffer();
		unit.accept(new ASTVisitor() {
			public boolean visit(SimpleName name){				
				String identifier = name.getIdentifier();
				String[] tokens = ReflexactoringUtil.mixedSplitting(identifier);
				
				for(String token: tokens){
					if(token.length() > 1){
						token = token.toLowerCase();
						buffer.append(token + ";");											
					}
				}
				
				return false;
			}
		});
		
		String content = buffer.toString();
		content = content.substring(0, content.length()-1);
		this.description = content;
	}
	
	
	
	/**
	 * @return the compilationUnit
	 */
	public ICompilationUnit getCompilationUnit() {
		return compilationUnit;
	}
	/**
	 * @param compilationUnit the compilationUnit to set
	 */
	public void setCompilationUnit(ICompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}
	/**
	 * @return the mappingModule
	 */
	public ModuleWrapper getMappingModule() {
		return mappingModule;
	}
	/**
	 * @param mappingModule the mappingModule to set
	 */
	public void setMappingModule(ModuleWrapper mappingModule) {
		this.mappingModule = mappingModule;
	}
	
	
}
