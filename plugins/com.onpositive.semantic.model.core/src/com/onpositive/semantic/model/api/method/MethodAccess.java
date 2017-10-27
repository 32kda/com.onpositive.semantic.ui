package com.onpositive.semantic.model.api.method;

import java.util.Collections;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;

public class MethodAccess {
	static IEvaluatorProvider defaultProvider;
	static boolean initDefaultProvider;

	public static IEvaluatorProvider getEvaluatorProvider(Object baseObject) {
		if (baseObject == null) {
			return null;
		}
		if (baseObject instanceof IHasEvaluatorProvider) {
			IHasEvaluatorProvider p = (IHasEvaluatorProvider) baseObject;
			return p.getEvaluatorProvider();
		}
		IHasMeta meta = MetaAccess.getMeta(baseObject);
		return DefaultMetaKeys.getService(meta, IEvaluatorProvider.class);
		
	
//		if (object == null) {
//			throw new NullPointerException("TODO:No default provider yet");
//		}
//		if (object instanceof IHasPropertyProvider) {
//			IHasPropertyProvider p = (IHasPropertyProvider) object;
//			return p.getPropertyProvider();
//		}
//		IHasMeta meta = MetaAccess.getMeta(object);
//		return DefaultMetaKeys.getService(meta, IPropertyProvider.class);
	}

	public static IMethodEvaluator getMethodEvaluator(Object baseObject, String methodName) {
		IEvaluatorProvider evaluatorProvider = getEvaluatorProvider(baseObject);
		if (evaluatorProvider != null) {
			return evaluatorProvider.getMethodEvaluator(baseObject, methodName);
		}
		return null;
	}
	
	public static Iterable<IMethodEvaluator> getAvailableEvaluators(Object baseObject) {
		IEvaluatorProvider evaluatorProvider = getEvaluatorProvider(baseObject);
		if (evaluatorProvider != null)
			return evaluatorProvider.getAllMethodEvaluators(baseObject);
		return Collections.emptyList();
	}
}
