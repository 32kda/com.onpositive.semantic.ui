package com.onpositive.businessdroids.model.aggregation;

import com.onpositive.businessdroids.model.IArray;

public class IdentityAggregator implements IAggregator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final IAggregator INSTANCE = new IdentityAggregator();



	@Override
	public Object getAggregatedValue(IArray values) {
		Object cVal = null;
		for (int a = 0; a < values.getItemCount(); a++) {
			Object vl=values.getItem(a);
			if (vl == null) {
				continue;
			}
			if (cVal == null) {
				cVal = vl;
			} else {
				if (!cVal.equals(vl)) {
					return null;
				}
			}
		}
		return cVal;
	}



	@Override
	public String getTitle() {
		return "Identity";
	}



	@Override
	public String getId() {
		return "identity";
	}

}
