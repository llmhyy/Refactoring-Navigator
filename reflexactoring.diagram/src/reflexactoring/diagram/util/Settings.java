/**
 * 
 */
package reflexactoring.diagram.util;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;

import reflexactoring.diagram.bean.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.HeuristicModuleUnitMapList;
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
		
	public static String diagramPath = "/refactoring/default.reflexactoring";
}
