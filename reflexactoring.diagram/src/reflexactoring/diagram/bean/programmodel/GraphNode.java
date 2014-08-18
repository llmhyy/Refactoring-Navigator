/**
 * 
 */
package reflexactoring.diagram.bean.programmodel;

import java.util.HashMap;
import java.util.List;

/**
 * The data structure of module list is changed as well. For now, we can define the term *call* in a general way. 
 * A call can be further distinguished in dependency, creation and etc. We support dependency and creation relationships 
 * for now. Different calls are maintained in a attribute (hashmap<node, referencing_detail>) of a node, indicating a 
 * node call the other node with some referencing detail. And a referencing detail is a hashmap<type, times> as well, 
 * indicating how many times a certain relation occurs.
 * @author linyun
 *
 */
public interface GraphNode {
	public HashMap<GraphNode, ReferencingDetail> getCallerList(int type);
	public HashMap<GraphNode, ReferencingDetail> getCalleeList(int type);
	public List<? extends GraphNode> getParentList();
	public List<? extends GraphNode> getChildList();
}
