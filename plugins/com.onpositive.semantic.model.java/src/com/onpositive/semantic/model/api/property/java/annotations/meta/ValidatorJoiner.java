package com.onpositive.semantic.model.api.property.java.annotations.meta;

import com.onpositive.semantic.model.api.meta.IServiceJoiner;
import com.onpositive.semantic.model.api.validation.CompositeValidator;
import com.onpositive.semantic.model.api.validation.ICanBeStricter;
import com.onpositive.semantic.model.api.validation.IValidator;

@SuppressWarnings("rawtypes")
public final class ValidatorJoiner implements IServiceJoiner<IValidator> {
	
	public IValidator joinService(IValidator o1, IValidator o2) {
		if (o1 != null && o2 != null) {

			if (o1.equals(o2)) {
				return o1;
			}
			if (o1 instanceof ICanBeStricter && o2 instanceof ICanBeStricter) {
				ICanBeStricter so1 = (ICanBeStricter) o1;
				ICanBeStricter so2 = (ICanBeStricter) o2;
				if (so1.isStricter(so2)) {
					return o1;
				}
				if (so2.isStricter(so1)) {
					return o2;
				}
			}
		}
		// it is safe here
		if (o1 instanceof CompositeValidator) {
			CompositeValidator c = (CompositeValidator) o1;
			c.addValidator(o2);
			return c;
		}
		// Do not add anything to probaly parent meta;
		// if (o2 instanceof CompositeValidator) {
		// CompositeValidator c = (CompositeValidator) o2;
		// c.addValidator(o1);
		// return c;
		// }
		CompositeValidator vl = new CompositeValidator();
		vl.addValidator(o1);
		vl.addValidator(o2);
		return vl;
	}
}