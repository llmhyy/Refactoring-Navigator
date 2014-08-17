/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;

import reflexactoring.diagram.action.smelldetection.AdvanceEvaluatorAdapter;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;



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
	 * this method is used to apply refactoring on real code
	 */
	public abstract boolean apply();
	
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
	 * this method is used to check whether a refactoring opportunity still hold true, on the process of checking
	 * validity, some opportunity might be updated and then get validated.
	 * @param model
	 * @return
	 */
	protected abstract boolean checkLegal(ProgramModel model);
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
		
		ModuleWrapper module = null;
		double fitness = 0;
		
		for(ModuleWrapper m: moduleList){
			newUnit.setMappingModule(m);
			double f = new AdvanceEvaluatorAdapter().computeFitness(model, moduleList);
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
		
		return module;
	}
	

	protected CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); 
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit); // set source
		parser.setResolveBindings(true); // we need bindings later on
		return (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
	}
	
	protected Name createQualifiedName(AST ast, String classToImport) {
		String[] parts = classToImport.split("\\."); //$NON-NLS-1$

		Name name = null;

		for (int i = 0; i < parts.length; i++) {
			SimpleName simpleName = ast.newSimpleName(parts[i]);
			if (i == 0) {
				name = simpleName;
			} else {
				name = ast.newQualifiedName(name, simpleName);
			}
		}
		return name;
	}
}
