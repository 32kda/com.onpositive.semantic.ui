package com.onpositive.semantic.model.api.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.globals.GlobalAccess;
import com.onpositive.semantic.model.api.globals.IKey;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.undo.IUndoManager;
import com.onpositive.semantic.model.api.undo.IUndoableOperation;
import com.onpositive.semantic.model.api.undo.UndoMetaUtils;
import com.onpositive.semantic.model.api.validation.DefaultValidationContext;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.api.validation.IterableValidator;

public class CompositeCommand extends BaseMeta implements ICommand,
		Iterable<ICommand>, IHasCommandExecutor, IUndoableOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected ArrayList<ICommand> cmds;

	private static final BaseMeta defaultCommandMeta = new BaseMeta();

	static {
		defaultCommandMeta.registerService(IValidator.class,
				new IterableValidator());
		defaultCommandMeta.putMeta(IValidationContext.DEEP_VALIDATION, false);
	}

	protected CompositeCommand(ArrayList<ICommand> undo) {
		this.cmds = undo;
		this.parentMeta = defaultCommandMeta;
	}

	public CompositeCommand() {
		this.cmds = new ArrayList<ICommand>();
		this.parentMeta = defaultCommandMeta;
	}

	public void addCommand(ICommand c) {
		this.cmds.add(c);
	}

	public void addCommands(ICommand... c) {
		final List<ICommand> asList = Arrays.asList(c);
		this.cmds.addAll(asList);
	}

	public static CompositeCommand create(ICommand... commands) {
		final CompositeCommand cm = new CompositeCommand();
		cm.addCommands(commands);
		return cm;
	}

	@Override
	public Iterator<ICommand> iterator() {
		return this.cmds.iterator();
	}

	public boolean isEmpty() {
		return this.cmds.isEmpty();
	}

	public void clear() {
		cmds.clear();
	}

	@Override
	public String getKind() {
		return ICommand.COMPOSITE;
	}

	public static void processDelta(HashDelta<Object> dlt, ICommand cmd) {
		if (cmd instanceof CompositeCommand) {
			for (final ICommand c : (CompositeCommand) cmd) {
				processDelta(dlt, c);
			}
		} else if (cmd instanceof SimpleOneArgCommand) {
			final SimpleOneArgCommand ss = (SimpleOneArgCommand) cmd;
			final Object target = ss.getTarget();
			dlt.markChanged(target);
		}
	}

	static synchronized void processDelta(ICommand cmd) {
		final HashDelta<Object> dlt = new HashDelta<Object>();
		processDelta(dlt, cmd);
		ObjectChangeManager.fireExternalDelta(dlt, cmd);
	}

	static ICommandExecutor iCommandExecutor = new ICommandExecutor() {

		private static final long serialVersionUID = 1L;

		@Override
		public void execute(ICommand cmd) {
			CompositeCommand c = (CompositeCommand) cmd;
			HashMap<ICommandExecutor, CompositeCommand> z = new HashMap<ICommandExecutor, CompositeCommand>();
			ICommandExecutor ex = this;
			for (ICommand m : c) {
				ICommandExecutor commandExecutor = m.getCommandExecutor();
				if (ex == this) {
					ex = commandExecutor;
				} else if (ex != commandExecutor) {
					ex = null;
				}
				CompositeCommand compositeCommand = z.get(commandExecutor);
				if (compositeCommand == null) {
					if (m instanceof CompositeCommand) {
						z.put(commandExecutor, (CompositeCommand) m);
					} else {
						CompositeCommand aaa = new CompositeCommand();
						aaa.addCommand(m);
						z.put(commandExecutor, aaa);
					}
				} else {
					compositeCommand.addCommand(c);
				}
			}
			if (ex != null && ex != iCommandExecutor) {
				ex.execute(c);
			} else {
				for (ICommandExecutor m : z.keySet()) {
					m.execute(z.get(m));
				}
			}
		}
	};

	@Override
	public ICommandExecutor getCommandExecutor() {
		IHasCommandExecutor service = getService(IHasCommandExecutor.class);
		if (service != null && service != this) {
			return service.getCommandExecutor();
		}
		ICommandExecutor services = getService(ICommandExecutor.class);
		if (services != null && services != this) {
			return services;
		}
		return iCommandExecutor;
	}

	@Override
	public IHasCommandExecutor getOwner() {
		return this;
	}

	@Override
	public ICommandFactory getCommandFactory() {
		ICommandFactory service = getService(ICommandFactory.class);
		if (service == null) {
			service = DefaultCommandFactory.INSTANCE;
		}
		return service;
	}

	@Override
	public IValidationContext getValidationContext() {
		return new DefaultValidationContext(this);
	}

	public static ICommand restoreFromRemoteForm(ICommand cmd) {
		if (cmd instanceof CompositeCommand) {
			CompositeCommand z = (CompositeCommand) cmd;
			CompositeCommand mm = new CompositeCommand();
			for (ICommand q : z) {
				mm.addCommand(restoreFromRemoteForm(q));
			}
			return mm;
		}
		if (cmd instanceof SimpleOneArgCommand) {
			SimpleOneArgCommand qq = (SimpleOneArgCommand) cmd;
			Object target = qq.getTarget();
			String kind = qq.getKind();
			Object value = qq.getValue();
			IHasCommandExecutor owner = qq.getOwner();
			target = uncleanupObject(target);
			value = uncleanupObject(value);
			ProxyExecutor x = (ProxyExecutor) owner;
			owner = (IHasCommandExecutor) GlobalAccess.getGlobal(GlobalAccess
					.stringToKey(x.id));
			SimpleOneArgCommand simpleOneArgCommand = new SimpleOneArgCommand(
					target, value, kind.intern(), owner);
			return simpleOneArgCommand;
		}
		throw new IllegalArgumentException();
	}

	public static ICommand transferToRemoteForm(ICommand cmd) {
		if (cmd instanceof CompositeCommand) {
			CompositeCommand z = (CompositeCommand) cmd;
			CompositeCommand mm = new CompositeCommand();
			for (ICommand q : z) {
				mm.addCommand(transferToRemoteForm(q));
			}
			Class<?> value = cmd.getMeta().getSingleValue(
					DefaultMetaKeys.SUBJECT_CLASS_KEY, Class.class, null);
			mm.putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, value);
			return mm;
		}
		if (cmd instanceof SimpleOneArgCommand) {
			SimpleOneArgCommand qq = (SimpleOneArgCommand) cmd;
			Object target = qq.getTarget();
			String kind = qq.getKind();
			Object value = qq.getValue();
			IHasCommandExecutor owner = qq.getOwner();
			target = cleanupObject(target);
			value = qq.getKind().equals(SimpleOneArgCommand.ADD_VALUE) ? value
					: cleanupObject(value);
			if (owner != null)
				owner = new ProxyExecutor(GlobalAccess.getKey(owner).toString());
			SimpleOneArgCommand simpleOneArgCommand = new SimpleOneArgCommand(
					target, value, kind, owner);
			((BaseMeta) simpleOneArgCommand.getMeta()).copyFrom(qq.getMeta());
			return simpleOneArgCommand;
		}
		throw new IllegalArgumentException();
	}

	private static Object uncleanupObject(Object target) {
		if (target instanceof IKey) {
			return GlobalAccess.getGlobal((IKey) target);
		}
		return target;
	}

	private static Object cleanupObject(Object target) {
		IKey key = GlobalAccess.getKey(target);
		if (key != null) {
			return key;
		}
		return target;
	}

	public void setSilent() {
		putMeta(ICommand.META_PROPERTY_SILENTLY, true);
	}

	public boolean isSilent() {
		return DefaultMetaKeys.getValue(this, ICommand.META_PROPERTY_SILENTLY);
	}
	
	

	public void replace(IdentityHashMap<ICommand, ICommand> m) {
		ArrayList<ICommand> nl = new ArrayList<ICommand>();
		for (ICommand mq : cmds) {
			if (m.containsKey(mq)) {
				ICommand iCommand = m.get(mq);
				if (iCommand != null) {
					nl.add(iCommand);
				}
			} else {
				nl.add(mq);
			}
		}
		cmds = nl;
	}

	public void setNoPreprocessors() {
		putMeta(DefaultMetaKeys.IGNORE_PREPROCESSORS, true);
	}

	protected ICommand undoCommand;

	
	public void run(){
		if(UndoMetaUtils.undoAllowed(this)){
			IUndoManager m=UndoMetaUtils.getUndoManager();
			undoCommand=createUndoCommand(this);
			m.execute(this);							
		}
		else{
			execute();
		}
	}
	
	@Override
	public Object execute() {
		try {
			getCommandExecutor().execute(this);
//			if (!isSilent()){
//				processDelta(this);
//			}
			return CodeAndMessage.OK_MESSAGE;
		} catch (Exception e) {
			return CodeAndMessage.errorMessage(e);
		}
	}

	private ICommand createUndoCommand(CompositeCommand compositeCommand) {
		CompositeCommand c=new CompositeCommand();
		c.metaInfo=compositeCommand.metaInfo;
		for (ICommand m:compositeCommand.cmds){
			if (m instanceof CompositeCommand){
				CompositeCommand z=(CompositeCommand) m;
				c.addCommand(createUndoCommand(z));
			}
			else{
				SimpleOneArgCommand mq=(SimpleOneArgCommand) m;
				c.addCommand(mq.createUndoCommand());
			}
		}
		return c;
	}

	@Override
	public Object undo() {
		if (undoCommand == null) {
			throw new IllegalStateException();
		}
		try {
			getCommandExecutor().execute(undoCommand);
			return CodeAndMessage.OK_MESSAGE;
		} catch (Exception e) {
			return CodeAndMessage.errorMessage(e);
		}
	}

	@Override
	public Object getUndoContext() {
		return UndoMetaUtils.undoContext(this);
	}
	
	

}