/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class SinglePointCrossoverer extends AbstractSexualCrossoverer{

	private FitnessComputingFactor computingFactor;
	/**
	 * @param computingFactor2
	 */
	public SinglePointCrossoverer(FitnessComputingFactor computingFactor) {
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
		
		int point = (int)(Math.random()*DNA1.length);
		
		for(int j=0; j<DNA1.length; j++){
			
			if(j<=point){
				childDNA1[j] = DNA1[j];
				childDNA2[j] = DNA2[j];
			}
			else{
				childDNA1[j] = DNA2[j];
				childDNA2[j] = DNA1[j];
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
		subGene1.computeFitness(computingFactor);
		Genotype subGene2 = new Genotype(childDNA2);
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
