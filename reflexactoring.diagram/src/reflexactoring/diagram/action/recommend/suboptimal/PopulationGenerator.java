/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;
import java.util.HashMap;

import reflexactoring.diagram.action.recommend.MemberModuleValidityExaminer;
import reflexactoring.diagram.action.recommend.RecommendUtil;
import reflexactoring.diagram.bean.GraphRelationType;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ReferencingDetail;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapperList;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * Generate population for genetic algorithm based on current map and fix and stop rules see {@link Rules}.
 * 
 * @author linyun
 *
 */
public class PopulationGenerator {
	
	private int populationSize;
	private FitnessEvaluator evaluator;
	
	public PopulationGenerator(int popSize, FitnessEvaluator evaluator){
		this.populationSize = popSize;
		this.evaluator = evaluator;
	}
	
	public Population createPopulation(ArrayList<ICompilationUnitWrapper> unitList, ArrayList<ModuleWrapper> moduleList){
		/**
		 * one map is a solution, current mapping solution on graph is seed DNA.
		 */
		int[] seedDNA = new int[unitList.size()];
		for(int i=0; i<unitList.size(); i++){
			ICompilationUnitWrapper unit = unitList.get(i);
			ModuleWrapper module = unit.getMappingModule();
			int index = ReflexactoringUtil.getModuleIndex(moduleList, module);
			
			seedDNA[i] = index;
		}
		
		/**
		 * Based on fix and stop rules, I generate the population by randomly changing some mapping.
		 */
		Rules rules = new Rules();
		Population population = generateRandomPopualtion(rules.getUnitModuleFixList(), rules.getUnitModuleStopList(), 
				seedDNA, moduleList.size(), populationSize, true);
		
		return population;
	}
	
	private double[][] computeMemberSimilarity(){
		ArrayList<ModuleWrapper> modules = ReflexactoringUtil.getModuleList(Settings.diagramPath);
		UnitMemberWrapperList members = Settings.scope.getScopeMemberList();
		
		MemberModuleValidityExaminer examiner = new MemberModuleValidityExaminer();
		double[][] similarityTable = new double[modules.size()][members.size()];
		
		for(int i=0; i<modules.size(); i++){
			ModuleWrapper module = modules.get(i);
			//module.extractTermFrequency(module.getDescription());
			for(int j=0; j<members.size(); j++){
				UnitMemberWrapper member = members.get(j);
				
				if(examiner.isValid(member, module)){
					double similarity = module.computeSimilarity(member);
					similarityTable[i][j] = Double.valueOf(ReflexactoringUtil.getMappingThreshold()) + similarity;					
				}
				else{
					similarityTable[i][j] = Double.valueOf(ReflexactoringUtil.getMappingThreshold()) - 1;
				}
				
				/*if(member.getJavaMember().toString().contains("listen")){
					System.out.println(member.getJavaMember().toString() + " " + module.getName() + ":" + similarityTable[i][j]);			
					System.currentTimeMillis();
					if(similarityTable[i][j] == -1){
						System.currentTimeMillis();
						examiner.isValid(member, module);
					}
				}*/
			}
		}
		
		return similarityTable;
	}

	/**
	 * @param memberModuleFixList
	 * @param memberModuleStopList
	 * @param seedDNA
	 * @param popSize
	 * @return
	 */
	private Population generateRandomPopualtion(
			HashMap<Integer, Integer> fixList,
			HashMap<Integer, ArrayList<Integer>> stopList, int[] seedDNA, int moduleNum,
			int popSize, boolean isForTypePopulation) {
		//Rules rules = new Rules();
		
		Population population = new Population();
		
		//int moduleNum = ReflexactoringUtil.getModuleList(Settings.diagramPath).size();
		for(int i=0; i<popSize; i++){
			int[] DNA = new int[seedDNA.length];
			
			for(int j=0; j<seedDNA.length; j++){
				/**
				 * fixed map cannot be moved.
				 */
				if(fixList.containsKey(j)){
					DNA[j] = seedDNA[j];
				}
				/**
				 * try to randomly change some mapping w.r.t stop list, i.e., a forbidden map
				 * will not be generated.
				 */
				else{
					ArrayList<Integer> availabelModuleIndexList = new ArrayList<>();
					ArrayList<Integer> stopModuleIndexList = stopList.get(j);
					for(int k=0; k<moduleNum; k++){
						if((stopModuleIndexList == null) || (!stopModuleIndexList.contains(k))){
							availabelModuleIndexList.add(k);
						}
					}
					
					if(availabelModuleIndexList.size() > 0){
						int index = (int) (Math.random()*availabelModuleIndexList.size());
						DNA[j] = availabelModuleIndexList.get(index);
					}
					/**
					 * For error information, i.e., no available low level nodes mapped to module.
					 */
					else{
						DNA[j] = seedDNA[j];
						
						String lowLevelModelName;
						if(isForTypePopulation){
							lowLevelModelName = Settings.scope.getScopeCompilationUnitList().get(j).getName();
						}
						else{
							lowLevelModelName = Settings.scope.getScopeMemberList().get(j).getName();
						}
						System.err.println("No possible module could be mapped to " + lowLevelModelName);
					}
				}					
			}
			
			Genotype gene = new Genotype(DNA, seedDNA, evaluator);
			population.add(gene);
		}
		
		return population;
	}
}
