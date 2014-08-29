/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import gr.uom.java.jdeodorant.refactoring.manipulators.MoveMethodRefactoring;
import gr.uom.java.jdeodorant.refactoring.views.MyRefactoringWizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.smelldetection.NameGernationCounter;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.RefactoringPrecondition;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.util.RefactoringOppUtil;
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
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class MoveMethodOpportunity extends RefactoringOpportunity {

	private UnitMemberWrapper objectMethod;
	private ICompilationUnitWrapper targetUnit;
	private ICompilationUnitWrapper sourceUnit;

	//private PerformChangeOperation performOperation;
	
	public MoveMethodOpportunity(UnitMemberWrapper objectMethod, ICompilationUnitWrapper targetUnit){
		this.objectMethod = objectMethod;
		this.targetUnit = targetUnit;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Move method ");
		buffer.append(this.objectMethod.getName() + " from ");
		if(this.sourceUnit != null){
			buffer.append(this.sourceUnit.getName());			
		}
		buffer.append(" to " + this.targetUnit.getName());
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof MoveMethodOpportunity){
			MoveMethodOpportunity thatOpp = (MoveMethodOpportunity)obj;
			if(thatOpp.getObjectMethod().equals(getObjectMethod()) &&
					thatOpp.getTargetUnit().equals(getTargetUnit())){
				return true;
			}
		}
		
		return false;
	} 
	
	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		
		/**
		 * TODO, if the re-implementation of extracting class is done, the following code could
		 * be removed.
		 * 
		 * remove some exclusive opportunities,
		 */
		Iterator<RefactoringOpportunity> oppIter = newModel.getOneShotOpportnityList().iterator();
		while(oppIter.hasNext()){
			RefactoringOpportunity opp = oppIter.next();
			if(opp instanceof ExtractClassOpportunity){
				ExtractClassOpportunity extractClassOpp = (ExtractClassOpportunity)opp;
				
				for(UnitMemberWrapper extractedMember: extractClassOpp.getToBeExtractedMembers()){
					if(extractedMember.equals(this.objectMethod)){
						oppIter.remove();
						break;
					}
				}
			}
		}
		
		MethodWrapper objMethod = (MethodWrapper)newModel.findMember(this.objectMethod);
		ICompilationUnitWrapper tarUnit = newModel.findUnit(this.targetUnit.getFullQualifiedName());
		
		/**
		 * change containing relations
		 */
		ICompilationUnitWrapper originalUnit = objMethod.getUnitWrapper();
		
		this.sourceUnit = originalUnit;
		
		originalUnit.getMembers().remove(objMethod);
		objMethod.setUnitWrapper(tarUnit);
		tarUnit.addMember(objMethod);
		
		System.currentTimeMillis();
		
		/**
		 * change the parameters of method
		 */
		ArrayList<ProgramReference> referenceList = RefactoringOppUtil.
				findTheReferingCalleeMemberInSourceUnit(originalUnit, tarUnit, objMethod, newModel);
		if(referenceList.size() > 0){
			objMethod.getParameters().add(originalUnit.getName());			
		}
		objMethod.removeParameter(tarUnit);
		
		modifyTheReferenceOfSourceUnit(originalUnit, objMethod, referenceList, newModel);
		
		VariableDeclarationWrapper variableDeclaration = modifyTheReferenceOfTargetUnit(newModel, objMethod, tarUnit);
		
		changeTheReferenceInClientCode(newModel, objMethod, tarUnit, originalUnit, variableDeclaration);
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		this.objectMethod = objMethod;
		this.targetUnit = tarUnit;
		
		return newModel;
	}
	
	
	
	/**
	 * @param originalUnit
	 * @param calleeMemberList
	 */
	private void modifyTheReferenceOfSourceUnit(ICompilationUnitWrapper originalUnit, MethodWrapper objMethod,
			ArrayList<ProgramReference> referenceList, ProgramModel newModel) {
		String variableName = ReflexactoringUtil.lowercaseFirstCharacterOfString(originalUnit.getName());
		String key = originalUnit.getFullQualifiedName() + "." + objMethod.getName() + ".(parameter)" + NameGernationCounter.retrieveNumber();
		VariableDeclarationWrapper dec = new VariableDeclarationWrapper(originalUnit, variableName, null, key, false, true);
		
		for(ProgramReference ref: referenceList){
			DeclarationInfluencingDetail decDetail = new DeclarationInfluencingDetail(ref, DeclarationInfluencingDetail.ACCESS_OBJECT);
			dec.getInfluencedReferenceList().add(decDetail);
			
			ReferenceInflucencedDetail refDetail = new ReferenceInflucencedDetail(dec, DeclarationInfluencingDetail.ACCESS_OBJECT);
			ref.getVariableDeclarationList().add(refDetail);
		}
		
		newModel.getDeclarationList().add(dec);
	}

	/**
	 * Given that
	 * A a;
	 * m(){
	 * 	a.m1();
	 * }
	 * 
	 * the field access relation from m() to field a should be removed after m() is moved to A.
	 * 
	 * @param newModel
	 * @param objMethod
	 * @param tarUnit
	 * @return
	 */
	public VariableDeclarationWrapper modifyTheReferenceOfTargetUnit(
			ProgramModel newModel, MethodWrapper objMethod,
			ICompilationUnitWrapper tarUnit) {
		
		
		
		
		VariableDeclarationWrapper variableDeclaration = findRootCauseVariableDeclarationForMovingThisMethod(objMethod, tarUnit);
		ArrayList<ProgramReference> refereeList = 
				findInflucencedReferenceInObjectMethodWithAccessObjectType(objMethod, variableDeclaration);
		removeTheInfluenceRelationBetweenReferenceAndDelcaration(variableDeclaration, refereeList);
		if(variableDeclaration.isField()){
			removeCorrespondingFieldAccessProgramReference(newModel, objMethod, variableDeclaration);
		}
		
		//System.currentTimeMillis();
		
		return variableDeclaration;
	}

	/**
	 * Given that
	 * A a;
	 * m(){
	 * 	a.m1();
	 * }
	 * 
	 * the field access relation from m() to field a should be removed after m() is moved to A.
	 * 
	 * @param objMethod
	 * @param variableDeclaration
	 */
	private void removeCorrespondingFieldAccessProgramReference(
			ProgramModel model, MethodWrapper objMethod,
			VariableDeclarationWrapper variableDeclaration) {
		FieldWrapper field = variableDeclaration.findCorrespondingFieldWrapper();
		
		Iterator<ProgramReference> prIter = model.getReferenceList().iterator();
		while(prIter.hasNext()){
			ProgramReference pr = prIter.next();
			if(pr.getReferer().equals(objMethod) && pr.getReferee().equals(field)){
				objMethod.removeReferee(pr);
				field.removeReferer(pr);
				prIter.remove();
			}
		}
		
		System.currentTimeMillis();
		
	}

	/**
	 * 
	 */
	private void removeTheInfluenceRelationBetweenReferenceAndDelcaration(VariableDeclarationWrapper variableDeclaration, 
			ArrayList<ProgramReference> refereeList) {
		for(ProgramReference referee: refereeList){
			referee.removeDominantDeclaration(variableDeclaration, DeclarationInfluencingDetail.ACCESS_OBJECT);
			variableDeclaration.removeReference(referee, DeclarationInfluencingDetail.ACCESS_OBJECT);
		}
	}
	
	

	/**
	 * This method will find all the variable declaration with the type of target unit and return the one which
	 * is invoked the most times.
	 * 
	 * For example, given the following code, this method will return the variable declaration "A a2" as the root
	 * cause for moving this method to type A.
	 * A a1;
	 * A a2;
	 * 
	 * m(){
	 *  a1.m1();
	 *  a2.m2();
	 *  a2.m3();
	 * }
	 * 
	 * 
	 * @param tarUnit
	 * @return
	 */
	private VariableDeclarationWrapper findRootCauseVariableDeclarationForMovingThisMethod(MethodWrapper objMethod,
			ICompilationUnitWrapper tarUnit) {
		/**
		 * find all the variable declaration with type tarUnit
		 */
		HashMap<VariableDeclarationWrapper, Integer> map = new HashMap<>();
		for(ProgramReference reference: objMethod.getRefereePointList()){
			LowLevelGraphNode node = reference.getReferee();
			if(node instanceof UnitMemberWrapper){
				UnitMemberWrapper member = (UnitMemberWrapper)node;
				if(member.getUnitWrapper().equals(tarUnit)){
					
					ArrayList<VariableDeclarationWrapper> list 
						= reference.findVariableDeclaratoins(DeclarationInfluencingDetail.ACCESS_OBJECT);
					if(list.size() != 0){
						VariableDeclarationWrapper vdw = list.get(0);
						Integer value = map.get(vdw);
						if(value == null){
							map.put(vdw, 1);
						}
						else{
							value++;
							map.put(vdw, value);
						}
					}
				}
			}
		}
		
		/**
		 * choose the one invoked most times.
		 */
		VariableDeclarationWrapper vdw = null;
		int value = 0;
		for(VariableDeclarationWrapper dec: map.keySet()){
			if(vdw == null){
				vdw = dec;
				value = map.get(dec);
			}
			else{
				if(map.get(dec) > value){
					vdw = dec;
					value = map.get(dec);
				}
			}
		}
		
		return vdw;
	}

	/**
	 * @param newModel
	 * @param objMethod
	 * @param tarUnit
	 * @param variableDeclaration
	 */
	public void changeTheReferenceInClientCode(ProgramModel newModel, MethodWrapper objMethod, ICompilationUnitWrapper tarUnit,
			ICompilationUnitWrapper sourceUnit,	VariableDeclarationWrapper variableDeclaration) {
		for(ProgramReference reference: objMethod.getRefererPointList()){
			UnitMemberWrapper callerMember = reference.getReferer();
			if(variableDeclaration.isField()){
				/**
				 * the caller is inside the source unit
				 */
				if(callerMember.getUnitWrapper().equals(sourceUnit)){
					variableDeclaration.getInfluencedReferenceList().
						add(new DeclarationInfluencingDetail(reference, DeclarationInfluencingDetail.ACCESS_OBJECT));
					reference.getVariableDeclarationList().
						add(new ReferenceInflucencedDetail(variableDeclaration, DeclarationInfluencingDetail.ACCESS_OBJECT));
				}
				/**
				 * no matter the caller is inside or outside the source unit, a new reference to the field is necessary.
				 */
				FieldWrapper fieldWrapper = variableDeclaration.findCorrespondingFieldWrapper();
				ProgramReference newRef = new ProgramReference(callerMember, fieldWrapper, null, ProgramReference.FIELD_ACCESS);
				callerMember.addProgramReferee(newRef);
				fieldWrapper.addProgramReferer(newRef);
				newModel.getReferenceList().add(newRef);
			}
			else if(variableDeclaration.isParameter()){
				modifyRefererBasedOnParameterModification(reference, tarUnit);
			}
		}
	}
	
	/**
	 * In the referer of this method, change the parameter to access_object.
	 */
	private void modifyRefererBasedOnParameterModification(ProgramReference reference,
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
	
	/**
	 * Find all the method invocation and field access in object method, which are called in terms of access-object
	 * with the type of target unit.
	 * 
	 * A a; => A is the target unit
	 * 
	 * in a method:
	 * m([A a]){
	 *   a.k;
	 *   a.foo();
	 *   b.goo();
	 * }
	 * 
	 * Given a variable declaration (A a) and a object method (m()), this method will return the program reference such
	 * as "a.k" and "a.foo()".
	 * @param objMethod
	 * @param variableDeclaration
	 * @return
	 */
	private ArrayList<ProgramReference> findInflucencedReferenceInObjectMethodWithAccessObjectType(
			MethodWrapper objMethod, VariableDeclarationWrapper variableDeclaration) {
		ArrayList<ProgramReference> refList = new ArrayList<>();
		for(DeclarationInfluencingDetail decDetail: variableDeclaration.getInfluencedReferenceList()){
			if(decDetail.getType() == DeclarationInfluencingDetail.ACCESS_OBJECT){
				ProgramReference reference = decDetail.getReference();
				if(reference.getReferer().equals(objMethod)){
					refList.add(reference);
				}
						
			}
		}
		
		return refList;
	}

	

	@Override
	public double computeSimilarityWith(RefactoringOpportunity opp){
		if(opp instanceof MoveMethodOpportunity){
			MoveMethodOpportunity moveMethodOpp = (MoveMethodOpportunity)opp;
			double targetUnitSim = getTargetUnit().computeSimilarityWith(moveMethodOpp.getTargetUnit());
			double sourceUnitSim = getSourceUnit().computeSimilarityWith(moveMethodOpp.getSourceUnit());
			double objMethodSim = getObjectMethod().computeSimilarityWith(moveMethodOpp.getObjectMethod());
			
			return (targetUnitSim + sourceUnitSim + objMethodSim)/3;
		}
		
		return 0;
	}
	
	@Override
	public ArrayList<ASTNode> getHints() {
		ArrayList<ASTNode> hints = new ArrayList<>();
		hints.add(objectMethod.getJavaElement());
		for(ProgramReference reference: objectMethod.getRefererPointList()){
			ASTNode node = reference.getASTNode();
			hints.add(node);
		}
		return hints;
	}

	@Override
	public boolean apply(int position, RefactoringSequence sequence) {
		MoveMethodOpportunity moveMethodOpportunity = this;
		
		MethodDeclaration methodDeclaration = (MethodDeclaration) moveMethodOpportunity.getObjectMethod().getJavaElement();	
		
		CompilationUnit sourceCompilationUnit = null; 
		CompilationUnit targetCompilationUnit = null;
		try {
			IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
			project.open(null);
			IJavaProject javaProject = JavaCore.create(project);
			
			IType sourceType = javaProject.findType(moveMethodOpportunity.getSourceUnit().getFullQualifiedName());
			ICompilationUnit sourceUnit = sourceType.getCompilationUnit();
			sourceCompilationUnit = RefactoringOppUtil.parse(sourceUnit);
			
			IType targetType = javaProject.findType(moveMethodOpportunity.getTargetUnit().getFullQualifiedName());
			ICompilationUnit targetUnit = targetType.getCompilationUnit();
			targetCompilationUnit = RefactoringOppUtil.parse(targetUnit);
		} catch (CoreException e) {
			e.printStackTrace();
		}		
		if(sourceCompilationUnit == null || targetCompilationUnit == null){
			return false;
		}
		
//		CompilationUnit sourceCompilationUnit = moveMethodOpportunity.getSourceUnit().getJavaUnit(); 
//		CompilationUnit targetCompilationUnit = moveMethodOpportunity.getTargetUnit().getJavaUnit();
		TypeDeclaration sTypeDeclaration = (TypeDeclaration) sourceCompilationUnit.types().get(0);
		TypeDeclaration tTypeDeclaration = (TypeDeclaration) targetCompilationUnit.types().get(0);
		
		Map<MethodInvocation, MethodDeclaration> additionalMethodsToBeMoved = new HashMap<MethodInvocation, MethodDeclaration>();

		methodDeclaration = (MethodDeclaration)sourceCompilationUnit.findDeclaringNode(methodDeclaration.resolveBinding().getKey());
		
		MoveMethodRefactoring refactoring = new MoveMethodRefactoring(sourceCompilationUnit, targetCompilationUnit,
				sTypeDeclaration, tTypeDeclaration, methodDeclaration,
				additionalMethodsToBeMoved, false, moveMethodOpportunity.getObjectMethod().getName());
		
//		try {
//			NullProgressMonitor monitor = new NullProgressMonitor();
//			RefactoringStatus status = refactoring.checkAllConditions(monitor);
//			CreateChangeOperation operation = new CreateChangeOperation(refactoring);			
//			PerformChangeOperation performOperation = new PerformChangeOperation(operation);
//			performOperation.run(monitor);
//		} catch (OperationCanceledException e1) {
//			e1.printStackTrace();
//			return false;
//		} catch (CoreException e1) {
//			e1.printStackTrace();
//			return false;
//		} catch (Exception e){
//			e.printStackTrace();
//			return false;
//		}
		
		MyRefactoringWizard wizard = new MyRefactoringWizard(refactoring, null);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard); 
		try { 
			String titleForFailedChecks = ""; //$NON-NLS-1$ 
			if(op.run(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), titleForFailedChecks) == IDialogConstants.CANCEL_ID){
				return false;
			}
		} catch(InterruptedException e1) {
			e1.printStackTrace();
		}

		//refresh model
		String newMethodName = refactoring.getMovedMethodName();
		refreshModel(position, sequence, newMethodName);
		
		return true;
	}
	
//	@Override
//	public boolean undoApply(){
//		if(performOperation != null){
//			try {
//				NullProgressMonitor monitor = new NullProgressMonitor();		
//				PerformChangeOperation performUndoOperation = new PerformChangeOperation(performOperation.getUndoChange());
//				performUndoOperation.run(monitor);
//			} catch (OperationCanceledException e1) {
//				e1.printStackTrace();
//				return false;
//			} catch (CoreException e1) {
//				e1.printStackTrace();
//				return false;
//			}
//		}else{
//			return false;
//		}
//		return true;
//	}

	@Override
	public boolean checkLegal() {		
		try {
			IProject project = ReflexactoringUtil.getSpecificJavaProjectInWorkspace();
			project.open(null);
			IJavaProject javaProject = JavaCore.create(project);			

			//check whether sourceUnit exists or not
			IType sourceType = javaProject.findType(sourceUnit.getFullQualifiedName());
			if(sourceType == null){
				return false;
			}
			ICompilationUnit sourceUnit = sourceType.getCompilationUnit();		
			if(sourceUnit == null){
				return false;
			}			

			//check whether targetUnit exists or not
			IType targetType = javaProject.findType(targetUnit.getFullQualifiedName());
			if(targetType == null){
				return false;
			}
			ICompilationUnit targetUnit = targetType.getCompilationUnit();		
			if(targetUnit == null){
				return false;
			}

			//check whether objectMethod exists or not
			IMethod[] objectMethods = sourceType.findMethods((IMethod) objectMethod.getJavaMember());	
			if(objectMethods == null || objectMethods.length != 1){
				return false;
			}
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * @return the objectMethod
	 */
	public UnitMemberWrapper getObjectMethod() {
		return objectMethod;
	}

	/**
	 * @param objectMethod the objectMethod to set
	 */
	public void setObjectMethod(UnitMemberWrapper objectMethod) {
		this.objectMethod = objectMethod;
	}

	/**
	 * @return the targetUnit
	 */
	public ICompilationUnitWrapper getTargetUnit() {
		return targetUnit;
	}

	/**
	 * @param targetUnit the targetUnit to set
	 */
	public void setTargetUnit(ICompilationUnitWrapper targetUnit) {
		this.targetUnit = targetUnit;
	}
	
	/**
	 * @return the sourceUnit
	 */
	public ICompilationUnitWrapper getSourceUnit() {
		return sourceUnit;
	}

	/**
	 * @param sourceUnit the sourceUnit to set
	 */
	public void setSourceUnit(ICompilationUnitWrapper sourceUnit) {
		this.sourceUnit = sourceUnit;
	}

	@Override
	public String getRefactoringName() {
		return "Move Method";
	}
	
	@Override
	public ArrayList<String> getRefactoringDetails(){
		ArrayList<String> refactoringDetails = new ArrayList<>();
		String step1 = "Move method " + this.objectMethod.getName() + " from " 
				+ this.sourceUnit.getName() + " to " + this.targetUnit.getName();
		
		refactoringDetails.add(step1);
		
		return refactoringDetails;
	};

	public class Precodition extends RefactoringPrecondition{
		/**
		 * I will check all the combinations between methods and units to identify whether there is a chance.
		 */
		@Override
		public ArrayList<RefactoringOpportunity> detectOpportunities(ProgramModel model) {
			ArrayList<RefactoringOpportunity> oppList = new ArrayList<>();
			
			for(UnitMemberWrapper member: model.getScopeMemberList()){
				if(member instanceof MethodWrapper){
					MethodWrapper method = (MethodWrapper)member;
					if(method.toString().contains("updateTableView")){
						System.currentTimeMillis();
					}
					if(method.isLegalMethodToBeMoved()){
						for(ICompilationUnitWrapper targetUnit: model.getScopeCompilationUnitList()){
							
							if(method.toString().contains("updateTableView") && targetUnit.toString().contains("Table")){
								System.currentTimeMillis();
								System.currentTimeMillis();
							}
							
							if(targetUnit.isLegalTargetClassToMoveMethodIn(method)){
								if(isFeatureEnvy(targetUnit, method)){
									MoveMethodOpportunity opp = new MoveMethodOpportunity(method, targetUnit);
									oppList.add(opp);									
								}
							}
						}						
					}
				}
			}
			
			return oppList;
		}
		
		private void updateMap(HashMap<ICompilationUnitWrapper, Double> map, ICompilationUnitWrapper calleeUnit){
			if(map.get(calleeUnit) == null){
				map.put(calleeUnit, 1.0);
				
			}
			else{
				Double freq = map.get(calleeUnit);
				freq++;
				map.put(calleeUnit, freq);
			}
		}

		private boolean isFeatureEnvy(ICompilationUnitWrapper targetUnit, MethodWrapper method){
			HashMap<ICompilationUnitWrapper, Double> map = new HashMap<>();
			
			double totalFreq = 0;
			for(ProgramReference reference: method.getRefereePointList()){
				LowLevelGraphNode calleeNode = reference.getReferee();
				if(calleeNode instanceof MethodWrapper){
					MethodWrapper calleeMember = (MethodWrapper)calleeNode;
					ICompilationUnitWrapper calleeUnit = calleeMember.getUnitWrapper();
					
					updateMap(map, calleeUnit);
					/*for(ICompilationUnitWrapper descedantUnit: calleeUnit.getAllDescedants()){
						updateMap(map, descedantUnit);
					}*/
					
					totalFreq++;					
				}
			}
			
			if(totalFreq == 0 || map.get(targetUnit) == null){
				return false;
			}
			else{
				double freq = map.get(targetUnit);
				double ratio = freq/totalFreq;
				return ratio > Settings.featureEnvyThreshold;
			}
		}
		
		
		@Override
		public boolean checkLegal(ProgramModel model) {
			MethodWrapper method = (MethodWrapper)model.findMember(objectMethod);
			ICompilationUnitWrapper unit = model.findUnit(targetUnit.getFullQualifiedName());
			
			if(method.isLegalMethodToBeMoved() && unit.isLegalTargetClassToMoveMethodIn(method)){
				return isFeatureEnvy(unit, method);				
			}
			
			return false;
		}
		
	}
	
	protected void refreshModel(int position, RefactoringSequence sequence, String newMethodName) {
		String fullQualifiedTypeName = this.objectMethod.getUnitWrapper().getFullQualifiedName();
		String toBeReplacedMethodName = this.objectMethod.getName();
		ArrayList<String> toBeReplacedMethodParam = ((MethodWrapper)this.objectMethod).getParameters();
		
		for(int i = position; i < sequence.size(); i++ ){
			ProgramModel model = sequence.get(i).getConsequenceModel();
			
			MethodWrapper oldMethodInModel = model.findMethod(fullQualifiedTypeName, toBeReplacedMethodName, toBeReplacedMethodParam);
			oldMethodInModel.setName(newMethodName);
		}
	}
}
