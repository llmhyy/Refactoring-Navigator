/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

import javax.swing.ProgressMonitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.UnitMemberExtractor;
import reflexactoring.diagram.action.recommend.action.AddDependencyAction;
import reflexactoring.diagram.action.recommend.action.AddModuleAction;
import reflexactoring.diagram.action.recommend.action.DeleteDependencyAction;
import reflexactoring.diagram.action.recommend.action.DependencyAction;
import reflexactoring.diagram.action.recommend.suboptimal.GeneticOptimizer;
import reflexactoring.diagram.action.recommend.suboptimal.Genotype;
import reflexactoring.diagram.action.recommend.suboptimal.Violation;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleDependencyWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapperList;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * This class is used to recommend refactoring suggestions to help user reconcile the conflicts between
 * high-level and low-level models. 
 * 
 * First, it will check whether there is any unmapped java types on
 * graph, if yes, it will first suggest the user to 1) add new module and 2) manually map them to other
 * modules. 
 * 
 * Second, it will search an optimal mapping on class level, i.e., it will try find an optimal 
 * movement for class. If it is not possible to move class to make two level consistent, it will provide
 * two choices: 1) modify the relation of high-level modules, if the user insists on maintain the high-level
 * module relations, 2)this class will try to break the classes and find an optimal mapping for methods and 
 * fields. 
 * 
 * Third, if it is still not possible to find an mapping conforming to the consistency of both module, there
 * will be two choices: 1) modify the relation of high-level modules, if the user insists on maintain the 
 * high-level module relations, 2)this class will try to break some methods with heuristic rules.
 * 
 * @author linyun
 *
 */
public class RefactoringRecommender {
	
	private String title = "Cannot find a solution";
	private String message = "I will show you the best solutions (still infeasible yet)";
	
	public RefactoringRecommender(){
		
	}
	
	public ArrayList<Suggestion> recommendStartByClass(IProgressMonitor monitor){
		
		ArrayList<ICompilationUnitWrapper> unmappedUnits = checkUnmappedCompilationUnits();
		if(unmappedUnits.size() != 0 && !Settings.isSkipUnMappedTypes){
			Suggestion suggestion = generateSuggestionForUnmappedUnits(unmappedUnits);
			ArrayList<Suggestion> suggestions = new ArrayList<>();
			suggestions.add(suggestion);
			return suggestions;
		}
		
		try {
			ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
			/**
			 * need to gain the dependencies amongst modules
			 */
			
			final String msg = checkPossible(moduleList, Settings.scope.getScopeCompilationUnitList());
			
			if(!msg.equals("OK")){
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Problem", msg);
						
					}
				});
				return new ArrayList<Suggestion>();
			}
			
			GeneticOptimizer optimizer = new GeneticOptimizer();
			ArrayList<Genotype> unitGeneList = optimizer.optimize(Settings.scope.getScopeCompilationUnitList(), moduleList, monitor);
			
			ArrayList<Suggestion> suggestions = new ArrayList<>();
			for(Genotype unitGene: unitGeneList){
				Suggestion suggestion = generateClassLevelSuggestion(unitGene, optimizer, moduleList);
				
				if(unitGene.isFeasible()){
					suggestion.setFeasible(true);
					suggestions.add(suggestion);
				}
				else{
					suggestion.setFeasible(false);
					ArrayList<SuggestionMove> highLevelSuggestion = findHighLevelModificationSuggestion(unitGene, moduleList);
					for(SuggestionMove move: highLevelSuggestion){
						suggestion.add(0, move);
					}
					suggestions.add(suggestion);
				}
				
			}
			
			return suggestions;
			
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<Suggestion>();
	}
	
	public ArrayList<Suggestion> recommendStartByMember(IProgressMonitor monitor){
		
		ArrayList<ModuleWrapper> moduleList;
		try {
			moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
			
			GeneticOptimizer optimizer = new GeneticOptimizer();
			
			//UnitMemberExtractor extractor = new UnitMemberExtractor();
			//UnitMemberWrapperList members = extractor.extract(Settings.scope.getScopeCompilationUnitList());
			UnitMemberWrapperList members = Settings.scope.getScopeMemberList();
			
			ArrayList<Genotype> memberGenes = optimizer.optimize(members, moduleList, monitor);
			
			ArrayList<Suggestion> suggestions = new ArrayList<>();
			
			for(Genotype memberGene: memberGenes){
				Suggestion suggestion = generateMemberLevelSuggestion(memberGene, optimizer, 
						moduleList, members);				
				
				if(memberGene.isFeasible()){
					suggestion.setFeasible(true);
					suggestions.add(suggestion);
				}
				else{
					suggestion.setFeasible(false);
					ArrayList<SuggestionMove> highLevelSuggestion = findHighLevelModificationSuggestion(memberGene, moduleList);
					for(SuggestionMove move: highLevelSuggestion){
						suggestion.add(0, move);
					}
					suggestions.add(suggestion);
				}
				
			}
			
			return suggestions;
			
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		
		return new ArrayList<Suggestion>();
	}
	
	private ArrayList<SuggestionMove> findHighLevelModificationSuggestion(Genotype bestGene, ArrayList<ModuleWrapper> moduleList){
		ArrayList<SuggestionMove> suggestions = new ArrayList<>();
		
		ArrayList<Violation> violations = bestGene.getViolationList();
		for(Violation violation: violations){
			if(Settings.confidenceTable.get(violation.getSourceModuleIndex()).
					getConfidenceList()[violation.getDestModuleIndex()] < 1){
				ModuleWrapper sourceModule = moduleList.get(violation.getSourceModuleIndex());
				ModuleWrapper targetModule = moduleList.get(violation.getDestModuleIndex());
				
				DependencyAction action = null;
				
				if(violation.getType() == Violation.ABSENCE){
					action = new DeleteDependencyAction();
					action.setOrigin(sourceModule);
					action.setDestination(targetModule);
				}
				else if(violation.getType() == Violation.DISONANCE){
					action = new AddDependencyAction();
					action.setOrigin(sourceModule);
					action.setDestination(targetModule);	
				}
				
				ModuleDependencyWrapper dependency = new ModuleDependencyWrapper(sourceModule, targetModule);
				SuggestionMove suggestion = new SuggestionMove(dependency, action);
				suggestions.add(suggestion);
			}
		}
		
		return suggestions;
	}

	private String checkPossible(ArrayList<ModuleWrapper> modules, ArrayList<ICompilationUnitWrapper> units){
		if(units.size() < modules.size()){
			return "The number of java file is less than the number of module.";
		}
		
		double[][] similarityTable = Settings.similarityTable.convertModuleUnitsSimilarityTableToRawTable();
		for(int i=0; i<modules.size(); i++){
			boolean hasSomeUnitToMatch = false;
			for(int j=0; j<units.size(); j++){
				if(similarityTable[i][j] >= Double.valueOf(ReflexactoringUtil.getMappingThreshold())){
					hasSomeUnitToMatch = true;
				}
			}
			
			if(!hasSomeUnitToMatch){
				ModuleWrapper module = modules.get(i);
				return "The module " + module.getName() + " is not able to match any java file.";
			}
		}
		
		for(int j=0; j<units.size(); j++){
			boolean hasSomeModuleToMatch = false;
			for(int i=0; i<modules.size(); i++){
				if(similarityTable[i][j] >= Double.valueOf(ReflexactoringUtil.getMappingThreshold())){
					hasSomeModuleToMatch = true;
				}
			}
			
			if(!hasSomeModuleToMatch){
				ICompilationUnitWrapper unit = units.get(j);
				return "The java file " + unit.getName() + " is not able to match any module.";
			}
		}
		
		return "OK";
	}
	
	private Suggestion generateMemberLevelSuggestion(Genotype gene, GeneticOptimizer optimizer, 
			ArrayList<ModuleWrapper> moduleList, UnitMemberWrapperList members){
		int[] bestSolution = gene.getDNA();
		
		int[] initialSolution = optimizer.getX0();
		ArrayList<int[]> relationMap = optimizer.getRelationMap();
		
		Suggester suggester = new Suggester();
		Suggestion suggestion = suggester.generateSuggestion(members, moduleList, 
				bestSolution, initialSolution, relationMap);
		
		return suggestion;
	}
	
	private Suggestion generateClassLevelSuggestion(Genotype gene, GeneticOptimizer optimizer, ArrayList<ModuleWrapper> moduleList){
		int[] bestSolution = gene.getDNA();
		
		int[] initialSolution = optimizer.getX0();
		ArrayList<int[]> relationMap = optimizer.getRelationMap();
		
		Suggester suggester = new Suggester();
		Suggestion suggestion = suggester.generateSuggestion(Settings.scope.getScopeCompilationUnitList(), moduleList, 
				bestSolution, initialSolution, relationMap);
		
		return suggestion;
	}
	
	private Suggestion generateSuggestionForUnmappedUnits(ArrayList<ICompilationUnitWrapper> unmappedUnits){
		
		AddModuleAction action = new AddModuleAction(unmappedUnits);
		
		SuggestionMove move = new SuggestionMove(null, action);
		
		Suggestion suggestion = new Suggestion();
		suggestion.add(move);
		
		return suggestion;
	}
	
	private ArrayList<ICompilationUnitWrapper> checkUnmappedCompilationUnits(){
		ArrayList<ICompilationUnitWrapper> list = new ArrayList<>();
		for(ICompilationUnitWrapper unit: Settings.scope.getScopeCompilationUnitList()){
			if(unit.getMappingModule() == null){
				list.add(unit);
			}
		}
		
		return list;
	}
	
}
