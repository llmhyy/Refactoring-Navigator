/**
 * 
 */
package reflexactoring.diagram.action.smelldetection;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eposoft.jccd.data.ASourceUnit;
import org.eposoft.jccd.data.JCCDFile;
import org.eposoft.jccd.data.SimilarityGroup;
import org.eposoft.jccd.data.SimilarityGroupManager;
import org.eposoft.jccd.data.SourceUnitPosition;
import org.eposoft.jccd.data.ast.ANode;
import org.eposoft.jccd.data.ast.NodeTypes;
import org.eposoft.jccd.detectors.APipeline;
import org.eposoft.jccd.detectors.ASTDetector;
import org.eposoft.jccd.preprocessors.java.GeneralizeArrayInitializers;
import org.eposoft.jccd.preprocessors.java.GeneralizeClassDeclarationNames;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodArgumentTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodCallNames;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodReturnTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeVariableDeclarationTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeVariableNames;
import org.eposoft.jccd.preprocessors.java.RemoveAnnotations;
import org.eposoft.jccd.preprocessors.java.RemoveAssertions;
import org.eposoft.jccd.preprocessors.java.RemoveEmptyBlocks;
import org.eposoft.jccd.preprocessors.java.RemoveSemicolons;

import reflexactoring.diagram.action.smelldetection.bean.CloneInstance;
import reflexactoring.diagram.action.smelldetection.bean.CloneSet;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.CreateSuperclassAndPullUpMemberOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMemberToInterfaceOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.PullUpMemberToSuperclassOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.bean.FieldWrapper;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.ProgramReference;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class BadSmellDetector {
	ArrayList<RefactoringOpportunity> opporuntities = new ArrayList<>();
	
	public void detect(ProgramModel model){
		detectClone(model);
		/**
		 * should be followed by other smell detection methods
		 */
		detectCloneBasedRefactoringOpportunities(model);
		//detectMetricBasedRefactoringOpportunities(model);
	}
	
	/**
	 * @param model
	 */
	private void detectCloneBasedRefactoringOpportunities(ProgramModel model) {
		// TODO for Lin Yun
		/**
		 * First, identify the *counter* methods across different classes, those class should be with same inheritance hierarchy.
		 */
		ArrayList<ArrayList<UnitMemberWrapper>> refactoringPlaceList = detectCounterMembers(model);
		
		detectPullingUpOpportunities(model, refactoringPlaceList);
		/**
		 * Then, we look for those clone sets whose instances are distributed irregularly, they are extract-method-to-utility-class.
		 */
		
		System.currentTimeMillis();
	}

	/**
	 * If those methods occupy the whole (sufficient) method body, it is a (create-and-)pull-up-to-super-class opportunity,
	 * otherwise, i.e., if those methods do not share code clones, it is a pull-up-to-new-interface opportunity.<p>
	 * 
	 * Noteworthy, if the declaring classes of those counter members share the same common ancestor, I create a pull-up-to-superclass
	 * opportunity, if the declaring classes of them share no super class, I create a create-and-pull-up-to-superclass
	 * opportunity. In other cases, e.g., some of the declaring classes share super class while others not, I will not consider
	 * such cases as pull-up-to-superclass opportunity.<p>
	 * 
	 * Moreover, for each (create-and-)pull-up-to-superclass opportunity of counter methods, there will always be a pull-up-to-interface 
	 * opportunity, of course, they conflicts with each other, which means the search algorithm should know to remove some 
	 * opportunities after applying some others.
	 */
	private void detectPullingUpOpportunities(ProgramModel model, ArrayList<ArrayList<UnitMemberWrapper>> refactoringPlaceList) {
		ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
		
		for(ArrayList<UnitMemberWrapper> refactoringPlace: refactoringPlaceList){
			ICompilationUnitWrapper commonAncestor = findCommonAncestor(refactoringPlace);
			boolean isWithoutAnySuperclass = isWithoutAnySuperclass(refactoringPlace);
			boolean isWithSimilarBody = isWithSimilarBody(model, refactoringPlace);
			UnitMemberWrapper member = refactoringPlace.get(0);
			
			if((isWithSimilarBody || (member instanceof FieldWrapper)) && ((commonAncestor != null) || (isWithoutAnySuperclass))){
				if(commonAncestor != null){
					PullUpMemberToSuperclassOpportunity opp = 
							new PullUpMemberToSuperclassOpportunity(refactoringPlace, moduleList, commonAncestor);
					this.opporuntities.add(opp);
				}
				else{
					CreateSuperclassAndPullUpMemberOpportunity opp = 
							new CreateSuperclassAndPullUpMemberOpportunity(refactoringPlace, moduleList);
					this.opporuntities.add(opp);
				}
			}
			
			if(member instanceof MethodWrapper){
				PullUpMemberToInterfaceOpportunity opportunity = 
						new PullUpMemberToInterfaceOpportunity(refactoringPlace, moduleList);
				this.opporuntities.add(opportunity);				
			}
		}
	}

	/**
	 * @param refactoringPlace
	 * @return
	 */
	private ICompilationUnitWrapper findCommonAncestor(ArrayList<UnitMemberWrapper> refactoringPlace) {
		int smallestSize = -1;
		
		ArrayList<ArrayList<ICompilationUnitWrapper>> list = new ArrayList<>();
		for(UnitMemberWrapper member: refactoringPlace){
			/**
			 * achieve all the parents of the first element in order
			 */
			ArrayList<ICompilationUnitWrapper> ancestorList = new ArrayList<>();
			ICompilationUnitWrapper parent = member.getUnitWrapper().getSuperClass();
			if(parent == null){
				return null;
			}
			else{
				ancestorList.add(parent);
				while(parent.getSuperClass() != null){
					parent = parent.getSuperClass();
					ancestorList.add(parent);
				}
			}
			
			ArrayList<ICompilationUnitWrapper> reverseList = new ArrayList<>();
			for(int i=0; i<ancestorList.size(); i++){
				reverseList.add(ancestorList.get(ancestorList.size()-1-i));
			}
			list.add(reverseList);
			
			if(smallestSize == -1){
				smallestSize = reverseList.size();
			}
			else{
				smallestSize = (smallestSize < reverseList.size())?smallestSize:reverseList.size();
			}
		}
		
		ICompilationUnitWrapper commonAncestor = null;
		for(int i=0; i<smallestSize; i++){
			ICompilationUnitWrapper unit = list.get(0).get(i);
			for(int j=1; j<list.size(); j++){
				ICompilationUnitWrapper otherUnit = list.get(j).get(i);
				if(!unit.equals(otherUnit)){
					return commonAncestor;
				}
			}
			commonAncestor = unit;
		}
		
		return commonAncestor;
	}

	/**
	 * @param refactoringPlace
	 * @return
	 */
	private boolean isWithoutAnySuperclass(
			ArrayList<UnitMemberWrapper> refactoringPlace) {
		for(UnitMemberWrapper member: refactoringPlace){
			ICompilationUnitWrapper unitWrapper = member.getUnitWrapper();
			if(unitWrapper.getSuperClass() != null){
				return false;
			}
		}
		return true;
	}

	/**
	 * @param refactoringPlace
	 * @return
	 */
	private boolean isWithSimilarBody(ProgramModel model, ArrayList<UnitMemberWrapper> refactoringPlace) {
		for(CloneSet set: model.getCloneSets()){
			boolean isAllCloneInstanceSufficientlyOccupyMemberBody = true;
			for(CloneInstance instance: set.getInstances()){
				if(!isCloneInstanceSufficientlyOccupyOneOfTheMember(instance, refactoringPlace)){
					isAllCloneInstanceSufficientlyOccupyMemberBody = false;
					break;
				}
			}
			
			if(isAllCloneInstanceSufficientlyOccupyMemberBody){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isCloneInstanceSufficientlyOccupyOneOfTheMember(CloneInstance instance, 
			ArrayList<UnitMemberWrapper> refactoringPlace){
		for(UnitMemberWrapper member: refactoringPlace){
			if(instance.getMember().equals(member)){
				CompilationUnit cu = member.getUnitWrapper().getJavaUnit();
				if(cu != null){
					int startPosition = member.getJavaElement().getStartPosition();
					int endPosition = startPosition + member.getJavaElement().getLength();

					int cloneCoverLength = instance.getLength();
					int memberLength = cu.getLineNumber(endPosition) - cu.getLineNumber(startPosition);
					
					if(cloneCoverLength > 0.8*memberLength){
						return true;
					}
				}
				else{
					return true;
				}
			}
		}
		
		return false;
	}
	
	

	/**
	 * This method is used to identify the "counter member" across the refactoring scope, that is, 
	 * 1) those members are with the same signature across different classes
	 * 2) those members do not override some member in super class or interface
	 * 
	 * Note that, current version only identify counter members in class instead of interface.
	 * 
	 * @param model
	 * @return
	 */
	private ArrayList<ArrayList<UnitMemberWrapper>> detectCounterMembers(ProgramModel model) {
		ArrayList<ArrayList<UnitMemberWrapper>> refactoringPlaceList = new ArrayList<>();
		ArrayList<UnitMemberWrapper> markedMemberList = new ArrayList<>();
		for(ICompilationUnitWrapper unit: model.getScopeCompilationUnitList()){
			if(unit.isInterface())continue;
			
			ArrayList<ICompilationUnitWrapper> otherUnits = model.findOtherUnits(unit);
			for(UnitMemberWrapper member: unit.getMembers()){
				if(markedMemberList.contains(member))continue;
				
				ArrayList<UnitMemberWrapper> counterMemberList = new ArrayList<>();
				if(!member.isOverrideSuperMember()){
					counterMemberList.add(member);					
					for(ICompilationUnitWrapper otherUnit: otherUnits){
						if(otherUnit.isInterface())continue;
						
						for(UnitMemberWrapper otherMember: otherUnit.getMembers()){
							if(markedMemberList.contains(otherMember))continue;
							
							if(member.hasSameSignatureWith(otherMember)){
								if(!otherMember.isOverrideSuperMember()){
									counterMemberList.add(otherMember);
									break;								
								}
							}
						}
					}
				}
				
				if(counterMemberList.size() >= 2){
					refactoringPlaceList.add(counterMemberList);
					markedMemberList.addAll(counterMemberList);
				}
			}
		}
		
		return refactoringPlaceList;
	}

	private void detectClone(ProgramModel model){
		
		ArrayList<JCCDFile> fileList = new ArrayList<JCCDFile>();
		HashMap<String, ICompilationUnitWrapper> map = new HashMap<>();
		
		for(ICompilationUnitWrapper unit: model.getScopeCompilationUnitList()){
			IResource resource = unit.getCompilationUnit().getResource();			
			fileList.add(new JCCDFile(resource.getRawLocation().toFile()));
			
			String path = resource.getRawLocation().toFile().getAbsolutePath();
			map.put(path, unit);
		}
		
		APipeline detector = new ASTDetector();
		JCCDFile[] files = fileList.toArray(new JCCDFile[0]);
		detector.setSourceFiles(files);
		
		detector.addOperator(new RemoveAnnotations());
		//detector.addOperator(new RemoveSimpleMethods());
		detector.addOperator(new RemoveSemicolons());
		detector.addOperator(new RemoveAssertions());
		detector.addOperator(new RemoveEmptyBlocks());		
		detector.addOperator(new GeneralizeArrayInitializers());
		detector.addOperator(new GeneralizeClassDeclarationNames());
		detector.addOperator(new GeneralizeMethodArgumentTypes());
		detector.addOperator(new GeneralizeMethodReturnTypes());
		detector.addOperator(new GeneralizeVariableDeclarationTypes());
		detector.addOperator(new GeneralizeMethodCallNames());
		detector.addOperator(new GeneralizeVariableDeclarationTypes());
		detector.addOperator(new GeneralizeVariableNames());
		
		SimilarityGroupManager manager = detector.process();
		SimilarityGroup[] simGroups = manager.getSimilarityGroups();
		
		ArrayList<CloneSet> cloneSets = convertToCloneSets(simGroups, map);
		
		model.setCloneSets(cloneSets);
	}

	/**
	 * In this method, clone sets will be constructed, in which every clone instance will contain not only file name, line numbers,
	 * but its related unit member and program reference as well.
	 * 
	 * @param simGroups
	 * @return
	 */
	private ArrayList<CloneSet> convertToCloneSets(SimilarityGroup[] simGroups, HashMap<String, ICompilationUnitWrapper> map) {
		
		ArrayList<CloneSet> sets = new ArrayList<>();
		
		for (int i = 0; i < simGroups.length; i++) {
			final ASourceUnit[] nodes = simGroups[i].getNodes();
			
			CloneSet set = new CloneSet(String.valueOf(simGroups[i].getGroupId()));
			
			for (int j = 0; j < nodes.length; j++) {
				
				final SourceUnitPosition minPos = APipeline.getFirstNodePosition((ANode) nodes[j]);
				final SourceUnitPosition maxPos = APipeline.getLastNodePosition((ANode) nodes[j]);

				ANode fileNode = (ANode) nodes[j];
				while (fileNode.getType() != NodeTypes.FILE.getType()) {
					fileNode = fileNode.getParent();
				}
				
				CloneInstance cloneInstance = new CloneInstance(set, fileNode.getText(), 
						minPos.getLine(), maxPos.getLine());
				
				//set.getInstances().add(cloneInstance);
				
				UnitMemberWrapper member = findResidingUnitMember(cloneInstance, map);
				if(member != null){
					set.getInstances().add(cloneInstance);					
				}
			}
			
			if(set.getInstances().size() >= 2){
				sets.add(set);				
			}
		}
		
		return sets;
	}

	/**
	 * @param cloneInstance
	 * @param map
	 * @return
	 */
	private UnitMemberWrapper findResidingUnitMember(
			CloneInstance cloneInstance,
			HashMap<String, ICompilationUnitWrapper> map) {
		ICompilationUnitWrapper unitWrapper = map.get(cloneInstance.getFileName());
		CompilationUnit unit = unitWrapper.getJavaUnit();
		for(UnitMemberWrapper member: unitWrapper.getMembers()){
			ASTNode declaringNode = member.getJavaElement();
			int startLine = unit.getLineNumber(declaringNode.getStartPosition());
			int endLine = unit.getLineNumber(declaringNode.getStartPosition()+declaringNode.getLength());
			
			/**
			 * Judge whether the cloned code fragment is located in the right scope.
			 */
			if(cloneInstance.getStartLineNumber() >= startLine && cloneInstance.getEndLineNumber() <= endLine){
				cloneInstance.setMember(member);
				
				/**
				 * Checking the program reference covered by this cloned code fragment.
				 */
				for(ProgramReference reference: member.getRefereePointList()){
					int lineNumber = unit.getLineNumber(reference.getASTNode().getStartPosition());
					if(lineNumber >= cloneInstance.getStartLineNumber() && lineNumber <= cloneInstance.getEndLineNumber()){
						cloneInstance.getCoveringReferenceList().add(reference);
					}
				}
				
				return member;
			}
		}
		
		
		return null;
	}
}
