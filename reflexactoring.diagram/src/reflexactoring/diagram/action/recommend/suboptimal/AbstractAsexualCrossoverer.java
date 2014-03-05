/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author linyun
 * 
 */
public abstract class AbstractAsexualCrossoverer implements Crossoverer {

	public abstract Genotype produceOffSpring(Genotype gene);
	/*
	 * (non-Javadoc)
	 * 
	 * @see reflexactoring.diagram.action.recommend.suboptimal.Crossoverer#
	 * crossoverAndMutate
	 * (reflexactoring.diagram.action.recommend.suboptimal.Population,
	 * reflexactoring
	 * .diagram.action.recommend.suboptimal.FitnessComputingFactor)
	 */
	@Override
	public Population crossoverAndMutate(Population selectedPopulation,
			FitnessComputingFactor computingFactor) {

		Population crosssoverPopulation = new Population();

		for (int i = 0; i < selectedPopulation.size(); i++) {
			Genotype gene = selectedPopulation.get(i);
			// long t1 = System.currentTimeMillis();
			Genotype childGene = produceOffSpring(gene);
			// long t2 = System.currentTimeMillis();
			// System.out.println(t2-t1);

			crosssoverPopulation.add(childGene);
		}

		crosssoverPopulation.addAll(selectedPopulation);
		Collections.sort(crosssoverPopulation, new GeneComparator());
		Population newCrossoverPopulation = new Population();
				
		int len = crosssoverPopulation.size()/4;
		for (int i = 0; i < 2*len; i++) {
			newCrossoverPopulation.add(crosssoverPopulation.get(i));
		}
		
		ArrayList<Genotype> candidateDistinctList = getDistinctCandidateList(crosssoverPopulation);
		int cursor = 0;
		
		HashMap<Genotype, Integer> occuringFreqMap = new HashMap<>();
		
		for(int i=0; i<newCrossoverPopulation.size(); i++){
			Genotype genotype = newCrossoverPopulation.get(i);
			Integer freq = occuringFreqMap.get(genotype);
			if(freq == null){
				occuringFreqMap.put(genotype, 1);
			}
			else{
				if(freq == 1){
					occuringFreqMap.put(genotype, 2);
				}
				else if(freq > 1){
					if(cursor < candidateDistinctList.size()){
						newCrossoverPopulation.set(i, candidateDistinctList.get(cursor++));
					}
				}
			}
			
			if(cursor >= candidateDistinctList.size()){
				break;
			}
		}
		Collections.sort(newCrossoverPopulation, new GeneComparator());
		
		/*for (int i = 0; i < crosssoverPopulation.size()/2; i++) {
			newCrossoverPopulation.add(crosssoverPopulation.get(i));
		}*/
		
		/*int len = crosssoverPopulation.size()/4;
		for (int i = 0; i < 2*len; i++) {
			newCrossoverPopulation.add(crosssoverPopulation.get(i));
		}*/
		/*for (int i = 2*len; i < 3*len; i++) {
			newCrossoverPopulation.add(crosssoverPopulation.get(i));
		}*/

		//newCrossoverPopulation.setOptimalGene(selectedPopulation.getOptimalGene());
		//newCrossoverPopulation.updateOptimalGene();

		return newCrossoverPopulation;
		
		/*crosssoverPopulation.setOptimalGene(selectedPopulation.getOptimalGene());
		crosssoverPopulation.updateOptimalGene();
		return crosssoverPopulation;*/
	}

	private ArrayList<Genotype> getDistinctCandidateList(Population crosssoverPopulation){
		ArrayList<Genotype> distinctList = new ArrayList<>();
		
		int len = crosssoverPopulation.size()/4;
		for(int i=2*len; i<crosssoverPopulation.size(); i++){
			Genotype gene = crosssoverPopulation.get(i);
			if(!distinctList.contains(gene)){
				distinctList.add(gene);
			}
		}
		
		return distinctList;
	}
}
