/**
 * 
 */
package reflexactoring.diagram.action;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import reflexactoring.diagram.bean.FieldWrapper;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ProgramReference;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapperList;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 * 
 */
public class ClassStructureBuilder {
	
	private ArrayList<ProgramReference> referenceList = new ArrayList<>();

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
		
		private void buildRelation(UnitMemberWrapper referer, UnitMemberWrapper referee, ASTNode node){
			ProgramReference reference = new ProgramReference(referer, referee, node);
			referer.addProgramReferee(reference);
			referee.addProgramReferer(reference);
			
			referenceList.add(reference);
		}
		
		public boolean visit(SimpleName name){
			IBinding binding = name.resolveBinding();
			IJavaElement element = binding.getJavaElement();
			if(element != null && binding instanceof IVariableBinding){
				for(UnitMemberWrapper calleeMember: members){
					if (calleeMember instanceof FieldWrapper) {
						FieldWrapper fieldWrapper = (FieldWrapper) calleeMember;
						if (element.equals(fieldWrapper.getJavaMember()) && !callerMember.equals(fieldWrapper)) {
							buildRelation(callerMember, fieldWrapper, name);
						}
					}
				}							
			}
			
			return false;
		}

		public boolean visit(MethodInvocation invocation) {

			IMethodBinding methodBinding = invocation.resolveMethodBinding();
			IJavaElement element = methodBinding.getJavaElement();

			for (UnitMemberWrapper calleeMember : members) {
				if (calleeMember instanceof MethodWrapper) {
					MethodWrapper methodWrapper = (MethodWrapper) calleeMember;
					if (element.equals(methodWrapper.getJavaMember())) {
						buildRelation(callerMember, methodWrapper, invocation);
					}
				}
			}

			return true;
		}

		public boolean visit(ClassInstanceCreation creation) {

			IMethodBinding methodBinding = creation.resolveConstructorBinding();
			methodBinding.getDeclaringClass();
			String key = methodBinding.getKey();

			for (UnitMemberWrapper calleeMember : members) {
				if (calleeMember instanceof MethodWrapper) {
					MethodWrapper methodWrapper = (MethodWrapper) calleeMember;
					String methodKey = methodWrapper.getMethod()
							.resolveBinding().getKey();

					if (key.equals(methodKey)) {
						buildRelation(callerMember, methodWrapper, creation);
					}
				}
			}

			return true;
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

					return false;
				}

				public boolean visit(MethodDeclaration md) {
					MethodWrapper methodWrapper = new MethodWrapper(md,
							unitWrapper);
					unitWrapper.getMembers().add(methodWrapper);
					memberList.add(methodWrapper);

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
