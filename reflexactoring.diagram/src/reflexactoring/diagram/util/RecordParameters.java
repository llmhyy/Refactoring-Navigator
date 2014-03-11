/**
 * 
 */
package reflexactoring.diagram.util;

/**
 * @author linyun
 *
 */
public class RecordParameters {
	
	public static int scopeDecisionTime = 0;
	public static int mapTime = 0;
	public static int typeSuggestion = 0;
	public static int memberSuggestion = 0;
	public static int rejectTime = 0;
	public static int applyTime = 0;
	public static int referenceCheck = 0;
	
	public static int freezeTime = 0;
	public static int fixAllMember = 0;
	public static int fixPartMember = 0;
	
	public static void clear(){
		scopeDecisionTime = 0;
		mapTime = 0;
		typeSuggestion = 0;
		memberSuggestion = 0;
		rejectTime = 0;
		applyTime = 0;
		referenceCheck = 0;
		
		freezeTime = 0;
		fixAllMember = 0;
		fixPartMember = 0;
	}
	
	public static int recordTime = 0;
}
