package com.onpositive.semantic.model.data;

import java.util.Map;



public interface IInstanceListener {

	public Map<IEntry,Object> processDelta(Object obj,Object delta);
}
