/**
 */
package reflexactoring;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Reflexactoring</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link reflexactoring.Reflexactoring#getModules <em>Modules</em>}</li>
 *   <li>{@link reflexactoring.Reflexactoring#getTypes <em>Types</em>}</li>
 *   <li>{@link reflexactoring.Reflexactoring#getModuleDenpencies <em>Module Denpencies</em>}</li>
 *   <li>{@link reflexactoring.Reflexactoring#getImplements <em>Implements</em>}</li>
 *   <li>{@link reflexactoring.Reflexactoring#getTypeDependencies <em>Type Dependencies</em>}</li>
 *   <li>{@link reflexactoring.Reflexactoring#getClassExtends <em>Class Extends</em>}</li>
 *   <li>{@link reflexactoring.Reflexactoring#getInterfaceExtends <em>Interface Extends</em>}</li>
 * </ul>
 * </p>
 *
 * @see reflexactoring.ReflexactoringPackage#getReflexactoring()
 * @model
 * @generated
 */
public interface Reflexactoring extends EObject {
	/**
	 * Returns the value of the '<em><b>Modules</b></em>' containment reference list.
	 * The list contents are of type {@link reflexactoring.Module}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Modules</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Modules</em>' containment reference list.
	 * @see reflexactoring.ReflexactoringPackage#getReflexactoring_Modules()
	 * @model containment="true"
	 * @generated
	 */
	EList<Module> getModules();

	/**
	 * Returns the value of the '<em><b>Types</b></em>' containment reference list.
	 * The list contents are of type {@link reflexactoring.Type}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Types</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Types</em>' containment reference list.
	 * @see reflexactoring.ReflexactoringPackage#getReflexactoring_Types()
	 * @model containment="true"
	 * @generated
	 */
	EList<Type> getTypes();

	/**
	 * Returns the value of the '<em><b>Module Denpencies</b></em>' containment reference list.
	 * The list contents are of type {@link reflexactoring.ModuleDependency}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Module Denpencies</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Module Denpencies</em>' containment reference list.
	 * @see reflexactoring.ReflexactoringPackage#getReflexactoring_ModuleDenpencies()
	 * @model containment="true"
	 * @generated
	 */
	EList<ModuleDependency> getModuleDenpencies();

	/**
	 * Returns the value of the '<em><b>Implements</b></em>' containment reference list.
	 * The list contents are of type {@link reflexactoring.Implement}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Implements</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Implements</em>' containment reference list.
	 * @see reflexactoring.ReflexactoringPackage#getReflexactoring_Implements()
	 * @model containment="true"
	 * @generated
	 */
	EList<Implement> getImplements();

	/**
	 * Returns the value of the '<em><b>Type Dependencies</b></em>' containment reference list.
	 * The list contents are of type {@link reflexactoring.TypeDependency}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type Dependencies</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type Dependencies</em>' containment reference list.
	 * @see reflexactoring.ReflexactoringPackage#getReflexactoring_TypeDependencies()
	 * @model containment="true"
	 * @generated
	 */
	EList<TypeDependency> getTypeDependencies();

	/**
	 * Returns the value of the '<em><b>Class Extends</b></em>' containment reference list.
	 * The list contents are of type {@link reflexactoring.ClassExtend}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Class Extends</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Class Extends</em>' containment reference list.
	 * @see reflexactoring.ReflexactoringPackage#getReflexactoring_ClassExtends()
	 * @model containment="true"
	 * @generated
	 */
	EList<ClassExtend> getClassExtends();

	/**
	 * Returns the value of the '<em><b>Interface Extends</b></em>' containment reference list.
	 * The list contents are of type {@link reflexactoring.InterfaceExtend}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Interface Extends</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Interface Extends</em>' containment reference list.
	 * @see reflexactoring.ReflexactoringPackage#getReflexactoring_InterfaceExtends()
	 * @model containment="true"
	 * @generated
	 */
	EList<InterfaceExtend> getInterfaceExtends();

} // Reflexactoring
