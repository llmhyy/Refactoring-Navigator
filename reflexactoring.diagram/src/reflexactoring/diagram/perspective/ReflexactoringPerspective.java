package reflexactoring.diagram.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class ReflexactoringPerspective implements IPerspectiveFactory {

	public static String HEURISTIC_MAPPING_VIEW = "reflexactoring.diagram.view.mappingAjustion";
	public static String PROPERTY_VIEW = "reflexactoring.diagram.view.suggestion";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT, 0.15f, editorArea);
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.6f, editorArea);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(ReflexactoringPerspective.HEURISTIC_MAPPING_VIEW);
		
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.7f, editorArea);
		right.addView(ReflexactoringPerspective.PROPERTY_VIEW);
		right.addView(IPageLayout.ID_OUTLINE);
	}

}