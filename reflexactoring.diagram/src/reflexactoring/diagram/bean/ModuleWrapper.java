/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.List;

import reflexactoring.Module;

/**
 * @author linyun
 *
 */
public class ModuleWrapper implements SuggestionObject, GraphNode{
	
	private Module module;
	private ArrayList<ModuleWrapper> calleeModuleList = new ArrayList<>();
	private ArrayList<ModuleWrapper> callerModuleList = new ArrayList<>();
	private ArrayList<ICompilationUnitWrapper> mappingList = new ArrayList<>();
	
	/**
	 * @param module
	 */
	public ModuleWrapper(Module module) {
		super();
		this.module = module;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof ModuleWrapper){
			ModuleWrapper moduleWrapper = (ModuleWrapper)obj;
			if(moduleWrapper.getName().equals(getName())){
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * @return the module
	 */
	public Module getModule() {
		return module;
	}
	/**
	 * @param module the module to set
	 */
	public void setModule(Module module) {
		this.module = module;
	}
	/**
	 * @return the mappingList
	 */
	public ArrayList<ICompilationUnitWrapper> getMappingList() {
		return mappingList;
	}
	/**
	 * @param mappingList the mappingList to set
	 */
	public void setMappingList(ArrayList<ICompilationUnitWrapper> mappingList) {
		this.mappingList = mappingList;
	}
	
	public String getDescription(){
		return this.module.getDescription();
	}
	
	public String getName(){
		return this.module.getName();
	}
	
	public String toString(){
		return getName() /*+ ":" + getDescription()*/;
	}

	/**
	 * @return the calleeModuleList
	 */
	public ArrayList<ModuleWrapper> getCalleeModuleList() {
		return calleeModuleList;
	}
	
	public void addCalleeModule(ModuleWrapper calleeWrapper){
		for(ModuleWrapper module: this.calleeModuleList){
			if(module.getName().equals(calleeWrapper.getName())){
				return;
			}
		}
		
		this.calleeModuleList.add(calleeWrapper);
	}

	/**
	 * @param calleeModuleList the calleeModuleList to set
	 */
	public void setCalleeModuleList(ArrayList<ModuleWrapper> calleeModuleList) {
		this.calleeModuleList = calleeModuleList;
	}

	/**
	 * @return the callerModuleList
	 */
	public ArrayList<ModuleWrapper> getCallerModuleList() {
		return callerModuleList;
	}

	public void addCallerModule(ModuleWrapper callerWrapper){
		for(ModuleWrapper module: this.callerModuleList){
			if(module.getName().equals(callerWrapper.getName())
					&& module.getDescription().equals(callerWrapper.getDescription())){
				return;
			}
		}
		
		this.callerModuleList.add(callerWrapper);
	}
	
	/**
	 * @param callerModuleList the callerModuleList to set
	 */
	public void setCallerModuleList(ArrayList<ModuleWrapper> callerModuleList) {
		this.callerModuleList = callerModuleList;
	}

	/** 
	 * (non-Javadoc)
	 * @see reflexactoring.diagram.bean.GraphNode#getCallerList()
	 */
	@Override
	public List<? extends GraphNode> getCallerList() {
		return this.callerModuleList;
	}

	/** (non-Javadoc)
	 * @see reflexactoring.diagram.bean.GraphNode#getCalleeList()
	 */
	@Override
	public List<? extends GraphNode> getCalleeList() {
		return this.calleeModuleList;
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.SuggestionObject#getNameWithTag()
	 */
	@Override
	public String getNameWithTag() {
		return "<a href=\"Module\">" + getName() + "</a>";
	}
}
