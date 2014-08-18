/**
 * 
 */
package reflexactoring.diagram.action.popup;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;

import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;

/**
 * @author linyun
 * 
 */
public class ReferenceDetailMap {
	private ICompilationUnitWrapper callerUnit;
	private ICompilationUnitWrapper calleeUnit;
	private ArrayList<ASTNode> references;

	/**
	 * @param callerUnit
	 * @param calleeUnit
	 * @param references
	 */
	public ReferenceDetailMap(ICompilationUnitWrapper callerUnit,
			ICompilationUnitWrapper calleeUnit, ArrayList<ASTNode> references) {
		super();
		this.callerUnit = callerUnit;
		this.calleeUnit = calleeUnit;
		this.references = references;
	}

	/**
	 * @return the callerUnit
	 */
	public ICompilationUnitWrapper getCallerUnit() {
		return callerUnit;
	}

	/**
	 * @param callerUnit
	 *            the callerUnit to set
	 */
	public void setCallerUnit(ICompilationUnitWrapper callerUnit) {
		this.callerUnit = callerUnit;
	}

	/**
	 * @return the calleeUnit
	 */
	public ICompilationUnitWrapper getCalleeUnit() {
		return calleeUnit;
	}

	/**
	 * @param calleeUnit
	 *            the calleeUnit to set
	 */
	public void setCalleeUnit(ICompilationUnitWrapper calleeUnit) {
		this.calleeUnit = calleeUnit;
	}

	/**
	 * @return the references
	 */
	public ArrayList<ASTNode> getReferences() {
		return references;
	}

	/**
	 * @param references
	 *            the references to set
	 */
	public void setReferences(ArrayList<ASTNode> references) {
		this.references = references;
	}

}
