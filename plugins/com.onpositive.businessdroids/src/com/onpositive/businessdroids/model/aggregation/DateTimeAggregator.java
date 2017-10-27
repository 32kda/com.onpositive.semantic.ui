package com.onpositive.businessdroids.model.aggregation;

import java.util.HashMap;
import java.util.Map;

import com.onpositive.businessdroids.model.IArray;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;
import com.onpositive.businessdroids.ui.dataview.persistence.NoSuchElement;

import android.text.format.Time;

public class DateTimeAggregator extends ComparingAggregator implements
		IModesProvider {

	protected static final Map<String, Integer> MODES_MAP = new HashMap<String, Integer>();
	protected static final String MODE_ID = "mode";

	static {
		DateTimeAggregator.MODES_MAP.put("Min", ComparingAggregator.MIN_MODE);
		DateTimeAggregator.MODES_MAP.put("Max", ComparingAggregator.MAX_MODE);
	}

	public DateTimeAggregator() {
		super();
	}

	@Override
	public Object getAggregatedValue(IArray values) {
		if (values.getItemCount() == 0) {
			return null;
		}

		if (Time.class.isAssignableFrom(values.getComponentType())) {

			Time res = (Time) values.getItem(0);
			for (Object i : values) {
				if (i==null){
					continue;
				}
				if (((this.mode == ComparingAggregator.MIN_MODE) && ((Time) i)
						.before(res))
						|| ((this.mode == ComparingAggregator.MAX_MODE) && ((Time) i)
								.after(res))) {
					res = (Time) i;
				}
			}
			return res;

		}
		if (Comparable.class.isAssignableFrom(values.getComponentType())) {
			return super.getAggregatedValue(values);
		}
		return null;
	}

	@Override
	public Map<String, Integer> getSupportedModes() {
		return DateTimeAggregator.MODES_MAP;
	}

	@Override
	public void save(IStore store) {
		store.putInt(DateTimeAggregator.MODE_ID, this.mode);
	}

	@Override
	public void load(IStore store) throws NoSuchElement {
		this.mode = store.getInt(DateTimeAggregator.MODE_ID, this.mode);
	}

}
