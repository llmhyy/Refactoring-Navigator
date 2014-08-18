/**
 * 
 */
package reflexactoring.diagram.action.recommend;

import java.util.ArrayList;

import reflexactoring.diagram.bean.programmodel.GraphNode;
import reflexactoring.diagram.bean.programmodel.GraphRelationType;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.ReferencingDetail;

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
					
					if(graphType == GraphRelationType.GRAPH_INHERITANCE){
						if(nodeI.getParentList().contains(nodeJ)){
							graphMatrix[i][j] = 1;
						}
					}
					else{
						if(nodeI.getCalleeList(type).keySet().contains(nodeJ)){
							ReferencingDetail detail = nodeI.getCalleeList(type).get(nodeJ);
							Integer value = detail.getMap().get(type);
							if(value != null){
								graphMatrix[i][j] = value;									
							}
						}
					}
				}
			}
		}
		
		return graphMatrix;
	}
}
