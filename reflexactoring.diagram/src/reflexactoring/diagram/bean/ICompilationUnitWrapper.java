/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.HashMap;

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
	
	private HashMap<ICompilationUnitWrapper, Integer> calleeCompilationUnitList = new HashMap<>();
	private HashMap<ICompilationUnitWrapper, Integer> callerCompilationUnitList = new HashMap<>();
	
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
	
	public String getUniqueName(){
		return this.compilationUnit.getElementName();
	}
	
	public int hashCode(){
		return getUniqueName().hashCode();
	}
	
	public boolean equals(Object obj){
		if(obj instanceof ICompilationUnitWrapper){
			ICompilationUnitWrapper unit = (ICompilationUnitWrapper)obj;
			return getUniqueName().equals(unit.getUniqueName());
		}
		
		return false;
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

	/**
	 * @return the calleeCompilationUnitList
	 */
	public HashMap<ICompilationUnitWrapper, Integer> getCalleeCompilationUnitList() {
		return calleeCompilationUnitList;
	}

	/**
	 * @param calleeCompilationUnitList the calleeCompilationUnitList to set
	 */
	public void setCalleeCompilationUnitList(
			HashMap<ICompilationUnitWrapper, Integer> calleeCompilationUnitList) {
		this.calleeCompilationUnitList = calleeCompilationUnitList;
	}

	/**
	 * @return the callerCompilationUnitList
	 */
	public HashMap<ICompilationUnitWrapper, Integer> getCallerCompilationUnitList() {
		return callerCompilationUnitList;
	}

	/**
	 * @param callerCompilationUnitList the callerCompilationUnitList to set
	 */
	public void setCallerCompilationUnitList(
			HashMap<ICompilationUnitWrapper, Integer> callerCompilationUnitList) {
		this.callerCompilationUnitList = callerCompilationUnitList;
	}
	
	public void addCallerCompilationUnit(ICompilationUnitWrapper unit){
		int count = 0;
		if(this.callerCompilationUnitList.containsKey(unit)){
			count = this.callerCompilationUnitList.get(unit);
		}
		
		this.callerCompilationUnitList.put(unit, ++count);
	}
	
	public void addCalleeCompilationUnit(ICompilationUnitWrapper unit){
		int count = 0;
		if(this.calleeCompilationUnitList.containsKey(unit)){
			count = this.calleeCompilationUnitList.get(unit);
		}
		
		this.calleeCompilationUnitList.put(unit, ++count);
	}
}
