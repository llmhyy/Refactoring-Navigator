/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimalold_representation;

import java.util.ArrayList;

/**
 * @author linyun
 *
 */
public class Population extends ArrayList<Genotype>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1030488561271723649L;
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for(Genotype gene: this){
			buffer.append(gene + ": " + gene.getFitness());
			buffer.append("\n");
		}
		return buffer.toString();
	}

	public ArrayList<Genotype> getKBestCandidate(int k){
		if(this.size() < k){
			return this;
		}
		else{
			ArrayList<Genotype> list = new ArrayList<>();
			for(int i=0; i<k; i++){
				Genotype gene = this.get(i);
				if(!list.contains(gene)){
					list.add(gene);
				}
			}
			
			return  list;
		}
	}
	
	/*private Genotype optimalGene;

	*//**
	 * @return the optimalGene
	 *//*
	public Genotype getOptimalGene() {
		return optimalGene;
	}

	*//**
	 * @param optimalGene the optimalGene to set
	 *//*
	public void setOptimalGene(Genotype optimalGene) {
		this.optimalGene = optimalGene;
	}
	
	public void updateOptimalGene(){
		
		if(this.size() == 0)return;
		
		Genotype optimalGene = (null == this.optimalGene)? this.get(0) : this.optimalGene;
		
		for(int i=0; i<this.size(); i++){
			Genotype gene = this.get(i);
			if(gene.getFitness() > optimalGene.getFitness()){
				optimalGene = gene;
			}
		}
		
		this.optimalGene = optimalGene;
	}*/
}
