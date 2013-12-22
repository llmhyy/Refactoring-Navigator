package reflexactoring.diagram.edit.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITextAwareEditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.tooling.runtime.directedit.locator.CellEditorLocatorAccess;

import reflexactoring.diagram.part.ReflexactoringVisualIDRegistry;

/**
 * @generated
 */
public class ReflexactoringEditPartFactory implements EditPartFactory {

	/**
	 * @generated
	 */
	public EditPart createEditPart(EditPart context, Object model) {
		if (model instanceof View) {
			View view = (View) model;
			switch (ReflexactoringVisualIDRegistry.getVisualID(view)) {

			case ReflexactoringEditPart.VISUAL_ID:
				return new ReflexactoringEditPart(view);

			case ClassEditPart.VISUAL_ID:
				return new ClassEditPart(view);

			case ClassNameEditPart.VISUAL_ID:
				return new ClassNameEditPart(view);

			case InterfaceEditPart.VISUAL_ID:
				return new InterfaceEditPart(view);

			case InterfaceNameEditPart.VISUAL_ID:
				return new InterfaceNameEditPart(view);

			case ModuleEditPart.VISUAL_ID:
				return new ModuleEditPart(view);

			case ModuleNameEditPart.VISUAL_ID:
				return new ModuleNameEditPart(view);

			case Class2EditPart.VISUAL_ID:
				return new Class2EditPart(view);

			case ClassName2EditPart.VISUAL_ID:
				return new ClassName2EditPart(view);

			case Interface2EditPart.VISUAL_ID:
				return new Interface2EditPart(view);

			case InterfaceName2EditPart.VISUAL_ID:
				return new InterfaceName2EditPart(view);

			case ModuleTypeContainerCompartmentEditPart.VISUAL_ID:
				return new ModuleTypeContainerCompartmentEditPart(view);

			case ModuleDependencyEditPart.VISUAL_ID:
				return new ModuleDependencyEditPart(view);

			case ClassExtendEditPart.VISUAL_ID:
				return new ClassExtendEditPart(view);

			case TypeDependencyEditPart.VISUAL_ID:
				return new TypeDependencyEditPart(view);

			case InterfaceExtendEditPart.VISUAL_ID:
				return new InterfaceExtendEditPart(view);

			case ImplementEditPart.VISUAL_ID:
				return new ImplementEditPart(view);

			}
		}
		return createUnrecognizedEditPart(context, model);
	}

	/**
	 * @generated
	 */
	private EditPart createUnrecognizedEditPart(EditPart context, Object model) {
		// Handle creation of unrecognized child node EditParts here
		return null;
	}

	/**
	 * @generated
	 */
	public static CellEditorLocator getTextCellEditorLocator(
			ITextAwareEditPart source) {
		return CellEditorLocatorAccess.INSTANCE
				.getTextCellEditorLocator(source);
	}

}
