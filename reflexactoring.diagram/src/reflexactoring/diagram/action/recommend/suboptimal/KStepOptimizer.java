/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;


import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class KStepOptimizer extends Suboptimizer{

	
	
	/* (non-Javadoc)
	 * @see reflexactoring.diagram.action.recommend.suboptimal.Suboptimizer#computeOptimalResult(reflexactoring.diagram.action.recommend.suboptimal.FitnessComputingFactor, java.util.ArrayList, java.util.ArrayList, double[][])
	 */
	@Override
	protected ArrayList<Genotype> computeOptimalResult(FitnessComputingFactor computingFactor,
			ArrayList<ModuleWrapper> modules, ArrayList<? extends LowLevelGraphNode> lowLevelNodes,
			double[][] similarityTable, ArrayList<int[]> relationMap, IProgressMonitor monitor) {

		int depth = Settings.kStep - 1;
		
		Genotype initialGene = convertToGene(computingFactor.getX0Vector(), computingFactor);
		
		ArrayList<Genotype> geneBucket = findBestGenotypeByOneMove(initialGene, computingFactor, Settings.bucketSize);
		
		if(geneBucket.get(0).getFitness() > 0){
			return geneBucket;
		}
		
		while(depth > 0){
			ArrayList<Genotype> possibleGenes = new ArrayList<>();
			for(Genotype gene: geneBucket){
				ArrayList<Genotype> bestGenesByOneMove = findBestGenotypeByOneMove(gene, computingFactor, Settings.bucketSize);
				
				if(bestGenesByOneMove.get(0).getFitness() > 0){
					return geneBucket;
				}
				
				possibleGenes.addAll(bestGenesByOneMove);
			}
			
			Collections.sort(possibleGenes, new GeneComparator());
			
			geneBucket.clear();
			for(int i=0; i<Settings.bucketSize; i++){
				geneBucket.add(possibleGenes.get(i));
			}
			
			depth--;
		}
		
		return geneBucket;
	}

	/**
	 * @param initialGene
	 * @param computingFactor
	 * @return
	 */
	private ArrayList<Genotype> findBestGenotypeByOneMove(Genotype initialGene,
			FitnessComputingFactor computingFactor, int bucketSize) {
		ArrayList<Genotype> possibleGenes = new ArrayList<>();
		
		ArrayList<Violation> violationList = initialGene.getViolationList();
		ArrayList<Genotype> candidateGenes = null;
		for(Violation violation: violationList){
			
			ArrayList<Genotype> genes = new ArrayList<>();
		
			SparseDoubleMatrix2D currentMappingRelation = getCurrentMappingRelation(initialGene.getDNA(), computingFactor.getRelationMatrix());
			if(violation.getType() == Violation.ABSENCE){
				genes = findAllRelevantCandidateGenesForAbsenceViolation(violation, computingFactor, currentMappingRelation);
			}
			else if(violation.getType() == Violation.DISONANCE){
				genes = findAllRelevantCandidateGenesForDissonanceViolation(violation, computingFactor, currentMappingRelation);
			}
			
			candidateGenes.addAll(genes);
		}
		
		Collections.sort(candidateGenes, new GeneComparator());
		
		ArrayList<Genotype> bestCandidates = new ArrayList<>();
		for(int i=0; i<bucketSize; i++){
			bestCandidates.add(candidateGenes.get(i));
		}
		return bestCandidates;
	}
	
	
	
	
	
	/**
	 * @param violation
	 * @param computingFactor
	 * @param currentMappingRelation
	 * @return
	 */
	private ArrayList<Genotype> findAllRelevantCandidateGenesForDissonanceViolation(
			Violation violation, FitnessComputingFactor computingFactor,
			SparseDoubleMatrix2D currentMappingRelation) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param violation
	 * @param computingFactor
	 * @param currentMappingRelation
	 * @return
	 */
	private ArrayList<Genotype> findAllRelevantCandidateGenesForAbsenceViolation(
			Violation violation, FitnessComputingFactor computingFactor,
			SparseDoubleMatrix2D currentMappingRelation) {
		// TODO Auto-generated method stub
		return null;
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
	
	

	/**
	 * @param x0Vector
	 * @return
	 */
	private Genotype convertToGene(SparseDoubleMatrix1D x0Vector, FitnessComputingFactor computingFactor) {
		
		int[] DNA = new int[x0Vector.size()];
		for(int i=0; i<DNA.length; i++){
			DNA[i] = (int) x0Vector.toArray()[i];
		}
		Genotype gene = new Genotype(DNA);
		
		gene.computeFitness(computingFactor);
		
		return gene;
	}
}
