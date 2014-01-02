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

import reflexactoring.diagram.action.semantic.TokenExtractor;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class ICompilationUnitWrapper {
	private ICompilationUnit compilationUnit;
	private ModuleWrapper mappingModule;
	private CompilationUnit javaUnit;
	private String description;
	
	/**
	 * @param compilationUnit
	 */
	public ICompilationUnitWrapper(ICompilationUnit compilationUnit) {
		super();
		this.compilationUnit = compilationUnit;
		setJavaUnit(AST.parseCompilationUnit(compilationUnit, false));
		String content = new TokenExtractor().extractTokens(compilationUnit);
		this.setDescription(content);
	}
	
	public String toString(){
		return this.compilationUnit.getElementName();
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



	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}



	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}



	/**
	 * @return the javaUnit
	 */
	public CompilationUnit getJavaUnit() {
		return javaUnit;
	}



	/**
	 * @param javaUnit the javaUnit to set
	 */
	public void setJavaUnit(CompilationUnit javaUnit) {
		this.javaUnit = javaUnit;
	}
	
	
}
