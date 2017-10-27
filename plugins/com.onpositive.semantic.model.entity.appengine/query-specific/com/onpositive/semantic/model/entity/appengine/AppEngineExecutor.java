package com.onpositive.semantic.model.entity.appengine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.code.twig.annotation.AnnotationObjectDatastore;
import com.google.code.twig.configuration.Configuration;
import com.google.code.twig.standard.StandardStoreCommand;
import com.google.code.twig.standard.StandardUntypedMultipleLoadCommand;
import com.google.code.twig.standard.TranslatorObjectDatastore;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.command.ProxyExecutor;
import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;
import com.onpositive.semantic.model.api.globals.GlobalAccess;
import com.onpositive.semantic.model.api.globals.IKey;
import com.onpositive.semantic.model.api.globals.Key;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.java.JavaPropertyProvider;
import com.onpositive.semantic.model.api.property.java.JavaPropertyProvider.ClassPropertyInfo;
import com.onpositive.semantic.model.api.query.IPermissionManager;
import com.onpositive.semantic.model.api.query.ITransport;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryFilter;
import com.onpositive.semantic.model.api.query.QueryResult;
import com.onpositive.semantic.model.api.query.memimpl.PartialInMemoryExecutor;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class AppEngineExecutor extends PartialInMemoryExecutor implements
		ITransport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IPermissionManager permissionManager;
	private IVariableResolver resolver;

	public AppEngineExecutor(IPermissionManager man,IVariableResolver resolver) {
		super(new InnerAppEngineExecutor());
		this.permissionManager=man;
		this.resolver=resolver;
	}

	final static HashSet<String> inEquality = new HashSet<String>();

	static {
		inEquality.add(QueryFilter.FILTER_GE);
		inEquality.add(QueryFilter.FILTER_LE);
		inEquality.add(QueryFilter.FILTER_GT);
		inEquality.add(QueryFilter.FILTER_LT);
		inEquality.add(QueryFilter.FILTER_STARTS_WITH);
		inEquality.add(QueryFilter.FILTER_NOT_EQUALS);
	}

	@Override
	protected LinkedHashSet<QueryFilter> getInStoreFilters(Query clone) {
		QueryFilter[] filters = clone.getFilters();
		LinkedHashSet<QueryFilter> flt = new LinkedHashSet<QueryFilter>();
		String inProp = clone.getSorting();
		for (QueryFilter f : filters) {
			String filterKind = f.getFilterKind();
			if (inEquality.contains(filterKind)) {
				if (inProp != null) {
					continue;
				} else {
					inProp = f.getPropId();
				}
			}
			flt.add(f);
		}
		return (LinkedHashSet<QueryFilter>) flt;
	}

	@Override
	public void execute(ICommand cmd) {
		CodeAndMessage validateCommand = permissionManager.validateCommand(cmd);
		if (validateCommand.isError()){
			throw new IllegalArgumentException(validateCommand.getMessage());
		}
		HashMap<IKey, Object> changes = new HashMap<IKey, Object>();
		preprocess(cmd, changes);
		AnnotationObjectDatastore annotationObjectDatastore = new AnnotationObjectDatastore();
		HashMap<IKey, com.google.appengine.api.datastore.Key> ks = new HashMap<IKey, com.google.appengine.api.datastore.Key>();
		HashMap<com.google.appengine.api.datastore.Key, IKey> ks2 = new HashMap<com.google.appengine.api.datastore.Key, IKey>();
		StandardUntypedMultipleLoadCommand keys = annotationObjectDatastore
				.load().keys(
						convertKeys(changes.keySet(), ks, ks2,
								annotationObjectDatastore.getConfiguration()));
		Map<com.google.appengine.api.datastore.Key, Object> returnResultsNow = keys
				.returnResultsNow();

		for (com.google.appengine.api.datastore.Key c : returnResultsNow
				.keySet()) {
			changes.put(ks2.get(c), returnResultsNow.get(c));
		}
		Collection<Object> toDelete = new HashSet<Object>();
		Collection<Object> changesV = new HashSet<Object>(changes.values());

		Collection<Object> additions = new HashSet<Object>();
		
//		for (Object o:toDelete){
//			DefaultMetaKeys.getService(MetaAccess.getMeta(o), clazz);
//		}
//		for (Object o:changesV){
//			DefaultMetaKeys.getService(MetaAccess.getMeta(o), clazz);
//		}
//		for (Object o:additions){
//			DefaultMetaKeys.getService(MetaAccess.getMeta(o), clazz);			
//		}
		CompositeCommand prepared = new CompositeCommand();
		prepare(cmd, changes, toDelete, additions, prepared);
		changesV.removeAll(toDelete);
		changesV.removeAll(additions);
		changesV.remove(null);
		ObjectChangeManager.getInstance().processPersistenceAdditions(cmd,additions);
		ObjectChangeManager.getInstance().processPersistenceDeletions(cmd,toDelete);
		// now lets store results;
		try {
			prepared.getCommandExecutor().execute(prepared);
			StandardStoreCommand store = annotationObjectDatastore.store();

			store.update(true).instances(changesV).now();
			store = annotationObjectDatastore.store();
			store.instances(additions).now();
			annotationObjectDatastore.deleteAll(toDelete);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void prepare(ICommand cmd, HashMap<IKey, Object> changes,
			Collection<Object> toDelete, Collection<Object> additions,
			CompositeCommand prepared) {
		if (cmd instanceof CompositeCommand) {
			CompositeCommand z = (CompositeCommand) cmd;
			for (ICommand c : z) {
				prepare(c, changes, toDelete, additions, prepared);
			}
		}
		if (cmd instanceof SimpleOneArgCommand) {
			SimpleOneArgCommand m = (SimpleOneArgCommand) cmd;
			// convert from remote form here

			if (m.getKind().equals(SimpleOneArgCommand.ADD)) {
				additions.add(m.getValue());
				return;
			}
			if (m.getKind().equals(SimpleOneArgCommand.DELETE)) {
				Object target = m.getTarget();
				if (!(target instanceof IKey)){
					toDelete.add(target);
				}
				else{
				toDelete.add(changes.get(target));
				}
				return;
			}
			Object target = m.getTarget();
			if (changes.containsKey(target)) {
				target = changes.get(target);
			}
			Object value = m.getValue();
			if (changes.containsKey(target)) {
				value = changes.get(value);
			}
			IHasCommandExecutor owner = m.getOwner();
			if (owner instanceof ProxyExecutor) {
				ProxyExecutor pe = (ProxyExecutor) owner;
				owner = pe.getHasCommandExecutor();
			}
			SimpleOneArgCommand z = new SimpleOneArgCommand(target,
					m.getValue(), m.getKind().intern(), owner);
			prepared.addCommand(z);
		}
	}

	private Collection<com.google.appengine.api.datastore.Key> convertKeys(
			Set<IKey> keySet,
			HashMap<IKey, com.google.appengine.api.datastore.Key> ks,
			HashMap<com.google.appengine.api.datastore.Key, IKey> ks2,
			Configuration configuration) {
		for (IKey c : keySet) {
			com.google.appengine.api.datastore.Key createKey = getKey(
					configuration, c);
			if (createKey==null){
				continue;
			}
			ks.put(c, createKey);
			ks2.put(createKey, c);
		}
		return ks.values();
	}

	public static com.google.appengine.api.datastore.Key getKey(
			Configuration configuration, IKey c) {
		if(c==null){
			return null;
		}
		com.google.appengine.api.datastore.Key createKey = null;
		IKey parent = c.getParent();
		String localId = parent.getLocalId();
		Class cm = getClass(parent);
		ClassPropertyInfo properties = JavaPropertyProvider.instance
				.getProperties(cm);
		Collection<IProperty> all = properties.getAll();
		for (IProperty p : all) {
			if (DefaultMetaKeys.getValue(p, DefaultMetaKeys.ID_KEY)) {
				Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(p);
				if (Number.class.isAssignableFrom(subjectClass)) {
					String localId2 = c.getLocalId();
					String typeToKind = configuration.typeToKind(cm);
					createKey = KeyFactory.createKey(typeToKind,
							Long.parseLong(localId2));
					break;
				} else {
					break;
				}
			}
		}
		if (createKey == null) {
			String typeToKind = configuration.typeToKind(cm);
			String localId2 = c.getLocalId();
			createKey = KeyFactory.createKey(typeToKind, localId2);
		}
		return createKey;
	}

	static HashMap<String, Class> clazzCache = new HashMap<String, Class>();

	private static Class getClass(IKey parent) {
		String localId = parent.getLocalId();
		Class class1 = clazzCache.get(localId);
		if (class1 == null) {
			try {
				class1 = Class.forName(localId);
				clazzCache.put(localId, class1);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return class1;
	}

	private void preprocess(ICommand cmd, HashMap<IKey, Object> changes) {
		if (cmd instanceof CompositeCommand) {
			CompositeCommand cz = (CompositeCommand) cmd;
			for (ICommand q : cz) {
				preprocess(q, changes);
			}
			return;
		}
		if (cmd instanceof SimpleOneArgCommand) {
			SimpleOneArgCommand m = (SimpleOneArgCommand) cmd;
			Object target = m.getTarget();
			if (target != null && target instanceof IKey) {
				Key x = (Key) target;
				changes.put(x, null);
			}
			IKey x=GlobalAccess.getKey(target);
			changes.put(x, null);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public QueryResult executeQuery(Query query) {
		try {
			CodeAndMessage authentificate = authentificate(query);
			if (authentificate.isError()) {
				QueryResult queryResult = new QueryResult();
				queryResult.setMessage(authentificate);
				return queryResult;
			}
			QueryResult execute = execute(query, null);
			QueryResult adjustResults = permissionManager.adjustResults(execute, query);
			return adjustResults;
		} catch (Throwable e) {
			e.printStackTrace();
			QueryResult queryResult = new QueryResult();
			queryResult.setMessage(CodeAndMessage.errorMessage(e.getMessage()));
			return queryResult;
		}
	}

	CodeAndMessage authentificate(Query query) {
		CodeAndMessage validateQuery = permissionManager.validateQuery(query);		
		return validateQuery;
	}

	@Override
	public Object get(Object id, IMeta meta) {		
		if (id.toString().startsWith("$")) {
			return resolver.resolveObject(id.toString(),meta);
		}
		return null;
	}
	public Object get(IKey c){
		HashMap<IKey, Object>om=new HashMap<IKey, Object>();
		om.put(c, null);
		get(om);
		return om.get(c);
	}

	public void get(HashMap<IKey, Object> changes) {
		HashMap<IKey, com.google.appengine.api.datastore.Key> ks = new HashMap<IKey, com.google.appengine.api.datastore.Key>();
		HashMap<com.google.appengine.api.datastore.Key, IKey> ks2 = new HashMap<com.google.appengine.api.datastore.Key, IKey>();
		TranslatorObjectDatastore annotationObjectDatastore = new AnnotationObjectDatastore();
		
		StandardUntypedMultipleLoadCommand keys = annotationObjectDatastore
				.load().keys(
						convertKeys(changes.keySet(), ks, ks2,
								annotationObjectDatastore
										.getConfiguration()));
		Map<com.google.appengine.api.datastore.Key, Object> returnResultsNow = keys
				.returnResultsNow();
		for(com.google.appengine.api.datastore.Key x:returnResultsNow.keySet()){
			changes.put(ks2.get(x), returnResultsNow.get(x));
		}
	}

	public void store(Object up) {
		AnnotationObjectDatastore annotationObjectDatastore = new AnnotationObjectDatastore();
		annotationObjectDatastore.store(up);
	}

	@Override
	public Object getObject(Object id) {
		return get((IKey) id);
	}

}
