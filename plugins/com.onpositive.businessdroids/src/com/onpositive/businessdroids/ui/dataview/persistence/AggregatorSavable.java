package com.onpositive.businessdroids.ui.dataview.persistence;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.model.aggregation.IdentityAggregator;

public class AggregatorSavable implements ISaveable {

	protected static final String AGGREG_POSTFIX_ID = "_aggreg";
	protected static final String AGGREG_PROP_POSTFIX_ID = "_aggreg_prop";
	protected static final String AGGREG_COUNT_ID = "aggreg_count";
	protected static final String NULL_ID = "null";
	protected final IColumn[] columns;

	public AggregatorSavable(IColumn[] columns) {
		this.columns = columns;
	}

	@Override
	public void save(IStore store) {
		// store.putInt(AGGREG_COUNT_ID, columns.length);
		for (IColumn column : this.columns) {
			IAggregator aggregatorUsed = column.getAggregator();
			if (aggregatorUsed != null) {
				store.putString(column.getId()
						+ AggregatorSavable.AGGREG_POSTFIX_ID, aggregatorUsed
						.getClass().getCanonicalName());
				if (aggregatorUsed instanceof ISaveable) {
					((ISaveable) aggregatorUsed)
							.save(store.getOrCreateSubStore(column.getId()
									+ AggregatorSavable.AGGREG_PROP_POSTFIX_ID));
				}
			} else {
				store.putString(column.getId(), AggregatorSavable.NULL_ID);
			}
		}
	}

	@Override
	public void load(IStore store) throws NoSuchElement {
		for (IColumn column : this.columns) {
			String className = store.getString(column.getId()
					+ AggregatorSavable.AGGREG_POSTFIX_ID, null);
			if (className != null) {
				IAggregator aggregator;
				try {
					if (IdentityAggregator.class.getName().equals(className)) {
						aggregator = IdentityAggregator.INSTANCE;
					} else {
						aggregator = (IAggregator) Class.forName(className)
								.newInstance();
					}
					if (aggregator instanceof ISaveable) {
						((ISaveable) aggregator)
								.load(store.getOrCreateSubStore(column.getId()
										+ AggregatorSavable.AGGREG_PROP_POSTFIX_ID));
					}
					column.setAggregator(aggregator);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
