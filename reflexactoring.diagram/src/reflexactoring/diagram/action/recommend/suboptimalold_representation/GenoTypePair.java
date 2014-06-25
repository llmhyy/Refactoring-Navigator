/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimalold_representation;

/**
 * @author linyun
 *
 */
public class GenoTypePair {
	private Genotype gene1;
	private Genotype gene2;
	/**
	 * @param gene1
	 * @param gene2
	 */
	public GenoTypePair(Genotype gene1, Genotype gene2) {
		super();
		this.gene1 = gene1;
		this.gene2 = gene2;
	}
	/**
	 * @return the gene1
	 */
	public Genotype getGene1() {
		return gene1;
	}
	/**
	 * @param gene1 the gene1 to set
	 */
	public void setGene1(Genotype gene1) {
		this.gene1 = gene1;
	}
	/**
	 * @return the gene2
	 */
	public Genotype getGene2() {
		return gene2;
	}
	/**
	 * @param gene2 the gene2 to set
	 */
	public void setGene2(Genotype gene2) {
		this.gene2 = gene2;
	}
	
	
}
