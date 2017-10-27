package com.onpositive.businessdroids.ui.dataview.persistence;

import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.themes.BasicTheme;
import com.onpositive.businessdroids.ui.themes.ITheme;

public class DataViewSavable implements ISaveable {

	private static final String MODEL_ID = "model";
	private static final String FOOTER_VISIBLE_ID = "footer_visible";
	private static final String HEADER_VISIBLE_ID = "header_visible";
	private static final String CURRENT_THEME_ID = "current_theme";
	private static final String FIRST_ELEMENT_ID = "first_element";
	private static final String FIRST_ELEMENT_Y_ID = "first_element_y";
	private static final String VIEW_STATE_ID = "view_state";
	StructuredDataView dataView;

	public DataViewSavable(StructuredDataView dataView) {
		this.dataView = dataView;
	}

	@Override
	public void save(IStore store) {
		store.putBoolean(DataViewSavable.HEADER_VISIBLE_ID,
				this.dataView.isHeaderVisible());
		store.putBoolean(DataViewSavable.FOOTER_VISIBLE_ID,
				this.dataView.isFooterVisible());
		store.putString(DataViewSavable.CURRENT_THEME_ID, this.dataView
				.getCurrentTheme().getClass().getName());
		store.putInt(DataViewSavable.FIRST_ELEMENT_ID,
				this.dataView.getFirstElement());
		store.putInt(DataViewSavable.FIRST_ELEMENT_Y_ID,
				this.dataView.getFirstElementY());
		store.putParcelable(DataViewSavable.VIEW_STATE_ID,
				this.dataView.saveInstanceState());
		TableModel tableModel = this.dataView.getTableModel();
		this.createModelSavable(tableModel).save(
				store.getOrCreateSubStore(DataViewSavable.MODEL_ID));
	}

	@Override
	public void load(IStore store) throws NoSuchElement {
		this.dataView.setHeaderVisible(store.getBoolean(
				DataViewSavable.HEADER_VISIBLE_ID, true));
		this.dataView.setFooterVisible(store.getBoolean(
				DataViewSavable.FOOTER_VISIBLE_ID, true));
		this.dataView.setFirstElement(
				store.getInt(DataViewSavable.FIRST_ELEMENT_ID, 0),
				store.getInt(DataViewSavable.FIRST_ELEMENT_Y_ID, 0));
		this.dataView.setCurrentTheme(this.loadCurrentTheme(store));
		IStore subStore = store.getSubStore(DataViewSavable.MODEL_ID);
		if (subStore != null) {
			TableModel tableModel = this.dataView.getTableModel();
			ModelSavable modelSavable = this.createModelSavable(tableModel);
			modelSavable.load(subStore);
			IFilter[] registeredFilters = modelSavable.getFilters();
			for (IFilter filter : registeredFilters) {
				if (filter instanceof IDataViewInitilizable) {
					((IDataViewInitilizable) filter).initialize(this.dataView);
				}
			}
			tableModel.setFilters(registeredFilters);
		}
		this.dataView.restoreInstanceState(store.getParcelable(
				DataViewSavable.VIEW_STATE_ID, null));
	}

	protected ModelSavable createModelSavable(TableModel tableModel) {
		return new ModelSavable(this.dataView, tableModel);
	}

	protected ITheme loadCurrentTheme(IStore store) {
		String className = store
				.getString(DataViewSavable.CURRENT_THEME_ID, "");
		if (className.length() > 0) {
			try {
				ITheme theme = (ITheme) Class.forName(className).newInstance();
				return theme;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
