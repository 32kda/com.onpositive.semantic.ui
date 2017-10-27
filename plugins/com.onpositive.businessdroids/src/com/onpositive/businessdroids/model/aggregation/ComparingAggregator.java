package com.onpositive.businessdroids.model.aggregation;

import com.onpositive.businessdroids.model.IArray;

public class ComparingAggregator implements IAggregator {

	public static final int MIN_MODE = 0;
	public static final int MAX_MODE = 1;

	protected int mode = ComparingAggregator.MIN_MODE;

	public ComparingAggregator() {
		super();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getAggregatedValue(IArray values) {
		if ((values.getItemCount() > 0)
				&& (Comparable.class
						.isAssignableFrom(values.getComponentType()))) {
			Comparable<?> res = (Comparable<?>) values.getItem(0);
			for (Object i : values) {
				if (i == null) {
					continue;
				}
				if (res == null) {
					res = (Comparable<?>) i;
				}
				if (((this.mode == ComparingAggregator.MIN_MODE) && (((Comparable) i)
						.compareTo(res) < 0))
						|| ((this.mode == ComparingAggregator.MAX_MODE) && (((Comparable) i)
								.compareTo(res) > 0))) {
					res = (Comparable<?>) i;
				}
			}
			return res;
		}
		return null;
	}

	public int getMode() {
		return this.mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public String getTitle() {
		return "Comparing";
	}

	@Override
	public String getId() {
		switch (mode) {
		case MIN_MODE:
			return "min";
		case MAX_MODE:
			return "max";
		default:
			break;
		}
		return null;
	}

}
