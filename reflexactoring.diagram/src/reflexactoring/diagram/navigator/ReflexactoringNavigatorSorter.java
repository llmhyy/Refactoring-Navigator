package reflexactoring.diagram.navigator;

import org.eclipse.jface.viewers.ViewerSorter;

import reflexactoring.diagram.part.ReflexactoringVisualIDRegistry;

/**
 * @generated
 */
public class ReflexactoringNavigatorSorter extends ViewerSorter {

	/**
	 * @generated
	 */
	private static final int GROUP_CATEGORY = 7003;

	/**
	 * @generated
	 */
	public int category(Object element) {
		if (element instanceof ReflexactoringNavigatorItem) {
			ReflexactoringNavigatorItem item = (ReflexactoringNavigatorItem) element;
			return ReflexactoringVisualIDRegistry.getVisualID(item.getView());
		}
		return GROUP_CATEGORY;
	}

}
