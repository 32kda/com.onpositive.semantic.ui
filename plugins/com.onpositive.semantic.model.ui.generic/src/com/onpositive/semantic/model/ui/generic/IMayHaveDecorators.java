package com.onpositive.semantic.model.ui.generic;

import com.onpositive.commons.xml.language.ChildSetter;
import com.onpositive.semantic.model.api.decoration.IObjectDecorator;

public interface IMayHaveDecorators<T> {

	@ChildSetter( value="decorator", needCasting=false )
	public abstract boolean addDecorator(IObjectDecorator e);

	public abstract boolean removeDecorator(IObjectDecorator o);

}