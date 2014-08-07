package reflexactoring.diagram.util;

public class DefaultComparator{

	public boolean isMatch(Object obj1, Object obj2) {
		return obj1.equals(obj2);
	}

	public double computeCost(Object obj1, Object obj2) throws Exception {
		return isMatch(obj1, obj2)? 0 : 1;
	}

}
