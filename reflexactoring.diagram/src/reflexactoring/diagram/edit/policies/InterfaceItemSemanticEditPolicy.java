package reflexactoring.diagram.edit.policies;

import java.util.Iterator;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.core.commands.DeleteCommand;
import org.eclipse.gmf.runtime.emf.commands.core.command.CompositeTransactionalCommand;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientRelationshipRequest;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;

import reflexactoring.diagram.edit.commands.ImplementCreateCommand;
import reflexactoring.diagram.edit.commands.ImplementReorientCommand;
import reflexactoring.diagram.edit.commands.InterfaceExtendCreateCommand;
import reflexactoring.diagram.edit.commands.InterfaceExtendReorientCommand;
import reflexactoring.diagram.edit.commands.TypeDependencyCreateCommand;
import reflexactoring.diagram.edit.commands.TypeDependencyReorientCommand;
import reflexactoring.diagram.edit.parts.ImplementEditPart;
import reflexactoring.diagram.edit.parts.InterfaceExtendEditPart;
import reflexactoring.diagram.edit.parts.TypeDependencyEditPart;
import reflexactoring.diagram.part.ReflexactoringVisualIDRegistry;
import reflexactoring.diagram.providers.ReflexactoringElementTypes;

/**
 * @generated
 */
public class InterfaceItemSemanticEditPolicy extends
		ReflexactoringBaseItemSemanticEditPolicy {

	/**
	 * @generated
	 */
	public InterfaceItemSemanticEditPolicy() {
		super(ReflexactoringElementTypes.Interface_2002);
	}

	/**
	 * @generated
	 */
	protected Command getDestroyElementCommand(DestroyElementRequest req) {
		View view = (View) getHost().getModel();
		CompositeTransactionalCommand cmd = new CompositeTransactionalCommand(
				getEditingDomain(), null);
		cmd.setTransactionNestingEnabled(false);
		for (Iterator<?> it = view.getTargetEdges().iterator(); it.hasNext();) {
			Edge incomingLink = (Edge) it.next();
			if (ReflexactoringVisualIDRegistry.getVisualID(incomingLink) == TypeDependencyEditPart.VISUAL_ID) {
				DestroyElementRequest r = new DestroyElementRequest(
						incomingLink.getElement(), false);
				cmd.add(new DestroyElementCommand(r));
				cmd.add(new DeleteCommand(getEditingDomain(), incomingLink));
				continue;
			}
			if (ReflexactoringVisualIDRegistry.getVisualID(incomingLink) == InterfaceExtendEditPart.VISUAL_ID) {
				DestroyElementRequest r = new DestroyElementRequest(
						incomingLink.getElement(), false);
				cmd.add(new DestroyElementCommand(r));
				cmd.add(new DeleteCommand(getEditingDomain(), incomingLink));
				continue;
			}
			if (ReflexactoringVisualIDRegistry.getVisualID(incomingLink) == ImplementEditPart.VISUAL_ID) {
				DestroyElementRequest r = new DestroyElementRequest(
						incomingLink.getElement(), false);
				cmd.add(new DestroyElementCommand(r));
				cmd.add(new DeleteCommand(getEditingDomain(), incomingLink));
				continue;
			}
		}
		for (Iterator<?> it = view.getSourceEdges().iterator(); it.hasNext();) {
			Edge outgoingLink = (Edge) it.next();
			if (ReflexactoringVisualIDRegistry.getVisualID(outgoingLink) == TypeDependencyEditPart.VISUAL_ID) {
				DestroyElementRequest r = new DestroyElementRequest(
						outgoingLink.getElement(), false);
				cmd.add(new DestroyElementCommand(r));
				cmd.add(new DeleteCommand(getEditingDomain(), outgoingLink));
				continue;
			}
			if (ReflexactoringVisualIDRegistry.getVisualID(outgoingLink) == InterfaceExtendEditPart.VISUAL_ID) {
				DestroyElementRequest r = new DestroyElementRequest(
						outgoingLink.getElement(), false);
				cmd.add(new DestroyElementCommand(r));
				cmd.add(new DeleteCommand(getEditingDomain(), outgoingLink));
				continue;
			}
		}
		EAnnotation annotation = view.getEAnnotation("Shortcut"); //$NON-NLS-1$
		if (annotation == null) {
			// there are indirectly referenced children, need extra commands: false
			addDestroyShortcutsCommand(cmd, view);
			// delete host element
			cmd.add(new DestroyElementCommand(req));
		} else {
			cmd.add(new DeleteCommand(getEditingDomain(), view));
		}
		return getGEFWrapper(cmd.reduce());
	}

	/**
	 * @generated
	 */
	protected Command getCreateRelationshipCommand(CreateRelationshipRequest req) {
		Command command = req.getTarget() == null ? getStartCreateRelationshipCommand(req)
				: getCompleteCreateRelationshipCommand(req);
		return command != null ? command : super
				.getCreateRelationshipCommand(req);
	}

	/**
	 * @generated
	 */
	protected Command getStartCreateRelationshipCommand(
			CreateRelationshipRequest req) {
		if (ReflexactoringElementTypes.TypeDependency_4003 == req
				.getElementType()) {
			return getGEFWrapper(new TypeDependencyCreateCommand(req,
					req.getSource(), req.getTarget()));
		}
		if (ReflexactoringElementTypes.InterfaceExtend_4004 == req
				.getElementType()) {
			return getGEFWrapper(new InterfaceExtendCreateCommand(req,
					req.getSource(), req.getTarget()));
		}
		if (ReflexactoringElementTypes.Implement_4005 == req.getElementType()) {
			return null;
		}
		return null;
	}

	/**
	 * @generated
	 */
	protected Command getCompleteCreateRelationshipCommand(
			CreateRelationshipRequest req) {
		if (ReflexactoringElementTypes.TypeDependency_4003 == req
				.getElementType()) {
			return getGEFWrapper(new TypeDependencyCreateCommand(req,
					req.getSource(), req.getTarget()));
		}
		if (ReflexactoringElementTypes.InterfaceExtend_4004 == req
				.getElementType()) {
			return getGEFWrapper(new InterfaceExtendCreateCommand(req,
					req.getSource(), req.getTarget()));
		}
		if (ReflexactoringElementTypes.Implement_4005 == req.getElementType()) {
			return getGEFWrapper(new ImplementCreateCommand(req,
					req.getSource(), req.getTarget()));
		}
		return null;
	}

	/**
	 * Returns command to reorient EClass based link. New link target or source
	 * should be the domain model element associated with this node.
	 * 
	 * @generated
	 */
	protected Command getReorientRelationshipCommand(
			ReorientRelationshipRequest req) {
		switch (getVisualID(req)) {
		case TypeDependencyEditPart.VISUAL_ID:
			return getGEFWrapper(new TypeDependencyReorientCommand(req));
		case InterfaceExtendEditPart.VISUAL_ID:
			return getGEFWrapper(new InterfaceExtendReorientCommand(req));
		case ImplementEditPart.VISUAL_ID:
			return getGEFWrapper(new ImplementReorientCommand(req));
		}
		return super.getReorientRelationshipCommand(req);
	}

}
