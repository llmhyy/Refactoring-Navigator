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
	
	private ModuleWrapper sourceModule;
	private ModuleWrapper targetModule;
	
	public ModuleLinkWrapper(ModuleWrapper sourceModule, ModuleWrapper targetModule){
		this.sourceModule = sourceModule;
		this.targetModule = targetModule;
	}

	public String toString(){
		return sourceModule.getName() + "=>" + targetModule.getName();
	}
	
	public int hashCode(){
		return (sourceModule.getDescription() + targetModule.getDescription()).hashCode();
	}
	
	public boolean equals(Object obj){
		if(obj instanceof ModuleLinkWrapper){
			ModuleLinkWrapper connection = (ModuleLinkWrapper)obj;
			if(connection.getSourceModule().equals(getSourceModule())
					&& connection.getTargetModule().equals(getTargetModule())){
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
		// TODO Auto-generated method stub
		return "dependency";
	}
}
