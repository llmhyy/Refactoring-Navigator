/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import reflexactoring.diagram.action.semantic.TokenExtractor;
import reflexactoring.diagram.util.JavaCodeUtil;

/**
 * @author linyun
 *
 */
public abstract class UnitMemberWrapper extends Document implements LowLevelSuggestionObject, LowLevelGraphNode, SimilarityComputable {
	protected ICompilationUnitWrapper unitWrapper;
	/**
	 * stands for the unit members referring this unit member
	 */
	protected ArrayList<ProgramReference> refererPointList = new ArrayList<>();
	/**
	 * stands for the unit members referred by this unit or unit member
	 */
	protected ArrayList<ProgramReference> refereePointList = new ArrayList<>();
	
	//protected ArrayList<UnitMemberWrapper> callerList = new ArrayList<>();
	//protected ArrayList<UnitMemberWrapper> calleeList = new ArrayList<>();

	public UnitMemberWrapper(ICompilationUnitWrapper unitWrapper){
		this.setUnitWrapper(unitWrapper);
	}
	
	public abstract ASTNode getJavaElement();
	
	public void openInEditor(){
		IEditorPart javaEditor;
		try {
			CompilationUnit cu = this.unitWrapper.getJavaUnit();
			int lineNumber = cu.getLineNumber(getJavaElement().getStartPosition());
			
			ICompilationUnit unit = this.unitWrapper.getCompilationUnit();
			javaEditor = JavaUI.openInEditor(unit);
			JavaUI.revealInEditor(javaEditor,
					(IJavaElement) unit);
			JavaCodeUtil.goToLine(javaEditor, lineNumber, lineNumber+1);
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * get the the java element they wrap
	 * @return
	 */
	public abstract IMember getJavaMember();
	
	/**
	 * @return the unitWrapper
	 */
	public ICompilationUnitWrapper getUnitWrapper() {
		return unitWrapper;
	}

	/**
	 * @param unitWrapper the unitWrapper to set
	 */
	public void setUnitWrapper(ICompilationUnitWrapper unitWrapper) {
		this.unitWrapper = unitWrapper;
	}

	@Override
	public HashMap<GraphNode, Integer> getCallerList() {
		HashMap<GraphNode, Integer> map = new HashMap<>();
		for(ProgramReference reference: this.refererPointList){
			map.put(reference.getReferer(), 1);
		}
		
		return map;
	}

	
	@Override
	public HashMap<GraphNode, Integer> getCalleeList() {
		HashMap<GraphNode, Integer> map = new HashMap<>();
		for(ProgramReference reference: this.refereePointList){
			if(reference.getReferee() instanceof UnitMemberWrapper){
				map.put((UnitMemberWrapper)reference.getReferee(), 1);				
			}
		}
		
		return map;
	}
	
	@Override
	public void addProgramReferer(ProgramReference reference){
		this.refererPointList.add(reference);
	}
	
	@Override
	public void addProgramReferee(ProgramReference reference){
		this.refereePointList.add(reference);
	}
	
	/*public void addCaller(UnitMemberWrapper member){
		for(UnitMemberWrapper m: callerList){
			if(m.getJavaMember().equals(member.getJavaMember())){
				return;
			}
		}
		
		this.callerList.add(member);
	}
	
	public void addCallee(UnitMemberWrapper member){
		for(UnitMemberWrapper m: calleeList){
			if(m.getJavaMember().equals(member.getJavaMember())){
				return;
			}
		}
		
		this.calleeList.add(member);
	}*/
	
	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.LowLevelGraphNode#getMappingModule()
	 */
	@Override
	public ModuleWrapper getMappingModule() {
		return this.unitWrapper.getMappingModule();
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.UnitMemberWrapper#getParentList()
	 */
	@Override
	public List<? extends GraphNode> getParentList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.UnitMemberWrapper#getChildList()
	 */
	@Override
	public List<? extends GraphNode> getChildList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public abstract void setName(String name);

	public ArrayList<ProgramReference> getRefererPointList() {
		return refererPointList;
	}

	public void setRefererPointList(ArrayList<ProgramReference> refererPointList) {
		this.refererPointList = refererPointList;
	}

	public ArrayList<ProgramReference> getRefereePointList() {
		return refereePointList;
	}

	public void setRefereePointList(ArrayList<ProgramReference> refereePointList) {
		this.refereePointList = refereePointList;
	}
	
	public ProgramReference removeReferer(ProgramReference reference){
		Iterator<ProgramReference> refIter = this.refererPointList.iterator();
		while(refIter.hasNext()){
			ProgramReference programReference = refIter.next();
			if(programReference.equals(reference)){
				refIter.remove();
				return programReference;
			}
		}
		
		return null;
	}

	/**
	 * @return
	 */
	public boolean isOverrideSuperMember() {
		for(GraphNode node: this.getUnitWrapper().getParentList()){
			ICompilationUnitWrapper superUnit = (ICompilationUnitWrapper)node;
			if(isOverrideSuperMember(this, superUnit)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isOverrideSuperMember(UnitMemberWrapper member, ICompilationUnitWrapper superUnit){
		for(UnitMemberWrapper superMember: superUnit.getMembers()){
			if(this.hasSameSignatureWith(superMember)){
				return true;
			}
		}
		
		for(GraphNode node: superUnit.getParentList()){
			ICompilationUnitWrapper parentUnit = (ICompilationUnitWrapper)node;
			return isOverrideSuperMember(member, parentUnit);		
		}
		
		return false;
	}
	
	/**
	 * if you ask why the code is cloned from the previous one, my answer is efficiency.
	 * @return
	 */
	public ArrayList<UnitMemberWrapper> findOverridedSuperMember(){
		ArrayList<UnitMemberWrapper> list = new ArrayList<>();
		for(GraphNode node: this.getUnitWrapper().getParentList()){
			ICompilationUnitWrapper superUnit = (ICompilationUnitWrapper)node;
			findOverridedSuperMember(this, superUnit, list);
		}
		return list;
	}
	
	/**
	 * @param unitMemberWrapper
	 * @param superUnit
	 * @return
	 */
	private void findOverridedSuperMember(
			UnitMemberWrapper unitMemberWrapper, ICompilationUnitWrapper superUnit,
			ArrayList<UnitMemberWrapper> list) {
		for(UnitMemberWrapper superMember: superUnit.getMembers()){
			if(this.hasSameSignatureWith(superMember)){
				list.add(superMember);
			}
		}
		
		for(GraphNode node: superUnit.getParentList()){
			ICompilationUnitWrapper parentUnit = (ICompilationUnitWrapper)node;
			findOverridedSuperMember(unitMemberWrapper, parentUnit, list);	
		}
	}

	public abstract boolean hasSameSignatureWith(UnitMemberWrapper member);

	public abstract double computeSimilarityForBeingPulledUp(UnitMemberWrapper otherMember);
}
