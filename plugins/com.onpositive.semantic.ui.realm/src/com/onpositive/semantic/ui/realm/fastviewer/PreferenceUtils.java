package com.onpositive.semantic.ui.realm.fastviewer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class PreferenceUtils {

	public static void persist(final IPreferenceStore store, final Table table,
			final String key) {
		PreferenceUtils.restoreSettingsFromPreferenceStore(store, key, table);
		table.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				PreferenceUtils.persistSettingsToPreferenceStore(store, key,
						table);
			}

		});
	}

	private static String getColumnKey(String key, int num) {
		return key + ".column." + num; //$NON-NLS-1$
	}

	public static void persistSettingsToPreferenceStore(IPreferenceStore store,
			String key, Table table) {
		if (true) {
			TableColumn[] columns = table.getColumns();
			for (int a = 0; a < columns.length; a++) {
				int width = columns[a].getWidth();
				store.setValue(getColumnKey(key, a), width);
			}
		}
	}

	/**
	 * restores columns widthes, from preferences, also ecranizes first layout
	 * in case when table has TableLayoutManager
	 * 
	 * @param store
	 * @param key
	 * @param table
	 */
	public static void restoreSettingsFromPreferenceStore(
			final IPreferenceStore store, final String key, final Table table) {
		final TableColumn[] columns = table.getColumns();
		actuallyRestore(store, key, columns);
		// if (table.getParent().getLayout() instanceof TableColumnLayout &&
		// found) {
		// final TableColumnLayout ll = (TableColumnLayout) table.getLayout();
		// table.getParent().setLayout(null);
		// table.addControlListener(new ControlListener() {
		//
		// int inc = 0;
		//
		// public void controlMoved(ControlEvent e) {
		// }
		//
		// public void controlResized(ControlEvent e) {
		// if (inc == 1) {
		// table.getParent().setLayout(ll);
		// }
		// inc++;
		// }
		//
		// });
		// }
	}

	private static boolean actuallyRestore(IPreferenceStore store, String key,
			TableColumn[] columns) {
		if (true){
		boolean found = false;
		for (int a = 0; a < columns.length; a++) {
			int int1 = store.getInt(getColumnKey(key, a));
			if (int1 != 0) {
				found = true;
				columns[a].setWidth(int1);
			}
		}
		return found;
		}
		return false;		
	}

	public static void persistSettings(IPreferenceStore preferenceStore,
			String oldId, Table control) {
		PreferenceUtils.persistSettingsToPreferenceStore(preferenceStore,
				oldId, control);

	}
}
