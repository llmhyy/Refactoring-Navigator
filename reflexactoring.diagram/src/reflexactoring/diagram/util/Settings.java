/**
 * 
 */
package reflexactoring.diagram.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.ICompilationUnit;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.bean.HeuristicModulePartFixMemberMapList;
import reflexactoring.diagram.bean.HeuristicModuleUnitFixMemberMapList;
import reflexactoring.diagram.bean.HeuristicModuleMemberStopMapList;
import reflexactoring.diagram.bean.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.HeuristicModuleUnitMapList;
import reflexactoring.diagram.bean.HeuristicModuleUnitStopMapList;
import reflexactoring.diagram.bean.ModuleCreationConfidenceTable;
import reflexactoring.diagram.bean.ModuleDependencyConfidenceTable;
import reflexactoring.diagram.bean.ModuleExtendConfidenceTable;
import reflexactoring.diagram.bean.ModuleUnitsSimilarityTable;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.efficiency.UnitPair;

/**
 * @author linyun
 *
 */
public class Settings {

	/**
	 * Temporarily used for global variable of user-selected 
	 * java classes/interfaces.
	 */
	public static ProgramModel scope = new ProgramModel();
	
	/**
	 * It is used for specifying user rejected refactoring suggestions, which could used as hints to add penalty
	 * to similar refactoring suggestions.
	 */
	public static ArrayList<RefactoringOpportunity> forbiddenOpps = new ArrayList<>();
	
	/**
	 * It is used for specifying user approved refactoring suggestions, which could be used as hints to add reward
	 * to similar refactoring suggestions.
	 */
	public static ArrayList<RefactoringOpportunity> approvedOpps = new ArrayList<>();
	/**
	 * It is used for keeping user-specified module-type(unit) mapping relation.
	 */
	public static HeuristicModuleUnitMapList heuristicModuleUnitFixList
		= new HeuristicModuleUnitMapList();
	
	/**
	 * It is used for keeping user-specified forbidden list indicating which type(unit) can never
	 * be mapped to which module.
	 */
	public static HeuristicModuleUnitStopMapList heuristicModuleUnitStopMapList
		= new HeuristicModuleUnitStopMapList();
	
	/**
	 * It is used for keeping user-specified forbidden list indicating which member can never
	 * be mapped to which module.
	 */
	public static HeuristicModuleMemberStopMapList heuristicModuleMemberStopMapList
		= new HeuristicModuleMemberStopMapList();
	
	/**
	 * It is used for keeping user-defined fixed list. In other words, when user fix a module
	 * and type, he/she force that all the members of a type can only be mapped to the module.
	 */
	public static HeuristicModuleUnitFixMemberMapList heuristicModuleUnitMemberFixList
		= new HeuristicModuleUnitFixMemberMapList();
	
	/**
	 * It is used for keeping user-defined fixed list. In other words, when user fix part a module
	 * and type, he/she force that part the members of a type can only be mapped to the module.
	 */
	public static HeuristicModulePartFixMemberMapList heuristicModuleMemberPartFixList
		= new HeuristicModulePartFixMemberMapList();
	
	/**
	 * It is used to keep user-specified module-type similarity value. For each pair
	 * of module and type, there is a similarity between them.
	 */
	public static ModuleUnitsSimilarityTable similarityTable = new ModuleUnitsSimilarityTable();
	
	/**
	 * It is used to record module-module dependency confidence. If the confidence is low, e.g., 0.1,
	 * it means user is not quite confident in such a dependency constraint, which will lead 
	 * my algorithm considering tolerating some violation.
	 */
	public static ModuleDependencyConfidenceTable dependencyConfidenceTable = new ModuleDependencyConfidenceTable();
	
	/**
	 * It is used to record module-module extend confidence. If the confidence is low, e.g., 0.1,
	 * it means user is not quite confident in such a extend constraint, which will lead 
	 * my algorithm considering tolerating some violation.
	 */
	public static ModuleExtendConfidenceTable extendConfidenceTable = new ModuleExtendConfidenceTable();
	
	/**
	 * It is used to record module-module creation confidence. If the confidence is low, e.g., 0.1,
	 * it means user is not quite confident in such a creation constraint, which will lead 
	 * my algorithm considering tolerating some violation.
	 */
	public static ModuleCreationConfidenceTable creationConfidenceTable = new ModuleCreationConfidenceTable();
	
	/**
	 * If a module is frozen on diagram, no member will be recommended to move into it, nor will any member
	 * inside it will be move out of it.
	 */
	public static ArrayList<ModuleWrapper> frozenModules = new ArrayList<>();
	
	public static boolean isSkipUnMappedTypes = false;
	public static boolean isCompliationUnitChanged = true;
	public static boolean isNeedClearCache = true;
		
	public static String diagramPath = "/refactoring/default.reflexactoring";
	public static double largeSimilarityValue = 1.0d;
	
	/**
	 * This constants is used to improve the efficiency of computing similarity.
	 */
	public static HashMap<UnitPair, Double> unitPairSimilarityMap = new HashMap<>();
	
	//=============================================================
	/**
	 * The following is the parameters for optimization
	 */
	public static double alpha = 0.5;
	public static double beta = 0.5;
	public static double mutationRate = 0.01;
	public static int geneticIterationNum = 1000;
	public static int populationSize = 50;
	public static int suggestionNum = 3;
	
	//=============================================================
	/**
	 * The following is the parameters for k-step optimization
	 */
	public static int kStep = 3;
	public static int bucketSize = 10;
	
	//==============================================================
	/**
	 * The following is the parameters for detecting bad smell
	 */
	public static final double counterMethodSimilarity = 0.6;
	public static final double featureEnvyThreshold = 0.2;
	
	public static final double penaltyRate = 0.2;
	public static final double rewardRate = 0.2;
	
	public static final double refactoringOppSimilarity = 0.6;
}
