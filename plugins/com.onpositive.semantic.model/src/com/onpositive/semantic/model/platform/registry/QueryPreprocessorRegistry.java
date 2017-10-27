package com.onpositive.semantic.model.platform.registry;

import java.util.ArrayList;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.query.DefaultQueryPreprocessorProvider;
import com.onpositive.semantic.model.api.query.IQueryPreProcessor;

public class QueryPreprocessorRegistry extends
		AbstractPlatformServiceProvider<QueryPreprocessorObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QueryPreprocessorRegistry() {
		super("com.onpositive.semantic.model.queryPreprocessor",
				QueryPreprocessorObject.class);
	}

	@SuppressWarnings("unchecked")
	public Object doResolve(IHasMeta original, Class<?> subjectClass,
			Object genericRegistryObject) {
		if (genericRegistryObject instanceof ArrayList) {
			ArrayList<?> r = (ArrayList<?>) genericRegistryObject;
			DefaultQueryPreprocessorProvider lp = new DefaultQueryPreprocessorProvider();
			for (Object q : r) {
				QueryPreprocessorObject m = (QueryPreprocessorObject) q;
				lp.add((IQueryPreProcessor) m.getProvider());
			}
			map.put(subjectClass, lp);
			return lp;
		} else {
			DefaultQueryPreprocessorProvider lp = new DefaultQueryPreprocessorProvider();
			lp.add((IQueryPreProcessor) ((QueryPreprocessorObject) genericRegistryObject)
					.getProvider());
			map.put(subjectClass, lp);
			return lp;
		}
	}
}
