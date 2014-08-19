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
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.programmodel.DeclarationInfluenceDetail;
import reflexactoring.diagram.bean.programmodel.FieldWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.MethodWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramReference;
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
							
							ArrayList<VariableDeclarationWrapper> decList = new ArrayList<>();
							ArrayList<Integer> influcenceTypeList = new ArrayList<>();

							if(null != declaration){
								decList.add(declaration);								
								influcenceTypeList.add(DeclarationInfluenceDetail.ACCESS_OBJECT);
							}
							
							buildRelation(callerMember, fieldWrapper, name, ProgramReference.FIELD_ACCESS, 
									decList, influcenceTypeList);
						}
					}
				}							
			}
			
			return false;
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
						ArrayList<VariableDeclarationWrapper> decList = new ArrayList<>();
						ArrayList<Integer> influcenceTypeList = new ArrayList<>();
						
						/**
						 * check the object access, e.g., where the "a" in "a.m()" is declared.
						 */
						VariableDeclarationWrapper declaration = null;
						Expression expression = invocation.getExpression();
						if(expression instanceof Name){
							Name name = (Name)expression;
							declaration = extractVariableDeclarationWrpper(name);
							
							if(null != declaration){
								decList.add(declaration);
								influcenceTypeList.add(DeclarationInfluenceDetail.ACCESS_OBJECT);
							}
						}
						
						/**
						 * check the parameter usage, e.g., where the "p" in "a.m(p)" is declared.
						 */
						for(Object obj: invocation.arguments()){
							if(obj instanceof SimpleName){
								SimpleName name = (SimpleName)obj;
								VariableDeclarationWrapper paramDeclaration = extractVariableDeclarationWrpper(name);
								
								if(paramDeclaration != null){
									decList.add(paramDeclaration);
									influcenceTypeList.add(DeclarationInfluenceDetail.PARAMETER);
								}
							}
						}
						
						buildRelation(callerMember, methodWrapper, invocation, ProgramReference.METHOD_INVOCATION, 
								decList, influcenceTypeList);
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
			VariableDeclaration vd = (VariableDeclaration)node;
			
			ITypeBinding typeBinding = name.resolveTypeBinding();
			if(typeBinding != null){
				String fullQualifiedName = typeBinding.getQualifiedName();
				ICompilationUnitWrapper declaringWrapper = Settings.scope.findUnit(fullQualifiedName);
				if(declaringWrapper != null){
					declaration = new VariableDeclarationWrapper(declaringWrapper, name.getFullyQualifiedName(), vd);
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
						
						ArrayList<VariableDeclarationWrapper> decList = new ArrayList<>();
						ArrayList<Integer> influcenceTypeList = new ArrayList<>();
						
						VariableDeclarationFragment fragment = (VariableDeclarationFragment) creation.getParent();
						ICompilationUnitWrapper unitWrapper = Settings.scope.findUnit(
								creation.getType().resolveBinding().getQualifiedName());
						
						VariableDeclarationWrapper declaration = 
								new VariableDeclarationWrapper(unitWrapper, fragment.getName().getIdentifier(), fragment);
						
						decList.add(declaration);
						influcenceTypeList.add(DeclarationInfluenceDetail.ACCESS_OBJECT);
						
						/**
						 * check the parameter usage, e.g., where the "p" in "a.m(p)" is declared.
						 */
						for(Object obj: creation.arguments()){
							SimpleName name = (SimpleName)obj;
							VariableDeclarationWrapper paramDeclaration = extractVariableDeclarationWrpper(name);
							
							if(paramDeclaration != null){
								decList.add(paramDeclaration);
								influcenceTypeList.add(DeclarationInfluenceDetail.PARAMETER);
							}
						}
						
						
						buildRelation(callerMember, methodWrapper, creation, ProgramReference.METHOD_INVOCATION, 
								decList, influcenceTypeList);
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
							new ArrayList<VariableDeclarationWrapper>(), new ArrayList<Integer>());
				}
			}

			return true;
		}
	}
	
	private void buildRelation(UnitMemberWrapper referer, LowLevelGraphNode referee, ASTNode node, 
			int referenceType, ArrayList<VariableDeclarationWrapper> decList, ArrayList<Integer> influenceTypeList){
		ProgramReference reference = new ProgramReference(referer, referee, node, referenceType, decList);
		referer.addProgramReferee(reference);
		referee.addProgramReferer(reference);
		
		referenceList.add(reference);
		
		if(decList.size() != 0){
			/**
			 * replace new variable declaration with existing variable declaration
			 */
			for(VariableDeclarationWrapper declaration: declarationList){
				for(int i=0; i<decList.size(); i++){
					VariableDeclarationWrapper dec = decList.get(i);
					if(dec.equals(declaration)){
						decList.set(i, declaration);
					}
				}
				
			}
			
			for(int i=0; i<decList.size(); i++){
				VariableDeclarationWrapper vdw = decList.get(i);
				int influenceType = influenceTypeList.get(i);
				
				if(!declarationList.contains(vdw)){
					declarationList.add(vdw);
				}
				
				DeclarationInfluenceDetail detail = new DeclarationInfluenceDetail(reference, influenceType);
				vdw.getInfluencedReferenceList().add(detail);
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
		Settings.scope.setScopeCompilationUnitList(compilationUnitList);

		buildExtendingAndImplementingRelations(Settings.scope.getScopeCompilationUnitList());

		ArrayList<UnitMemberWrapper> memberList = buildUnitMemberContainmentRelation(Settings.scope.getScopeCompilationUnitList());

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
			CompilationUnit unit = unitWrapper.getJavaUnit();
			unit.accept(new ASTVisitor() {
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
									new ArrayList<VariableDeclarationWrapper>(), new ArrayList<Integer>());
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
										new ArrayList<VariableDeclarationWrapper>(), new ArrayList<Integer>());
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
										new ArrayList<VariableDeclarationWrapper>(), new ArrayList<Integer>());
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

					TypeDeclaration refererType = (TypeDeclaration) refererUnit
							.getJavaUnit().types().get(0);
					TypeDeclaration refereeType = (TypeDeclaration) refereeUnit
							.getJavaUnit().types().get(0);

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
