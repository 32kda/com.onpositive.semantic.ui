package com.onpositive.semantic.model.api.property;

import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.InverseOfCommandPreProcessor;
import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.undo.IUndoableOperation;
import com.onpositive.semantic.model.api.undo.UndoMetaUtils;

public class DefaultExecutor implements ICommandExecutor{

	protected static final InverseOfCommandPreProcessor INVERSE_OF_COMMAND_PRE_PROCESSOR = new InverseOfCommandPreProcessor();

	// private final IChangeManager manager = UndoRedoSupport
		// .getUndoRedoChangeManager();

		public void execute(ICommand cmd) {
			boolean undo = true;
			// TODO FIXME
			undo = UndoMetaUtils.undoAllowed(cmd);
			if (!DefaultMetaKeys.getValue(cmd, DefaultMetaKeys.IGNORE_PREPROCESSORS)){
			cmd=INVERSE_OF_COMMAND_PRE_PROCESSOR.preProcess(cmd);
			}
			final IUndoableOperation op = this.convertToUndoable(cmd);
			// if (this.manager != null&&undo) {
			// this.manager.execute(op);
			// } else {
			op.execute();
			// }
		}

		private IUndoableOperation convertToUndoable(final ICommand cmd) {
			Object undoContext = null;
			undoContext = UndoMetaUtils.undoContext(cmd);
			final CompositeExecutableOperation ca = new CompositeExecutableOperation(
					undoContext);
			this.createCommands(ca, cmd);
			return ca;
		}

		protected void createCommands(CompositeExecutableOperation ca, final ICommand cm) {
			if (cm instanceof CompositeCommand) {
				final CompositeCommand cac = (CompositeCommand) cm;
				for (final ICommand m : cac) {
					this.createCommands(ca, m);
				}
				return;
			} else if (cm instanceof SimpleOneArgCommand) {
				final SimpleOneArgCommand oneArg = (SimpleOneArgCommand) cm;
				final BasicExecutableOperation exec = new BasicExecutableOperation(
						oneArg);
				ca.add(exec);
			} else {
				throw new RuntimeException();
			}
		}

}
