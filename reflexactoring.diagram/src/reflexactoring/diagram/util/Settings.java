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
	public static boolean isRedoSimilarityCalculation = true;
		
	public static String diagramPath = "/refactoring/default.reflexactoring";
	public static double largeSimilarityValue = 1.0d;
}
