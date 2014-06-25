/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimalold_representation;

import java.util.Collections;

import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class RandomCrossoverer extends AbstractSexualCrossoverer{

	private FitnessComputingFactor computingFactor;
	
	/**
	 * @param computingFactor
	 */
	public RandomCrossoverer(FitnessComputingFactor computingFactor) {
		super();
		this.computingFactor = computingFactor;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.suboptimal.Crossoverer#crossNewPair(reflexactoring.diagram.action.recommend.suboptimal.GenoTypePair)
	 */
	@Override
	public GenoTypePair produceOffString(GenoTypePair pair) {
		
		Genotype gene1 = pair.getGene1();
		Genotype gene2 = pair.getGene2();
		
		int[] DNA1 = gene1.getDNA();
		int[] DNA2 = gene2.getDNA();
		
		int[] childDNA1 = new int[DNA1.length];
		int[] childDNA2 = new int[DNA2.length];
		
		for(int j=0; j<DNA1.length; j++){
			if(DNA1[j] == DNA2[j]){
				childDNA1[j] = DNA1[j];
				childDNA2[j] = DNA2[j];
			}
			else{
				double fitness1 = computingFactor.normalizeFitnessValue(gene1.getFitness(), 
						computingFactor.getHighLevelMatrix().rows(), computingFactor.getLowLevelMatrix().columns());
				double fitness2 =  computingFactor.normalizeFitnessValue(gene2.getFitness(), 
						computingFactor.getHighLevelMatrix().rows(), computingFactor.getLowLevelMatrix().columns());
				
				double flipPoint = fitness1/(fitness1 + fitness2);
				childDNA1[j] = (Math.random()<=flipPoint)? 1 : 0;
				childDNA2[j] = (Math.random()>flipPoint)? 1 : 0;
			}
			
			/**
			 * mutate
			 */
			if(Math.random() <= Settings.mutationRate){
				childDNA1[j] = flip(childDNA1[j]);
			}
			if(Math.random() <= Settings.mutationRate){
				childDNA2[j] = flip(childDNA2[j]);
			}
		}
		
		Genotype subGene1 = new Genotype(childDNA1);
		//subGene1.setPreviousMappingMatrix(gene1.getMappingMatrix());
		//subGene1.setPreviousTmpMatrix(gene1.getTmpMatrix());
		subGene1.computeFitness(computingFactor);
		
		Genotype subGene2 = new Genotype(childDNA2);
		//subGene2.setPreviousMappingMatrix(gene2.getMappingMatrix());
		//subGene2.setPreviousTmpMatrix(gene2.getTmpMatrix());
		subGene2.computeFitness(computingFactor);
		
		GenoTypePair subPair = new GenoTypePair(subGene1, subGene2);

		return subPair;
	}

	private int flip(int code){
		if(code == 0){
			return 1;
		}
		else{
			return 0;
		}
	}
}
