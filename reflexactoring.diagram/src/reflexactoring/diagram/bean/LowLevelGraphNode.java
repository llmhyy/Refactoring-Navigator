/**
 * 
 */
package reflexactoring.diagram.bean;

/**
 * @author linyun
 *
 */
public interface LowLevelGraphNode extends GraphNode {
	public ModuleWrapper getMappingModule();
	
	public void addProgramReferer(ProgramReference reference);
	public void addProgramReferee(ProgramReference reference);
}
