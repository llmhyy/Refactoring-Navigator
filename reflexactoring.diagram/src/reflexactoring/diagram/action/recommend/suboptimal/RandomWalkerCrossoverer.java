/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import org.eclipse.ui.PartInitException;

import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 * @author linyun
 *
 */
public class RandomWalkerCrossoverer extends AbstractAsexualCrossoverer implements Crossoverer {

	private FitnessComputingFactor computingFactor;
	private ArrayList<int[]> relationMap;
	
	public RandomWalkerCrossoverer(FitnessComputingFactor computingFactor, ArrayList<int[]> relationMap){
		this.computingFactor = computingFactor;
		this.relationMap = relationMap;
	}
	
	/*public Genotype produceOffSpring(Genotype oldGene){
		
		int lowLevelNum = this.computingFactor.getLowLevelMatrix().rows();
		int highLevelNum = this.computingFactor.getHighLevelMatrix().rows();
		
		int moduleSize = computingFactor.getHighLevelMatrix().rows();
		*//**
		 * The module partition contains the number of correspondable units for each module.
		 *//*
		int[] modulePartition = new int[moduleSize];
		SparseDoubleMatrix2D moduleUnitCorrespondingMatrix = computingFactor.getA_h();
		for(int i=0; i<modulePartition.length; i++){
			SparseDoubleMatrix1D moduleVector = (SparseDoubleMatrix1D) moduleUnitCorrespondingMatrix.viewRow(i);
			modulePartition[i] = moduleVector.cardinality();
		}
		
		int chosenMovableLowLevelNodeIndex = (int)(Math.random() * lowLevelNum);
		
		SparseDoubleMatrix1D vectorMatrix = this.computingFactor.convertToVectorMatrix(oldGene.getDNA());
		SparseDoubleMatrix2D realMappingMatrix = this.computingFactor.getRealMappingMatrix(vectorMatrix, computingFactor.getRelationMatrix());
		
		int sourceModuleIndex = getMappingModuleIndex(chosenMovableLowLevelNodeIndex, realMappingMatrix, highLevelNum, lowLevelNum);
		int destModuleIndex = -1;
		int moduleTrialNum = 10;
		do{
			destModuleIndex = (int)(Math.random()*moduleSize);
			moduleTrialNum--;
		}while(sourceModuleIndex == destModuleIndex && moduleTrialNum>0);
		
		if(sourceModuleIndex == destModuleIndex){
			return oldGene;
		}
		
		//ModuleWrapper source = showModuleWrapper(sourceModuleIndex);
		//ModuleWrapper target = showModuleWrapper(destModuleIndex);
		//LowLevelGraphNode node = showLowLevelNode(chosenMovableLowLevelNodeIndex);
		if(sourceModuleIndex == -1){
			System.currentTimeMillis();
		}
		
		int correspondingIndexInSourceModule = getCorrespondingIndexInPartition(computingFactor.getRelationMatrix(), sourceModuleIndex, chosenMovableLowLevelNodeIndex);
		int correspondingIndexInDestModule = getCorrespondingIndexInPartition(computingFactor.getRelationMatrix(), destModuleIndex, chosenMovableLowLevelNodeIndex);
		
		int vectorIndexOfSource = convertToVectorIndex(modulePartition, sourceModuleIndex, correspondingIndexInSourceModule);
		int vectorIndexOfDest = convertToVectorIndex(modulePartition, destModuleIndex, correspondingIndexInDestModule);
		
		if(oldGene.getDNA()[vectorIndexOfSource]==1 && oldGene.getDNA()[vectorIndexOfDest]==1){
			System.err.println("Random Walk Crossoverer does not allow the hard constraints to "
					+ "be broken, more specifically, a unit/mehtod/field is mapped to at least two modules.");
			System.err.println("Souce module index: " + sourceModuleIndex + "; target module index: " + destModuleIndex);
			return oldGene;
		}
		else{
			int[] DNA = flip(oldGene, vectorIndexOfSource, vectorIndexOfDest);
			
			Genotype newGene = oldGene.clone();
			newGene.setDNA(DNA);
			newGene.computeFitness(computingFactor);
			//System.out.println(newGene.getFitness());
			return newGene;	
			
		}
	}
	
	private int getMappingModuleIndex(int lowLevelNodeIndex, SparseDoubleMatrix2D realMappingMatrix, int highLevelNum, int lowLevelNum){
		for(int i=0; i<highLevelNum; i++){
			if(realMappingMatrix.get(i, lowLevelNodeIndex) != 0){
				return i;
			}
		}
		
		return -1;
	}*/
	
	
	
	@Override
	public Genotype produceOffSpring(Genotype oldGene) {
		
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
			return oldGene;
		}
		//sourceModuleIndex = 0;
		//destModuleIndex = 1;
		//System.out.println(showModuleWrapper(sourceModuleIndex) + "=>" + showModuleWrapper(destModuleIndex));
		
		/**
		 * the units that both source module and dest module can be mapped to.
		 */
		Integer[] movableUnitIndexInSourceModule = getMovableUnitIndex(computingFactor.getRelationMatrix(), oldGene.getDNA(), sourceModuleIndex, destModuleIndex);
		
		if(movableUnitIndexInSourceModule != null && movableUnitIndexInSourceModule.length != 0){
			
			int unitIndexTrialNum = 10;
			int[] DNA = oldGene.getDNA();
			do{
				int index = (int)(movableUnitIndexInSourceModule.length*Math.random());
				int chosenMovableLowLevelNodeIndex = movableUnitIndexInSourceModule[index];
				
				//ModuleWrapper source = showModuleWrapper(sourceModuleIndex);
				//ModuleWrapper target = showModuleWrapper(destModuleIndex);
				/*System.out.println(sourceModuleIndex + "=>" + destModuleIndex);
				LowLevelGraphNode node = showMember(chosenMovableLowLevelNodeIndex);
				System.out.println(node);
				if(node.toString().contains("loadFunction")){
					System.currentTimeMillis();
				}*/
				
				//SparseDoubleMatrix1D xVector = computingFactor.convertToVectorMatrix(DNA);
				//SparseDoubleMatrix2D mappingMatrix = computingFactor.getRealMappingMatrix(xVector, computingFactor.getRelationMatrix());
				
				int correspondingIndexInSourceModule = getCorrespondingIndexInPartition(computingFactor.getRelationMatrix(), sourceModuleIndex, chosenMovableLowLevelNodeIndex);
				int correspondingIndexInDestModule = getCorrespondingIndexInPartition(computingFactor.getRelationMatrix(), destModuleIndex, chosenMovableLowLevelNodeIndex);
				
				int vectorIndexOfSource = convertToVectorIndex(modulePartition, sourceModuleIndex, correspondingIndexInSourceModule);
				int vectorIndexOfDest = convertToVectorIndex(modulePartition, destModuleIndex, correspondingIndexInDestModule);
				
				if(oldGene.getDNA()[vectorIndexOfSource]==1 && oldGene.getDNA()[vectorIndexOfDest]==1){
					System.err.println("Random Walk Crossoverer does not allow the hard constraints to "
							+ "be broken, more specifically, a unit/mehtod/field is mapped to at least two modules.");
					return oldGene;
				}
				else{
					DNA = flip(oldGene, vectorIndexOfSource, vectorIndexOfDest);
					
					if(isNoEmptyModuleProduced(DNA, sourceModuleIndex, destModuleIndex, modulePartition)){
						
						Genotype newGene = oldGene.clone();
						newGene.setDNA(DNA);
						newGene.computeFitness(computingFactor);
						//System.out.println(newGene.getFitness());
						return newGene;						
					}
					
				}
				
				unitIndexTrialNum--;
			}while(!isNoEmptyModuleProduced(DNA, sourceModuleIndex, destModuleIndex, modulePartition) && unitIndexTrialNum>0);
			
			if(!isNoEmptyModuleProduced(DNA, sourceModuleIndex, destModuleIndex, modulePartition)){
				return oldGene;
			}
			
		}
		else{
			return oldGene;
		}
		
		return oldGene;
	}
	
	private ModuleWrapper showModuleWrapper(int moduleIndex){
		try {
			return ReflexactoringUtil.getModuleList(Settings.diagramPath).get(moduleIndex);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private LowLevelGraphNode showType(int nodeIndex){
		return Settings.scope.getScopeCompilationUnitList().get(nodeIndex);
		//return Settings.scope.getScopeCompilationUnitList().get(nodeIndex);
	}
	
	private LowLevelGraphNode showMember(int nodeIndex){
		return Settings.scope.getScopeMemberList().get(nodeIndex);
		//return Settings.scope.getScopeCompilationUnitList().get(nodeIndex);
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
	private Integer[] getMovableUnitIndex(SparseDoubleMatrix2D relationMatrix, int[] DNA,
			int sourceModuleIndex, int destModuleIndex) {
		
		SparseDoubleMatrix2D currentMapMatrix = new SparseDoubleMatrix2D(relationMatrix.rows(), relationMatrix.columns());
		int count = 0;
		for(int i=0; i<currentMapMatrix.rows(); i++){
			for(int j=0; j<currentMapMatrix.columns(); j++){
				if(relationMatrix.get(i, j) != 0){
					currentMapMatrix.set(i, j, DNA[count++]);
				}
			}
		}
		
		ArrayList<Integer> movableLowLevelNodeIndexes = new ArrayList<>();
		
		for(int j=0; j<relationMatrix.columns(); j++){
			if(relationMatrix.get(sourceModuleIndex, j) == 1 && relationMatrix.get(destModuleIndex, j)==1 
					&& currentMapMatrix.get(sourceModuleIndex, j) == 1){
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
