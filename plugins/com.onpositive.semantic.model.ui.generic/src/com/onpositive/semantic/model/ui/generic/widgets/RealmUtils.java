package com.onpositive.semantic.model.ui.generic.widgets;

import java.util.Collection;
import java.util.List;

import com.onpositive.semantic.model.api.expressions.LyfecycleUtils;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.ObjectListeningRealm;
import com.onpositive.semantic.model.api.realm.OrderedRealm;
import com.onpositive.semantic.model.api.realm.Realm;
import com.onpositive.semantic.model.binding.IBinding;

public class RealmUtils {

	@SuppressWarnings("unchecked")
	public static IRealm<Object> getRealm(Object value2,IBinding bnd,boolean vi) {
		if (vi){
			return bnd.getRealm();
		}
		
		if (value2 instanceof IRealm<?>) {
			return (IRealm<Object>) value2;
		}
		final IRealm<Object> realm = newRealm(value2);
		ObjectListeningRealm objectListeningRealm = new ObjectListeningRealm(realm);
		LyfecycleUtils.markShortLyfeCycle(realm, objectListeningRealm);
		Class<?> subjectClass = bnd.getSubjectClass();
		((BaseMeta)realm.getMeta()).putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, subjectClass);
		return objectListeningRealm;
	}
	
	@SuppressWarnings("unchecked")
	public static IRealm<Object> newRealm(Object value2) {
		if (value2 instanceof Collection) {
			final Collection<?> es = (Collection<?>) value2;
			if (value2 instanceof List) {
				OrderedRealm<Object> orderedRealm = new OrderedRealm<Object>((Collection<Object>) es);
				
				return orderedRealm;
			}
			return new Realm<Object>((Collection<Object>) es);
		}
		final Realm<Object> realm = new Realm<Object>();
		if (value2 != null) {
			realm.add(value2);
		}
		return realm;
	}
}
