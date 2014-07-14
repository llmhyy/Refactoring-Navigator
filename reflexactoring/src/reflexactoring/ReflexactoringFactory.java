/**
 */
package reflexactoring;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see reflexactoring.ReflexactoringPackage
 * @generated
 */
public interface ReflexactoringFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ReflexactoringFactory eINSTANCE = reflexactoring.impl.ReflexactoringFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Reflexactoring</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Reflexactoring</em>'.
	 * @generated
	 */
	Reflexactoring createReflexactoring();

	/**
	 * Returns a new object of class '<em>Module</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Module</em>'.
	 * @generated
	 */
	Module createModule();

	/**
	 * Returns a new object of class '<em>Class</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Class</em>'.
	 * @generated
	 */
	Class createClass();

	/**
	 * Returns a new object of class '<em>Interface</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Interface</em>'.
	 * @generated
	 */
	Interface createInterface();

	/**
	 * Returns a new object of class '<em>Module Dependency</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Module Dependency</em>'.
	 * @generated
	 */
	ModuleDependency createModuleDependency();

	/**
	 * Returns a new object of class '<em>Type Dependency</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Type Dependency</em>'.
	 * @generated
	 */
	TypeDependency createTypeDependency();

	/**
	 * Returns a new object of class '<em>Implement</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Implement</em>'.
	 * @generated
	 */
	Implement createImplement();

	/**
	 * Returns a new object of class '<em>Class Extend</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Class Extend</em>'.
	 * @generated
	 */
	ClassExtend createClassExtend();

	/**
	 * Returns a new object of class '<em>Interface Extend</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Interface Extend</em>'.
	 * @generated
	 */
	InterfaceExtend createInterfaceExtend();

	/**
	 * Returns a new object of class '<em>Module Extend</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Module Extend</em>'.
	 * @generated
	 */
	ModuleExtend createModuleExtend();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ReflexactoringPackage getReflexactoringPackage();

} //ReflexactoringFactory
