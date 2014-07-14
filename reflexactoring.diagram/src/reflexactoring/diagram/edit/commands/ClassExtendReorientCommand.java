package reflexactoring.diagram.edit.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.emf.type.core.commands.EditElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientRelationshipRequest;

import reflexactoring.Class;
import reflexactoring.ClassExtend;
import reflexactoring.Reflexactoring;
import reflexactoring.diagram.edit.policies.ReflexactoringBaseItemSemanticEditPolicy;

/**
 * @generated
 */
public class ClassExtendReorientCommand extends EditElementCommand {

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
	public ClassExtendReorientCommand(ReorientRelationshipRequest request) {
		super(request.getLabel(), request.getRelationship(), request);
		reorientDirection = request.getDirection();
		oldEnd = request.getOldRelationshipEnd();
		newEnd = request.getNewRelationshipEnd();
	}

	/**
	 * @generated
	 */
	public boolean canExecute() {
		if (false == getElementToEdit() instanceof ClassExtend) {
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
		if (!(oldEnd instanceof Class && newEnd instanceof Class)) {
			return false;
		}
		Class target = getLink().getSuperClass();
		if (!(getLink().eContainer() instanceof Reflexactoring)) {
			return false;
		}
		Reflexactoring container = (Reflexactoring) getLink().eContainer();
		return ReflexactoringBaseItemSemanticEditPolicy.getLinkConstraints()
				.canExistClassExtend_4002(container, getLink(), getNewSource(),
						target);
	}

	/**
	 * @generated
	 */
	protected boolean canReorientTarget() {
		if (!(oldEnd instanceof Class && newEnd instanceof Class)) {
			return false;
		}
		Class source = getLink().getSubClass();
		if (!(getLink().eContainer() instanceof Reflexactoring)) {
			return false;
		}
		Reflexactoring container = (Reflexactoring) getLink().eContainer();
		return ReflexactoringBaseItemSemanticEditPolicy.getLinkConstraints()
				.canExistClassExtend_4002(container, getLink(), source,
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
		getLink().setSubClass(getNewSource());
		return CommandResult.newOKCommandResult(getLink());
	}

	/**
	 * @generated
	 */
	protected CommandResult reorientTarget() throws ExecutionException {
		getLink().setSuperClass(getNewTarget());
		return CommandResult.newOKCommandResult(getLink());
	}

	/**
	 * @generated
	 */
	protected ClassExtend getLink() {
		return (ClassExtend) getElementToEdit();
	}

	/**
	 * @generated
	 */
	protected Class getOldSource() {
		return (Class) oldEnd;
	}

	/**
	 * @generated
	 */
	protected Class getNewSource() {
		return (Class) newEnd;
	}

	/**
	 * @generated
	 */
	protected Class getOldTarget() {
		return (Class) oldEnd;
	}

	/**
	 * @generated
	 */
	protected Class getNewTarget() {
		return (Class) newEnd;
	}
}
