/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.CompilationUnitCache;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.distance.DistanceMatrix;
import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.ExtractClassCandidateRefactoring;
import gr.uom.java.distance.MySystem;
import gr.uom.java.jdeodorant.refactoring.manipulators.ExtractClassRefactoring;
import gr.uom.java.jdeodorant.refactoring.views.MyRefactoringWizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import datamining.cluster.IClusterElement;
import datamining.cluster.hierarchical.HierarchicalCluster;
import datamining.cluster.hierarchical.HierarchicalClustering;
import reflexactoring.diagram.action.smelldetection.NameGernationCounter;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequenceElement;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.RefactoringPrecondition;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.util.RefactoringOppUtil;
import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.ModuleWrapper;
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
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class ExtractClassOpportunity extends RefactoringOpportunity {

	private int id;
	private ArrayList<LowLevelGraphNode> toBeExtractedMembers = new ArrayList<>();
	private ExtractClassCandidateRefactoring refactoring;
	private ICompilationUnitWrapper sourceUnit;
	private String newFieldName;
	private String targetUnitName;
	
	private ModuleWrapper mappingModule;
	
	/**
	 * @param toBeExtractedMembers
	 */
	public ExtractClassOpportunity(
			ArrayList<LowLevelGraphNode> toBeExtractedMembers, ExtractClassCandidateRefactoring refactoring, 
			ArrayList<ModuleWrapper> moduleList, ICompilationUnitWrapper sourceUnit) {
		super();
		this.toBeExtractedMembers = toBeExtractedMembers;
		this.sourceUnit = sourceUnit;
		this.setRefactoring(refactoring);
		this.moduleList = moduleList;
	}

	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("extract the following members in " );
		if(toBeExtractedMembers.get(0) instanceof UnitMemberWrapper){
			UnitMemberWrapper member = (UnitMemberWrapper)toBeExtractedMembers.get(0);
			buffer.append(member.getUnitWrapper().getName());
		}
		else{
			ICompilationUnitWrapper innerClass = (ICompilationUnitWrapper)toBeExtractedMembers.get(0);
			buffer.append(innerClass.getOuterClass().getName());
		}
		
		buffer.append(" : ");
		for(LowLevelGraphNode member: toBeExtractedMembers){
			if(member instanceof MethodWrapper){
				buffer.append(member.getName() + "(), ");
			}
			else{
				buffer.append(member.getName() + ", ");
			}
		}
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof ExtractClassOpportunity){
			ExtractClassOpportunity thatOpp = (ExtractClassOpportunity)obj;
			if(thatOpp.isHavingSameMemberList(toBeExtractedMembers) 
					&&
					(thatOpp.getSourceUnit().getName().equals(this.getSourceUnit().getName()) || 
							(thatOpp.getSourceUnit().getName().contains("ExtractedClass") && this.getSourceUnit().getName().contains("ExtractedClass")))){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isHavingSameMemberList(ArrayList<LowLevelGraphNode> memberList){
		if(memberList.size() == toBeExtractedMembers.size()){
			for(LowLevelGraphNode thatMember: memberList){
				if(!canFindAnEqualNodeInList(toBeExtractedMembers, thatMember)){
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private boolean canFindAnEqualNodeInList(ArrayList<LowLevelGraphNode> list, LowLevelGraphNode member){
		for(LowLevelGraphNode node: list){
			if(node.getName().equals(member.getName())){
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		//TODO
		/**
		 * replace those to-be-extracted members with new ones.
		 */
		ArrayList<LowLevelGraphNode> extractMembers = new ArrayList<>();
		for(LowLevelGraphNode member: toBeExtractedMembers){
			if(member instanceof UnitMemberWrapper){
				UnitMemberWrapper newMem = newModel.findMember((UnitMemberWrapper)member);
				extractMembers.add(newMem);				
			}
			else if(member instanceof ICompilationUnitWrapper){
				ICompilationUnitWrapper oldUnit = (ICompilationUnitWrapper)member;
				ICompilationUnitWrapper newUnit = newModel.findUnit(oldUnit.getFullQualifiedName());
				extractMembers.add(newUnit);
			}
		}
		
		ArrayList<UnitMemberWrapper> delegateMethods = identifyDelegatingMethods(extractMembers);
		
		//toBeExtractedMembers = extractMembers;
		/**
		 * create a new class named "ExtractClass**"
		 */
		LowLevelGraphNode node = extractMembers.get(0);
		String packageName = null;
		if(node instanceof UnitMemberWrapper){
			packageName = ((UnitMemberWrapper)node).getUnitWrapper().getPackageName();
		}
		else if(node instanceof ICompilationUnitWrapper){
			packageName = ((ICompilationUnitWrapper)node).getPackageName();
		}
		ICompilationUnitWrapper newTargetUnit = new ICompilationUnitWrapper(null, false, "ExtractedClass"+NameGernationCounter.retrieveNumber(), 
				packageName, new HashMap<String, Integer>(), "", false, ModifierWrapper.PUBLIC);
		newModel.getAllTheTypesInScope().add(newTargetUnit);
		this.targetUnitName = newTargetUnit.getFullQualifiedName();
		
		ICompilationUnitWrapper newSourceUnit = newModel.findUnit(this.sourceUnit.getFullQualifiedName());
		FieldWrapper newField = null;
		if(isSomeUnitMemberNonStatic(extractMembers)){		
			/**
			 * create a new field in source class
			 */
			newField = new FieldWrapper("extractedClass"+NameGernationCounter.retrieveNumber(), newTargetUnit.getName(), 
					newSourceUnit, null, "", null, false, false, ModifierWrapper.PUBLIC);
			newSourceUnit.getMembers().add(newField);
			newModel.getScopeMemberList().add(newField);
			this.newFieldName = newField.getName();
			
			/**
			 * create the relation indicating field has the type of newly created class
			 */
			ProgramReference ref = new ProgramReference(newField, newTargetUnit, null, ProgramReference.TYPE_DECLARATION, new ArrayList<ReferenceInflucencedDetail>());
			newField.addProgramReferee(ref);
			newTargetUnit.addProgramReferer(ref);
			newModel.getReferenceList().add(ref);			
			
			
			/**
			 * create type relation
			 */
			ProgramReference programReference = new ProgramReference(newField, newTargetUnit, null, 
					ProgramReference.NEW_DEFAULT_CONSTRUCTOR, new ArrayList<ReferenceInflucencedDetail>());
			newField.addProgramReferee(programReference);
			newTargetUnit.addProgramReferer(programReference);
			newModel.getReferenceList().add(programReference);
		}
		
		moveExtractMembersToTargetUnit(extractMembers, newModel, newTargetUnit, newSourceUnit); 
		
		createDelegatingMethods(newModel, delegateMethods, newTargetUnit, newSourceUnit);

		if(newField != null){
			buildVariableDeclarationRelations(newModel, extractMembers, newTargetUnit, newSourceUnit, newField);			
		}
		
		//System.currentTimeMillis();
		newModel.updateUnitCallingRelationByMemberRelations();
		newModel.updateUnitDescription();

		ModuleWrapper bestModule = calculateBestMappingModule(newModel, newTargetUnit);
		newTargetUnit.setMappingModule(bestModule);
		this.mappingModule = bestModule;
		
		return newModel;
	}
	
//	@Override
//	public ProgramModel simulate(ProgramModel model) {
//		ProgramModel newModel = model.clone();
//		/**
//		 * replace those to-be-extracted members with new ones.
//		 */
//		ArrayList<UnitMemberWrapper> extractMembers = new ArrayList<>();
//		for(UnitMemberWrapper member: toBeExtractedMembers){
//			UnitMemberWrapper newMem = newModel.findMember(member);
//			extractMembers.add(newMem);
//		}
//		
//		ArrayList<UnitMemberWrapper> delegateMethods = identifyDelegatingMethods(extractMembers);
//		
//		//toBeExtractedMembers = extractMembers;
//		/**
//		 * create a new class named "ExtractClass**"
//		 */
//		ICompilationUnitWrapper newTargetUnit = new ICompilationUnitWrapper(null, false, "ExtractedClass"+NameGernationCounter.retrieveNumber(), 
//				extractMembers.get(0).getUnitWrapper().getPackageName(), new HashMap<String, Integer>(), "", false, ModifierWrapper.PUBLIC);
//		newModel.getAllTheTypesInScope().add(newTargetUnit);
//		this.targetUnitName = newTargetUnit.getFullQualifiedName();
//		
//		/**
//		 * create a new field in source class
//		 */
//		ICompilationUnitWrapper newSourceUnit = newModel.findUnit(this.sourceUnit.getFullQualifiedName());
//		FieldWrapper newField = new FieldWrapper("extractedClass"+NameGernationCounter.retrieveNumber(), newTargetUnit.getName(), 
//				newSourceUnit, null, "", null, false, ModifierWrapper.PUBLIC);
//		newSourceUnit.getMembers().add(newField);
//		newModel.getScopeMemberList().add(newField);
//		this.newFieldName = newField.getName();
//		
//		/**
//		 * create the relation indicating field has the type of newly created class
//		 */
//		ProgramReference ref = new ProgramReference(newField, newTargetUnit, null, ProgramReference.TYPE_DECLARATION, new ArrayList<ReferenceInflucencedDetail>());
//		newField.addProgramReferee(ref);
//		newTargetUnit.addProgramReferer(ref);
//		newModel.getReferenceList().add(ref);
//		
//		/**
//		 * create type relation
//		 */
//		ProgramReference programReference = new ProgramReference(newField, newTargetUnit, null, 
//				ProgramReference.NEW_DEFAULT_CONSTRUCTOR, new ArrayList<ReferenceInflucencedDetail>());
//		newField.addProgramReferee(programReference);
//		newTargetUnit.addProgramReferer(programReference);
//		newModel.getReferenceList().add(programReference);
//		
//		moveExtractMembersToTargetUnit(extractMembers, newModel, newTargetUnit, newSourceUnit); 
//		
//		createDelegatingMethods(newModel, delegateMethods, newTargetUnit, newSourceUnit);
//
//		buildVariableDeclarationRelations(newModel, extractMembers, newTargetUnit, newSourceUnit, newField);
//		
//		System.currentTimeMillis();
//		newModel.updateUnitCallingRelationByMemberRelations();
//
//		ModuleWrapper bestModule = calculateBestMappingModule(newModel, newTargetUnit);
//		newTargetUnit.setMappingModule(bestModule);
//		this.mappingModule = bestModule;
//		
//		
//		/**
//		 * remove some exclusive opportunities
//		 */
//		Iterator<RefactoringOpportunity> oppIter = newModel.getOneShotOpportnityList().iterator();
//		while(oppIter.hasNext()){
//			RefactoringOpportunity opp = oppIter.next();
//			if(opp instanceof ExtractClassOpportunity){
//				ExtractClassOpportunity extractClassOpp = (ExtractClassOpportunity)opp;
//				
//				if(extractClassOpp.getId() == this.getId()){
//					oppIter.remove();
//				}
//			}
//		}
//		
//		return newModel;
//	}

	/**
	 * @param extractMembers
	 * @return
	 */
	private boolean isSomeUnitMemberNonStatic(
			ArrayList<LowLevelGraphNode> extractMembers) {
		for(LowLevelGraphNode node: extractMembers){
			if(node instanceof UnitMemberWrapper){
				UnitMemberWrapper member = (UnitMemberWrapper)node;
				if(!member.isStatic()){
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * @param newModel
	 * @param extractMembers
	 * @param newTargetUnit
	 * @param newSourceUnit
	 * @param newField
	 */
	private void buildVariableDeclarationRelations(ProgramModel newModel,
			ArrayList<LowLevelGraphNode> extractMembers,
			ICompilationUnitWrapper newTargetUnit,
			ICompilationUnitWrapper newSourceUnit, FieldWrapper newField) {
		/**
		 * create variable declaration
		 */
		VariableDeclarationWrapper variableDeclaration = new VariableDeclarationWrapper(newTargetUnit, newField.getName(), 
				null, newSourceUnit.getFullQualifiedName()+"."+newField.getName()+"(field)", true, false);
		newModel.getDeclarationList().add(variableDeclaration);
		
		System.currentTimeMillis();
		
		/**
		 * all the referer to the extracted members may refer to newly created field by accessing object
		 */
		for(LowLevelGraphNode node: extractMembers){
			if(node instanceof UnitMemberWrapper){
				UnitMemberWrapper newToBeExtractedMember = (UnitMemberWrapper)node;
				/*for(ProgramReference reference: newToBeExtractedMember.getRefererPointList()){
					UnitMemberWrapper referer = reference.getReferer();
					
					if(!referer.getUnitWrapper().equals(newTargetUnit)){
						ProgramReference ref = new ProgramReference(referer, newField, null, ProgramReference.FIELD_ACCESS);
						referer.addProgramReferee(ref);
						newField.addProgramReferer(ref);
						newModel.getReferenceList().add(ref);
						
						ReferenceInflucencedDetail refDetail = new ReferenceInflucencedDetail(variableDeclaration, DeclarationInfluencingDetail.ACCESS_OBJECT);
						ref.getVariableDeclarationList().add(refDetail);
						DeclarationInfluencingDetail decDetail = new DeclarationInfluencingDetail(ref, DeclarationInfluencingDetail.ACCESS_OBJECT);
						variableDeclaration.getInfluencedReferenceList().add(decDetail);
						
					}
					
				}*/
				
				if(!newToBeExtractedMember.isStatic()){
					RefactoringOppUtil.changeTheReferenceInClientCode(newModel, newToBeExtractedMember, 
							newTargetUnit, newSourceUnit, variableDeclaration, newField);					
				}
			}
			
		}
	}

	/**
	 * @param newModel
	 * @param delegateMethods
	 * @param newTargetUnit
	 * @param newSourceUnit
	 */
	private void createDelegatingMethods(ProgramModel newModel,
			ArrayList<UnitMemberWrapper> delegateMethods,
			ICompilationUnitWrapper newTargetUnit,
			ICompilationUnitWrapper newSourceUnit) {
		/**
		 * create delegating method
		 */
		for(UnitMemberWrapper member: delegateMethods){
			MethodWrapper method = (MethodWrapper)member;
			MethodWrapper newDeletegateMethod = new MethodWrapper(method.getName(), 
					method.getReturnType(), method.getParameters(), 
					method.isConstructor(), newSourceUnit, method.getTermFrequency(), 
					method.getDescription(), null, null, false, false, ModifierWrapper.PUBLIC);
			newSourceUnit.getMembers().add(newDeletegateMethod);
			newModel.getScopeMemberList().add(newDeletegateMethod);

			/**
			 * the referer of the delegated method need to refer to the delegating method
			 */
			for(ProgramReference ref: member.getRefererPointList()){
				UnitMemberWrapper referer = ref.getReferer();
				if(!(referer.getUnitWrapper().equals(newSourceUnit) && referer.getUnitWrapper().equals(newTargetUnit))){
					ProgramReference outSideRef = new ProgramReference(referer, newDeletegateMethod, null, ProgramReference.METHOD_INVOCATION, ref.getVariableDeclarationList());
					referer.addProgramReferee(outSideRef);
					newDeletegateMethod.addProgramReferer(outSideRef);
					newModel.getReferenceList().add(outSideRef);
				}
			}
			
			/**
			 * make the delegating method call newly created method
			 */
			ProgramReference newRef = new ProgramReference(newDeletegateMethod, method, null, ProgramReference.METHOD_INVOCATION);
			newDeletegateMethod.addProgramReferee(newRef);
			method.addProgramReferer(newRef);
			newModel.getReferenceList().add(newRef);
		}
	}

	/**
	 * @param extractMembers
	 * @param newTargetUnit
	 * @param newSourceUnit
	 */
	private void moveExtractMembersToTargetUnit(
			ArrayList<LowLevelGraphNode> extractMembers, ProgramModel newModel,
			ICompilationUnitWrapper newTargetUnit,
			ICompilationUnitWrapper newSourceUnit) {
		/**
		 * move those to-be-extracted members/inner classes from source unit to target unit.
		 */
		Iterator<UnitMemberWrapper> memIter = newSourceUnit.getMembers().iterator();
		while(memIter.hasNext()){
			UnitMemberWrapper member = memIter.next();
			
			if(extractMembers.contains(member)){
				/**
				 * remove the old containing relation
				 */
				//member.getUnitWrapper().getMembers().remove(member);
				memIter.remove();
				
				if(member instanceof MethodWrapper){
					/**
					 * change the parameters of method
					 */
					MethodWrapper objMethod = (MethodWrapper)member;
					ArrayList<ProgramReference> referenceList = RefactoringOppUtil.
							findTheReferingCalleeMemberInSourceUnit(newSourceUnit, newTargetUnit, objMethod, newModel);
					if(referenceList.size() > 0){
						objMethod.getParameters().add(newSourceUnit.getName());			
					}
					objMethod.removeParameter(newTargetUnit);
				}
				/**
				 * add the new containing relation
				 */
				member.setUnitWrapper(newTargetUnit);	
				newTargetUnit.addMember(member);
			}
		}
		
		/**
		 * move inner class if the new source code has any
		 */
		Iterator<ICompilationUnitWrapper> unitIter = newSourceUnit.getInnerClassList().iterator();
		while(unitIter.hasNext()){
			ICompilationUnitWrapper innerClass = unitIter.next();
			
			if(extractMembers.contains(innerClass)){
				unitIter.remove();
				ICompilationUnitWrapper newInnerClass = newModel.findUnit(innerClass.getFullQualifiedName());
				newInnerClass.setOuterClass(newTargetUnit);
				newTargetUnit.getInnerClassList().add(newInnerClass);
			}
		}
		
	}

	/**
	 * identify delegating method
	 * @param extractMembers
	 * @return
	 */
	private ArrayList<UnitMemberWrapper> identifyDelegatingMethods(
			ArrayList<LowLevelGraphNode> extractMembers) {
		ArrayList<UnitMemberWrapper> delegateMethods = new ArrayList<>();
		for(LowLevelGraphNode member: extractMembers){
			if(member instanceof MethodWrapper){
				if(((MethodWrapper)member).needDelegation()){
					delegateMethods.add((MethodWrapper)member);
				}
			}
		}
		return delegateMethods;
	}
	
	/**
	 * @return the toBeExtractedMembers
	 */
	public ArrayList<LowLevelGraphNode> getToBeExtractedMembers() {
		return toBeExtractedMembers;
	}

	/**
	 * @param toBeExtractedMembers the toBeExtractedMembers to set
	 */
	public void setToBeExtractedMembers(
			ArrayList<LowLevelGraphNode> toBeExtractedMembers) {
		this.toBeExtractedMembers = toBeExtractedMembers;
	}

	@Override
	public String getRefactoringName() {
		return "Extract Class";
	}

	@Override
	public ArrayList<String> getRefactoringDetails() {
		ArrayList<String> details = new ArrayList<>();
		details.add(this.toString());
		details.add("Moving this class to module " + getMappingModule());
		return details;
	}

	@Override
	public ArrayList<ASTNode> getHints() {
		ArrayList<ASTNode> nodeList = new ArrayList<>();
		for(LowLevelGraphNode member: toBeExtractedMembers){
			nodeList.add(member.getJavaElement());
		}
		return nodeList;
	}

	@Override
	public double computeSimilarityWith(RefactoringOpportunity opp) {
		if(opp instanceof ExtractClassOpportunity){
			ExtractClassOpportunity thatOpp = (ExtractClassOpportunity)opp;
			ArrayList<UnitMemberWrapper> thisList = new ArrayList<>();
			ArrayList<UnitMemberWrapper> thatList = new ArrayList<>();
			for(LowLevelGraphNode node: getToBeExtractedMembers()){
				if(node instanceof UnitMemberWrapper){
					thisList.add((UnitMemberWrapper)node);
				}
			}
			for(LowLevelGraphNode node: thatOpp.getToBeExtractedMembers()){
				if(node instanceof UnitMemberWrapper){
					thatList.add((UnitMemberWrapper)node);
				}
			}
			
			double sim = ReflexactoringUtil.computeSetSimilarity(thisList, thatList);
			
			return sim;
			/*double simSum = 0;
			
			ArrayList<UnitMemberWrapper> thisList = RefactoringOppUtil.copyAList(getToBeExtractedMembers());
			ArrayList<UnitMemberWrapper> thatList = RefactoringOppUtil.copyAList(thatOpp.getToBeExtractedMembers());
			
			int base = thisList.size() + thatList.size();
			
			Iterator<UnitMemberWrapper> memIter = thisList.iterator();
			while(memIter.hasNext()){
				UnitMemberWrapper thisMem = memIter.next();
				
				double bestSim = 0;
				UnitMemberWrapper bestThatMem = null;
				for(UnitMemberWrapper thatMem: thatList){
					double s = thisMem.computeSimilarityWith(thatMem);
					if(bestThatMem == null){
						bestThatMem = thatMem;
						bestSim = s;
					}
					else{
						if(bestSim < s){
							bestThatMem = thatMem;
							bestSim = s;
						}
					}
				}
				
				simSum += bestSim;
				
				memIter.remove();
				if(bestThatMem != null){
					thatList.remove(bestThatMem);					
				}
			}
			
			System.currentTimeMillis();
			
			return 2*simSum/base;*/
		}
		
		return 0;
	}

	@Override
	public boolean apply(int position, RefactoringSequence sequence) {
		IFile sourceFile = this.refactoring.getSourceIFile();
		CompilationUnit sourceCompilationUnit = (CompilationUnit)this.refactoring.getSourceClassTypeDeclaration().getRoot();
		String extractedClassName = "ExtractedClass";
		
		Set<VariableDeclaration> extractedFieldFragments = this.refactoring.getExtractedFieldFragments();
		Set<MethodDeclaration> extractedMethods = this.refactoring.getExtractedMethods();
		
		Refactoring refactoring = new ExtractClassRefactoring(sourceFile, sourceCompilationUnit,
				this.refactoring.getSourceClassTypeDeclaration(),
				extractedFieldFragments, extractedMethods,
				this.refactoring.getDelegateMethods(), extractedClassName);
		
		MyRefactoringWizard wizard = new MyRefactoringWizard(refactoring, null);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard); 
		try { 
			String titleForFailedChecks = ""; 
			int status = op.run(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), titleForFailedChecks); 
			if(status == IDialogConstants.OK_ID){
				String newExtractedName = ((ExtractClassRefactoring)refactoring).getExtractedTypeName();
				String packName = this.targetUnitName.substring(0, this.targetUnitName.lastIndexOf("."));
				
				for(int i=position; i<sequence.size(); i++){
					RefactoringSequenceElement element = sequence.get(i);
					ProgramModel model = element.getConsequenceModel();
					
					ICompilationUnitWrapper extractedUnit = model.findUnit(this.targetUnitName);
					extractedUnit.setSimpleName(newExtractedName);
					
					System.currentTimeMillis();
				}
				
				this.targetUnitName = packName + "." + newExtractedName;
				String head = "" + newExtractedName.toCharArray()[0];
				head = head.toLowerCase();
				this.newFieldName = head + newExtractedName.substring(1, newExtractedName.length());
			}
			else{
				return false;
			}
			
			
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		try {
			IJavaElement sourceJavaElement = JavaCore.create(sourceFile);
			JavaUI.openInEditor(sourceJavaElement);
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		return true;
	}

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

			//check whether to be exracted member exists or not
			for(LowLevelGraphNode member : toBeExtractedMembers){
				if(member instanceof MethodWrapper){				
					IMethod[] methods = sourceType.findMethods((IMethod) ((MethodWrapper) member).getJavaMember());	
					if(methods == null || methods.length != 1){
						return false;
					}
				}else{
					IField field = (IField) ((FieldWrapper) member).getJavaMember();
					IField[] fields = sourceType.getFields();
					boolean fieldExist = false;
					for(IField f : fields){
						if(f.getFlags() == field.getFlags() && f.getElementName().equals(field.getElementName())
								&& f.getElementType() == field.getElementType()){
							fieldExist = true;
							break;
						}
					}
					if(!fieldExist){
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
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the refactoring
	 */
	public ExtractClassCandidateRefactoring getRefactoring() {
		return refactoring;
	}

	/**
	 * @param refactoring the refactoring to set
	 */
	public void setRefactoring(ExtractClassCandidateRefactoring refactoring) {
		this.refactoring = refactoring;
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
	
	/**
	 * @return the targetUnitName
	 */
	public String getTargetUnitName() {
		return targetUnitName;
	}

	/**
	 * @param targetUnitName the targetUnitName to set
	 */
	public void setTargetUnitName(String targetUnitName) {
		this.targetUnitName = targetUnitName;
	}



	/**
	 * @return the newFieldName
	 */
	public String getNewFieldName() {
		return newFieldName;
	}

	/**
	 * @param newFieldName the newFieldName to set
	 */
	public void setNewFieldName(String newFieldName) {
		this.newFieldName = newFieldName;
	}



	/**
	 * @return the mappingModule
	 */
	public ModuleWrapper getMappingModule() {
		return mappingModule;
	}

	/**
	 * @param mappingModule the mappingModule to set
	 */
	public void setMappingModule(ModuleWrapper mappingModule) {
		this.mappingModule = mappingModule;
	}

	public class Precondition extends RefactoringPrecondition{

		@Override
		public ArrayList<RefactoringOpportunity> detectOpportunities(
				ProgramModel model) {
			ArrayList<RefactoringOpportunity> extractClassOppList = new ArrayList<>();
			for(ICompilationUnitWrapper sourceUnit: model.getOutmostTypesInScope()){
				ArrayList<IClusterElement> list = new ArrayList<>();
				list.addAll(sourceUnit.getMembers());
				list.addAll(sourceUnit.getInnerClassList());
				
				HierarchicalClustering clustering = new HierarchicalClustering(list, HierarchicalClustering.singleLinkage, 0);
				HierarchicalCluster cluster = clustering.buildDendrogram();
				
				ArrayList<HierarchicalCluster> possibleDivisions = identifyUsefulClusters(cluster);
				
				for(HierarchicalCluster cl: possibleDivisions){
					ArrayList<LowLevelGraphNode> memberList = new ArrayList<>();
					for(IClusterElement element: cl.getElements()){
						if(element instanceof UnitMemberWrapper){
							memberList.add((UnitMemberWrapper)element);
						}
						else if(element instanceof ICompilationUnitWrapper){
							memberList.add((ICompilationUnitWrapper)element);
						}
					}
					
					ExtractClassOpportunity opp = new ExtractClassOpportunity(memberList, null, moduleList, sourceUnit);
					extractClassOppList.add(opp);
				}
				
			}
			
			return extractClassOppList;
		}
		
		/**
		 * traverse the hierarchical tree to collect cluster according to certain criteria.
		 * @param cluster
		 * @return
		 */
		private ArrayList<HierarchicalCluster> identifyUsefulClusters(HierarchicalCluster cluster) {
			ArrayList<HierarchicalCluster> clusterList = new ArrayList<>();
			for(HierarchicalCluster hc: cluster.getChildren()){
				collectUsefulClusters(hc, clusterList);
			}
			
			return clusterList;
		}

		/**
		 * @param hc
		 * @param clusterList
		 */
		private void collectUsefulClusters(HierarchicalCluster hc,
				ArrayList<HierarchicalCluster> clusterList) {
			
			if(isValid(hc)){
				clusterList.add(hc);
			}
			
			if(hc.getChildren().size() > 1){
				for(HierarchicalCluster cluster: hc.getChildren()){
					collectUsefulClusters(cluster, clusterList);
				}				
			}
		}
		

		/**
		 * @param cluster
		 * @return
		 */
		private boolean isValid(HierarchicalCluster cluster) {
			boolean flag1 = cluster.size() >= 2 && cluster.size() <= 4;
			//boolean flag2 = isAllMethodNonAbstract(cluster);
			boolean flag3 = isContainPrivateFieldUsedByElementsNotInCluster(cluster);
			boolean flag4 = isContainOverridingMethod(cluster);
			boolean flag5 = isContainMethodCallingSuperMember(cluster);
			
			return flag1 /*&& flag2*/ && !flag3 && !flag4 && !flag5;
		}

		/**
		 * @param cluster
		 * @return
		 */
		private boolean isContainMethodCallingSuperMember(
				HierarchicalCluster cluster) {
			for(IClusterElement element: cluster.getElements()){
				if(element instanceof MethodWrapper){
					MethodWrapper method = (MethodWrapper)element;
					
					ICompilationUnitWrapper superClass = method.getUnitWrapper().getSuperClass();
					if(superClass != null){
						for(ProgramReference reference: method.getRefereePointList()){
							LowLevelGraphNode node = reference.getReferee();
							if(node instanceof UnitMemberWrapper){
								if(superClass.getMembers().contains(node)){
									return true;
								}
							}
						}
					}
				}
			}
			return false;
		}

		/**
		 * @param cluster
		 * @return
		 */
		private boolean isContainOverridingMethod(HierarchicalCluster cluster) {
			for(IClusterElement element: cluster.getElements()){
				if(element instanceof MethodWrapper){
					MethodWrapper method = (MethodWrapper)element;
					if(method.getOverridedMethod() != null){
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * @param cluster
		 * @return
		 */
		private boolean isContainPrivateFieldUsedByElementsNotInCluster(HierarchicalCluster cluster) {
			for(IClusterElement element: cluster.getElements()){
				if(element instanceof FieldWrapper){
					FieldWrapper field = (FieldWrapper)element;
					if(field.getVisbility().equals(ModifierWrapper.PRIVATE)){
						for(ProgramReference reference: field.getRefererPointList()){
							UnitMemberWrapper member = reference.getReferer();
							
							if(!isClusterContain(cluster, member)){
								return true;
							}
							
						}
					}
				}
			}
			return false;
		}

		/**
		 * @param member
		 * @return
		 */
		private boolean isClusterContain(HierarchicalCluster cluster, UnitMemberWrapper member) {
			for(IClusterElement element: cluster.getElements()){
				if(element instanceof UnitMemberWrapper){
					UnitMemberWrapper umw = (UnitMemberWrapper)element;
					if(umw.equals(member)){
						return true;
					}
				}
				else if(element instanceof ICompilationUnitWrapper){
					ICompilationUnitWrapper innerClass = (ICompilationUnitWrapper)element;
					if(innerClass.getMembers().contains(member)){
						return true;
					}
				}
			}
			return false;
		}

		public ArrayList<RefactoringOpportunity> detectOpportunities_bak(
				ProgramModel model) {
			ArrayList<RefactoringOpportunity> oppList = new ArrayList<>();
			
			int id = 0;
			ExtractClassCandidateGroup[] groups = getJDeodorantResults();
			for(ExtractClassCandidateGroup group: groups){
				for(ExtractClassCandidateRefactoring refactoring: group.getCandidates()){
					ArrayList<UnitMemberWrapper> toBeExtractedMemberList = new ArrayList<>();
					
					Set<VariableDeclaration> fieldSet = refactoring.getExtractedFieldFragments();
					Set<MethodDeclaration> methodSet = refactoring.getExtractedMethods();
					
					for(VariableDeclaration dec: fieldSet){
						String fieldName = dec.getName().getIdentifier();
						CompilationUnit cu = (CompilationUnit) dec.getRoot();
						String typeName = ((TypeDeclaration)cu.types().get(0)).resolveBinding().getQualifiedName();
						
						FieldWrapper member = Settings.scope.findField(typeName, fieldName);
						toBeExtractedMemberList.add(member);
					}
					
					for(MethodDeclaration dec: methodSet){
						String methodName = dec.getName().getIdentifier();
						ArrayList<String> params = new ArrayList<>();
						for(Object obj: dec.parameters()){
							SingleVariableDeclaration svd = (SingleVariableDeclaration)obj;
							String typeString = svd.getType().toString();
							params.add(typeString);
						}
						
						CompilationUnit cu = (CompilationUnit) dec.getRoot();
						String typeName = ((TypeDeclaration)cu.types().get(0)).resolveBinding().getQualifiedName();
						
						MethodWrapper member = Settings.scope.findMethod(typeName, methodName, params);
						if(member == null){
							
							System.currentTimeMillis();
						}
						
						toBeExtractedMemberList.add(member);
					}
					
					ICompilationUnitWrapper sourceUnit = toBeExtractedMemberList.get(0).getUnitWrapper();
					
					
//					ExtractClassOpportunity opp = new ExtractClassOpportunity(toBeExtractedMemberList, refactoring, 
//							moduleList, sourceUnit);
//					opp.setId(id);
//					oppList.add(opp);
				}
				id++;
			}
			
			ExtractClassOpportunity heuristicOpp = constructHeuristicExtractClassOpp(id);
			if(heuristicOpp !=  null){
				oppList.add(heuristicOpp);
			}
			
			return oppList;
		}
		
		private ExtractClassCandidateGroup[] getJDeodorantResults(){
			CompilationUnitCache.getInstance().clearCache();
			
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			
			IProject project = root.getProject(ReflexactoringUtil.getTargetProjectName());
			final IJavaProject selectedProject = JavaCore.create(project);
			
			ExtractClassCandidateGroup[] groups = null;
			new ASTReader(selectedProject, new NullProgressMonitor());
			SystemObject systemObject = ASTReader.getSystemObject();
			Set<ClassObject> classObjectsToBeExamined = new LinkedHashSet<ClassObject>();
			//classObjectsToBeExamined.addAll(systemObject.getClassObjects());
			
			final Set<String> classNamesToBeExamined = new LinkedHashSet<String>();
			
			for(ICompilationUnitWrapper unit: Settings.scope.getOutmostTypesInScope()){
				classObjectsToBeExamined.addAll(systemObject.getClassObjects(unit.getCompilationUnit()));
			}
			for(ClassObject classObject : classObjectsToBeExamined) {
				classNamesToBeExamined.add(classObject.getName());
			}
			
			MySystem system = new MySystem(systemObject, true);
			final DistanceMatrix distanceMatrix = new DistanceMatrix(system);
			distanceMatrix.generateDistances(new NullProgressMonitor());
			final List<ExtractClassCandidateRefactoring> extractClassCandidateList = new ArrayList<ExtractClassCandidateRefactoring>();
			
			extractClassCandidateList.addAll(distanceMatrix.getExtractClassCandidateRefactorings(classNamesToBeExamined, new NullProgressMonitor()));

			
			HashMap<String, ExtractClassCandidateGroup> groupedBySourceClassMap = new HashMap<String, ExtractClassCandidateGroup>();
			for(ExtractClassCandidateRefactoring candidate : extractClassCandidateList) {
				if(groupedBySourceClassMap.keySet().contains(candidate.getSourceEntity())) {
					groupedBySourceClassMap.get(candidate.getSourceEntity()).addCandidate(candidate);
				}
				else {
					ExtractClassCandidateGroup group = new ExtractClassCandidateGroup(candidate.getSourceEntity());
					group.addCandidate(candidate);
					groupedBySourceClassMap.put(candidate.getSourceEntity(), group);
				}
			}
			for(String sourceClass : groupedBySourceClassMap.keySet()) {
				groupedBySourceClassMap.get(sourceClass).groupConcepts();
			}

			groups = new ExtractClassCandidateGroup[groupedBySourceClassMap.values().size()];
			int count = 0;
			for(ExtractClassCandidateGroup group: groupedBySourceClassMap.values()){
				groups[count++] = group;
			}
			
			return groups;
		}

		@Override
		public boolean checkLegal(ProgramModel model) {
			// TODO Auto-generated method stub
			return false;
		}
		
		private ExtractClassOpportunity constructHeuristicExtractClassOpp(int id){
			ArrayList<UnitMemberWrapper> toBeExtractedMemberList = new ArrayList<>();
			ICompilationUnitWrapper unitWrapper = Settings.scope.findUnit("gui.BusMonitor");
			if(unitWrapper == null){
				return null;
			}
			
			ArrayList<String> params0 = new ArrayList<>();
			params0.add("int");
			params0.add("int");
			params0.add("int");
			params0.add("int");
			MethodWrapper method0 = Settings.scope.findMethod("gui.BusMonitor", "updateViews", params0);
			toBeExtractedMemberList.add(method0);;
			
			ArrayList<String> params1 = new ArrayList<>();
			params1.add("MapView");
			params1.add("int");
			params1.add("int");
			params1.add("int");
			params1.add("int");
			MethodWrapper method1 = Settings.scope.findMethod("gui.BusMonitor", "updateMapView", params1);
			toBeExtractedMemberList.add(method1);
			
			ArrayList<String> params2 = new ArrayList<>();
			params2.add("TableView");
			params2.add("int");
			params2.add("int");
			params2.add("int");
			params2.add("int");
			MethodWrapper method2 = Settings.scope.findMethod("gui.BusMonitor", "updateTableView", params2);
			toBeExtractedMemberList.add(method2);
			
			ArrayList<String> params3 = new ArrayList<>();
			params3.add("CurveView");
			params3.add("int");
			params3.add("int");
			params3.add("int");
			params3.add("int");
			MethodWrapper method3 = Settings.scope.findMethod("gui.BusMonitor", "updateCurveView", params3);
			toBeExtractedMemberList.add(method3);
			
			if(toBeExtractedMemberList.contains(null)){
				return null;
			}
			
//			ExtractClassOpportunity opp = new ExtractClassOpportunity(toBeExtractedMemberList, null, moduleList, unitWrapper);
//			opp.setId(id);
//			
//			return opp;
			return null;
		}
		
	}

}
