package com.onpositive.semantic.model.api.undo;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.property.PropertyAccess;

public class UndoMetaUtils {

	public static final String UNDO_ALLOWED = "IS_UNDO_ALLOWED";
	public static final String UNDO_CONTEXT = "UNDO_CONTEXT";
	private static IUndoManager undoRedoChangeManager;

	public static boolean undoAllowed(IHasMeta meta) {
		Boolean boolean1 = meta.getMeta().getSingleValue(UNDO_ALLOWED, Boolean.class, null);
		if (boolean1 == null) {
			return false;
		}
		return boolean1;
	}

	public static Object undoContext(IHasMeta meta) {
		Object context = meta.getMeta().getSingleValue(UNDO_CONTEXT, Object.class, null);
		return context;
	}

	public static void markUndoable(IHasMeta meta, boolean undoable) {
		String undoAllowed = UNDO_ALLOWED;
		setMeta(meta, undoable, undoAllowed);
	}

	public static void setMeta(IHasMeta meta, Object undoable,
			String undoAllowed) {
		IMeta meta2 = meta.getMeta();		
		if (meta2 instanceof IWritableMeta) {
			IWritableMeta m=(IWritableMeta) meta2;
			m.putMeta(undoAllowed, undoable);
			return;
		}
		PropertyAccess.setValue(undoAllowed,meta2, undoable);
	}
	
	public static IUndoManager getUndoManager(){
		return undoRedoChangeManager;		
	}

	public static void setUndoContext(IHasMeta meta, Object undoContext) {
		setMeta(meta, undoContext, UNDO_CONTEXT);
	}

	public static void setUndoManager(IUndoManager undoRedoChangeManager) {
		UndoMetaUtils.undoRedoChangeManager = undoRedoChangeManager;
	}
}
