package reflexactoring.diagram.edit.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;

import reflexactoring.diagram.edit.commands.Class2CreateCommand;
import reflexactoring.diagram.edit.commands.Interface2CreateCommand;
import reflexactoring.diagram.providers.ReflexactoringElementTypes;

/**
 * @generated
 */
public class ModuleTypeContainerCompartmentItemSemanticEditPolicy extends
		ReflexactoringBaseItemSemanticEditPolicy {

	/**
	 * @generated
	 */
	public ModuleTypeContainerCompartmentItemSemanticEditPolicy() {
		super(ReflexactoringElementTypes.Module_2003);
	}

	/**
	 * @generated
	 */
	protected Command getCreateCommand(CreateElementRequest req) {
		if (ReflexactoringElementTypes.Class_3001 == req.getElementType()) {
			return getGEFWrapper(new Class2CreateCommand(req));
		}
		if (ReflexactoringElementTypes.Interface_3002 == req.getElementType()) {
			return getGEFWrapper(new Interface2CreateCommand(req));
		}
		return super.getCreateCommand(req);
	}

}
