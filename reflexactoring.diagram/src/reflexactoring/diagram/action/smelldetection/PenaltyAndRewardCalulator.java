/**
 * 
 */
package reflexactoring.diagram.action.smelldetection;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.refactoringopportunities.MoveMethodOpportunity;
import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class PenaltyAndRewardCalulator {
	public double calculate(double fitness, RefactoringOpportunity calculatingOpp){
		int forbiddenCount = countSimilarOppNum(calculatingOpp, Settings.forbiddenOpps);
		int approvedCount = countSimilarOppNum(calculatingOpp, Settings.approvedOpps);
		
		if(fitness > 0){
			fitness = fitness * Math.pow(1-Double.valueOf(ReflexactoringUtil.getPenaltyRate()), forbiddenCount) * 
					Math.pow(1+Double.valueOf(ReflexactoringUtil.getRewardRate()), approvedCount);
		}
		else {
			fitness = fitness * Math.pow(1+Double.valueOf(ReflexactoringUtil.getPenaltyRate()), forbiddenCount) * 
					Math.pow(1-Double.valueOf(ReflexactoringUtil.getRewardRate()), approvedCount);
		}
		
		return fitness;
	}
	
	public boolean isConformToUserFeedback(RefactoringOpportunity calculatingOpp){
		for(RefactoringOpportunity opp: Settings.approvedOpps){
			double sim = opp.computeSimilarityWith(calculatingOpp);
			
			if(sim >= Settings.refactoringOppSimilarity){
				return true;
			}	
		}
		
		return false;
	}
	
	private int countSimilarOppNum(RefactoringOpportunity calculatingOpp, ArrayList<RefactoringOpportunity> oppList){
		int count = 0;
		for(RefactoringOpportunity opp: oppList){
			//System.out.println("opp: " + opp);
			//System.out.println("calOpp: " + calculatingOpp);
			double sim = opp.computeSimilarityWith(calculatingOpp);
			//System.out.println("Sim: " + sim);
			
			/*if(calculatingOpp instanceof MoveMethodOpportunity){
				System.currentTimeMillis();
			}*/
			
			if(sim >= Settings.refactoringOppSimilarity){
				count++;
			}	
		}
		
		return count;
	}
}
