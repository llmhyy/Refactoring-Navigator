/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import reflexactoring.diagram.action.recommend.action.AddCreationAction;
import reflexactoring.diagram.action.recommend.action.AddDependencyAction;
import reflexactoring.diagram.action.recommend.action.AddExtendAction;
import reflexactoring.diagram.action.recommend.action.AddModuleAction;
import reflexactoring.diagram.action.recommend.action.DeleteCreationAction;
import reflexactoring.diagram.action.recommend.action.DeleteDependencyAction;
import reflexactoring.diagram.action.recommend.action.DeleteExtendAction;
import reflexactoring.diagram.action.recommend.action.LinkAction;
import reflexactoring.diagram.action.recommend.suboptimal.AdvancedCrossoverer;
import reflexactoring.diagram.action.recommend.suboptimal.AdvancedFitnessEvaluator;
import reflexactoring.diagram.action.recommend.suboptimal.DefaultCrossoverer;
import reflexactoring.diagram.action.recommend.suboptimal.DefaultFitnessEvaluator;
import reflexactoring.diagram.action.recommend.suboptimal.DefaultMutator;
import reflexactoring.diagram.action.recommend.suboptimal.DefaultSelector;
import reflexactoring.diagram.action.recommend.suboptimal.GeneticOptimizer;
import reflexactoring.diagram.action.recommend.suboptimal.Genotype;
import reflexactoring.diagram.action.recommend.suboptimal.Population;
import reflexactoring.diagram.action.recommend.suboptimal.PopulationGenerator;


import reflexactoring.diagram.action.recommend.suboptimal.Rules;
import reflexactoring.diagram.action.recommend.suboptimal.Violation;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleLinkWrapper;
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
	
	public ArrayList<Suggestion> recommendStartByClass(IProgressMonitor monitor) /*throws PartInitException*/{
		
		/**
		 * Give suggestions for unmapped class.
		 */
		ArrayList<ICompilationUnitWrapper> unmappedUnits = checkUnmappedCompilationUnits();
		if(unmappedUnits.size() != 0 && !Settings.isSkipUnMappedTypes){
			Suggestion suggestion = generateSuggestionForUnmappedUnits(unmappedUnits);
			ArrayList<Suggestion> suggestions = new ArrayList<>();
			suggestions.add(suggestion);
			return suggestions;
		}
		
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
		
		/**
		 * Now, the the pre-check has been finished, we start GA for refactoring suggestions.
		 */
		Rules rules = new Rules();
		
		PopulationGenerator popGenerator = new PopulationGenerator(Integer.valueOf(ReflexactoringUtil.getPopulationSize()));
		Population population = popGenerator.createPopulation(Settings.scope.getScopeCompilationUnitList(), moduleList);
		/*GeneticOptimizer optimizer = new GeneticOptimizer(population, new DefaultSelector(), 
				new DefaultCrossoverer(), new DefaultMutator(rules.getUnitModuleFixList(), rules.getUnitModuleStopList(), moduleList.size()));*/
		GeneticOptimizer optimizer = new GeneticOptimizer(population, new DefaultSelector(), 
				new AdvancedCrossoverer(), new DefaultMutator(rules.getUnitModuleFixList(), rules.getUnitModuleStopList(), moduleList.size()));
		
		Population pop = optimizer.optimize();
		
		/**
		 * The first genotype in population should be the best genotype
		 */
		Genotype bestGene = pop.getList().get(0);
		Suggestion suggestion = generateClassLevelSuggestion(bestGene, moduleList);
		
		ArrayList<Suggestion> suggestions = generateSuggestions(bestGene, suggestion, moduleList);
		return suggestions;
	}
	
	public ArrayList<Suggestion> recommendStartByMember(IProgressMonitor monitor){
		ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
		
		Rules rules = new Rules();
		
		PopulationGenerator popGenerator = new PopulationGenerator(Integer.valueOf(ReflexactoringUtil.getPopulationSize()));
		Population population = popGenerator.createPopulation(Settings.scope.getScopeMemberList(), moduleList);
		/*GeneticOptimizer optimizer = new GeneticOptimizer(population, new DefaultSelector(), 
				new DefaultCrossoverer(), new DefaultMutator(rules.getMemberModuleFixList(), rules.getMemberModuleStopList(), moduleList.size()));*/
		GeneticOptimizer optimizer = new GeneticOptimizer(population, new DefaultSelector(), 
				new AdvancedCrossoverer(), new DefaultMutator(rules.getUnitModuleFixList(), rules.getUnitModuleStopList(), moduleList.size()));
		
		Population pop = optimizer.optimize();
		
		/**
		 * The first genotype in population should be the best genotype
		 */
		Genotype bestGene = pop.getList().get(0);
		Suggestion suggestion = generateMemberLevelSuggestion(bestGene, moduleList);				
		
		ArrayList<Suggestion> suggestions = generateSuggestions(bestGene, suggestion, moduleList);
		return suggestions;
	}
	
	
	
	/**
	 * Given a best gene and its current suggestion, generate suggestions w.r.t its feasibility.
	 * @param bestGene
	 * @return
	 */
	private ArrayList<Suggestion> generateSuggestions(Genotype bestGene, Suggestion suggestion, 
			ArrayList<ModuleWrapper> moduleList){
		ArrayList<Suggestion> suggestions = new ArrayList<>();
		if(bestGene.isFeasible()){
			suggestion.setFeasible(true);
			suggestions.add(suggestion);
		}
		else{
			suggestion.setFeasible(false);
			
			ArrayList<SuggestionMove> highLevelSuggestion = findHighLevelModificationSuggestion(
					((AdvancedFitnessEvaluator)bestGene.getEvaluator()).getViolationList(), moduleList);
			for(SuggestionMove move: highLevelSuggestion){
				suggestion.add(0, move);
			}
			suggestions.add(suggestion);
		}
		
		return suggestions;
	}

	public ArrayList<SuggestionMove> findHighLevelModificationSuggestion(ArrayList<Violation> violations, ArrayList<ModuleWrapper> moduleList){
		ArrayList<SuggestionMove> suggestions = new ArrayList<>();
		
		/*ArrayList<Violation> violations = ((DefaultFitnessEvaluator)bestGene.getEvaluator()).getViolationList();*/
		for(Violation violation: violations){
			ModuleWrapper sourceModule = moduleList.get(violation.getSourceModuleIndex());
			ModuleWrapper targetModule = moduleList.get(violation.getDestModuleIndex());
			
			LinkAction action = null;
			ModuleLinkWrapper link = null;
			
			if(violation.getType() == Violation.DEPENDENCY_ABSENCE){
				action = new DeleteDependencyAction();
				action.setOrigin(sourceModule);
				action.setDestination(targetModule);
				link = new ModuleLinkWrapper(sourceModule, targetModule, ModuleLinkWrapper.MODULE_DEPENDENCY);
			}
			else if(violation.getType() == Violation.DEPENDENCY_DIVERGENCE){
				action = new AddDependencyAction();
				action.setOrigin(sourceModule);
				action.setDestination(targetModule);	
				link = new ModuleLinkWrapper(sourceModule, targetModule, ModuleLinkWrapper.MODULE_DEPENDENCY);
			}
			else if(violation.getType() == Violation.INHERITANCE_ABSENCE){
				action = new DeleteExtendAction();
				action.setOrigin(sourceModule);
				action.setDestination(targetModule);
				link = new ModuleLinkWrapper(sourceModule, targetModule, ModuleLinkWrapper.MODULE_EXTEND);
			}
			else if(violation.getType() == Violation.INHERITANCE_DIVERGENCE){
				action = new AddExtendAction();
				action.setOrigin(sourceModule);
				action.setDestination(targetModule);	
				link = new ModuleLinkWrapper(sourceModule, targetModule, ModuleLinkWrapper.MODULE_EXTEND);
			}
			else if(violation.getType() == Violation.CREATION_ABSENCE){
				action = new DeleteCreationAction();
				action.setOrigin(sourceModule);
				action.setDestination(targetModule);
				link = new ModuleLinkWrapper(sourceModule, targetModule, ModuleLinkWrapper.MODULE_CREATION);
			}
			else if(violation.getType() == Violation.CREATION_DIVERGENCE){
				action = new AddCreationAction();
				action.setOrigin(sourceModule);
				action.setDestination(targetModule);	
				link = new ModuleLinkWrapper(sourceModule, targetModule, ModuleLinkWrapper.MODULE_CREATION);
			}
			
			SuggestionMove suggestion = new SuggestionMove(link, action);
			suggestions.add(suggestion);
		}
		
		return suggestions;
	}

	/**
	 * Check possibility by similarity and threshold, i.e., if there is a java file whose similarity with any
	 * module is lower than user-defined threshold, it will be considered as not possible to map.
	 * @param modules
	 * @param units
	 * @return
	 */
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
	
	private Suggestion generateClassLevelSuggestion(Genotype gene, ArrayList<ModuleWrapper> moduleList){
		int[] bestSolution = gene.getDNA();
		int[] initialSolution = gene.getOriginalDNA();
		
		Suggester suggester = new Suggester();
		Suggestion suggestion = suggester.generateSuggestion(Settings.scope.getScopeCompilationUnitList(), moduleList, 
				bestSolution, initialSolution);
		
		return suggestion;
	}
	
	private Suggestion generateMemberLevelSuggestion(Genotype gene, 
			ArrayList<ModuleWrapper> moduleList){
		int[] bestSolution = gene.getDNA();
		int[] initialSolution = gene.getOriginalDNA();
		
		Suggester suggester = new Suggester();
		Suggestion suggestion = suggester.generateSuggestion(Settings.scope.getScopeMemberList(), moduleList, 
				bestSolution, initialSolution);
		
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
