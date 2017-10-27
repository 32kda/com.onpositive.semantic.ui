package com.onpositive.semantic.model.api.expressions;

import java.util.Collection;

import com.onpositive.semantic.model.api.command.DefaultCommandFactory;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.validation.ValidationAccess;

public class EditableExpressionExecutor implements ICommandExecutor,
		IHasCommandExecutor {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private IEditableExpression expr;

	@SuppressWarnings("rawtypes")
	public EditableExpressionExecutor(IEditableExpression variableExpression) {
		this.expr = variableExpression;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	
	public void execute(ICommand cmd) {
		Object value = expr.getValue();
		if (cmd instanceof SimpleOneArgCommand) {
			SimpleOneArgCommand m=(SimpleOneArgCommand) cmd;
			CodeAndMessage validate = ValidationAccess.validate(cmd);
			if (validate.isError()) {
				throw new IllegalArgumentException("Illegal command:"
						+ validate + ":" + cmd);
			}
			if (cmd.getKind().equals(ICommand.ADD_VALUE)) {
				if (value instanceof Collection){
					Collection c=(Collection<?>) value;
					c.add(m.getValue());
					expr.setValue(c);
					return;
				}
				throw new IllegalArgumentException("Illegal command:"
						+ validate + ":" + cmd);
			}
			if (cmd.getKind().equals(ICommand.REMOVE_VALUE)) {
				if (value instanceof Collection){
					Collection c=(Collection<?>) value;
					c.remove(m.getValue());
					expr.setValue(c);
					return;
				}
				throw new IllegalArgumentException("Illegal command:"
						+ validate + ":" + cmd);
			}			
			if (cmd.getKind().equals(ICommand.SET_VALUE)) {
				expr.setValue(m.getValue());
				return;
			}
			if (cmd.getKind().equals(ICommand.SET_VALUES)) {
				expr.setValue(m.getValue());
				return;
			}
		}
	}

	
	@Override
	public ICommandExecutor getCommandExecutor() {
		return this;
	}

	
	@Override
	public ICommandFactory getCommandFactory() {
		return DefaultCommandFactory.INSTANCE;
	}

}
