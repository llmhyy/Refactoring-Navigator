package reflexactoring.diagram.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gmf.runtime.diagram.ui.tools.UnspecifiedTypeConnectionTool;
import org.eclipse.gmf.runtime.diagram.ui.tools.UnspecifiedTypeCreationTool;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;

import reflexactoring.diagram.providers.ReflexactoringElementTypes;

/**
 * @generated
 */
public class ReflexactoringPaletteFactory {

	/**
	 * @generated
	 */
	public void fillPalette(PaletteRoot paletteRoot) {
		paletteRoot.add(createReflexactoring1Group());
	}

	/**
	 * Creates "reflexactoring" palette tool group
	 * @generated
	 */
	private PaletteContainer createReflexactoring1Group() {
		PaletteGroup paletteContainer = new PaletteGroup(
				Messages.Reflexactoring1Group_title);
		paletteContainer.setId("createReflexactoring1Group"); //$NON-NLS-1$
		paletteContainer.add(createModule1CreationTool());
		paletteContainer.add(createClass2CreationTool());
		paletteContainer.add(createInterface3CreationTool());
		paletteContainer.add(createTypeDependency4CreationTool());
		paletteContainer.add(createImplement5CreationTool());
		paletteContainer.add(createClassExtend6CreationTool());
		paletteContainer.add(createInterfaceExtend7CreationTool());
		paletteContainer.add(createModuleExtend8CreationTool());
		paletteContainer.add(createModuleDependency9CreationTool());
		return paletteContainer;
	}

	/**
	 * @generated
	 */
	private ToolEntry createModule1CreationTool() {
		NodeToolEntry entry = new NodeToolEntry(
				Messages.Module1CreationTool_title,
				Messages.Module1CreationTool_desc,
				Collections
						.singletonList(ReflexactoringElementTypes.Module_2003));
		entry.setId("createModule1CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(ReflexactoringDiagramEditorPlugin
				.findImageDescriptor("icons/tool/module.gif")); //$NON-NLS-1$
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createClass2CreationTool() {
		ArrayList<IElementType> types = new ArrayList<IElementType>(2);
		types.add(ReflexactoringElementTypes.Class_2001);
		types.add(ReflexactoringElementTypes.Class_3001);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.Class2CreationTool_title,
				Messages.Class2CreationTool_desc, types);
		entry.setId("createClass2CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(ReflexactoringDiagramEditorPlugin
				.findImageDescriptor("icons/tool/class.gif")); //$NON-NLS-1$
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createInterface3CreationTool() {
		ArrayList<IElementType> types = new ArrayList<IElementType>(2);
		types.add(ReflexactoringElementTypes.Interface_2002);
		types.add(ReflexactoringElementTypes.Interface_3002);
		NodeToolEntry entry = new NodeToolEntry(
				Messages.Interface3CreationTool_title,
				Messages.Interface3CreationTool_desc, types);
		entry.setId("createInterface3CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(ReflexactoringDiagramEditorPlugin
				.findImageDescriptor("icons/tool/interface.gif")); //$NON-NLS-1$
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createTypeDependency4CreationTool() {
		LinkToolEntry entry = new LinkToolEntry(
				Messages.TypeDependency4CreationTool_title,
				Messages.TypeDependency4CreationTool_desc,
				Collections
						.singletonList(ReflexactoringElementTypes.TypeDependency_4003));
		entry.setId("createTypeDependency4CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(ReflexactoringDiagramEditorPlugin
				.findImageDescriptor("icons/tool/module.gif")); //$NON-NLS-1$
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createImplement5CreationTool() {
		LinkToolEntry entry = new LinkToolEntry(
				Messages.Implement5CreationTool_title,
				Messages.Implement5CreationTool_desc,
				Collections
						.singletonList(ReflexactoringElementTypes.Implement_4005));
		entry.setId("createImplement5CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(ReflexactoringDiagramEditorPlugin
				.findImageDescriptor("icons/tool/module.gif")); //$NON-NLS-1$
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createClassExtend6CreationTool() {
		LinkToolEntry entry = new LinkToolEntry(
				Messages.ClassExtend6CreationTool_title,
				Messages.ClassExtend6CreationTool_desc,
				Collections
						.singletonList(ReflexactoringElementTypes.ClassExtend_4002));
		entry.setId("createClassExtend6CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(ReflexactoringDiagramEditorPlugin
				.findImageDescriptor("icons/tool/module.gif")); //$NON-NLS-1$
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createInterfaceExtend7CreationTool() {
		LinkToolEntry entry = new LinkToolEntry(
				Messages.InterfaceExtend7CreationTool_title,
				Messages.InterfaceExtend7CreationTool_desc,
				Collections
						.singletonList(ReflexactoringElementTypes.InterfaceExtend_4004));
		entry.setId("createInterfaceExtend7CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(ReflexactoringDiagramEditorPlugin
				.findImageDescriptor("icons/tool/module.gif")); //$NON-NLS-1$
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createModuleExtend8CreationTool() {
		LinkToolEntry entry = new LinkToolEntry(
				Messages.ModuleExtend8CreationTool_title,
				Messages.ModuleExtend8CreationTool_desc,
				Collections
						.singletonList(ReflexactoringElementTypes.ModuleExtend_4006));
		entry.setId("createModuleExtend8CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(ReflexactoringDiagramEditorPlugin
				.findImageDescriptor("icons/tool/module.gif")); //$NON-NLS-1$
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private ToolEntry createModuleDependency9CreationTool() {
		LinkToolEntry entry = new LinkToolEntry(
				Messages.ModuleDependency9CreationTool_title,
				Messages.ModuleDependency9CreationTool_desc,
				Collections
						.singletonList(ReflexactoringElementTypes.ModuleDependency_4001));
		entry.setId("createModuleDependency9CreationTool"); //$NON-NLS-1$
		entry.setSmallIcon(ReflexactoringDiagramEditorPlugin
				.findImageDescriptor("icons/tool/module.gif")); //$NON-NLS-1$
		entry.setLargeIcon(entry.getSmallIcon());
		return entry;
	}

	/**
	 * @generated
	 */
	private static class NodeToolEntry extends ToolEntry {

		/**
		 * @generated
		 */
		private final List<IElementType> elementTypes;

		/**
		 * @generated
		 */
		private NodeToolEntry(String title, String description,
				List<IElementType> elementTypes) {
			super(title, description, null, null);
			this.elementTypes = elementTypes;
		}

		/**
		 * @generated
		 */
		public Tool createTool() {
			Tool tool = new UnspecifiedTypeCreationTool(elementTypes);
			tool.setProperties(getToolProperties());
			return tool;
		}
	}

	/**
	 * @generated
	 */
	private static class LinkToolEntry extends ToolEntry {

		/**
		 * @generated
		 */
		private final List<IElementType> relationshipTypes;

		/**
		 * @generated
		 */
		private LinkToolEntry(String title, String description,
				List<IElementType> relationshipTypes) {
			super(title, description, null, null);
			this.relationshipTypes = relationshipTypes;
		}

		/**
		 * @generated
		 */
		public Tool createTool() {
			Tool tool = new UnspecifiedTypeConnectionTool(relationshipTypes);
			tool.setProperties(getToolProperties());
			return tool;
		}
	}
}
