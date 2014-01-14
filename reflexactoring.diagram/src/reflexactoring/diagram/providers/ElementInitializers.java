package reflexactoring.diagram.providers;

import reflexactoring.diagram.part.ReflexactoringDiagramEditorPlugin;

/**
 * @generated
 */
public class ElementInitializers {

	protected ElementInitializers() {
		// use #getInstance to access cached instance
	}

	/**
	 * @generated
	 */
	public static ElementInitializers getInstance() {
		ElementInitializers cached = ReflexactoringDiagramEditorPlugin
				.getInstance().getElementInitializers();
		if (cached == null) {
			ReflexactoringDiagramEditorPlugin.getInstance()
					.setElementInitializers(cached = new ElementInitializers());
		}
		return cached;
	}
}
