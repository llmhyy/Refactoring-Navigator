/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

import reflexactoring.diagram.bean.GraphNode;
import reflexactoring.diagram.bean.GraphRelationType;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ReferencingDetail;

/**
 * @author linyun
 *
 */
public class RecommendUtil {
	
	
	public static double[][] extractGraph(ArrayList<? extends GraphNode> nodes, int graphType, int type){
		
		int dimension = nodes.size();
		double[][] graphMatrix = new double[dimension][dimension];
		
		for(int i=0; i<dimension; i++){
			for(int j=0; j<dimension; j++){
				if(i != j){
					GraphNode nodeI = nodes.get(i);
					GraphNode nodeJ = nodes.get(j);
					
					if(graphType == GraphRelationType.GRAPH_DEPENDENCY){
						if(nodeI.getCalleeList(type).keySet().contains(nodeJ)){
							ReferencingDetail detail = nodeI.getCalleeList(type).get(nodeJ);
							graphMatrix[i][j] = detail.getMap().get(type);	
						}
					}else if(graphType == GraphRelationType.GRAPH_INHERITANCE){
						if(nodeI.getParentList().contains(nodeJ)){
							graphMatrix[i][j] = 1;
						}
					}
				}
			}
		}
		
		return graphMatrix;
	}
}
