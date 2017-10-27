package com.onpositive.semantic.model.api.meta;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;

import com.onpositive.semantic.model.api.globals.GlobalAccess;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.api.validation.DefaultValidationContext;
import com.onpositive.semantic.model.api.validation.IHasValidationContext;
import com.onpositive.semantic.model.api.validation.IValidationContext;

public class BaseMeta implements IMeta, IHasMeta, IWritableMeta, Cloneable,
		IHasValidationContext {

	public static final String KEY_FIELD="kkey";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected HashMap<Object, Object> metaInfo;
	protected IdentityHashMap<Class<?>, Object> classInfo;
	protected IdentityHashMap<Class<?>, IHasMeta> stransfInfo;

	transient protected IdentityHashMap<Class<?>, Object> cachedService;
	transient protected IServiceProvider<?> defaultServiceProvider;

	@Override
	public IServiceProvider<?> getDefaultServiceProvider() {
		return defaultServiceProvider;
	}

	@Override
	public void setDefaultServiceProvider(
			IServiceProvider<?> defaultServiceProvider) {
		checkReadonly();
		this.defaultServiceProvider = defaultServiceProvider;
	}

	protected boolean readOnly;
	protected IMeta defaultMeta;
	protected IMeta parentMeta;

	protected int revisionId;
	protected int parentRevisionId;

	public BaseMeta(IMeta parentMeta) {
		super();
		this.parentMeta = parentMeta;
	}

	public BaseMeta() {
	}

	protected void notifyChanges() {
		revisionId++;
		cachedService = null;
	}

	@Override
	public void setParentMeta(IMeta meta) {
		checkReadonly();
		parentMeta = meta;
		cachedService = null;
	}

	protected void checkReadonly() {
		if (readOnly) {
			throw new IllegalStateException("this meta is read only");
		}
	}

	@Override
	public void setDefaultMeta(IMeta meta) {
		checkReadonly();
		defaultMeta = meta;
		cachedService = null;
	}

	@Override
	public void putMeta(String key, Object object) {
		checkReadonly();
		checkInfo();
		metaInfo.put(key, object);

		notifyChanges();
	}

	private void checkInfo() {
		if (metaInfo == null) {
			metaInfo = new HashMap<Object, Object>(8);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getSingleValue(String key, Class<T> requestedClass,
			Object defaultValue) {
		return internalGetSingleValue(key, requestedClass, defaultValue, this);
	}

	@SuppressWarnings("unchecked")
	protected <T> T internalGetSingleValue(String key, Class<T> requestedClass,
			Object defaultValue, IMeta ma) {
		if (metaInfo != null && metaInfo.containsKey(key)) {
			Object object = metaInfo.get(key);

			if (object != null) {

				if (requestedClass == null) {
					return (T) object;
				}
				if (requestedClass.isInstance(object)) {
					return requestedClass.cast(object);
				}
				if (object instanceof IFunction) {
					IFunction c = (IFunction) object;
					Object value = c.getValue(this);
					if (requestedClass.isInstance(value)) {
						return requestedClass.cast(value);
					}
				}
			}
			return (T) defaultValue;
		}
		if (defaultMeta != null) {
			T t2 = atomicGet(defaultMeta, key, requestedClass, ma);
			if (t2 != null) {
				return t2;
			}
		}
		if (parentMeta != null) {
			T t2 = atomicGet(parentMeta, key, requestedClass, ma);
			if (t2 != null) {
				return t2;
			}
		}
		return requestedClass.cast(defaultValue);
	}

	private <T> T atomicGet(IMeta m, String key, Class<T> requestedClass,
			IMeta root) {
		if (m instanceof BaseMeta) {
			BaseMeta bm = (BaseMeta) m;
			return bm.internalGetSingleValue(key, requestedClass, null, root);
		}
		return m.getSingleValue(key, requestedClass, null);
	}

	@Override
	public IMeta getMeta() {
		return this;
	}

	@Override
	public Collection<Object> keys() {
		if (parentMeta == null && metaInfo != null) {
			return metaInfo.keySet();
		}
		if (parentMeta != null) {
			if (metaInfo != null) {
				ArrayList<Object> st = new ArrayList<Object>(metaInfo.keySet());
				st.addAll(parentMeta.keys());
			}
			return parentMeta.keys();
		}
		return Collections.emptySet();
	}

	public Collection<Object> declaredKeys() {
		if (metaInfo == null) {
			return Collections.emptySet();
		}
		return metaInfo.keySet();
	}

	public void lock() {
		this.readOnly = true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T, A extends T> A getService(Class<T> requestedClass) {
		if (cachedService != null) {
			if (parentMeta != null) {
				if (parentRevisionId == parentMeta.getRevisionId()) {
					if (cachedService.containsKey(requestedClass)) {
						return (A) cachedService.get(requestedClass);
					}
				} else {
					// Clear all cached services;
					cachedService = null;
				}
			} else {
				if (cachedService.containsKey(requestedClass)) {
					return (A) cachedService.get(requestedClass);
				}
			}
		}
		if (cachedService == null) {
			cachedService = new IdentityHashMap<Class<?>, Object>();
			if (parentMeta != null) {
				parentRevisionId = parentMeta.getRevisionId();
			}
		}
		A internalGetService = internalGetService(requestedClass, this);
		cachedService.put(requestedClass, internalGetService);
		return internalGetService;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T, A extends T> A internalGetService(Class<T> requestedClass,
			BaseMeta originalService) {

		if (requestedClass.isInstance(this)) {
			return (A) this;
		}

		if (classInfo != null) {

			if (classInfo.containsKey(requestedClass)) {
				Object object = classInfo.get(requestedClass);
				if (object instanceof IServiceProvider) {
					IServiceProvider<T> pr = (IServiceProvider<T>) object;
					object = pr.getService(originalService, requestedClass, originalService);
				}
				T cast = requestedClass.cast(object);
				if (cast != null) {
					if (parentMeta != null) {
						IServiceJoiner joiner = getJoiner(requestedClass);
						if (joiner != null
								&& !doNotInheritService(requestedClass)) {
							T service = getFromParent(parentMeta,
									requestedClass, originalService);
							if (service != null) {
								cast = (T) joiner.joinService(cast, service);
							}
							if (defaultServiceProvider != null) {
								service = (T) defaultServiceProvider.getService(this,
										(Class) requestedClass, originalService);
								if (service!=null){
								cast = (T) joiner.joinService(cast, service);
								}
							}
						}
					}
				}
				return (A) cast;
			}
		}
		if (stransfInfo!=null&& stransfInfo.containsKey(requestedClass)){
			IHasMeta iHasMeta = stransfInfo.get(requestedClass);
			T service = DefaultMetaKeys.getService(iHasMeta, requestedClass);
			if (service!=null){
				return (A)requestedClass.cast(service);
			}
		}
		if (defaultServiceProvider != null) {
			Object service = defaultServiceProvider.getService(this,
					(Class) requestedClass, originalService);
			if (service != null) {
				return (A) requestedClass.cast(service);
			}
		}
		
		if (defaultMeta != null) {
			A fromParent = (A) getFromParent(defaultMeta, requestedClass,
					originalService);
			if (fromParent != null) {
				return fromParent;
			}
		}
		if (parentMeta != null) {
			A service = (A) getFromParent(parentMeta, requestedClass,
					originalService);
			if (service != null) {
				return service;
			}
		}
		return null;
	}

	protected <T> T getFromParent(IMeta parent, Class<T> requestedClass,
			BaseMeta root) {
		if (parent instanceof BaseMeta) {
			BaseMeta bm = (BaseMeta) parent;
			return bm.internalGetService(requestedClass, root);
		}
		return parent.getService(requestedClass);
	}

	protected HashSet<Class<?>> doNotInherit;

	protected boolean doNotInheritService(Class<?> requestedClass) {
		if (doNotInherit != null) {
			return doNotInherit.contains(requestedClass);
		}
		return false;
	}

	@Override
	public void overrideService(Class<?> cl) {
		if (doNotInherit == null) {
			doNotInherit = new HashSet<Class<?>>();
		}
		doNotInherit.add(cl);
	}

	@SuppressWarnings("rawtypes")
	protected <T> IServiceJoiner getJoiner(Class<T> requestedClass) {
		return DefaultMetaKeys.getService(MetaAccess.getMeta(requestedClass),
				IServiceJoiner.class);
	}

	@Override
	public Collection<Class<?>> services() {
		if (parentMeta == null && classInfo != null) {
			return classInfo.keySet();
		}
		if (parentMeta != null) {
			if (classInfo != null) {
				ArrayList<Class<?>> st = new ArrayList<Class<?>>(
						classInfo.keySet());
				st.addAll(parentMeta.services());
				return st;
			}
			return parentMeta.services();
		}
		return Collections.emptySet();
	}

	@Override
	public <T, A extends T> void registerService(Class<T> servClazz, A object) {
		internalRegisterService(servClazz, object);
	}

	@Override
	public <T> void registerService(Class<T> servClazz,
			IServiceProvider<T> object) {
		internalRegisterService(servClazz, object);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void internalRegisterService(Class<?> servClazz, Object object) {
		IServiceRegistrator serviceRegistrator = MetaAccess
				.getServiceRegistrator(servClazz);
		if  (cachedService!=null){
			cachedService.remove(servClazz);
		}
		if (serviceRegistrator != null) {
			serviceRegistrator.registerService(this, servClazz, object);
			return;
		}
		
		if (classInfo == null) {
			classInfo = new IdentityHashMap<Class<?>, Object>();
			classInfo.put(servClazz, object);
			revisionId++;
			
			return;
		}
		Object object2 = classInfo.get(servClazz);
		if (object2 != null) {
			IServiceJoiner service = getJoiner(servClazz);
			if (service != null) {
				classInfo.put(servClazz,
						service.joinService(object, servClazz.cast(object2)));
				revisionId++;
				return;
			}
		}
		classInfo.put(servClazz, object);
		revisionId++;
		notifyChanges();
	}

	@Override
	public BaseMeta getWritableCopy() {
		try {
			BaseMeta clone = (BaseMeta) super.clone();
			clone.readOnly = false;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException();
		}
	}

	@Override
	public IMeta getParentMeta() {
		return parentMeta;
	}

	@Override
	public IMeta getDefaultMeta() {
		return defaultMeta;
	}

	@Override
	public int getRevisionId() {
		if (parentMeta != null) {
			// FIXME
			return Math.max(revisionId, parentMeta.getRevisionId());
		}
		return revisionId;
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append("Meta:" + metaInfo + "\n");
		//bld.append("Services:" + classInfo + "\n");
		if (defaultMeta != null) {
			bld.append("Default:\n");
			bld.append(defaultMeta.toString());
		}
		if (parentMeta != null) {
			bld.append("***************************\n");
			if (parentMeta != null) {
				bld.append(parentMeta.toString());
			}
		}
		return bld.toString();
	}

	@Override
	public IValidationContext getValidationContext() {
		return new DefaultValidationContext(null);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		String key = null;
		if (metaInfo != null) {
			Object kObj = metaInfo.get(KEY_FIELD);
			if (kObj != null) {
				key = kObj.toString();
			}
		}
		if (key != null) {
			out.writeInt(2);
			out.writeUTF(key);
		} else {
			out.writeInt(1);

			if (metaInfo != null) {
				out.writeInt(1);
				out.writeObject(metaInfo);
			} else {
				out.writeInt(0);
			}

			if (classInfo != null) {
				out.writeInt(1);
				out.writeObject(classInfo);
			} else {
				out.writeInt(0);
			}

			out.writeBoolean(readOnly);

			if (defaultMeta != null) {
				out.writeInt(1);
				out.writeObject(defaultMeta);
			} else {
				out.writeInt(0);
			}

			if (parentMeta != null) {
				out.writeInt(1);
				out.writeObject(parentMeta);
			} else {
				out.writeInt(0);
			}

			out.writeInt(revisionId);
			out.writeInt(parentRevisionId);
		}
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		int mode = in.readInt();
		if (mode == 2) {
			String key = in.readUTF();
			Object resolved = GlobalAccess.resolve(key);
			if (resolved instanceof BaseMeta) {
				BaseMeta bm = (BaseMeta) resolved;
				initFromOtherMeta(bm);
			}
		} else {
			int metaInfCode = in.readInt();
			if (metaInfCode != 0) {
				Object mObj = in.readObject();
				metaInfo = (HashMap<Object, Object>) mObj;
			}

			int clsInfCode = in.readInt();
			if (clsInfCode != 0) {
				Object cObj = in.readObject();
				classInfo = (IdentityHashMap<Class<?>, Object>) cObj;
			}

			readOnly = in.readBoolean();

			int defMeta = in.readInt();
			if (defMeta != 0) {
				Object readed = in.readObject();
				defaultMeta = (IMeta) readed;
			}

			int pMeta = in.readInt();
			if (pMeta != 0) {
				Object readed = in.readObject();
				parentMeta = (IMeta) readed;
			}

			revisionId = in.readInt();
			parentRevisionId = in.readInt();
		}
	}

	private void initFromOtherMeta(BaseMeta m) {
		metaInfo = m.metaInfo;
		classInfo = m.classInfo;
		cachedService = m.cachedService;
		defaultServiceProvider = m.defaultServiceProvider;
		readOnly = m.readOnly;
		defaultMeta = m.defaultMeta;
		parentMeta = m.parentMeta;
		revisionId = m.revisionId;
	}

	public void registerServiceTransfer(Class<?> class1,
			IHasMeta meta) {
		if (stransfInfo==null){
			stransfInfo=new IdentityHashMap<Class<?>, IHasMeta>(2);
		}
		//TODO FIX SERVICE TRANSFER SERIALIZATION
		stransfInfo.put(class1, meta);
		notifyChanges();
	}

	@Override
	public void copyFrom(IMeta meta) {
		if (meta instanceof BaseMeta) {
			if (((BaseMeta)meta).metaInfo != null)
				metaInfo = new HashMap<Object, Object>(((BaseMeta)meta).metaInfo);
			parentMeta = meta.getParentMeta();
			defaultMeta = meta.getParentMeta();
			classInfo = ((BaseMeta) meta).classInfo;
			stransfInfo = ((BaseMeta) meta).stransfInfo;
			cachedService = ((BaseMeta) meta).cachedService;
			defaultServiceProvider = ((BaseMeta) meta).defaultServiceProvider;
			
		}
		
	}

}