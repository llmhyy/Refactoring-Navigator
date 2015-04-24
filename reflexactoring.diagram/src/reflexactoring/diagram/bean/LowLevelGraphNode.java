/**
 * 
 */
package reflexactoring.diagram.bean;

import datamining.cluster.IClusterElement;
import reflexactoring.diagram.bean.programmodel.GraphNode;
import reflexactoring.diagram.bean.programmodel.ProgramReference;

/**
 * @author linyun
 *
 */
public interface LowLevelGraphNode extends GraphNode, IClusterElement {
	public ModuleWrapper getMappingModule();
	
	public void addProgramReferer(ProgramReference reference);
	public void addProgramReferee(ProgramReference reference);
}
