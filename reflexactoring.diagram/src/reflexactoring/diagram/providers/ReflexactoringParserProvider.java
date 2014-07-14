package reflexactoring.diagram.providers;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.service.AbstractProvider;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.common.ui.services.parser.GetParserOperation;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParser;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParserProvider;
import org.eclipse.gmf.runtime.common.ui.services.parser.ParserService;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.ui.services.parser.ParserHintAdapter;
import org.eclipse.gmf.runtime.notation.View;

import reflexactoring.ReflexactoringPackage;
import reflexactoring.diagram.edit.parts.ClassName2EditPart;
import reflexactoring.diagram.edit.parts.ClassNameEditPart;
import reflexactoring.diagram.edit.parts.InterfaceName2EditPart;
import reflexactoring.diagram.edit.parts.InterfaceNameEditPart;
import reflexactoring.diagram.edit.parts.ModuleNameEditPart;
import reflexactoring.diagram.parsers.MessageFormatParser;
import reflexactoring.diagram.part.ReflexactoringVisualIDRegistry;

/**
 * @generated
 */
public class ReflexactoringParserProvider extends AbstractProvider implements
		IParserProvider {

	/**
	 * @generated
	 */
	private IParser className_5001Parser;

	/**
	 * @generated
	 */
	private IParser getClassName_5001Parser() {
		if (className_5001Parser == null) {
			EAttribute[] features = new EAttribute[] { ReflexactoringPackage.eINSTANCE
					.getType_Name() };
			MessageFormatParser parser = new MessageFormatParser(features);
			className_5001Parser = parser;
		}
		return className_5001Parser;
	}

	/**
	 * @generated
	 */
	private IParser interfaceName_5002Parser;

	/**
	 * @generated
	 */
	private IParser getInterfaceName_5002Parser() {
		if (interfaceName_5002Parser == null) {
			EAttribute[] features = new EAttribute[] { ReflexactoringPackage.eINSTANCE
					.getType_Name() };
			MessageFormatParser parser = new MessageFormatParser(features);
			interfaceName_5002Parser = parser;
		}
		return interfaceName_5002Parser;
	}

	/**
	 * @generated
	 */
	private IParser moduleName_5005Parser;

	/**
	 * @generated
	 */
	private IParser getModuleName_5005Parser() {
		if (moduleName_5005Parser == null) {
			EAttribute[] features = new EAttribute[] { ReflexactoringPackage.eINSTANCE
					.getModule_Name() };
			MessageFormatParser parser = new MessageFormatParser(features);
			moduleName_5005Parser = parser;
		}
		return moduleName_5005Parser;
	}

	/**
	 * @generated
	 */
	private IParser className_5003Parser;

	/**
	 * @generated
	 */
	private IParser getClassName_5003Parser() {
		if (className_5003Parser == null) {
			EAttribute[] features = new EAttribute[] { ReflexactoringPackage.eINSTANCE
					.getType_Name() };
			MessageFormatParser parser = new MessageFormatParser(features);
			className_5003Parser = parser;
		}
		return className_5003Parser;
	}

	/**
	 * @generated
	 */
	private IParser interfaceName_5004Parser;

	/**
	 * @generated
	 */
	private IParser getInterfaceName_5004Parser() {
		if (interfaceName_5004Parser == null) {
			EAttribute[] features = new EAttribute[] { ReflexactoringPackage.eINSTANCE
					.getType_Name() };
			MessageFormatParser parser = new MessageFormatParser(features);
			interfaceName_5004Parser = parser;
		}
		return interfaceName_5004Parser;
	}

	/**
	 * @generated
	 */
	protected IParser getParser(int visualID) {
		switch (visualID) {
		case ClassNameEditPart.VISUAL_ID:
			return getClassName_5001Parser();
		case InterfaceNameEditPart.VISUAL_ID:
			return getInterfaceName_5002Parser();
		case ModuleNameEditPart.VISUAL_ID:
			return getModuleName_5005Parser();
		case ClassName2EditPart.VISUAL_ID:
			return getClassName_5003Parser();
		case InterfaceName2EditPart.VISUAL_ID:
			return getInterfaceName_5004Parser();
		}
		return null;
	}

	/**
	 * Utility method that consults ParserService
	 * @generated
	 */
	public static IParser getParser(IElementType type, EObject object,
			String parserHint) {
		return ParserService.getInstance().getParser(
				new HintAdapter(type, object, parserHint));
	}

	/**
	 * @generated
	 */
	public IParser getParser(IAdaptable hint) {
		String vid = (String) hint.getAdapter(String.class);
		if (vid != null) {
			return getParser(ReflexactoringVisualIDRegistry.getVisualID(vid));
		}
		View view = (View) hint.getAdapter(View.class);
		if (view != null) {
			return getParser(ReflexactoringVisualIDRegistry.getVisualID(view));
		}
		return null;
	}

	/**
	 * @generated
	 */
	public boolean provides(IOperation operation) {
		if (operation instanceof GetParserOperation) {
			IAdaptable hint = ((GetParserOperation) operation).getHint();
			if (ReflexactoringElementTypes.getElement(hint) == null) {
				return false;
			}
			return getParser(hint) != null;
		}
		return false;
	}

	/**
	 * @generated
	 */
	private static class HintAdapter extends ParserHintAdapter {

		/**
		 * @generated
		 */
		private final IElementType elementType;

		/**
		 * @generated
		 */
		public HintAdapter(IElementType type, EObject object, String parserHint) {
			super(object, parserHint);
			assert type != null;
			elementType = type;
		}

		/**
		 * @generated
		 */
		public Object getAdapter(Class adapter) {
			if (IElementType.class.equals(adapter)) {
				return elementType;
			}
			return super.getAdapter(adapter);
		}
	}

}
