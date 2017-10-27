package com.onpositive.semantic.model.api.property;

import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.undo.IUndoableOperation;
import com.onpositive.semantic.model.api.undo.UndoMetaUtils;

public class BasicExecutableOperation implements IUndoableOperation {

	private final SimpleOneArgCommand command;
	private SimpleOneArgCommand undoCommand;
	private final AbstractWritableProperty property;
	private final Object undo;
	private final boolean silent;

	public BasicExecutableOperation(SimpleOneArgCommand m) {
		this.command = m;
		this.property = (AbstractWritableProperty) m.getOwner();
		this.undo = UndoMetaUtils.undoAllowed(m);
		Boolean boolean1 = DefaultMetaKeys.getValue(m,ICommand.META_PROPERTY_SILENTLY, Boolean.class, null);
		silent=boolean1!=null?boolean1:false;
		
	}

	public Object execute() {
		try {
			this.undoCommand = this.property.executeSimpleCommand(this.command);
		} catch (final Exception e) {
			throw new IllegalArgumentException(e.getMessage(),e);
		}
		return null;
	}

	public Object undo() {
		try {
			this.property.executeSimpleCommand(this.undoCommand);
		} catch (final Exception e) {
			throw new IllegalArgumentException(e.getMessage(),e);
		}
		return null;
	}

	public Object getTarget() {
		return this.command.getTarget();
	}

	public Object getUndoContext() {
		return this.undo;
	}

	public boolean isSilent() {
		return silent;
	}

	public SimpleOneArgCommand getUndoCommand() {
		return undoCommand;
	}
	public SimpleOneArgCommand getCommand() {
		return command;
	}
}
