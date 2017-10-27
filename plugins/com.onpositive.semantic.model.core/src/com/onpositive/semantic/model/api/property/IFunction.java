package com.onpositive.semantic.model.api.property;

import com.onpositive.semantic.model.api.meta.IHasMeta;

public interface IFunction extends IHasMeta{

	Object getValue(Object context);

}
