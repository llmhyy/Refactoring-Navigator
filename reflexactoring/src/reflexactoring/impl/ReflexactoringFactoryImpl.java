/**
 */
package reflexactoring.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import reflexactoring.ClassExtend;
import reflexactoring.Implement;
import reflexactoring.Interface;
import reflexactoring.InterfaceExtend;
import reflexactoring.Module;
import reflexactoring.ModuleDependency;
import reflexactoring.Reflexactoring;
import reflexactoring.ReflexactoringFactory;
import reflexactoring.ReflexactoringPackage;
import reflexactoring.TypeDependency;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ReflexactoringFactoryImpl extends EFactoryImpl implements ReflexactoringFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ReflexactoringFactory init() {
		try {
			ReflexactoringFactory theReflexactoringFactory = (ReflexactoringFactory)EPackage.Registry.INSTANCE.getEFactory(ReflexactoringPackage.eNS_URI);
			if (theReflexactoringFactory != null) {
				return theReflexactoringFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ReflexactoringFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReflexactoringFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case ReflexactoringPackage.REFLEXACTORING: return createReflexactoring();
			case ReflexactoringPackage.MODULE: return createModule();
			case ReflexactoringPackage.CLASS: return createClass();
			case ReflexactoringPackage.INTERFACE: return createInterface();
			case ReflexactoringPackage.MODULE_DEPENDENCY: return createModuleDependency();
			case ReflexactoringPackage.TYPE_DEPENDENCY: return createTypeDependency();
			case ReflexactoringPackage.IMPLEMENT: return createImplement();
			case ReflexactoringPackage.CLASS_EXTEND: return createClassExtend();
			case ReflexactoringPackage.INTERFACE_EXTEND: return createInterfaceExtend();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Reflexactoring createReflexactoring() {
		ReflexactoringImpl reflexactoring = new ReflexactoringImpl();
		return reflexactoring;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Module createModule() {
		ModuleImpl module = new ModuleImpl();
		return module;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public reflexactoring.Class createClass() {
		ClassImpl class_ = new ClassImpl();
		return class_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Interface createInterface() {
		InterfaceImpl interface_ = new InterfaceImpl();
		return interface_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModuleDependency createModuleDependency() {
		ModuleDependencyImpl moduleDependency = new ModuleDependencyImpl();
		return moduleDependency;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TypeDependency createTypeDependency() {
		TypeDependencyImpl typeDependency = new TypeDependencyImpl();
		return typeDependency;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Implement createImplement() {
		ImplementImpl implement = new ImplementImpl();
		return implement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ClassExtend createClassExtend() {
		ClassExtendImpl classExtend = new ClassExtendImpl();
		return classExtend;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InterfaceExtend createInterfaceExtend() {
		InterfaceExtendImpl interfaceExtend = new InterfaceExtendImpl();
		return interfaceExtend;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReflexactoringPackage getReflexactoringPackage() {
		return (ReflexactoringPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static ReflexactoringPackage getPackage() {
		return ReflexactoringPackage.eINSTANCE;
	}

} //ReflexactoringFactoryImpl
