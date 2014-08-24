/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;

import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.programmodel.FieldWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.MethodWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.ProgramReference;
import reflexactoring.diagram.bean.programmodel.ReferenceInflucencedDetail;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public class RefactoringOppUtil {
	
	public static ArrayList<UnitMemberWrapper> copyAList(ArrayList<UnitMemberWrapper> list){
		ArrayList<UnitMemberWrapper> newList = new ArrayList<>();
		for(UnitMemberWrapper m: list){
			newList.add(m);
		}
		
		return newList;
	}
	
	public static ArrayList<String> extractParameters(ICompilationUnitWrapper originalUnit, MethodWrapper objMethod, ProgramModel newModel){
		ArrayList<FieldWrapper> calleeMemberList = new ArrayList<>();
		boolean isMethodInvolved = false;
		for(ProgramReference reference: objMethod.getRefereePointList()){
			LowLevelGraphNode calleeNode = reference.getReferee();
			if(calleeNode instanceof UnitMemberWrapper){
				UnitMemberWrapper calleeMember = (UnitMemberWrapper)calleeNode;
				if(originalUnit.getMembers().contains(calleeMember)){
					if(calleeMember instanceof MethodWrapper){
						isMethodInvolved = true;
					}
					else if(calleeMember instanceof FieldWrapper){
						if(!calleeMemberList.contains(calleeMember)){
							calleeMemberList.add((FieldWrapper)calleeMember);
						}
					}
				}				
			}
			
		}
		
		ArrayList<String> parameters = new ArrayList<>();
		if(isMethodInvolved){
			/**
			 * modify program model
			 */
			ProgramReference reference = new ProgramReference(objMethod, originalUnit, objMethod.getJavaElement(), 
					ProgramReference.PARAMETER_ACCESS, new ArrayList<ReferenceInflucencedDetail>());
			objMethod.addProgramReferee(reference);
			originalUnit.addProgramReferer(reference);
			newModel.getReferenceList().add(reference);
			
			/**
			 * change the signature
			 */
			String parameter = originalUnit.getName();
			parameters.add(parameter);
			return parameters;
		}
		else{
			for(FieldWrapper calleeMember: calleeMemberList){
				ICompilationUnitWrapper accessType = null;
				for(ProgramReference ref: calleeMember.getRefereePointList()){
					if(ref.getReferenceType() == ProgramReference.TYPE_DECLARATION){
						accessType = (ICompilationUnitWrapper) ref.getReferee();
					}
				}
				
				if(accessType != null){
					ProgramReference reference = new ProgramReference(objMethod, accessType, objMethod.getJavaElement(), 
							ProgramReference.PARAMETER_ACCESS, new ArrayList<ReferenceInflucencedDetail>());
					objMethod.addProgramReferee(reference);
					accessType.addProgramReferer(reference);
					newModel.getReferenceList().add(reference);
				}
				
				
				String parameter = calleeMember.getType();
				parameters.add(parameter);	
			}			
		}
		
		
		return parameters;
	}
	

	protected static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); 
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit); // set source
		parser.setResolveBindings(true); // we need bindings later on
		return (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
	}
	
	protected static Name createQualifiedName(AST ast, String classToImport) {
		String[] parts = classToImport.split("\\."); //$NON-NLS-1$

		Name name = null;

		for (int i = 0; i < parts.length; i++) {
			SimpleName simpleName = ast.newSimpleName(parts[i]);
			if (i == 0) {
				name = simpleName;
			} else {
				name = ast.newQualifiedName(name, simpleName);
			}
		}
		return name;
	}
}
