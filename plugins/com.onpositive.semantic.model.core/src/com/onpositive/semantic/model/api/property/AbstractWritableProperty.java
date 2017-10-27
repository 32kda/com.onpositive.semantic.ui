package com.onpositive.semantic.model.api.property;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.command.DefaultCommandFactory;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.DefaultProperty;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.validation.ValidationAccess;

public abstract class AbstractWritableProperty extends DefaultProperty implements IHasCommandExecutor,IProperty{

	protected Class<?> type;
	protected boolean typeIsCollection;
	protected boolean typeisArray;
	protected boolean commit;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AbstractWritableProperty(String id) {
		super(id);
		metadata.registerService(ICommandFactory.class, DefaultCommandFactory.INSTANCE);
		metadata.registerService(IHasCommandExecutor.class, this);
	}
	
	@Override
	public ICommandExecutor getCommandExecutor() {
		return new DefaultExecutor();
	}

	@Override
	public ICommandFactory getCommandFactory() {
		return DefaultCommandFactory.INSTANCE;
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected SimpleOneArgCommand executeSimpleCommand(SimpleOneArgCommand command)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
				//FIXME
				CodeAndMessage validate = ValidationAccess.validate(command);
				if (validate.isError()){
					if (!ValidationAccess.validate(command.getTarget()).isError()){
						throw new IllegalArgumentException(validate.getMessage());
					}
				}
				final Object target = command.getTarget();
				Object toAdd = command.getValue();
				final String kind = command.getKind();
				final Object oldValue = this.getValue(target);
				if ((kind == SimpleOneArgCommand.SET_VALUE)
						|| (kind == SimpleOneArgCommand.SET_VALUES)) {
					
					Class<?> class1 = toAdd != null ? toAdd.getClass()
							: Object.class;
					if (class1.isArray()){
						toAdd=Arrays.asList((Object[])toAdd);
						class1=Collection.class;
					}
					final boolean objectIsCollection = Collection.class
							.isAssignableFrom(class1);
					
					if (this.typeisArray) {
						Collection<Object> r = null;
						if (objectIsCollection) {
							r = (Collection<Object>) toAdd;
						}
						if (toAdd instanceof Object[]) {
							r = Arrays.asList((Object[]) toAdd);
						} else {
							r = Collections.singleton(toAdd);
						}
						final int size = r.size();
						final Object newInstance = Array.newInstance(
								this.getSubjectClass(), size);
						final Iterator it = r.iterator();
						for (int a = 0; a < size; a++) {
							Array.set(newInstance, a, it.next());
						}
						this.doSet(target, newInstance);
			
						return new SimpleOneArgCommand(target, oldValue,
								SimpleOneArgCommand.SET_VALUE, this);
					}
					if (this.typeIsCollection && objectIsCollection) {
						if (this.type == class1) {
							this.doSet(target, toAdd);
						} else if (this.type.isAssignableFrom(class1)) {
							this.doSet(target, toAdd);
						} else {
							final Object newInstance = this.type.getConstructor(
									Collection.class).newInstance(toAdd);
							this.doSet(target, newInstance);
						}
					} else if (this.typeIsCollection) {
						final Collection c = (Collection) oldValue;
						if (c != null) {
							final Collection<Object> newInstance = new ArrayList<Object>(
									c);
							Object m = toAdd;
							if (!(toAdd instanceof Collection)) {
								if (toAdd==null){
									m=Collections.emptySet();
								}
								else{
									m = Collections.singleton(toAdd);
								}
							}
							HashDelta buildFrom = HashDelta
									.buildFrom(c, (Collection) m);
							for (final Object o : buildFrom.getRemovedElements()) {
								this.remove(target, o, oldValue);
							}
							for (final Object o : buildFrom.getAddedElements()) {
								this.add(target, o, oldValue);
							}
							// c.clear();
							// this.add(target, toAdd, oldValue);
							return new SimpleOneArgCommand(target, newInstance,
									SimpleOneArgCommand.SET_VALUE, this);
						} else {
							final Collection<Object> newInstance = (Collection<Object>) this.type
									.newInstance();
							newInstance.add(c);
							this.doSet(target, newInstance);
						}
					} else if (objectIsCollection) {
						final Collection c1 = (Collection) toAdd;
						if ((c1 == null) || c1.isEmpty()) {
							this.doSet(target, null);
						} else {
							this.doSet(target, c1.iterator().next());
						}
					} else {
						this.doSet(target, toAdd);
					}
					return new SimpleOneArgCommand(target, oldValue,
							SimpleOneArgCommand.SET_VALUE, this);
				} else if (kind == SimpleOneArgCommand.REMOVE_VALUE) {
					this.remove(target, toAdd, oldValue);
			
					return new SimpleOneArgCommand(target, toAdd,
							SimpleOneArgCommand.ADD_VALUE, this);
				} else if (kind == SimpleOneArgCommand.ADD_VALUE) {
					this.add(target, toAdd, oldValue);
					return new SimpleOneArgCommand(target, toAdd,
							SimpleOneArgCommand.REMOVE_VALUE, this);
				} 
				else if (kind == SimpleOneArgCommand.DOWN_VALUE) {
					Object value = this.getValue(target);
					
					if (value instanceof List){
						List q=(List) value;
						int indexOf = q.indexOf(command.getValue());
						Collections.swap(q, indexOf+1, indexOf);
					}
					else{
						throw new IllegalStateException();
					}
					if (value instanceof Collection<?>) {
						return new SimpleOneArgCommand(target, value,
								SimpleOneArgCommand.UP_VALUE, this);
					}
					//this.setOrder(target, toAdd, oldValue);
				}
				else if (kind == SimpleOneArgCommand.UP_VALUE) {
					Object value = this.getValue(target);
					if (value instanceof List){
						List q=(List) value;
						int indexOf = q.indexOf(command.getValue());
						Collections.swap(q, indexOf, indexOf-1);
					}
					else{
						throw new IllegalStateException();
					}
					if (value instanceof Collection<?>) {
						return new SimpleOneArgCommand(target, value,
								SimpleOneArgCommand.DOWN_VALUE, this);
					}					
				}
				throw new UnsupportedOperationException();
			}

	@SuppressWarnings("unchecked")
	protected void add(Object target, Object toAdd, Object oldValue) {
		if (oldValue instanceof Collection) {
			@SuppressWarnings("rawtypes")
			final Collection c = (Collection) oldValue;
			c.add(toAdd);
			this.commit(target, c);
		} else if (oldValue instanceof Object[]) {
			final ArrayList<Object> c = new ArrayList<Object>(
					Arrays.asList((Object[]) oldValue));
			c.add(toAdd);
			final int size = c.size();
			final Object newInstance = Array.newInstance(
					this.getSubjectClass(), size);
			for (int a = 0; a < size; a++) {
				Array.set(newInstance, a, c.get(a));
			}
			try {
				this.doSet(target, newInstance);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}
		else{
			try {
				this.doSet(target, toAdd);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException();
			}
		}
	}
	@SuppressWarnings("rawtypes")
	protected void commit(Object target, Collection c) {
		if (this.commit) {
			try {
				this.doSet(target, c);
			} catch (final IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@SuppressWarnings({ "rawtypes" })
	protected void remove(Object target, Object toAdd, Object oldValue) {
		if (oldValue instanceof Collection) {
			final Collection c = (Collection) oldValue;
			c.remove(toAdd);
			this.commit(target, c);
		} else if (oldValue instanceof Object[]) {
			final ArrayList<Object> c = new ArrayList<Object>(
					Arrays.asList((Object[]) oldValue));
			c.remove(toAdd);
			final int size = c.size();
			final Object newInstance = Array.newInstance(
					this.getSubjectClass(), size);
			for (int a = 0; a < size; a++) {
				Array.set(newInstance, a, c.get(a));
			}
			try {
				this.doSet(target, newInstance);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}
	}
	
	public final Class<?> getSubjectClass() {
		return DefaultMetaKeys.getSubjectClass(this);
	}

	protected abstract void doSet(Object target, Object object)
			throws IllegalAccessException;

}