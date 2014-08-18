/**
 * 
 */
package reflexactoring.diagram.action.smelldetection;

import java.util.ArrayList;

import reflexactoring.diagram.action.recommend.suboptimal.FitnessEvaluator;
import reflexactoring.diagram.action.recommend.suboptimal.FitnessEvaluatorFactory;
import reflexactoring.diagram.action.recommend.suboptimal.Genotype;
import reflexactoring.diagram.action.recommend.suboptimal.Violation;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.programmodel.ProgramModel;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class AdvanceEvaluatorAdapter {
	
	private ArrayList<Violation> violationList = new ArrayList<>();
	
	public double computeFitness(ProgramModel model, ArrayList<ModuleWrapper> moduleList){
		
		//long t1 = System.currentTimeMillis();
		FitnessEvaluator evaluator = FitnessEvaluatorFactory.createFitnessEvaluator(model, moduleList,
				FitnessEvaluator.ADVANCED_EVALUATOR);
		//long t2 = System.currentTimeMillis();
		//System.out.println("Extract model time: " + (t2-t1));
		
		int[] DNA = constructDNA(model, moduleList);
		
		Genotype gene = new Genotype(DNA, null, evaluator);
		
		//long t3 = System.currentTimeMillis();
		double structureAndLexicalFitness = gene.getFitness();
		//long t4 = System.currentTimeMillis();
		//System.out.println("Compute fitness time: " + (t4-t3));
		
		double CBO = model.computeNormalizedCBOMetrics();
		double LCOM = model.computeNormalizedLCOMMetrics();
		
		this.violationList = gene.getViolationList();
		
		double fitness = (1-CBO) + (1-LCOM) + structureAndLexicalFitness;
		return fitness;
	}

	/**
	 * @return the violationList
	 */
	public ArrayList<Violation> getViolationList() {
		return violationList;
	}

	/**
	 * @param model
	 * @param moduleList
	 * @return
	 */
	private int[] constructDNA(ProgramModel model,
			ArrayList<ModuleWrapper> moduleList) {
		
		int[] DNA = new int[model.getScopeCompilationUnitList().size()];
		for(int i=0; i<DNA.length; i++){
			ModuleWrapper module = model.getScopeCompilationUnitList().get(i).getMappingModule();
			int index = ReflexactoringUtil.getModuleIndex(moduleList, module);
			DNA[i] = index;
		}
		
		return DNA;
		
	}
	
}
