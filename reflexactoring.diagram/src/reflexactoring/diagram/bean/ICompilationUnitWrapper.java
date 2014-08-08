/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import reflexactoring.diagram.bean.efficiency.UnitPair;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class ICompilationUnitWrapper extends Document implements LowLevelSuggestionObject, LowLevelGraphNode, SimilarityComputable{
	private ICompilationUnit compilationUnit; 
	private ModuleWrapper mappingModule;
	private CompilationUnit javaUnit;
	
	private boolean isInterface;
	private String simpleName;
	private String packageName;
	
	private ICompilationUnitWrapper superClass;
	private ArrayList<ICompilationUnitWrapper> superInterfaceList = new ArrayList<>();
	
	private ArrayList<ICompilationUnitWrapper> parentList = new ArrayList<>();
	private ArrayList<ICompilationUnitWrapper> childList = new ArrayList<>();
	
	private HashMap<ICompilationUnitWrapper, Integer> calleeCompilationUnitList = new HashMap<>();
	private HashMap<ICompilationUnitWrapper, Integer> callerCompilationUnitList = new HashMap<>();
	
	private HashMap<ICompilationUnitWrapper, ArrayList<ASTNode>> referingDetails
		= new HashMap<>();
	
	private ArrayList<UnitMemberWrapper> members = new ArrayList<>();
	private ArrayList<ICompilationUnitWrapper> ancestors;
	
	/**
	 * stands for the unit members referring this unit member
	 */
	private ArrayList<ProgramReference> refererPointList = new ArrayList<>();
	/**
	 * stands for the unit members referred by this unit or unit member
	 */
	private ArrayList<ProgramReference> refereePointList = new ArrayList<>();
	
	/**
	 * @param mappingModule
	 * @param isInterface
	 * @param simpleName
	 * @param packageName
	 */
	public ICompilationUnitWrapper(ModuleWrapper mappingModule, boolean isInterface, String simpleName, 
			String packageName, HashMap<String, Integer> termFrequency, String description) {
		super();
		this.mappingModule = mappingModule;
		this.isInterface = isInterface;
		this.simpleName = simpleName;
		this.packageName = packageName;
		this.termFrequency = termFrequency;
		this.description = description;
	}

	/**
	 * @param compilationUnit
	 */
	public ICompilationUnitWrapper(ICompilationUnit compilationUnit) {
		super();
		
		/**
		 * retrieve JDT related properties, like AST node and Java model element.
		 */
		this.compilationUnit = compilationUnit;
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_5, options);
		parser.setCompilerOptions(options);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setSource(compilationUnit);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		setJavaUnit(cu);
		
		/**
		 * generate necessary data, we need this step because JDT API may get
		 * obsolete during some steps (e.g., we may create a new compilation unit wrapper)
		 * when searching/simulating for a proper refactoring solution)
		 */
		TypeDeclaration typeDeclar = (TypeDeclaration) this.javaUnit.types().get(0);
		this.isInterface = typeDeclar.isInterface();
		
		this.packageName = "";
		try {
			this.packageName = this.compilationUnit.getPackageDeclarations()[0].getElementName();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		String uniqueName = this.compilationUnit.getElementName();
		uniqueName = uniqueName.substring(0, uniqueName.indexOf(".java"));
		this.simpleName = uniqueName;
		
		/**
		 * Extract some keywords used for lexical similarity
		 */
		//String content = new TokenExtractor(this).extractTokens(cu);
		//content = content + generateTitle();
		String content = generateTitle().toLowerCase();
		
		this.setDescription(content);
		this.extractTermFrequency(content);
	}
	
	public boolean isInterface(){
		return isInterface;
	}
	
	public void setInterface(boolean isInterface){
		this.isInterface = isInterface;
	}
	
	public String toString(){
		return this.getFullQualifiedName();
	}
	
	public ArrayList<ICompilationUnitWrapper> getAllAncestors(){
		if(ancestors == null){
			ArrayList<ICompilationUnitWrapper> ancestors = new ArrayList<>();
			
			if(this.superClass != null){
				ancestors.add(this.superClass);
			}
			for(ICompilationUnitWrapper interf: this.superInterfaceList){
				ancestors.add(interf);
			}
			
			ArrayList<ICompilationUnitWrapper> ancestors0 = (ArrayList<ICompilationUnitWrapper>) ancestors.clone();
			
			for(ICompilationUnitWrapper unit: ancestors0){
				ArrayList<ICompilationUnitWrapper> ancesList = unit.getAllAncestors();
				ancestors.addAll(ancesList);
			}
			
			this.ancestors = ancestors;
		}
		
		return ancestors;
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
	
	public boolean hasCalleeCompilationUnit(ICompilationUnitWrapper unit){
		for(ICompilationUnitWrapper calleeUnit: this.calleeCompilationUnitList.keySet()){
			if(calleeUnit.equals(unit)){
				return true;
			}
		}
		
		return false;
	}
	
	public String getSimpleName(){
		return this.simpleName;
	}
	
	public void setSimpleName(String simpleName){
		this.simpleName = simpleName;
	}
	
	public String getPackageName(){
		return this.packageName;
	}
	
	public void setPackageName(String packageName){
		this.packageName = packageName;
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
	
	public void addParent(ICompilationUnitWrapper unit){
		if(hasSuperCompilationUnit(unit) && !this.parentList.contains(unit)){
			this.parentList.add(unit);
		}
	}
	
	public void addChild(ICompilationUnitWrapper unit){
		if(!this.childList.contains(unit)){
			this.childList.add(unit);
		}
	}
	
	public void addCaller(ICompilationUnitWrapper unit){
		if(!this.callerCompilationUnitList.keySet().contains(unit)){
			this.callerCompilationUnitList.put(unit, 1);
		}
		else{
			int value = this.callerCompilationUnitList.get(unit);
			value++;
			this.callerCompilationUnitList.put(unit, value);
		}
	}
	
	public void addCallee(ICompilationUnitWrapper unit){
		if(!this.calleeCompilationUnitList.keySet().contains(unit)){
			this.calleeCompilationUnitList.put(unit, 1);
		}
		else{
			int value = this.calleeCompilationUnitList.get(unit);
			value++;
			this.calleeCompilationUnitList.put(unit, value);
		}
	}
	
	public void addMember(UnitMemberWrapper member){
		if(!this.members.contains(member)){
			this.members.add(member);
		}
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
	public HashMap<GraphNode, Integer> getCallerList() {
		//return convertToList(callerCompilationUnitList);
		HashMap<GraphNode, Integer> map = new HashMap<>();
		for(ICompilationUnitWrapper unit: this.callerCompilationUnitList.keySet()){
			map.put(unit, this.callerCompilationUnitList.get(unit));
		}
		return map;
	}

	/** (non-Javadoc)
	 * @see reflexactoring.diagram.bean.GraphNode#getCalleeList()
	 */
	@Override
	public HashMap<GraphNode, Integer> getCalleeList() {
		//return convertToList(calleeCompilationUnitList);
		HashMap<GraphNode, Integer> map = new HashMap<>();
		for(ICompilationUnitWrapper unit: this.calleeCompilationUnitList.keySet()){
			map.put(unit, this.calleeCompilationUnitList.get(unit));
		}
		return map;
	}
	

	/** (non-Javadoc)
	 * @see reflexactoring.diagram.bean.GraphNode#getParentList()
	 */
	@Override
	public List<? extends GraphNode> getParentList() {
		return this.parentList;
	}

	/** (non-Javadoc)
	 * @see reflexactoring.diagram.bean.GraphNode#getChildList()
	 */
	@Override
	public List<? extends GraphNode> getChildList() {
		return this.childList;
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
	public double computeSimilarityWith(Object obj){
		if(obj instanceof ICompilationUnitWrapper){			
			ICompilationUnitWrapper thatUnit = (ICompilationUnitWrapper)obj;
			
			UnitPair pair = new UnitPair(this, thatUnit);
			Double v = Settings.unitPairSimilarityMap.get(pair);
			if(v != null){
				return v;
			}
			
			ArrayList<ICompilationUnitWrapper> thisAncestors = getAllAncestors();
			ArrayList<ICompilationUnitWrapper> thatAncestors = thatUnit.getAllAncestors();
			
			double count = 0;
			for(ICompilationUnitWrapper thisAncestor: thisAncestors){
				for(ICompilationUnitWrapper thatAncestor: thatAncestors){
					if(thisAncestor.equals(thatAncestor)){
						count++;
					}
				}
			}
			
			double totalSize = thisAncestors.size() + thatAncestors.size();
			/**
			 * Jaccard coefficient
			 */
			double ancestorSim = (totalSize == 0) ? 1 : count/(totalSize-count);
			
			double nameSim = ReflexactoringUtil.compareStringSimilarity(getName(), thatUnit.getName());
			
			ArrayList<UnitMemberWrapper> members1 = getMembers();
			ArrayList<UnitMemberWrapper> members2 = thatUnit.getMembers();
			
			//long t1 = System.currentTimeMillis();
			double memberSim = ReflexactoringUtil.computeSetSimilarity(members1, members2);
			//long t2 = System.currentTimeMillis();
			//System.out.println("Time: " + (t2-t1));
			
			double value = (ancestorSim + nameSim + memberSim)/3;
			
			Settings.unitPairSimilarityMap.put(pair, value);
			
			return value;
		}
		
		return 0;
	}
	
	public boolean isLegalTargetClassToMoveMethodIn(MethodWrapper method){
		return !isInterface() &&
				!this.getMembers().contains(method) &&
				!alreadyHasAMethodWithSameSignature(method) &&
				!alreadyHasAMethodInSuperTypeWithSameSingature(method);
				
	}
	
	public boolean alreadyHasAMethodInSuperTypeWithSameSingature(MethodWrapper method){
		for(GraphNode node: this.getParentList()){
			ICompilationUnitWrapper superUnit = (ICompilationUnitWrapper)node;
			if(superUnit.alreadyHasAMethodWithSameSignature(method)){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean alreadyHasAMethodWithSameSignature(MethodWrapper method){
		for(UnitMemberWrapper member: this.getMembers()){
			if(member instanceof MethodWrapper){
				MethodWrapper m = (MethodWrapper)member;
				if(m.hasSameSignatureWith(method)){
					return true;
				}
			}
		}
		
		return false;
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

	@Override
	public void addProgramReferer(ProgramReference reference){
		this.refererPointList.add(reference);
	}
	
	@Override
	public void addProgramReferee(ProgramReference reference){
		this.refereePointList.add(reference);
	}
}
