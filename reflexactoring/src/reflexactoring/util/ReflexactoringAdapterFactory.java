/**
 */
package reflexactoring.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import reflexactoring.ClassExtend;
import reflexactoring.Implement;
import reflexactoring.Interface;
import reflexactoring.InterfaceExtend;
import reflexactoring.Module;
import reflexactoring.ModuleCreation;
import reflexactoring.ModuleDependency;
import reflexactoring.ModuleExtend;
import reflexactoring.ModuleLink;
import reflexactoring.Reflexactoring;
import reflexactoring.ReflexactoringPackage;
import reflexactoring.Type;
import reflexactoring.TypeDependency;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see reflexactoring.ReflexactoringPackage
 * @generated
 */
public class ReflexactoringAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ReflexactoringPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReflexactoringAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = ReflexactoringPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReflexactoringSwitch<Adapter> modelSwitch =
		new ReflexactoringSwitch<Adapter>() {
			@Override
			public Adapter caseReflexactoring(Reflexactoring object) {
				return createReflexactoringAdapter();
			}
			@Override
			public Adapter caseModule(Module object) {
				return createModuleAdapter();
			}
			@Override
			public Adapter caseType(Type object) {
				return createTypeAdapter();
			}
			@Override
			public Adapter caseClass(reflexactoring.Class object) {
				return createClassAdapter();
			}
			@Override
			public Adapter caseInterface(Interface object) {
				return createInterfaceAdapter();
			}
			@Override
			public Adapter caseModuleLink(ModuleLink object) {
				return createModuleLinkAdapter();
			}
			@Override
			public Adapter caseTypeDependency(TypeDependency object) {
				return createTypeDependencyAdapter();
			}
			@Override
			public Adapter caseImplement(Implement object) {
				return createImplementAdapter();
			}
			@Override
			public Adapter caseClassExtend(ClassExtend object) {
				return createClassExtendAdapter();
			}
			@Override
			public Adapter caseInterfaceExtend(InterfaceExtend object) {
				return createInterfaceExtendAdapter();
			}
			@Override
			public Adapter caseModuleExtend(ModuleExtend object) {
				return createModuleExtendAdapter();
			}
			@Override
			public Adapter caseModuleDependency(ModuleDependency object) {
				return createModuleDependencyAdapter();
			}
			@Override
			public Adapter caseModuleCreation(ModuleCreation object) {
				return createModuleCreationAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.Reflexactoring <em>Reflexactoring</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.Reflexactoring
	 * @generated
	 */
	public Adapter createReflexactoringAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.Module <em>Module</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.Module
	 * @generated
	 */
	public Adapter createModuleAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.Type <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.Type
	 * @generated
	 */
	public Adapter createTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.Class <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.Class
	 * @generated
	 */
	public Adapter createClassAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.Interface <em>Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.Interface
	 * @generated
	 */
	public Adapter createInterfaceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.ModuleLink <em>Module Link</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.ModuleLink
	 * @generated
	 */
	public Adapter createModuleLinkAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.ModuleDependency <em>Module Dependency</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.ModuleDependency
	 * @generated
	 */
	public Adapter createModuleDependencyAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.ModuleCreation <em>Module Creation</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.ModuleCreation
	 * @generated
	 */
	public Adapter createModuleCreationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.TypeDependency <em>Type Dependency</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.TypeDependency
	 * @generated
	 */
	public Adapter createTypeDependencyAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.Implement <em>Implement</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.Implement
	 * @generated
	 */
	public Adapter createImplementAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.ClassExtend <em>Class Extend</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.ClassExtend
	 * @generated
	 */
	public Adapter createClassExtendAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.InterfaceExtend <em>Interface Extend</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.InterfaceExtend
	 * @generated
	 */
	public Adapter createInterfaceExtendAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link reflexactoring.ModuleExtend <em>Module Extend</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see reflexactoring.ModuleExtend
	 * @generated
	 */
	public Adapter createModuleExtendAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //ReflexactoringAdapterFactory
