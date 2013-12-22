/**
 */
package reflexactoring.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import reflexactoring.ClassExtend;
import reflexactoring.Implement;
import reflexactoring.InterfaceExtend;
import reflexactoring.Module;
import reflexactoring.ModuleDependency;
import reflexactoring.Reflexactoring;
import reflexactoring.ReflexactoringPackage;
import reflexactoring.Type;
import reflexactoring.TypeDependency;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Reflexactoring</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link reflexactoring.impl.ReflexactoringImpl#getModules <em>Modules</em>}</li>
 *   <li>{@link reflexactoring.impl.ReflexactoringImpl#getTypes <em>Types</em>}</li>
 *   <li>{@link reflexactoring.impl.ReflexactoringImpl#getModuleDenpencies <em>Module Denpencies</em>}</li>
 *   <li>{@link reflexactoring.impl.ReflexactoringImpl#getImplements <em>Implements</em>}</li>
 *   <li>{@link reflexactoring.impl.ReflexactoringImpl#getTypeDependencies <em>Type Dependencies</em>}</li>
 *   <li>{@link reflexactoring.impl.ReflexactoringImpl#getClassExtends <em>Class Extends</em>}</li>
 *   <li>{@link reflexactoring.impl.ReflexactoringImpl#getInterfaceExtends <em>Interface Extends</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ReflexactoringImpl extends MinimalEObjectImpl.Container implements Reflexactoring {
	/**
	 * The cached value of the '{@link #getModules() <em>Modules</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModules()
	 * @generated
	 * @ordered
	 */
	protected EList<Module> modules;

	/**
	 * The cached value of the '{@link #getTypes() <em>Types</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypes()
	 * @generated
	 * @ordered
	 */
	protected EList<Type> types;

	/**
	 * The cached value of the '{@link #getModuleDenpencies() <em>Module Denpencies</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModuleDenpencies()
	 * @generated
	 * @ordered
	 */
	protected EList<ModuleDependency> moduleDenpencies;

	/**
	 * The cached value of the '{@link #getImplements() <em>Implements</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplements()
	 * @generated
	 * @ordered
	 */
	protected EList<Implement> implements_;

	/**
	 * The cached value of the '{@link #getTypeDependencies() <em>Type Dependencies</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTypeDependencies()
	 * @generated
	 * @ordered
	 */
	protected EList<TypeDependency> typeDependencies;

	/**
	 * The cached value of the '{@link #getClassExtends() <em>Class Extends</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClassExtends()
	 * @generated
	 * @ordered
	 */
	protected EList<ClassExtend> classExtends;

	/**
	 * The cached value of the '{@link #getInterfaceExtends() <em>Interface Extends</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterfaceExtends()
	 * @generated
	 * @ordered
	 */
	protected EList<InterfaceExtend> interfaceExtends;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReflexactoringImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReflexactoringPackage.Literals.REFLEXACTORING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Module> getModules() {
		if (modules == null) {
			modules = new EObjectContainmentEList<Module>(Module.class, this, ReflexactoringPackage.REFLEXACTORING__MODULES);
		}
		return modules;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Type> getTypes() {
		if (types == null) {
			types = new EObjectContainmentEList<Type>(Type.class, this, ReflexactoringPackage.REFLEXACTORING__TYPES);
		}
		return types;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ModuleDependency> getModuleDenpencies() {
		if (moduleDenpencies == null) {
			moduleDenpencies = new EObjectContainmentEList<ModuleDependency>(ModuleDependency.class, this, ReflexactoringPackage.REFLEXACTORING__MODULE_DENPENCIES);
		}
		return moduleDenpencies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Implement> getImplements() {
		if (implements_ == null) {
			implements_ = new EObjectContainmentEList<Implement>(Implement.class, this, ReflexactoringPackage.REFLEXACTORING__IMPLEMENTS);
		}
		return implements_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<TypeDependency> getTypeDependencies() {
		if (typeDependencies == null) {
			typeDependencies = new EObjectContainmentEList<TypeDependency>(TypeDependency.class, this, ReflexactoringPackage.REFLEXACTORING__TYPE_DEPENDENCIES);
		}
		return typeDependencies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ClassExtend> getClassExtends() {
		if (classExtends == null) {
			classExtends = new EObjectContainmentEList<ClassExtend>(ClassExtend.class, this, ReflexactoringPackage.REFLEXACTORING__CLASS_EXTENDS);
		}
		return classExtends;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<InterfaceExtend> getInterfaceExtends() {
		if (interfaceExtends == null) {
			interfaceExtends = new EObjectContainmentEList<InterfaceExtend>(InterfaceExtend.class, this, ReflexactoringPackage.REFLEXACTORING__INTERFACE_EXTENDS);
		}
		return interfaceExtends;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ReflexactoringPackage.REFLEXACTORING__MODULES:
				return ((InternalEList<?>)getModules()).basicRemove(otherEnd, msgs);
			case ReflexactoringPackage.REFLEXACTORING__TYPES:
				return ((InternalEList<?>)getTypes()).basicRemove(otherEnd, msgs);
			case ReflexactoringPackage.REFLEXACTORING__MODULE_DENPENCIES:
				return ((InternalEList<?>)getModuleDenpencies()).basicRemove(otherEnd, msgs);
			case ReflexactoringPackage.REFLEXACTORING__IMPLEMENTS:
				return ((InternalEList<?>)getImplements()).basicRemove(otherEnd, msgs);
			case ReflexactoringPackage.REFLEXACTORING__TYPE_DEPENDENCIES:
				return ((InternalEList<?>)getTypeDependencies()).basicRemove(otherEnd, msgs);
			case ReflexactoringPackage.REFLEXACTORING__CLASS_EXTENDS:
				return ((InternalEList<?>)getClassExtends()).basicRemove(otherEnd, msgs);
			case ReflexactoringPackage.REFLEXACTORING__INTERFACE_EXTENDS:
				return ((InternalEList<?>)getInterfaceExtends()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ReflexactoringPackage.REFLEXACTORING__MODULES:
				return getModules();
			case ReflexactoringPackage.REFLEXACTORING__TYPES:
				return getTypes();
			case ReflexactoringPackage.REFLEXACTORING__MODULE_DENPENCIES:
				return getModuleDenpencies();
			case ReflexactoringPackage.REFLEXACTORING__IMPLEMENTS:
				return getImplements();
			case ReflexactoringPackage.REFLEXACTORING__TYPE_DEPENDENCIES:
				return getTypeDependencies();
			case ReflexactoringPackage.REFLEXACTORING__CLASS_EXTENDS:
				return getClassExtends();
			case ReflexactoringPackage.REFLEXACTORING__INTERFACE_EXTENDS:
				return getInterfaceExtends();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ReflexactoringPackage.REFLEXACTORING__MODULES:
				getModules().clear();
				getModules().addAll((Collection<? extends Module>)newValue);
				return;
			case ReflexactoringPackage.REFLEXACTORING__TYPES:
				getTypes().clear();
				getTypes().addAll((Collection<? extends Type>)newValue);
				return;
			case ReflexactoringPackage.REFLEXACTORING__MODULE_DENPENCIES:
				getModuleDenpencies().clear();
				getModuleDenpencies().addAll((Collection<? extends ModuleDependency>)newValue);
				return;
			case ReflexactoringPackage.REFLEXACTORING__IMPLEMENTS:
				getImplements().clear();
				getImplements().addAll((Collection<? extends Implement>)newValue);
				return;
			case ReflexactoringPackage.REFLEXACTORING__TYPE_DEPENDENCIES:
				getTypeDependencies().clear();
				getTypeDependencies().addAll((Collection<? extends TypeDependency>)newValue);
				return;
			case ReflexactoringPackage.REFLEXACTORING__CLASS_EXTENDS:
				getClassExtends().clear();
				getClassExtends().addAll((Collection<? extends ClassExtend>)newValue);
				return;
			case ReflexactoringPackage.REFLEXACTORING__INTERFACE_EXTENDS:
				getInterfaceExtends().clear();
				getInterfaceExtends().addAll((Collection<? extends InterfaceExtend>)newValue);
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
			case ReflexactoringPackage.REFLEXACTORING__MODULES:
				getModules().clear();
				return;
			case ReflexactoringPackage.REFLEXACTORING__TYPES:
				getTypes().clear();
				return;
			case ReflexactoringPackage.REFLEXACTORING__MODULE_DENPENCIES:
				getModuleDenpencies().clear();
				return;
			case ReflexactoringPackage.REFLEXACTORING__IMPLEMENTS:
				getImplements().clear();
				return;
			case ReflexactoringPackage.REFLEXACTORING__TYPE_DEPENDENCIES:
				getTypeDependencies().clear();
				return;
			case ReflexactoringPackage.REFLEXACTORING__CLASS_EXTENDS:
				getClassExtends().clear();
				return;
			case ReflexactoringPackage.REFLEXACTORING__INTERFACE_EXTENDS:
				getInterfaceExtends().clear();
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
			case ReflexactoringPackage.REFLEXACTORING__MODULES:
				return modules != null && !modules.isEmpty();
			case ReflexactoringPackage.REFLEXACTORING__TYPES:
				return types != null && !types.isEmpty();
			case ReflexactoringPackage.REFLEXACTORING__MODULE_DENPENCIES:
				return moduleDenpencies != null && !moduleDenpencies.isEmpty();
			case ReflexactoringPackage.REFLEXACTORING__IMPLEMENTS:
				return implements_ != null && !implements_.isEmpty();
			case ReflexactoringPackage.REFLEXACTORING__TYPE_DEPENDENCIES:
				return typeDependencies != null && !typeDependencies.isEmpty();
			case ReflexactoringPackage.REFLEXACTORING__CLASS_EXTENDS:
				return classExtends != null && !classExtends.isEmpty();
			case ReflexactoringPackage.REFLEXACTORING__INTERFACE_EXTENDS:
				return interfaceExtends != null && !interfaceExtends.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ReflexactoringImpl
