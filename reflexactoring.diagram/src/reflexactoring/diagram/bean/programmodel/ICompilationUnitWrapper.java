/**
 * 
 */
package reflexactoring.diagram.bean.programmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import reflexactoring.diagram.bean.Document;
import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.LowLevelSuggestionObject;
import reflexactoring.diagram.bean.ModuleWrapper;
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
	private boolean isAbstract;
	private String simpleName;
	private String packageName;
	private String modifier;
	
	private ICompilationUnitWrapper superClass;
	private ArrayList<ICompilationUnitWrapper> superInterfaceList = new ArrayList<>();
	
	private ArrayList<ICompilationUnitWrapper> parentList = new ArrayList<>();
	private ArrayList<ICompilationUnitWrapper> childList = new ArrayList<>();
	
	private HashMap<ICompilationUnitWrapper, ReferencingDetail> calleeCompilationUnitList = new HashMap<>();
	private HashMap<ICompilationUnitWrapper, ReferencingDetail> callerCompilationUnitList = new HashMap<>();
	
	private HashMap<ICompilationUnitWrapper, ArrayList<ASTNode>> referingDetails
		= new HashMap<>();
	
	private ArrayList<UnitMemberWrapper> members = new ArrayList<>();
	private ArrayList<ICompilationUnitWrapper> ancestors;
	private ArrayList<ICompilationUnitWrapper> descendants;
	
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
			String packageName, HashMap<String, Integer> termFrequency, String description, boolean isAbstract, String modifier) {
		super();
		this.mappingModule = mappingModule;
		this.isInterface = isInterface;
		this.simpleName = simpleName;
		this.packageName = packageName;
		this.termFrequency = termFrequency;
		this.description = description;
		this.isAbstract = isAbstract;
		this.modifier = modifier;
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
		
		int modiferFlag = typeDeclar.getModifiers();
		this.modifier = ModifierWrapper.parseSecurityModifer(modiferFlag);
		this.isAbstract = Modifier.isAbstract(modiferFlag);
		
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
	
	public ArrayList<ICompilationUnitWrapper> getAllAncesterSuperInterfaces(){
		ArrayList<ICompilationUnitWrapper> superInterfaceList = getAllAncestors();
		Iterator<ICompilationUnitWrapper> interfIter = superInterfaceList.iterator();
		while(interfIter.hasNext()){
			ICompilationUnitWrapper unit = interfIter.next();
			if(!unit.isInterface()){
				interfIter.remove();
			}
		}
		
		return superInterfaceList;
	}
	
	public ArrayList<ICompilationUnitWrapper> getAllAncestors(){
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
		
		return ancestors;
	}
	
	public ArrayList<ICompilationUnitWrapper> getAllDescedants(){
		if(this.descendants == null){
			ArrayList<ICompilationUnitWrapper> descendants = new ArrayList<>();
			
			for(ICompilationUnitWrapper child: this.childList){
				descendants.add(child);
			}
			
			ArrayList<ICompilationUnitWrapper> descendants0 = (ArrayList<ICompilationUnitWrapper>) descendants.clone();
			
			for(ICompilationUnitWrapper unit: descendants0){
				ArrayList<ICompilationUnitWrapper> ancesList = unit.getAllDescedants();
				descendants.addAll(ancesList);
			}
			
			this.descendants = descendants;
		}
		
		return descendants;
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
	
	public boolean hasCalleeCompilationUnit(ICompilationUnitWrapper unit, int type){
		for(Entry<ICompilationUnitWrapper, ReferencingDetail> calleeUnitEntry: this.calleeCompilationUnitList.entrySet()){
			//ReferencingDetail detail = this.calleeCompilationUnitList.get(calleeUnit);
			if(calleeUnitEntry.getKey().equals(unit) && calleeUnitEntry.getValue().getMap().containsKey(type)){
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
	public HashMap<ICompilationUnitWrapper, ReferencingDetail> getCalleeCompilationUnitList() {
		return calleeCompilationUnitList;
	}

	/**
	 * @param calleeCompilationUnitList the calleeCompilationUnitList to set
	 */
	public void setCalleeCompilationUnitList(
			HashMap<ICompilationUnitWrapper, ReferencingDetail> calleeCompilationUnitList) {
		this.calleeCompilationUnitList = calleeCompilationUnitList;
	}

	/**
	 * @return the callerCompilationUnitList
	 */
	public HashMap<ICompilationUnitWrapper, ReferencingDetail> getCallerCompilationUnitList() {
		return callerCompilationUnitList;
	}

	/**
	 * @param callerCompilationUnitList the callerCompilationUnitList to set
	 */
	public void setCallerCompilationUnitList(
			HashMap<ICompilationUnitWrapper, ReferencingDetail> callerCompilationUnitList) {
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
	
	public void addCaller(ICompilationUnitWrapper unit, int referenceType){
		if(!this.callerCompilationUnitList.keySet().contains(unit)){
			ReferencingDetail detail = new ReferencingDetail();
			detail.addOneReference(referenceType);
			this.callerCompilationUnitList.put(unit, detail);
		}
		else{
			ReferencingDetail detail = this.callerCompilationUnitList.get(unit);
			detail.addOneReference(referenceType);
			this.callerCompilationUnitList.put(unit, detail);
		}
	}
	
	public void addCallee(ICompilationUnitWrapper unit, int referenceType){
		if(!this.calleeCompilationUnitList.keySet().contains(unit)){
			ReferencingDetail detail = new ReferencingDetail();
			detail.addOneReference(referenceType);
			this.calleeCompilationUnitList.put(unit, detail);
		}
		else{
			ReferencingDetail detail = this.calleeCompilationUnitList.get(unit);
			detail.addOneReference(referenceType);
			this.calleeCompilationUnitList.put(unit, detail);
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
	 * @see reflexactoring.diagram.bean.programmodel.GraphNode#getCallerList()
	 */
	@Override
	public HashMap<GraphNode, ReferencingDetail> getCallerList(int type) {
		//return convertToList(callerCompilationUnitList);
		HashMap<GraphNode, ReferencingDetail> map = new HashMap<>();
		for(ICompilationUnitWrapper unit: this.callerCompilationUnitList.keySet()){
			ReferencingDetail detail = this.callerCompilationUnitList.get(unit);
			if(detail.getMap().containsKey(type)){
				map.put(unit, detail);				
			}
		}
		return map;
	}

	/** (non-Javadoc)
	 * @see reflexactoring.diagram.bean.programmodel.GraphNode#getCalleeList()
	 */
	@Override
	public HashMap<GraphNode, ReferencingDetail> getCalleeList(int type) {
		//return convertToList(calleeCompilationUnitList);
		HashMap<GraphNode, ReferencingDetail> map = new HashMap<>();
		
		for(Entry<ICompilationUnitWrapper,ReferencingDetail> unitEntry: this.calleeCompilationUnitList.entrySet()){
			ReferencingDetail detail = unitEntry.getValue();
			
			if(detail.getMap().containsKey(type)){
				map.put(unitEntry.getKey(), detail);				
			}
		}
		return map;
	}
	

	/** (non-Javadoc)
	 * @see reflexactoring.diagram.bean.programmodel.GraphNode#getParentList()
	 */
	@Override
	public List<? extends GraphNode> getParentList() {
		return this.parentList;
	}

	/** (non-Javadoc)
	 * @see reflexactoring.diagram.bean.programmodel.GraphNode#getChildList()
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
		if(nodeList == null || node == null){
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
				!alreadyHasAMethodInSuperTypeWithSameSingature(method) &&
				(isTheSameTypeWithAFieldAccessedByTheMethod(method) || isAsAParameterOfTheMethod(method));
				
	}
	
	public boolean isTheSameTypeWithAFieldAccessedByTheMethod(MethodWrapper method){
		ICompilationUnitWrapper sourceUnit = method.getUnitWrapper();
		for(UnitMemberWrapper member: sourceUnit.getMembers()){
			if(member instanceof FieldWrapper){
				FieldWrapper field = (FieldWrapper)member;
				
				for(ProgramReference reference: method.getRefereePointList()){
					LowLevelGraphNode node = reference.getReferee();
					if(node instanceof FieldWrapper){
						FieldWrapper calleeField = (FieldWrapper)node;
						if(field.equals(calleeField)){
							/**
							 * at least one field accessed by the method should be with the same type as target unit
							 */
							for(ProgramReference ref: field.getRefereePointList()){
								if(ref.getReferenceType() == ProgramReference.TYPE_DECLARATION){
									LowLevelGraphNode n = ref.getReferee();
									if(n instanceof ICompilationUnitWrapper){
										ICompilationUnitWrapper type = (ICompilationUnitWrapper)n;
										if(type.equals(this)){
											return true;
										}
									}
								}
								
							}
							
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean isAsAParameterOfTheMethod(MethodWrapper method){
		for(ProgramReference reference: method.getRefereePointList()){
			if(reference.getReferenceType() == ProgramReference.PARAMETER_ACCESS){
				LowLevelGraphNode node = reference.getReferee();
				if(node instanceof ICompilationUnitWrapper){
					ICompilationUnitWrapper unit = (ICompilationUnitWrapper)node;
					
					if(unit.equals(this)){
						return true;
					}
				}
			}
		}
		return false;
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

	/**
	 * @return the modifier
	 */
	public String getModifier() {
		return modifier;
	}

	/**
	 * @param modifier the modifier to set
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * @return the isAbstract
	 */
	public boolean isAbstract() {
		return isAbstract;
	}

	/**
	 * @param isAbstract the isAbstract to set
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
}
