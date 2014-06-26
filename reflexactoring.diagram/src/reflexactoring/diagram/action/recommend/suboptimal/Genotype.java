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

	/**
	 * @param DNA
	 */
	public Genotype(int[] dNA) {
		super();
		DNA = dNA;
	}
	
	/**
	 * @return the DNA
	 */
	public int[] getDNA() {
		return DNA;
	}
	
	public void setDNA(int[] DNA){
		this.DNA = DNA;
	}
	
	public int getLength(){
		return DNA.length;
	}
}
