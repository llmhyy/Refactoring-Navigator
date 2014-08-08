/**
 * 
 */
package reflexactoring.diagram.bean.efficiency;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;

/**
 * @author linyun
 * 
 */
public class UnitPair {
	private ICompilationUnitWrapper unit1;
	private ICompilationUnitWrapper unit2;

	/**
	 * @return the unit1
	 */
	public ICompilationUnitWrapper getUnit1() {
		return unit1;
	}

	/**
	 * @param unit1
	 *            the unit1 to set
	 */
	public void setUnit1(ICompilationUnitWrapper unit1) {
		this.unit1 = unit1;
	}

	/**
	 * @return the unit2
	 */
	public ICompilationUnitWrapper getUnit2() {
		return unit2;
	}

	/**
	 * @param unit2
	 *            the unit2 to set
	 */
	public void setUnit2(ICompilationUnitWrapper unit2) {
		this.unit2 = unit2;
	}

	/**
	 * @param unit1
	 * @param unit2
	 */
	public UnitPair(ICompilationUnitWrapper unit1, ICompilationUnitWrapper unit2) {
		super();
		this.unit1 = unit1;
		this.unit2 = unit2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unit1 == null) ? 0 : unit1.hashCode());
		result = prime * result + ((unit2 == null) ? 0 : unit2.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		UnitPair other = (UnitPair) obj;
		if (unit1 == null) {
			if (other.unit1 != null)
				return false;
		} else if (!unit1.equals(other.unit1))
			return false;
		if (unit2 == null) {
			if (other.unit2 != null)
				return false;
		} else if (!unit2.equals(other.unit2))
			return false;
		return true;
	}

}
