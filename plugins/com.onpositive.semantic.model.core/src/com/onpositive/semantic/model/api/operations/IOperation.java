package com.onpositive.semantic.model.api.operations;

import java.util.Collection;
import java.util.Map;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public interface IOperation<T> extends IHasMeta {

	String getId();
	
	public boolean isEnabledFor(Collection<T> document);

	public CodeAndMessage executeOn(Collection<T> document,
			Map<IHasMeta, Object> args);

	public IHasMeta getArguments(Collection<T> selection);
	
}
