/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import reflexactoring.Module;
import reflexactoring.diagram.bean.programmodel.GraphNode;
import reflexactoring.diagram.bean.programmodel.ReferencingDetail;

/**
 * @author linyun
 *
 */
public class ModuleWrapper extends Document implements SuggestionObject, GraphNode{
	
	private Module module;
	private HashMap<ModuleWrapper, ReferencingDetail> calleeModuleList = new HashMap<>();
	private HashMap<ModuleWrapper, ReferencingDetail> callerModuleList = new HashMap<>();
	private ArrayList<ModuleWrapper> parentModuleList = new ArrayList<>();
	private ArrayList<ModuleWrapper> childModuleList = new ArrayList<>();
	//private ArrayList<ICompilationUnitWrapper> mappingList = new ArrayList<>();
	
	/**
	 * @param module
	 */
	public ModuleWrapper(Module module) {
		super();
		this.module = module;
		this.extractTermFrequency(module.getDescription());
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
	 *//*
	public ArrayList<ICompilationUnitWrapper> getMappingList() {
		return mappingList;
	}
	*//**
	 * @param mappingList the mappingList to set
	 *//*
	public void setMappingList(ArrayList<ICompilationUnitWrapper> mappingList) {
		this.mappingList = mappingList;
	}*/
	
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
	public HashMap<ModuleWrapper, ReferencingDetail> getCalleeModuleList() {
		return calleeModuleList;
	}
	

	/**
	 * @param calleeModuleList the calleeModuleList to set
	 */
	public void setCalleeModuleList(HashMap<ModuleWrapper, ReferencingDetail> calleeModuleList) {
		this.calleeModuleList = calleeModuleList;
	}

	/**
	 * @return the callerModuleList
	 */
	public HashMap<ModuleWrapper, ReferencingDetail> getCallerModuleList() {
		return callerModuleList;
	}

	public void addCalleeModule(ModuleWrapper calleeWrapper, int type){
		ReferencingDetail detail = this.calleeModuleList.get(calleeWrapper);
		if(detail == null){
			detail = new ReferencingDetail();
		}
		
		detail.addOneReference(type, null);
		this.calleeModuleList.put(calleeWrapper, detail);
	}
	
	public void addCallerModule(ModuleWrapper callerWrapper, int type){
		ReferencingDetail detail = this.callerModuleList.get(callerWrapper);
		if(detail == null){
			detail = new ReferencingDetail();
		}
		
		detail.addOneReference(type, null);
		this.callerModuleList.put(callerWrapper, detail);
	}
	
	/**
	 * @param callerModuleList the callerModuleList to set
	 */
	public void setCallerModuleList(HashMap<ModuleWrapper, ReferencingDetail> callerModuleList) {
		this.callerModuleList = callerModuleList;
	}

	/**
	 * @return the parentModuleList
	 */
	public ArrayList<ModuleWrapper> getParentModuleList() {
		return parentModuleList;
	}

	public void addParentModule(ModuleWrapper callerWrapper){
		for(ModuleWrapper module: this.parentModuleList){
			if(module.equals(callerWrapper)){
				return;
			}
		}
		
		this.parentModuleList.add(callerWrapper);
	}
	
	/**
	 * @param parentModuleList the parentModuleList to set
	 */
	public void setParentModuleList(ArrayList<ModuleWrapper> parentModuleList) {
		this.parentModuleList = parentModuleList;
	}

	/**
	 * @return the getChildModuleList
	 */
	public ArrayList<ModuleWrapper> getChildModuleList() {
		return childModuleList;
	}

	public void addChildModule(ModuleWrapper callerWrapper){
		for(ModuleWrapper module: this.childModuleList){
			if(module.equals(callerWrapper)){
				return;
			}
		}
		
		this.childModuleList.add(callerWrapper);
	}
	
	/**
	 * @param childModuleList the childModuleList to set
	 */
	public void setChildModuleList(ArrayList<ModuleWrapper> childModuleList) {
		this.childModuleList = childModuleList;
	}
	
	/** 
	 * (non-Javadoc)
	 * @see reflexactoring.diagram.bean.programmodel.GraphNode#getCallerList()
	 */
	@Override
	public HashMap<GraphNode, ReferencingDetail> getCallerList(int type) {
		HashMap<GraphNode, ReferencingDetail> map = new HashMap<>();
		for(ModuleWrapper module: this.callerModuleList.keySet()){
			//ReferencingDetail detail = new ReferencingDetail();
			//detail.addOneReference(ReferencingDetail.ALL);
			map.put(module, this.callerModuleList.get(module));
		}
		return map;
	}

	/** (non-Javadoc)
	 * @see reflexactoring.diagram.bean.programmodel.GraphNode#getCalleeList()
	 */
	@Override
	public HashMap<GraphNode, ReferencingDetail> getCalleeList(int type) {
		HashMap<GraphNode, ReferencingDetail> map = new HashMap<>();
		for(ModuleWrapper module: this.calleeModuleList.keySet()){
			//ReferencingDetail detail = new ReferencingDetail();
			//detail.addOneReference(ReferencingDetail.ALL);
			map.put(module, this.calleeModuleList.get(module));
		}
		return map;
	}

	/** (non-Javadoc)
	 * @see reflexactoring.diagram.bean.programmodel.GraphNode#getParentList()
	 */
	@Override
	public List<? extends GraphNode> getParentList() {
		return this.parentModuleList;
	}

	/** (non-Javadoc)
	 * @see reflexactoring.diagram.bean.programmodel.GraphNode#getChildList()
	 */
	@Override
	public List<? extends GraphNode> getChildList() {
		return this.childModuleList;
	}
	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.SuggestionObject#getNameWithTag()
	 */
	@Override
	public String getNameWithTag() {
		return "<a href=\"Module\">" + getName() + "</a>";
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.Document#getDocName()
	 */
	@Override
	protected String getDocName() {
		return getName();
	}

}
