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
import org.eposoft.jccd.preprocessors.java.RemoveSimpleMethods;

import reflexactoring.diagram.action.smelldetection.bean.CloneInstance;
import reflexactoring.diagram.action.smelldetection.bean.CloneSet;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.ProgramReference;
import reflexactoring.diagram.bean.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public class BadSmellDetector {
	ArrayList<RefactoringOpportunity> opporuntities = new ArrayList<>();
	
	public void detect(ProgramModel model){
		detectClone(model);
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
