package com.onpositive.semantic.model.java.tests;

import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;
import com.onpositive.semantic.model.api.property.java.annotations.Validator;

public class ItemRange<T> {

	@Validator(value= "this<=$.max",message="Min value should be less then max")
	@TextLabel("this!=null?(this):('any')")
	Comparable<T> min;

	@Validator(value="this>=$.min",message="Max value should be greater then min value")
	@TextLabel("this!=null?(this):('any')")
	Comparable<T> max;
}
