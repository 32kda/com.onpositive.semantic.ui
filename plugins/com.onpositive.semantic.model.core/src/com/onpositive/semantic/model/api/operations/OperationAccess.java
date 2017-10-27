package com.onpositive.semantic.model.api.operations;

import java.util.Collection;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.IPropertyProvider;

public final class OperationAccess {

	public static Collection<IOperation<?>> getOperations(IHasMeta meta,
			Object parent, Object object) {
		IOperationProvider service = DefaultMetaKeys.getService(meta, IOperationProvider.class);
		if (service==null){
			IPropertyProvider service2 = DefaultMetaKeys.getService(meta, IPropertyProvider.class);
			if (service2!=null&&service2 instanceof IOperationProvider){
				service=(IOperationProvider) service2;
			}
		}
		if (service!=null){
			return service.getGenericOperations(meta, object, object);
		}
		return null;
	}

	public static Collection<IOperation<?>> getOperations(IHasMeta meta, Object object) {
		return getOperations(meta, null, object);
	}

	public static Collection<IOperation<?>> getOperations(Object object) {
		return getOperations(MetaAccess.getMeta(object), null, object);
	}
	
	public static IOperation<?> getOperation(IHasMeta meta,
			Object parent, Object object,String id) {
		IOperationProvider service = DefaultMetaKeys.getService(meta, IOperationProvider.class);
		if (service==null){
			IPropertyProvider service2 = DefaultMetaKeys.getService(meta, IPropertyProvider.class);
			if (service2!=null&&service2 instanceof IOperationProvider){
				service=(IOperationProvider) service2;
			}
		}
		if (service!=null){
			return service.getOperation(meta, object, object,id);
		}
		return null;
	}

	public static IOperation<?> getOperation(IHasMeta meta, Object object,String id) {
		return getOperation(meta, null, object,id);
	}

	
	public static IOperation<?> getOperation(Object object,String id) {
		return getOperation(MetaAccess.getMeta(object), null, object,id);
	}
}
