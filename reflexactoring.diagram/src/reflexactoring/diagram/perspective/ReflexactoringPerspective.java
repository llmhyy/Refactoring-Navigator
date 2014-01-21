package reflexactoring.diagram.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class ReflexactoringPerspective implements IPerspectiveFactory {

	public static String HEURISTIC_MAPPING_VIEW = "reflexactoring.diagram.view.mappingAjustion";
	public static String REFACTORING_SUGGESTION = "reflexactoring.diagram.view.suggestion";
	public static String MODULE_TYPE_SIMILARITY_VIEW = "reflexactoring.diagram.view.moduleTypeSimilarity";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT, 0.15f, editorArea);
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.6f, editorArea);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(ReflexactoringPerspective.HEURISTIC_MAPPING_VIEW);
		bottom.addView(ReflexactoringPerspective.MODULE_TYPE_SIMILARITY_VIEW);
		
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.7f, editorArea);
		right.addView(ReflexactoringPerspective.REFACTORING_SUGGESTION);
		right.addView(IPageLayout.ID_OUTLINE);
	}

}
