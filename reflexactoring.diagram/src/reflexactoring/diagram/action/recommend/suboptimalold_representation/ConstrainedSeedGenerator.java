/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimalold_representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class ConstrainedSeedGenerator implements SeedGenerator {

	private int dimension;
	private ArrayList<ModuleWrapper> modules;
	private ArrayList<? extends LowLevelGraphNode> lowLevelNodes;
	private double[][] similarityTable;
	
	private HashMap<ModuleUnitCorrespondence, Integer> reverseRelationMap;
	
	/**
	 * @param computingFactor
	 * @param modules
	 * @param lowLevelNodes
	 * @param similarityTable
	 */
	public ConstrainedSeedGenerator(int dimension,
			ArrayList<ModuleWrapper> modules,
			ArrayList<? extends LowLevelGraphNode> lowLevelNodes,
			double[][] similarityTable,
			HashMap<ModuleUnitCorrespondence, Integer> reverseRelationMap) {
		super();
		this.dimension = dimension;
		this.modules = modules;
		this.lowLevelNodes = lowLevelNodes;
		this.similarityTable = similarityTable;
		this.reverseRelationMap = reverseRelationMap;
	}

	@Override
	public int[] generateSeed() {
		int[] seed = new int[dimension];
		
		@SuppressWarnings("unchecked")
		ArrayList<LowLevelGraphNode>[] buckets = new ArrayList[modules.size()];
		for(int i=0; i<buckets.length; i++){
			buckets[i] = new ArrayList<>();
		}
		
		do{
			seed = new int[dimension];
			for(int i=0; i<buckets.length; i++){
				buckets[i].clear();
			}
			
			
			for(int q=0; q<lowLevelNodes.size(); q++){
				ArrayList<Integer> validModuleIndex = new ArrayList<>();
				
				/**
				 * find all the valid modules to assign units
				 */
				for(int p=0; p<modules.size(); p++){
					if(similarityTable[p][q] > Double.valueOf(ReflexactoringUtil.getMappingThreshold())){
						validModuleIndex.add(p);
					}
				}
				if(validModuleIndex.size() == 0){
					System.err.println("valid module size is 0");
				}
				
				/**
				 * In all the buckets (modules), if there are some *valid* buckets which have not been assigned
				 * any units (low level nodes), the algorithm will favor them some priority to be assigned
				 * first.
				 */
				if(isSomeValidBucketEmpty(buckets, modules, lowLevelNodes.get(q))){
					Iterator<Integer> iterator = validModuleIndex.iterator();
					while(iterator.hasNext()){
						Integer moduleIndex = iterator.next();
						if(buckets[moduleIndex].size() != 0){
							iterator.remove();
						}
					}
				}
				if(validModuleIndex.size() == 0){
					System.err.println("valid module size is 0");
				}
				
				double random = Math.random();
				
				int randomIndex = (int)(random*validModuleIndex.size());
				
				int chosenModule = validModuleIndex.get(randomIndex);
				int chosenUnit = q;
				
				ModuleUnitCorrespondence chosenPair = new ModuleUnitCorrespondence(chosenModule, chosenUnit);
				buckets[chosenModule].add(lowLevelNodes.get(chosenUnit));
				
				Integer componentIndex = this.reverseRelationMap.get(chosenPair);
				seed[componentIndex] = 1;
			}
		}while(isSomeBucketEmpty(buckets));
		
		
		return seed;
	}

	private boolean isSomeValidBucketEmpty(ArrayList<? extends LowLevelGraphNode>[] buckets, 
			ArrayList<ModuleWrapper> modules, LowLevelGraphNode node){
		for(int i=0; i<buckets.length; i++){
			if(buckets[i].size() == 0 && modules.get(i).equals(node.getMappingModule())){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isSomeBucketEmpty(ArrayList<? extends LowLevelGraphNode>[] buckets){
		for(ArrayList<? extends LowLevelGraphNode> bucket: buckets){
			if(bucket.size() == 0){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return the computingFactor
	 */
	public int getDimension() {
		return this.dimension;
	}

	/**
	 * @param computingFactor the computingFactor to set
	 */
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	/**
	 * @return the modules
	 */
	public ArrayList<ModuleWrapper> getModules() {
		return modules;
	}

	/**
	 * @param modules the modules to set
	 */
	public void setModules(ArrayList<ModuleWrapper> modules) {
		this.modules = modules;
	}

	/**
	 * @return the lowLevelNodes
	 */
	public ArrayList<? extends LowLevelGraphNode> getLowLevelNodes() {
		return lowLevelNodes;
	}

	/**
	 * @param lowLevelNodes the lowLevelNodes to set
	 */
	public void setLowLevelNodes(
			ArrayList<? extends LowLevelGraphNode> lowLevelNodes) {
		this.lowLevelNodes = lowLevelNodes;
	}

	/**
	 * @return the similarityTable
	 */
	public double[][] getSimilarityTable() {
		return similarityTable;
	}

	/**
	 * @param similarityTable the similarityTable to set
	 */
	public void setSimilarityTable(double[][] similarityTable) {
		this.similarityTable = similarityTable;
	}

	
	
}
