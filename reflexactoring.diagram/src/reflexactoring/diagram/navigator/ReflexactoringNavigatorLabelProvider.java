package reflexactoring.diagram.navigator;

import org.eclipse.gmf.runtime.common.ui.services.parser.IParser;
import org.eclipse.gmf.runtime.common.ui.services.parser.ParserOptions;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITreePathLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;

import reflexactoring.ClassExtend;
import reflexactoring.Implement;
import reflexactoring.InterfaceExtend;
import reflexactoring.ModuleDependency;
import reflexactoring.ModuleExtend;
import reflexactoring.TypeDependency;
import reflexactoring.diagram.edit.parts.Class2EditPart;
import reflexactoring.diagram.edit.parts.ClassEditPart;
import reflexactoring.diagram.edit.parts.ClassExtendEditPart;
import reflexactoring.diagram.edit.parts.ClassName2EditPart;
import reflexactoring.diagram.edit.parts.ClassNameEditPart;
import reflexactoring.diagram.edit.parts.ImplementEditPart;
import reflexactoring.diagram.edit.parts.Interface2EditPart;
import reflexactoring.diagram.edit.parts.InterfaceEditPart;
import reflexactoring.diagram.edit.parts.InterfaceExtendEditPart;
import reflexactoring.diagram.edit.parts.InterfaceName2EditPart;
import reflexactoring.diagram.edit.parts.InterfaceNameEditPart;
import reflexactoring.diagram.edit.parts.ModuleDependencyEditPart;
import reflexactoring.diagram.edit.parts.ModuleEditPart;
import reflexactoring.diagram.edit.parts.ModuleExtendEditPart;
import reflexactoring.diagram.edit.parts.ModuleNameEditPart;
import reflexactoring.diagram.edit.parts.ReflexactoringEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart;
import reflexactoring.diagram.part.ReflexactoringDiagramEditorPlugin;
import reflexactoring.diagram.part.ReflexactoringVisualIDRegistry;
import reflexactoring.diagram.providers.ReflexactoringElementTypes;
import reflexactoring.diagram.providers.ReflexactoringParserProvider;

/**
 * @generated
 */
public class ReflexactoringNavigatorLabelProvider extends LabelProvider
		implements ICommonLabelProvider, ITreePathLabelProvider {

	/**
	 * @generated
	 */
	static {
		ReflexactoringDiagramEditorPlugin
				.getInstance()
				.getImageRegistry()
				.put("Navigator?UnknownElement", ImageDescriptor.getMissingImageDescriptor()); //$NON-NLS-1$
		ReflexactoringDiagramEditorPlugin
				.getInstance()
				.getImageRegistry()
				.put("Navigator?ImageNotFound", ImageDescriptor.getMissingImageDescriptor()); //$NON-NLS-1$
	}

	/**
	 * @generated
	 */
	public void updateLabel(ViewerLabel label, TreePath elementPath) {
		Object element = elementPath.getLastSegment();
		if (element instanceof ReflexactoringNavigatorItem
				&& !isOwnView(((ReflexactoringNavigatorItem) element).getView())) {
			return;
		}
		label.setText(getText(element));
		label.setImage(getImage(element));
	}

	/**
	 * @generated
	 */
	public Image getImage(Object element) {
		if (element instanceof ReflexactoringNavigatorGroup) {
			ReflexactoringNavigatorGroup group = (ReflexactoringNavigatorGroup) element;
			return ReflexactoringDiagramEditorPlugin.getInstance()
					.getBundledImage(group.getIcon());
		}

		if (element instanceof ReflexactoringNavigatorItem) {
			ReflexactoringNavigatorItem navigatorItem = (ReflexactoringNavigatorItem) element;
			if (!isOwnView(navigatorItem.getView())) {
				return super.getImage(element);
			}
			return getImage(navigatorItem.getView());
		}

		return super.getImage(element);
	}

	/**
	 * @generated
	 */
	public Image getImage(View view) {
		switch (ReflexactoringVisualIDRegistry.getVisualID(view)) {
		case ClassEditPart.VISUAL_ID:
			return getImage(
					"Navigator?TopLevelNode?http://reflexactoring.com?Class", ReflexactoringElementTypes.Class_2001); //$NON-NLS-1$
		case ReflexactoringEditPart.VISUAL_ID:
			return getImage(
					"Navigator?Diagram?http://reflexactoring.com?Reflexactoring", ReflexactoringElementTypes.Reflexactoring_1000); //$NON-NLS-1$
		case ModuleEditPart.VISUAL_ID:
			return getImage(
					"Navigator?TopLevelNode?http://reflexactoring.com?Module", ReflexactoringElementTypes.Module_2003); //$NON-NLS-1$
		case ImplementEditPart.VISUAL_ID:
			return getImage(
					"Navigator?Link?http://reflexactoring.com?Implement", ReflexactoringElementTypes.Implement_4005); //$NON-NLS-1$
		case Interface2EditPart.VISUAL_ID:
			return getImage(
					"Navigator?Node?http://reflexactoring.com?Interface", ReflexactoringElementTypes.Interface_3002); //$NON-NLS-1$
		case Class2EditPart.VISUAL_ID:
			return getImage(
					"Navigator?Node?http://reflexactoring.com?Class", ReflexactoringElementTypes.Class_3001); //$NON-NLS-1$
		case ModuleExtendEditPart.VISUAL_ID:
			return getImage(
					"Navigator?Link?http://reflexactoring.com?ModuleExtend", ReflexactoringElementTypes.ModuleExtend_4006); //$NON-NLS-1$
		case ClassExtendEditPart.VISUAL_ID:
			return getImage(
					"Navigator?Link?http://reflexactoring.com?ClassExtend", ReflexactoringElementTypes.ClassExtend_4002); //$NON-NLS-1$
		case TypeDependencyEditPart.VISUAL_ID:
			return getImage(
					"Navigator?Link?http://reflexactoring.com?TypeDependency", ReflexactoringElementTypes.TypeDependency_4003); //$NON-NLS-1$
		case InterfaceEditPart.VISUAL_ID:
			return getImage(
					"Navigator?TopLevelNode?http://reflexactoring.com?Interface", ReflexactoringElementTypes.Interface_2002); //$NON-NLS-1$
		case ModuleDependencyEditPart.VISUAL_ID:
			return getImage(
					"Navigator?Link?http://reflexactoring.com?ModuleDependency", ReflexactoringElementTypes.ModuleDependency_4001); //$NON-NLS-1$
		case InterfaceExtendEditPart.VISUAL_ID:
			return getImage(
					"Navigator?Link?http://reflexactoring.com?InterfaceExtend", ReflexactoringElementTypes.InterfaceExtend_4004); //$NON-NLS-1$
		}
		return getImage("Navigator?UnknownElement", null); //$NON-NLS-1$
	}

	/**
	 * @generated
	 */
	private Image getImage(String key, IElementType elementType) {
		ImageRegistry imageRegistry = ReflexactoringDiagramEditorPlugin
				.getInstance().getImageRegistry();
		Image image = imageRegistry.get(key);
		if (image == null && elementType != null
				&& ReflexactoringElementTypes.isKnownElementType(elementType)) {
			image = ReflexactoringElementTypes.getImage(elementType);
			imageRegistry.put(key, image);
		}

		if (image == null) {
			image = imageRegistry.get("Navigator?ImageNotFound"); //$NON-NLS-1$
			imageRegistry.put(key, image);
		}
		return image;
	}

	/**
	 * @generated
	 */
	public String getText(Object element) {
		if (element instanceof ReflexactoringNavigatorGroup) {
			ReflexactoringNavigatorGroup group = (ReflexactoringNavigatorGroup) element;
			return group.getGroupName();
		}

		if (element instanceof ReflexactoringNavigatorItem) {
			ReflexactoringNavigatorItem navigatorItem = (ReflexactoringNavigatorItem) element;
			if (!isOwnView(navigatorItem.getView())) {
				return null;
			}
			return getText(navigatorItem.getView());
		}

		return super.getText(element);
	}

	/**
	 * @generated
	 */
	public String getText(View view) {
		if (view.getElement() != null && view.getElement().eIsProxy()) {
			return getUnresolvedDomainElementProxyText(view);
		}
		switch (ReflexactoringVisualIDRegistry.getVisualID(view)) {
		case ClassEditPart.VISUAL_ID:
			return getClass_2001Text(view);
		case ReflexactoringEditPart.VISUAL_ID:
			return getReflexactoring_1000Text(view);
		case ModuleEditPart.VISUAL_ID:
			return getModule_2003Text(view);
		case ImplementEditPart.VISUAL_ID:
			return getImplement_4005Text(view);
		case Interface2EditPart.VISUAL_ID:
			return getInterface_3002Text(view);
		case Class2EditPart.VISUAL_ID:
			return getClass_3001Text(view);
		case ModuleExtendEditPart.VISUAL_ID:
			return getModuleExtend_4006Text(view);
		case ClassExtendEditPart.VISUAL_ID:
			return getClassExtend_4002Text(view);
		case TypeDependencyEditPart.VISUAL_ID:
			return getTypeDependency_4003Text(view);
		case InterfaceEditPart.VISUAL_ID:
			return getInterface_2002Text(view);
		case ModuleDependencyEditPart.VISUAL_ID:
			return getModuleDependency_4001Text(view);
		case InterfaceExtendEditPart.VISUAL_ID:
			return getInterfaceExtend_4004Text(view);
		}
		return getUnknownElementText(view);
	}

	/**
	 * @generated
	 */
	private String getTypeDependency_4003Text(View view) {
		TypeDependency domainModelElement = (TypeDependency) view.getElement();
		if (domainModelElement != null) {
			return domainModelElement.getName();
		} else {
			ReflexactoringDiagramEditorPlugin.getInstance().logError(
					"No domain element for view with visualID = " + 4003); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getClass_3001Text(View view) {
		IParser parser = ReflexactoringParserProvider.getParser(
				ReflexactoringElementTypes.Class_3001,
				view.getElement() != null ? view.getElement() : view,
				ReflexactoringVisualIDRegistry
						.getType(ClassName2EditPart.VISUAL_ID));
		if (parser != null) {
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		} else {
			ReflexactoringDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5003); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getReflexactoring_1000Text(View view) {
		return ""; //$NON-NLS-1$
	}

	/**
	 * @generated
	 */
	private String getImplement_4005Text(View view) {
		Implement domainModelElement = (Implement) view.getElement();
		if (domainModelElement != null) {
			return domainModelElement.getName();
		} else {
			ReflexactoringDiagramEditorPlugin.getInstance().logError(
					"No domain element for view with visualID = " + 4005); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getModuleDependency_4001Text(View view) {
		ModuleDependency domainModelElement = (ModuleDependency) view
				.getElement();
		if (domainModelElement != null) {
			return domainModelElement.getName();
		} else {
			ReflexactoringDiagramEditorPlugin.getInstance().logError(
					"No domain element for view with visualID = " + 4001); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getInterfaceExtend_4004Text(View view) {
		InterfaceExtend domainModelElement = (InterfaceExtend) view
				.getElement();
		if (domainModelElement != null) {
			return domainModelElement.getName();
		} else {
			ReflexactoringDiagramEditorPlugin.getInstance().logError(
					"No domain element for view with visualID = " + 4004); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getClassExtend_4002Text(View view) {
		ClassExtend domainModelElement = (ClassExtend) view.getElement();
		if (domainModelElement != null) {
			return domainModelElement.getName();
		} else {
			ReflexactoringDiagramEditorPlugin.getInstance().logError(
					"No domain element for view with visualID = " + 4002); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getInterface_3002Text(View view) {
		IParser parser = ReflexactoringParserProvider.getParser(
				ReflexactoringElementTypes.Interface_3002,
				view.getElement() != null ? view.getElement() : view,
				ReflexactoringVisualIDRegistry
						.getType(InterfaceName2EditPart.VISUAL_ID));
		if (parser != null) {
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		} else {
			ReflexactoringDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5004); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getModuleExtend_4006Text(View view) {
		ModuleExtend domainModelElement = (ModuleExtend) view.getElement();
		if (domainModelElement != null) {
			return domainModelElement.getName();
		} else {
			ReflexactoringDiagramEditorPlugin.getInstance().logError(
					"No domain element for view with visualID = " + 4006); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getInterface_2002Text(View view) {
		IParser parser = ReflexactoringParserProvider.getParser(
				ReflexactoringElementTypes.Interface_2002,
				view.getElement() != null ? view.getElement() : view,
				ReflexactoringVisualIDRegistry
						.getType(InterfaceNameEditPart.VISUAL_ID));
		if (parser != null) {
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		} else {
			ReflexactoringDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5002); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getModule_2003Text(View view) {
		IParser parser = ReflexactoringParserProvider.getParser(
				ReflexactoringElementTypes.Module_2003,
				view.getElement() != null ? view.getElement() : view,
				ReflexactoringVisualIDRegistry
						.getType(ModuleNameEditPart.VISUAL_ID));
		if (parser != null) {
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		} else {
			ReflexactoringDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5005); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getClass_2001Text(View view) {
		IParser parser = ReflexactoringParserProvider.getParser(
				ReflexactoringElementTypes.Class_2001,
				view.getElement() != null ? view.getElement() : view,
				ReflexactoringVisualIDRegistry
						.getType(ClassNameEditPart.VISUAL_ID));
		if (parser != null) {
			return parser.getPrintString(new EObjectAdapter(
					view.getElement() != null ? view.getElement() : view),
					ParserOptions.NONE.intValue());
		} else {
			ReflexactoringDiagramEditorPlugin.getInstance().logError(
					"Parser was not found for label " + 5001); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @generated
	 */
	private String getUnknownElementText(View view) {
		return "<UnknownElement Visual_ID = " + view.getType() + ">"; //$NON-NLS-1$  //$NON-NLS-2$
	}

	/**
	 * @generated
	 */
	private String getUnresolvedDomainElementProxyText(View view) {
		return "<Unresolved domain element Visual_ID = " + view.getType() + ">"; //$NON-NLS-1$  //$NON-NLS-2$
	}

	/**
	 * @generated
	 */
	public void init(ICommonContentExtensionSite aConfig) {
	}

	/**
	 * @generated
	 */
	public void restoreState(IMemento aMemento) {
	}

	/**
	 * @generated
	 */
	public void saveState(IMemento aMemento) {
	}

	/**
	 * @generated
	 */
	public String getDescription(Object anElement) {
		return null;
	}

	/**
	 * @generated
	 */
	private boolean isOwnView(View view) {
		return ReflexactoringEditPart.MODEL_ID
				.equals(ReflexactoringVisualIDRegistry.getModelID(view));
	}

}
