/**
 */
package reflexactoring;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Module Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link reflexactoring.ModuleLink#getOrigin <em>Origin</em>}</li>
 *   <li>{@link reflexactoring.ModuleLink#getDestination <em>Destination</em>}</li>
 *   <li>{@link reflexactoring.ModuleLink#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see reflexactoring.ReflexactoringPackage#getModuleLink()
 * @model abstract="true"
 * @generated
 */
public interface ModuleLink extends EObject {
	/**
	 * Returns the value of the '<em><b>Origin</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Origin</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Origin</em>' reference.
	 * @see #setOrigin(Module)
	 * @see reflexactoring.ReflexactoringPackage#getModuleLink_Origin()
	 * @model required="true"
	 * @generated
	 */
	Module getOrigin();

	/**
	 * Sets the value of the '{@link reflexactoring.ModuleLink#getOrigin <em>Origin</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Origin</em>' reference.
	 * @see #getOrigin()
	 * @generated
	 */
	void setOrigin(Module value);

	/**
	 * Returns the value of the '<em><b>Destination</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Destination</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Destination</em>' reference.
	 * @see #setDestination(Module)
	 * @see reflexactoring.ReflexactoringPackage#getModuleLink_Destination()
	 * @model required="true"
	 * @generated
	 */
	Module getDestination();

	/**
	 * Sets the value of the '{@link reflexactoring.ModuleLink#getDestination <em>Destination</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Destination</em>' reference.
	 * @see #getDestination()
	 * @generated
	 */
	void setDestination(Module value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * The default value is <code>"origin"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see reflexactoring.ReflexactoringPackage#getModuleLink_Name()
	 * @model default="origin" required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link reflexactoring.ModuleLink#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // ModuleLink
