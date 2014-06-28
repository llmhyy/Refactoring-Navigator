/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.ui.PartInitException;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import reflexactoring.diagram.action.recommend.MemberModuleValidityExaminer;
import reflexactoring.diagram.bean.GraphNode;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
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
	
	public PopulationGenerator(int popSize){
		this.populationSize = popSize;
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
		 * Based on fix and stop rules, I generate the population with randomly changed some mapping.
		 */
		Rules rules = new Rules();
		Population population = generateRandomPopualtion(rules.getUnitModuleFixList(), rules.getUnitModuleStopList(), 
				seedDNA, moduleList.size(), populationSize, Settings.similarityTable.convertModuleUnitsSimilarityTableToRawTable(), true);
		
		return population;
	}
	
	public Population createPopulation(UnitMemberWrapperList memberList, ArrayList<ModuleWrapper> moduleList){
		
		int[] seedDNA = new int[memberList.size()];
		for(int i=0; i<memberList.size(); i++){
			UnitMemberWrapper member = memberList.get(i);
			ModuleWrapper module = member.getMappingModule();
			
			int index = ReflexactoringUtil.getModuleIndex(moduleList, module);
			
			seedDNA[i] = index;
		}
		
		Rules rules = new Rules();
		double[][] similarityTable = computeMemberSimilarity();
		Population population = generateRandomPopualtion(rules.getMemberModuleFixList(), rules.getMemberModuleStopList(), 
				seedDNA, moduleList.size(), populationSize, similarityTable, false);
		
		return population;
	}
	
	private double[][] computeMemberSimilarity(){
		ArrayList<ModuleWrapper> modules = ReflexactoringUtil.getModuleList(Settings.diagramPath);
		UnitMemberWrapperList members = Settings.scope.getScopeMemberList();
		
		MemberModuleValidityExaminer examiner = new MemberModuleValidityExaminer();
		double[][] similarityTable = new double[modules.size()][members.size()];
		
		for(int i=0; i<modules.size(); i++){
			ModuleWrapper module = modules.get(i);
			module.extractTermFrequency(module.getDescription());
			for(int j=0; j<members.size(); j++){
				UnitMemberWrapper member = members.get(j);
				if(examiner.isValid(member, module)){
					double similarity = module.computeSimilarity(member);
					//System.currentTimeMillis();
					similarityTable[i][j] = Double.valueOf(ReflexactoringUtil.getMappingThreshold()) + similarity;					
				}
				else{
					similarityTable[i][j] = Double.valueOf(ReflexactoringUtil.getMappingThreshold()) - 1;
				}
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
			int popSize, double[][] similarityTable, boolean isForTypePopulation) {
		//Rules rules = new Rules();
		
		Population population = new Population();
		
		double[][] highLevelNodeMatrix = extractGraph(ReflexactoringUtil.getModuleList(Settings.diagramPath));
		double[][] lowLevelNodeMatrix;
		
		if(isForTypePopulation){
			lowLevelNodeMatrix = extractGraph(Settings.scope.getScopeCompilationUnitList());
		}
		else{
			lowLevelNodeMatrix = extractGraph(Settings.scope.getScopeMemberList());
		}
		
		
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
			
			/*if(!ReflexactoringUtil.checkCorrectMapping(DNA, rules)){
				System.currentTimeMillis();
			}*/
			
			Genotype gene = new Genotype(DNA, seedDNA, 
					new DefaultFitnessEvaluator(similarityTable, highLevelNodeMatrix, lowLevelNodeMatrix));
			population.add(gene);
		}
		
		return population;
	}
	
	private double[][] extractGraph(ArrayList<? extends GraphNode> nodes){
		
		int dimension = nodes.size();
		double[][] graphMatrix = new double[dimension][dimension];
		
		for(int i=0; i<dimension; i++){
			for(int j=0; j<dimension; j++){
				if(i != j){
					GraphNode nodeI = nodes.get(i);
					GraphNode nodeJ = nodes.get(j);
					
					if(nodeI.getCalleeList().contains(nodeJ)){
						graphMatrix[i][j] = 1;
					}
				}
			}
		}
		
		return graphMatrix;
	}
}
