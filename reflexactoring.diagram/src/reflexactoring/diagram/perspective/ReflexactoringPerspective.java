package reflexactoring.diagram.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class ReflexactoringPerspective implements IPerspectiveFactory {

	public static String HEURISTIC_MAPPING_VIEW = "reflexactoring.diagram.view.mappingAjustion";
	public static String REFACTORING_SUGGESTION = "reflexactoring.diagram.view.suggestion";
	public static String REFACTORING_SUGGESTIONS = "reflexactoring.diagram.view.refactoringSuggestion";
	public static String MODULE_TYPE_SIMILARITY_VIEW = "reflexactoring.diagram.view.moduleTypeSimilarity";
	public static String REFERENCE_DETAIL_VIEW = "reflexactoring.diagram.view.referenceDetail";
	public static String MODULE_MEMBER_FORBIDDEN_VIEW = "reflexactoring.diagram.view.moduleMemberForbidMap";
	public static String MODULE_UNIT_FORBIDDEN_VIEW = "reflexactoring.diagram.view.moduleUnitForbidMap";
	public static String DEPENDENCY_CONSTRAINT_CONFIDENCE_VIEW = "reflexactoring.diagram.view.dependencyConstraintConfidence";
	public static String EXTEND_CONSTRAINT_CONFIDENCE_VIEW = "reflexactoring.diagram.view.extendConstraintConfidence";
	public static String MEMBER_MAPPING_FIX_BY_CLASS_VIEW = "reflexactoring.diagram.mappingFix";
	public static String MEMBER_MAPING_FIX_VIEW = "reflexactoring.diagram.view.memberFixingView";
	public static String FORBIDDEN_REFACTORING_OPP_VIEW = "reflexactoring.diagram.view.refactoringOppForbiddenView";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT, 0.15f, editorArea);
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.6f, editorArea);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addView(ReflexactoringPerspective.HEURISTIC_MAPPING_VIEW);
		bottom.addView(ReflexactoringPerspective.MODULE_TYPE_SIMILARITY_VIEW);
		bottom.addView(ReflexactoringPerspective.REFERENCE_DETAIL_VIEW);
		//bottom.addView(ReflexactoringPerspective.MODULE_MEMBER_FORBIDDEN_VIEW);
		bottom.addView(ReflexactoringPerspective.MODULE_UNIT_FORBIDDEN_VIEW);
		bottom.addView(ReflexactoringPerspective.DEPENDENCY_CONSTRAINT_CONFIDENCE_VIEW);
		bottom.addView(ReflexactoringPerspective.EXTEND_CONSTRAINT_CONFIDENCE_VIEW);
		bottom.addView(ReflexactoringPerspective.FORBIDDEN_REFACTORING_OPP_VIEW);
		//bottom.addView(ReflexactoringPerspective.MEMBER_MAPPING_FIX_BY_CLASS_VIEW);
		//bottom.addView(ReflexactoringPerspective.MEMBER_MAPING_FIX_VIEW);
		
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.7f, editorArea);
		right.addView(ReflexactoringPerspective.REFACTORING_SUGGESTION);
		right.addView(ReflexactoringPerspective.REFACTORING_SUGGESTIONS);
		right.addView(IPageLayout.ID_OUTLINE);
	}

}
