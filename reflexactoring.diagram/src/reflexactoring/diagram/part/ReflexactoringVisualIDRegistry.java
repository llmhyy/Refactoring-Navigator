package reflexactoring.diagram.part;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.tooling.runtime.structure.DiagramStructure;

import reflexactoring.Reflexactoring;
import reflexactoring.ReflexactoringPackage;
import reflexactoring.diagram.edit.parts.Class2EditPart;
import reflexactoring.diagram.edit.parts.ClassEditPart;
import reflexactoring.diagram.edit.parts.ClassExtendEditPart;
import reflexactoring.diagram.edit.parts.ClassName2EditPart;
import reflexactoring.diagram.edit.parts.ClassNameEditPart;
import reflexactoring.diagram.edit.parts.ImplementEditPart;
import reflexactoring.diagram.edit.parts.Interface2EditPart;
import reflexactoring.diagram.edit.parts.InterfaceEditPart;
import reflexactoring.diagram.edit.parts.InterfaceExtendEditPart;
import reflexactoring.diagram.edit.parts.InterfaceName2EditPart;
import reflexactoring.diagram.edit.parts.InterfaceNameEditPart;
import reflexactoring.diagram.edit.parts.ModuleDependencyEditPart;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.edit.parts.ModuleNameEditPart;
import reflexactoring.diagram.edit.parts.ModuleTypeContainerCompartmentEditPart;
import reflexactoring.diagram.edit.parts.ReflexactoringEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart;

/**
 * This registry is used to determine which type of visual object should be
 * created for the corresponding Diagram, Node, ChildNode or Link represented
 * by a domain model object.
 * 
 * @generated
 */
public class ReflexactoringVisualIDRegistry {

	/**
	 * @generated
	 */
	private static final String DEBUG_KEY = "reflexactoring.diagram/debug/visualID"; //$NON-NLS-1$

	/**
	 * @generated
	 */
	public static int getVisualID(View view) {
		if (view instanceof Diagram) {
			if (ReflexactoringEditPart.MODEL_ID.equals(view.getType())) {
				return ReflexactoringEditPart.VISUAL_ID;
			} else {
				return -1;
			}
		}
		return reflexactoring.diagram.part.ReflexactoringVisualIDRegistry
				.getVisualID(view.getType());
	}

	/**
	 * @generated
	 */
	public static String getModelID(View view) {
		View diagram = view.getDiagram();
		while (view != diagram) {
			EAnnotation annotation = view.getEAnnotation("Shortcut"); //$NON-NLS-1$
			if (annotation != null) {
				return (String) annotation.getDetails().get("modelID"); //$NON-NLS-1$
			}
			view = (View) view.eContainer();
		}
		return diagram != null ? diagram.getType() : null;
	}

	/**
	 * @generated
	 */
	public static int getVisualID(String type) {
		try {
			return Integer.parseInt(type);
		} catch (NumberFormatException e) {
			if (Boolean.TRUE.toString().equalsIgnoreCase(
					Platform.getDebugOption(DEBUG_KEY))) {
				ReflexactoringDiagramEditorPlugin.getInstance().logError(
						"Unable to parse view type as a visualID number: "
								+ type);
			}
		}
		return -1;
	}

	/**
	 * @generated
	 */
	public static String getType(int visualID) {
		return Integer.toString(visualID);
	}

	/**
	 * @generated
	 */
	public static int getDiagramVisualID(EObject domainElement) {
		if (domainElement == null) {
			return -1;
		}
		if (ReflexactoringPackage.eINSTANCE.getReflexactoring().isSuperTypeOf(
				domainElement.eClass())
				&& isDiagram((Reflexactoring) domainElement)) {
			return ReflexactoringEditPart.VISUAL_ID;
		}
		return -1;
	}

	/**
	 * @generated
	 */
	public static int getNodeVisualID(View containerView, EObject domainElement) {
		if (domainElement == null) {
			return -1;
		}
		String containerModelID = reflexactoring.diagram.part.ReflexactoringVisualIDRegistry
				.getModelID(containerView);
		if (!ReflexactoringEditPart.MODEL_ID.equals(containerModelID)) {
			return -1;
		}
		int containerVisualID;
		if (ReflexactoringEditPart.MODEL_ID.equals(containerModelID)) {
			containerVisualID = reflexactoring.diagram.part.ReflexactoringVisualIDRegistry
					.getVisualID(containerView);
		} else {
			if (containerView instanceof Diagram) {
				containerVisualID = ReflexactoringEditPart.VISUAL_ID;
			} else {
				return -1;
			}
		}
		switch (containerVisualID) {
		case ReflexactoringEditPart.VISUAL_ID:
			if (ReflexactoringPackage.eINSTANCE.getClass_().isSuperTypeOf(
					domainElement.eClass())) {
				return ClassEditPart.VISUAL_ID;
			}
			if (ReflexactoringPackage.eINSTANCE.getInterface().isSuperTypeOf(
					domainElement.eClass())) {
				return InterfaceEditPart.VISUAL_ID;
			}
			if (ReflexactoringPackage.eINSTANCE.getModule().isSuperTypeOf(
					domainElement.eClass())) {
				return ModuleEditPart.VISUAL_ID;
			}
			break;
		case ModuleTypeContainerCompartmentEditPart.VISUAL_ID:
			if (ReflexactoringPackage.eINSTANCE.getClass_().isSuperTypeOf(
					domainElement.eClass())) {
				return Class2EditPart.VISUAL_ID;
			}
			if (ReflexactoringPackage.eINSTANCE.getInterface().isSuperTypeOf(
					domainElement.eClass())) {
				return Interface2EditPart.VISUAL_ID;
			}
			break;
		}
		return -1;
	}

	/**
	 * @generated
	 */
	public static boolean canCreateNode(View containerView, int nodeVisualID) {
		String containerModelID = reflexactoring.diagram.part.ReflexactoringVisualIDRegistry
				.getModelID(containerView);
		if (!ReflexactoringEditPart.MODEL_ID.equals(containerModelID)) {
			return false;
		}
		int containerVisualID;
		if (ReflexactoringEditPart.MODEL_ID.equals(containerModelID)) {
			containerVisualID = reflexactoring.diagram.part.ReflexactoringVisualIDRegistry
					.getVisualID(containerView);
		} else {
			if (containerView instanceof Diagram) {
				containerVisualID = ReflexactoringEditPart.VISUAL_ID;
			} else {
				return false;
			}
		}
		switch (containerVisualID) {
		case ReflexactoringEditPart.VISUAL_ID:
			if (ClassEditPart.VISUAL_ID == nodeVisualID) {
				return true;
			}
			if (InterfaceEditPart.VISUAL_ID == nodeVisualID) {
				return true;
			}
			if (ModuleEditPart.VISUAL_ID == nodeVisualID) {
				return true;
			}
			break;
		case ClassEditPart.VISUAL_ID:
			if (ClassNameEditPart.VISUAL_ID == nodeVisualID) {
				return true;
			}
			break;
		case InterfaceEditPart.VISUAL_ID:
			if (InterfaceNameEditPart.VISUAL_ID == nodeVisualID) {
				return true;
			}
			break;
		case ModuleEditPart.VISUAL_ID:
			if (ModuleNameEditPart.VISUAL_ID == nodeVisualID) {
				return true;
			}
			if (ModuleTypeContainerCompartmentEditPart.VISUAL_ID == nodeVisualID) {
				return true;
			}
			break;
		case Class2EditPart.VISUAL_ID:
			if (ClassName2EditPart.VISUAL_ID == nodeVisualID) {
				return true;
			}
			break;
		case Interface2EditPart.VISUAL_ID:
			if (InterfaceName2EditPart.VISUAL_ID == nodeVisualID) {
				return true;
			}
			break;
		case ModuleTypeContainerCompartmentEditPart.VISUAL_ID:
			if (Class2EditPart.VISUAL_ID == nodeVisualID) {
				return true;
			}
			if (Interface2EditPart.VISUAL_ID == nodeVisualID) {
				return true;
			}
			break;
		}
		return false;
	}

	/**
	 * @generated
	 */
	public static int getLinkWithClassVisualID(EObject domainElement) {
		if (domainElement == null) {
			return -1;
		}
		if (ReflexactoringPackage.eINSTANCE.getModuleDependency()
				.isSuperTypeOf(domainElement.eClass())) {
			return ModuleDependencyEditPart.VISUAL_ID;
		}
		if (ReflexactoringPackage.eINSTANCE.getClassExtend().isSuperTypeOf(
				domainElement.eClass())) {
			return ClassExtendEditPart.VISUAL_ID;
		}
		if (ReflexactoringPackage.eINSTANCE.getTypeDependency().isSuperTypeOf(
				domainElement.eClass())) {
			return TypeDependencyEditPart.VISUAL_ID;
		}
		if (ReflexactoringPackage.eINSTANCE.getInterfaceExtend().isSuperTypeOf(
				domainElement.eClass())) {
			return InterfaceExtendEditPart.VISUAL_ID;
		}
		if (ReflexactoringPackage.eINSTANCE.getImplement().isSuperTypeOf(
				domainElement.eClass())) {
			return ImplementEditPart.VISUAL_ID;
		}
		return -1;
	}

	/**
	 * User can change implementation of this method to handle some specific
	 * situations not covered by default logic.
	 * 
	 * @generated
	 */
	private static boolean isDiagram(Reflexactoring element) {
		return true;
	}

	/**
	 * @generated
	 */
	public static boolean checkNodeVisualID(View containerView,
			EObject domainElement, int candidate) {
		if (candidate == -1) {
			//unrecognized id is always bad
			return false;
		}
		int basic = getNodeVisualID(containerView, domainElement);
		return basic == candidate;
	}

	/**
	 * @generated
	 */
	public static boolean isCompartmentVisualID(int visualID) {
		switch (visualID) {
		case ModuleTypeContainerCompartmentEditPart.VISUAL_ID:
			return true;
		default:
			break;
		}
		return false;
	}

	/**
	 * @generated
	 */
	public static boolean isSemanticLeafVisualID(int visualID) {
		switch (visualID) {
		case ReflexactoringEditPart.VISUAL_ID:
			return false;
		case ClassEditPart.VISUAL_ID:
		case InterfaceEditPart.VISUAL_ID:
		case Class2EditPart.VISUAL_ID:
		case Interface2EditPart.VISUAL_ID:
			return true;
		default:
			break;
		}
		return false;
	}

	/**
	 * @generated
	 */
	public static final DiagramStructure TYPED_INSTANCE = new DiagramStructure() {
		/**
		 * @generated
		 */
		@Override
		public int getVisualID(View view) {
			return reflexactoring.diagram.part.ReflexactoringVisualIDRegistry
					.getVisualID(view);
		}

		/**
		 * @generated
		 */
		@Override
		public String getModelID(View view) {
			return reflexactoring.diagram.part.ReflexactoringVisualIDRegistry
					.getModelID(view);
		}

		/**
		 * @generated
		 */
		@Override
		public int getNodeVisualID(View containerView, EObject domainElement) {
			return reflexactoring.diagram.part.ReflexactoringVisualIDRegistry
					.getNodeVisualID(containerView, domainElement);
		}

		/**
		 * @generated
		 */
		@Override
		public boolean checkNodeVisualID(View containerView,
				EObject domainElement, int candidate) {
			return reflexactoring.diagram.part.ReflexactoringVisualIDRegistry
					.checkNodeVisualID(containerView, domainElement, candidate);
		}

		/**
		 * @generated
		 */
		@Override
		public boolean isCompartmentVisualID(int visualID) {
			return reflexactoring.diagram.part.ReflexactoringVisualIDRegistry
					.isCompartmentVisualID(visualID);
		}

		/**
		 * @generated
		 */
		@Override
		public boolean isSemanticLeafVisualID(int visualID) {
			return reflexactoring.diagram.part.ReflexactoringVisualIDRegistry
					.isSemanticLeafVisualID(visualID);
		}
	};

}
