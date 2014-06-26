/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

/**
 * @author linyun
 *
 */
public class DefaultCrossoverer implements Crossoverer{

	/**
	 * The default number of population is even.
	 */
	@Override
	public Population deriveGeneration(Population population) {
		Population newGeneration = new Population();
		
		for(int i=0; i<population.getSize(); i=i+2){
			Genotype gene1 = population.getList().get(i);
			Genotype gene2 = population.getList().get(i+1);
			
			int[] originalDNA = gene1.getOriginalDNA();
			
			int[] DNA1 = new int[gene1.getLength()];
			int[] DNA2 = new int[gene2.getLength()];
			
			for(int j=0; j<gene1.getLength(); j++){
				/**
				 * throw a dice for DNA1
				 */
				if(Math.random() > 0.5){
					DNA1[j] = gene1.getDNA()[j];
				}
				else{
					DNA1[j] = gene2.getDNA()[j];
				}
				
				/**
				 * throw a dice for DNA2
				 */
				if(Math.random() > 0.5){
					DNA2[j] = gene1.getDNA()[j];
				}
				else{
					DNA2[j] = gene2.getDNA()[j];
				}
			}
			
			newGeneration.add(new Genotype(DNA1, originalDNA));
			newGeneration.add(new Genotype(DNA2, originalDNA));
		}
		
		return newGeneration;
	}

}
