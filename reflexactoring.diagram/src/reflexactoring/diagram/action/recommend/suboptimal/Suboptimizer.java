/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;

import reflexactoring.diagram.action.ModelMapper;
import reflexactoring.diagram.action.recommend.MemberModuleValidityExaminer;
import reflexactoring.diagram.bean.GraphNode;
import reflexactoring.diagram.bean.HeuristicModuleMemberStopMap;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.LowLevelGraphNode;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapperList;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 * @author linyun
 *
 */
public abstract class Suboptimizer {
	protected SparseDoubleMatrix1D weightVector;
	protected SparseDoubleMatrix1D x0Vector;
	
	/**
	 * The below two variables stand for the map relation between a solution and its corresponding module-unit pair.
	 * A solution actually specify how a unit/member will be mapped to a module. For example, a solution will be
	 * like [0, 1, 0, 1, 0], which means that there are five possible maps between some modules an some units. More
	 * specific, there are two modules A, B and three units a, b and c. The five possible maps are A->a, A->b, B->a,
	 * B->b, B->c. And the above vector stands for A->b and B->b are chosen while others are not. And now, the below
	 * two variables stands for which maps are a component in the vector stands for.
	 * 
	 * The mapping relation is between the index of a component and a pair of index (one is for index of module; one is
	 * for the index of unit). By this means, I am able to correspond the relation.
	 */
	protected ArrayList<int[]> relationMap;
	protected HashMap<ModuleUnitCorrespondence, Integer> reverseRelationMap;
	
	protected abstract ArrayList<Genotype> computeOptimalResult(FitnessComputingFactor computingFactor,
			ArrayList<ModuleWrapper> modules, ArrayList<? extends LowLevelGraphNode> lowLevelNodes, 
			double[][] similarityTable, ArrayList<int[]> relationMap, IProgressMonitor monitor);
	
	public ArrayList<Genotype> optimize(ArrayList<ICompilationUnitWrapper> units, ArrayList<ModuleWrapper> modules, IProgressMonitor monitor){
		//double[][] similarityTable = new ModelMapper().computeSimilarityTableWithRegardToHeurisitcRules(modules, units);
		double[][] similarityTable;
		
		if(ReflexactoringUtil.isReflexionModelChanged()){
			similarityTable = new ModelMapper().computeSimilarityTableWithRegardToHeurisitcRules(modules, units);
		}
		else{
			similarityTable	= Settings.similarityTable.
					convertModuleUnitsSimilarityTableToRawTable();
			
		}
		
		FitnessComputingFactor computingFactor = buildComputingFactor(similarityTable, modules, units);
		
		ArrayList<Genotype> genes = computeOptimalResult(computingFactor, modules, units, similarityTable, relationMap, monitor);
		
		return genes;
	}
	
	public ArrayList<Genotype> optimize(UnitMemberWrapperList members, ArrayList<ModuleWrapper> modules, IProgressMonitor monitor){
		
		MemberModuleValidityExaminer examiner = new MemberModuleValidityExaminer();
		double[][] similarityTable = new double[modules.size()][members.size()];
		
		for(int i=0; i<modules.size(); i++){
			ModuleWrapper module = modules.get(i);
			module.extractTermFrequency(module.getDescription());
			for(int j=0; j<members.size(); j++){
				UnitMemberWrapper member = members.get(j);
				if(examiner.isValid(member, module)){
					double similarity = module.computeSimilarity(member);
					//System.currentTimeMillis();
					similarityTable[i][j] = Double.valueOf(ReflexactoringUtil.getMappingThreshold()) + similarity;					
				}
				else{
					similarityTable[i][j] = Double.valueOf(ReflexactoringUtil.getMappingThreshold()) - 1;
				}
				
				if(member.getName().contains("op_listener")){
					System.out.println(member.getUnitWrapper().getSimpleName()+"."+member);
					System.out.println(member.getTermFrequency());
					System.out.println(module);
					System.out.println(module.getTermFrequency());
					System.out.println("similarity:"+similarityTable[i][j]);
					System.currentTimeMillis();					
				}
				
			}
		}
		
		FitnessComputingFactor computingFactor = buildComputingFactor(similarityTable, modules, members);
		
		ArrayList<Genotype> genes = computeOptimalResult(computingFactor, modules, members, similarityTable, relationMap, monitor);
		
		return genes;
	}
	
	public ArrayList<int[]> getRelationMap(){
		return this.relationMap;
	}
	
	/**
	 * this method should be invoked after calling optimize().
	 * @return
	 */
	public int[] getX0(){
		int[] x0 = new int[x0Vector.size()];
		
		for(int i=0; i<x0Vector.size(); i++){
			x0[i] = (int) x0Vector.get(i);
		}
		
		return x0;
	}
	
	public FitnessComputingFactor buildComputingFactor(double[][] similarityTable, ArrayList<ModuleWrapper> modules, 
			ArrayList<? extends LowLevelGraphNode> lowLevelUnits){
		/**
		 * In the method as extractRelation, the weight vector and x0 vector will be initialized as well.
		 */
		SparseDoubleMatrix2D relationMatrix = extractRelation(similarityTable, modules, lowLevelUnits);
		
		System.out.println("The variable number is: " + this.weightVector.size());
		
		SparseDoubleMatrix2D highLevelMatrix = extractGraph(modules);
		SparseDoubleMatrix2D lowLevelMatrix = extractGraph(lowLevelUnits);
		
		FitnessComputingFactor computingFactor = new FitnessComputingFactor();
		computingFactor.setHighLevelMatrix(highLevelMatrix);
		computingFactor.setLowLevelMatrix(lowLevelMatrix);
		computingFactor.setRelationMatrix(relationMatrix);
		computingFactor.setWeightVector(weightVector);
		computingFactor.setX0Vector(x0Vector);
		computingFactor.initializeEdgeVertexMatrix();
		
		return computingFactor;
	}
	
	/**
	 * In this method, the weight vector and x0 vector will be initialized as well. This may not
	 * be well from an engineering point of view, but may save some computation.
	 * @param similarityTable
	 * @param modules
	 * @param units
	 * @return
	 */
	private SparseDoubleMatrix2D extractRelation(double[][] similarityTable, ArrayList<ModuleWrapper> modules, ArrayList<? extends LowLevelGraphNode> lowLevelNodes){
		int highLevelNumber = modules.size();
		int lowLevelNumber = lowLevelNodes.size();
		
		SparseDoubleMatrix2D relationMatrix = new SparseDoubleMatrix2D(highLevelNumber, lowLevelNumber);
		
		ArrayList<Double> weightVectorList = new ArrayList<>();
		ArrayList<Double> x0VectorList = new ArrayList<>();
		ArrayList<int[]> relationMap = new ArrayList<>();
		HashMap<ModuleUnitCorrespondence, Integer> reverseRelationMap = new HashMap<>();
		
		Integer mapIndex = 0;
		for(int i=0; i<highLevelNumber; i++){
			for(int j=0; j<lowLevelNumber; j++){
				if(similarityTable[i][j] >= Double.valueOf(ReflexactoringUtil.getMappingThreshold())){
					relationMatrix.set(i, j, 1);
					
					int[] moduleUnitPair = new int[]{i, j};
					relationMap.add(moduleUnitPair);
					reverseRelationMap.put(new ModuleUnitCorrespondence(i, j), mapIndex++);
					/**
					 * initial weight
					 */
					weightVectorList.add(similarityTable[i][j]);
					/**
					 * initial x0
					 */
					LowLevelGraphNode node = lowLevelNodes.get(j);
					ModuleWrapper module = modules.get(i);
					if(module.equals(node.getMappingModule())){
						x0VectorList.add(1d);
					}
					else{
						x0VectorList.add(0d);
					}
				}
			}
		}
		
		this.weightVector = GeneticUtil.convertVectorToMatrix(weightVectorList);
		this.x0Vector = GeneticUtil.convertVectorToMatrix(x0VectorList);
		this.relationMap = relationMap;
		this.reverseRelationMap = reverseRelationMap;
		
		return relationMatrix;
	}
	
	/*private Matrix extractMemberLevelRelation(double[][] similarityTable, ArrayList<ModuleWrapper> modules, ArrayList<UnitMemberWrapper> members){
		int highLevelNumber = modules.size();
		int lowLevelNumber = members.size();
		
		Matrix relationMatrix = new Matrix(highLevelNumber, lowLevelNumber);
		
		ArrayList<Double> weightVectorList = new ArrayList<>();
		ArrayList<Integer> x0VectorList = new ArrayList<>();
		ArrayList<int[]> relationMap = new ArrayList<>();
		
		for(int i=0; i<highLevelNumber; i++){
			for(int j=0; j<lowLevelNumber; j++){
				relationMatrix.set(i, j, 1);
				relationMap.add(new int[]{i, j});
				*//**
				 * initial weight
				 *//*
				weightVectorList.add(similarityTable[i][j]);
				*//**
				 * initial x0
				 *//*
				ICompilationUnitWrapper unit = members.get(j).getUnitWrapper();
				ModuleWrapper module = modules.get(i);
				if(module.equals(unit.getMappingModule())){
					x0VectorList.add(1);
				}
				else{
					x0VectorList.add(0);
				}
			}
		}
		
		this.weightVector = GeneticUtil.convertRowVectorToMatrix(weightVectorList);
		this.x0Vector = GeneticUtil.convertColumnVectorToMatrx(x0VectorList);
		this.relationMap = relationMap;
		
		return relationMatrix;
	}*/
	
	private SparseDoubleMatrix2D extractGraph(ArrayList<? extends GraphNode> nodes){
		
		int dimension = nodes.size();
		SparseDoubleMatrix2D graphMatrix = new SparseDoubleMatrix2D(dimension, dimension);
		
		for(int i=0; i<dimension; i++){
			for(int j=0; j<dimension; j++){
				if(i != j){
					GraphNode nodeI = nodes.get(i);
					GraphNode nodeJ = nodes.get(j);
					
					if(nodeI.getCalleeList().contains(nodeJ)){
						graphMatrix.set(i, j, 1);
					}
				}
			}
		}
		
		return graphMatrix;
	}
}
