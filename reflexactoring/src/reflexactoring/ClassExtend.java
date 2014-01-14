/**
 */
package reflexactoring;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Class Extend</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link reflexactoring.ClassExtend#getSubClass <em>Sub Class</em>}</li>
 *   <li>{@link reflexactoring.ClassExtend#getSuperClass <em>Super Class</em>}</li>
 *   <li>{@link reflexactoring.ClassExtend#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see reflexactoring.ReflexactoringPackage#getClassExtend()
 * @model
 * @generated
 */
public interface ClassExtend extends EObject {
	/**
	 * Returns the value of the '<em><b>Sub Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sub Class</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sub Class</em>' reference.
	 * @see #setSubClass(reflexactoring.Class)
	 * @see reflexactoring.ReflexactoringPackage#getClassExtend_SubClass()
	 * @model required="true"
	 * @generated
	 */
	reflexactoring.Class getSubClass();

	/**
	 * Sets the value of the '{@link reflexactoring.ClassExtend#getSubClass <em>Sub Class</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sub Class</em>' reference.
	 * @see #getSubClass()
	 * @generated
	 */
	void setSubClass(reflexactoring.Class value);

	/**
	 * Returns the value of the '<em><b>Super Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Super Class</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Super Class</em>' reference.
	 * @see #setSuperClass(reflexactoring.Class)
	 * @see reflexactoring.ReflexactoringPackage#getClassExtend_SuperClass()
	 * @model required="true"
	 * @generated
	 */
	reflexactoring.Class getSuperClass();

	/**
	 * Sets the value of the '{@link reflexactoring.ClassExtend#getSuperClass <em>Super Class</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Super Class</em>' reference.
	 * @see #getSuperClass()
	 * @generated
	 */
	void setSuperClass(reflexactoring.Class value);

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
	 * @see reflexactoring.ReflexactoringPackage#getClassExtend_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link reflexactoring.ClassExtend#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // ClassExtend
