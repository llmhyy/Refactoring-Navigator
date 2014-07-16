/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.List;

/**
 * @author linyun
 *
 */
public interface GraphNode {
	public List<? extends GraphNode> getCallerList();
	public List<? extends GraphNode> getCalleeList();
	public List<? extends GraphNode> getParentList();
	public List<? extends GraphNode> getChildList();
}
