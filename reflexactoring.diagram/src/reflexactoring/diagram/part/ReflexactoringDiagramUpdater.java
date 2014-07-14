package reflexactoring.diagram.part;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.tooling.runtime.update.DiagramUpdater;

import reflexactoring.Class;
import reflexactoring.ClassExtend;
import reflexactoring.Implement;
import reflexactoring.Interface;
import reflexactoring.InterfaceExtend;
import reflexactoring.Module;
import reflexactoring.ModuleDependency;
import reflexactoring.ModuleExtend;
import reflexactoring.Reflexactoring;
import reflexactoring.ReflexactoringPackage;
import reflexactoring.Type;
import reflexactoring.TypeDependency;
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
import reflexactoring.diagram.edit.parts.ModuleTypeContainerCompartmentEditPart;
import reflexactoring.diagram.edit.parts.ReflexactoringEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart;
import reflexactoring.diagram.providers.ReflexactoringElementTypes;

/**
 * @generated
 */
public class ReflexactoringDiagramUpdater {

	/**
	 * @generated
	 */
	public static List<ReflexactoringNodeDescriptor> getSemanticChildren(
			View view) {
		switch (ReflexactoringVisualIDRegistry.getVisualID(view)) {
		case ReflexactoringEditPart.VISUAL_ID:
			return getReflexactoring_1000SemanticChildren(view);
		case ModuleTypeContainerCompartmentEditPart.VISUAL_ID:
			return getModuleTypeContainerCompartment_7001SemanticChildren(view);
		}
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringNodeDescriptor> getReflexactoring_1000SemanticChildren(
			View view) {
		if (!view.isSetElement()) {
			return Collections.emptyList();
		}
		Reflexactoring modelElement = (Reflexactoring) view.getElement();
		LinkedList<ReflexactoringNodeDescriptor> result = new LinkedList<ReflexactoringNodeDescriptor>();
		for (Iterator<?> it = modelElement.getTypes().iterator(); it.hasNext();) {
			Type childElement = (Type) it.next();
			int visualID = ReflexactoringVisualIDRegistry.getNodeVisualID(view,
					childElement);
			if (visualID == ClassEditPart.VISUAL_ID) {
				result.add(new ReflexactoringNodeDescriptor(childElement,
						visualID));
				continue;
			}
			if (visualID == InterfaceEditPart.VISUAL_ID) {
				result.add(new ReflexactoringNodeDescriptor(childElement,
						visualID));
				continue;
			}
		}
		for (Iterator<?> it = modelElement.getModules().iterator(); it
				.hasNext();) {
			Module childElement = (Module) it.next();
			int visualID = ReflexactoringVisualIDRegistry.getNodeVisualID(view,
					childElement);
			if (visualID == ModuleEditPart.VISUAL_ID) {
				result.add(new ReflexactoringNodeDescriptor(childElement,
						visualID));
				continue;
			}
		}
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringNodeDescriptor> getModuleTypeContainerCompartment_7001SemanticChildren(
			View view) {
		if (false == view.eContainer() instanceof View) {
			return Collections.emptyList();
		}
		View containerView = (View) view.eContainer();
		if (!containerView.isSetElement()) {
			return Collections.emptyList();
		}
		Module modelElement = (Module) containerView.getElement();
		LinkedList<ReflexactoringNodeDescriptor> result = new LinkedList<ReflexactoringNodeDescriptor>();
		for (Iterator<?> it = modelElement.getMappingTypes().iterator(); it
				.hasNext();) {
			Type childElement = (Type) it.next();
			int visualID = ReflexactoringVisualIDRegistry.getNodeVisualID(view,
					childElement);
			if (visualID == Class2EditPart.VISUAL_ID) {
				result.add(new ReflexactoringNodeDescriptor(childElement,
						visualID));
				continue;
			}
			if (visualID == Interface2EditPart.VISUAL_ID) {
				result.add(new ReflexactoringNodeDescriptor(childElement,
						visualID));
				continue;
			}
		}
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getContainedLinks(View view) {
		switch (ReflexactoringVisualIDRegistry.getVisualID(view)) {
		case ReflexactoringEditPart.VISUAL_ID:
			return getReflexactoring_1000ContainedLinks(view);
		case ClassEditPart.VISUAL_ID:
			return getClass_2001ContainedLinks(view);
		case InterfaceEditPart.VISUAL_ID:
			return getInterface_2002ContainedLinks(view);
		case ModuleEditPart.VISUAL_ID:
			return getModule_2003ContainedLinks(view);
		case Class2EditPart.VISUAL_ID:
			return getClass_3001ContainedLinks(view);
		case Interface2EditPart.VISUAL_ID:
			return getInterface_3002ContainedLinks(view);
		case ModuleDependencyEditPart.VISUAL_ID:
			return getModuleDependency_4001ContainedLinks(view);
		case ModuleExtendEditPart.VISUAL_ID:
			return getModuleExtend_4006ContainedLinks(view);
		case ClassExtendEditPart.VISUAL_ID:
			return getClassExtend_4002ContainedLinks(view);
		case TypeDependencyEditPart.VISUAL_ID:
			return getTypeDependency_4003ContainedLinks(view);
		case InterfaceExtendEditPart.VISUAL_ID:
			return getInterfaceExtend_4004ContainedLinks(view);
		case ImplementEditPart.VISUAL_ID:
			return getImplement_4005ContainedLinks(view);
		}
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getIncomingLinks(View view) {
		switch (ReflexactoringVisualIDRegistry.getVisualID(view)) {
		case ClassEditPart.VISUAL_ID:
			return getClass_2001IncomingLinks(view);
		case InterfaceEditPart.VISUAL_ID:
			return getInterface_2002IncomingLinks(view);
		case ModuleEditPart.VISUAL_ID:
			return getModule_2003IncomingLinks(view);
		case Class2EditPart.VISUAL_ID:
			return getClass_3001IncomingLinks(view);
		case Interface2EditPart.VISUAL_ID:
			return getInterface_3002IncomingLinks(view);
		case ModuleDependencyEditPart.VISUAL_ID:
			return getModuleDependency_4001IncomingLinks(view);
		case ModuleExtendEditPart.VISUAL_ID:
			return getModuleExtend_4006IncomingLinks(view);
		case ClassExtendEditPart.VISUAL_ID:
			return getClassExtend_4002IncomingLinks(view);
		case TypeDependencyEditPart.VISUAL_ID:
			return getTypeDependency_4003IncomingLinks(view);
		case InterfaceExtendEditPart.VISUAL_ID:
			return getInterfaceExtend_4004IncomingLinks(view);
		case ImplementEditPart.VISUAL_ID:
			return getImplement_4005IncomingLinks(view);
		}
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getOutgoingLinks(View view) {
		switch (ReflexactoringVisualIDRegistry.getVisualID(view)) {
		case ClassEditPart.VISUAL_ID:
			return getClass_2001OutgoingLinks(view);
		case InterfaceEditPart.VISUAL_ID:
			return getInterface_2002OutgoingLinks(view);
		case ModuleEditPart.VISUAL_ID:
			return getModule_2003OutgoingLinks(view);
		case Class2EditPart.VISUAL_ID:
			return getClass_3001OutgoingLinks(view);
		case Interface2EditPart.VISUAL_ID:
			return getInterface_3002OutgoingLinks(view);
		case ModuleDependencyEditPart.VISUAL_ID:
			return getModuleDependency_4001OutgoingLinks(view);
		case ModuleExtendEditPart.VISUAL_ID:
			return getModuleExtend_4006OutgoingLinks(view);
		case ClassExtendEditPart.VISUAL_ID:
			return getClassExtend_4002OutgoingLinks(view);
		case TypeDependencyEditPart.VISUAL_ID:
			return getTypeDependency_4003OutgoingLinks(view);
		case InterfaceExtendEditPart.VISUAL_ID:
			return getInterfaceExtend_4004OutgoingLinks(view);
		case ImplementEditPart.VISUAL_ID:
			return getImplement_4005OutgoingLinks(view);
		}
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getReflexactoring_1000ContainedLinks(
			View view) {
		Reflexactoring modelElement = (Reflexactoring) view.getElement();
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		result.addAll(getContainedTypeModelFacetLinks_ModuleDependency_4001(modelElement));
		result.addAll(getContainedTypeModelFacetLinks_ModuleExtend_4006(modelElement));
		result.addAll(getContainedTypeModelFacetLinks_ClassExtend_4002(modelElement));
		result.addAll(getContainedTypeModelFacetLinks_TypeDependency_4003(modelElement));
		result.addAll(getContainedTypeModelFacetLinks_InterfaceExtend_4004(modelElement));
		result.addAll(getContainedTypeModelFacetLinks_Implement_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getClass_2001ContainedLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getInterface_2002ContainedLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getModule_2003ContainedLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getClass_3001ContainedLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getInterface_3002ContainedLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getModuleDependency_4001ContainedLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getModuleExtend_4006ContainedLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getClassExtend_4002ContainedLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getTypeDependency_4003ContainedLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getInterfaceExtend_4004ContainedLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getImplement_4005ContainedLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getClass_2001IncomingLinks(
			View view) {
		Class modelElement = (Class) view.getElement();
		Map<EObject, Collection<EStructuralFeature.Setting>> crossReferences = EcoreUtil.CrossReferencer
				.find(view.eResource().getResourceSet().getResources());
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		result.addAll(getIncomingTypeModelFacetLinks_ClassExtend_4002(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_TypeDependency_4003(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getInterface_2002IncomingLinks(
			View view) {
		Interface modelElement = (Interface) view.getElement();
		Map<EObject, Collection<EStructuralFeature.Setting>> crossReferences = EcoreUtil.CrossReferencer
				.find(view.eResource().getResourceSet().getResources());
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		result.addAll(getIncomingTypeModelFacetLinks_TypeDependency_4003(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_InterfaceExtend_4004(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_Implement_4005(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getModule_2003IncomingLinks(
			View view) {
		Module modelElement = (Module) view.getElement();
		Map<EObject, Collection<EStructuralFeature.Setting>> crossReferences = EcoreUtil.CrossReferencer
				.find(view.eResource().getResourceSet().getResources());
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		result.addAll(getIncomingTypeModelFacetLinks_ModuleDependency_4001(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_ModuleExtend_4006(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getClass_3001IncomingLinks(
			View view) {
		Class modelElement = (Class) view.getElement();
		Map<EObject, Collection<EStructuralFeature.Setting>> crossReferences = EcoreUtil.CrossReferencer
				.find(view.eResource().getResourceSet().getResources());
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		result.addAll(getIncomingTypeModelFacetLinks_ClassExtend_4002(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_TypeDependency_4003(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getInterface_3002IncomingLinks(
			View view) {
		Interface modelElement = (Interface) view.getElement();
		Map<EObject, Collection<EStructuralFeature.Setting>> crossReferences = EcoreUtil.CrossReferencer
				.find(view.eResource().getResourceSet().getResources());
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		result.addAll(getIncomingTypeModelFacetLinks_TypeDependency_4003(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_InterfaceExtend_4004(
				modelElement, crossReferences));
		result.addAll(getIncomingTypeModelFacetLinks_Implement_4005(
				modelElement, crossReferences));
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getModuleDependency_4001IncomingLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getModuleExtend_4006IncomingLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getClassExtend_4002IncomingLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getTypeDependency_4003IncomingLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getInterfaceExtend_4004IncomingLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getImplement_4005IncomingLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getClass_2001OutgoingLinks(
			View view) {
		Class modelElement = (Class) view.getElement();
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		result.addAll(getOutgoingTypeModelFacetLinks_ClassExtend_4002(modelElement));
		result.addAll(getOutgoingTypeModelFacetLinks_TypeDependency_4003(modelElement));
		result.addAll(getOutgoingTypeModelFacetLinks_Implement_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getInterface_2002OutgoingLinks(
			View view) {
		Interface modelElement = (Interface) view.getElement();
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		result.addAll(getOutgoingTypeModelFacetLinks_TypeDependency_4003(modelElement));
		result.addAll(getOutgoingTypeModelFacetLinks_InterfaceExtend_4004(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getModule_2003OutgoingLinks(
			View view) {
		Module modelElement = (Module) view.getElement();
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		result.addAll(getOutgoingTypeModelFacetLinks_ModuleDependency_4001(modelElement));
		result.addAll(getOutgoingTypeModelFacetLinks_ModuleExtend_4006(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getClass_3001OutgoingLinks(
			View view) {
		Class modelElement = (Class) view.getElement();
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		result.addAll(getOutgoingTypeModelFacetLinks_ClassExtend_4002(modelElement));
		result.addAll(getOutgoingTypeModelFacetLinks_TypeDependency_4003(modelElement));
		result.addAll(getOutgoingTypeModelFacetLinks_Implement_4005(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getInterface_3002OutgoingLinks(
			View view) {
		Interface modelElement = (Interface) view.getElement();
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		result.addAll(getOutgoingTypeModelFacetLinks_TypeDependency_4003(modelElement));
		result.addAll(getOutgoingTypeModelFacetLinks_InterfaceExtend_4004(modelElement));
		return result;
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getModuleDependency_4001OutgoingLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getModuleExtend_4006OutgoingLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getClassExtend_4002OutgoingLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getTypeDependency_4003OutgoingLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getInterfaceExtend_4004OutgoingLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	public static List<ReflexactoringLinkDescriptor> getImplement_4005OutgoingLinks(
			View view) {
		return Collections.emptyList();
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getContainedTypeModelFacetLinks_ModuleDependency_4001(
			Reflexactoring container) {
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		for (Iterator<?> links = container.getModuleDenpencies().iterator(); links
				.hasNext();) {
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof ModuleDependency) {
				continue;
			}
			ModuleDependency link = (ModuleDependency) linkObject;
			if (ModuleDependencyEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Module dst = link.getDestination();
			Module src = link.getOrigin();
			result.add(new ReflexactoringLinkDescriptor(src, dst, link,
					ReflexactoringElementTypes.ModuleDependency_4001,
					ModuleDependencyEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getContainedTypeModelFacetLinks_ModuleExtend_4006(
			Reflexactoring container) {
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		for (Iterator<?> links = container.getModuleDenpencies().iterator(); links
				.hasNext();) {
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof ModuleExtend) {
				continue;
			}
			ModuleExtend link = (ModuleExtend) linkObject;
			if (ModuleExtendEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Module dst = link.getDestination();
			Module src = link.getOrigin();
			result.add(new ReflexactoringLinkDescriptor(src, dst, link,
					ReflexactoringElementTypes.ModuleExtend_4006,
					ModuleExtendEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getContainedTypeModelFacetLinks_ClassExtend_4002(
			Reflexactoring container) {
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		for (Iterator<?> links = container.getClassExtends().iterator(); links
				.hasNext();) {
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof ClassExtend) {
				continue;
			}
			ClassExtend link = (ClassExtend) linkObject;
			if (ClassExtendEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Class dst = link.getSuperClass();
			Class src = link.getSubClass();
			result.add(new ReflexactoringLinkDescriptor(src, dst, link,
					ReflexactoringElementTypes.ClassExtend_4002,
					ClassExtendEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getContainedTypeModelFacetLinks_TypeDependency_4003(
			Reflexactoring container) {
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		for (Iterator<?> links = container.getTypeDependencies().iterator(); links
				.hasNext();) {
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof TypeDependency) {
				continue;
			}
			TypeDependency link = (TypeDependency) linkObject;
			if (TypeDependencyEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Type dst = link.getDestination();
			Type src = link.getOrigin();
			result.add(new ReflexactoringLinkDescriptor(src, dst, link,
					ReflexactoringElementTypes.TypeDependency_4003,
					TypeDependencyEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getContainedTypeModelFacetLinks_InterfaceExtend_4004(
			Reflexactoring container) {
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		for (Iterator<?> links = container.getInterfaceExtends().iterator(); links
				.hasNext();) {
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof InterfaceExtend) {
				continue;
			}
			InterfaceExtend link = (InterfaceExtend) linkObject;
			if (InterfaceExtendEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Interface dst = link.getSuperInterface();
			Interface src = link.getSubInterface();
			result.add(new ReflexactoringLinkDescriptor(src, dst, link,
					ReflexactoringElementTypes.InterfaceExtend_4004,
					InterfaceExtendEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getContainedTypeModelFacetLinks_Implement_4005(
			Reflexactoring container) {
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		for (Iterator<?> links = container.getImplements().iterator(); links
				.hasNext();) {
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof Implement) {
				continue;
			}
			Implement link = (Implement) linkObject;
			if (ImplementEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Interface dst = link.getInterface();
			Class src = link.getClass_();
			result.add(new ReflexactoringLinkDescriptor(src, dst, link,
					ReflexactoringElementTypes.Implement_4005,
					ImplementEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getIncomingTypeModelFacetLinks_ModuleDependency_4001(
			Module target,
			Map<EObject, Collection<EStructuralFeature.Setting>> crossReferences) {
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		Collection<EStructuralFeature.Setting> settings = crossReferences
				.get(target);
		for (EStructuralFeature.Setting setting : settings) {
			if (setting.getEStructuralFeature() != ReflexactoringPackage.eINSTANCE
					.getModuleLink_Destination()
					|| false == setting.getEObject() instanceof ModuleDependency) {
				continue;
			}
			ModuleDependency link = (ModuleDependency) setting.getEObject();
			if (ModuleDependencyEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Module src = link.getOrigin();
			result.add(new ReflexactoringLinkDescriptor(src, target, link,
					ReflexactoringElementTypes.ModuleDependency_4001,
					ModuleDependencyEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getIncomingTypeModelFacetLinks_ModuleExtend_4006(
			Module target,
			Map<EObject, Collection<EStructuralFeature.Setting>> crossReferences) {
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		Collection<EStructuralFeature.Setting> settings = crossReferences
				.get(target);
		for (EStructuralFeature.Setting setting : settings) {
			if (setting.getEStructuralFeature() != ReflexactoringPackage.eINSTANCE
					.getModuleLink_Destination()
					|| false == setting.getEObject() instanceof ModuleExtend) {
				continue;
			}
			ModuleExtend link = (ModuleExtend) setting.getEObject();
			if (ModuleExtendEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Module src = link.getOrigin();
			result.add(new ReflexactoringLinkDescriptor(src, target, link,
					ReflexactoringElementTypes.ModuleExtend_4006,
					ModuleExtendEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getIncomingTypeModelFacetLinks_ClassExtend_4002(
			Class target,
			Map<EObject, Collection<EStructuralFeature.Setting>> crossReferences) {
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		Collection<EStructuralFeature.Setting> settings = crossReferences
				.get(target);
		for (EStructuralFeature.Setting setting : settings) {
			if (setting.getEStructuralFeature() != ReflexactoringPackage.eINSTANCE
					.getClassExtend_SuperClass()
					|| false == setting.getEObject() instanceof ClassExtend) {
				continue;
			}
			ClassExtend link = (ClassExtend) setting.getEObject();
			if (ClassExtendEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Class src = link.getSubClass();
			result.add(new ReflexactoringLinkDescriptor(src, target, link,
					ReflexactoringElementTypes.ClassExtend_4002,
					ClassExtendEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getIncomingTypeModelFacetLinks_TypeDependency_4003(
			Type target,
			Map<EObject, Collection<EStructuralFeature.Setting>> crossReferences) {
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		Collection<EStructuralFeature.Setting> settings = crossReferences
				.get(target);
		for (EStructuralFeature.Setting setting : settings) {
			if (setting.getEStructuralFeature() != ReflexactoringPackage.eINSTANCE
					.getTypeDependency_Destination()
					|| false == setting.getEObject() instanceof TypeDependency) {
				continue;
			}
			TypeDependency link = (TypeDependency) setting.getEObject();
			if (TypeDependencyEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Type src = link.getOrigin();
			result.add(new ReflexactoringLinkDescriptor(src, target, link,
					ReflexactoringElementTypes.TypeDependency_4003,
					TypeDependencyEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getIncomingTypeModelFacetLinks_InterfaceExtend_4004(
			Interface target,
			Map<EObject, Collection<EStructuralFeature.Setting>> crossReferences) {
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		Collection<EStructuralFeature.Setting> settings = crossReferences
				.get(target);
		for (EStructuralFeature.Setting setting : settings) {
			if (setting.getEStructuralFeature() != ReflexactoringPackage.eINSTANCE
					.getInterfaceExtend_SuperInterface()
					|| false == setting.getEObject() instanceof InterfaceExtend) {
				continue;
			}
			InterfaceExtend link = (InterfaceExtend) setting.getEObject();
			if (InterfaceExtendEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Interface src = link.getSubInterface();
			result.add(new ReflexactoringLinkDescriptor(src, target, link,
					ReflexactoringElementTypes.InterfaceExtend_4004,
					InterfaceExtendEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getIncomingTypeModelFacetLinks_Implement_4005(
			Interface target,
			Map<EObject, Collection<EStructuralFeature.Setting>> crossReferences) {
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		Collection<EStructuralFeature.Setting> settings = crossReferences
				.get(target);
		for (EStructuralFeature.Setting setting : settings) {
			if (setting.getEStructuralFeature() != ReflexactoringPackage.eINSTANCE
					.getImplement_Interface()
					|| false == setting.getEObject() instanceof Implement) {
				continue;
			}
			Implement link = (Implement) setting.getEObject();
			if (ImplementEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Class src = link.getClass_();
			result.add(new ReflexactoringLinkDescriptor(src, target, link,
					ReflexactoringElementTypes.Implement_4005,
					ImplementEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getOutgoingTypeModelFacetLinks_ModuleDependency_4001(
			Module source) {
		Reflexactoring container = null;
		// Find container element for the link.
		// Climb up by containment hierarchy starting from the source
		// and return the first element that is instance of the container class.
		for (EObject element = source; element != null && container == null; element = element
				.eContainer()) {
			if (element instanceof Reflexactoring) {
				container = (Reflexactoring) element;
			}
		}
		if (container == null) {
			return Collections.emptyList();
		}
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		for (Iterator<?> links = container.getModuleDenpencies().iterator(); links
				.hasNext();) {
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof ModuleDependency) {
				continue;
			}
			ModuleDependency link = (ModuleDependency) linkObject;
			if (ModuleDependencyEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Module dst = link.getDestination();
			Module src = link.getOrigin();
			if (src != source) {
				continue;
			}
			result.add(new ReflexactoringLinkDescriptor(src, dst, link,
					ReflexactoringElementTypes.ModuleDependency_4001,
					ModuleDependencyEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getOutgoingTypeModelFacetLinks_ModuleExtend_4006(
			Module source) {
		Reflexactoring container = null;
		// Find container element for the link.
		// Climb up by containment hierarchy starting from the source
		// and return the first element that is instance of the container class.
		for (EObject element = source; element != null && container == null; element = element
				.eContainer()) {
			if (element instanceof Reflexactoring) {
				container = (Reflexactoring) element;
			}
		}
		if (container == null) {
			return Collections.emptyList();
		}
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		for (Iterator<?> links = container.getModuleDenpencies().iterator(); links
				.hasNext();) {
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof ModuleExtend) {
				continue;
			}
			ModuleExtend link = (ModuleExtend) linkObject;
			if (ModuleExtendEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Module dst = link.getDestination();
			Module src = link.getOrigin();
			if (src != source) {
				continue;
			}
			result.add(new ReflexactoringLinkDescriptor(src, dst, link,
					ReflexactoringElementTypes.ModuleExtend_4006,
					ModuleExtendEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getOutgoingTypeModelFacetLinks_ClassExtend_4002(
			Class source) {
		Reflexactoring container = null;
		// Find container element for the link.
		// Climb up by containment hierarchy starting from the source
		// and return the first element that is instance of the container class.
		for (EObject element = source; element != null && container == null; element = element
				.eContainer()) {
			if (element instanceof Reflexactoring) {
				container = (Reflexactoring) element;
			}
		}
		if (container == null) {
			return Collections.emptyList();
		}
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		for (Iterator<?> links = container.getClassExtends().iterator(); links
				.hasNext();) {
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof ClassExtend) {
				continue;
			}
			ClassExtend link = (ClassExtend) linkObject;
			if (ClassExtendEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Class dst = link.getSuperClass();
			Class src = link.getSubClass();
			if (src != source) {
				continue;
			}
			result.add(new ReflexactoringLinkDescriptor(src, dst, link,
					ReflexactoringElementTypes.ClassExtend_4002,
					ClassExtendEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getOutgoingTypeModelFacetLinks_TypeDependency_4003(
			Type source) {
		Reflexactoring container = null;
		// Find container element for the link.
		// Climb up by containment hierarchy starting from the source
		// and return the first element that is instance of the container class.
		for (EObject element = source; element != null && container == null; element = element
				.eContainer()) {
			if (element instanceof Reflexactoring) {
				container = (Reflexactoring) element;
			}
		}
		if (container == null) {
			return Collections.emptyList();
		}
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		for (Iterator<?> links = container.getTypeDependencies().iterator(); links
				.hasNext();) {
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof TypeDependency) {
				continue;
			}
			TypeDependency link = (TypeDependency) linkObject;
			if (TypeDependencyEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Type dst = link.getDestination();
			Type src = link.getOrigin();
			if (src != source) {
				continue;
			}
			result.add(new ReflexactoringLinkDescriptor(src, dst, link,
					ReflexactoringElementTypes.TypeDependency_4003,
					TypeDependencyEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getOutgoingTypeModelFacetLinks_InterfaceExtend_4004(
			Interface source) {
		Reflexactoring container = null;
		// Find container element for the link.
		// Climb up by containment hierarchy starting from the source
		// and return the first element that is instance of the container class.
		for (EObject element = source; element != null && container == null; element = element
				.eContainer()) {
			if (element instanceof Reflexactoring) {
				container = (Reflexactoring) element;
			}
		}
		if (container == null) {
			return Collections.emptyList();
		}
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		for (Iterator<?> links = container.getInterfaceExtends().iterator(); links
				.hasNext();) {
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof InterfaceExtend) {
				continue;
			}
			InterfaceExtend link = (InterfaceExtend) linkObject;
			if (InterfaceExtendEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Interface dst = link.getSuperInterface();
			Interface src = link.getSubInterface();
			if (src != source) {
				continue;
			}
			result.add(new ReflexactoringLinkDescriptor(src, dst, link,
					ReflexactoringElementTypes.InterfaceExtend_4004,
					InterfaceExtendEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private static Collection<ReflexactoringLinkDescriptor> getOutgoingTypeModelFacetLinks_Implement_4005(
			Class source) {
		Reflexactoring container = null;
		// Find container element for the link.
		// Climb up by containment hierarchy starting from the source
		// and return the first element that is instance of the container class.
		for (EObject element = source; element != null && container == null; element = element
				.eContainer()) {
			if (element instanceof Reflexactoring) {
				container = (Reflexactoring) element;
			}
		}
		if (container == null) {
			return Collections.emptyList();
		}
		LinkedList<ReflexactoringLinkDescriptor> result = new LinkedList<ReflexactoringLinkDescriptor>();
		for (Iterator<?> links = container.getImplements().iterator(); links
				.hasNext();) {
			EObject linkObject = (EObject) links.next();
			if (false == linkObject instanceof Implement) {
				continue;
			}
			Implement link = (Implement) linkObject;
			if (ImplementEditPart.VISUAL_ID != ReflexactoringVisualIDRegistry
					.getLinkWithClassVisualID(link)) {
				continue;
			}
			Interface dst = link.getInterface();
			Class src = link.getClass_();
			if (src != source) {
				continue;
			}
			result.add(new ReflexactoringLinkDescriptor(src, dst, link,
					ReflexactoringElementTypes.Implement_4005,
					ImplementEditPart.VISUAL_ID));
		}
		return result;
	}

	/**
	 * @generated
	 */
	public static final DiagramUpdater TYPED_INSTANCE = new DiagramUpdater() {
		/**
		 * @generated
		 */
		@Override
		public List<ReflexactoringNodeDescriptor> getSemanticChildren(View view) {
			return ReflexactoringDiagramUpdater.getSemanticChildren(view);
		}

		/**
		 * @generated
		 */
		@Override
		public List<ReflexactoringLinkDescriptor> getContainedLinks(View view) {
			return ReflexactoringDiagramUpdater.getContainedLinks(view);
		}

		/**
		 * @generated
		 */
		@Override
		public List<ReflexactoringLinkDescriptor> getIncomingLinks(View view) {
			return ReflexactoringDiagramUpdater.getIncomingLinks(view);
		}

		/**
		 * @generated
		 */
		@Override
		public List<ReflexactoringLinkDescriptor> getOutgoingLinks(View view) {
			return ReflexactoringDiagramUpdater.getOutgoingLinks(view);
		}
	};

}
