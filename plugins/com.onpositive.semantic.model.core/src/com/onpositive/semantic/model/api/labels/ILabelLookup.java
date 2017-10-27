package com.onpositive.semantic.model.api.labels;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IService;


public interface ILabelLookup extends IService {

	public Object lookUpByLabel(IHasMeta model, Object parentObject,String label)
			throws NotFoundException;
}
