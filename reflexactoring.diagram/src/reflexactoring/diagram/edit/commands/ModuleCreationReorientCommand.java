package reflexactoring.diagram.edit.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.emf.type.core.commands.EditElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientRelationshipRequest;

import reflexactoring.Module;
import reflexactoring.ModuleCreation;
import reflexactoring.Reflexactoring;
import reflexactoring.diagram.edit.policies.ReflexactoringBaseItemSemanticEditPolicy;

/**
 * @generated
 */
public class ModuleCreationReorientCommand extends EditElementCommand {

	/**
	 * @generated
	 */
	private final int reorientDirection;

	/**
	 * @generated
	 */
	private final EObject oldEnd;

	/**
	 * @generated
	 */
	private final EObject newEnd;

	/**
	 * @generated
	 */
	public ModuleCreationReorientCommand(ReorientRelationshipRequest request) {
		super(request.getLabel(), request.getRelationship(), request);
		reorientDirection = request.getDirection();
		oldEnd = request.getOldRelationshipEnd();
		newEnd = request.getNewRelationshipEnd();
	}

	/**
	 * @generated
	 */
	public boolean canExecute() {
		if (false == getElementToEdit() instanceof ModuleCreation) {
			return false;
		}
		if (reorientDirection == ReorientRelationshipRequest.REORIENT_SOURCE) {
			return canReorientSource();
		}
		if (reorientDirection == ReorientRelationshipRequest.REORIENT_TARGET) {
			return canReorientTarget();
		}
		return false;
	}

	/**
	 * @generated
	 */
	protected boolean canReorientSource() {
		if (!(oldEnd instanceof Module && newEnd instanceof Module)) {
			return false;
		}
		Module target = getLink().getDestination();
		if (!(getLink().eContainer() instanceof Reflexactoring)) {
			return false;
		}
		Reflexactoring container = (Reflexactoring) getLink().eContainer();
		return ReflexactoringBaseItemSemanticEditPolicy.getLinkConstraints()
				.canExistModuleCreation_4007(container, getLink(),
						getNewSource(), target);
	}

	/**
	 * @generated
	 */
	protected boolean canReorientTarget() {
		if (!(oldEnd instanceof Module && newEnd instanceof Module)) {
			return false;
		}
		Module source = getLink().getOrigin();
		if (!(getLink().eContainer() instanceof Reflexactoring)) {
			return false;
		}
		Reflexactoring container = (Reflexactoring) getLink().eContainer();
		return ReflexactoringBaseItemSemanticEditPolicy.getLinkConstraints()
				.canExistModuleCreation_4007(container, getLink(), source,
						getNewTarget());
	}

	/**
	 * @generated
	 */
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		if (!canExecute()) {
			throw new ExecutionException(
					"Invalid arguments in reorient link command"); //$NON-NLS-1$
		}
		if (reorientDirection == ReorientRelationshipRequest.REORIENT_SOURCE) {
			return reorientSource();
		}
		if (reorientDirection == ReorientRelationshipRequest.REORIENT_TARGET) {
			return reorientTarget();
		}
		throw new IllegalStateException();
	}

	/**
	 * @generated
	 */
	protected CommandResult reorientSource() throws ExecutionException {
		getLink().setOrigin(getNewSource());
		return CommandResult.newOKCommandResult(getLink());
	}

	/**
	 * @generated
	 */
	protected CommandResult reorientTarget() throws ExecutionException {
		getLink().setDestination(getNewTarget());
		return CommandResult.newOKCommandResult(getLink());
	}

	/**
	 * @generated
	 */
	protected ModuleCreation getLink() {
		return (ModuleCreation) getElementToEdit();
	}

	/**
	 * @generated
	 */
	protected Module getOldSource() {
		return (Module) oldEnd;
	}

	/**
	 * @generated
	 */
	protected Module getNewSource() {
		return (Module) newEnd;
	}

	/**
	 * @generated
	 */
	protected Module getOldTarget() {
		return (Module) oldEnd;
	}

	/**
	 * @generated
	 */
	protected Module getNewTarget() {
		return (Module) newEnd;
	}
}
