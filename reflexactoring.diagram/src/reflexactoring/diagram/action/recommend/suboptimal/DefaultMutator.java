/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;
import java.util.HashMap;

import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class DefaultMutator implements Mutator {

	private HashMap<Integer, Integer> fixList;
	private HashMap<Integer, ArrayList<Integer>> stopList;
	
	private int moduleNum;
	
	/**
	 * @param fixList
	 * @param stopList
	 */
	public DefaultMutator(HashMap<Integer, Integer> fixList,
			HashMap<Integer, ArrayList<Integer>> stopList, int moduleNum) {
		super();
		this.fixList = fixList;
		this.stopList = stopList;
		this.moduleNum = moduleNum;
	}

	@Override
	public Population mutate(Population pop) {
		for(Genotype gene: pop.getList()){
			for(int i=0; i<gene.getLength(); i++){
				/**
				 * if such a low level node is fixed to certain map, continue.
				 */
				if(fixList.get(i) != null){
					continue;
				}
				/**
				 * mutate this low level node to another possible (available) map.
				 */
				else{
					/**
					 * find the possible (available) module to map, excluding the module it currently mapped.
					 */
					//int moduleNum = ReflexactoringUtil.getModuleList(Settings.diagramPath).size();
					ArrayList<Integer> availabelModuleIndexList = new ArrayList<>();
					ArrayList<Integer> stopModuleIndexList = stopList.get(i);
					for(int k=0; k<moduleNum; k++){
						if((stopModuleIndexList == null) || (!stopModuleIndexList.contains(k) && k!=gene.getDNA()[i])){
							availabelModuleIndexList.add(k);
						}
					}
					
					if(availabelModuleIndexList.size() > 0){
						if(Math.random() < Double.valueOf(ReflexactoringUtil.getMutationRate())){
							int index = (int) (Math.random()*availabelModuleIndexList.size());
							int moduleIndex = availabelModuleIndexList.get(index);	
							
							int[] newDNA = gene.getDNA();
							newDNA[i] = moduleIndex;
							gene.setDNA(newDNA);
						}
					}
				}
				
			}
		}
			
		return pop;
	}

}
