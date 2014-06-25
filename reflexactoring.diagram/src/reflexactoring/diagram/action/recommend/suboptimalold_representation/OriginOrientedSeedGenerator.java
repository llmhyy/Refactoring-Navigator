/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimalold_representation;

/**
 * @author linyun
 *
 */
public class OriginOrientedSeedGenerator implements SeedGenerator {

	private int[] x0;
	
	public OriginOrientedSeedGenerator(FitnessComputingFactor computingFactor){
		int[] array = new int[computingFactor.getX0Vector().size()];
		for(int i=0; i<array.length; i++){
			array[i] = (int) computingFactor.getX0Vector().get(i);
		}
		
		this.x0 = array;
	}
	
	@Override
	public int[] generateSeed() {
		
		return this.x0;
	}

}
