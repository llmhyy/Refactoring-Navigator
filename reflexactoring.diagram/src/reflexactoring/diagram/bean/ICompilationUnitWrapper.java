/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
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
public class ICompilationUnitWrapper implements LowLevelSuggestionObject, GraphNode{
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
		this(compilationUnit, true);
	}
	
	public ICompilationUnitWrapper(ICompilationUnit compilationUnit, boolean extractDesc){
		super();
		this.compilationUnit = compilationUnit;
		if(extractDesc){
			
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setResolveBindings(true);
			//parser.setSource(doc.get().toCharArray());
			parser.setSource(compilationUnit);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			
			setJavaUnit(cu);
			String content = new TokenExtractor().extractTokens(compilationUnit);
			
			content = content + generateTitle();
			
			this.setDescription(content);
		}
	}
	
	/**
	 * I need to add the weight of the java class name.
	 * @return
	 */
	private String generateTitle(){
		String title = getSimpleName();
		String[] titleList = ReflexactoringUtil.mixedSplitting(title);
		
		StringBuffer buffer = new StringBuffer();
		for(String titleKeyword: titleList){
			for(int i=0; i<10; i++){
				buffer.append(titleKeyword + " ");				
			}
		}
		
		return buffer.toString();
	}
	
	
	public String toString(){
		return this.compilationUnit.getElementName();
	}
	
	public String getSimpleName(){
		String uniqueName = this.compilationUnit.getElementName();
		uniqueName = uniqueName.substring(0, uniqueName.indexOf(".java"));
		return uniqueName;
	}
	
	public String getPackageName(){
		try {
			return this.compilationUnit.getPackageDeclarations()[0].getElementName();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public String getFullQualifiedName(){
		return getPackageName() + "." + getSimpleName();
	}
	
	public int hashCode(){
		return getSimpleName().hashCode();
	}
	
	public boolean equals(Object obj){
		if(obj instanceof ICompilationUnitWrapper){
			ICompilationUnitWrapper type = (ICompilationUnitWrapper)obj;
			return type.getFullQualifiedName().equals(this.getFullQualifiedName());
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

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.SuggestionObject#getName()
	 */
	@Override
	public String getName() {
		return this.getSimpleName();
	}
	
	/** 
	 * (non-Javadoc)
	 * @see reflexactoring.diagram.bean.GraphNode#getCallerList()
	 */
	@Override
	public List<? extends GraphNode> getCallerList() {
		return convertToList(callerCompilationUnitList);
	}

	/** (non-Javadoc)
	 * @see reflexactoring.diagram.bean.GraphNode#getCalleeList()
	 */
	@Override
	public List<? extends GraphNode> getCalleeList() {
		return convertToList(calleeCompilationUnitList);
	}
	
	private ArrayList<ICompilationUnitWrapper> convertToList(HashMap<ICompilationUnitWrapper, Integer> map){
		ArrayList<ICompilationUnitWrapper> list = new ArrayList<>();
		for(ICompilationUnitWrapper wrapper: map.keySet()){
			list.add(wrapper);
		}
		
		return list;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.SuggestionObject#getType()
	 */
	@Override
	public String getTypeName() {
		return "Java type";
	}
}
