/**
 * 
 */
package reflexactoring.diagram.util;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;

/**
 * @author linyun
 *
 */
public class Settings {
	/**
	 * Temporarily used for global variable of user-selected 
	 * java classes/interfaces.
	 */
	public static ArrayList<ICompilationUnit> scopeCompilationUnitList 
		= new ArrayList<>();
}
