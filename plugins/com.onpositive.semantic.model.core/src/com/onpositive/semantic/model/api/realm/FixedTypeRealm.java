package com.onpositive.semantic.model.api.realm;

import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;

public class FixedTypeRealm<T> extends Realm<T> implements IHasCommandExecutor,
		ICommandExecutor {

	public FixedTypeRealm(Class<T> type) {
		super();
		this.type = type;
		registerService(IHasCommandExecutor.class, this);
		ObjectChangeManager.registerRealm(this);
		putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, type);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final Class<T> type;

	public Class<T> getType() {
		return type;
	}

	@Override
	public void dispose() {
		ObjectChangeManager.unregisterRealm(this);
		super.dispose();
	}
}