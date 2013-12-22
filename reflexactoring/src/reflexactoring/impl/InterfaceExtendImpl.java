/**
 */
package reflexactoring.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import reflexactoring.Interface;
import reflexactoring.InterfaceExtend;
import reflexactoring.ReflexactoringPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Interface Extend</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link reflexactoring.impl.InterfaceExtendImpl#getName <em>Name</em>}</li>
 *   <li>{@link reflexactoring.impl.InterfaceExtendImpl#getSubInterface <em>Sub Interface</em>}</li>
 *   <li>{@link reflexactoring.impl.InterfaceExtendImpl#getSuperInterface <em>Super Interface</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InterfaceExtendImpl extends MinimalEObjectImpl.Container implements InterfaceExtend {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSubInterface() <em>Sub Interface</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubInterface()
	 * @generated
	 * @ordered
	 */
	protected Interface subInterface;

	/**
	 * The cached value of the '{@link #getSuperInterface() <em>Super Interface</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuperInterface()
	 * @generated
	 * @ordered
	 */
	protected Interface superInterface;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InterfaceExtendImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReflexactoringPackage.Literals.INTERFACE_EXTEND;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReflexactoringPackage.INTERFACE_EXTEND__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Interface getSubInterface() {
		if (subInterface != null && subInterface.eIsProxy()) {
			InternalEObject oldSubInterface = (InternalEObject)subInterface;
			subInterface = (Interface)eResolveProxy(oldSubInterface);
			if (subInterface != oldSubInterface) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReflexactoringPackage.INTERFACE_EXTEND__SUB_INTERFACE, oldSubInterface, subInterface));
			}
		}
		return subInterface;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Interface basicGetSubInterface() {
		return subInterface;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSubInterface(Interface newSubInterface) {
		Interface oldSubInterface = subInterface;
		subInterface = newSubInterface;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReflexactoringPackage.INTERFACE_EXTEND__SUB_INTERFACE, oldSubInterface, subInterface));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Interface getSuperInterface() {
		if (superInterface != null && superInterface.eIsProxy()) {
			InternalEObject oldSuperInterface = (InternalEObject)superInterface;
			superInterface = (Interface)eResolveProxy(oldSuperInterface);
			if (superInterface != oldSuperInterface) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReflexactoringPackage.INTERFACE_EXTEND__SUPER_INTERFACE, oldSuperInterface, superInterface));
			}
		}
		return superInterface;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Interface basicGetSuperInterface() {
		return superInterface;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSuperInterface(Interface newSuperInterface) {
		Interface oldSuperInterface = superInterface;
		superInterface = newSuperInterface;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReflexactoringPackage.INTERFACE_EXTEND__SUPER_INTERFACE, oldSuperInterface, superInterface));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ReflexactoringPackage.INTERFACE_EXTEND__NAME:
				return getName();
			case ReflexactoringPackage.INTERFACE_EXTEND__SUB_INTERFACE:
				if (resolve) return getSubInterface();
				return basicGetSubInterface();
			case ReflexactoringPackage.INTERFACE_EXTEND__SUPER_INTERFACE:
				if (resolve) return getSuperInterface();
				return basicGetSuperInterface();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ReflexactoringPackage.INTERFACE_EXTEND__NAME:
				setName((String)newValue);
				return;
			case ReflexactoringPackage.INTERFACE_EXTEND__SUB_INTERFACE:
				setSubInterface((Interface)newValue);
				return;
			case ReflexactoringPackage.INTERFACE_EXTEND__SUPER_INTERFACE:
				setSuperInterface((Interface)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ReflexactoringPackage.INTERFACE_EXTEND__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ReflexactoringPackage.INTERFACE_EXTEND__SUB_INTERFACE:
				setSubInterface((Interface)null);
				return;
			case ReflexactoringPackage.INTERFACE_EXTEND__SUPER_INTERFACE:
				setSuperInterface((Interface)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ReflexactoringPackage.INTERFACE_EXTEND__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ReflexactoringPackage.INTERFACE_EXTEND__SUB_INTERFACE:
				return subInterface != null;
			case ReflexactoringPackage.INTERFACE_EXTEND__SUPER_INTERFACE:
				return superInterface != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //InterfaceExtendImpl
