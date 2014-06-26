/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

/**
 * 
 * 
 * @author linyun
 * 
 */
public class Genotype {
	private int[] DNA;
	private int[] originalDNA;
	private double fitness;
	

	/**
	 * @param DNA
	 */
	public Genotype(int[] DNA, int[] originalDNA) {
		super();
		this.DNA = DNA;
		this.setOriginalDNA(originalDNA);
	}

	/**
	 * @return the DNA
	 */
	public int[] getDNA() {
		return DNA;
	}

	public void setDNA(int[] DNA) {
		this.DNA = DNA;
	}

	public int getLength() {
		return DNA.length;
	}

	/**
	 * @return the fitness
	 */
	public double getFitness() {
		
		if(fitness == 0){
			fitness = computeFitness();
		}
		
		return fitness;
	}
	
	private double computeFitness(){
		//TODO
		return 0;
	}

	/**
	 * @param fitness
	 *            the fitness to set
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * @return the originalDNA
	 */
	public int[] getOriginalDNA() {
		return originalDNA;
	}

	/**
	 * @param originalDNA the originalDNA to set
	 */
	public void setOriginalDNA(int[] originalDNA) {
		this.originalDNA = originalDNA;
	}
}
