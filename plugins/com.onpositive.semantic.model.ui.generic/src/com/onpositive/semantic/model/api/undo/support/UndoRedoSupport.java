package com.onpositive.semantic.model.api.undo.support;

import com.onpositive.core.runtime.Bundle;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.undo.IBasicUndoProvider;
import com.onpositive.semantic.model.api.undo.IUndoManager;
import com.onpositive.semantic.model.api.undo.IUndoableOperation;
import com.onpositive.semantic.model.api.undo.IUndoProvider;
import com.onpositive.semantic.model.api.undo.UndoMetaUtils;


//TODO FIXME
public class UndoRedoSupport {

	protected static IBasicUndoProvider manager;

	protected UndoRedoSupport() {
	}

	static {
		try{
		final Bundle bundle = Platform.getBundle("com.onpositive.semantic.ui.workbench"); //$NON-NLS-1$
		
		if (bundle != null) {
			try {
				final Class<?> loadClass = bundle
						.loadClass("com.onpositive.semantic.ui.workbench.providers.WorkbenchUndoRedoProvider"); //$NON-NLS-1$
				try {
					manager = (IBasicUndoProvider) loadClass.newInstance();
				} 
				catch (final IllegalStateException e){
					manager=new IBasicUndoProvider() {
						
						public IUndoManager getUndoChangeManager() {
							return new IUndoManager() {
								
								public Object execute(IUndoableOperation undoable) {
									undoable.execute();
									return null;
								}
							};
						}
					};
				}
				catch (final InstantiationException e) {
					Platform.log(e);
					throw new RuntimeException();
				} catch (final IllegalAccessException e) {
					Platform.log(e);
				}
			} catch (final ClassNotFoundException e) {
				Platform.log(e);
			}
		}
		}catch (Throwable e) {
			Platform.log(e);
		}
		UndoMetaUtils.setUndoManager(getUndoRedoChangeManager());
	}

	public static IUndoManager getUndoRedoChangeManager() {
		initManagerIfNeeded();
		if (manager == null) {
			return null;
		}
		return manager.getUndoChangeManager();
	}

	private static void initManagerIfNeeded() {
		if (manager == null) {
			manager = (IBasicUndoProvider) Platform.getAdapter( IBasicUndoProvider.class, IBasicUndoProvider.class );
		}
	}

	public static Object createUndoAction() {
		final Object undoAction = ((IUndoProvider) UndoRedoSupport
				.getUndoRedoChangeManager()).createUndoAction();
		return undoAction;
	}

	public static Object createRedoAction() {
		return ((IUndoProvider) UndoRedoSupport.getUndoRedoChangeManager())
				.createRedoAction();
	}

	public static Object getDefaultContext() {
		IUndoManager undoRedoChangeManager = UndoRedoSupport.getUndoRedoChangeManager();
		if (undoRedoChangeManager==null){
			return null;
		}
		return ((IUndoProvider) undoRedoChangeManager)
				.getGlobalUndoContext();
	}
}
