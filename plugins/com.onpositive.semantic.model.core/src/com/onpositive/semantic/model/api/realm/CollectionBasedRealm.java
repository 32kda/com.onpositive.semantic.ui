package com.onpositive.semantic.model.api.realm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.INotifyableCollection;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.DefaultCommandFactory;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;

public abstract class CollectionBasedRealm<T> extends AbstractRealm<T> implements IModifiableRealm<T>,INotifyableCollection<T>,ICommandExecutor,IHasCommandExecutor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Collection<T> collection;

	private boolean readOnly;
	
	@Override
	public boolean isOrdered() {
		return collection instanceof List||collection instanceof LinkedHashSet;
	}
	
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void markReadOnly(){
		readOnly=true;
	}
	
	@Override
	public IRealm<T> getParent() {
		return null;
	}
	
	public void clear() {
		remove(getContents());
	}
	
	public CollectionBasedRealm(T... elements) {
		this.collection = createCollection(Arrays.asList(elements));		
	}

	public CollectionBasedRealm(Collection<T> elements) {
		this.collection = createCollection(elements);
	}

	protected abstract Collection<T> createCollection(Collection<T> elements);
	
	@SuppressWarnings("unchecked")
	public CollectionBasedRealm() {
		this.collection = createCollection((Collection<T>) Collections.emptySet());
	}

	@Override
	public Collection<T> getContents() {
		if (isOrdered()){
			return Collections.unmodifiableList((List<? extends T>) collection);
		}
		return Collections.unmodifiableCollection(collection);
	}

	@Override
	public int size() {
		return this.collection.size();
	}

	@Override
	public boolean contains(Object value) {
		return this.collection.contains(value);
	}
	@Override
	@SuppressWarnings("unchecked")
	public void changed(ISetDelta<?> dlt) {
		this.changed((Iterable<T>) dlt.getChangedElements());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void changed(Iterable<T> element) {
		final HashSet<T> changed = new HashSet<T>();
		for (final T e : element) {

			changed.add(e);

		}
		if (!changed.isEmpty()) {
			this.fireDelta(new HashDelta(Collections.emptySet(), changed,
					Collections.emptySet()));
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.semantic.model.realm.IModifiableRealm#markChanged(T)
	 */
	public void markChanged(T element) {
		this.fireDelta(new HashDelta<T>(element));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.onpositive.semantic.model.realm.IModifiableRealm#applyDelta(com.
	 * onpositive.semantic.model.realm.ISetDelta)
	 */
	public void applyDelta(ISetDelta<T> dlt) {
		if (readOnly){
			throw new IllegalStateException("realm is read only");
		}
		//System.out.println("App");
		this.collection.addAll(dlt.getAddedElements());
		this.collection.removeAll(dlt.getRemovedElements());		
		this.fireDelta(dlt);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.semantic.model.realm.IModifiableRealm#add(T)
	 */
	@Override
	public void add(T element) {
		if (readOnly){
			throw new IllegalStateException("realm is read only");
		}
		if (element==null){
			return;
		}
		if (this.collection.add(element)) {
			// fireDelta(new SoloDelta<T>(SoloDelta.KIND_ADDED, element));
			final HashDelta<T> hashDelta = new HashDelta<T>();
			hashDelta.markAdded(element);
			this.fireDelta(hashDelta);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.semantic.model.realm.IModifiableRealm#remove(T)
	 */
	@Override
	public void remove(T element) {
		if (readOnly){
			throw new IllegalStateException("realm is read only");
		}
		if (this.collection.remove(element)) {
			HashDelta<T>t=new HashDelta<T>();
			t.markRemoved(element);
			this.fireDelta(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.semantic.model.realm.IModifiableRealm#add(java.lang.Iterable
	 * )
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void add(Iterable<T> element) {
		if (readOnly){
			throw new IllegalStateException("realm is read only");
		}
		final HashSet<T> added = new HashSet<T>();
		for (final T e : element) {
			if (e==null){
				return;
			}
			if (this.collection.add(e)) {
				added.add(e);
			}
		}
		if (!added.isEmpty()) {
			this.fireDelta(new HashDelta(added, Collections.emptySet(), Collections
					.emptySet()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.semantic.model.realm.IModifiableRealm#remove(java.lang
	 * .Iterable)
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void remove(Iterable<T> element) {
		if (readOnly){
			throw new IllegalStateException("realm is read only");
		}
		final HashSet<T> removed = new HashSet<T>();
		for (final T e : element) {
			if (this.collection.remove(e)) {
				removed.add(e);
			}
		}
		if (!removed.isEmpty()) {
			this.fireDelta(new HashDelta(Collections.emptySet(), Collections
					.emptySet(), removed));
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	
	public Iterator<T> iterator() {
		return (Iterator<T>) Arrays.asList(collection.toArray()).iterator();
	}
	
	
	@Override
	public boolean mayHaveDublicates() {
		return !(collection instanceof Set);
	}
	
	@Override
	public ICommandExecutor getCommandExecutor() {
		return this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	
	public void execute(ICommand cmd) {
		HashDelta<Object>dlt=new HashDelta<Object>();
		internalExec(cmd,dlt);
		applyDelta((ISetDelta<T>) dlt);
	}
	//FIXME UNDO
	protected void internalExec(ICommand cmd, HashDelta<Object> dlt) {		
		if (cmd instanceof CompositeCommand){
			CompositeCommand c=(CompositeCommand) cmd;
			for (ICommand a:c){
				internalExec(a,dlt);
			}
		}
		if (cmd instanceof SimpleOneArgCommand){
			SimpleOneArgCommand acmd=(SimpleOneArgCommand) cmd;
			Object target = acmd.getTarget();
			if (acmd.getOwner()!=this){
				//FIXME
				acmd.getOwner().getCommandExecutor().execute(cmd);
				dlt.markChanged(target);
				return;
			}
			else{
				executeSimpleCommand(cmd, dlt, acmd, target);
				return;
			}
			
		}
	}

	protected void executeSimpleCommand(ICommand cmd, HashDelta<Object> dlt,
			SimpleOneArgCommand acmd, Object target) {
		if (cmd.getKind().equals(ICommand.ADD)||cmd.getKind().equals(ICommand.ADD_VALUE)){
			if (target==null){
				target=((SimpleOneArgCommand) cmd).getValue();
			}
			dlt.markAdded(target);
			
			//check type
			return;
		}
		if (cmd.getKind().equals(ICommand.DELETE)||cmd.getKind().equals(ICommand.REMOVE_VALUE)){
			dlt.markRemoved(target);
			//check type;
			return;
		}
		if (cmd.getKind().equals(ICommand.UP_VALUE)){
			if (target==null){
				target=((SimpleOneArgCommand) cmd).getValue();
			}
			List q=(List) collection;
			int indexOf = q.indexOf(acmd.getValue());
			Collections.swap(q, indexOf-1, indexOf);
			dlt.setOrderChanged(true);
			dlt.markChanged(target);
			return;
		}
		if (cmd.getKind().equals(ICommand.DOWN_VALUE)){
			List q=(List) collection;
			dlt.setOrderChanged(true);
			int indexOf = q.indexOf(acmd.getValue());
			Collections.swap(q, indexOf, indexOf+1);
			dlt.markChanged(target);
			//check type;
			return;
		}
		throw new IllegalArgumentException("wrong command:"+cmd.toString());
	}
	
	
	@Override
	public ICommandFactory getCommandFactory() {
		return DefaultCommandFactory.INSTANCE;
	}
}
