/**
 */
package reflexactoring;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see reflexactoring.ReflexactoringFactory
 * @model kind="package"
 * @generated
 */
public interface ReflexactoringPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "reflexactoring";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://reflexactoring.com";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "cn.edu.fudan";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ReflexactoringPackage eINSTANCE = reflexactoring.impl.ReflexactoringPackageImpl.init();

	/**
	 * The meta object id for the '{@link reflexactoring.impl.ReflexactoringImpl <em>Reflexactoring</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see reflexactoring.impl.ReflexactoringImpl
	 * @see reflexactoring.impl.ReflexactoringPackageImpl#getReflexactoring()
	 * @generated
	 */
	int REFLEXACTORING = 0;

	/**
	 * The feature id for the '<em><b>Modules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFLEXACTORING__MODULES = 0;

	/**
	 * The feature id for the '<em><b>Types</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFLEXACTORING__TYPES = 1;

	/**
	 * The feature id for the '<em><b>Module Denpencies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFLEXACTORING__MODULE_DENPENCIES = 2;

	/**
	 * The feature id for the '<em><b>Implements</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFLEXACTORING__IMPLEMENTS = 3;

	/**
	 * The feature id for the '<em><b>Type Dependencies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFLEXACTORING__TYPE_DEPENDENCIES = 4;

	/**
	 * The feature id for the '<em><b>Class Extends</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFLEXACTORING__CLASS_EXTENDS = 5;

	/**
	 * The feature id for the '<em><b>Interface Extends</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFLEXACTORING__INTERFACE_EXTENDS = 6;

	/**
	 * The number of structural features of the '<em>Reflexactoring</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFLEXACTORING_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Reflexactoring</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFLEXACTORING_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link reflexactoring.impl.ModuleImpl <em>Module</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see reflexactoring.impl.ModuleImpl
	 * @see reflexactoring.impl.ReflexactoringPackageImpl#getModule()
	 * @generated
	 */
	int MODULE = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Stereotype</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE__STEREOTYPE = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>Mapping Types</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE__MAPPING_TYPES = 3;

	/**
	 * The number of structural features of the '<em>Module</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Module</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link reflexactoring.impl.TypeImpl <em>Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see reflexactoring.impl.TypeImpl
	 * @see reflexactoring.impl.ReflexactoringPackageImpl#getType()
	 * @generated
	 */
	int TYPE = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__PACKAGE_NAME = 1;

	/**
	 * The number of structural features of the '<em>Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link reflexactoring.impl.ClassImpl <em>Class</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see reflexactoring.impl.ClassImpl
	 * @see reflexactoring.impl.ReflexactoringPackageImpl#getClass_()
	 * @generated
	 */
	int CLASS = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS__NAME = TYPE__NAME;

	/**
	 * The feature id for the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS__PACKAGE_NAME = TYPE__PACKAGE_NAME;

	/**
	 * The number of structural features of the '<em>Class</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_FEATURE_COUNT = TYPE_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Class</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_OPERATION_COUNT = TYPE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link reflexactoring.impl.InterfaceImpl <em>Interface</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see reflexactoring.impl.InterfaceImpl
	 * @see reflexactoring.impl.ReflexactoringPackageImpl#getInterface()
	 * @generated
	 */
	int INTERFACE = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE__NAME = TYPE__NAME;

	/**
	 * The feature id for the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE__PACKAGE_NAME = TYPE__PACKAGE_NAME;

	/**
	 * The number of structural features of the '<em>Interface</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_FEATURE_COUNT = TYPE_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Interface</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_OPERATION_COUNT = TYPE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link reflexactoring.impl.ModuleDependencyImpl <em>Module Dependency</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see reflexactoring.impl.ModuleDependencyImpl
	 * @see reflexactoring.impl.ReflexactoringPackageImpl#getModuleDependency()
	 * @generated
	 */
	int MODULE_DEPENDENCY = 5;

	/**
	 * The feature id for the '<em><b>Origin</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_DEPENDENCY__ORIGIN = 0;

	/**
	 * The feature id for the '<em><b>Destination</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_DEPENDENCY__DESTINATION = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_DEPENDENCY__NAME = 2;

	/**
	 * The number of structural features of the '<em>Module Dependency</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_DEPENDENCY_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Module Dependency</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_DEPENDENCY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link reflexactoring.impl.TypeDependencyImpl <em>Type Dependency</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see reflexactoring.impl.TypeDependencyImpl
	 * @see reflexactoring.impl.ReflexactoringPackageImpl#getTypeDependency()
	 * @generated
	 */
	int TYPE_DEPENDENCY = 6;

	/**
	 * The feature id for the '<em><b>Origin</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_DEPENDENCY__ORIGIN = 0;

	/**
	 * The feature id for the '<em><b>Destination</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_DEPENDENCY__DESTINATION = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_DEPENDENCY__NAME = 2;

	/**
	 * The number of structural features of the '<em>Type Dependency</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_DEPENDENCY_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Type Dependency</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_DEPENDENCY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link reflexactoring.impl.ImplementImpl <em>Implement</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see reflexactoring.impl.ImplementImpl
	 * @see reflexactoring.impl.ReflexactoringPackageImpl#getImplement()
	 * @generated
	 */
	int IMPLEMENT = 7;

	/**
	 * The feature id for the '<em><b>Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENT__CLASS = 0;

	/**
	 * The feature id for the '<em><b>Interface</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENT__INTERFACE = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENT__NAME = 2;

	/**
	 * The number of structural features of the '<em>Implement</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENT_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Implement</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link reflexactoring.impl.ClassExtendImpl <em>Class Extend</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see reflexactoring.impl.ClassExtendImpl
	 * @see reflexactoring.impl.ReflexactoringPackageImpl#getClassExtend()
	 * @generated
	 */
	int CLASS_EXTEND = 8;

	/**
	 * The feature id for the '<em><b>Sub Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_EXTEND__SUB_CLASS = 0;

	/**
	 * The feature id for the '<em><b>Super Class</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_EXTEND__SUPER_CLASS = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_EXTEND__NAME = 2;

	/**
	 * The number of structural features of the '<em>Class Extend</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_EXTEND_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Class Extend</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASS_EXTEND_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link reflexactoring.impl.InterfaceExtendImpl <em>Interface Extend</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see reflexactoring.impl.InterfaceExtendImpl
	 * @see reflexactoring.impl.ReflexactoringPackageImpl#getInterfaceExtend()
	 * @generated
	 */
	int INTERFACE_EXTEND = 9;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_EXTEND__NAME = 0;

	/**
	 * The feature id for the '<em><b>Sub Interface</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_EXTEND__SUB_INTERFACE = 1;

	/**
	 * The feature id for the '<em><b>Super Interface</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_EXTEND__SUPER_INTERFACE = 2;

	/**
	 * The number of structural features of the '<em>Interface Extend</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_EXTEND_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Interface Extend</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_EXTEND_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link reflexactoring.Reflexactoring <em>Reflexactoring</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Reflexactoring</em>'.
	 * @see reflexactoring.Reflexactoring
	 * @generated
	 */
	EClass getReflexactoring();

	/**
	 * Returns the meta object for the containment reference list '{@link reflexactoring.Reflexactoring#getModules <em>Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Modules</em>'.
	 * @see reflexactoring.Reflexactoring#getModules()
	 * @see #getReflexactoring()
	 * @generated
	 */
	EReference getReflexactoring_Modules();

	/**
	 * Returns the meta object for the containment reference list '{@link reflexactoring.Reflexactoring#getTypes <em>Types</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Types</em>'.
	 * @see reflexactoring.Reflexactoring#getTypes()
	 * @see #getReflexactoring()
	 * @generated
	 */
	EReference getReflexactoring_Types();

	/**
	 * Returns the meta object for the containment reference list '{@link reflexactoring.Reflexactoring#getModuleDenpencies <em>Module Denpencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Module Denpencies</em>'.
	 * @see reflexactoring.Reflexactoring#getModuleDenpencies()
	 * @see #getReflexactoring()
	 * @generated
	 */
	EReference getReflexactoring_ModuleDenpencies();

	/**
	 * Returns the meta object for the containment reference list '{@link reflexactoring.Reflexactoring#getImplements <em>Implements</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Implements</em>'.
	 * @see reflexactoring.Reflexactoring#getImplements()
	 * @see #getReflexactoring()
	 * @generated
	 */
	EReference getReflexactoring_Implements();

	/**
	 * Returns the meta object for the containment reference list '{@link reflexactoring.Reflexactoring#getTypeDependencies <em>Type Dependencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Type Dependencies</em>'.
	 * @see reflexactoring.Reflexactoring#getTypeDependencies()
	 * @see #getReflexactoring()
	 * @generated
	 */
	EReference getReflexactoring_TypeDependencies();

	/**
	 * Returns the meta object for the containment reference list '{@link reflexactoring.Reflexactoring#getClassExtends <em>Class Extends</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Class Extends</em>'.
	 * @see reflexactoring.Reflexactoring#getClassExtends()
	 * @see #getReflexactoring()
	 * @generated
	 */
	EReference getReflexactoring_ClassExtends();

	/**
	 * Returns the meta object for the containment reference list '{@link reflexactoring.Reflexactoring#getInterfaceExtends <em>Interface Extends</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Interface Extends</em>'.
	 * @see reflexactoring.Reflexactoring#getInterfaceExtends()
	 * @see #getReflexactoring()
	 * @generated
	 */
	EReference getReflexactoring_InterfaceExtends();

	/**
	 * Returns the meta object for class '{@link reflexactoring.Module <em>Module</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Module</em>'.
	 * @see reflexactoring.Module
	 * @generated
	 */
	EClass getModule();

	/**
	 * Returns the meta object for the attribute '{@link reflexactoring.Module#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see reflexactoring.Module#getName()
	 * @see #getModule()
	 * @generated
	 */
	EAttribute getModule_Name();

	/**
	 * Returns the meta object for the attribute '{@link reflexactoring.Module#getStereotype <em>Stereotype</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Stereotype</em>'.
	 * @see reflexactoring.Module#getStereotype()
	 * @see #getModule()
	 * @generated
	 */
	EAttribute getModule_Stereotype();

	/**
	 * Returns the meta object for the attribute '{@link reflexactoring.Module#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see reflexactoring.Module#getDescription()
	 * @see #getModule()
	 * @generated
	 */
	EAttribute getModule_Description();

	/**
	 * Returns the meta object for the containment reference list '{@link reflexactoring.Module#getMappingTypes <em>Mapping Types</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Mapping Types</em>'.
	 * @see reflexactoring.Module#getMappingTypes()
	 * @see #getModule()
	 * @generated
	 */
	EReference getModule_MappingTypes();

	/**
	 * Returns the meta object for class '{@link reflexactoring.Type <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Type</em>'.
	 * @see reflexactoring.Type
	 * @generated
	 */
	EClass getType();

	/**
	 * Returns the meta object for the attribute '{@link reflexactoring.Type#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see reflexactoring.Type#getName()
	 * @see #getType()
	 * @generated
	 */
	EAttribute getType_Name();

	/**
	 * Returns the meta object for the attribute '{@link reflexactoring.Type#getPackageName <em>Package Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Package Name</em>'.
	 * @see reflexactoring.Type#getPackageName()
	 * @see #getType()
	 * @generated
	 */
	EAttribute getType_PackageName();

	/**
	 * Returns the meta object for class '{@link reflexactoring.Class <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Class</em>'.
	 * @see reflexactoring.Class
	 * @generated
	 */
	EClass getClass_();

	/**
	 * Returns the meta object for class '{@link reflexactoring.Interface <em>Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Interface</em>'.
	 * @see reflexactoring.Interface
	 * @generated
	 */
	EClass getInterface();

	/**
	 * Returns the meta object for class '{@link reflexactoring.ModuleDependency <em>Module Dependency</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Module Dependency</em>'.
	 * @see reflexactoring.ModuleDependency
	 * @generated
	 */
	EClass getModuleDependency();

	/**
	 * Returns the meta object for the reference '{@link reflexactoring.ModuleDependency#getOrigin <em>Origin</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Origin</em>'.
	 * @see reflexactoring.ModuleDependency#getOrigin()
	 * @see #getModuleDependency()
	 * @generated
	 */
	EReference getModuleDependency_Origin();

	/**
	 * Returns the meta object for the reference '{@link reflexactoring.ModuleDependency#getDestination <em>Destination</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Destination</em>'.
	 * @see reflexactoring.ModuleDependency#getDestination()
	 * @see #getModuleDependency()
	 * @generated
	 */
	EReference getModuleDependency_Destination();

	/**
	 * Returns the meta object for the attribute '{@link reflexactoring.ModuleDependency#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see reflexactoring.ModuleDependency#getName()
	 * @see #getModuleDependency()
	 * @generated
	 */
	EAttribute getModuleDependency_Name();

	/**
	 * Returns the meta object for class '{@link reflexactoring.TypeDependency <em>Type Dependency</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Type Dependency</em>'.
	 * @see reflexactoring.TypeDependency
	 * @generated
	 */
	EClass getTypeDependency();

	/**
	 * Returns the meta object for the reference '{@link reflexactoring.TypeDependency#getOrigin <em>Origin</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Origin</em>'.
	 * @see reflexactoring.TypeDependency#getOrigin()
	 * @see #getTypeDependency()
	 * @generated
	 */
	EReference getTypeDependency_Origin();

	/**
	 * Returns the meta object for the reference '{@link reflexactoring.TypeDependency#getDestination <em>Destination</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Destination</em>'.
	 * @see reflexactoring.TypeDependency#getDestination()
	 * @see #getTypeDependency()
	 * @generated
	 */
	EReference getTypeDependency_Destination();

	/**
	 * Returns the meta object for the attribute '{@link reflexactoring.TypeDependency#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see reflexactoring.TypeDependency#getName()
	 * @see #getTypeDependency()
	 * @generated
	 */
	EAttribute getTypeDependency_Name();

	/**
	 * Returns the meta object for class '{@link reflexactoring.Implement <em>Implement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Implement</em>'.
	 * @see reflexactoring.Implement
	 * @generated
	 */
	EClass getImplement();

	/**
	 * Returns the meta object for the reference '{@link reflexactoring.Implement#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Class</em>'.
	 * @see reflexactoring.Implement#getClass_()
	 * @see #getImplement()
	 * @generated
	 */
	EReference getImplement_Class();

	/**
	 * Returns the meta object for the reference '{@link reflexactoring.Implement#getInterface <em>Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Interface</em>'.
	 * @see reflexactoring.Implement#getInterface()
	 * @see #getImplement()
	 * @generated
	 */
	EReference getImplement_Interface();

	/**
	 * Returns the meta object for the attribute '{@link reflexactoring.Implement#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see reflexactoring.Implement#getName()
	 * @see #getImplement()
	 * @generated
	 */
	EAttribute getImplement_Name();

	/**
	 * Returns the meta object for class '{@link reflexactoring.ClassExtend <em>Class Extend</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Class Extend</em>'.
	 * @see reflexactoring.ClassExtend
	 * @generated
	 */
	EClass getClassExtend();

	/**
	 * Returns the meta object for the reference '{@link reflexactoring.ClassExtend#getSubClass <em>Sub Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Sub Class</em>'.
	 * @see reflexactoring.ClassExtend#getSubClass()
	 * @see #getClassExtend()
	 * @generated
	 */
	EReference getClassExtend_SubClass();

	/**
	 * Returns the meta object for the reference '{@link reflexactoring.ClassExtend#getSuperClass <em>Super Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Super Class</em>'.
	 * @see reflexactoring.ClassExtend#getSuperClass()
	 * @see #getClassExtend()
	 * @generated
	 */
	EReference getClassExtend_SuperClass();

	/**
	 * Returns the meta object for the attribute '{@link reflexactoring.ClassExtend#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see reflexactoring.ClassExtend#getName()
	 * @see #getClassExtend()
	 * @generated
	 */
	EAttribute getClassExtend_Name();

	/**
	 * Returns the meta object for class '{@link reflexactoring.InterfaceExtend <em>Interface Extend</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Interface Extend</em>'.
	 * @see reflexactoring.InterfaceExtend
	 * @generated
	 */
	EClass getInterfaceExtend();

	/**
	 * Returns the meta object for the attribute '{@link reflexactoring.InterfaceExtend#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see reflexactoring.InterfaceExtend#getName()
	 * @see #getInterfaceExtend()
	 * @generated
	 */
	EAttribute getInterfaceExtend_Name();

	/**
	 * Returns the meta object for the reference '{@link reflexactoring.InterfaceExtend#getSubInterface <em>Sub Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Sub Interface</em>'.
	 * @see reflexactoring.InterfaceExtend#getSubInterface()
	 * @see #getInterfaceExtend()
	 * @generated
	 */
	EReference getInterfaceExtend_SubInterface();

	/**
	 * Returns the meta object for the reference '{@link reflexactoring.InterfaceExtend#getSuperInterface <em>Super Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Super Interface</em>'.
	 * @see reflexactoring.InterfaceExtend#getSuperInterface()
	 * @see #getInterfaceExtend()
	 * @generated
	 */
	EReference getInterfaceExtend_SuperInterface();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ReflexactoringFactory getReflexactoringFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link reflexactoring.impl.ReflexactoringImpl <em>Reflexactoring</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see reflexactoring.impl.ReflexactoringImpl
		 * @see reflexactoring.impl.ReflexactoringPackageImpl#getReflexactoring()
		 * @generated
		 */
		EClass REFLEXACTORING = eINSTANCE.getReflexactoring();

		/**
		 * The meta object literal for the '<em><b>Modules</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFLEXACTORING__MODULES = eINSTANCE.getReflexactoring_Modules();

		/**
		 * The meta object literal for the '<em><b>Types</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFLEXACTORING__TYPES = eINSTANCE.getReflexactoring_Types();

		/**
		 * The meta object literal for the '<em><b>Module Denpencies</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFLEXACTORING__MODULE_DENPENCIES = eINSTANCE.getReflexactoring_ModuleDenpencies();

		/**
		 * The meta object literal for the '<em><b>Implements</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFLEXACTORING__IMPLEMENTS = eINSTANCE.getReflexactoring_Implements();

		/**
		 * The meta object literal for the '<em><b>Type Dependencies</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFLEXACTORING__TYPE_DEPENDENCIES = eINSTANCE.getReflexactoring_TypeDependencies();

		/**
		 * The meta object literal for the '<em><b>Class Extends</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFLEXACTORING__CLASS_EXTENDS = eINSTANCE.getReflexactoring_ClassExtends();

		/**
		 * The meta object literal for the '<em><b>Interface Extends</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFLEXACTORING__INTERFACE_EXTENDS = eINSTANCE.getReflexactoring_InterfaceExtends();

		/**
		 * The meta object literal for the '{@link reflexactoring.impl.ModuleImpl <em>Module</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see reflexactoring.impl.ModuleImpl
		 * @see reflexactoring.impl.ReflexactoringPackageImpl#getModule()
		 * @generated
		 */
		EClass MODULE = eINSTANCE.getModule();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODULE__NAME = eINSTANCE.getModule_Name();

		/**
		 * The meta object literal for the '<em><b>Stereotype</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODULE__STEREOTYPE = eINSTANCE.getModule_Stereotype();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODULE__DESCRIPTION = eINSTANCE.getModule_Description();

		/**
		 * The meta object literal for the '<em><b>Mapping Types</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODULE__MAPPING_TYPES = eINSTANCE.getModule_MappingTypes();

		/**
		 * The meta object literal for the '{@link reflexactoring.impl.TypeImpl <em>Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see reflexactoring.impl.TypeImpl
		 * @see reflexactoring.impl.ReflexactoringPackageImpl#getType()
		 * @generated
		 */
		EClass TYPE = eINSTANCE.getType();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE__NAME = eINSTANCE.getType_Name();

		/**
		 * The meta object literal for the '<em><b>Package Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE__PACKAGE_NAME = eINSTANCE.getType_PackageName();

		/**
		 * The meta object literal for the '{@link reflexactoring.impl.ClassImpl <em>Class</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see reflexactoring.impl.ClassImpl
		 * @see reflexactoring.impl.ReflexactoringPackageImpl#getClass_()
		 * @generated
		 */
		EClass CLASS = eINSTANCE.getClass_();

		/**
		 * The meta object literal for the '{@link reflexactoring.impl.InterfaceImpl <em>Interface</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see reflexactoring.impl.InterfaceImpl
		 * @see reflexactoring.impl.ReflexactoringPackageImpl#getInterface()
		 * @generated
		 */
		EClass INTERFACE = eINSTANCE.getInterface();

		/**
		 * The meta object literal for the '{@link reflexactoring.impl.ModuleDependencyImpl <em>Module Dependency</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see reflexactoring.impl.ModuleDependencyImpl
		 * @see reflexactoring.impl.ReflexactoringPackageImpl#getModuleDependency()
		 * @generated
		 */
		EClass MODULE_DEPENDENCY = eINSTANCE.getModuleDependency();

		/**
		 * The meta object literal for the '<em><b>Origin</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODULE_DEPENDENCY__ORIGIN = eINSTANCE.getModuleDependency_Origin();

		/**
		 * The meta object literal for the '<em><b>Destination</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODULE_DEPENDENCY__DESTINATION = eINSTANCE.getModuleDependency_Destination();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODULE_DEPENDENCY__NAME = eINSTANCE.getModuleDependency_Name();

		/**
		 * The meta object literal for the '{@link reflexactoring.impl.TypeDependencyImpl <em>Type Dependency</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see reflexactoring.impl.TypeDependencyImpl
		 * @see reflexactoring.impl.ReflexactoringPackageImpl#getTypeDependency()
		 * @generated
		 */
		EClass TYPE_DEPENDENCY = eINSTANCE.getTypeDependency();

		/**
		 * The meta object literal for the '<em><b>Origin</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TYPE_DEPENDENCY__ORIGIN = eINSTANCE.getTypeDependency_Origin();

		/**
		 * The meta object literal for the '<em><b>Destination</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TYPE_DEPENDENCY__DESTINATION = eINSTANCE.getTypeDependency_Destination();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE_DEPENDENCY__NAME = eINSTANCE.getTypeDependency_Name();

		/**
		 * The meta object literal for the '{@link reflexactoring.impl.ImplementImpl <em>Implement</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see reflexactoring.impl.ImplementImpl
		 * @see reflexactoring.impl.ReflexactoringPackageImpl#getImplement()
		 * @generated
		 */
		EClass IMPLEMENT = eINSTANCE.getImplement();

		/**
		 * The meta object literal for the '<em><b>Class</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference IMPLEMENT__CLASS = eINSTANCE.getImplement_Class();

		/**
		 * The meta object literal for the '<em><b>Interface</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference IMPLEMENT__INTERFACE = eINSTANCE.getImplement_Interface();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPLEMENT__NAME = eINSTANCE.getImplement_Name();

		/**
		 * The meta object literal for the '{@link reflexactoring.impl.ClassExtendImpl <em>Class Extend</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see reflexactoring.impl.ClassExtendImpl
		 * @see reflexactoring.impl.ReflexactoringPackageImpl#getClassExtend()
		 * @generated
		 */
		EClass CLASS_EXTEND = eINSTANCE.getClassExtend();

		/**
		 * The meta object literal for the '<em><b>Sub Class</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_EXTEND__SUB_CLASS = eINSTANCE.getClassExtend_SubClass();

		/**
		 * The meta object literal for the '<em><b>Super Class</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASS_EXTEND__SUPER_CLASS = eINSTANCE.getClassExtend_SuperClass();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASS_EXTEND__NAME = eINSTANCE.getClassExtend_Name();

		/**
		 * The meta object literal for the '{@link reflexactoring.impl.InterfaceExtendImpl <em>Interface Extend</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see reflexactoring.impl.InterfaceExtendImpl
		 * @see reflexactoring.impl.ReflexactoringPackageImpl#getInterfaceExtend()
		 * @generated
		 */
		EClass INTERFACE_EXTEND = eINSTANCE.getInterfaceExtend();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERFACE_EXTEND__NAME = eINSTANCE.getInterfaceExtend_Name();

		/**
		 * The meta object literal for the '<em><b>Sub Interface</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_EXTEND__SUB_INTERFACE = eINSTANCE.getInterfaceExtend_SubInterface();

		/**
		 * The meta object literal for the '<em><b>Super Interface</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_EXTEND__SUPER_INTERFACE = eINSTANCE.getInterfaceExtend_SuperInterface();

	}

} //ReflexactoringPackage
