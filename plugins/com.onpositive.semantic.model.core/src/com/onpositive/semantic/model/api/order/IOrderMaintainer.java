package com.onpositive.semantic.model.api.order;

import java.util.Comparator;

public interface IOrderMaintainer extends Comparator<Object>{

	

	public boolean canMove(Object obj,boolean direction);

	
}
