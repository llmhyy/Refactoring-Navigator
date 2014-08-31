/**
 * 
 */
package reflexactoring.diagram.util;

import java.util.ArrayList;

/**
 * @author linyun
 *
 */
public class RecordParameters {
	
	public static int scopeDecisionTime = 0;
	public static int proceedTime = 0;
	//public static int typeSuggestion = 0;
	//public static int memberSuggestion = 0;
	public static int checkHintTime = 0;
	public static int rejectTime = 0;
	public static int undoRejectTime = 0;
//	public static int approveTime = 0;
//	public static int undoApproveTime = 0;
	public static int applyTime = 0;
	public static int undoApplyTime = 0;
	public static int simulateTime = 0;
	public static int undoSimulateTime = 0;
	public static int referenceCheck = 0;
	public static ArrayList<String> manualMaps = new ArrayList<String>();
	
//	public static int freezeTime = 0;
//	public static int fixAllMember = 0;
//	public static int fixPartMember = 0;
	
	public static void clear(){
		scopeDecisionTime = 0;
		proceedTime = 0;
//		typeSuggestion = 0;
//		memberSuggestion = 0;
		checkHintTime = 0;
		rejectTime = 0;
		undoRejectTime = 0;
//		approveTime = 0;
//		undoApproveTime = 0;
		applyTime = 0;
		undoApplyTime = 0;
		simulateTime = 0;
		undoSimulateTime = 0;
		referenceCheck = 0;
		manualMaps = new ArrayList<String>();
		
//		freezeTime = 0;
//		fixAllMember = 0;
//		fixPartMember = 0;
	}
	
	public static int recordTime = 0;
}
