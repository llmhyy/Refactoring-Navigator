package reflexactoring.diagram.providers;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gmf.runtime.emf.type.core.ElementTypeRegistry;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import reflexactoring.ReflexactoringPackage;
import reflexactoring.diagram.edit.parts.Class2EditPart;
import reflexactoring.diagram.edit.parts.ClassEditPart;
import reflexactoring.diagram.edit.parts.ClassExtendEditPart;
import reflexactoring.diagram.edit.parts.ImplementEditPart;
import reflexactoring.diagram.edit.parts.Interface2EditPart;
import reflexactoring.diagram.edit.parts.InterfaceEditPart;
import reflexactoring.diagram.edit.parts.InterfaceExtendEditPart;
import reflexactoring.diagram.edit.parts.ModuleDependencyEditPart;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.edit.parts.ModuleExtendEditPart;
import reflexactoring.diagram.edit.parts.ReflexactoringEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart;
import reflexactoring.diagram.part.ReflexactoringDiagramEditorPlugin;

/**
 * @generated
 */
public class ReflexactoringElementTypes {

	/**
	 * @generated
	 */
	private ReflexactoringElementTypes() {
	}

	/**
	 * @generated
	 */
	private static Map<IElementType, ENamedElement> elements;

	/**
	 * @generated
	 */
	private static ImageRegistry imageRegistry;

	/**
	 * @generated
	 */
	private static Set<IElementType> KNOWN_ELEMENT_TYPES;

	/**
	 * @generated
	 */
	public static final IElementType Reflexactoring_1000 = getElementType("reflexactoring.diagram.Reflexactoring_1000"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType Class_2001 = getElementType("reflexactoring.diagram.Class_2001"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType Interface_2002 = getElementType("reflexactoring.diagram.Interface_2002"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType Module_2003 = getElementType("reflexactoring.diagram.Module_2003"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType Class_3001 = getElementType("reflexactoring.diagram.Class_3001"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType Interface_3002 = getElementType("reflexactoring.diagram.Interface_3002"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType ModuleDependency_4001 = getElementType("reflexactoring.diagram.ModuleDependency_4001"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType ModuleExtend_4006 = getElementType("reflexactoring.diagram.ModuleExtend_4006"); //$NON-NLS-1$

	/**
	 * @generated
	 */
	public static final IElementType ClassExtend_4002 = getElementType("reflexactoring.diagram.ClassExtend_4002"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType TypeDependency_4003 = getElementType("reflexactoring.diagram.TypeDependency_4003"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType InterfaceExtend_4004 = getElementType("reflexactoring.diagram.InterfaceExtend_4004"); //$NON-NLS-1$
	/**
	 * @generated
	 */
	public static final IElementType Implement_4005 = getElementType("reflexactoring.diagram.Implement_4005"); //$NON-NLS-1$

	/**
	 * @generated
	 */
	private static ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			imageRegistry = new ImageRegistry();
		}
		return imageRegistry;
	}

	/**
	 * @generated
	 */
	private static String getImageRegistryKey(ENamedElement element) {
		return element.getName();
	}

	/**
	 * @generated
	 */
	private static ImageDescriptor getProvidedImageDescriptor(
			ENamedElement element) {
		if (element instanceof EStructuralFeature) {
			EStructuralFeature feature = ((EStructuralFeature) element);
			EClass eContainingClass = feature.getEContainingClass();
			EClassifier eType = feature.getEType();
			if (eContainingClass != null && !eContainingClass.isAbstract()) {
				element = eContainingClass;
			} else if (eType instanceof EClass
					&& !((EClass) eType).isAbstract()) {
				element = eType;
			}
		}
		if (element instanceof EClass) {
			EClass eClass = (EClass) element;
			if (!eClass.isAbstract()) {
				return ReflexactoringDiagramEditorPlugin.getInstance()
						.getItemImageDescriptor(
								eClass.getEPackage().getEFactoryInstance()
										.create(eClass));
			}
		}
		// TODO : support structural features
		return null;
	}

	/**
	 * @generated
	 */
	public static ImageDescriptor getImageDescriptor(ENamedElement element) {
		String key = getImageRegistryKey(element);
		ImageDescriptor imageDescriptor = getImageRegistry().getDescriptor(key);
		if (imageDescriptor == null) {
			imageDescriptor = getProvidedImageDescriptor(element);
			if (imageDescriptor == null) {
				imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
			}
			getImageRegistry().put(key, imageDescriptor);
		}
		return imageDescriptor;
	}

	/**
	 * @generated
	 */
	public static Image getImage(ENamedElement element) {
		String key = getImageRegistryKey(element);
		Image image = getImageRegistry().get(key);
		if (image == null) {
			ImageDescriptor imageDescriptor = getProvidedImageDescriptor(element);
			if (imageDescriptor == null) {
				imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
			}
			getImageRegistry().put(key, imageDescriptor);
			image = getImageRegistry().get(key);
		}
		return image;
	}

	/**
	 * @generated
	 */
	public static ImageDescriptor getImageDescriptor(IAdaptable hint) {
		ENamedElement element = getElement(hint);
		if (element == null) {
			return null;
		}
		return getImageDescriptor(element);
	}

	/**
	 * @generated
	 */
	public static Image getImage(IAdaptable hint) {
		ENamedElement element = getElement(hint);
		if (element == null) {
			return null;
		}
		return getImage(element);
	}

	/**
	 * Returns 'type' of the ecore object associated with the hint.
	 * 
	 * @generated
	 */
	public static ENamedElement getElement(IAdaptable hint) {
		Object type = hint.getAdapter(IElementType.class);
		if (elements == null) {
			elements = new IdentityHashMap<IElementType, ENamedElement>();

			elements.put(Reflexactoring_1000,
					ReflexactoringPackage.eINSTANCE.getReflexactoring());

			elements.put(Class_2001,
					ReflexactoringPackage.eINSTANCE.getClass_());

			elements.put(Interface_2002,
					ReflexactoringPackage.eINSTANCE.getInterface());

			elements.put(Module_2003,
					ReflexactoringPackage.eINSTANCE.getModule());

			elements.put(Class_3001,
					ReflexactoringPackage.eINSTANCE.getClass_());

			elements.put(Interface_3002,
					ReflexactoringPackage.eINSTANCE.getInterface());

			elements.put(ModuleDependency_4001,
					ReflexactoringPackage.eINSTANCE.getModuleDependency());

			elements.put(ModuleExtend_4006,
					ReflexactoringPackage.eINSTANCE.getModuleExtend());

			elements.put(ClassExtend_4002,
					ReflexactoringPackage.eINSTANCE.getClassExtend());

			elements.put(TypeDependency_4003,
					ReflexactoringPackage.eINSTANCE.getTypeDependency());

			elements.put(InterfaceExtend_4004,
					ReflexactoringPackage.eINSTANCE.getInterfaceExtend());

			elements.put(Implement_4005,
					ReflexactoringPackage.eINSTANCE.getImplement());
		}
		return (ENamedElement) elements.get(type);
	}

	/**
	 * @generated
	 */
	private static IElementType getElementType(String id) {
		return ElementTypeRegistry.getInstance().getType(id);
	}

	/**
	 * @generated
	 */
	public static boolean isKnownElementType(IElementType elementType) {
		if (KNOWN_ELEMENT_TYPES == null) {
			KNOWN_ELEMENT_TYPES = new HashSet<IElementType>();
			KNOWN_ELEMENT_TYPES.add(Reflexactoring_1000);
			KNOWN_ELEMENT_TYPES.add(Class_2001);
			KNOWN_ELEMENT_TYPES.add(Interface_2002);
			KNOWN_ELEMENT_TYPES.add(Module_2003);
			KNOWN_ELEMENT_TYPES.add(Class_3001);
			KNOWN_ELEMENT_TYPES.add(Interface_3002);
			KNOWN_ELEMENT_TYPES.add(ModuleDependency_4001);
			KNOWN_ELEMENT_TYPES.add(ModuleExtend_4006);
			KNOWN_ELEMENT_TYPES.add(ClassExtend_4002);
			KNOWN_ELEMENT_TYPES.add(TypeDependency_4003);
			KNOWN_ELEMENT_TYPES.add(InterfaceExtend_4004);
			KNOWN_ELEMENT_TYPES.add(Implement_4005);
		}
		return KNOWN_ELEMENT_TYPES.contains(elementType);
	}

	/**
	 * @generated
	 */
	public static IElementType getElementType(int visualID) {
		switch (visualID) {
		case ReflexactoringEditPart.VISUAL_ID:
			return Reflexactoring_1000;
		case ClassEditPart.VISUAL_ID:
			return Class_2001;
		case InterfaceEditPart.VISUAL_ID:
			return Interface_2002;
		case ModuleEditPart.VISUAL_ID:
			return Module_2003;
		case Class2EditPart.VISUAL_ID:
			return Class_3001;
		case Interface2EditPart.VISUAL_ID:
			return Interface_3002;
		case ModuleDependencyEditPart.VISUAL_ID:
			return ModuleDependency_4001;
		case ModuleExtendEditPart.VISUAL_ID:
			return ModuleExtend_4006;
		case ClassExtendEditPart.VISUAL_ID:
			return ClassExtend_4002;
		case TypeDependencyEditPart.VISUAL_ID:
			return TypeDependency_4003;
		case InterfaceExtendEditPart.VISUAL_ID:
			return InterfaceExtend_4004;
		case ImplementEditPart.VISUAL_ID:
			return Implement_4005;
		}
		return null;
	}

}
