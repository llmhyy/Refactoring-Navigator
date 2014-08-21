/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.distance.DistanceMatrix;
import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.ExtractClassCandidateRefactoring;
import gr.uom.java.distance.MySystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import reflexactoring.diagram.action.smelldetection.NameGernationCounter;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.precondition.RefactoringPrecondition;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.FieldWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.MethodWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class ExtractClassOpportunity extends RefactoringOpportunity {

	private int id;
	private ArrayList<UnitMemberWrapper> toBeExtractedMembers = new ArrayList<>();
	private ExtractClassCandidateRefactoring refactoring;
	private ICompilationUnitWrapper sourceUnit;
	
	/**
	 * @param toBeExtractedMembers
	 */
	public ExtractClassOpportunity(
			ArrayList<UnitMemberWrapper> toBeExtractedMembers, ExtractClassCandidateRefactoring refactoring, 
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
		buffer.append(toBeExtractedMembers.get(0).getUnitWrapper().getName());
		buffer.append(" : ");
		for(UnitMemberWrapper member: toBeExtractedMembers){
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
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		/**
		 * replace those to-be-extracted members with new ones.
		 */
		ArrayList<UnitMemberWrapper> extractMembers = new ArrayList<>();
		for(UnitMemberWrapper member: toBeExtractedMembers){
			UnitMemberWrapper newMem = newModel.findMember(member);
			extractMembers.add(newMem);
		}
		//toBeExtractedMembers = extractMembers;
		
		ICompilationUnitWrapper newUnit = new ICompilationUnitWrapper(null, false, "ExtractedClass"+NameGernationCounter.retrieveNumber(), 
				extractMembers.get(0).getUnitWrapper().getPackageName(), null, "");
		
		newModel.getScopeCompilationUnitList().add(newUnit);
		
		newUnit.setMembers(extractMembers);
		for(UnitMemberWrapper member: extractMembers){
			/**
			 * remove the old containing relation
			 */
			member.getUnitWrapper().getMembers().remove(member);
			/**
			 * add the new containing relation
			 */
			member.setUnitWrapper(newUnit);
		}
		
		calculateBestMappingModule(newModel, newUnit);
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		/**
		 * remove some exclusive opportunities
		 */
		Iterator<RefactoringOpportunity> oppIter = newModel.getOneShotOpportnityList().iterator();
		while(oppIter.hasNext()){
			RefactoringOpportunity opp = oppIter.next();
			if(opp instanceof ExtractClassOpportunity){
				ExtractClassOpportunity extractClassOpp = (ExtractClassOpportunity)opp;
				
				if(extractClassOpp.getId() == this.getId()){
					oppIter.remove();
				}
			}
		}
		
		return newModel;
	}
	
	/**
	 * @return the toBeExtractedMembers
	 */
	public ArrayList<UnitMemberWrapper> getToBeExtractedMembers() {
		return toBeExtractedMembers;
	}

	/**
	 * @param toBeExtractedMembers the toBeExtractedMembers to set
	 */
	public void setToBeExtractedMembers(
			ArrayList<UnitMemberWrapper> toBeExtractedMembers) {
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
		return details;
	}

	@Override
	public ArrayList<ASTNode> getHints() {
		ArrayList<ASTNode> nodeList = new ArrayList<>();
		for(UnitMemberWrapper member: toBeExtractedMembers){
			nodeList.add(member.getJavaElement());
		}
		return nodeList;
	}

	@Override
	public double computeSimilarityWith(RefactoringOpportunity opp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean apply() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkLegal(ProgramModel model) {
		// TODO Auto-generated method stub
		return false;
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

	public class Precondition extends RefactoringPrecondition{

		@Override
		public ArrayList<RefactoringOpportunity> detectOpportunities(
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
						CompilationUnit cu = (CompilationUnit) dec.getRoot();
						String typeName = ((TypeDeclaration)cu.types().get(0)).resolveBinding().getQualifiedName();
						
						MethodWrapper member = Settings.scope.findMethod(typeName, methodName);
						toBeExtractedMemberList.add(member);
					}
					
					ICompilationUnitWrapper sourceUnit = toBeExtractedMemberList.get(0).getUnitWrapper();
					
					ExtractClassOpportunity opp = new ExtractClassOpportunity(toBeExtractedMemberList, refactoring, 
							moduleList, sourceUnit);
					opp.setId(id);
					oppList.add(opp);
				}
				id++;
			}
			
			return oppList;
		}
		
		private ExtractClassCandidateGroup[] getJDeodorantResults(){
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			
			IProject project = root.getProject(ReflexactoringUtil.getTargetProjectName());
			final IJavaProject selectedProject = JavaCore.create(project);
			
			ExtractClassCandidateGroup[] groups = null;
			new ASTReader(selectedProject, new NullProgressMonitor());
			SystemObject systemObject = ASTReader.getSystemObject();
			Set<ClassObject> classObjectsToBeExamined = new LinkedHashSet<ClassObject>();
			classObjectsToBeExamined.addAll(systemObject.getClassObjects());
			
			final Set<String> classNamesToBeExamined = new LinkedHashSet<String>();
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
		
	}

}
