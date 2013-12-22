/**
 */
package reflexactoring.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import reflexactoring.Reflexactoring;
import reflexactoring.ReflexactoringFactory;
import reflexactoring.ReflexactoringPackage;

/**
 * This is the item provider adapter for a {@link reflexactoring.Reflexactoring} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ReflexactoringItemProvider
	extends ItemProviderAdapter
	implements
		IEditingDomainItemProvider,
		IStructuredItemContentProvider,
		ITreeItemContentProvider,
		IItemLabelProvider,
		IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReflexactoringItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

		}
		return itemPropertyDescriptors;
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(ReflexactoringPackage.Literals.REFLEXACTORING__MODULES);
			childrenFeatures.add(ReflexactoringPackage.Literals.REFLEXACTORING__TYPES);
			childrenFeatures.add(ReflexactoringPackage.Literals.REFLEXACTORING__MODULE_DENPENCIES);
			childrenFeatures.add(ReflexactoringPackage.Literals.REFLEXACTORING__IMPLEMENTS);
			childrenFeatures.add(ReflexactoringPackage.Literals.REFLEXACTORING__TYPE_DEPENDENCIES);
			childrenFeatures.add(ReflexactoringPackage.Literals.REFLEXACTORING__CLASS_EXTENDS);
			childrenFeatures.add(ReflexactoringPackage.Literals.REFLEXACTORING__INTERFACE_EXTENDS);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns Reflexactoring.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/Reflexactoring"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		return getString("_UI_Reflexactoring_type");
	}
	

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(Reflexactoring.class)) {
			case ReflexactoringPackage.REFLEXACTORING__MODULES:
			case ReflexactoringPackage.REFLEXACTORING__TYPES:
			case ReflexactoringPackage.REFLEXACTORING__MODULE_DENPENCIES:
			case ReflexactoringPackage.REFLEXACTORING__IMPLEMENTS:
			case ReflexactoringPackage.REFLEXACTORING__TYPE_DEPENDENCIES:
			case ReflexactoringPackage.REFLEXACTORING__CLASS_EXTENDS:
			case ReflexactoringPackage.REFLEXACTORING__INTERFACE_EXTENDS:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(ReflexactoringPackage.Literals.REFLEXACTORING__MODULES,
				 ReflexactoringFactory.eINSTANCE.createModule()));

		newChildDescriptors.add
			(createChildParameter
				(ReflexactoringPackage.Literals.REFLEXACTORING__TYPES,
				 ReflexactoringFactory.eINSTANCE.createClass()));

		newChildDescriptors.add
			(createChildParameter
				(ReflexactoringPackage.Literals.REFLEXACTORING__TYPES,
				 ReflexactoringFactory.eINSTANCE.createInterface()));

		newChildDescriptors.add
			(createChildParameter
				(ReflexactoringPackage.Literals.REFLEXACTORING__MODULE_DENPENCIES,
				 ReflexactoringFactory.eINSTANCE.createModuleDependency()));

		newChildDescriptors.add
			(createChildParameter
				(ReflexactoringPackage.Literals.REFLEXACTORING__IMPLEMENTS,
				 ReflexactoringFactory.eINSTANCE.createImplement()));

		newChildDescriptors.add
			(createChildParameter
				(ReflexactoringPackage.Literals.REFLEXACTORING__TYPE_DEPENDENCIES,
				 ReflexactoringFactory.eINSTANCE.createTypeDependency()));

		newChildDescriptors.add
			(createChildParameter
				(ReflexactoringPackage.Literals.REFLEXACTORING__CLASS_EXTENDS,
				 ReflexactoringFactory.eINSTANCE.createClassExtend()));

		newChildDescriptors.add
			(createChildParameter
				(ReflexactoringPackage.Literals.REFLEXACTORING__INTERFACE_EXTENDS,
				 ReflexactoringFactory.eINSTANCE.createInterfaceExtend()));
	}

	/**
	 * Return the resource locator for this item provider's resources.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return ReflexactoringEditPlugin.INSTANCE;
	}

}
