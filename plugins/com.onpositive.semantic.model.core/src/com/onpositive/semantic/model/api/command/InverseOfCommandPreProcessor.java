package com.onpositive.semantic.model.api.command;

import java.util.Collection;
import java.util.IdentityHashMap;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.ValueUtils;

public class InverseOfCommandPreProcessor implements ICommandPreProcessor {

	@SuppressWarnings("rawtypes")
	@Override
	public ICommand preProcess(ICommand cmd) throws CommandException {

		if (cmd instanceof SimpleOneArgCommand) {
			SimpleOneArgCommand scmd = (SimpleOneArgCommand) cmd;
			IHasCommandExecutor commandExecutor = scmd.getOwner();
			IProperty stringValue = DefaultMetaKeys.getValue(
					MetaAccess.getMeta(commandExecutor),
					DefaultMetaKeys.COMMUTATIVE_WITH,IProperty.class);
			if (stringValue != null) {
				IProperty property =stringValue;				
				CompositeCommand z = new CompositeCommand();
				z.addCommand(scmd);
				String kind = scmd.getKind();
				if (kind.equals(SimpleOneArgCommand.ADD_VALUE)) {
					z.addCommand(PropertyAccess.createAddValueCommand(
							scmd.getValue(), scmd.getTarget(), property));
				}
				if (kind.equals(SimpleOneArgCommand.REMOVE_VALUE)) {
					z.addCommand(PropertyAccess.createRemoveValueCommand(
							scmd.getValue(), scmd.getTarget(), property));
				}
				if (kind.equals(SimpleOneArgCommand.SET_VALUE)
						|| kind.equals(SimpleOneArgCommand.SET_VALUES)) {
					IProperty m=(IProperty) commandExecutor;
					Collection<Object> values2 = (Collection<Object>) PropertyAccess.getValues(m,((SimpleOneArgCommand) cmd).getTarget());
					HashDelta buildFrom = HashDelta.buildFrom(values2,ValueUtils.toCollection(((SimpleOneArgCommand) cmd).getValue()));
					for (Object o:buildFrom.getAddedElements()){
						z.addCommand(PropertyAccess.createAddValueCommand(
								o, scmd.getTarget(), property));
					}
					for (Object o:buildFrom.getRemovedElements()){
						z.addCommand(PropertyAccess.createRemoveValueCommand(
								o, scmd.getTarget(), property));
					}					
				}
				return z;
			}
		}
		if (cmd instanceof CompositeCommand) {
			CompositeCommand ccmd = (CompositeCommand) cmd;
			IdentityHashMap<ICommand, ICommand> m = null;
			for (ICommand q : ccmd) {
				ICommand preProcess = preProcess(q);
				if (preProcess != q) {
					if (m == null) {
						m = new IdentityHashMap<ICommand, ICommand>();
					}
					m.put(q, preProcess);
				}
			}
			if (m != null) {
				ccmd.replace(m);
			}
		}
		return cmd;
	}

}
