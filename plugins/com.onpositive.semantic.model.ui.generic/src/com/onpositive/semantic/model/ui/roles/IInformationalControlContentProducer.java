package com.onpositive.semantic.model.ui.roles;


public interface IInformationalControlContentProducer {

	Object create(Object owner, Object parent, Object element,
			String role, String theme, String defContent);

}
