/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.HashMap;
import java.util.List;

/**
 * @author linyun
 *
 */
public interface GraphNode {
	public HashMap<GraphNode, ReferencingDetail> getCallerList(int type);
	public HashMap<GraphNode, ReferencingDetail> getCalleeList(int type);
	public List<? extends GraphNode> getParentList();
	public List<? extends GraphNode> getChildList();
}
