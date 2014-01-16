package reflexactoring.diagram.action;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

public class AutoMappingAction implements IWorkbenchWindowActionDelegate {

	
	
	@Override
	public void run(IAction action) {
		try {
			ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
			ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList = new ArrayList<>();
			for(ICompilationUnit unit: Settings.scope.getScopeCompilationUnitList()){
				compilationUnitWrapperList.add(new ICompilationUnitWrapper(unit));
			}
			
			ArrayList<ModuleWrapper> moduleListWithNoDesc = checkModulesNotDescribedYet(moduleList);
			if(moduleListWithNoDesc.size() != 0){
				/**
				 * Open a message dialog here, inform user of the to-be-described module.
				 */
				String message = "All modules should be described before performing mapping, please fill in descriprions for following modules:\n\n";
				for (ModuleWrapper moduleWrapper : moduleListWithNoDesc) {
					message += moduleWrapper.getName() + "\n";
				}
				JOptionPane.showMessageDialog(null, message, "Modules with no description", JOptionPane.ERROR_MESSAGE);				
				
				return;
			}
			
			compilationUnitWrapperList = buildStructuralDependency(compilationUnitWrapperList);
			
			new ModelMapper().generateMappingRelation(moduleList, compilationUnitWrapperList);
			
			new DiagramUpdater().generateReflexionModel(moduleList, compilationUnitWrapperList);
			
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Checking whether all the description of module has been filled in.
	 * @param moduleList
	 * @return
	 */
	private ArrayList<ModuleWrapper> checkModulesNotDescribedYet(ArrayList<ModuleWrapper> moduleList){
		
		ArrayList<ModuleWrapper> moduleListWithNoDesc = new ArrayList<>();
		for(ModuleWrapper module : moduleList){
			if(module.getDescription().equals("") || module.getDescription() == null){
				moduleListWithNoDesc.add(module);
			}
		}
		return moduleListWithNoDesc;
	}

	/**
	 * Identifying the reference relations in classes/interfaces in scope.
	 * @param compilationUnitList
	 * @return
	 */
	private ArrayList<ICompilationUnitWrapper> buildStructuralDependency(final ArrayList<ICompilationUnitWrapper> compilationUnitList){
		for(final ICompilationUnitWrapper refererCompilationUnit: compilationUnitList){
			final CompilationUnit compilationUnit = refererCompilationUnit.getJavaUnit();
			compilationUnit.accept(new ASTVisitor() {
				/**
				 * Currently, I just simplify the problem by considering only SimpleType. It could be extended to
				 * other subclasses of Type.
				 */
				public boolean visit(SimpleType type){
					String typeName = type.getName().getFullyQualifiedName();
					for(ICompilationUnitWrapper refereeCompilationUnit: compilationUnitList){
						String tobeComparedName = refereeCompilationUnit.getCompilationUnit().getElementName();
						tobeComparedName = tobeComparedName.substring(0, tobeComparedName.indexOf(".java"));
						if(typeName.equals(tobeComparedName) && !refererCompilationUnit.equals(refereeCompilationUnit)){
							refererCompilationUnit.addCalleeCompilationUnit(refereeCompilationUnit);
							refereeCompilationUnit.addCallerCompilationUnit(refererCompilationUnit);
						}
					}
					
					
					return true;
				}
			});
		}
		
		return compilationUnitList;
	}
	
	

	
	
	
	

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}
