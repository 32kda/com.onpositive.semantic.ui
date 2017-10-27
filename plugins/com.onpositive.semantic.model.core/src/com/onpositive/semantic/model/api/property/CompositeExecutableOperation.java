package com.onpositive.semantic.model.api.property;

import java.util.ArrayList;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.undo.IUndoableOperation;

public class CompositeExecutableOperation implements
		IUndoableOperation {

	private final ArrayList<BasicExecutableOperation> ops = new ArrayList<BasicExecutableOperation>();
	private final Object object;

	public CompositeExecutableOperation(Object undoContext) {
		this.object = undoContext;
	}

	public void add(BasicExecutableOperation op) {
		this.ops.add(op);
	}

	static void processDelta(ArrayList<BasicExecutableOperation> ops,
			boolean undo) {
		final HashDelta<Object> dlt = new HashDelta<Object>();
		CompositeCommand cc = new CompositeCommand();
		for (final BasicExecutableOperation o : ops) {

			final Object target = o.getTarget();
			if (!o.isSilent()) {
				dlt.markChanged(target);
			}
			cc.addCommand(undo?o.getUndoCommand():o.getCommand());
		}
		if (!dlt.isEmpty()) {
			ObjectChangeManager.fireExternalDelta(dlt,cc);
		}
	}

	public Object execute() {
		for (final IUndoableOperation op : this.ops) {
			op.execute();
		}
		processDelta(this.ops, false);
		return null;
	}

	public Object undo() {
		for (final IUndoableOperation op : this.ops) {
			op.undo();
		}
		processDelta(this.ops, true);
		return null;
	}

	public Object getUndoContext() {
		return this.object;
	}	
}
