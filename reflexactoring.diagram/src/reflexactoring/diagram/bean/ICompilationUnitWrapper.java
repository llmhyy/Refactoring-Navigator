/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import reflexactoring.diagram.action.semantic.TokenExtractor;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class ICompilationUnitWrapper extends Document implements LowLevelSuggestionObject, LowLevelGraphNode{
	private ICompilationUnit compilationUnit;
	private ModuleWrapper mappingModule;
	private CompilationUnit javaUnit;
	
	private ICompilationUnitWrapper superClass;
	private ArrayList<ICompilationUnitWrapper> superInterfaceList = new ArrayList<>();
	
	private HashMap<ICompilationUnitWrapper, Integer> calleeCompilationUnitList = new HashMap<>();
	private HashMap<ICompilationUnitWrapper, Integer> callerCompilationUnitList = new HashMap<>();
	
	private HashMap<ICompilationUnitWrapper, ArrayList<ASTNode>> referingDetails
		= new HashMap<>();
	
	private ArrayList<UnitMemberWrapper> members = new ArrayList<>();
	
	/**
	 * @param compilationUnit
	 */
	public ICompilationUnitWrapper(ICompilationUnit compilationUnit) {
		super();
		this.compilationUnit = compilationUnit;
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		
		parser.setSource(compilationUnit);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		setJavaUnit(cu);
		
		String content = new TokenExtractor(this).extractTokens(cu);
		content = content + generateTitle();
		
		this.setDescription(content);
		this.extractTermFrequency(content);
	}
	
	public boolean isInterface(){
		TypeDeclaration typeDeclar = (TypeDeclaration) this.javaUnit.types().get(0);
		return typeDeclar.isInterface();
	}
	
	public String toString(){
		return this.compilationUnit.getElementName();
	}
	
	public void openInEditor(){
		IEditorPart javaEditor;
		try {
			javaEditor = JavaUI.openInEditor(this.compilationUnit);
			JavaUI.revealInEditor(javaEditor,
					(IJavaElement) this.compilationUnit);
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasSuperCompilationUnit(ICompilationUnitWrapper unit){
		if(superClass != null && superClass.equals(unit)){
			return true;
		}
		else{
			for(ICompilationUnitWrapper interf: superInterfaceList){
				if(interf.equals(unit)){
					return true;
				}
			}
		}
		
		return false;
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
		
		if(obj == null){
			return false;
		}
		
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
	
	@Override
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

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.SuggestionObject#getNameWithTag()
	 */
	@Override
	public String getNameWithTag() {
		return "<a href=\"Type\" value=\"test\">" + getName() + "</a>";
	}

	/**
	 * @return the members
	 */
	public ArrayList<UnitMemberWrapper> getMembers() {
		return members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(ArrayList<UnitMemberWrapper> members) {
		this.members = members;
	}

	/**
	 * @return the referingDetails
	 */
	public HashMap<ICompilationUnitWrapper, ArrayList<ASTNode>> getReferingDetails() {
		return referingDetails;
	}

	/**
	 * @param refereeCompilationUnit
	 * @param type
	 */
	public void putReferringDetail(
			ICompilationUnitWrapper refereeCompilationUnit, ASTNode node) {
		ArrayList<ASTNode> nodeList = this.referingDetails.get(refereeCompilationUnit);
		if(nodeList == null){
			nodeList = new ArrayList<>();
		}
		if(!nodeList.contains(node)){
			nodeList.add(node);			
		}
		this.referingDetails.put(refereeCompilationUnit, nodeList);
	}

	@Override
	protected String getDocName() {
		return getName();
	}


	/**
	 * @return the superClass
	 */
	public ICompilationUnitWrapper getSuperClass() {
		return superClass;
	}


	/**
	 * @param superClass the superClass to set
	 */
	public void setSuperClass(ICompilationUnitWrapper superClass) {
		this.superClass = superClass;
	}


	/**
	 * @return the superInterfaceList
	 */
	public ArrayList<ICompilationUnitWrapper> getSuperInterfaceList() {
		return superInterfaceList;
	}


	/**
	 * @param superInterfaceList the superInterfaceList to set
	 */
	public void setSuperInterfaceList(ArrayList<ICompilationUnitWrapper> superInterfaceList) {
		this.superInterfaceList = superInterfaceList;
	}
	
	public void addSuperInterface(ICompilationUnitWrapper superInterface){
		this.superInterfaceList.add(superInterface);
	}
}
