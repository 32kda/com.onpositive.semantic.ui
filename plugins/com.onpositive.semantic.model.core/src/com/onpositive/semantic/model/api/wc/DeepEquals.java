package com.onpositive.semantic.model.api.wc;

import java.util.LinkedHashSet;

import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;

public class DeepEquals {

	public static boolean isEq(Object o1, Object o2) {
		LinkedHashSet<IProperty> primaryProperties = PropertyAccess
				.getPrimaryProperties(o1);
		LinkedHashSet<IProperty> primaryProperties1 = PropertyAccess
				.getPrimaryProperties(o2);
		if (primaryProperties.size()!=primaryProperties1.size()){
			return false;
		}
		for (IProperty p0:primaryProperties){
			if (!primaryProperties1.contains(p0)){
				return false;
			}
		}
		return false;
	}

}
