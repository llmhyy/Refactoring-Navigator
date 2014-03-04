/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.Collections;

/**
 * @author linyun
 *
 */
public abstract class AbstractSexualCrossoverer implements Crossoverer {
	public abstract GenoTypePair produceOffString(GenoTypePair pair);
	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.suboptimal.Crossoverer#crossoverAndMutate(reflexactoring.diagram.action.recommend.suboptimal.Population, reflexactoring.diagram.action.recommend.suboptimal.FitnessComputingFactor)
	 */
	
	/**
	 * @param selectedPopulation
	 * @return
	 */
	@Override
	public Population crossoverAndMutate(Population selectedPopulation, FitnessComputingFactor computingFactor) {
		
		Population crosssoverPopulation = new Population();
		
		
		for(int i=0; i<selectedPopulation.size(); i=i+2){
			Genotype gene1 = selectedPopulation.get(i);
			Genotype gene2 = selectedPopulation.get(i+1);
			
			GenoTypePair pair = new GenoTypePair(gene1, gene2);
			
			//long t1 = System.currentTimeMillis();
			GenoTypePair subPair = produceOffString(pair);
			//long t2 = System.currentTimeMillis();
			//System.out.println(t2-t1);
			
			
			crosssoverPopulation.add(subPair.getGene1());
			crosssoverPopulation.add(subPair.getGene2());
		}
		
		//Collections.sort(selectedPopulation, new GeneComparator());
		crosssoverPopulation.addAll(selectedPopulation);
		
		Collections.sort(crosssoverPopulation, new GeneComparator());
		
		Population newCrossoverPopulation = new Population();
		
		int quater = crosssoverPopulation.size()/4;
		for(int i=0; i<2*quater; i++){
			if(i<quater){
				newCrossoverPopulation.add(crosssoverPopulation.get(i));
			}
			else{
				newCrossoverPopulation.add(crosssoverPopulation.get(quater+i));
			}
		}
		
		//newCrossoverPopulation.setOptimalGene(selectedPopulation.getOptimalGene());
		//newCrossoverPopulation.updateOptimalGene();
		
		return newCrossoverPopulation;
	}

}
