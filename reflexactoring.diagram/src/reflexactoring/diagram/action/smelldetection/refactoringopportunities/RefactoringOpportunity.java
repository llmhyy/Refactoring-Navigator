/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;

import reflexactoring.diagram.action.smelldetection.AdvanceEvaluatorAdapter;
import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;



/**
 * @author linyun
 *
 */
public abstract class RefactoringOpportunity {
	
	/**
	 * this API (getModuleList) is very time consuming.
	 */
	protected ArrayList<ModuleWrapper> moduleList;
	
	/**
	 * Given a program model, this method is used to simulate the effect of applying
	 * the specific refactoring.
	 * @param model
	 * @return
	 */
	public abstract ProgramModel simulate(ProgramModel model);
	
	public abstract String getRefactoringName();
	
	public String getRefactoringDescription(){
		return toString();
	};
	
	public abstract ArrayList<String> getRefactoringDetails();
	
	public abstract ArrayList<ASTNode> getHints();
	
	public abstract double computeSimilarityWith(RefactoringOpportunity opp);
	/**
	 * this method is used to apply refactoring on real code, note that it is possible to need some user input
	 * for example, a new class name for user to input. Therefore, we need position and sequence to update user's
	 * input in program model.
	 * @param position
	 * @param sequence
	 * @return
	 */
	public abstract boolean apply(int position, RefactoringSequence sequence);
	
	/**
	 * this method is used to undo apply refactoring on real code, simulate user presses ctrl+Z in default
	 */
	public boolean undoApply(){
		try {
			IWorkbenchOperationSupport operationSupport = PlatformUI.getWorkbench().getOperationSupport();
			IUndoContext context = operationSupport.getUndoContext();
			IOperationHistory operationHistory = operationSupport.getOperationHistory();  
			IStatus status = operationHistory.undo(context, null, null);
		} catch (ExecutionException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/**
	 * this method is used to check whether a refactoring opportunity still hold true.
	 * @param model
	 * @return
	 */
	public abstract boolean checkLegal();
	/**
	 * @return the moduleList
	 */
	public ArrayList<ModuleWrapper> getModuleList() {
		return moduleList;
	}
	/**
	 * @param moduleList the moduleList to set
	 */
	public void setModuleList(ArrayList<ModuleWrapper> moduleList) {
		this.moduleList = moduleList;
	}
	
	/**
	 * @param newUnit
	 * @return
	 */
	protected ModuleWrapper calculateBestMappingModule(ProgramModel model,
			ICompilationUnitWrapper newUnit) {
		
		//long t1 = System.currentTimeMillis();
		
		ModuleWrapper module = null;
		double fitness = 0;
		
		for(ModuleWrapper m: moduleList){
			newUnit.setMappingModule(m);
			
			//long t3 = System.currentTimeMillis();
			double f = new AdvanceEvaluatorAdapter().computeFitness(model, moduleList);
			//long t4 = System.currentTimeMillis();
			//System.out.println("Calculating fitness: " + (t4-t3));
			
			if(module == null){
				module = m;
				fitness = f;
			}
			else{
				if(f > fitness){
					module = m;
					fitness = f;
				}
			}
		}
		
		//long t2 = System.currentTimeMillis();
		//System.out.println("Calculating best module time: " + (t2-t1));
		
		return module;
	}
	
	protected boolean canFindAnEqualMemberInList(ArrayList<UnitMemberWrapper> list, UnitMemberWrapper member){
		for(UnitMemberWrapper memberInList: list){
			if(memberInList.equals(member)){
				return true;
			}
		}
		
		return false;
	}
}
