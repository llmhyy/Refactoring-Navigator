/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities.util;

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
	
	/**
	 * This method can also modify the program model by building a new reference of parameter-access type when 
	 * we find some new parameters need to be added to this method.
	 * @param sourceUnit
	 * @param objMethod
	 * @param newModel
	 * @return
	 */
	public static ArrayList<ProgramReference> findTheReferingCalleeMemberInSourceUnit(ICompilationUnitWrapper sourceUnit, ICompilationUnitWrapper targetUnit, MethodWrapper objMethod, ProgramModel newModel){
		ArrayList<ProgramReference> calleeReferenceList = new ArrayList<>();
		
		for(ProgramReference reference: objMethod.getRefereePointList()){
			LowLevelGraphNode calleeNode = reference.getReferee();
			if(calleeNode instanceof UnitMemberWrapper){
				UnitMemberWrapper calleeMember = (UnitMemberWrapper)calleeNode;
				if(calleeMember.getUnitWrapper().equals(sourceUnit)){
					if(calleeMember instanceof MethodWrapper){
						calleeReferenceList.add(reference);						
					}
					else if(calleeMember instanceof FieldWrapper){
						FieldWrapper fieldWrapper = (FieldWrapper)calleeMember;
						if(!fieldWrapper.getFieldType().equals(targetUnit)){
							calleeReferenceList.add(reference);		
						}
					}
				}
			}
		}
		
		if(calleeReferenceList.size() > 0){
			/**
			 * modify program model
			 */
			ProgramReference reference = new ProgramReference(objMethod, sourceUnit, objMethod.getJavaElement(), 
					ProgramReference.PARAMETER_ACCESS, new ArrayList<ReferenceInflucencedDetail>());
			objMethod.addProgramReferee(reference);
			sourceUnit.addProgramReferer(reference);
			newModel.getReferenceList().add(reference);
		}
		
		return calleeReferenceList;
	}
	

	public static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); 
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit); // set source
		parser.setResolveBindings(true); // we need bindings later on
		return (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
	}
	
	public static Name createQualifiedName(AST ast, String classToImport) {
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
