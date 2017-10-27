package com.onpositive.semantic.model.api.status;

import java.io.Serializable;


public interface IStatusChangeListener extends Serializable{

	void statusChanged(IHasStatus bnd, CodeAndMessage cm);
}
