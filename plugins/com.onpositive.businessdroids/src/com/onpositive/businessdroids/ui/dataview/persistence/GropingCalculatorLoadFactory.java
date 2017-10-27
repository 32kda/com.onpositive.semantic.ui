package com.onpositive.businessdroids.ui.dataview.persistence;

import com.onpositive.businessdroids.model.groups.IGroupingCalculator;
import com.onpositive.businessdroids.model.groups.NumericRangeGroupingCalculator;
import com.onpositive.businessdroids.model.groups.RoughDateGroupingCalculator;

public class GropingCalculatorLoadFactory {

	public static IGroupingCalculator load(IStore store,
			String calculatorClassName) {
		if (NumericRangeGroupingCalculator.class.getName().equals(
				calculatorClassName)) {
			return new NumericRangeGroupingCalculator(null, 0);
		} else if (RoughDateGroupingCalculator.class.getName().equals(
				calculatorClassName)) {
			return new RoughDateGroupingCalculator(null);
		}
		try {
			return (IGroupingCalculator) Class.forName(calculatorClassName).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}

}
