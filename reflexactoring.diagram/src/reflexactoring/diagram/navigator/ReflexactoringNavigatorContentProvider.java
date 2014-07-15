package reflexactoring.diagram.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gmf.runtime.emf.core.GMFEditingDomainFactory;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;

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
import reflexactoring.diagram.part.Messages;
import reflexactoring.diagram.part.ReflexactoringVisualIDRegistry;

/**
 * @generated
 */
public class ReflexactoringNavigatorContentProvider implements
		ICommonContentProvider {

	/**
	 * @generated
	 */
	private static final Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * @generated
	 */
	private Viewer myViewer;

	/**
	 * @generated
	 */
	private AdapterFactoryEditingDomain myEditingDomain;

	/**
	 * @generated
	 */
	private WorkspaceSynchronizer myWorkspaceSynchronizer;

	/**
	 * @generated
	 */
	private Runnable myViewerRefreshRunnable;

	/**
	 * @generated
	 */
	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	public ReflexactoringNavigatorContentProvider() {
		TransactionalEditingDomain editingDomain = GMFEditingDomainFactory.INSTANCE
				.createEditingDomain();
		myEditingDomain = (AdapterFactoryEditingDomain) editingDomain;
		myEditingDomain.setResourceToReadOnlyMap(new HashMap() {
			public Object get(Object key) {
				if (!containsKey(key)) {
					put(key, Boolean.TRUE);
				}
				return super.get(key);
			}
		});
		myViewerRefreshRunnable = new Runnable() {
			public void run() {
				if (myViewer != null) {
					myViewer.refresh();
				}
			}
		};
		myWorkspaceSynchronizer = new WorkspaceSynchronizer(editingDomain,
				new WorkspaceSynchronizer.Delegate() {
					public void dispose() {
					}

					public boolean handleResourceChanged(final Resource resource) {
						unloadAllResources();
						asyncRefresh();
						return true;
					}

					public boolean handleResourceDeleted(Resource resource) {
						unloadAllResources();
						asyncRefresh();
						return true;
					}

					public boolean handleResourceMoved(Resource resource,
							final URI newURI) {
						unloadAllResources();
						asyncRefresh();
						return true;
					}
				});
	}

	/**
	 * @generated
	 */
	public void dispose() {
		myWorkspaceSynchronizer.dispose();
		myWorkspaceSynchronizer = null;
		myViewerRefreshRunnable = null;
		myViewer = null;
		unloadAllResources();
		((TransactionalEditingDomain) myEditingDomain).dispose();
		myEditingDomain = null;
	}

	/**
	 * @generated
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		myViewer = viewer;
	}

	/**
	 * @generated
	 */
	void unloadAllResources() {
		for (Resource nextResource : myEditingDomain.getResourceSet()
				.getResources()) {
			nextResource.unload();
		}
	}

	/**
	 * @generated
	 */
	void asyncRefresh() {
		if (myViewer != null && !myViewer.getControl().isDisposed()) {
			myViewer.getControl().getDisplay()
					.asyncExec(myViewerRefreshRunnable);
		}
	}

	/**
	 * @generated
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/**
	 * @generated
	 */
	public void restoreState(IMemento aMemento) {
	}

	/**
	 * @generated
	 */
	public void saveState(IMemento aMemento) {
	}

	/**
	 * @generated
	 */
	public void init(ICommonContentExtensionSite aConfig) {
	}

	/**
	 * @generated
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IFile) {
			IFile file = (IFile) parentElement;
			URI fileURI = URI.createPlatformResourceURI(file.getFullPath()
					.toString(), true);
			Resource resource = myEditingDomain.getResourceSet().getResource(
					fileURI, true);
			ArrayList<ReflexactoringNavigatorItem> result = new ArrayList<ReflexactoringNavigatorItem>();
			ArrayList<View> topViews = new ArrayList<View>(resource
					.getContents().size());
			for (EObject o : resource.getContents()) {
				if (o instanceof View) {
					topViews.add((View) o);
				}
			}
			result.addAll(createNavigatorItems(
					selectViewsByType(topViews, ReflexactoringEditPart.MODEL_ID),
					file, false));
			return result.toArray();
		}

		if (parentElement instanceof ReflexactoringNavigatorGroup) {
			ReflexactoringNavigatorGroup group = (ReflexactoringNavigatorGroup) parentElement;
			return group.getChildren();
		}

		if (parentElement instanceof ReflexactoringNavigatorItem) {
			ReflexactoringNavigatorItem navigatorItem = (ReflexactoringNavigatorItem) parentElement;
			if (navigatorItem.isLeaf() || !isOwnView(navigatorItem.getView())) {
				return EMPTY_ARRAY;
			}
			return getViewChildren(navigatorItem.getView(), parentElement);
		}

		return EMPTY_ARRAY;
	}

	/**
	 * @generated
	 */
	private Object[] getViewChildren(View view, Object parentElement) {
		switch (ReflexactoringVisualIDRegistry.getVisualID(view)) {

		case ClassEditPart.VISUAL_ID: {
			LinkedList<ReflexactoringAbstractNavigatorItem> result = new LinkedList<ReflexactoringAbstractNavigatorItem>();
			Node sv = (Node) view;
			ReflexactoringNavigatorGroup incominglinks = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Class_2001_incominglinks,
					"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			ReflexactoringNavigatorGroup outgoinglinks = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Class_2001_outgoinglinks,
					"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			Collection<View> connectedViews;
			connectedViews = getIncomingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ClassExtendEditPart.VISUAL_ID));
			incominglinks.addChildren(createNavigatorItems(connectedViews,
					incominglinks, true));
			connectedViews = getOutgoingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ClassExtendEditPart.VISUAL_ID));
			outgoinglinks.addChildren(createNavigatorItems(connectedViews,
					outgoinglinks, true));
			connectedViews = getIncomingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(TypeDependencyEditPart.VISUAL_ID));
			incominglinks.addChildren(createNavigatorItems(connectedViews,
					incominglinks, true));
			connectedViews = getOutgoingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(TypeDependencyEditPart.VISUAL_ID));
			outgoinglinks.addChildren(createNavigatorItems(connectedViews,
					outgoinglinks, true));
			connectedViews = getOutgoingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ImplementEditPart.VISUAL_ID));
			outgoinglinks.addChildren(createNavigatorItems(connectedViews,
					outgoinglinks, true));
			if (!incominglinks.isEmpty()) {
				result.add(incominglinks);
			}
			if (!outgoinglinks.isEmpty()) {
				result.add(outgoinglinks);
			}
			return result.toArray();
		}

		case ReflexactoringEditPart.VISUAL_ID: {
			LinkedList<ReflexactoringAbstractNavigatorItem> result = new LinkedList<ReflexactoringAbstractNavigatorItem>();
			Diagram sv = (Diagram) view;
			ReflexactoringNavigatorGroup links = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Reflexactoring_1000_links,
					"icons/linksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			Collection<View> connectedViews;
			connectedViews = getChildrenByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ClassEditPart.VISUAL_ID));
			result.addAll(createNavigatorItems(connectedViews, parentElement,
					false));
			connectedViews = getChildrenByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(InterfaceEditPart.VISUAL_ID));
			result.addAll(createNavigatorItems(connectedViews, parentElement,
					false));
			connectedViews = getChildrenByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleEditPart.VISUAL_ID));
			result.addAll(createNavigatorItems(connectedViews, parentElement,
					false));
			connectedViews = getDiagramLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleDependencyEditPart.VISUAL_ID));
			links.addChildren(createNavigatorItems(connectedViews, links, false));
			connectedViews = getDiagramLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleExtendEditPart.VISUAL_ID));
			links.addChildren(createNavigatorItems(connectedViews, links, false));
			connectedViews = getDiagramLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ClassExtendEditPart.VISUAL_ID));
			links.addChildren(createNavigatorItems(connectedViews, links, false));
			connectedViews = getDiagramLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(TypeDependencyEditPart.VISUAL_ID));
			links.addChildren(createNavigatorItems(connectedViews, links, false));
			connectedViews = getDiagramLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(InterfaceExtendEditPart.VISUAL_ID));
			links.addChildren(createNavigatorItems(connectedViews, links, false));
			connectedViews = getDiagramLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ImplementEditPart.VISUAL_ID));
			links.addChildren(createNavigatorItems(connectedViews, links, false));
			if (!links.isEmpty()) {
				result.add(links);
			}
			return result.toArray();
		}

		case ModuleEditPart.VISUAL_ID: {
			LinkedList<ReflexactoringAbstractNavigatorItem> result = new LinkedList<ReflexactoringAbstractNavigatorItem>();
			Node sv = (Node) view;
			ReflexactoringNavigatorGroup incominglinks = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Module_2003_incominglinks,
					"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			ReflexactoringNavigatorGroup outgoinglinks = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Module_2003_outgoinglinks,
					"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			Collection<View> connectedViews;
			connectedViews = getChildrenByType(
					Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleTypeContainerCompartmentEditPart.VISUAL_ID));
			connectedViews = getChildrenByType(connectedViews,
					ReflexactoringVisualIDRegistry
							.getType(Class2EditPart.VISUAL_ID));
			result.addAll(createNavigatorItems(connectedViews, parentElement,
					false));
			connectedViews = getChildrenByType(
					Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleTypeContainerCompartmentEditPart.VISUAL_ID));
			connectedViews = getChildrenByType(connectedViews,
					ReflexactoringVisualIDRegistry
							.getType(Interface2EditPart.VISUAL_ID));
			result.addAll(createNavigatorItems(connectedViews, parentElement,
					false));
			connectedViews = getIncomingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleDependencyEditPart.VISUAL_ID));
			incominglinks.addChildren(createNavigatorItems(connectedViews,
					incominglinks, true));
			connectedViews = getOutgoingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleDependencyEditPart.VISUAL_ID));
			outgoinglinks.addChildren(createNavigatorItems(connectedViews,
					outgoinglinks, true));
			connectedViews = getIncomingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleExtendEditPart.VISUAL_ID));
			incominglinks.addChildren(createNavigatorItems(connectedViews,
					incominglinks, true));
			connectedViews = getOutgoingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleExtendEditPart.VISUAL_ID));
			outgoinglinks.addChildren(createNavigatorItems(connectedViews,
					outgoinglinks, true));
			if (!incominglinks.isEmpty()) {
				result.add(incominglinks);
			}
			if (!outgoinglinks.isEmpty()) {
				result.add(outgoinglinks);
			}
			return result.toArray();
		}

		case ImplementEditPart.VISUAL_ID: {
			LinkedList<ReflexactoringAbstractNavigatorItem> result = new LinkedList<ReflexactoringAbstractNavigatorItem>();
			Edge sv = (Edge) view;
			ReflexactoringNavigatorGroup target = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Implement_4005_target,
					"icons/linkTargetNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			ReflexactoringNavigatorGroup source = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Implement_4005_source,
					"icons/linkSourceNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			Collection<View> connectedViews;
			connectedViews = getLinksTargetByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(InterfaceEditPart.VISUAL_ID));
			target.addChildren(createNavigatorItems(connectedViews, target,
					true));
			connectedViews = getLinksTargetByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(Interface2EditPart.VISUAL_ID));
			target.addChildren(createNavigatorItems(connectedViews, target,
					true));
			connectedViews = getLinksSourceByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ClassEditPart.VISUAL_ID));
			source.addChildren(createNavigatorItems(connectedViews, source,
					true));
			connectedViews = getLinksSourceByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(Class2EditPart.VISUAL_ID));
			source.addChildren(createNavigatorItems(connectedViews, source,
					true));
			if (!target.isEmpty()) {
				result.add(target);
			}
			if (!source.isEmpty()) {
				result.add(source);
			}
			return result.toArray();
		}

		case Interface2EditPart.VISUAL_ID: {
			LinkedList<ReflexactoringAbstractNavigatorItem> result = new LinkedList<ReflexactoringAbstractNavigatorItem>();
			Node sv = (Node) view;
			ReflexactoringNavigatorGroup incominglinks = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Interface_3002_incominglinks,
					"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			ReflexactoringNavigatorGroup outgoinglinks = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Interface_3002_outgoinglinks,
					"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			Collection<View> connectedViews;
			connectedViews = getIncomingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(TypeDependencyEditPart.VISUAL_ID));
			incominglinks.addChildren(createNavigatorItems(connectedViews,
					incominglinks, true));
			connectedViews = getOutgoingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(TypeDependencyEditPart.VISUAL_ID));
			outgoinglinks.addChildren(createNavigatorItems(connectedViews,
					outgoinglinks, true));
			connectedViews = getIncomingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(InterfaceExtendEditPart.VISUAL_ID));
			incominglinks.addChildren(createNavigatorItems(connectedViews,
					incominglinks, true));
			connectedViews = getOutgoingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(InterfaceExtendEditPart.VISUAL_ID));
			outgoinglinks.addChildren(createNavigatorItems(connectedViews,
					outgoinglinks, true));
			connectedViews = getIncomingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ImplementEditPart.VISUAL_ID));
			incominglinks.addChildren(createNavigatorItems(connectedViews,
					incominglinks, true));
			if (!incominglinks.isEmpty()) {
				result.add(incominglinks);
			}
			if (!outgoinglinks.isEmpty()) {
				result.add(outgoinglinks);
			}
			return result.toArray();
		}

		case Class2EditPart.VISUAL_ID: {
			LinkedList<ReflexactoringAbstractNavigatorItem> result = new LinkedList<ReflexactoringAbstractNavigatorItem>();
			Node sv = (Node) view;
			ReflexactoringNavigatorGroup incominglinks = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Class_3001_incominglinks,
					"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			ReflexactoringNavigatorGroup outgoinglinks = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Class_3001_outgoinglinks,
					"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			Collection<View> connectedViews;
			connectedViews = getIncomingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ClassExtendEditPart.VISUAL_ID));
			incominglinks.addChildren(createNavigatorItems(connectedViews,
					incominglinks, true));
			connectedViews = getOutgoingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ClassExtendEditPart.VISUAL_ID));
			outgoinglinks.addChildren(createNavigatorItems(connectedViews,
					outgoinglinks, true));
			connectedViews = getIncomingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(TypeDependencyEditPart.VISUAL_ID));
			incominglinks.addChildren(createNavigatorItems(connectedViews,
					incominglinks, true));
			connectedViews = getOutgoingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(TypeDependencyEditPart.VISUAL_ID));
			outgoinglinks.addChildren(createNavigatorItems(connectedViews,
					outgoinglinks, true));
			connectedViews = getOutgoingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ImplementEditPart.VISUAL_ID));
			outgoinglinks.addChildren(createNavigatorItems(connectedViews,
					outgoinglinks, true));
			if (!incominglinks.isEmpty()) {
				result.add(incominglinks);
			}
			if (!outgoinglinks.isEmpty()) {
				result.add(outgoinglinks);
			}
			return result.toArray();
		}

		case ModuleExtendEditPart.VISUAL_ID: {
			LinkedList<ReflexactoringAbstractNavigatorItem> result = new LinkedList<ReflexactoringAbstractNavigatorItem>();
			Edge sv = (Edge) view;
			ReflexactoringNavigatorGroup target = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_ModuleExtend_4006_target,
					"icons/linkTargetNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			ReflexactoringNavigatorGroup source = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_ModuleExtend_4006_source,
					"icons/linkSourceNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			Collection<View> connectedViews;
			connectedViews = getLinksTargetByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleEditPart.VISUAL_ID));
			target.addChildren(createNavigatorItems(connectedViews, target,
					true));
			connectedViews = getLinksSourceByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleEditPart.VISUAL_ID));
			source.addChildren(createNavigatorItems(connectedViews, source,
					true));
			if (!target.isEmpty()) {
				result.add(target);
			}
			if (!source.isEmpty()) {
				result.add(source);
			}
			return result.toArray();
		}

		case ClassExtendEditPart.VISUAL_ID: {
			LinkedList<ReflexactoringAbstractNavigatorItem> result = new LinkedList<ReflexactoringAbstractNavigatorItem>();
			Edge sv = (Edge) view;
			ReflexactoringNavigatorGroup target = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_ClassExtend_4002_target,
					"icons/linkTargetNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			ReflexactoringNavigatorGroup source = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_ClassExtend_4002_source,
					"icons/linkSourceNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			Collection<View> connectedViews;
			connectedViews = getLinksTargetByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ClassEditPart.VISUAL_ID));
			target.addChildren(createNavigatorItems(connectedViews, target,
					true));
			connectedViews = getLinksTargetByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(Class2EditPart.VISUAL_ID));
			target.addChildren(createNavigatorItems(connectedViews, target,
					true));
			connectedViews = getLinksSourceByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ClassEditPart.VISUAL_ID));
			source.addChildren(createNavigatorItems(connectedViews, source,
					true));
			connectedViews = getLinksSourceByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(Class2EditPart.VISUAL_ID));
			source.addChildren(createNavigatorItems(connectedViews, source,
					true));
			if (!target.isEmpty()) {
				result.add(target);
			}
			if (!source.isEmpty()) {
				result.add(source);
			}
			return result.toArray();
		}

		case TypeDependencyEditPart.VISUAL_ID: {
			LinkedList<ReflexactoringAbstractNavigatorItem> result = new LinkedList<ReflexactoringAbstractNavigatorItem>();
			Edge sv = (Edge) view;
			ReflexactoringNavigatorGroup target = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_TypeDependency_4003_target,
					"icons/linkTargetNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			ReflexactoringNavigatorGroup source = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_TypeDependency_4003_source,
					"icons/linkSourceNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			Collection<View> connectedViews;
			connectedViews = getLinksTargetByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ClassEditPart.VISUAL_ID));
			target.addChildren(createNavigatorItems(connectedViews, target,
					true));
			connectedViews = getLinksTargetByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(InterfaceEditPart.VISUAL_ID));
			target.addChildren(createNavigatorItems(connectedViews, target,
					true));
			connectedViews = getLinksTargetByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(Class2EditPart.VISUAL_ID));
			target.addChildren(createNavigatorItems(connectedViews, target,
					true));
			connectedViews = getLinksTargetByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(Interface2EditPart.VISUAL_ID));
			target.addChildren(createNavigatorItems(connectedViews, target,
					true));
			connectedViews = getLinksSourceByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ClassEditPart.VISUAL_ID));
			source.addChildren(createNavigatorItems(connectedViews, source,
					true));
			connectedViews = getLinksSourceByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(InterfaceEditPart.VISUAL_ID));
			source.addChildren(createNavigatorItems(connectedViews, source,
					true));
			connectedViews = getLinksSourceByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(Class2EditPart.VISUAL_ID));
			source.addChildren(createNavigatorItems(connectedViews, source,
					true));
			connectedViews = getLinksSourceByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(Interface2EditPart.VISUAL_ID));
			source.addChildren(createNavigatorItems(connectedViews, source,
					true));
			if (!target.isEmpty()) {
				result.add(target);
			}
			if (!source.isEmpty()) {
				result.add(source);
			}
			return result.toArray();
		}

		case InterfaceEditPart.VISUAL_ID: {
			LinkedList<ReflexactoringAbstractNavigatorItem> result = new LinkedList<ReflexactoringAbstractNavigatorItem>();
			Node sv = (Node) view;
			ReflexactoringNavigatorGroup incominglinks = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Interface_2002_incominglinks,
					"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			ReflexactoringNavigatorGroup outgoinglinks = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_Interface_2002_outgoinglinks,
					"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			Collection<View> connectedViews;
			connectedViews = getIncomingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(TypeDependencyEditPart.VISUAL_ID));
			incominglinks.addChildren(createNavigatorItems(connectedViews,
					incominglinks, true));
			connectedViews = getOutgoingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(TypeDependencyEditPart.VISUAL_ID));
			outgoinglinks.addChildren(createNavigatorItems(connectedViews,
					outgoinglinks, true));
			connectedViews = getIncomingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(InterfaceExtendEditPart.VISUAL_ID));
			incominglinks.addChildren(createNavigatorItems(connectedViews,
					incominglinks, true));
			connectedViews = getOutgoingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(InterfaceExtendEditPart.VISUAL_ID));
			outgoinglinks.addChildren(createNavigatorItems(connectedViews,
					outgoinglinks, true));
			connectedViews = getIncomingLinksByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ImplementEditPart.VISUAL_ID));
			incominglinks.addChildren(createNavigatorItems(connectedViews,
					incominglinks, true));
			if (!incominglinks.isEmpty()) {
				result.add(incominglinks);
			}
			if (!outgoinglinks.isEmpty()) {
				result.add(outgoinglinks);
			}
			return result.toArray();
		}

		case ModuleDependencyEditPart.VISUAL_ID: {
			LinkedList<ReflexactoringAbstractNavigatorItem> result = new LinkedList<ReflexactoringAbstractNavigatorItem>();
			Edge sv = (Edge) view;
			ReflexactoringNavigatorGroup target = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_ModuleDependency_4001_target,
					"icons/linkTargetNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			ReflexactoringNavigatorGroup source = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_ModuleDependency_4001_source,
					"icons/linkSourceNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			Collection<View> connectedViews;
			connectedViews = getLinksTargetByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleEditPart.VISUAL_ID));
			target.addChildren(createNavigatorItems(connectedViews, target,
					true));
			connectedViews = getLinksSourceByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(ModuleEditPart.VISUAL_ID));
			source.addChildren(createNavigatorItems(connectedViews, source,
					true));
			if (!target.isEmpty()) {
				result.add(target);
			}
			if (!source.isEmpty()) {
				result.add(source);
			}
			return result.toArray();
		}

		case InterfaceExtendEditPart.VISUAL_ID: {
			LinkedList<ReflexactoringAbstractNavigatorItem> result = new LinkedList<ReflexactoringAbstractNavigatorItem>();
			Edge sv = (Edge) view;
			ReflexactoringNavigatorGroup target = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_InterfaceExtend_4004_target,
					"icons/linkTargetNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			ReflexactoringNavigatorGroup source = new ReflexactoringNavigatorGroup(
					Messages.NavigatorGroupName_InterfaceExtend_4004_source,
					"icons/linkSourceNavigatorGroup.gif", parentElement); //$NON-NLS-1$
			Collection<View> connectedViews;
			connectedViews = getLinksTargetByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(InterfaceEditPart.VISUAL_ID));
			target.addChildren(createNavigatorItems(connectedViews, target,
					true));
			connectedViews = getLinksTargetByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(Interface2EditPart.VISUAL_ID));
			target.addChildren(createNavigatorItems(connectedViews, target,
					true));
			connectedViews = getLinksSourceByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(InterfaceEditPart.VISUAL_ID));
			source.addChildren(createNavigatorItems(connectedViews, source,
					true));
			connectedViews = getLinksSourceByType(Collections.singleton(sv),
					ReflexactoringVisualIDRegistry
							.getType(Interface2EditPart.VISUAL_ID));
			source.addChildren(createNavigatorItems(connectedViews, source,
					true));
			if (!target.isEmpty()) {
				result.add(target);
			}
			if (!source.isEmpty()) {
				result.add(source);
			}
			return result.toArray();
		}
		}
		return EMPTY_ARRAY;
	}

	/**
	 * @generated
	 */
	private Collection<View> getLinksSourceByType(Collection<Edge> edges,
			String type) {
		LinkedList<View> result = new LinkedList<View>();
		for (Edge nextEdge : edges) {
			View nextEdgeSource = nextEdge.getSource();
			if (type.equals(nextEdgeSource.getType())
					&& isOwnView(nextEdgeSource)) {
				result.add(nextEdgeSource);
			}
		}
		return result;
	}

	/**
	 * @generated
	 */
	private Collection<View> getLinksTargetByType(Collection<Edge> edges,
			String type) {
		LinkedList<View> result = new LinkedList<View>();
		for (Edge nextEdge : edges) {
			View nextEdgeTarget = nextEdge.getTarget();
			if (type.equals(nextEdgeTarget.getType())
					&& isOwnView(nextEdgeTarget)) {
				result.add(nextEdgeTarget);
			}
		}
		return result;
	}

	/**
	 * @generated
	 */
	private Collection<View> getOutgoingLinksByType(
			Collection<? extends View> nodes, String type) {
		LinkedList<View> result = new LinkedList<View>();
		for (View nextNode : nodes) {
			result.addAll(selectViewsByType(nextNode.getSourceEdges(), type));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private Collection<View> getIncomingLinksByType(
			Collection<? extends View> nodes, String type) {
		LinkedList<View> result = new LinkedList<View>();
		for (View nextNode : nodes) {
			result.addAll(selectViewsByType(nextNode.getTargetEdges(), type));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private Collection<View> getChildrenByType(
			Collection<? extends View> nodes, String type) {
		LinkedList<View> result = new LinkedList<View>();
		for (View nextNode : nodes) {
			result.addAll(selectViewsByType(nextNode.getChildren(), type));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private Collection<View> getDiagramLinksByType(
			Collection<Diagram> diagrams, String type) {
		ArrayList<View> result = new ArrayList<View>();
		for (Diagram nextDiagram : diagrams) {
			result.addAll(selectViewsByType(nextDiagram.getEdges(), type));
		}
		return result;
	}

	// TODO refactor as static method
	/**
	 * @generated
	 */
	private Collection<View> selectViewsByType(Collection<View> views,
			String type) {
		ArrayList<View> result = new ArrayList<View>();
		for (View nextView : views) {
			if (type.equals(nextView.getType()) && isOwnView(nextView)) {
				result.add(nextView);
			}
		}
		return result;
	}

	/**
	 * @generated
	 */
	private boolean isOwnView(View view) {
		return ReflexactoringEditPart.MODEL_ID
				.equals(ReflexactoringVisualIDRegistry.getModelID(view));
	}

	/**
	 * @generated
	 */
	private Collection<ReflexactoringNavigatorItem> createNavigatorItems(
			Collection<View> views, Object parent, boolean isLeafs) {
		ArrayList<ReflexactoringNavigatorItem> result = new ArrayList<ReflexactoringNavigatorItem>(
				views.size());
		for (View nextView : views) {
			result.add(new ReflexactoringNavigatorItem(nextView, parent,
					isLeafs));
		}
		return result;
	}

	/**
	 * @generated
	 */
	public Object getParent(Object element) {
		if (element instanceof ReflexactoringAbstractNavigatorItem) {
			ReflexactoringAbstractNavigatorItem abstractNavigatorItem = (ReflexactoringAbstractNavigatorItem) element;
			return abstractNavigatorItem.getParent();
		}
		return null;
	}

	/**
	 * @generated
	 */
	public boolean hasChildren(Object element) {
		return element instanceof IFile || getChildren(element).length > 0;
	}

}
