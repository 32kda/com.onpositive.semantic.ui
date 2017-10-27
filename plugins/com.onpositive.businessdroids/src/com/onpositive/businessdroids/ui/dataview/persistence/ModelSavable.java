package com.onpositive.businessdroids.ui.dataview.persistence;

import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.model.groups.IFieldGroupingCalculator;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;
import com.onpositive.businessdroids.ui.AbstractViewer;

public class ModelSavable implements ISaveable {

	private static final String GROUPING_CALCULATOR_CLASS_ID = "current_grouping_calculator_class";
	public static final String GROUPING_CALCULATOR_FIELD_ID = "current_grouping_calculator_field";
	//private static final String GROUP_FIELD_ID = "current_group_field";
	private static final String AGGREGATORS_ID = "aggregators";
	private static final String COLUMNS_ID = "columns";
	private static final String FILTERS_ID = "filers";
	protected static final String ASCENDING_ID = "ascending";
	protected static final String SORT_FIELD_ID = "sort_field";
	protected final TableModel tableModel;
	protected IFilter[] filters;

	public ModelSavable(AbstractViewer dataView, TableModel tableModel) {
		this.tableModel = tableModel;
	}

	@Override
	public void save(IStore store) {
		IField sortField = null;
		sortField = this.tableModel.getSortField();
		if (sortField != null) {
			store.putString(ModelSavable.SORT_FIELD_ID, sortField.getId());
		} else {
			store.putString(ModelSavable.SORT_FIELD_ID, "");
		}
		boolean ascending = this.tableModel.isAscendingSort();
		store.putBoolean(ModelSavable.ASCENDING_ID, ascending);
//		IField currentGroupField = this.tableModel.getCurrentGroupField();
//		String id = "";
//		if (currentGroupField != null) {
//			id = currentGroupField.getId();
//		}

		IGroupingCalculator currentGroupingCalculator = this.tableModel
				.getCurrentGroupingCalculator();
		if (currentGroupingCalculator != null) {
			store.putString(ModelSavable.GROUPING_CALCULATOR_CLASS_ID,
					currentGroupingCalculator.getClass().getName());
			if (currentGroupingCalculator instanceof IFieldGroupingCalculator) {
				store.putString(ModelSavable.GROUPING_CALCULATOR_FIELD_ID,
						((IFieldGroupingCalculator) currentGroupingCalculator).getGroupField().getId());
			}
			currentGroupingCalculator.save(store);
		}

		IStore filtersSubStore = store
				.getOrCreateSubStore(ModelSavable.FILTERS_ID);
		this.createFilterSavable().save(filtersSubStore);
		
			IStore aggregatorSubStore = store
					.getOrCreateSubStore(ModelSavable.AGGREGATORS_ID);
			new AggregatorSavable(
					this.tableModel.getColumns())
					.save(aggregatorSubStore);
			IStore columnsSubStore = store
					.getOrCreateSubStore(ModelSavable.COLUMNS_ID);
			new ColumnsSaveable( this.tableModel)
					.save(columnsSubStore);
		
	}

	@Override
	public void load(IStore store) throws NoSuchElement {
		this.tableModel.setEventsEnabled(false);
		try {
			String fieldId = store.getString(ModelSavable.SORT_FIELD_ID, "");
			boolean ascending = store.getBoolean(ModelSavable.ASCENDING_ID,
					true);
			if (fieldId.length() > 0) {
				IField sortField = this.getFieldById(fieldId);
				this.tableModel.sort(sortField, ascending);
			}
//			String groupId = store.getString(ModelSavable.GROUP_FIELD_ID, "");
//			if (groupId.length() > 0) {
//				this.tableModel.setCurrentGroupColumn(this
//						.getFieldById(groupId));
//			}

			String calculatorClassName = store.getString(
					ModelSavable.GROUPING_CALCULATOR_CLASS_ID, null);
			if (calculatorClassName != null) {
				IGroupingCalculator calculator = GropingCalculatorLoadFactory
						.load(store, calculatorClassName);
				calculator.load(store);
				String calculatorFieldId = store.getString(
						ModelSavable.GROUPING_CALCULATOR_FIELD_ID, null);
				if (calculatorFieldId != null) {
					if (!(calculator instanceof IFieldGroupingCalculator))
						throw new AssertionError("Should be IFieldGroupingCalculator to use grouping field id!");
					IField fieldById = this
							.getFieldById(calculatorFieldId);
					if (fieldById != null)
					((IFieldGroupingCalculator) calculator).setGroupField(fieldById);
				}
				//calculator.setGroupingColumn((IColumn) fieldById);
				this.tableModel.setCurrentGrouping(calculator);
			}
			

			FiltersSaveble filtersSaveble = this.createFilterSavable();
			filtersSaveble.load(store
					.getOrCreateSubStore(ModelSavable.FILTERS_ID));
			this.filters = filtersSaveble.getFilters();
			
				IStore subStore = store
						.getSubStore(ModelSavable.AGGREGATORS_ID);
				if (subStore != null) {
					new AggregatorSavable(
							(this.tableModel).getColumns())
							.load(subStore);
				}
				subStore = store.getSubStore(ModelSavable.COLUMNS_ID);
				if (subStore != null) {
					new ColumnsSaveable(this.tableModel)
							.load(subStore);
				}
			
		} finally {
			this.tableModel.setEventsEnabled(true);
		}
	}

	protected FiltersSaveble createFilterSavable() {
		return new FiltersSaveble(this.tableModel);
	}

	protected IField getFieldById(String fieldId) {
		IField field = this.tableModel.getColumnById(fieldId);
			if (field != null) {
				return field;
			}
		
		return null;
	}

	public IFilter[] getFilters() {
		return this.filters;
	}

}
