/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.Collections;

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
		
		/*for (int i = 0; i < crosssoverPopulation.size()/2; i++) {
			newCrossoverPopulation.add(crosssoverPopulation.get(i));
		}*/
		
		int len = crosssoverPopulation.size()/4;
		for (int i = 0; i < len; i++) {
			newCrossoverPopulation.add(crosssoverPopulation.get(i));
		}
		for (int i = 2*len; i < 3*len; i++) {
			newCrossoverPopulation.add(crosssoverPopulation.get(i));
		}

		//newCrossoverPopulation.setOptimalGene(selectedPopulation.getOptimalGene());
		//newCrossoverPopulation.updateOptimalGene();

		return newCrossoverPopulation;
		
		/*crosssoverPopulation.setOptimalGene(selectedPopulation.getOptimalGene());
		crosssoverPopulation.updateOptimalGene();
		return crosssoverPopulation;*/
	}

}
