package com.onpositive.businessdroids.model.aggregation;

import java.util.HashMap;
import java.util.Map;

import com.onpositive.businessdroids.model.IArray;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;
import com.onpositive.businessdroids.ui.dataview.persistence.NoSuchElement;


public class NumericAggregator implements IAggregator, IModesProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String MODE_ID = "mode";
	public static final int SUM_MODE = 0;
	public static final int AVERAGE_MODE = 1;
	public static final int MIN_MODE = 2;
	public static final int MAX_MODE = 3;
	public static final Map<String, Integer> MODES_MAP = new HashMap<String, Integer>();

	protected int mode = NumericAggregator.SUM_MODE;

	static {
		NumericAggregator.MODES_MAP.put("Sum", NumericAggregator.SUM_MODE);
		NumericAggregator.MODES_MAP.put("Average",
				NumericAggregator.AVERAGE_MODE);
		NumericAggregator.MODES_MAP.put("Min", NumericAggregator.MIN_MODE);
		NumericAggregator.MODES_MAP.put("Max", NumericAggregator.MAX_MODE);
	}

	public NumericAggregator() {
		super();
	}

	@Override
	public Object getAggregatedValue(IArray values) {
		if (values.getItemCount() > 0) {
			switch (this.mode) {
			case SUM_MODE:
				return this.computeDoubleSum(values);
			case AVERAGE_MODE:
				boolean nulls = true;
				int count=0;
				for (Object object : values) {
					if (object != null) {
						nulls = false;
						count++;						
					}
				}
				if (nulls) {
					return null;
				}
				double sum = this.computeDoubleSum(values);
				return sum / count;
			case MIN_MODE:
				double min = Double.MAX_VALUE;
				for (Object i:values) {
					if (i == null) {
						continue;
					}
					if ((((Number) i).doubleValue()) < min) {
						min = ((Number)i).doubleValue();
					}
				}
				if (min == Double.MAX_VALUE) {
					return values.getItem(0);
				}
				return min;
			case MAX_MODE:
				double max = Double.MIN_VALUE;
				for (Object i:values) {
					if (i == null) {
						continue;
					}
					if ((((Number) i).doubleValue()) > max) {
						max = ((Number) i).doubleValue();
					}
				}
				if (max == Double.MIN_VALUE) {
					return values.getItem(0);
				}
				return max;
			default:
				throw new IllegalStateException(
						"Invalid aggregate calculation mode - " + this.mode);
			}
		}

		return 0;
	}

	protected double computeDoubleSum(IArray values) {
		double sum = 0;
		for (Object value : values) {
			if (value == null) {
				continue;
			}
			sum += ((Number) value).doubleValue();
		}
		return sum;
	}

	@Override
	public int getMode() {
		return this.mode;
	}

	@Override
	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public String getTitle() {
		return "Arithmetic";
	}

	@Override
	public Map<String, Integer> getSupportedModes() {
		return NumericAggregator.MODES_MAP;
	}

	@Override
	public void save(IStore store) {
		store.putInt(NumericAggregator.MODE_ID, this.mode);
	}

	@Override
	public void load(IStore store) throws NoSuchElement {
		this.mode = store.getInt(NumericAggregator.MODE_ID, this.mode);
	}

	@Override
	public String getId() {
		switch (mode) {
		case MAX_MODE:
			return "max";
		case MIN_MODE:
			return "min";
		case AVERAGE_MODE:
			return "ave";
		case SUM_MODE:
			return "sum";		
		default:
			break;
		}
		return null;
	}

}
