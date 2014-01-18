/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;

/**
 * @author linyun
 *
 */
public class AddModuleAction extends RefactoringAction {
	
	
	private ArrayList<ICompilationUnitWrapper> unMappedUnits;
	
	public AddModuleAction(ArrayList<ICompilationUnitWrapper> unMappedUnits){
		this.actionName = RefactoringAction.ADD;
		
		this.unMappedUnits = unMappedUnits;
	}

	/**
	 * @return the unMappedUnits
	 */
	public ArrayList<ICompilationUnitWrapper> getUnMappedUnits() {
		return unMappedUnits;
	}

	/**
	 * @param unMappedUnits the unMappedUnits to set
	 */
	public void setUnMappedUnits(ArrayList<ICompilationUnitWrapper> unMappedUnits) {
		this.unMappedUnits = unMappedUnits;
	}
	
	@Override
	public String getDetailedDescription(){
		StringBuffer buffer = new StringBuffer();
		String desc = "containing ";
		buffer.append(desc);
		for(ICompilationUnitWrapper wrapper: unMappedUnits){
			buffer.append(wrapper.getSimpleName() + ", ");
		}
		
		return buffer.toString();
	}
	
}
