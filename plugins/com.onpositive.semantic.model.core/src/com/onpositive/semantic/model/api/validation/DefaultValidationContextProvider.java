package com.onpositive.semantic.model.api.validation;

import java.util.Iterator;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;

public final class DefaultValidationContextProvider implements
		IValidationContextProvider {

	@Override
	public Iterable<IValidationContext> getNestedContexts(
			final IValidationContext context) {
		final Iterable<IProperty> properties = context.getMeta()
				.getService(IPropertyProvider.class)
				.getProperties(context.getValue());
		if (properties != null) {
			Iterable<IValidationContext> m = new Iterable<IValidationContext>() {

				
				@Override
				public Iterator<IValidationContext> iterator() {
					return new Iterator<IValidationContext>() {

						Iterator<IProperty> it = properties.iterator();

						
						@Override
						public boolean hasNext() {
							return it.hasNext();
						}

						
						@Override
						public IValidationContext next() {
							final IProperty next = it.next();
							IHasMeta m = next;
							Object value2 = next.getValue(context.getValue());
							if (value2 != null) {
								IWritableMeta cm = ((IHasMeta) next).getMeta()
										.getWritableCopy();
								cm.putMeta(IValidationContext.DEEP_VALIDATION, cm.getSingleValue(IValidationContext.DEEP_VALIDATION, Boolean.class, true));
								cm.setDefaultMeta(MetaAccess.getMeta(value2).getMeta());
								
								m=cm;
							}
							return new DefaultValidationContext(value2,
									context, m);
						}

						
						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				}
			};
			return m;
		}
		return DefaultValidationContext.NO_CONTEXT;
	}

	
	@Override
	public IValidationContext getValidationContext(Object object) {
		return new DefaultValidationContext(object);
	}
}