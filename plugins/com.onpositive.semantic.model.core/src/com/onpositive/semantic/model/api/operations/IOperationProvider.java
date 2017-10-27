package com.onpositive.semantic.model.api.operations;

import java.util.Collection;

import com.onpositive.semantic.model.api.meta.IHasMeta;

public interface IOperationProvider {

	Collection<IOperation<? extends Object>> getGenericOperations(IHasMeta mt,
			Object parent, Object object);

	IOperation<?> getOperation(IHasMeta meta, Object object, Object object2,
			String id);
}
