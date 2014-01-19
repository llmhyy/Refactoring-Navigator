/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;

import reflexactoring.Type;

/**
 * @author linyun
 *
 */
public class Scope {
	private ArrayList<ICompilationUnitWrapper> scopeCompilationUnitList = new ArrayList<>();

	/**
	 * @return the scopeCompilationUnitList
	 */
	public ArrayList<ICompilationUnitWrapper> getScopeCompilationUnitList() {
		return scopeCompilationUnitList;
	}
	
	public ArrayList<ICompilationUnit> getScopeRawCompilationUnitList(){
		ArrayList<ICompilationUnit> units = new ArrayList<>();
		for(ICompilationUnitWrapper wrapper: this.scopeCompilationUnitList){
			units.add(wrapper.getCompilationUnit());
		}
		
		return units;
	}

	/**
	 * @param scopeCompilationUnitList the scopeCompilationUnitList to set
	 */
	public void setScopeCompilationUnitList(ArrayList<ICompilationUnitWrapper> scopeCompilationUnitList) {
		this.scopeCompilationUnitList = scopeCompilationUnitList;
	}
	
	public ICompilationUnitWrapper findUnit(String identifier){
		for(ICompilationUnitWrapper unit: this.scopeCompilationUnitList){
			//ICompilationUnitWrapper wrapper = new ICompilationUnitWrapper(unit, false);
			if(unit.getFullQualifiedName().equals(identifier)){
				return unit;
			}
		}
		
		return null;
	}
	
	public ICompilationUnitWrapper findUnit(Type type){
		String packageName = type.getPackageName();
		String typeName = type.getName();
		
		String identifier = packageName+"."+typeName;
		
		return findUnit(identifier);
	}
	
	public void removeUnit(ICompilationUnitWrapper unitWrapper){
		this.scopeCompilationUnitList.remove(unitWrapper);
	}
}
