package com.onpositive.semantic.model.api.changes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandListener;
import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;

@SuppressWarnings("rawtypes")
public class ObjectChangeManager {

	protected HashMap<INotifyableCollection<?>, Integer> realms = new HashMap<INotifyableCollection<?>, Integer>();

	protected WeakIdentityMap<Object, WeakHashMap<IValueListener<Object>, Object>> weakObjectListeners = new WeakIdentityMap<Object, WeakHashMap<IValueListener<Object>, Object>>();
	
	protected HashSet<IObjectChangeManagerListener>listeners=new HashSet<IObjectChangeManagerListener>();
	
	protected static ObjectChangeManager globalManager=new ObjectChangeManager();
	
	protected static ThreadLocal<ObjectChangeManager>threadLocal=new ThreadLocal<ObjectChangeManager>();
	
	
	
	public static ObjectChangeManager getInstance(){
		ObjectChangeManager objectChangeManager = threadLocal.get();
		if (objectChangeManager!=null){
			return objectChangeManager;
		}
		return globalManager;		
	}
	
	public static void startThreadLocal(){
		threadLocal.set(new ObjectChangeManager());
	}
	
	public static void endThredLocal(){
		threadLocal.set(null);
	}
	
	protected ObjectChangeManager(){
		
	}
	
	public static synchronized void addManagerListener(IObjectChangeManagerListener l){
		getInstance().listeners.add(l);
	}
	public static synchronized void removeManagerListener(IObjectChangeManagerListener l){
		getInstance().listeners.remove(l);
	}

	// static IdentityHashMap<Object, Collection<IValueListener<Object>>>
	// objects = new IdentityHashMap<Object,
	// Collection<IValueListener<Object>>>();
	public static void registerRealm(INotifyableCollection<?> rs) {
		getInstance().registerRealmP(rs);
	}
	public static void unregisterRealm(INotifyableCollection<?> rs) {
		getInstance().unregisterRealmP(rs);
	}

	protected synchronized void registerRealmP(INotifyableCollection<?> rs) {
		Map<INotifyableCollection<?>, Integer> realms2 = realms;
		System.out.println("!!!!"+rs);
		Integer collection = realms2.get(rs);
		if (collection == null) {
			realms2.put(rs, new Integer(1));
			for (IObjectChangeManagerListener m:listeners){
				m.realmRegisted(rs);
			}
			return;
		}
		realms2.put(rs, collection + 1);
		for (IObjectChangeManagerListener m:listeners){
			m.realmRegisted(rs);
		}
	}

	public  int realmSizeP() {
		return realms.size();
	}

	public  int listenerSize() {
		return weakObjectListeners.size();
	}
	public static int realmSize(){
		return getInstance().realmSizeP();
	}
	

	protected synchronized void unregisterRealmP(INotifyableCollection<?> rs) {
		Map<INotifyableCollection<?>, Integer> realms2 = realms;
		Integer collection = realms2.get(rs);
		if (collection == null) {
			return;
		}
		int i = collection - 1;
		if (i != 0) {
			realms2.put(rs, i);
		} else {
			realms2.remove(rs);
		}
		for (IObjectChangeManagerListener q:listeners){
			q.realmUnRegisted(rs);
		}
	}
	public  void processPersistenceAdditions(ICommand c,Object... obj){
		process(c, IRoledListener.ADD, obj);
	}
	public  void processPersistenceDeletions(ICommand c,Object... obj){
		process(c, IRoledListener.DELETE, obj);
	}
	
	@SuppressWarnings("unchecked")
	public  void process(ICommand cmd,String r,Object...objects){
		
		HashMap<ICommandListener, IdentityHashMap<Object, Object>> cm = null;
		for (final Object o : objects) {
			// final Collection<IValueListener<Object>> collection = objects
			// .get(o);
			// if (collection != null) {
			// for (final IValueListener<Object> v : new
			// ArrayList<IValueListener<Object>>(
			// collection)) {
			// v.valueChanged(null, o);
			// }
			// }
			ArrayList<Object> ll = getListeners(o,r);

			if (ll != null) {

				for (final Object vv : ll) {
					if (vv instanceof IValueListener) {
						((IValueListener<Object>) vv).valueChanged(null, o);
					}
					if (vv instanceof ICommandListener && cmd != null) {
						if (cm == null) {
							cm = new HashMap<ICommandListener, IdentityHashMap<Object, Object>>();
						}
						IdentityHashMap<Object, Object> identityHashMap = cm
								.get(vv);
						if (identityHashMap == null) {
							identityHashMap = new IdentityHashMap<Object, Object>();
							cm.put((ICommandListener) vv, identityHashMap);
						}
						identityHashMap.put(o, o);
					}
				}
				// FIXME SYNC

			}
		}
		if (cm != null) {
			for (ICommandListener l : cm.keySet()) {
				IdentityHashMap<Object, Object> z = cm.get(l);
				CompositeCommand m = new CompositeCommand();
				buildCommand(cmd, z, m);
				if (!m.isEmpty()) {
					l.commandExecuted(m);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void fireDelta(HashDelta<Object> dlt, ICommand cmd) {

		HashSet<INotifyableCollection<?>> hashSet = null;
		synchronized (ObjectChangeManager.class) {
			Set<INotifyableCollection<?>> keySet = realms.keySet();
			hashSet = new HashSet<INotifyableCollection<?>>(keySet);
		}
		for (final INotifyableCollection<?> e : hashSet) {
			if (e instanceof INotifyableCollection) {
				final INotifyableCollection ss = e;
				final HashDelta<Object> dlt1 = new HashDelta<Object>();
				for (final Object o : dlt.getChangedElements()) {
					if (e.contains(o)) {
						dlt1.markChanged(o);
					}
				}
				if (!dlt1.isEmpty()) {
					ss.changed(dlt1);
				}
			}
		}
		HashMap<ICommandListener, IdentityHashMap<Object, Object>> cm = null;
		for (final Object o : dlt.getChangedElements()) {
			// final Collection<IValueListener<Object>> collection = objects
			// .get(o);
			// if (collection != null) {
			// for (final IValueListener<Object> v : new
			// ArrayList<IValueListener<Object>>(
			// collection)) {
			// v.valueChanged(null, o);
			// }
			// }
			ArrayList<Object> ll = getListeners(o,IRoledListener.UPDATE);

			if (ll != null) {				
				for (final Object vv : ll) {
					if (vv instanceof IValueListener) {
						((IValueListener<Object>) vv).valueChanged(null, o);
					}
					if (vv instanceof ICommandListener && cmd != null) {
						if (cm == null) {
							cm = new HashMap<ICommandListener, IdentityHashMap<Object, Object>>();
						}
						IdentityHashMap<Object, Object> identityHashMap = cm
								.get(vv);
						if (identityHashMap == null) {
							identityHashMap = new IdentityHashMap<Object, Object>();
							cm.put((ICommandListener) vv, identityHashMap);
						}
						identityHashMap.put(o, o);
					}
				}
				// FIXME SYNC

			}
		}
		if (cm != null) {
			for (ICommandListener l : cm.keySet()) {
				IdentityHashMap<Object, Object> z = cm.get(l);
				CompositeCommand m = new CompositeCommand();
				buildCommand(cmd, z, m);
				if (!m.isEmpty()) {
					l.commandExecuted(m);
				}
			}
		}
	}

	protected synchronized ArrayList<Object> getListeners(Object obj,String role) {
		WeakHashMap<IValueListener<Object>, Object> weakHashMap = weakObjectListeners
				.get(obj);
		ArrayList<Object> objL = null;
		
		IHasMeta meta = MetaAccess.getMeta(obj);
		IObjectListenersProvider service = DefaultMetaKeys.getService(meta,
				IObjectListenersProvider.class);
		if (service != null) {
			IObjectListener[] listeners = service.getListeners(obj, role);
			if (listeners != null && listeners.length > 0) {
				if (objL == null) {
					objL = new ArrayList<Object>();
				}
				for (Object o : listeners) {
					objL.add(o);
				}
			}
		}
		if (weakHashMap != null) {
			if (objL == null) {
				objL = new ArrayList<Object>();
			}
			objL.addAll(weakHashMap.keySet());
		}
		return objL;
	}

	private static void buildCommand(ICommand cmd,
			IdentityHashMap<Object, Object> z, CompositeCommand m) {
		if (cmd instanceof CompositeCommand) {
			CompositeCommand cc = (CompositeCommand) cmd;
			for (ICommand qq : cc) {
				buildCommand(qq, z, m);
			}
		} else {
			if (cmd instanceof SimpleOneArgCommand) {
				SimpleOneArgCommand ma = (SimpleOneArgCommand) cmd;
				// FIXME DEPENDENCY GRAPH
				if (z.containsKey(ma.getTarget())
						|| z.containsKey(ma.getValue())) {
					m.addCommand(ma);
				}
			}
		}
	}

	public  void markChanged(Iterable<?> objects) {
		final HashDelta<Object> dlt = new HashDelta<Object>();
		for (final Object o : objects) {
			dlt.markChanged(o);
		}
		fireDelta(dlt, null);
	}

	public  void markChangedP(Object... objects) {
		markChanged(Arrays.asList(objects));
	}
	
	public static void markChanged(Object... objects) {
		getInstance().markChanged(Arrays.asList(objects));
	}
	public static  void addWeakListener(Object o,
			IValueListener<Object> vl){
		getInstance().addWeakListenerP(o, vl);
	}
	
	public static  void removeWeakListener(Object o,
			IValueListener<Object> vl){
		getInstance().removeWeakListenerP(o, vl);
	}

	public  synchronized void addWeakListenerP(Object o,
			IValueListener<Object> vl) {
		if (o == null) {
			return;
		}
		if (o instanceof String){
			return ;
		}
		if (o instanceof Number){
			return ;
		}
		
		WeakHashMap<IValueListener<Object>, Object> collection = weakObjectListeners
				.get(o);
		
		if (collection == null) {
			collection = new WeakHashMap<IValueListener<Object>, Object>(1);
			weakObjectListeners.put(o, collection);
		}
		
		collection.put(vl, ObjectChangeManager.class);
		for (IObjectChangeManagerListener m:listeners){
			m.listenerAdded(o, vl);
		}
	}

	protected synchronized void removeWeakListenerP(Object o,
			IValueListener<Object> vl) {
		if (o == null) {
			return;
		}
		
		final WeakHashMap<IValueListener<Object>, Object> collection = weakObjectListeners
				.get(o);
		if (collection == null) {
			return;
		}
		collection.remove(vl);
		if (collection.isEmpty()) {
			weakObjectListeners.remove(o);
		}
		for (IObjectChangeManagerListener m:listeners){
			m.listenerRemoved(o, vl);
		}
	}

	public  void fireExternalDelta(HashDelta<Object> dlt) {
		fireExternalDelta(dlt, null);
	}

	protected  void fireExternalDeltaP(HashDelta<Object> dlt, ICommand cmd) {
		fireDelta(dlt, cmd);
	}
	
	public static void fireExternalDelta(HashDelta<Object> dlt, ICommand cmd) {
		getInstance().fireDelta(dlt, cmd);
	}
	public  synchronized static void registerRealmWithRefs(INotifyableCollection<?> m,
			Integer integer) {
		getInstance().realms.put(m, integer);
		
	}
	public static  void unregisterRealmTotally(INotifyableCollection<?> m) {
		getInstance().realms.remove(m);
		
	}

}
