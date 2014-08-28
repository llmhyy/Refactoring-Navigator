/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.smelldetection.NameGernationCounter;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.util.RefactoringOppUtil;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.DeclarationInfluencingDetail;
import reflexactoring.diagram.bean.programmodel.FieldWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.MethodWrapper;
import reflexactoring.diagram.bean.programmodel.ModifierWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.ProgramReference;
import reflexactoring.diagram.bean.programmodel.ReferenceInflucencedDetail;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.bean.programmodel.VariableDeclarationWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public abstract class PullUpMemberOpportunity extends RefactoringOpportunity{
	protected ArrayList<UnitMemberWrapper> toBePulledMemberList = new ArrayList<>();
	protected ICompilationUnitWrapper targetUnit;
	
	public PullUpMemberOpportunity(ArrayList<UnitMemberWrapper> toBePulledMemberList, ArrayList<ModuleWrapper> moduleList){
		this.moduleList = moduleList;
		this.toBePulledMemberList = toBePulledMemberList;
	}
	
	
	public ArrayList<UnitMemberWrapper> getToBePulledMemberList() {
		return toBePulledMemberList;
	}
	public void setToBePulledMemberList(
			ArrayList<UnitMemberWrapper> toBePulledMemberList) {
		this.toBePulledMemberList = toBePulledMemberList;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Pull up ");
		String elementType = (toBePulledMemberList.get(0) instanceof MethodWrapper)?"method":"field";
		buffer.append(elementType + " ");
		for(UnitMemberWrapper member: toBePulledMemberList){
			buffer.append(member.toString()+",");
		}
		return buffer.toString();
	}
	
	@Override
	public ArrayList<ASTNode> getHints() {
		ArrayList<ASTNode> hints = new ArrayList<>();
		for(UnitMemberWrapper member: toBePulledMemberList){
			hints.add(member.getJavaElement());
		}
		for(UnitMemberWrapper member: toBePulledMemberList){
			for(ProgramReference reference: member.getRefererPointList()){
				ASTNode node = reference.getASTNode();
				hints.add(node);
			}
		}
		return hints;
	}

	@Override
	public boolean checkLegal() {
		try {
			IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
			project.open(null);
			IJavaProject javaProject = JavaCore.create(project);

			//check whether to be pulled member exists or not
			for(UnitMemberWrapper member : toBePulledMemberList){
				if(member instanceof MethodWrapper){					
					IType sourceType = javaProject.findType(member.getUnitWrapper().getFullQualifiedName());
					if(sourceType == null){
						return false;
					}
					ICompilationUnit unit = sourceType.getCompilationUnit();
					CompilationUnit cu = RefactoringOppUtil.parse(unit);
					FindMethodDeclarationVisitor findMemberVisitor = new FindMethodDeclarationVisitor(((MethodWrapper) member).getName(), ((MethodWrapper) member).getParameters());
					cu.accept(findMemberVisitor);

					System.currentTimeMillis();
					
					if(findMemberVisitor.result == null){
						return false;
					}
				}else{
					IType sourceType = javaProject.findType(member.getUnitWrapper().getFullQualifiedName());
					if(sourceType == null){
						return false;
					}
					ICompilationUnit unit = sourceType.getCompilationUnit();
					CompilationUnit cu = RefactoringOppUtil.parse(unit);
					FindFieldDeclarationVisitor findMemberVisitor = new FindFieldDeclarationVisitor(((FieldWrapper) member).getName());
					cu.accept(findMemberVisitor);

					if(findMemberVisitor.result == null){
						return false;
					}
				}
			}
			
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * In this method, a new member is created, the following relations are built: containment relation between member and unit,
	 * all the references to to-be-pulled members now point to the new member in unit.
	 * 
	 * @param newModel
	 * @param superUnit
	 * @return
	 */
	protected UnitMemberWrapper createNewMemberInSuperUnit(ProgramModel newModel, ICompilationUnitWrapper superUnit, boolean isPullSignature) {
		UnitMemberWrapper oldMember = toBePulledMemberList.get(0);
		
		UnitMemberWrapper newMember = null;
		if(oldMember instanceof MethodWrapper){
			MethodWrapper methodWrapper = (MethodWrapper)oldMember;
			newMember = new MethodWrapper(methodWrapper.getName(), methodWrapper.getReturnType(), 
					methodWrapper.getParameters(), methodWrapper.isConstructor(), superUnit, null, 
					methodWrapper.getDescription(), null, null, isPullSignature, ModifierWrapper.PUBLIC);			
		}
		else if(oldMember instanceof FieldWrapper){
			FieldWrapper fieldWrapper = (FieldWrapper)oldMember;
			newMember = new FieldWrapper(fieldWrapper.getName(), fieldWrapper.getType(), superUnit,
					null, fieldWrapper.getDescription(), null, false, oldMember.getModifier());
		}
		
		if(newMember == null)return null;
		
		newModel.getScopeMemberList().add(newMember);
		superUnit.getMembers().add(newMember);
		
		for(UnitMemberWrapper oldMem: toBePulledMemberList){
			UnitMemberWrapper newToBePulledMember = newModel.findMember(oldMem);
			
			handleReferersOfToBePulledMember(newToBePulledMember, newMember, superUnit, newModel);
			
			if(!isPullSignature){
				handleRefereesOfToBePulledMember(newToBePulledMember, newMember);
				
				/**
				 * handle overriding relations, that is, if a method overrides the oldMem, then it need overrides
				 * the new member in super type
				 */
				for(UnitMemberWrapper member: newModel.getScopeMemberList()){
					if(member instanceof MethodWrapper){
						MethodWrapper overridedMethod = ((MethodWrapper)member).getOverridedMethod();
						if(overridedMethod.equals(oldMem)){
							((MethodWrapper)member).setOverridedMethod((MethodWrapper) newMember);
						}
					}
				}
			}
			else{
				if(newToBePulledMember instanceof MethodWrapper){
					((MethodWrapper)newToBePulledMember).setOverridedMethod((MethodWrapper)newMember);
				}
			}
			
		}
		
		return newMember;
	}
	
	/**
	 * On creating a new member, new_mem in super class (or interface) to be overriden by the to-be-pulled member, tbp_mem, this method will do
	 * the following things:
	 * 
	 * 1) all the referers of tbp_mem (but are not located in the class declaring tbp_m) will now refer to new_mem.
	 * 2) the refer pointer list in tbp_mem is set empty.
	 * @param newToBePulledMember
	 * @param newMember
	 */
	protected void handleReferersOfToBePulledMember(UnitMemberWrapper newToBePulledMember, 
			UnitMemberWrapper newMember, ICompilationUnitWrapper superUnit, ProgramModel newModel){
		
		ArrayList<ICompilationUnitWrapper> subClasses = new ArrayList<>();
		for(UnitMemberWrapper m: toBePulledMemberList){
			ICompilationUnitWrapper subClass = m.getUnitWrapper();
			if(!subClasses.contains(subClass)){
				subClasses.add(subClass);
			}
		}
		
		for(ProgramReference reference: newToBePulledMember.getRefererPointList()){
			reference.setReferee(newMember);
			newMember.addProgramReferer(reference);
			/**
			 * find the variable declaration defining the access object of the method invocation and field access, 
			 * change its type declaration from subclass to superclass.
			 */
			for(ReferenceInflucencedDetail refDecDetail: reference.getVariableDeclarationList()){	
				
				if(refDecDetail.getType() == DeclarationInfluencingDetail.ACCESS_OBJECT){
					VariableDeclarationWrapper dec = refDecDetail.getDeclaration();
					
					dec.setUnitWrapper(superUnit);
					
					if(dec.isField()){
						UnitMemberWrapper referer = reference.getReferer();
						ICompilationUnitWrapper declaringClass = referer.getUnitWrapper();
						String fieldName = dec.getVariableName();
						FieldWrapper fieldWrapper = newModel.findField(declaringClass.getFullQualifiedName(), fieldName);
						
						for(ProgramReference ref: fieldWrapper.getRefereePointList()){
							if(ref.getReferenceType() == ProgramReference.TYPE_DECLARATION){
								ICompilationUnitWrapper referedUnit = (ICompilationUnitWrapper) ref.getReferee();
								if(subClasses.contains(referedUnit)){
									ref.setReferee(superUnit);
									dec.setUnitWrapper(superUnit);
								}
								
								break;
							}
						}
					}
				}
			
			}
			
			
		}
		
		newToBePulledMember.setRefererPointList(new ArrayList<ProgramReference>());
	}
	
	/**
	 * On creating a new member, new_mem in super class to be overriden by the to-be-pulled member, tbp_mem, this method will do
	 * the following things:
	 * 
	 * 1) all the referees of tbp_mem (but are not located in the class declaring tbp_m) will now be refered by new_mem.
	 * 2) the referee pointer list in tbp_mem is set empty.
	 * @param newToBePulledMember
	 * @param newMember
	 */
	protected void handleRefereesOfToBePulledMember(UnitMemberWrapper newToBePulledMember, UnitMemberWrapper newMember){
		for(ProgramReference reference: newToBePulledMember.getRefereePointList()){
			reference.setReferer(newMember);
			newMember.addProgramReferee(reference);
		}
		
		newToBePulledMember.setRefereePointList(new ArrayList<ProgramReference>());
	}

	/**
	 * A new interface will be named by ***able, class will be named by ***Parent, and the inheritance relation will be formed as well.
	 * 
	 * @param newModel
	 * @return
	 */
	protected ICompilationUnitWrapper createNewUnit(ProgramModel newModel, boolean isInterface) {
		UnitMemberWrapper memberWrapper = toBePulledMemberList.get(0);
		ICompilationUnitWrapper referringUnit = memberWrapper.getUnitWrapper();
		ICompilationUnitWrapper subClassUnit = newModel.findUnit(referringUnit.getFullQualifiedName());
		
		/**
		 * find a way to name the new unit
		 */
		String simpleName = memberWrapper.getName();
		if(isInterface){
			simpleName += "able" + NameGernationCounter.retrieveNumber();
		}else{
			simpleName += "Parent" + NameGernationCounter.retrieveNumber();
		}
		
		String head = "" + simpleName.toCharArray()[0];
		simpleName = head.toUpperCase() + simpleName.substring(1, simpleName.length());
		
		ICompilationUnitWrapper newUnit = new ICompilationUnitWrapper(subClassUnit.getMappingModule(), 
				isInterface, simpleName, subClassUnit.getPackageName(), new HashMap<String, Integer>(), "abstract", true, ModifierWrapper.PUBLIC);
		
		newModel.getScopeCompilationUnitList().add(newUnit);
		
		for(UnitMemberWrapper member: toBePulledMemberList){
			UnitMemberWrapper newMember = newModel.findMember(member);
			
			ICompilationUnitWrapper unit = newMember.getUnitWrapper();
			if(isInterface){
				unit.addSuperInterface(newUnit);
			}else{
				unit.setSuperClass(newUnit);
			}
			
			unit.addParent(newUnit);
			newUnit.addChild(unit);
		}
		
		return newUnit;
	}
	
	/*@Override
	public double computeSimilarityWith(RefactoringOpportunity opp){
		if(opp instanceof PullUpMemberOpportunity){
			PullUpMemberOpportunity thatOpp = (PullUpMemberOpportunity)opp;
			
			double memberSim = ReflexactoringUtil.computeSetSimilarity(toBePulledMemberList, thatOpp.getToBePulledMemberList());
			double unitSim = ReflexactoringUtil.computeSetSimilarity(getUnitsOfToBePulledMembers(), thatOpp.getUnitsOfToBePulledMembers());
			
			return (memberSim + unitSim)/2;
		}
		
		return 0;
	}*/
	
	public ArrayList<ICompilationUnitWrapper> getUnitsOfToBePulledMembers(){
		ArrayList<ICompilationUnitWrapper> units = new ArrayList<>();
		for(UnitMemberWrapper member: toBePulledMemberList){
			units.add(member.getUnitWrapper());
		}
		return units;
	} 
	
	protected boolean isHavingSameMemberList(ArrayList<UnitMemberWrapper> memberList){
		if(memberList.size() == toBePulledMemberList.size()){
			for(UnitMemberWrapper thatMember: memberList){
				if(!canFindAnEqualMemberInList(toBePulledMemberList, thatMember)){
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private boolean canFindAnEqualMemberInList(ArrayList<UnitMemberWrapper> list, UnitMemberWrapper member){
		for(UnitMemberWrapper memberInList: list){
			if(memberInList.equals(member)){
				return true;
			}
		}
		
		return false;
	}
	
	public class ASTNodeInfo {
		public ASTNode node;
		public boolean isToBePulled;
		public String castType;
		public ASTNodeInfo(ASTNode node, boolean isToBePulled, String castType){
			this.node = node;
			this.isToBePulled = isToBePulled;
			this.castType = castType;
		}
		public boolean equails(Object o){
			if(o instanceof ASTNodeInfo){
				if(((ASTNodeInfo)o).node.equals(this.node)
						&& ((ASTNodeInfo)o).isToBePulled == this.isToBePulled
						&& ((ASTNodeInfo)o).castType.equals(this.castType)) return true;
			}
			return false;
		}
	}
	
	public class ReturnStatementVisitor extends ASTVisitor {
		private String oldTypeName;
		private AST ast;
		private ASTRewrite rewrite;
		private ASTNodeInfo nodeInfo;
		
		/**
		 * @param oldTypeName
		 * @param ast
		 * @param rewrite
		 * @param nodeInfo
		 */
		public ReturnStatementVisitor(String oldTypeName, AST ast,
				ASTRewrite rewrite, ASTNodeInfo nodeInfo) {
			super();
			this.oldTypeName = oldTypeName;
			this.ast = ast;
			this.rewrite = rewrite;
			this.nodeInfo = nodeInfo;
		}

		public boolean visit(ReturnStatement statement){
			
			Expression exp = statement.getExpression();
			
			if(exp == null){
				return false;
			}
			
			if(exp.resolveTypeBinding().getName().equals(oldTypeName)){
				
				Expression expressionCopy1 = (Expression) rewrite.createCopyTarget(exp);
				
				ParenthesizedExpression paExpression1 = ast.newParenthesizedExpression();	
				paExpression1.setExpression(expressionCopy1);
				
				CastExpression castExpression1 = ast.newCastExpression();
				Name name1 = ast.newSimpleName(nodeInfo.castType);
				Type type1 = ast.newSimpleType(name1);
				castExpression1.setType(type1);
				castExpression1.setExpression(paExpression1);
				
				ParenthesizedExpression pa2Expression1 = ast.newParenthesizedExpression();	
				pa2Expression1.setExpression(castExpression1);
				
				rewrite.replace(exp, pa2Expression1, null);
				
			}
			
			
			return true;
		}
	}
	
	public class FieldDeclarationVisitor extends ASTVisitor {

		private String newMemberName;

		/**
		 * @param newMemberName
		 */
		public FieldDeclarationVisitor(String newMemberName) {
			super();
			this.newMemberName = newMemberName;
		}
		
		public boolean visit(FieldDeclaration fd){
			if(((VariableDeclarationFragment) fd.fragments().get(0)).getName().toString().equals(newMemberName)){
				fd.delete();
				return false;
			}
			
			return true;
		}
	}
	
	public class MethodDeclarationVisitor extends ASTVisitor {

		private String newMemberName;
		ArrayList<String> paramList;

		/**
		 * @param newMemberName
		 */
		public MethodDeclarationVisitor(String newMemberName, ArrayList<String> paramList) {
			super();
			this.newMemberName = newMemberName;
			this.paramList = paramList;
		}
		
		public boolean visit(MethodDeclaration md){
			boolean hasSameParam = true;
			for(Object o : md.parameters()){
				SingleVariableDeclaration svd = (SingleVariableDeclaration) o;
				String pName = ((SimpleType) svd.getType()).getName().toString();
				if(!pName.equals(paramList.get(md.parameters().indexOf(o)))){
					hasSameParam = false;
					break;
				}
			}
			if(md.getName().toString().equals(newMemberName) && hasSameParam){
				md.delete();
				return false;
			}
			
			return true;
		}
	}
	
	public class FindMethodDeclarationVisitor extends ASTVisitor {

		private String methodName;
		ArrayList<String> paramList;
		MethodDeclaration result;

		/**
		 * @param methodName
		 */
		public FindMethodDeclarationVisitor(String methodName, ArrayList<String> paramList) {
			super();
			this.methodName = methodName;
			this.paramList = paramList;
		}
		
		public boolean visit(MethodDeclaration md){
			boolean hasSameParam = true;
			if(paramList.size() == md.parameters().size()){
				for(Object o : md.parameters()){
					SingleVariableDeclaration svd = (SingleVariableDeclaration) o;
					String pName = svd.getType().toString();
					
					if(!pName.equals(paramList.get(md.parameters().indexOf(o)))){
						hasSameParam = false;
						break;
					}
				}
				
			}
			if(md.getName().toString().equals(methodName) && hasSameParam){
				result = md;
				return false;
			}
			
			return true;
		}
	}

	public class FindFieldDeclarationVisitor extends ASTVisitor {

		private String fieldName;
		FieldDeclaration result;

		/**
		 * @param fieldName
		 */
		public FindFieldDeclarationVisitor(String fieldName) {
			super();
			this.fieldName = fieldName;
		}
		
		public boolean visit(FieldDeclaration fd){
			if(((VariableDeclarationFragment) fd.fragments().get(0)).getName().toString().equals(fieldName)){
				result = fd;
				return false;
			}
			
			return true;
		}
	}
	
	
	/**
	 * @param parent
	 * @param memberList
	 * @param newMemberName
	 */
	protected boolean createAbstractMethodInParent(
			ICompilationUnitWrapper parent,
			ArrayList<UnitMemberWrapper> memberList, String newMemberName) {		
		//ICompilationUnit parentUnit = parent.getCompilationUnit();
		try{			
			IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
			project.open(null);
			IJavaProject javaProject = JavaCore.create(project);
			IType parentType = javaProject.findType(parent.getFullQualifiedName());
			ICompilationUnit parentUnit = parentType.getCompilationUnit();			
			
			parentUnit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
			IBuffer parentBuffer = parentUnit.getBuffer();		
			
			CompilationUnit parentCompilationUnit = RefactoringOppUtil.parse(parentUnit);
			parentCompilationUnit.recordModifications();
			
			IType childType = javaProject.findType(memberList.get(0).getUnitWrapper().getFullQualifiedName());
			ICompilationUnit childUnit = childType.getCompilationUnit();
			CompilationUnit childCompilationUnit = RefactoringOppUtil.parse(childUnit);
			FindMethodDeclarationVisitor findMemberVisitor = new FindMethodDeclarationVisitor(((MethodWrapper) memberList.get(0)).getName(), ((MethodWrapper) memberList.get(0)).getParameters());
			childCompilationUnit.accept(findMemberVisitor);
			
			MethodDeclaration mdOfMemberToPull = findMemberVisitor.result;								
			MethodDeclaration md = (MethodDeclaration) ASTNode.copySubtree(parentCompilationUnit.getAST(), mdOfMemberToPull);
			
			((TypeDeclaration) parentCompilationUnit.types().get(0)).bodyDeclarations().add(md);
			md.setName(parentCompilationUnit.getAST().newSimpleName(newMemberName));
			md.modifiers().add(parentCompilationUnit.getAST().newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
			md.setBody(null);
			
			Type returnType = mdOfMemberToPull.getReturnType2();
			if(returnType != null && !returnType.isPrimitiveType()){
				if(returnType.isArrayType()){
					ArrayType arrayType = (ArrayType) returnType;
					Type elementType = arrayType.getElementType();
					String name = elementType.resolveBinding().getQualifiedName();
					Name qualifiedName = RefactoringOppUtil.createQualifiedName(parentCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = parentCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					parentCompilationUnit.imports().add(importDeclaration);
				}else if(returnType.isSimpleType()){
					String name = returnType.resolveBinding().getQualifiedName();
					Name qualifiedName = RefactoringOppUtil.createQualifiedName(parentCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = parentCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					parentCompilationUnit.imports().add(importDeclaration);
				}else if(returnType.isParameterizedType()){
					ParameterizedType pType = (ParameterizedType) returnType;
					String name = pType.resolveBinding().getQualifiedName().split("<")[0];
					Name qualifiedName = RefactoringOppUtil.createQualifiedName(parentCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = parentCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					parentCompilationUnit.imports().add(importDeclaration);
				}
				
			}
			
			
			List parameters = mdOfMemberToPull.parameters();
			for(Object o : parameters){
				VariableDeclaration vd = (VariableDeclaration) o;
				if(!vd.resolveBinding().getType().isPrimitive()){
					String name;
					if(vd.resolveBinding().getType().isParameterizedType()){
						name = vd.resolveBinding().getType().getTypeDeclaration().getQualifiedName();
					}else{
						name = vd.resolveBinding().getType().getQualifiedName();
					}					
					Name qualifiedName = RefactoringOppUtil.createQualifiedName(parentCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = parentCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					parentCompilationUnit.imports().add(importDeclaration);
				}
			}
			
			Document parentDocument = new Document(parentUnit.getSource());
			TextEdit parentTextEdit = parentCompilationUnit.rewrite(parentDocument, null);
			parentTextEdit.apply(parentDocument);
			
			parentBuffer.setContents(parentDocument.get());	
			
			JavaModelUtil.reconcile(parentUnit);
			parentUnit.commitWorkingCopy(true, new NullProgressMonitor());
			parentUnit.discardWorkingCopy();
		} catch (JavaModelException e1) {
			e1.printStackTrace();
			return false;
		} catch (MalformedTreeException e1) {
			e1.printStackTrace();
			return false;
		} catch (BadLocationException e1) {
			e1.printStackTrace();
			return false;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @param parent
	 * @param memberList
	 * @param newMemberName
	 */
	protected boolean createConcreteMethodInParent(
			ICompilationUnitWrapper parent,
			ArrayList<UnitMemberWrapper> memberList, String newMemberName) {
		//ICompilationUnit parentUnit = parent.getCompilationUnit();
		try{			
			IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
			project.open(null);
			IJavaProject javaProject = JavaCore.create(project);
			IType parentType = javaProject.findType(parent.getFullQualifiedName());
			ICompilationUnit parentUnit = parentType.getCompilationUnit();		
			
			parentUnit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
			IBuffer parentBuffer = parentUnit.getBuffer();		
			
			CompilationUnit parentCompilationUnit = RefactoringOppUtil.parse(parentUnit);
			parentCompilationUnit.recordModifications();
			
			IType childType = javaProject.findType(memberList.get(0).getUnitWrapper().getFullQualifiedName());
			ICompilationUnit childUnit = childType.getCompilationUnit();
			CompilationUnit childCompilationUnit = RefactoringOppUtil.parse(childUnit);
			FindMethodDeclarationVisitor findMemberVisitor = new FindMethodDeclarationVisitor(((MethodWrapper) memberList.get(0)).getName(), ((MethodWrapper) memberList.get(0)).getParameters());
			childCompilationUnit.accept(findMemberVisitor);
			
			MethodDeclaration mdOfMemberToPull = findMemberVisitor.result;								
			MethodDeclaration md = (MethodDeclaration) ASTNode.copySubtree(parentCompilationUnit.getAST(), mdOfMemberToPull);
			
			((TypeDeclaration) parentCompilationUnit.types().get(0)).bodyDeclarations().add(md);
			md.setName(parentCompilationUnit.getAST().newSimpleName(newMemberName));
			boolean hasModifier = false;
			for(Object o : md.modifiers()){
				Modifier m = (Modifier) o;
				if(m.getKeyword().toFlagValue() == Modifier.PUBLIC || m.getKeyword().toFlagValue() == Modifier.PROTECTED){
					hasModifier = true;
					break;
				}else if(m.getKeyword().toFlagValue() == Modifier.PRIVATE){
					hasModifier = true;
					m.setKeyword(Modifier.ModifierKeyword.PROTECTED_KEYWORD);
					break;
				}
			}
			if(!hasModifier){
				md.modifiers().add(parentCompilationUnit.getAST().newModifier(Modifier.ModifierKeyword.PROTECTED_KEYWORD));
			}
			
			Type returnType = mdOfMemberToPull.getReturnType2();
			if(returnType != null && !returnType.isPrimitiveType()){
				if(returnType.isArrayType()){
					ArrayType arrayType = (ArrayType) returnType;
					Type elementType = arrayType.getElementType();
					String name = elementType.resolveBinding().getQualifiedName();
					Name qualifiedName = RefactoringOppUtil.createQualifiedName(parentCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = parentCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					parentCompilationUnit.imports().add(importDeclaration);
				}else if(returnType.isSimpleType()){
					String name = returnType.resolveBinding().getQualifiedName();
					Name qualifiedName = RefactoringOppUtil.createQualifiedName(parentCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = parentCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					parentCompilationUnit.imports().add(importDeclaration);
				}else if(returnType.isParameterizedType()){
					ParameterizedType pType = (ParameterizedType) returnType;
					String name = pType.resolveBinding().getQualifiedName().split("<")[0];
					Name qualifiedName = RefactoringOppUtil.createQualifiedName(parentCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = parentCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					parentCompilationUnit.imports().add(importDeclaration);
				}
				
			}
			
			
			List parameters = mdOfMemberToPull.parameters();
			for(Object o : parameters){
				VariableDeclaration vd = (VariableDeclaration) o;
				if(!vd.resolveBinding().getType().isPrimitive()){
					String name;
					if(vd.resolveBinding().getType().isParameterizedType()){
						name = vd.resolveBinding().getType().getTypeDeclaration().getQualifiedName();
					}else{
						name = vd.resolveBinding().getType().getQualifiedName();
					}					
					Name qualifiedName = RefactoringOppUtil.createQualifiedName(parentCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = parentCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					parentCompilationUnit.imports().add(importDeclaration);
				}
			}
			
			Document parentDocument = new Document(parentUnit.getSource());
			TextEdit parentTextEdit = parentCompilationUnit.rewrite(parentDocument, null);
			parentTextEdit.apply(parentDocument);
			
			parentBuffer.setContents(parentDocument.get());	
			
			JavaModelUtil.reconcile(parentUnit);
			parentUnit.commitWorkingCopy(true, new NullProgressMonitor());
			parentUnit.discardWorkingCopy();
		} catch (JavaModelException e1) {
			e1.printStackTrace();
			return false;
		} catch (MalformedTreeException e1) {
			e1.printStackTrace();
			return false;
		} catch (BadLocationException e1) {
			e1.printStackTrace();
			return false;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @param parent
	 * @param memberList
	 * @param newMemberName
	 */
	protected boolean createConcreteFieldInParent(
			ICompilationUnitWrapper parent,
			ArrayList<UnitMemberWrapper> memberList, String newMemberName) {
		//ICompilationUnit parentUnit = parent.getCompilationUnit();
		try{			
			IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
			project.open(null);
			IJavaProject javaProject = JavaCore.create(project);
			IType parentType = javaProject.findType(parent.getFullQualifiedName());
			ICompilationUnit parentUnit = parentType.getCompilationUnit();		
			
			parentUnit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
			IBuffer parentBuffer = parentUnit.getBuffer();		
			
			CompilationUnit parentCompilationUnit = RefactoringOppUtil.parse(parentUnit);
			parentCompilationUnit.recordModifications();
			
			IType childType = javaProject.findType(memberList.get(0).getUnitWrapper().getFullQualifiedName());
			ICompilationUnit childUnit = childType.getCompilationUnit();
			CompilationUnit childCompilationUnit = RefactoringOppUtil.parse(childUnit);
			FindFieldDeclarationVisitor findMemberVisitor = new FindFieldDeclarationVisitor(((FieldWrapper) memberList.get(0)).getName());
			childCompilationUnit.accept(findMemberVisitor);
			
			FieldDeclaration fdOfMemberToPull = findMemberVisitor.result;	
			FieldDeclaration fd = (FieldDeclaration) ASTNode.copySubtree(parentCompilationUnit.getAST(), fdOfMemberToPull);
			boolean hasModifier = false;
			for(Object o : fd.modifiers()){
				Modifier m = (Modifier) o;
				if(m.getKeyword().toFlagValue() == Modifier.PUBLIC || m.getKeyword().toFlagValue() == Modifier.PROTECTED){
					hasModifier = true;
					break;
				}else if(m.getKeyword().toFlagValue() == Modifier.PRIVATE){
					hasModifier = true;
					m.setKeyword(Modifier.ModifierKeyword.PROTECTED_KEYWORD);
					break;
				}
			}
			if(!hasModifier){
				fd.modifiers().add(parentCompilationUnit.getAST().newModifier(Modifier.ModifierKeyword.PROTECTED_KEYWORD));
			}
			
			((TypeDeclaration) parentCompilationUnit.types().get(0)).bodyDeclarations().add(fd);
			VariableDeclarationFragment fragment = (VariableDeclarationFragment) fd.fragments().get(0);
			fragment.setName(parentCompilationUnit.getAST().newSimpleName(newMemberName));
			
			Type fieldType = fdOfMemberToPull.getType();
			if(fieldType != null && !fieldType.isPrimitiveType()){
				if(fieldType.isArrayType()){
					ArrayType arrayType = (ArrayType) fieldType;
					Type elementType = arrayType.getElementType();
					String name = elementType.resolveBinding().getQualifiedName();
					Name qualifiedName = RefactoringOppUtil.createQualifiedName(parentCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = parentCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					parentCompilationUnit.imports().add(importDeclaration);
				}else if(fieldType.isSimpleType()){
					String name = fieldType.resolveBinding().getQualifiedName();
					Name qualifiedName = RefactoringOppUtil.createQualifiedName(parentCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = parentCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					parentCompilationUnit.imports().add(importDeclaration);
				}else if(fieldType.isParameterizedType()){
					ParameterizedType pType = (ParameterizedType) fieldType;
					String name = pType.resolveBinding().getQualifiedName().split("<")[0];
					Name qualifiedName = RefactoringOppUtil.createQualifiedName(parentCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = parentCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					parentCompilationUnit.imports().add(importDeclaration);
				}
				
			}
			
			Document parentDocument = new Document(parentUnit.getSource());
			TextEdit parentTextEdit = parentCompilationUnit.rewrite(parentDocument, null);
			parentTextEdit.apply(parentDocument);
			
			parentBuffer.setContents(parentDocument.get());	
			
			JavaModelUtil.reconcile(parentUnit);
			parentUnit.commitWorkingCopy(true, new NullProgressMonitor());
			parentUnit.discardWorkingCopy();
		} catch (JavaModelException e1) {
			e1.printStackTrace();
			return false;
		} catch (MalformedTreeException e1) {
			e1.printStackTrace();
			return false;
		} catch (BadLocationException e1) {
			e1.printStackTrace();
			return false;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	

	/**
	 * @param parent
	 * @param memberList
	 * @param newMemberName
	 */
	protected boolean removeConcreteMemberInChild(UnitMemberWrapper member, String newMemberName) {
		ICompilationUnit unit = member.getUnitWrapper().getCompilationUnit();
		try{
			unit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
			IBuffer buffer = unit.getBuffer();		
			
			CompilationUnit compilationUnit = RefactoringOppUtil.parse(unit);
			compilationUnit.recordModifications();
									
			if(member instanceof MethodWrapper){
				compilationUnit.accept(new MethodDeclarationVisitor(newMemberName, ((MethodWrapper) member).getParameters()));
			}else{
				compilationUnit.accept(new FieldDeclarationVisitor(newMemberName));
			}
			
			Document parentDocument = new Document(unit.getSource());
			TextEdit parentTextEdit = compilationUnit.rewrite(parentDocument, null);
			parentTextEdit.apply(parentDocument);
			
			buffer.setContents(parentDocument.get());	
			
			JavaModelUtil.reconcile(unit);
			unit.commitWorkingCopy(true, new NullProgressMonitor());
			unit.discardWorkingCopy();
		} catch (JavaModelException e1) {
			e1.printStackTrace();
			return false;
		} catch (MalformedTreeException e1) {
			e1.printStackTrace();
			return false;
		} catch (BadLocationException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * @param parentInterface
	 * @param memberList
	 */
	protected static boolean addSubClassImplements(ICompilationUnitWrapper parentInterface,
			ArrayList<UnitMemberWrapper> memberList) {
		for(UnitMemberWrapper member : memberList){
			ICompilationUnit unit = member.getUnitWrapper().getCompilationUnit();
			try {
				unit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
				IBuffer buffer = unit.getBuffer();				
				buffer.getContents();
				
				CompilationUnit compilationUnit = RefactoringOppUtil.parse(unit);
				compilationUnit.recordModifications();
				
				TypeDeclaration td = (TypeDeclaration)compilationUnit.types().get(0);	
				Name name = td.getAST().newSimpleName(parentInterface.getName());
				Type type = td.getAST().newSimpleType(name);
				td.superInterfaceTypes().add(type);
				
				Name qualifiedName = RefactoringOppUtil.createQualifiedName(td.getAST(), parentInterface.getFullQualifiedName());
				ImportDeclaration importDeclaration = td.getAST().newImportDeclaration();
				importDeclaration.setName(qualifiedName);
				importDeclaration.setOnDemand(false);
				compilationUnit.imports().add(importDeclaration);
				
				Document document = new Document(unit.getSource());
				TextEdit textEdit = compilationUnit.rewrite(document, null);
				textEdit.apply(document);
				
				buffer.setContents(document.get());	
				
				JavaModelUtil.reconcile(unit);
				unit.commitWorkingCopy(true, new NullProgressMonitor());
				unit.discardWorkingCopy();
				
			} catch (JavaModelException e1) {
				e1.printStackTrace();
				return false;
			} catch (MalformedTreeException e1) {
				e1.printStackTrace();
				return false;
			} catch (BadLocationException e1) {
				e1.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param parentClass
	 * @param memberList
	 */
	protected static boolean addSubClassExtends(ICompilationUnitWrapper parentClass,
			ArrayList<UnitMemberWrapper> memberList) {
		for(UnitMemberWrapper member : memberList){
			ICompilationUnit unit = member.getUnitWrapper().getCompilationUnit();
			try {
				unit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
				IBuffer buffer = unit.getBuffer();				
				buffer.getContents();
				
				CompilationUnit compilationUnit = RefactoringOppUtil.parse(unit);
				compilationUnit.recordModifications();
				
				TypeDeclaration td = (TypeDeclaration)compilationUnit.types().get(0);	
				Name name = td.getAST().newSimpleName(parentClass.getName());
				Type type = td.getAST().newSimpleType(name);
				td.setSuperclassType(type);
				
				Name qualifiedName = RefactoringOppUtil.createQualifiedName(td.getAST(), parentClass.getFullQualifiedName());
				ImportDeclaration importDeclaration = td.getAST().newImportDeclaration();
				importDeclaration.setName(qualifiedName);
				importDeclaration.setOnDemand(false);
				compilationUnit.imports().add(importDeclaration);
				
				Document document = new Document(unit.getSource());
				TextEdit textEdit = compilationUnit.rewrite(document, null);
				textEdit.apply(document);
				
				buffer.setContents(document.get());	
				
				JavaModelUtil.reconcile(unit);
				unit.commitWorkingCopy(true, new NullProgressMonitor());
				unit.discardWorkingCopy();
				
			} catch (JavaModelException e1) {
				e1.printStackTrace();
				return false;
			} catch (MalformedTreeException e1) {
				e1.printStackTrace();
				return false;
			} catch (BadLocationException e1) {
				e1.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param memberList
	 * @param newMemberName
	 */
	protected boolean renameMembers(ArrayList<UnitMemberWrapper> memberList,
			String newMemberName) {
		try {
			IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
			project.open(null);
			IJavaProject javaProject = JavaCore.create(project);
		
			for(UnitMemberWrapper memberWrapper : memberList){	
				if(memberWrapper instanceof MethodWrapper){
	
					IType sourceType = javaProject.findType(memberWrapper.getUnitWrapper().getFullQualifiedName());
					ICompilationUnit unit = sourceType.getCompilationUnit();
					CompilationUnit cu = RefactoringOppUtil.parse(unit);
					FindMethodDeclarationVisitor findMemberVisitor = new FindMethodDeclarationVisitor(((MethodWrapper) memberWrapper).getName(), ((MethodWrapper) memberWrapper).getParameters());
					cu.accept(findMemberVisitor);
	
					IMethod methodToRename = (IMethod) findMemberVisitor.result.resolveBinding().getJavaElement();
					
					if(!methodToRename.getElementName().equals(newMemberName)){
						
						RenameSupport support = RenameSupport.create(methodToRename, newMemberName, RenameSupport.UPDATE_REFERENCES);
						support.perform(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), PlatformUI.getWorkbench().getActiveWorkbenchWindow());
						
					}
				}else{
					IType sourceType = javaProject.findType(memberWrapper.getUnitWrapper().getFullQualifiedName());
					ICompilationUnit unit = sourceType.getCompilationUnit();
					CompilationUnit cu = RefactoringOppUtil.parse(unit);
					FindFieldDeclarationVisitor findMemberVisitor = new FindFieldDeclarationVisitor(((FieldWrapper) memberWrapper).getName());
					cu.accept(findMemberVisitor);
					
					IField fieldToRename = (IField) ((VariableDeclarationFragment)findMemberVisitor.result.fragments().get(0)).resolveBinding().getJavaElement();
					
					if(!fieldToRename.getElementName().equals(newMemberName)){
						
						RenameSupport support = RenameSupport.create(fieldToRename, newMemberName, RenameSupport.UPDATE_REFERENCES);
						support.perform(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), PlatformUI.getWorkbench().getActiveWorkbenchWindow());
						
					}
				}					
			}
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
			return false;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return false;
		}		
		return true;
	}
	
	/**
	 * @param parent
	 * @param memberList
	 * @return
	 */
	protected HashMap<ICompilationUnit, ArrayList<ASTNodeInfo>> summarizeCastMap(
			ICompilationUnitWrapper parent,
			ArrayList<UnitMemberWrapper> memberList) {
		HashMap<ICompilationUnit, ArrayList<ASTNodeInfo>> modificationMap = new HashMap<ICompilationUnit, ArrayList<ASTNodeInfo>>();
		
		for(UnitMemberWrapper member : memberList){
			for(ProgramReference reference : member.getRefererPointList()){
				for(ReferenceInflucencedDetail variableDetail : reference.getVariableDeclarationList()){
					VariableDeclarationWrapper variable = variableDetail.getDeclaration();
					//cast the declaration into parent class 
					VariableDeclaration variableAST = variable.getAstNode();	
					ICompilationUnit unit = (ICompilationUnit) ((CompilationUnit) variableAST.getRoot()).getJavaElement();
					Type currentVariableType = modifyDeclarationType(unit, variableAST, parent); 
					
					//cast other references' declaration into child class
					for(DeclarationInfluencingDetail detail : variable.getInfluencedReferenceList()){
						ProgramReference influencedReference = detail.getReference();
						
						//for current pulled method's reference, if casted, remove current casting
						if(reference == influencedReference){
							
							if(influencedReference.getReferenceType() == ProgramReference.METHOD_INVOCATION && influencedReference.getASTNode() instanceof MethodInvocation){
								
								
								MethodInvocation invocation = (MethodInvocation) influencedReference.getASTNode();	
								
								IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
								try {
									project.open(null);
									IJavaProject javaProject = JavaCore.create(project);
									IType iType = javaProject.findType(influencedReference.getReferer().getUnitWrapper().getFullQualifiedName());
									CompilationUnit cu = RefactoringOppUtil.parse(iType.getCompilationUnit());
									
									ASTNode node = findCorrespondingNode(cu, invocation);
									while(!(node instanceof MethodInvocation)){
										node = node.getParent();
									}
									
									invocation = (MethodInvocation)node;
								} catch (CoreException e) {
									e.printStackTrace();
								}
								
								//for current pulled method's reference, if casted, remove current casting
								if(detail.getType() == DeclarationInfluencingDetail.ACCESS_OBJECT){
									
									addNodeInfoToMap(modificationMap, invocation.getExpression(), true, null);
									
								}
								//for current pulled method's reference, if parameter casted, remove current casting
								else if(detail.getType() == DeclarationInfluencingDetail.PARAMETER){
									List<Expression> arguments = invocation.arguments();
									for(Expression args : arguments){
										//Name name = (Name) args;
										
										if(args.resolveTypeBinding().getName().equals(currentVariableType.toString())){

											addNodeInfoToMap(modificationMap, args, true, null);
											
										}
									}
								}
								
							}
							//for current pulled method's reference, if assignment casted, remove current casting
							else if(influencedReference.getReferenceType() == ProgramReference.FIELD_ACCESS && influencedReference.getASTNode() instanceof Name){
								Name name = (Name) influencedReference.getASTNode();
								
								IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
								try {
									project.open(null);
									IJavaProject javaProject = JavaCore.create(project);
									IType iType = javaProject.findType(influencedReference.getReferer().getUnitWrapper().getFullQualifiedName());
									CompilationUnit cu = RefactoringOppUtil.parse(iType.getCompilationUnit());
									
									name = (Name) findCorrespondingNode(cu, name);
									
								} catch (CoreException e) {
									e.printStackTrace();
								}
								
								addNodeInfoToMap(modificationMap, name, true, null);
							}
												
							
						}else if(!reference.equals(influencedReference) && influencedReference.getASTNode() instanceof MethodInvocation){
							
							//if variableAST belongs to parent class, it's not the first time to update, then do nothing
							//otherwise, it's the first time, cast all references to sub class
							if(currentVariableType != null && !currentVariableType.toString().equals(parent.getName())){
								
								if(influencedReference.getReferenceType() == ProgramReference.METHOD_INVOCATION && influencedReference.getASTNode() instanceof MethodInvocation){
									MethodInvocation invocation = (MethodInvocation) influencedReference.getASTNode();
									
									IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
									try {
										project.open(null);
										IJavaProject javaProject = JavaCore.create(project);
										IType iType = javaProject.findType(influencedReference.getReferer().getUnitWrapper().getFullQualifiedName());
										CompilationUnit cu = RefactoringOppUtil.parse(iType.getCompilationUnit());
										
										ASTNode node = findCorrespondingNode(cu, invocation);
										while(!(node instanceof MethodInvocation)){
											node = node.getParent();
										}
										
										invocation = (MethodInvocation)node;
									} catch (CoreException e) {
										e.printStackTrace();
									}
									
									if(detail.getType() == DeclarationInfluencingDetail.ACCESS_OBJECT){

										addNodeInfoToMap(modificationMap, invocation.getExpression(), false, currentVariableType.toString());
										
									}
									else if(detail.getType() == DeclarationInfluencingDetail.PARAMETER){
										List<Expression> arguments = invocation.arguments();
										for(Expression args : arguments){
											//Name name = (Name) args;
											
											if(args.resolveTypeBinding().getName().equals(currentVariableType.toString())){
												
												addNodeInfoToMap(modificationMap, args, false, currentVariableType.toString());
												
											}
										}
									}
									
								}
								else if(influencedReference.getReferenceType() == ProgramReference.FIELD_ACCESS && influencedReference.getASTNode() instanceof Name){
									Name name = (Name) influencedReference.getASTNode();
									
									IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
									try {
										project.open(null);
										IJavaProject javaProject = JavaCore.create(project);
										IType iType = javaProject.findType(influencedReference.getReferer().getUnitWrapper().getFullQualifiedName());
										CompilationUnit cu = RefactoringOppUtil.parse(iType.getCompilationUnit());
										
										name = (Name) findCorrespondingNode(cu, name);
										
									} catch (CoreException e) {
										e.printStackTrace();
									}
									
									addNodeInfoToMap(modificationMap, name, false, currentVariableType.toString());
									
								}
							}
							
						}
						
					}
				}
			}
		}
		return modificationMap;
	}
	
	
	protected void addNodeInfoToMap(HashMap<ICompilationUnit, ArrayList<ASTNodeInfo>> map, ASTNode node, boolean isToBePulled, String castType){
		ICompilationUnit icu = (ICompilationUnit) ((CompilationUnit) node.getRoot()).getJavaElement();
		ASTNodeInfo info = new ASTNodeInfo(node, isToBePulled, castType);
		if(!map.containsKey(icu)){
			ArrayList<ASTNodeInfo> infoList = new ArrayList<ASTNodeInfo>();
			infoList.add(info);
			map.put(icu, infoList);
		}else{
			if(!map.get(icu).contains(info)){
				map.get(icu).add(info);
			}
		}
	}
	
	protected boolean modifyCastExpression(ArrayList<ASTNodeInfo> nodeInfoList){
		try {
			ICompilationUnit unit = (ICompilationUnit) ((CompilationUnit) nodeInfoList.get(0).node.getRoot()).getJavaElement();
//			String source = unit.getBuffer().getContents();
//			Document document= new Document(source);
			String oldTypeName = null;
			

			unit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
			IBuffer buffer = unit.getBuffer();			

			CompilationUnit compilationUnit = RefactoringOppUtil.parse(unit);
		
			AST ast = compilationUnit.getAST();
			ASTRewrite rewrite= ASTRewrite.create(ast);		
			
			for(ASTNodeInfo nodeInfo : nodeInfoList){	
				
				ASTNode node = findCorrespondingNode(compilationUnit, nodeInfo.node);
				if(node == null) return false;

				//for current pulled method's reference, if casted, remove current casting
				if(nodeInfo.isToBePulled){
					if(node instanceof ParenthesizedExpression && ((ParenthesizedExpression) node).getExpression() instanceof CastExpression
							//&& ((CastExpression) ((ParenthesizedExpression) node).getExpression()).getExpression() instanceof ParenthesizedExpression
							){
						ParenthesizedExpression pa2Expression = (ParenthesizedExpression) node;
						
						CastExpression castExpression= (CastExpression) pa2Expression.getExpression();
//						ParenthesizedExpression paExpression = (ParenthesizedExpression) castExpression.getExpression();
//						Expression expression = paExpression.getExpression();
						
						Expression expression = castExpression.getExpression();
						
						rewrite.replace(node, expression, null);
					}
					
				}else{
					oldTypeName = ((Expression) node).resolveTypeBinding().getName();
					
					Expression expressionCopy = (Expression) rewrite.createCopyTarget(node);
					
					ParenthesizedExpression paExpression = ast.newParenthesizedExpression();	
					paExpression.setExpression(expressionCopy);
					
					CastExpression castExpression = ast.newCastExpression();
					Name name = ast.newSimpleName(nodeInfo.castType);
					Type type = ast.newSimpleType(name);
					castExpression.setType(type);
					castExpression.setExpression(paExpression);
					
					ParenthesizedExpression pa2Expression = ast.newParenthesizedExpression();	
					pa2Expression.setExpression(castExpression);
					
					rewrite.replace(node, pa2Expression, null);
					

					//modify return type if needed
					ASTNode methodDeclarationNode = node;
					//this node should not be in an invocation while the invocation is in a return statement, such as : return control.setItem();
					//we only change those node which are not in a return statement, such as return control
					while(!(methodDeclarationNode instanceof MethodDeclaration 
							|| methodDeclarationNode instanceof ReturnStatement 
							|| methodDeclarationNode instanceof TypeDeclaration) 
								&& methodDeclarationNode != null){
						methodDeclarationNode = methodDeclarationNode.getParent();
					}
					if(methodDeclarationNode instanceof MethodDeclaration){
						MethodDeclaration md = (MethodDeclaration) methodDeclarationNode;
						md.accept(new ReturnStatementVisitor(oldTypeName, ast, rewrite, nodeInfo));
					}
				}
				

			}
				
			
//			TextEdit textEdit = rewrite.rewriteAST(document, unit.getJavaProject().getOptions(true));
//			textEdit.apply(document);
//			
//			String newSource = document.get();
//			unit.getBuffer().setContents(newSource);
			
			Document document = new Document(unit.getSource());
			TextEdit textEdit = rewrite.rewriteAST(document, null);
			textEdit.apply(document);

			
			buffer.setContents(document.get());	
			
			JavaModelUtil.reconcile(unit);
			unit.commitWorkingCopy(true, new NullProgressMonitor());
			unit.discardWorkingCopy();
			
		} catch (JavaModelException e1) {
			e1.printStackTrace();
			return false;
		} catch (MalformedTreeException e1) {
			e1.printStackTrace();
			return false;
		} catch (BadLocationException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}
	


	/**
	 * find the corresponding node of old nodeInfo in the new compilationUnit
	 * 
	 * @param compilationUnit
	 * @param node
	 * @return
	 */
	private static ASTNode findCorrespondingNode(CompilationUnit compilationUnit,
			ASTNode node) {
		CompilationUnit oldCU = (CompilationUnit) node.getRoot();
		ASTNode oldDeclaringNode = node;
		while(!(oldDeclaringNode instanceof MethodDeclaration || oldDeclaringNode instanceof FieldDeclaration) 
				&& oldDeclaringNode != null){
			oldDeclaringNode = oldDeclaringNode.getParent();
		}
		
		if(oldDeclaringNode == null){
			System.err.println("some ast node is declared in neither method or field ");
			return null;
		}
		
		int methodLineNum = oldCU.getLineNumber(oldDeclaringNode.getStartPosition());
		int nodeLineNum = oldCU.getLineNumber(node.getStartPosition());
		int lineDiff = nodeLineNum - methodLineNum;
		int spaceDiff = node.getStartPosition() - oldCU.getPosition(nodeLineNum, 0);

		ASTNode newDeclaringNode = null;
		if(oldDeclaringNode instanceof MethodDeclaration){
			newDeclaringNode = compilationUnit.findDeclaringNode(((MethodDeclaration)oldDeclaringNode).resolveBinding().getKey());
		}
		else if(oldDeclaringNode instanceof FieldDeclaration){
			FieldDeclaration fd = (FieldDeclaration)oldDeclaringNode;
			VariableDeclarationFragment fragment = (VariableDeclarationFragment) fd.fragments().get(0);
			newDeclaringNode = compilationUnit.findDeclaringNode(fragment.resolveBinding().getKey());
		}
		
		int lineNum = compilationUnit.getLineNumber(newDeclaringNode.getStartPosition()) + lineDiff;
		int startPosition = compilationUnit.getPosition(lineNum, 0) + spaceDiff;
		
		ASTNode newNode = NodeFinder.perform(compilationUnit, startPosition, 0);
		return newNode;
	}
	
	/**
	 * modify the declaration type into new typeName
	 * 
	 * @param unit
	 * @param variableNode0
	 * @param typeName
	 * @return the original type of the declaration
	 * 
	 */
	protected static Type modifyDeclarationType(ICompilationUnit unit, VariableDeclaration variableNode0, ICompilationUnitWrapper parent){
		Type currentVariableType = null;		
		try {
			unit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
			IBuffer buffer = unit.getBuffer();									
			
			CompilationUnit compilationUnit = RefactoringOppUtil.parse(unit);
			VariableDeclaration variableNode = (VariableDeclaration) compilationUnit.findDeclaringNode(variableNode0.resolveBinding().getKey());
			
			compilationUnit.recordModifications();
			
			//add import
			Name qualifiedName = RefactoringOppUtil.createQualifiedName(compilationUnit.getAST(), parent.getFullQualifiedName());
			ImportDeclaration importDeclaration = compilationUnit.getAST().newImportDeclaration();
			importDeclaration.setName(qualifiedName);
			importDeclaration.setOnDemand(false);
			boolean hasImport = false;
			for(Object o : compilationUnit.imports()){
				ImportDeclaration currentImport = (ImportDeclaration) o;
				if(currentImport.toString().equals(importDeclaration.toString())) {
					hasImport = true;
					break;
				}
			}
			if(!hasImport){
				compilationUnit.imports().add(importDeclaration);				
			}
			
			//modify type
			Name name = compilationUnit.getAST().newSimpleName(parent.getName());
			Type type = compilationUnit.getAST().newSimpleType(name);
			if(variableNode instanceof SingleVariableDeclaration){					
				currentVariableType = ((SingleVariableDeclaration) variableNode).getType();	
				((SingleVariableDeclaration) variableNode).setType(type);
			}else if(variableNode instanceof VariableDeclarationFragment){
				if(((VariableDeclarationFragment) variableNode).getParent() instanceof FieldDeclaration){
					currentVariableType = ((FieldDeclaration) ((VariableDeclarationFragment) variableNode).getParent()).getType();	
					((FieldDeclaration) ((VariableDeclarationFragment) variableNode).getParent()).setType(type);
				}else if(((VariableDeclarationFragment) variableNode).getParent() instanceof VariableDeclarationStatement){
					currentVariableType = ((VariableDeclarationStatement) ((VariableDeclarationFragment) variableNode).getParent()).getType();
					((VariableDeclarationStatement) ((VariableDeclarationFragment) variableNode).getParent()).setType(type);
				}else if(((VariableDeclarationFragment) variableNode).getParent() instanceof VariableDeclarationExpression){
					currentVariableType = ((VariableDeclarationExpression) ((VariableDeclarationFragment) variableNode).getParent()).getType();
					((VariableDeclarationExpression) ((VariableDeclarationFragment) variableNode).getParent()).setType(type);
				}
			}			
			
			Document document = new Document(unit.getSource());
			TextEdit textEdit = compilationUnit.rewrite(document, null);
			textEdit.apply(document);
			
			buffer.setContents(document.get());	
			
			JavaModelUtil.reconcile(unit);
			unit.commitWorkingCopy(true, new NullProgressMonitor());
			unit.discardWorkingCopy();
			
		} catch (JavaModelException e1) {
			e1.printStackTrace();
			return null;
		} catch (MalformedTreeException e1) {
			e1.printStackTrace();
			return null;
		} catch (BadLocationException e1) {
			e1.printStackTrace();
			return null;
		}
		return currentVariableType;
	}

	/**
	 * @param position
	 * @param sequence
	 * @param parent
	 * @param memberList
	 * @param newMemberName
	 */
	protected void refreshModel(int position, RefactoringSequence sequence,
			ICompilationUnitWrapper parent,
			ArrayList<UnitMemberWrapper> memberList, String newMemberName) {
		String toBeReplacedTypeName = this.targetUnit.getFullQualifiedName();
		ArrayList<String> toBeReplacedMemberNameList = new ArrayList<>();
		for(UnitMemberWrapper member: this.toBePulledMemberList){
			toBeReplacedMemberNameList.add(member.getName());
		}
		
		for(int i = position; i < sequence.size(); i++ ){
			ProgramModel model = sequence.get(i).getConsequenceModel();
			
			ICompilationUnitWrapper oldUnitInModel = model.findUnit(toBeReplacedTypeName);
			oldUnitInModel.setPackageName(parent.getPackageName());
			oldUnitInModel.setSimpleName(parent.getName());

			/**
			 * Replace the name of the member in super class/ interfaces
			 */
			UnitMemberWrapper firstToBePulledMember = this.toBePulledMemberList.get(0);
			for(UnitMemberWrapper member: oldUnitInModel.getMembers()){
				boolean isMemberAMethod = member instanceof MethodWrapper;
				boolean isPulledMemberMethod = firstToBePulledMember instanceof MethodWrapper;
				
				if(isMemberAMethod == isPulledMemberMethod){
					if(isMemberAMethod){
						MethodWrapper method = (MethodWrapper)member;
						if(method.hasSameSignatureWith(firstToBePulledMember)){
							method.setName(newMemberName);
						}
					}
					else{
						FieldWrapper field = (FieldWrapper)member;
						if(field.getName().equals(firstToBePulledMember.getName())){
							field.setName(newMemberName);
						}
					}
				}
			}
			
			
			for(int j=0; j<memberList.size(); j++){
				UnitMemberWrapper memberWrapper = memberList.get(j);
				UnitMemberWrapper oldMemberInModel;
				if(memberWrapper instanceof MethodWrapper){
					oldMemberInModel = model.findMethod(memberWrapper.getUnitWrapper().getFullQualifiedName(),
													toBeReplacedMemberNameList.get(j), ((MethodWrapper)memberWrapper).getParameters());
				}else{
					oldMemberInModel = model.findField(memberWrapper.getUnitWrapper().getFullQualifiedName(), toBeReplacedMemberNameList.get(j));
				}
				if(oldMemberInModel !=null) {
					oldMemberInModel.setName(newMemberName);
				}
			}
			
			
			
		}
		
		ICompilationUnitWrapper oldUnit = this.targetUnit;
		oldUnit.setSimpleName(parent.getName());
		for(int j=0; j<this.toBePulledMemberList.size(); j++){
			toBePulledMemberList.get(j).setName(newMemberName);
		}
	}
}
