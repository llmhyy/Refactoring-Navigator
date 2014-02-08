/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 * @author linyun
 *
 */
public class RandomWalkerCrossoverer implements Crossoverer {

	private FitnessComputingFactor computingFactor;
	
	public RandomWalkerCrossoverer(FitnessComputingFactor computingFactor){
		this.computingFactor = computingFactor;
	}
	
	@Override
	public GenoTypePair crossNewPair(GenoTypePair pair) {
		Genotype[] genes = new Genotype[2];
		genes[0] = pair.getGene1();
		genes[1] = pair.getGene2();
		
		Genotype[] childGenes = new Genotype[2];
		
		for(int k=0; k<genes.length; k++){
			int moduleSize = computingFactor.getHighLevelMatrix().rows();
			/**
			 * The module partition contains the number of correspondable units for each module.
			 */
			int[] modulePartition = new int[moduleSize];
			SparseDoubleMatrix2D moduleUnitCorrespondingMatrix = computingFactor.getA_h();
			for(int i=0; i<modulePartition.length; i++){
				SparseDoubleMatrix1D moduleVector = (SparseDoubleMatrix1D) moduleUnitCorrespondingMatrix.viewRow(i);
				modulePartition[i] = moduleVector.cardinality();
			}
			
			
			int sourceModuleIndex;
			int destModuleIndex;
			int moduleTrialNum = 10;
			do{
				sourceModuleIndex = (int)(Math.random()*moduleSize);
				destModuleIndex = (int)(Math.random()*moduleSize);
				moduleTrialNum--;
			}while(sourceModuleIndex == destModuleIndex && moduleTrialNum>0);
			
			if(sourceModuleIndex == destModuleIndex){
				childGenes[k] = genes[k];
				continue;
			}
			
			/**
			 * the units that both source module and dest module can be mapped to.
			 */
			Integer[] movableUnitIndexInSourceModule = getMovableUnitIndex(computingFactor.getRelationMatrix(), sourceModuleIndex, destModuleIndex);
			
			if(movableUnitIndexInSourceModule != null && movableUnitIndexInSourceModule.length != 0){
				
				int unitIndexTrialNum = 10;
				int[] DNA = genes[k].getDNA();
				do{
					int index = (int)(movableUnitIndexInSourceModule.length*Math.random());
					int chosenMovableLowLevelNodeIndex = movableUnitIndexInSourceModule[index];
					
					int correspondingIndexInSourceModule = getCorrespondingIndexInPartition(computingFactor.getRelationMatrix(), sourceModuleIndex, chosenMovableLowLevelNodeIndex);
					int correspondingIndexInDestModule = getCorrespondingIndexInPartition(computingFactor.getRelationMatrix(), destModuleIndex, chosenMovableLowLevelNodeIndex);
					
					int vectorIndexOfSource = convertToVectorIndex(modulePartition, sourceModuleIndex, correspondingIndexInSourceModule);
					int vectorIndexOfDest = convertToVectorIndex(modulePartition, destModuleIndex, correspondingIndexInDestModule);
					
					if(genes[k].getDNA()[vectorIndexOfSource]==1 && genes[k].getDNA()[vectorIndexOfDest]==1){
						System.err.println("Random Walk Crossoverer does not allow the hard constraints to "
								+ "be broken, more specifically, a unit/mehtod/field is mapped to at least two modules.");
						childGenes[k] = genes[k];
						break;
					}
					else{
						DNA = flip(genes[k], vectorIndexOfSource, vectorIndexOfDest);
						
						if(isNoEmptyModuleProduced(DNA, sourceModuleIndex, destModuleIndex, modulePartition)){
							Genotype newGene = new Genotype(DNA);
							newGene.setFitness(newGene.computeFitness(computingFactor));
							childGenes[k] = newGene;						
						}
						
					}
					
					unitIndexTrialNum--;
				}while(!isNoEmptyModuleProduced(DNA, sourceModuleIndex, destModuleIndex, modulePartition) && unitIndexTrialNum>0);
				
				if(!isNoEmptyModuleProduced(DNA, sourceModuleIndex, destModuleIndex, modulePartition)){
					childGenes[k] = genes[k];
				}
				
			}
			else{
				childGenes[k] = genes[k];
			}
		}
		
		GenoTypePair newPair = new GenoTypePair(childGenes[0], childGenes[1]);
		return newPair;
	}
	

	/**
	 * @param dNA
	 * @param sourceModuleIndex
	 * @param destModuleIndex
	 * @param modulePartition
	 * @return
	 */
	private boolean isNoEmptyModuleProduced(int[] DNA, int sourceModuleIndex,
			int destModuleIndex, int[] modulePartition) {
		int countIndex = 0;
		for(int i=0; i<modulePartition.length; i++){
			if(i == sourceModuleIndex || i == destModuleIndex){
				if(isPartitionEmpty(DNA, countIndex, modulePartition[i])){
					return false;
				}
			}
			
			countIndex += modulePartition[i];
		}
		
		return true;
	}
	
	private boolean isPartitionEmpty(int[] DNA, int startIndex, int len){
		for(int j=startIndex; j<startIndex+len;j++){
			if(DNA[j] != 0){
				return false;
			}
		}
		
		return true;
	}

	/**
	 * @param modulePartition
	 * @param sourceModuleIndex
	 * @param correspondingIndexInSourceModule
	 * @return
	 */
	private int convertToVectorIndex(int[] modulePartition,
			int sourceModuleIndex, int correspondingIndexInSourceModule) {
		int vectorIndex = 0;
		
		for(int i=0; i<sourceModuleIndex; i++){
			vectorIndex += modulePartition[i];
		}
		
		return vectorIndex+correspondingIndexInSourceModule;
	}

	/**
	 * @param modulePartition
	 * @param sourceModuleIndex
	 * @param index
	 * @return
	 */
	private int getCorrespondingIndexInPartition(SparseDoubleMatrix2D relationMatrix,
			int moduleIndex, int chosenMovableLowLevelNodeIndex) {
		int countIndex = 0;
		for(int j=0; j<relationMatrix.columns(); j++){
			if(j < chosenMovableLowLevelNodeIndex && relationMatrix.get(moduleIndex, j) == 1){
				countIndex++;
			}
			else if(j == chosenMovableLowLevelNodeIndex){
				countIndex++;
				break;
			}
		}
		
		return countIndex-1;
	}

	/**
	 * @param relationMatrix
	 * @param sourceModuleIndex
	 * @param destModuleIndex
	 * @return
	 */
	private Integer[] getMovableUnitIndex(SparseDoubleMatrix2D relationMatrix,
			int sourceModuleIndex, int destModuleIndex) {
		ArrayList<Integer> movableLowLevelNodeIndexes = new ArrayList<>();
		
		for(int j=0; j<relationMatrix.columns(); j++){
			if(relationMatrix.get(sourceModuleIndex, j) == 1 && relationMatrix.get(destModuleIndex, j)==1){
				movableLowLevelNodeIndexes.add(j);
			}
		}
		
		return movableLowLevelNodeIndexes.toArray(new Integer[0]);
	}

	/**
	 * @param genotype
	 * @param vectorIndexOfSource
	 * @param vectorIndexOfDest
	 * @return
	 */
	private int[] flip(Genotype genotype, int vectorIndexOfSource,
			int vectorIndexOfDest) {
		int[] newDNA = new int[genotype.getDNA().length];
		
		for(int i=0; i<newDNA.length; i++){
			newDNA[i] = genotype.getDNA()[i];
		}
		
		int tmp = newDNA[vectorIndexOfDest];
		newDNA[vectorIndexOfDest] = newDNA[vectorIndexOfSource];
		newDNA[vectorIndexOfSource] = tmp;
		
		return newDNA;
	}

}
