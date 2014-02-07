/**
 * 
 */
package reflexactoring.diagram.action.recommend.suboptimal;


/**
 * @author linyun
 *
 */
public class ModuleUnitCorrespondence{
	private int moduleIndex;
	private int unitIndex;
	
	
	/**
	 * @param moduleIndex
	 * @param unitIndex
	 */
	public ModuleUnitCorrespondence(int moduleIndex, int unitIndex) {
		super();
		this.moduleIndex = moduleIndex;
		this.unitIndex = unitIndex;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + moduleIndex;
		result = prime * result + unitIndex;
		return result;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModuleUnitCorrespondence other = (ModuleUnitCorrespondence) obj;
		
		if (moduleIndex != other.moduleIndex)
			return false;
		if (unitIndex != other.unitIndex)
			return false;
		return true;
	}



	/**
	 * @return the moduleIndex
	 */
	public int getModuleIndex() {
		return moduleIndex;
	}
	/**
	 * @param moduleIndex the moduleIndex to set
	 */
	public void setModuleIndex(int moduleIndex) {
		this.moduleIndex = moduleIndex;
	}
	/**
	 * @return the unitIndex
	 */
	public int getUnitIndex() {
		return unitIndex;
	}
	/**
	 * @param unitIndex the unitIndex to set
	 */
	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}
	
	
}
