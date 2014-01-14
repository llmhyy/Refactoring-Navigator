/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;

/**
 * @author linyun
 *
 */
public class Scope {
	private ArrayList<ICompilationUnit> scopeCompilationUnitList = new ArrayList<>();

	/**
	 * @return the scopeCompilationUnitList
	 */
	public ArrayList<ICompilationUnit> getScopeCompilationUnitList() {
		return scopeCompilationUnitList;
	}

	/**
	 * @param scopeCompilationUnitList the scopeCompilationUnitList to set
	 */
	public void setScopeCompilationUnitList(ArrayList<ICompilationUnit> scopeCompilationUnitList) {
		this.scopeCompilationUnitList = scopeCompilationUnitList;
	}
	
	public ICompilationUnitWrapper findUnit(String identifier){
		for(ICompilationUnit unit: this.scopeCompilationUnitList){
			ICompilationUnitWrapper wrapper = new ICompilationUnitWrapper(unit, false);
			if(wrapper.getFullQualifiedName().equals(identifier)){
				return wrapper;
			}
		}
		
		return null;
	}
}
