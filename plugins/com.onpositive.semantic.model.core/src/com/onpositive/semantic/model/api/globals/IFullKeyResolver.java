package com.onpositive.semantic.model.api.globals;

public interface IFullKeyResolver extends IKeyResolver{

	Object resolveKey(IKey orig);

	boolean isReallyFullKey();
}
