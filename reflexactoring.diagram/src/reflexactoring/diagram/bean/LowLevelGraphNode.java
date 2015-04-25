/**
 * 
 */
package reflexactoring.diagram.bean;

import org.eclipse.jdt.core.dom.ASTNode;

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
	
	public String getName();
	public ASTNode getJavaElement();
}
