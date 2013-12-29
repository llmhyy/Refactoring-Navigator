/**
 * 
 */
package reflexactoring.diagram.util;

import reflexactoring.Activator;
import reflexactoring.diagram.preferences.ProjectInfoPage;

/**
 * @author linyun
 *
 */
public class ReflexactoringUtil {
	
	public static String getTargetProjectName(){
		return Activator.getDefault().getPreferenceStore().getString(ProjectInfoPage.TARGET_PORJECT);
	}
	
	public static void setTargetProjectName(String targetProjectName){
		Activator.getDefault().getPreferenceStore().putValue(ProjectInfoPage.TARGET_PORJECT, targetProjectName);
	}
}
