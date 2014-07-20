/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
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
public abstract class UnitMemberWrapper extends Document implements LowLevelSuggestionObject, LowLevelGraphNode {
	protected ICompilationUnitWrapper unitWrapper;
	/**
	 * stands for the unit members referring this unit member
	 */
	protected ArrayList<ProgramReference> refererPointList = new ArrayList<>();
	/**
	 * stands for the unit members referred by this unit member
	 */
	protected ArrayList<ProgramReference> refereePointList = new ArrayList<>();
	
	//protected ArrayList<UnitMemberWrapper> callerList = new ArrayList<>();
	//protected ArrayList<UnitMemberWrapper> calleeList = new ArrayList<>();

	public UnitMemberWrapper(ICompilationUnitWrapper unitWrapper){
		this.setUnitWrapper(unitWrapper);
	}
	
	protected abstract ASTNode getJavaElement();
	
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
	public List<? extends GraphNode> getCallerList() {
		
		ArrayList<UnitMemberWrapper> callerList = new ArrayList<>();
		for(ProgramReference reference: this.refererPointList){
			callerList.add(reference.getReferer());
		}
		
		return callerList;
	}

	
	@Override
	public List<? extends GraphNode> getCalleeList() {
		ArrayList<UnitMemberWrapper> calleeList = new ArrayList<>();
		for(ProgramReference reference: this.refereePointList){
			calleeList.add(reference.getReferee());
		}
		
		return calleeList;
	}
	
	public void addProgramReferer(ProgramReference reference){
		this.refererPointList.add(reference);
	}
	
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
}
