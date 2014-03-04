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
	protected ArrayList<UnitMemberWrapper> callerList = new ArrayList<>();
	protected ArrayList<UnitMemberWrapper> calleeList = new ArrayList<>();

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
		return this.callerList;
	}

	
	@Override
	public List<? extends GraphNode> getCalleeList() {
		return this.calleeList;
	}
	
	public void addCaller(UnitMemberWrapper member){
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
	}
	
	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.LowLevelGraphNode#getMappingModule()
	 */
	@Override
	public ModuleWrapper getMappingModule() {
		return this.unitWrapper.getMappingModule();
	}
	
}
