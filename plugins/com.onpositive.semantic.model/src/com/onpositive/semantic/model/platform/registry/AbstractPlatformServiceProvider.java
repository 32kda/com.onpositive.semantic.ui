package com.onpositive.semantic.model.platform.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;

import com.onpositive.commons.platform.registry.GenericRegistryObject;
import com.onpositive.commons.platform.registry.RegistryMap;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IServiceProvider;

public abstract class AbstractPlatformServiceProvider<T extends LabelObject>
		extends RegistryMap<T> implements IServiceProvider<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AbstractPlatformServiceProvider(String point, Class<T> ownerClass) {
		super(point, ownerClass);
	}
	

	@SuppressWarnings("rawtypes")
	IdentityHashMap map = new IdentityHashMap<Class, Object>();

	@SuppressWarnings("rawtypes")
	Class supportedClass;

	@SuppressWarnings("unchecked")
	public Object getService(IHasMeta meta, Class<Object> serv, IHasMeta original) {
		Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(meta);
		Object doRes = doRes(original, subjectClass);
		
		return doRes;
	}

	@SuppressWarnings("unchecked")
	private Object doRes(IHasMeta original, Class<?> subjectClass) {
		if (map.containsKey(subjectClass)) {
			return map.get(subjectClass);
		}
		checkLoad();
		Object genericRegistryObject = tclassMap.get(subjectClass
				.getName());
		if (genericRegistryObject != null) {
			return doResolve(original, subjectClass, genericRegistryObject);
		}
		else{
			Class<?> superclass = subjectClass.getSuperclass();
			if (superclass!=null&&superclass!=Object.class){
				Object doRes = doRes(original, superclass);
				
				if (doRes!=null){
					map.put(subjectClass, doRes);
					return doRes;
				}
			}
			Class<?>[] interfaces = subjectClass.getInterfaces();
			for (Class<?>m:interfaces){
				Object doRes = doRes(original, m);
				if (doRes!=null){
					map.put(subjectClass, doRes);
					return doRes;
										
				}
			}
		}
		map.put(subjectClass	,null);
		return null;
	}

	public Object doResolve(IHasMeta original, Class<?> subjectClass,
			Object genericRegistryObject) {
		if (genericRegistryObject instanceof ArrayList){
			ArrayList<?>r=(ArrayList<?>) genericRegistryObject;
			String stringValue = DefaultMetaKeys.getStringValue(original, DefaultMetaKeys.ROLE_KEY);
			for (Object q:r){
				T m=(T) q;
				String stringAttribute = m.getStringAttribute(DefaultMetaKeys.ROLE_KEY,null);					
				if (stringValue!=null&&stringValue.equals(stringAttribute)){
					Object object =((T)genericRegistryObject).getProvider();
					return object;
				}
			}
		}
		else{
			Object object = null;
			object = ((T)genericRegistryObject).getProvider();
			map.put(subjectClass, object);
			return object;
		}
		return null;
	}

	protected boolean stritlyMatches(
			GenericRegistryObject genericRegistryObject, IHasMeta meta) {
		return true;
	}

	protected IdentityHashMap<String, Object> tclassMap = new IdentityHashMap<String, Object>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void initMap(HashMap<String, T> map) {
		super.initMap(map);
		for (T obj : map.values()) {
			String intern = obj.targetClass().intern();
			if (tclassMap.containsKey(intern)){
				Object labelObject = tclassMap.get(intern);
				if (labelObject instanceof ArrayList<?>){
					ArrayList l=(ArrayList) labelObject;
					l.add(obj);
					continue;
				}
				else{
					ArrayList r=new ArrayList();
					r.add(labelObject);
					r.add(obj);
					tclassMap.put(intern, r);
					continue;
				}
			}
			tclassMap.put(intern, obj);
		}
	}
}
