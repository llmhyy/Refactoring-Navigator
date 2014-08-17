/**
 * 
 */
package reflexactoring.diagram.bean;

/**
 * @author linyun
 *
 */
public class ModuleLinkWrapper implements SuggestionObject{
	
	/**
	 * Difference types 
	 */
	public static final String ORIGIN = "origin";
	public static final String CONFORMANCE = "conformance";
	public static final String DIVERGENCE = "divergence";
	public static final String ABSENCE = "absence";
	
	public static final int MODULE_EXTEND = 1;
	public static final int MODULE_DEPENDENCY = 2;
	public static final int MODULE_CREATION = 3;
	
	private ModuleWrapper sourceModule;
	private ModuleWrapper targetModule;
	private int linkType;
	
	public ModuleLinkWrapper(ModuleWrapper sourceModule, ModuleWrapper targetModule, int linkType){
		this.sourceModule = sourceModule;
		this.targetModule = targetModule;
		this.setLinkType(linkType);
	}

	public String toString(){
		String relation = "rel";
		if(getLinkType() == ModuleLinkWrapper.MODULE_DEPENDENCY){
			relation = " depends on ";
		}
		else if(getLinkType() == ModuleLinkWrapper.MODULE_EXTEND){
			relation = " extends ";
		}
		else if(getLinkType() == ModuleLinkWrapper.MODULE_CREATION){
			relation = " creates ";
		}
		
		return sourceModule.getName() + relation + targetModule.getName();
	}
	
	public int hashCode(){
		return (sourceModule.getName() + targetModule.getName()).hashCode() + getLinkType();
	}
	
	public boolean equals(Object obj){
		if(obj instanceof ModuleLinkWrapper){
			ModuleLinkWrapper connection = (ModuleLinkWrapper)obj;
			if(connection.getSourceModule().equals(getSourceModule())
					&& connection.getTargetModule().equals(getTargetModule())
					&& connection.getLinkType() == getLinkType()){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return the sourceModule
	 */
	public ModuleWrapper getSourceModule() {
		return sourceModule;
	}

	/**
	 * @param sourceModule the sourceModule to set
	 */
	public void setSourceModule(ModuleWrapper sourceModule) {
		this.sourceModule = sourceModule;
	}

	/**
	 * @return the targetModule
	 */
	public ModuleWrapper getTargetModule() {
		return targetModule;
	}

	/**
	 * @param targetModule the targetModule to set
	 */
	public void setTargetModule(ModuleWrapper targetModule) {
		this.targetModule = targetModule;
	}
	
	@Override
	public String getName() {
		return "edge";
	}

	/* (non-Javadoc)
	 * @see reflexactoring.diagram.bean.SuggestionObject#getNameWithTag()
	 */
	@Override
	public String getNameWithTag() {
		if(this.linkType == ModuleLinkWrapper.MODULE_EXTEND){
			return "inheritance";
		}
		else if(this.linkType == ModuleLinkWrapper.MODULE_DEPENDENCY){
			return "dependency";			
		}
		else{
			return "creation";			
		}
	}

	/**
	 * @return the linkType
	 */
	public int getLinkType() {
		return linkType;
	}

	/**
	 * @param linkType the linkType to set
	 */
	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}
}
