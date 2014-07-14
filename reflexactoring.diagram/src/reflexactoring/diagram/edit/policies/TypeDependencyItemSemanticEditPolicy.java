package reflexactoring.diagram.edit.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;

import reflexactoring.diagram.providers.ReflexactoringElementTypes;

/**
 * @generated
 */
public class TypeDependencyItemSemanticEditPolicy extends
		ReflexactoringBaseItemSemanticEditPolicy {

	/**
	 * @generated
	 */
	public TypeDependencyItemSemanticEditPolicy() {
		super(ReflexactoringElementTypes.TypeDependency_4003);
	}

	/**
	 * @generated
	 */
	protected Command getDestroyElementCommand(DestroyElementRequest req) {
		return getGEFWrapper(new DestroyElementCommand(req));
	}

}
