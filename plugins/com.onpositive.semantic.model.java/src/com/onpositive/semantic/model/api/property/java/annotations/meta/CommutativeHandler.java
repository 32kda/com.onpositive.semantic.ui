package com.onpositive.semantic.model.api.property.java.annotations.meta;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.java.annotations.CommutativeWith;

public class CommutativeHandler implements CustomHandler<CommutativeWith>{

	@Override
	public void handle(CommutativeWith annotation, IWritableMeta meta) {
		String value = annotation.value();
		Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(meta);
		IProperty property = PropertyAccess.getProperty(subjectClass, value);
		meta.putMeta(DefaultMetaKeys.COMMUTATIVE_WITH, property);
	}

}
