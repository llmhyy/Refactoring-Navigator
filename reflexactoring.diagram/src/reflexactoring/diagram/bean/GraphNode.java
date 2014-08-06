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
	public HashMap<GraphNode, Integer> getCallerList();
	public HashMap<GraphNode, Integer> getCalleeList();
	public List<? extends GraphNode> getParentList();
	public List<? extends GraphNode> getChildList();
}
