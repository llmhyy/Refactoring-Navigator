/**
 * 
 */
package reflexactoring.diagram.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.programmodel.DeclarationInfluencingDetail;
import reflexactoring.diagram.bean.programmodel.FieldWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.MethodWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramReference;
import reflexactoring.diagram.bean.programmodel.ReferenceInflucencedDetail;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapperList;
import reflexactoring.diagram.bean.programmodel.VariableDeclarationWrapper;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 * 
 */
public class ClassStructureBuilder {
	
	private ArrayList<ProgramReference> referenceList = new ArrayList<>();
	private ArrayList<VariableDeclarationWrapper> declarationList = new ArrayList<>();

	public class MemberRelationVisitor extends ASTVisitor {
		
		private UnitMemberWrapperList members;
		private UnitMemberWrapper callerMember;
		
		public MemberRelationVisitor(UnitMemberWrapper callerUnit, UnitMemberWrapperList members){
			this.callerMember = callerUnit;
			this.members = members;
		}
		
		public UnitMemberWrapperList getMembers() {
			return members;
		}

		public void setMembers(UnitMemberWrapperList members) {
			this.members = members;
		}
		
		/**
		 * access field
		 */
		public boolean visit(SimpleName name){
			IBinding binding = name.resolveBinding();
			IJavaElement element = binding.getJavaElement();
			if(element != null && binding instanceof IVariableBinding){
				for(UnitMemberWrapper calleeMember: members){
					if (calleeMember instanceof FieldWrapper) {
						FieldWrapper fieldWrapper = (FieldWrapper) calleeMember;
						if (element.equals(fieldWrapper.getJavaMember()) && !callerMember.equals(fieldWrapper)) {
							VariableDeclarationWrapper declaration = null;
							
							VariableDeclarationFragment fragment = (VariableDeclarationFragment) fieldWrapper.getField().fragments().get(0);
							String fullQualifiedName = fragment.getName().resolveTypeBinding().getQualifiedName();
							ICompilationUnitWrapper unitWrapper = Settings.scope.findUnit(fullQualifiedName);
							if(unitWrapper != null){
								declaration = new VariableDeclarationWrapper(unitWrapper, fieldWrapper.getName(), fragment); 
							}
							
							ArrayList<ReferenceInflucencedDetail> decList = new ArrayList<ReferenceInflucencedDetail>();

							if(null != declaration){
								decList.add(new ReferenceInflucencedDetail(declaration, DeclarationInfluencingDetail.ACCESS_OBJECT));
							}
							
							buildRelation(callerMember, fieldWrapper, name, ProgramReference.FIELD_ACCESS, 
									decList);
						}
					}
				}							
			}
			
			return false;
		}

		public class ExpressionVisitor extends ASTVisitor{
			
			private VariableDeclarationWrapper declaration;

			public boolean visit(SimpleType type){
				return false;
			}
			
			public boolean visit(SimpleName name){
				if(name.resolveBinding() instanceof IVariableBinding){
					setDeclaration(extractVariableDeclarationWrpper(name));					
				}
				else{
					this.declaration = null;
				}
				
				return false;
			}

			/**
			 * @return the declaration
			 */
			public VariableDeclarationWrapper getDeclaration() {
				return declaration;
			}

			/**
			 * @param declaration the declaration to set
			 */
			public void setDeclaration(VariableDeclarationWrapper declaration) {
				this.declaration = declaration;
			}
		}
		
		/**
		 * invoke method
		 */
		public boolean visit(MethodInvocation invocation) {

			IMethodBinding methodBinding = invocation.resolveMethodBinding();
			IJavaElement element = methodBinding.getJavaElement();

			for (UnitMemberWrapper calleeMember : members) {
				if (calleeMember instanceof MethodWrapper) {
					MethodWrapper methodWrapper = (MethodWrapper) calleeMember;
					if (element.equals(methodWrapper.getJavaMember())) {
						ArrayList<ReferenceInflucencedDetail> decList = new ArrayList<>();
						
						/**
						 * check the object access, e.g., where the "a" in "a.m()" is declared.
						 */
						VariableDeclarationWrapper declaration = null;
						Expression expression = invocation.getExpression();
						if(expression != null){
							if(expression instanceof Name){
								Name name = (Name)expression;
								declaration = extractVariableDeclarationWrpper(name);
								
								if(null != declaration){
									decList.add(new ReferenceInflucencedDetail(declaration, DeclarationInfluencingDetail.ACCESS_OBJECT));
									
									//decList.put(declaration, DeclarationInfluencingDetail.ACCESS_OBJECT);
								}
							}
							else{
								ExpressionVisitor expVisitor = new ExpressionVisitor();
								expression.accept(expVisitor);
								declaration = expVisitor.getDeclaration();
								if(null != declaration){
									decList.add(new ReferenceInflucencedDetail(declaration, DeclarationInfluencingDetail.ACCESS_OBJECT));
								}
							}
						}
						
						
						/**
						 * check the parameter usage, e.g., where the "p" in "a.m(p)" is declared.
						 */
						VariableDeclarationWrapper paramDeclaration = null;
						for(Object obj: invocation.arguments()){
							if(obj instanceof Name){
								Name name = (Name)obj;
								paramDeclaration = extractVariableDeclarationWrpper(name);
								
								if(null != paramDeclaration){
									decList.add(new ReferenceInflucencedDetail(paramDeclaration, DeclarationInfluencingDetail.PARAMETER));
								}
							}
							else{
								Expression paramExpression = (Expression)obj;
								ExpressionVisitor expVisitor = new ExpressionVisitor();
								paramExpression.accept(expVisitor);
								paramDeclaration = expVisitor.getDeclaration();
								if(null != paramDeclaration){
									decList.add(new ReferenceInflucencedDetail(paramDeclaration, DeclarationInfluencingDetail.PARAMETER));
								}
							}
							
						}
						
						buildRelation(callerMember, methodWrapper, invocation, ProgramReference.METHOD_INVOCATION, 
								decList);
					}
				}
			}

			return true;
		}

		/**
		 * @param invocation
		 * @return
		 */
		private VariableDeclarationWrapper extractVariableDeclarationWrpper(
				Name name) {
			VariableDeclarationWrapper declaration = null;
			
			IBinding binding = name.resolveBinding();
			ASTNode node = callerMember.getUnitWrapper().getJavaUnit().findDeclaringNode(binding);
			if(node instanceof VariableDeclaration){
				VariableDeclaration vd = (VariableDeclaration)node;
				
				ITypeBinding typeBinding = name.resolveTypeBinding();
				if(typeBinding != null){
					String fullQualifiedName = typeBinding.getQualifiedName();
					ICompilationUnitWrapper declaringWrapper = Settings.scope.findUnit(fullQualifiedName);
					if(declaringWrapper != null){
						declaration = new VariableDeclarationWrapper(declaringWrapper, name.getFullyQualifiedName(), vd);
					}
				}				
			}
			
			return declaration;
		}

		/**
		 * invoke constructor
		 */
		public boolean visit(ClassInstanceCreation creation) {

			IMethodBinding methodBinding = creation.resolveConstructorBinding();
			String key = methodBinding.getKey();

			boolean isContainedInList = false;
			for (UnitMemberWrapper calleeMember : members) {
				if (calleeMember instanceof MethodWrapper) {
					MethodWrapper methodWrapper = (MethodWrapper) calleeMember;
					String methodKey = methodWrapper.getMethod().resolveBinding().getKey();

					if (key.equals(methodKey)) {
						isContainedInList = true;
						
						//HashMap<VariableDeclarationWrapper, Integer> decList = new HashMap<>();
						ArrayList<ReferenceInflucencedDetail> decList = new ArrayList<>();
						
						CompilationUnit cu = callerMember.getUnitWrapper().getJavaUnit();
						VariableDeclarationFragment fragment = null;
						ASTNode parent = creation.getParent(); 
						if(parent instanceof VariableDeclarationFragment){
							fragment = (VariableDeclarationFragment) parent;							
						}
						else if(parent instanceof Assignment){
							Assignment assignment = (Assignment)parent;
							Expression expression = assignment.getLeftHandSide();
							if(expression instanceof SimpleName){
								SimpleName name = (SimpleName)expression;
								fragment = (VariableDeclarationFragment) cu.findDeclaringNode(name.resolveBinding().getKey());
							}
						}
						else{
							//may be throw new exception
						}
						
						if(fragment != null){
							ICompilationUnitWrapper unitWrapper = Settings.scope.findUnit(
									creation.getType().resolveBinding().getQualifiedName());
							
							VariableDeclarationWrapper declaration = 
									new VariableDeclarationWrapper(unitWrapper, fragment.getName().getIdentifier(), fragment);
							
							//decList.put(declaration, DeclarationInfluencingDetail.ACCESS_OBJECT);
							decList.add(new ReferenceInflucencedDetail(declaration, DeclarationInfluencingDetail.ACCESS_OBJECT));
						}
						
						/**
						 * check the parameter usage, e.g., where the "p" in "a.m(p)" is declared.
						 */
						VariableDeclarationWrapper paramDeclaration = null;
						for(Object obj: creation.arguments()){
							if(obj instanceof Name){
								Name name = (Name)obj;
								paramDeclaration = extractVariableDeclarationWrpper(name);
								
								if(null != paramDeclaration){
									decList.add(new ReferenceInflucencedDetail(paramDeclaration, DeclarationInfluencingDetail.PARAMETER));
								}
							}
							else{
								Expression paramExpression = (Expression)obj;
								ExpressionVisitor expVisitor = new ExpressionVisitor();
								paramExpression.accept(expVisitor);
								paramDeclaration = expVisitor.getDeclaration();
								if(null != paramDeclaration){
									decList.add(new ReferenceInflucencedDetail(paramDeclaration, DeclarationInfluencingDetail.PARAMETER));
								}
							}
							
						}
						/*for(Object obj: creation.arguments()){
							if(obj instanceof SimpleName){
								SimpleName name = (SimpleName)obj;
								VariableDeclarationWrapper paramDeclaration = extractVariableDeclarationWrpper(name);
								
								if(paramDeclaration != null){
									//decList.put(paramDeclaration, DeclarationInfluencingDetail.PARAMETER);
									decList.add(new ReferenceInflucencedDetail(paramDeclaration, DeclarationInfluencingDetail.PARAMETER));
								}
								
							}
							
						}*/
						
						
						buildRelation(callerMember, methodWrapper, creation, ProgramReference.METHOD_INVOCATION, 
								decList);
					}
				}
			}
			
			if(!isContainedInList){
				String qualifiedName = methodBinding.getDeclaringClass().getQualifiedName();
				ICompilationUnitWrapper correspondingUnit = Settings.scope.findUnit(qualifiedName);
				if(null != correspondingUnit){
					/**
					 * empty constructor is always available, therefore, I do not further check the details.
					 */
					buildRelation(callerMember, correspondingUnit, creation, ProgramReference.NEW_DEFAULT_CONSTRUCTOR, 
							new ArrayList<ReferenceInflucencedDetail>());
				}
			}

			return true;
		}
	}
	
	private void buildRelation(UnitMemberWrapper referer, LowLevelGraphNode referee, ASTNode node, 
			int referenceType, ArrayList<ReferenceInflucencedDetail> decList){
		
		if(referer.toString().contains("writePage")){
			System.currentTimeMillis();
		}
		
		ProgramReference reference = new ProgramReference(referer, referee, node, referenceType, decList);
		referer.addProgramReferee(reference);
		referee.addProgramReferer(reference);
		
		referenceList.add(reference);
		
		if(decList.size() != 0){
			/**
			 * there is a global variable to record the overall variable declarations, thus, I replace 
			 * new variable declaration with existing variable declaration
			 */
			for(VariableDeclarationWrapper declaration: declarationList){
				for(ReferenceInflucencedDetail detail: decList){
					VariableDeclarationWrapper dec = detail.getDeclaration();
					if(dec.equals(declaration)){
						detail.setDeclaration(declaration);
					}
				}
				
			}
			
			for(ReferenceInflucencedDetail refDetail: decList){
				VariableDeclarationWrapper dec = refDetail.getDeclaration();
				if(!declarationList.contains(dec)){
					declarationList.add(dec);
				}
				
				DeclarationInfluencingDetail decDetail = new DeclarationInfluencingDetail(reference, refDetail.getType());
				dec.getInfluencedReferenceList().add(decDetail);
			}
			
			reference.setVariableDeclarationList(decList);
		}
	}

	/**
	 * Identifying the reference relations in classes/interfaces in scope.
	 * 
	 * @param compilationUnitList
	 * 
	 */
	public void buildStructuralDependency(
			final ArrayList<ICompilationUnitWrapper> compilationUnitList,
			IProgressMonitor monitor, int scale) {
		
		ArrayList<ICompilationUnitWrapper> innerClassList = parseInnerClass(compilationUnitList);
		compilationUnitList.addAll(innerClassList);
		Settings.scope.setScopeCompilationUnitList(compilationUnitList);

		buildExtendingAndImplementingRelations(Settings.scope.getAllTheTypesInScope());

		ArrayList<UnitMemberWrapper> memberList = buildUnitMemberContainmentRelation(Settings.scope.getAllTheTypesInScope());

		/**
		 * build caller/callee relation for member list
		 */
		UnitMemberWrapperList memberlist = buildMemberRelations(memberList);
		
		Settings.scope.setScopeMemberList(memberlist);
		Settings.scope.setReferenceList(referenceList);
		Settings.scope.setDeclarationList(declarationList);
		
		/**
		 * build caller/callee relation for compilation unit list
		 */
		Settings.scope.updateUnitCallingRelationByMemberRelations();
		
		System.currentTimeMillis();
	}

	/**
	 * @param outerClassList
	 * @return
	 */
	private ArrayList<ICompilationUnitWrapper> parseInnerClass(
			ArrayList<ICompilationUnitWrapper> outerClassList) {
		ArrayList<ICompilationUnitWrapper> innerClassList = new ArrayList<>();
		for(ICompilationUnitWrapper outerClass: outerClassList){
			TypeDeclaration td = (TypeDeclaration) outerClass.getJavaUnit().types().get(0);
			for(TypeDeclaration innerType: td.getTypes()){
				ICompilationUnitWrapper innerClassWrapper = new ICompilationUnitWrapper(innerType);
				outerClass.getInnerClassList().add(innerClassWrapper);
				innerClassWrapper.setOuterClass(outerClass);
				
				innerClassList.add(innerClassWrapper);
			}
		}
		return innerClassList;
	}

	/**
	 * @param memberList
	 * @return
	 */
	private UnitMemberWrapperList buildMemberRelations(ArrayList<UnitMemberWrapper> memberList) {
		UnitMemberWrapperList members = new UnitMemberWrapperList();

		for (UnitMemberWrapper wrapper : memberList) {
			members.add(wrapper);
		}

		for (final UnitMemberWrapper member : members) {
			if (member instanceof MethodWrapper) {
				MethodWrapper methodWrapper = (MethodWrapper) member;
				methodWrapper.getMethod().accept(new MemberRelationVisitor(member, members));
				
				if(methodWrapper.toString().equals("MapQuestion.setAnswer")){
					System.currentTimeMillis();
				}
				
				UnitMemberWrapper superMember = methodWrapper.findMemberWithSameSignatureInSuperTypes();
				if(superMember != null){
					methodWrapper.setOverridedMethod((MethodWrapper)superMember);
				}
			} else if (member instanceof FieldWrapper) {
				((FieldWrapper) member).getField().accept(new MemberRelationVisitor(member, members));
			}
		}

		return members;
	}

	private ArrayList<UnitMemberWrapper> buildUnitMemberContainmentRelation(
			final ArrayList<ICompilationUnitWrapper> compilationUnitList) {
		final ArrayList<UnitMemberWrapper> memberList = new ArrayList<>();

		for (final ICompilationUnitWrapper unitWrapper : compilationUnitList) {
			final TypeDeclaration unit = unitWrapper.getTypeDeclaration();
			unit.accept(new ASTVisitor() {
				public boolean visit(TypeDeclaration td){
					if(unit != td){
						return false;
					}
					return true;
				}
				
				public boolean visit(FieldDeclaration fd) {
					FieldWrapper fieldWrapper = new FieldWrapper(fd,
							unitWrapper);
					unitWrapper.getMembers().add(fieldWrapper);
					memberList.add(fieldWrapper);
					/**
					 * build relation for type declaration based program reference
					 */
					String typeName = fd.getType().resolveBinding().getQualifiedName();
					for(ICompilationUnitWrapper declaringUnitWrapper: compilationUnitList){
						if(declaringUnitWrapper.getFullQualifiedName().equals(typeName)){
							buildRelation(fieldWrapper, declaringUnitWrapper, fd, ProgramReference.TYPE_DECLARATION, 
									new ArrayList<ReferenceInflucencedDetail>());
						}
					}

					return false;
				}

				public boolean visit(MethodDeclaration md) {
					MethodWrapper methodWrapper = new MethodWrapper(md,
							unitWrapper);
					unitWrapper.getMembers().add(methodWrapper);
					memberList.add(methodWrapper);
					
					/**
					 * build relation for type declaration based and parameter based program reference
					 */
					Type returnType = md.getReturnType2();
					if(returnType != null){
						String typeName = returnType.resolveBinding().getQualifiedName();
						for(ICompilationUnitWrapper searchingUnitWrapper: compilationUnitList){
							if(searchingUnitWrapper.getFullQualifiedName().equals(typeName)){
								buildRelation(methodWrapper, searchingUnitWrapper, md, ProgramReference.TYPE_DECLARATION, 
										new ArrayList<ReferenceInflucencedDetail>());
							}
						}
					}
					
					@SuppressWarnings("unchecked")
					List<SingleVariableDeclaration> parameters = md.parameters();
					for(SingleVariableDeclaration svd: parameters){
						String typeName = svd.getType().resolveBinding().getQualifiedName();
						for(ICompilationUnitWrapper declaringUnitWrapper: compilationUnitList){
							if(declaringUnitWrapper.getFullQualifiedName().equals(typeName)){
								/*VariableDeclarationWrapper declaration = 
										new VariableDeclarationWrapper(declaringUnitWrapper, svd.getName().getIdentifier(), svd);*/
								buildRelation(methodWrapper, declaringUnitWrapper, md, ProgramReference.PARAMETER_ACCESS, 
										new ArrayList<ReferenceInflucencedDetail>());
							}
						}
					}
					
					return false;
				}
				
			});

		}

		return memberList;
	}

	private void buildExtendingAndImplementingRelations(
			ArrayList<ICompilationUnitWrapper> compilationUnitList) {
		for (ICompilationUnitWrapper refererUnit : compilationUnitList) {
			for (ICompilationUnitWrapper refereeUnit : compilationUnitList) {
				if (!refererUnit.equals(refereeUnit)) {
					System.currentTimeMillis();

					TypeDeclaration refererType = refererUnit.getTypeDeclaration();
					TypeDeclaration refereeType = refereeUnit.getTypeDeclaration();

					ITypeBinding superType = refererType.resolveBinding()
							.getSuperclass();
					ITypeBinding[] interfaceList = refererType.resolveBinding()
							.getInterfaces();

					ITypeBinding referee = refereeType.resolveBinding();

					if (superType != null
							&& superType.getJavaElement().equals(
									referee.getJavaElement())) {
						refererUnit.setSuperClass(refereeUnit);
						refererUnit.addParent(refereeUnit);
						refereeUnit.addChild(refererUnit);
					} else {
						for (int i = 0; i < interfaceList.length; i++) {
							if (referee.getJavaElement().equals(
									interfaceList[i].getJavaElement())) {
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
