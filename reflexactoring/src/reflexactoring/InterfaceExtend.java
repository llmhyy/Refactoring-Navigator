/**
 */
package reflexactoring;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Interface Extend</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link reflexactoring.InterfaceExtend#getName <em>Name</em>}</li>
 *   <li>{@link reflexactoring.InterfaceExtend#getSubInterface <em>Sub Interface</em>}</li>
 *   <li>{@link reflexactoring.InterfaceExtend#getSuperInterface <em>Super Interface</em>}</li>
 * </ul>
 * </p>
 *
 * @see reflexactoring.ReflexactoringPackage#getInterfaceExtend()
 * @model
 * @generated
 */
public interface InterfaceExtend extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see reflexactoring.ReflexactoringPackage#getInterfaceExtend_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link reflexactoring.InterfaceExtend#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Sub Interface</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sub Interface</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sub Interface</em>' reference.
	 * @see #setSubInterface(Interface)
	 * @see reflexactoring.ReflexactoringPackage#getInterfaceExtend_SubInterface()
	 * @model required="true"
	 * @generated
	 */
	Interface getSubInterface();

	/**
	 * Sets the value of the '{@link reflexactoring.InterfaceExtend#getSubInterface <em>Sub Interface</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sub Interface</em>' reference.
	 * @see #getSubInterface()
	 * @generated
	 */
	void setSubInterface(Interface value);

	/**
	 * Returns the value of the '<em><b>Super Interface</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Super Interface</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Super Interface</em>' reference.
	 * @see #setSuperInterface(Interface)
	 * @see reflexactoring.ReflexactoringPackage#getInterfaceExtend_SuperInterface()
	 * @model required="true"
	 * @generated
	 */
	Interface getSuperInterface();

	/**
	 * Sets the value of the '{@link reflexactoring.InterfaceExtend#getSuperInterface <em>Super Interface</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Super Interface</em>' reference.
	 * @see #getSuperInterface()
	 * @generated
	 */
	void setSuperInterface(Interface value);

} // InterfaceExtend
