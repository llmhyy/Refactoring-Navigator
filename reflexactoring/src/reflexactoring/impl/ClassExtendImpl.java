/**
 */
package reflexactoring.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import reflexactoring.ClassExtend;
import reflexactoring.ReflexactoringPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Class Extend</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link reflexactoring.impl.ClassExtendImpl#getSubClass <em>Sub Class</em>}</li>
 *   <li>{@link reflexactoring.impl.ClassExtendImpl#getSuperClass <em>Super Class</em>}</li>
 *   <li>{@link reflexactoring.impl.ClassExtendImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ClassExtendImpl extends MinimalEObjectImpl.Container implements ClassExtend {
	/**
	 * The cached value of the '{@link #getSubClass() <em>Sub Class</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubClass()
	 * @generated
	 * @ordered
	 */
	protected reflexactoring.Class subClass;

	/**
	 * The cached value of the '{@link #getSuperClass() <em>Super Class</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuperClass()
	 * @generated
	 * @ordered
	 */
	protected reflexactoring.Class superClass;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ClassExtendImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReflexactoringPackage.Literals.CLASS_EXTEND;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public reflexactoring.Class getSubClass() {
		if (subClass != null && subClass.eIsProxy()) {
			InternalEObject oldSubClass = (InternalEObject)subClass;
			subClass = (reflexactoring.Class)eResolveProxy(oldSubClass);
			if (subClass != oldSubClass) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReflexactoringPackage.CLASS_EXTEND__SUB_CLASS, oldSubClass, subClass));
			}
		}
		return subClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public reflexactoring.Class basicGetSubClass() {
		return subClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSubClass(reflexactoring.Class newSubClass) {
		reflexactoring.Class oldSubClass = subClass;
		subClass = newSubClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReflexactoringPackage.CLASS_EXTEND__SUB_CLASS, oldSubClass, subClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public reflexactoring.Class getSuperClass() {
		if (superClass != null && superClass.eIsProxy()) {
			InternalEObject oldSuperClass = (InternalEObject)superClass;
			superClass = (reflexactoring.Class)eResolveProxy(oldSuperClass);
			if (superClass != oldSuperClass) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ReflexactoringPackage.CLASS_EXTEND__SUPER_CLASS, oldSuperClass, superClass));
			}
		}
		return superClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public reflexactoring.Class basicGetSuperClass() {
		return superClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSuperClass(reflexactoring.Class newSuperClass) {
		reflexactoring.Class oldSuperClass = superClass;
		superClass = newSuperClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ReflexactoringPackage.CLASS_EXTEND__SUPER_CLASS, oldSuperClass, superClass));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ReflexactoringPackage.CLASS_EXTEND__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ReflexactoringPackage.CLASS_EXTEND__SUB_CLASS:
				if (resolve) return getSubClass();
				return basicGetSubClass();
			case ReflexactoringPackage.CLASS_EXTEND__SUPER_CLASS:
				if (resolve) return getSuperClass();
				return basicGetSuperClass();
			case ReflexactoringPackage.CLASS_EXTEND__NAME:
				return getName();
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
			case ReflexactoringPackage.CLASS_EXTEND__SUB_CLASS:
				setSubClass((reflexactoring.Class)newValue);
				return;
			case ReflexactoringPackage.CLASS_EXTEND__SUPER_CLASS:
				setSuperClass((reflexactoring.Class)newValue);
				return;
			case ReflexactoringPackage.CLASS_EXTEND__NAME:
				setName((String)newValue);
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
			case ReflexactoringPackage.CLASS_EXTEND__SUB_CLASS:
				setSubClass((reflexactoring.Class)null);
				return;
			case ReflexactoringPackage.CLASS_EXTEND__SUPER_CLASS:
				setSuperClass((reflexactoring.Class)null);
				return;
			case ReflexactoringPackage.CLASS_EXTEND__NAME:
				setName(NAME_EDEFAULT);
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
			case ReflexactoringPackage.CLASS_EXTEND__SUB_CLASS:
				return subClass != null;
			case ReflexactoringPackage.CLASS_EXTEND__SUPER_CLASS:
				return superClass != null;
			case ReflexactoringPackage.CLASS_EXTEND__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
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

} //ClassExtendImpl
