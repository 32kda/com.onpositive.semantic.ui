package com.onpositive.semantic.model.api.expressions;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.realm.IDisposable;

public class LyfecycleUtils {

	public static void markShortLyfeCycle(IHasMeta h, IDisposable d) {
		IWritableMeta m = (IWritableMeta) h.getMeta();
		m.putMeta(DefaultMetaKeys.SHORT_LYFECYCLE, true);
		m.registerService(IDisposable.class, d);
	}

	public static void disposeIfShortLyfecycle(IHasMeta meta) {
		if (meta != null) {
			boolean value = DefaultMetaKeys.getValue(meta,
					DefaultMetaKeys.SHORT_LYFECYCLE);
			if (value) {
				IDisposable service = DefaultMetaKeys.getService(meta,
						IDisposable.class);
				if (service != null) {
					service.dispose();
				}
			}
		}
	}
}
