/**
 * 
 */
package reflexactoring.diagram.util;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;

import reflexactoring.diagram.bean.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.HeuristicModuleUnitMapList;
import reflexactoring.diagram.bean.ModuleUnitsSimilarityTable;
import reflexactoring.diagram.bean.Scope;

/**
 * @author linyun
 *
 */
public class Settings {
	/**
	 * Temporarily used for global variable of user-selected 
	 * java classes/interfaces.
	 */
	public static Scope scope = new Scope();
	
	/**
	 * It is used for keep user-specified module-type(unit) mapping relation.
	 */
	public static HeuristicModuleUnitMapList heuristicModuleUnitMapList
		= new HeuristicModuleUnitMapList();
	
	/**
	 * It is used to keep user-specified module-type similarity value. For each pair
	 * of module and type, there is a similarity between them.
	 */
	public static ModuleUnitsSimilarityTable similarityTable = new ModuleUnitsSimilarityTable();
	
	public static boolean isSkipUnMappedTypes = false;
	public static boolean isCompliationUnitChanged = true;
		
	public static String diagramPath = "/refactoring/default.reflexactoring";
	public static double largeSimilarityValue = 1.0d;
	
	//=============================================================
	/**
	 * The following is the parameters for optimization
	 */
	public static double alpha = 0.5;
	public static double beta = 0.5;
	public static double mutationRate = 0.01;
	public static int geneticIterationNum = 1000;
	public static int populationSize = 50;
	
	//=============================================================
	/**
	 * The following is the parameters for k-step optimization
	 */
	public static int kStep = 3;
	public static int bucketSize = 10;
}
