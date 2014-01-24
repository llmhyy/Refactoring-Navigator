package reflexactoring.diagram.action;

import java.util.ArrayList;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleUnitsSimilarityTable;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.perspective.ReflexactoringPerspective;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;
import reflexactoring.diagram.view.ModuleUnitsSimilarityView;

public class AutoMappingAction implements IWorkbenchWindowActionDelegate {

	private final static String OK_MESSAGE = "ok";
	
	@Override
	public void run(IAction action) {
		try {
			ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
			String message = checkValidity(moduleList);
			if(!AutoMappingAction.OK_MESSAGE.equals(message)){
				MessageDialog.openError(null, "Validity Error", message);
				return;
			}
			
			/*ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList = new ArrayList<>();
			for(ICompilationUnit unit: Settings.scope.getScopeCompilationUnitList()){
				compilationUnitWrapperList.add(new ICompilationUnitWrapper(unit));
			}
			compilationUnitWrapperList = buildStructuralDependency(compilationUnitWrapperList);*/
			ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList 
				= Settings.scope.getScopeCompilationUnitList();
			
			ModelMapper mapper = new ModelMapper();
			
			double[][] similarityTable = mapper.generateMappingRelation(moduleList, compilationUnitWrapperList);
			//double[][] similarityTable = mapper.computeSimilarityTableWithRegardToHeurisitcRules(moduleList, compilationUnitWrapperList);
			ModuleUnitsSimilarityTable table = ReflexactoringUtil.convertRawTableToModuleUnitsSimilarityTable(similarityTable, 
					moduleList, compilationUnitWrapperList);
			Settings.similarityTable = table;
			
			ModuleUnitsSimilarityView view = (ModuleUnitsSimilarityView)PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getActivePage().findView(ReflexactoringPerspective.MODULE_TYPE_SIMILARITY_VIEW);
			view.refreshUI(Settings.similarityTable);
			//view.getViewer().setContentProvider(new ArrayContentProvider());
			//view.getViewer().setInput(Settings.similarityTable);
			//view.getViewer().refresh();
			
			
			
			new DiagramUpdater().generateReflexionModel(moduleList, compilationUnitWrapperList);
			
			/**
			 * after all, the recompute settings should be set false to improve efficiency
			 */
			Settings.isCompliationUnitChanged = false;
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Check three points:
	 * 1) Whether any module with no name;
	 * 2) Whether any module with no desc;
	 * 3) WHether there is any duplicated name amongst modules
	 * @param moduleList
	 * @return
	 */
	private String checkValidity(ArrayList<ModuleWrapper> moduleList){
		//TODO
		/**
		 * checking modules with no name
		 */
		boolean isAnyModuleWithNoName = checkModulesWithNoName(moduleList);
		if(isAnyModuleWithNoName){
			/**
			 * return a message
			 */
			String message = "There is module with no name, please fill in names for all modules";
			return message;
		}
		
		/**
		 * checking modules with no description.
		 */
		ArrayList<ModuleWrapper> modulesWithNoDesc = checkModulesNotDescribedYet(moduleList);
		if(modulesWithNoDesc.size() != 0){
			/**
			 * Open a message dialog here, inform user of the to-be-described module.
			 */
			String message = "All modules should be described before performing mapping, please fill in descriprions for following modules:\n\n";
			for (ModuleWrapper moduleWrapper : modulesWithNoDesc) {
				message += moduleWrapper.getName() + "\n";
			}			
			//MessageDialog.openError(null, "Modules with no description", message);
			return message;
		}
		
		//TODO
		/**
		 * checking modules with duplicated name
		 */
		String duplicatedModuleName = checkDuplicatedModuleName(moduleList);
		if(duplicatedModuleName != null){
			/**
			 * return a message
			 */
			String message = "Modules should not have duplicated names, please eliminate duplication of following module names:\n\n" + duplicatedModuleName;
			return message;
		}
		
		/**
		 * if no error message, just return the OK message.
		 */
		return AutoMappingAction.OK_MESSAGE;
	}
	
	private boolean checkModulesWithNoName(ArrayList<ModuleWrapper> moduleList){
		for (ModuleWrapper moduleWrapper : moduleList) {
			if(moduleWrapper.getName() == null || moduleWrapper.getName().equals(""))
				return true;
		}
		return false;
	}
	
	private String checkDuplicatedModuleName(ArrayList<ModuleWrapper> moduleList){
		ArrayList<String> moduleName = new ArrayList<>();
		ArrayList<String> duplicatedName = new ArrayList<>();
		String duplicatedModuleName = null;
		for (ModuleWrapper moduleWrapper : moduleList) {
			String name = moduleWrapper.getName();
			if(moduleName.contains(name)){
				if(!duplicatedName.contains(name)){
					duplicatedName.add(name);
					if(duplicatedModuleName == null){
						duplicatedModuleName = "";
					}
					duplicatedModuleName += moduleWrapper.getName() + "\n";
				}
			}
			else{
				moduleName.add(name);
			}
		}
		return duplicatedModuleName;
	}
	
	/**
	 * Checking whether all the description of module has been filled in.
	 * @param moduleList
	 * @return
	 */
	private ArrayList<ModuleWrapper> checkModulesNotDescribedYet(ArrayList<ModuleWrapper> moduleList){
		
		ArrayList<ModuleWrapper> moduleListWithNoDesc = new ArrayList<>();
		for(ModuleWrapper module : moduleList){
			if(module.getDescription() == null || module.getDescription().equals("")){
				moduleListWithNoDesc.add(module);
			}
		}
		return moduleListWithNoDesc;
		
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
