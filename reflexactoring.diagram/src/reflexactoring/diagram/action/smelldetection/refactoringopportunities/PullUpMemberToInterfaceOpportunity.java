/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
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
import org.eclipse.jface.window.Window;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.popup.RenameMethodsDialog;
import reflexactoring.diagram.action.recommend.gencode.JavaClassCreator;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.PullUpMemberPrecondition;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.DeclarationInfluencingDetail;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.MethodWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.ProgramReference;
import reflexactoring.diagram.bean.programmodel.ReferenceInflucencedDetail;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.bean.programmodel.VariableDeclarationWrapper;

/**
 * @author linyun
 * 
 */
public class PullUpMemberToInterfaceOpportunity extends PullUpMemberOpportunity {

	/**
	 * @param toBePulledMemberList
	 */
	public PullUpMemberToInterfaceOpportunity(
			ArrayList<UnitMemberWrapper> toBePulledMemberList,
			ArrayList<ModuleWrapper> moduleList) {
		super(toBePulledMemberList, moduleList);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append(" to created newly interface");
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof PullUpMemberToInterfaceOpportunity){
			PullUpMemberToInterfaceOpportunity thatOpp = (PullUpMemberToInterfaceOpportunity)obj;
			if(thatOpp.isHavingSameMemberList(toBePulledMemberList)){
				return true;
			}
		}
		
		return false;
	} 

	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		/**
		 * create a new interface
		 */
		ICompilationUnitWrapper newInterfaceUnit = createNewUnit(newModel, true);

		/**
		 * create a new method in the newly created interface and change
		 * reference
		 */
		createNewMemberInSuperClass(newModel, newInterfaceUnit);

		newModel.updateUnitCallingRelationByMemberRelations();

		/**
		 * may calculate which module is proper to hold the newly created
		 * interface
		 */
		ModuleWrapper bestMappingModule = calculateBestMappingModule(newModel,
				newInterfaceUnit);
		newInterfaceUnit.setMappingModule(bestMappingModule);
		
		this.targetUnit = newInterfaceUnit;

		return newModel;
	}
	
	/**
	 * It is empty because only the signature of those members are pulled.
	 */
	@Override
	protected void handleRefereesOfToBePulledMember(UnitMemberWrapper newToBePulledMember, UnitMemberWrapper newMember){
		
	}

	@Override
	public boolean apply(int position, RefactoringSequence sequence) {
		JavaClassCreator javaCreator = new JavaClassCreator();
		ICompilationUnitWrapper parentInterface = javaCreator.createInterface();	
		if(parentInterface == null){
			return false;
		}

		//get all members to be pulled
		ArrayList<UnitMemberWrapper> memberList = this.getToBePulledMemberList();
		IMember[] members = new IMember[memberList.size()];
		String[] methodNames = new String[memberList.size()];
		for(UnitMemberWrapper memberWrapper : memberList){
			members[memberList.indexOf(memberWrapper)] = memberWrapper.getJavaMember();	
			methodNames[memberList.indexOf(memberWrapper)] = memberWrapper.getUnitWrapper().getName() + "." + memberWrapper.getName();
		}
		
		//show a wizard to rename all the funcions into one name
		String newMethodName = "";
		RenameMethodsDialog dialog = new RenameMethodsDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, methodNames);
		dialog.create();
		if(dialog.open() == Window.OK){
			newMethodName = dialog.getNewMethodName();								
		}else{
			return false;
		}
		
		//Create an method in parentInterface and set corresponding imports
		ICompilationUnit interfaceUnit = parentInterface.getCompilationUnit();
		try{
			interfaceUnit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
			IBuffer interfaceBuffer = interfaceUnit.getBuffer();		
			
			CompilationUnit interfaceCompilationUnit = parse(interfaceUnit);
			interfaceCompilationUnit.recordModifications();
			
			MethodDeclaration mdOfMemberToPull = (MethodDeclaration) memberList.get(0).getJavaElement();								
			MethodDeclaration md = (MethodDeclaration) ASTNode.copySubtree(interfaceCompilationUnit.getAST(), mdOfMemberToPull);
			
			((TypeDeclaration) interfaceCompilationUnit.types().get(0)).bodyDeclarations().add(md);
			md.setName(interfaceCompilationUnit.getAST().newSimpleName(newMethodName));
			md.modifiers().add(interfaceCompilationUnit.getAST().newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
			md.setBody(null);
			
			Type returnType = mdOfMemberToPull.getReturnType2();
			if(returnType != null && !returnType.isPrimitiveType()){
				if(returnType.isArrayType()){
					ArrayType arrayType = (ArrayType) returnType;
					Type elementType = arrayType.getElementType();
					String name = elementType.resolveBinding().getQualifiedName();
					Name qualifiedName = createQualifiedName(interfaceCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = interfaceCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					interfaceCompilationUnit.imports().add(importDeclaration);
				}else if(returnType.isSimpleType()){
					String name = returnType.resolveBinding().getQualifiedName();
					Name qualifiedName = createQualifiedName(interfaceCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = interfaceCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					interfaceCompilationUnit.imports().add(importDeclaration);
				}else if(returnType.isParameterizedType()){
					ParameterizedType pType = (ParameterizedType) returnType;
					String name = pType.resolveBinding().getQualifiedName().split("<")[0];
					Name qualifiedName = createQualifiedName(interfaceCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = interfaceCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					interfaceCompilationUnit.imports().add(importDeclaration);
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
					Name qualifiedName = createQualifiedName(interfaceCompilationUnit.getAST(), name);
					ImportDeclaration importDeclaration = interfaceCompilationUnit.getAST().newImportDeclaration();
					importDeclaration.setName(qualifiedName);
					importDeclaration.setOnDemand(false);
					interfaceCompilationUnit.imports().add(importDeclaration);
				}
			}
			
			Document interfaceDocument = new Document(interfaceUnit.getSource());
			TextEdit interfaceTextEdit = interfaceCompilationUnit.rewrite(interfaceDocument, null);
			interfaceTextEdit.apply(interfaceDocument);
			
			interfaceBuffer.setContents(interfaceDocument.get());	
			
			JavaModelUtil.reconcile(interfaceUnit);
			interfaceUnit.commitWorkingCopy(true, new NullProgressMonitor());
			interfaceUnit.discardWorkingCopy();
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
		
		//make every child class implements the interface	
		for(UnitMemberWrapper member : memberList){
			ICompilationUnit unit = member.getUnitWrapper().getCompilationUnit();
			try {
				unit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
				IBuffer buffer = unit.getBuffer();				
				buffer.getContents();
				
				CompilationUnit compilationUnit = parse(unit);
				compilationUnit.recordModifications();
				
				TypeDeclaration td = (TypeDeclaration)compilationUnit.types().get(0);	
				Name name = td.getAST().newSimpleName(parentInterface.getName());
				Type type = td.getAST().newSimpleType(name);
				td.superInterfaceTypes().add(type);
				
				Name qualifiedName = createQualifiedName(td.getAST(), parentInterface.getFullQualifiedName());
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
									
		//rename each method
		for(UnitMemberWrapper memberWrapper : memberList){	
			try {									
				IMethod methodToRename = (IMethod) memberWrapper.getJavaMember();
				
				if(!methodToRename.getElementName().equals(newMethodName)){
					
					RenameSupport support = RenameSupport.create(methodToRename, newMethodName, RenameSupport.UPDATE_REFERENCES);
					support.perform(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), PlatformUI.getWorkbench().getActiveWorkbenchWindow());
					
				}
				
			} catch (CoreException e1) {
				e1.printStackTrace();
				return false;
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
				return false;
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				return false;
			}				
		}
		
		HashMap<ICompilationUnit, ArrayList<ASTNodeInfo>> modificationMap = new HashMap<ICompilationUnit, ArrayList<ASTNodeInfo>>();
		
		//cast method invocation variable into parent interface, summarize a map out first
		for(UnitMemberWrapper member : memberList){
			for(ProgramReference reference : member.getRefererPointList()){
				for(ReferenceInflucencedDetail variableDetail : reference.getVariableDeclarationList()){
					VariableDeclarationWrapper variable = variableDetail.getDeclaration();
					//cast the declaration into parent interface 
					VariableDeclaration variableAST = variable.getAstNode();	
					ICompilationUnit unit = (ICompilationUnit) ((CompilationUnit) variableAST.getRoot()).getJavaElement();
					Type currentVariableType = this.modifyDeclarationType(unit, variableAST, parentInterface); 
					
					//cast other references' declaration into child class
					for(DeclarationInfluencingDetail detail : variable.getInfluencedReferenceList()){
						ProgramReference influencedReference = detail.getReference();
						
						//for current pulled method's reference, if casted, remove current casting
						if(reference.equals(influencedReference)){
							
							if(influencedReference.getReferenceType() == ProgramReference.METHOD_INVOCATION && influencedReference.getASTNode() instanceof MethodInvocation){
								MethodInvocation invocation = (MethodInvocation) influencedReference.getASTNode();	
								
								//for current pulled method's reference, if casted, remove current casting
								if(detail.getType() == DeclarationInfluencingDetail.ACCESS_OBJECT){
									
									this.addNodeInfoToMap(modificationMap, invocation.getExpression(), true, currentVariableType.toString());
									
								}
								//for current pulled method's reference, if parameter casted, remove current casting
								else if(detail.getType() == DeclarationInfluencingDetail.PARAMETER){
									List<Expression> arguments = invocation.arguments();
									for(Expression args : arguments){
										Name name = (Name) args;
										
										if(name.resolveTypeBinding().getName().equals(currentVariableType.toString())){

											this.addNodeInfoToMap(modificationMap, args, true, currentVariableType.toString());
											
										}
									}
								}
								
							}
							//for current pulled method's reference, if assignment casted, remove current casting
							else if(influencedReference.getReferenceType() == ProgramReference.FIELD_ACCESS && influencedReference.getASTNode() instanceof Name){
								Name name = (Name) influencedReference.getASTNode();
								
								this.addNodeInfoToMap(modificationMap, name, true, currentVariableType.toString());
							}
												
							
						}else if(!reference.equals(influencedReference) && influencedReference.getASTNode() instanceof MethodInvocation){
							
							//if variableAST belongs to parent interface, it's not the first time to update, then do nothing
							//otherwise, it's the first time, cast all references to sub class
							if(currentVariableType != null && !currentVariableType.toString().equals(parentInterface.getName())){
								
								if(influencedReference.getReferenceType() == ProgramReference.METHOD_INVOCATION && influencedReference.getASTNode() instanceof MethodInvocation){
									MethodInvocation invocation = (MethodInvocation) influencedReference.getASTNode();
																		
									if(detail.getType() == DeclarationInfluencingDetail.ACCESS_OBJECT){

										this.addNodeInfoToMap(modificationMap, invocation.getExpression(), false, currentVariableType.toString());
										
									}
									else if(detail.getType() == DeclarationInfluencingDetail.PARAMETER){
										List<Expression> arguments = invocation.arguments();
										for(Expression args : arguments){
											Name name = (Name) args;
											
											if(name.resolveTypeBinding().getName().equals(currentVariableType.toString())){
												
												this.addNodeInfoToMap(modificationMap, args, false, currentVariableType.toString());
												
											}
										}
									}
									
								}
								else if(influencedReference.getReferenceType() == ProgramReference.FIELD_ACCESS && influencedReference.getASTNode() instanceof Name){
									Name name = (Name) influencedReference.getASTNode();
									
									this.addNodeInfoToMap(modificationMap, name, false, currentVariableType.toString());
									
								}
							}
							
						}
						
					}
				}
			}
		}
		
		//do modifications: add or remove casting
		for(ICompilationUnit icu : modificationMap.keySet()){
			this.modifyCastExpression(modificationMap.get(icu));
		}
		
		//call Eclipse API to pull up
//		try {
//			System.out.println(RefactoringAvailabilityTester.isPullUpAvailable(membersToPull));
//			System.out.println(ActionUtil.isEditable(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), membersToPull[0]));
//			if (RefactoringAvailabilityTester.isPullUpAvailable(membersToPull) && ActionUtil.isEditable(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), membersToPull[0]))
//				RefactoringExecutionStarter.startPullUpRefactoring(membersToPull, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
//		} catch (JavaModelException jme) {
//			ExceptionHandler.handle(jme, RefactoringMessages.OpenRefactoringWizardAction_refactoring, RefactoringMessages.OpenRefactoringWizardAction_exception);
//		}	
		
		return true;
	}

	@Override
	protected boolean checkLegal(ProgramModel model) {
		Precondition precondition = new Precondition(getModuleList());
		return precondition.checkLegal(model);
	}
	
	@Override
	public String getRefactoringName() {
		return "Pull Up Member to New Interface";
	}
	
	public ICompilationUnitWrapper getTargetInterface(){
		return this.targetUnit;
	}
	
	@Override
	public ArrayList<String> getRefactoringDetails(){
		ArrayList<String> refactoringDetails = new ArrayList<>();
		String step1 = "Create an interface for ";
		StringBuffer buffer1 = new StringBuffer();
		for(UnitMemberWrapper member: toBePulledMemberList){
			buffer1.append(member.getUnitWrapper().getSimpleName() + ",");
		}
		String str = buffer1.toString();
		str = str.substring(0, str.length()-1);
		step1 += str;
		
		refactoringDetails.add(step1);
		
		String step2 = "create the member " + toBePulledMemberList.get(0).getName() + " in interface";
		refactoringDetails.add(step2);
		
		String step3 = "Those methods refer to ";
		StringBuffer buffer2 = new StringBuffer();
		for(UnitMemberWrapper member: toBePulledMemberList){
			buffer2.append(member.toString()+ ",");
		}
		String memberString = buffer2.toString();
		memberString = memberString.substring(0, memberString.length()-1);
		step3 += memberString;
		step3 += " now refer to the " + toBePulledMemberList.get(0).getName() + " in super class"; 
		refactoringDetails.add(step3);
		
		String step4 = "Move the interface " + this.targetUnit.getName() + " to module " + this.targetUnit.getMappingModule().getName();
		refactoringDetails.add(step4);
		
		return refactoringDetails;
	};

	public class Precondition extends
			PullUpMemberPrecondition {

		/**
		 * @param moduleList
		 */
		public Precondition(ArrayList<ModuleWrapper> moduleList) {
			super(moduleList);
		}

		@Override
		protected ArrayList<RefactoringOpportunity> detectPullingUpOpportunities(ProgramModel model, ArrayList<ArrayList<UnitMemberWrapper>> refactoringPlaceList,
				ArrayList<ModuleWrapper> moduleList) {
			ArrayList<RefactoringOpportunity> opportunities = new ArrayList<>();
			
			for(ArrayList<UnitMemberWrapper> refactoringPlace: refactoringPlaceList){
				UnitMemberWrapper member = refactoringPlace.get(0);
				
				if(member instanceof MethodWrapper){
					PullUpMemberToInterfaceOpportunity opportunity = 
							new PullUpMemberToInterfaceOpportunity(refactoringPlace, moduleList);
					opportunities.add(opportunity);				
				}
			}
			
			return opportunities;
		}
		
		/**
		 * Given data input by a refactoring opportunity, check whether such opportunity still validates.
		 * 
		 * @param model
		 * @param refactoringPlace
		 * @return
		 */
		@Override
		public boolean checkLegal(ProgramModel model) {
			ArrayList<UnitMemberWrapper> newTBPMemberList = new ArrayList<>();
			for(UnitMemberWrapper oldMember: toBePulledMemberList){
				UnitMemberWrapper newMember = model.findMember(oldMember);
				if(newMember != null){
					newTBPMemberList.add(newMember);
				}
			}
			
			if(newTBPMemberList.size() >= 2){
				toBePulledMemberList = newTBPMemberList;
				return true;
			}
			
			return false;
		}
	}
		
	class ASTNodeInfo {
		ASTNode node;
		boolean isToBePulled;
		String castType;
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
	
	private void addNodeInfoToMap(HashMap<ICompilationUnit, ArrayList<ASTNodeInfo>> map, ASTNode node, boolean isToBePulled, String castType){
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
	
	private boolean modifyCastExpression(ArrayList<ASTNodeInfo> nodeInfoList){
		try {
			ICompilationUnit unit = (ICompilationUnit) ((CompilationUnit) nodeInfoList.get(0).node.getRoot()).getJavaElement();
			String source = unit.getBuffer().getContents();
			Document document= new Document(source);
			String oldTypeName = null;
			

			CompilationUnit compilationUnit = parse(unit);
		
			AST ast = compilationUnit.getAST();
			ASTRewrite rewrite= ASTRewrite.create(ast);		
			
			for(ASTNodeInfo nodeInfo : nodeInfoList){	
				
				ASTNode node = findCorrespondingNode(compilationUnit, nodeInfo);
				if(node == null) return false;

				//for current pulled method's reference, if casted, remove current casting
				if(nodeInfo.isToBePulled){
					if(node instanceof ParenthesizedExpression && ((ParenthesizedExpression) node).getExpression() instanceof CastExpression){
						ParenthesizedExpression pa2Expression = (ParenthesizedExpression) node;
						
						CastExpression castExpression= (CastExpression) pa2Expression.getExpression();
						ParenthesizedExpression paExpression = (ParenthesizedExpression) castExpression.getExpression();
						Expression expression = paExpression.getExpression();
						
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
						
						/*for(Object o : md.getBody().statements()){
							Statement statement = (Statement) o;
							if(statement instanceof ReturnStatement){
							}
						}*/
					}
				}
				

			}
				
			
			TextEdit textEdit = rewrite.rewriteAST(document, unit.getJavaProject().getOptions(true));
			textEdit.apply(document);
			
			String newSource = document.get();
			unit.getBuffer().setContents(newSource);
			
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
	 * @param nodeInfo
	 * @return
	 */
	private ASTNode findCorrespondingNode(CompilationUnit compilationUnit,
			ASTNodeInfo nodeInfo) {
		CompilationUnit oldCU = (CompilationUnit) nodeInfo.node.getRoot();
		ASTNode oldDeclaringNode = nodeInfo.node;
		while(!(oldDeclaringNode instanceof MethodDeclaration || oldDeclaringNode instanceof FieldDeclaration) 
				&& oldDeclaringNode != null){
			oldDeclaringNode = oldDeclaringNode.getParent();
		}
		
		if(oldDeclaringNode == null){
			System.err.println("some ast node is declared in neither method or field ");
			return null;
		}
		
		int methodLineNum = oldCU.getLineNumber(oldDeclaringNode.getStartPosition());
		int nodeLineNum = oldCU.getLineNumber(nodeInfo.node.getStartPosition());
		int lineDiff = nodeLineNum - methodLineNum;
		int spaceDiff = nodeInfo.node.getStartPosition() - oldCU.getPosition(nodeLineNum, 0);

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
		
		ASTNode node = NodeFinder.perform(compilationUnit, startPosition, 0);
		return node;
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
	private Type modifyDeclarationType(ICompilationUnit unit, VariableDeclaration variableNode0, ICompilationUnitWrapper parentInterface){
		Type currentVariableType = null;		
		try {
			unit.becomeWorkingCopy(new SubProgressMonitor(new NullProgressMonitor(), 1));
			IBuffer buffer = unit.getBuffer();									
			
			CompilationUnit compilationUnit = parse(unit);
			VariableDeclaration variableNode = (VariableDeclaration) compilationUnit.findDeclaringNode(variableNode0.resolveBinding().getKey());
			
			compilationUnit.recordModifications();
			
			//add import
			Name qualifiedName = createQualifiedName(compilationUnit.getAST(), parentInterface.getFullQualifiedName());
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
			Name name = compilationUnit.getAST().newSimpleName(parentInterface.getName());
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
	
/*	private ICompilationUnit resolveICompilationUnit(IJavaElement element){

		if (element instanceof IMember) {
            return ((IMember) element).getCompilationUnit();
        } else if (element instanceof IAnnotation 
                // ignore annotations on PackageDeclaration, such as in package-info.java
                && element.getParent() instanceof IMember) { 
            return ((IMember) (element.getParent())).getCompilationUnit();
        } else if (element instanceof ICompilationUnit) {
            return (ICompilationUnit) element;
        } else if (element instanceof ILocalVariable){
        	return  ((ILocalVariable) element).getDeclaringMember().getCompilationUnit();
        }
        return null;
	}*/
}
