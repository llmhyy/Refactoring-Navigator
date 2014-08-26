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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.ltk.core.refactoring.CreateChangeOperation;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.RefactoringPrecondition;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.util.RefactoringOppUtil;
import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.programmodel.FieldWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.MethodWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.ProgramReference;
import reflexactoring.diagram.bean.programmodel.ReferenceInflucencedDetail;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.bean.programmodel.VariableDeclarationWrapper;
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

		for(ProgramReference reference: objMethod.getRefereePointList()){
			if(reference.getReferenceType() == ProgramReference.FIELD_ACCESS){
				FieldWrapper fieldWrapper = (FieldWrapper)reference.getReferee();
				ICompilationUnitWrapper fieldType = fieldWrapper.getFieldType();
				if(fieldType != null && fieldType.equals(tarUnit)){
					
					for(ProgramReference ref: objMethod.getRefererPointList()){
						UnitMemberWrapper member = ref.getReferer();
						
						ProgramReference pr = new ProgramReference(member, fieldWrapper, null, 
								ProgramReference.FIELD_ACCESS, ref.getVariableDeclarationList());
						member.addProgramReferee(pr);
						fieldWrapper.addProgramReferer(pr);
						newModel.getReferenceList().add(pr);
					}
					
				}
			}
		}
		
		/**
		 * change the parameters of method
		 */
		ArrayList<String> newParameters = RefactoringOppUtil.extractParameters(originalUnit, objMethod, newModel);
		objMethod.getParameters().addAll(newParameters);
		objMethod.removeParameter(tarUnit);
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		this.objectMethod = objMethod;
		this.targetUnit = tarUnit;
		
		return newModel;
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
		CompilationUnit sourceCompilationUnit = moveMethodOpportunity.getSourceUnit().getJavaUnit();
		CompilationUnit targetCompilationUnit = moveMethodOpportunity.getTargetUnit().getJavaUnit();
		TypeDeclaration sTypeDeclaration = (TypeDeclaration) sourceCompilationUnit.types().get(0);
		TypeDeclaration tTypeDeclaration = (TypeDeclaration) targetCompilationUnit.types().get(0);
		
		Map<MethodInvocation, MethodDeclaration> additionalMethodsToBeMoved = new HashMap<MethodInvocation, MethodDeclaration>();
//		for(ProgramReference reference : moveMethodOpportunity.getObjectMethod().getRefererPointList()){
//			additionalMethodsToBeMoved.put((MethodInvocation)reference.getASTNode(), (MethodDeclaration)((UnitMemberWrapper) reference.getReferee()).getJavaElement());
//			if(!additionalMethodsToBeMoved.containsKey((MethodInvocation)reference.getASTNode())){
//				additionalMethodsToBeMoved.put((MethodInvocation)reference.getASTNode(), methodDeclaration);
//			}
//		}
		
		MoveMethodRefactoring refactoring = new MoveMethodRefactoring(sourceCompilationUnit, targetCompilationUnit,
				sTypeDeclaration, tTypeDeclaration, methodDeclaration,
				additionalMethodsToBeMoved, false, moveMethodOpportunity.getObjectMethod().getName());
		
//		try {
//			NullProgressMonitor monitor = new NullProgressMonitor();
//			RefactoringStatus status = refactoring.checkAllConditions(monitor);
//			CreateChangeOperation operation = new CreateChangeOperation(refactoring);			
//			performOperation = new PerformChangeOperation(operation);
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
			op.run(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), titleForFailedChecks); 
		} catch(InterruptedException e1) {
			e1.printStackTrace();
		}
		
		return true;
		
		
		//TODO refresh model (get new method name first)
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
	protected boolean checkLegal() {
		// TODO Auto-generated method stub
		return false;
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
					if(method.isLegalMethodToBeMoved()){
						for(ICompilationUnitWrapper targetUnit: model.getScopeCompilationUnitList()){
							
							if(method.toString().contains("updateMap") && targetUnit.toString().contains("Map")){
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
}
