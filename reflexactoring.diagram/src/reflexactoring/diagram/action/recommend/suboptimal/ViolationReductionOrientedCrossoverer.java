/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;

import reflexactoring.diagram.bean.LowLevelGraphNode;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 * @author linyun
 *
 */
public class ViolationReductionOrientedCrossoverer extends AbstractAsexualCrossoverer{

	private FitnessComputingFactor computingFactor;
	
	public ViolationReductionOrientedCrossoverer(FitnessComputingFactor computingFactor){
		this.computingFactor = computingFactor;
	}
	
	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.suboptimal.Crossoverer#crossNewPair(reflexactoring.diagram.action.recommend.suboptimal.GenoTypePair)
	 */
	@Override
	public Genotype produceOffSpring(Genotype oldGene) {
		
		int[] DNA = oldGene.getDNA();
		
		if(oldGene.getViolationList().size() == 0){
			/**
			 * random move
			 */
			return oldGene;
		}
		else{
			SparseDoubleMatrix2D highLevelMatrix = computingFactor.getHighLevelMatrix();
			
			/**
			 * randomly pick up a violation.
			 */
			ArrayList<Violation> violationList = oldGene.getViolationList();
			int violationIndex = (int)(Math.random()*violationList.size());
			Violation violation = violationList.get(violationIndex);
			
			//System.out.println();
			
			ArrayList<MoveStep> candidateMoveSteps = null;
			SparseDoubleMatrix2D currentMappingRelation = getCurrentMappingRelation(DNA, computingFactor.getRelationMatrix());
			if(violation.getType() == Violation.ABSENCE){
				candidateMoveSteps = findAllRelevantNodesForAbsenceViolation(violation, computingFactor, currentMappingRelation);
			}
			else if(violation.getType() == Violation.DISONANCE){
				candidateMoveSteps = findAllRelevantNodesForDissonanceViolation(violation, computingFactor, currentMappingRelation);
			}
			else{
				System.err.println("There are some other violation type or violation type has never been assigned.");
			}
			
			if(candidateMoveSteps.size() !=0){
				MoveStep step = findBestMove(candidateMoveSteps, oldGene, currentMappingRelation);
				//int[] childDNA = generateNewDNA(step, DNA);
				
				Genotype newGene = step.getResultingGene();
				return newGene;
			}
			else{
				return oldGene;
			}
			
		}
	}

	private MoveStep findBestMove(ArrayList<MoveStep> candidateMoveSteps, Genotype oldGene, SparseDoubleMatrix2D currentMappingRelation) {
		MoveStep bestStep = null;
		
		for(MoveStep step: candidateMoveSteps){
			double marginalValue = step.calculateMarginalValue(computingFactor, oldGene, currentMappingRelation);
			step.setMarginalIncrease(marginalValue);
			if(bestStep == null){
				bestStep = step;
			}
			else{
				if(marginalValue > bestStep.getMarginalIncrease()){
					bestStep = step;
				}
			}
		}
		
		return bestStep;
	}

	private SparseDoubleMatrix2D getCurrentMappingRelation(int[] DNA, SparseDoubleMatrix2D relationMatrix){
		SparseDoubleMatrix2D currentMappingRelationMatrix = new SparseDoubleMatrix2D(relationMatrix.rows(), relationMatrix.columns());
		int count = 0;
		for(int i=0; i<relationMatrix.rows(); i++){
			for(int j=0; j<relationMatrix.columns(); j++){
				if(DNA[count++] != 0){
					currentMappingRelationMatrix.set(i, j, 1);
				}
			}
		}
		
		return currentMappingRelationMatrix;
	}
	
	private int findMappingModuleByLowLevelNodeIndex(SparseDoubleMatrix2D currentMappingRelationMatrix, int lowLevelNodeIndex){
		for(int i=0; i<currentMappingRelationMatrix.rows(); i++){
			if(currentMappingRelationMatrix.get(i, lowLevelNodeIndex) != 0){
				return i;
			}
		}
		
		return -1;
	}
	
	private ArrayList<MoveStep> findAllRelevantNodesForAbsenceViolation(Violation violation, 
			FitnessComputingFactor computingFactor, 
			SparseDoubleMatrix2D currentMappingRelationMatrix){
		SparseDoubleMatrix2D lowLevelMatrix = computingFactor.getLowLevelMatrix();
		
		int callerModuleIndex = violation.getSourceModuleIndex();
		int calleeModuleIndex = violation.getDestModuleIndex();
		
		ArrayList<MoveStep> moveSteps = new ArrayList<>();
		
		/**
		 * find all the low level nodes called by the low level nodes mapped to caller module.
		 */
		for(int i=0; i<lowLevelMatrix.rows(); i++){
			if(currentMappingRelationMatrix.get(callerModuleIndex, i) != 0){
				for(int j=0; j<lowLevelMatrix.rows(); j++){
					if(j != i && lowLevelMatrix.get(i, j)!= 0){
						int sourceModuleIndex = findMappingModuleByLowLevelNodeIndex(currentMappingRelationMatrix, j);
						MoveStep step = new MoveStep(j, sourceModuleIndex, callerModuleIndex);
						moveSteps.add(step);
					}
				}
			}
		}
		
		/**
		 * find all the low level nodes calling the low level nodes mapped to callee module.
		 */
		for(int i=0; i<lowLevelMatrix.rows(); i++){
			if(currentMappingRelationMatrix.get(calleeModuleIndex, i) != 0){
				for(int j=0; j<lowLevelMatrix.rows(); j++){
					if(j != i && lowLevelMatrix.get(j, i)!= 0){
						int sourceModuleIndex = findMappingModuleByLowLevelNodeIndex(currentMappingRelationMatrix, j);
						MoveStep step = new MoveStep(j, sourceModuleIndex, callerModuleIndex);
						moveSteps.add(step);
					}
				}
			}
		}
		
		return moveSteps;
	}
	
	private ArrayList<MoveStep> findAllRelevantNodesForDissonanceViolation(Violation violation, 
			FitnessComputingFactor computingFactor, 
			SparseDoubleMatrix2D currentMappingRelationMatrix){
		SparseDoubleMatrix2D lowLevelMatrix = computingFactor.getLowLevelMatrix();
		SparseDoubleMatrix2D highLevelMatrix = computingFactor.getHighLevelMatrix();
		
		int callerModuleIndex = violation.getSourceModuleIndex();
		int calleeModuleIndex = violation.getDestModuleIndex();
		
		ArrayList<LowLevelDependency> relevantDependencies = findAllRevelantDependenciesByViolation(callerModuleIndex, 
				calleeModuleIndex, currentMappingRelationMatrix, highLevelMatrix, lowLevelMatrix);
		
		ArrayList<MoveStep> moveSteps = new ArrayList<>();
		for(LowLevelDependency dependency: relevantDependencies){
			int sourceLowLevelIndex = dependency.getSourceIndex();
			int targetLowLevelIndex = dependency.getTargetIndex();
			
			for(int i=0; i<highLevelMatrix.rows(); i++){
				if(currentMappingRelationMatrix.get(i, sourceLowLevelIndex) == 0){
					int sourceModuleIndex = findMappingModuleByLowLevelNodeIndex(currentMappingRelationMatrix, sourceLowLevelIndex);
					MoveStep step = new MoveStep(sourceLowLevelIndex, sourceModuleIndex, i);
					moveSteps.add(step);
				}
				if(currentMappingRelationMatrix.get(i, targetLowLevelIndex) == 0){
					int sourceModuleIndex = findMappingModuleByLowLevelNodeIndex(currentMappingRelationMatrix, targetLowLevelIndex);
					MoveStep step = new MoveStep(targetLowLevelIndex, sourceModuleIndex, i);
					moveSteps.add(step);
				}
			}
		}
		//System.out.println();
		return moveSteps;
	}
	
	private ArrayList<LowLevelDependency> findAllRevelantDependenciesByViolation(
			int callerModuleIndex, int calleeModuleIndex,
			SparseDoubleMatrix2D currentMappingRelationMatrix,
			SparseDoubleMatrix2D highLevelMatrix, SparseDoubleMatrix2D lowLevelMatrix) {
		ArrayList<Integer> candidateSourceLowLevelIndexes = new ArrayList<>();
		ArrayList<Integer> candidateTargetLowLevelIndexes = new ArrayList<>();
		ArrayList<LowLevelDependency> dependencies = new ArrayList<>();
		
		for(int j=0; j<currentMappingRelationMatrix.columns(); j++){
			if(currentMappingRelationMatrix.get(callerModuleIndex, j) != 0){
				candidateSourceLowLevelIndexes.add(j);
			}
			else if(currentMappingRelationMatrix.get(calleeModuleIndex, j) != 0){
				candidateTargetLowLevelIndexes.add(j);
			}
		}
		
		for(int i=0; i<candidateSourceLowLevelIndexes.size(); i++){
			for(int j=0; j<candidateTargetLowLevelIndexes.size(); j++){
				
				int sourceLowLevelIndex = candidateSourceLowLevelIndexes.get(i);
				int targetLowLevelIndex = candidateTargetLowLevelIndexes.get(j);
				
				if(sourceLowLevelIndex!=targetLowLevelIndex && lowLevelMatrix.get(sourceLowLevelIndex, targetLowLevelIndex)!=0){
					LowLevelDependency dependency = new LowLevelDependency(sourceLowLevelIndex, targetLowLevelIndex);
					dependencies.add(dependency);
				}
			}
		}
		
		return dependencies;
	}



	public class LowLevelDependency{
		private int sourceIndex;
		private int targetIndex;
		/**
		 * @param sourceIndex
		 * @param targetIndex
		 */
		public LowLevelDependency(int sourceIndex, int targetIndex) {
			super();
			this.sourceIndex = sourceIndex;
			this.targetIndex = targetIndex;
		}
		/**
		 * @return the sourceIndex
		 */
		public int getSourceIndex() {
			return sourceIndex;
		}
		/**
		 * @param sourceIndex the sourceIndex to set
		 */
		public void setSourceIndex(int sourceIndex) {
			this.sourceIndex = sourceIndex;
		}
		/**
		 * @return the targetIndex
		 */
		public int getTargetIndex() {
			return targetIndex;
		}
		/**
		 * @param targetIndex the targetIndex to set
		 */
		public void setTargetIndex(int targetIndex) {
			this.targetIndex = targetIndex;
		}
		
		
	}
	
	public class MoveStep{
		private int lowLevelNodeIndex;
		private int sourceModuleIndex;
		private int targetModuleIndex;
		private double marginalIncrease;
		
		private Genotype resultingGene;
		
		/**
		 * @return the marginalIncrease
		 */
		public double getMarginalIncrease() {
			return marginalIncrease;
		}
		/**
		 * @param currentMappingRelation
		 * @return
		 */
		public double calculateMarginalValue(FitnessComputingFactor computingFactor, Genotype oldGene,
				SparseDoubleMatrix2D currentMappingRelation) {
			int[] newDNA = move(oldGene.getDNA(), currentMappingRelation, computingFactor.getRelationMatrix());
			
			Genotype newGene = new Genotype(newDNA);
			double fitness = newGene.computeFitness(computingFactor);
			
			newGene.setFitness(fitness);
			this.setResultingGene(newGene);
			
			double diff = fitness - oldGene.getFitness();
			
			return diff;
		}
		
		public int[] move(int[] oldDNA, SparseDoubleMatrix2D currentMappingRelation, SparseDoubleMatrix2D relationMatrix){
			int[] newDNA = new int[oldDNA.length];
			int count = 0;
			for(int i=0; i<relationMatrix.rows(); i++){
				for(int j=0;j<relationMatrix.columns(); j++){
					if(relationMatrix.get(i, j) != 0){
						if(i==sourceModuleIndex && j==lowLevelNodeIndex){
							newDNA[count] = 0;
						}
						else if(i==targetModuleIndex && j==lowLevelNodeIndex){
							newDNA[count] = 1;
						}
						else{
							newDNA[count] = oldDNA[count];
						}
						
						count++;
					}
				}
			}
			
			return newDNA;
			
		}
		/**
		 * @param marginalIncrease the marginalIncrease to set
		 */
		public void setMarginalIncrease(double marginalIncrease) {
			this.marginalIncrease = marginalIncrease;
		}
		/**
		 * @param lowLevelNodeIndex
		 * @param targetModuleIndex
		 */
		public MoveStep(int lowLevelNodeIndex, int sourceModuleIndex, int targetModuleIndex) {
			super();
			this.lowLevelNodeIndex = lowLevelNodeIndex;
			this.sourceModuleIndex = sourceModuleIndex;
			this.targetModuleIndex = targetModuleIndex;
		}
		/**
		 * @return the lowLevelNodeIndex
		 */
		public int getLowLevelNodeIndex() {
			return lowLevelNodeIndex;
		}
		/**
		 * @param lowLevelNodeIndex the lowLevelNodeIndex to set
		 */
		public void setLowLevelNodeIndex(int lowLevelNodeIndex) {
			this.lowLevelNodeIndex = lowLevelNodeIndex;
		}
		/**
		 * @return the sourceModuleIndex
		 */
		public int getSourceModuleIndex() {
			return sourceModuleIndex;
		}
		/**
		 * @param sourceModuleIndex the sourceModuleIndex to set
		 */
		public void setSourceModuleIndex(int sourceModuleIndex) {
			this.sourceModuleIndex = sourceModuleIndex;
		}
		/**
		 * @return the targetModuleIndex
		 */
		public int getTargetModuleIndex() {
			return targetModuleIndex;
		}
		/**
		 * @param targetModuleIndex the targetModuleIndex to set
		 */
		public void setTargetModuleIndex(int targetModuleIndex) {
			this.targetModuleIndex = targetModuleIndex;
		}
		/**
		 * @return the resultingGene
		 */
		public Genotype getResultingGene() {
			return resultingGene;
		}
		/**
		 * @param resultingGene the resultingGene to set
		 */
		public void setResultingGene(Genotype resultingGene) {
			this.resultingGene = resultingGene;
		}
		
	}

}
