package com.onpositive.semantic.model.api.realm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.command.DefaultCommandFactory;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.globals.IKey;
import com.onpositive.semantic.model.api.globals.IKeyResolver;
import com.onpositive.semantic.model.api.globals.Key;
import com.onpositive.semantic.model.api.id.IIdentifierProvider;
import com.onpositive.semantic.model.api.id.IdAccess;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.query.IQueryExecutor;
import com.onpositive.semantic.model.api.query.ITransport;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.query.QueryResult;
import com.onpositive.semantic.model.api.realm.AbstractRealm;
import com.onpositive.semantic.model.api.realm.IRealm;

public class FixedTypeTransportRealm<T> extends AbstractRealm<T> 
		implements IIdentifierProvider<T>,IHasCommandExecutor , IQueryExecutor {

	public static IKey CLASS_KEY = new Key(null, "class:");
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected ITransport transport;

	private Class<T> clazz;
	
	
	
	protected void startListening() {
		super.startListening();
	};

	public FixedTypeTransportRealm(ITransport lc, final Class<T> clazz) {
		super();
		this.transport = lc;
		this.clazz = clazz;
		putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, clazz);
		registerService(IQueryExecutor.class, this);
		registerService(ICommandExecutor.class, lc);
		registerService(IKeyResolver.class, new IKeyResolver() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object resolveKey(String key) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public IKey getKey(Object obj) {
				if (obj instanceof FixedTypeTransportRealm){
					return new Key(CLASS_KEY, clazz.getName());
				}
				return null;
			}
		});
		registerService(IHasCommandExecutor.class, this);
		//lc.addValueListener(l);
	}
	
	public void dispose(){
		
	}

	protected Integer size;
	long lastSizeTime=0;

	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> getContents() {
		QueryResult execute = transport.executeQuery(new Query(clazz.getName()));
		size=execute.getResult().length;
		lastSizeTime=System.currentTimeMillis();
		return new ArrayList<T>((Collection<? extends T>) Arrays.asList(execute.getResult()));
	}

	@Override
	public IRealm<T> getParent() {
		return null;
	}

	@Override
	public int size() {
		if (size == null||(System.currentTimeMillis()-lastSizeTime>2000)) {
			Query query = new Query(clazz.getName());
			query.setMode(Query.KEYS_ONLY);
			QueryResult execute = transport.executeQuery(query);
			Long totalcount = execute.getTotalcount();
			size=totalcount.intValue();			
			lastSizeTime=System.currentTimeMillis();
			System.out.println(size);
			return size;
		}
		return size;
	}

	@Override
	public boolean isOrdered() {
		return true;
	}

	@Override
	public boolean mayHaveDublicates() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		if (!clazz.isInstance(o)) {
			return false;
		}
		/**
		 * assuming that not null id means that object exists
		 */
		return IdAccess.getId(o) != null;
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject(IHasMeta meta, Object parent, Object id) {
		return (T) transport.getObject(id);
	}

	@Override
	public Object getId(IHasMeta meta, Object parent, Object object2) {
		return IdAccess.getId(object2);
	}

	@Override
	public ICommandExecutor getCommandExecutor() {
		return new ICommandExecutor() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void execute(ICommand cmd) {
				transport.execute(cmd);
				preExecute(cmd);
				
			}
		};
	}

	protected void preExecute(ICommand cmd) {
		size=null;
		fireDelta(null);
		
	}

	@Override
	public ICommandFactory getCommandFactory() {
		return DefaultCommandFactory.INSTANCE;
	}

	@Override
	public QueryResult execute(Query query, IResultUpdate async) {
		return transport.executeQuery(query);
	}

	@Override
	public void cancel(IResultUpdate async) {
		
	}

}
