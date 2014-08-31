/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities.util;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.programmodel.DeclarationInfluencingDetail;
import reflexactoring.diagram.bean.programmodel.FieldWrapper;
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
public class RefactoringOppUtil {
	
	/**
	 * Check all conditions including client unit, source unit and target unit.
	 * variableDeclaration is the variable declaration influencing the moved method, for example,
	 * 
	 * T t;
	 * m_moved([T t]){
	 *  ...
	 * }
	 * 
	 * @param newModel
	 * @param objMember
	 * @param tarUnit
	 * @param variableDeclaration
	 */
	public static void changeTheReferenceInClientCode(ProgramModel newModel, UnitMemberWrapper objMember, ICompilationUnitWrapper tarUnit,
			ICompilationUnitWrapper sourceUnit,	VariableDeclarationWrapper variableDeclaration, FieldWrapper correspField) {
		for(ProgramReference reference: objMember.getRefererPointList()){
			UnitMemberWrapper callerMember = reference.getReferer();
			ICompilationUnitWrapper callerUnit = callerMember.getUnitWrapper();
			if(variableDeclaration.isField()){
				/**
				 * The caller is in source unit or client unit, a new reference to the field is necessary.
				 */
				if(!callerUnit.equals(tarUnit)){
					FieldWrapper fieldWrapper = (correspField != null) ? correspField : variableDeclaration.findCorrespondingFieldWrapper();
					ProgramReference newRef = new ProgramReference(callerMember, fieldWrapper, null, ProgramReference.FIELD_ACCESS);
					callerMember.addProgramReferee(newRef);
					fieldWrapper.addProgramReferer(newRef);
					newModel.getReferenceList().add(newRef);
					
					/**
					 * the caller is inside the source unit
					 */
					if(callerUnit.equals(sourceUnit)){
						/**
						 * This declaration influence the reference (with access_object type) from caller to the object member.
						 */
						variableDeclaration.getInfluencedReferenceList().
							add(new DeclarationInfluencingDetail(reference, DeclarationInfluencingDetail.ACCESS_OBJECT));
						reference.getVariableDeclarationList().
							add(new ReferenceInflucencedDetail(variableDeclaration, DeclarationInfluencingDetail.ACCESS_OBJECT));
						
						/**
						 * This declaration influence the reference (with access_object type) from caller to field declaration.
						 */
						variableDeclaration.getInfluencedReferenceList().
							add(new DeclarationInfluencingDetail(newRef, DeclarationInfluencingDetail.ACCESS_OBJECT));
						newRef.getVariableDeclarationList().
							add(new ReferenceInflucencedDetail(variableDeclaration, DeclarationInfluencingDetail.ACCESS_OBJECT));
					}
				}
				/**
				 * The caller is in target unit, for example, 
				 * S s;
				 * m_t(){
				 *  s.m_moved()
				 * }
				 * 
				 * the code need to be changed into:
				 * S s;
				 * m_t(){
				 *  m_moved()
				 * }
				 */
				else{
					ArrayList<VariableDeclarationWrapper> list 
						= reference.findVariableDeclaratoins(DeclarationInfluencingDetail.ACCESS_OBJECT);
					if(list.size() != 0){
						VariableDeclarationWrapper dec = list.get(0);
						dec.removeReference(reference, DeclarationInfluencingDetail.ACCESS_OBJECT);
						reference.removeDominantDeclaration(dec, DeclarationInfluencingDetail.ACCESS_OBJECT);
					}
					
					//System.currentTimeMillis();
				}
				
			}
			else if(variableDeclaration.isParameter()){
				modifyRefererBasedOnParameterModification(reference, tarUnit);			
			}
		}
	}
	
	/**
	 * In the referer of this method, change the parameter to access_object.
	 */
	private static void modifyRefererBasedOnParameterModification(ProgramReference reference,
			ICompilationUnitWrapper targetUnit) {
		double bestSim = 0;
		ReferenceInflucencedDetail refDetail0 = null;
		DeclarationInfluencingDetail decDetail0 = null;
		
		for(ReferenceInflucencedDetail refDetail: reference.getVariableDeclarationList()){
			if(refDetail.getType() == DeclarationInfluencingDetail.PARAMETER){
				ICompilationUnitWrapper paramType = refDetail.getDeclaration().getVariableType();
				
				double sim = paramType.computeSimilarityWith(targetUnit);
				if(sim >= bestSim){
					bestSim = sim;
					refDetail0 = refDetail;
					
					for(DeclarationInfluencingDetail decDetail: refDetail.getDeclaration().getInfluencedReferenceList()){
						if(decDetail.getReference() == reference){
							decDetail0 = decDetail;
							break;
						}
					}
				}
			}
		}
		
		if(refDetail0 != null){
			refDetail0.setType(DeclarationInfluencingDetail.ACCESS_OBJECT);
			decDetail0.setType(DeclarationInfluencingDetail.ACCESS_OBJECT);					
		}
	}
	
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
						if(!targetUnit.equals(fieldWrapper.getFieldType())){
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

	/**
	 * find the corresponding node of old nodeInfo in the new compilationUnit
	 * 
	 * @param compilationUnit
	 * @param node
	 * @return
	 */
	public static ASTNode findCorrespondingNode(CompilationUnit compilationUnit,
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
}
