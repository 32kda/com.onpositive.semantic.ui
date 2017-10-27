package com.onpositive.semantic.model.api.realm;

import java.util.Collection;

import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.api.property.IProperty;

public class RealmAccess {


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IRealm<Object> getRealm(IHasMeta context,
			Object parentObject, Object object) {
		IRealmProvider service = DefaultMetaKeys.getService(context,
				IRealmProvider.class);
		if (service != null) {
			if (!(context instanceof IFunction)){
			
				if (service instanceof ContextNotAwareProvider){
					ContextNotAwareProvider m=(ContextNotAwareProvider) service;
					return m.getRealm(context, parentObject);
				}
				else{
					if (object==null){
						return service.getRealm(context, null, parentObject);
					}
				}
			}
			
			return service.getRealm(context, parentObject, object);
		}
		return null;
	}

	public static IRealm<Object> getRealm(IHasMeta context, Object object) {
		return getRealm(context, object, null); //FIXME exchanged object & null params, caused tests to fail
	}

	public static IRealm<Object> getRealm(Object object) {
		return getRealm(MetaAccess.getMeta(object), object);
	}
	public static<T> IRealm<T> getRealm(Class<T>clazz) {
		return checkedGetRealm(clazz, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static<T> IRealm<T> checkedGetRealm(Object object,Class<T>clazz) {
		IRealm<Object> realm = getRealm(MetaAccess.getMeta(object), object);
		if (realm!=null){
			Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(realm);
			if (subjectClass.isAssignableFrom(clazz)){
				return (IRealm<T>) realm;
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static ICommand createAddToRealmCommand(IRealm realm, Object object) {
		IHasCommandExecutor service = DefaultMetaKeys.getService(realm,
				IHasCommandExecutor.class);
		if (service != null) {
			ICommand createAddValueCommand = service.getCommandFactory()
					.createAddValueCommand(service, object, object);
			return createAddValueCommand;
		}
		return null;
	}
	@SuppressWarnings("rawtypes")
	public static ICommand createMoveCommand(IRealm realm, Object object,boolean directoin) {
		IHasCommandExecutor service = DefaultMetaKeys.getService(realm,
				IHasCommandExecutor.class);
		if (service != null) {
			if (directoin){
			ICommand createAddValueCommand = service.getCommandFactory()
					.createUpValueCommand(service, object, object);
			return createAddValueCommand;
			}
			else{
				ICommand createAddValueCommand = service.getCommandFactory()
						.createDownValueCommand(service, object, object);
				return createAddValueCommand;	
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static ICommand createRemoveFromRealm(IRealm realm, Object object) {
		IHasCommandExecutor service = DefaultMetaKeys.getService(realm,
				IHasCommandExecutor.class);
		if (service != null) {
			ICommand createAddValueCommand = service.getCommandFactory()
					.createRemoveValueCommand(service, object, object);
			return createAddValueCommand;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static void addElement(IRealm r, Object object) {
		ICommand addToRealm = createAddToRealmCommand(r, object);
		addToRealm.getCommandExecutor().execute(addToRealm);
	}

	@SuppressWarnings("rawtypes")
	public static void removeElement(IRealm r, Object object) {
		ICommand addToRealm = createRemoveFromRealm(r, object);
		addToRealm.getCommandExecutor().execute(addToRealm);
	}

	public static void removeElements(IRealm<Object> realm,
			Collection<Object> object) {
		CompositeCommand c=new CompositeCommand();
		for (Object o:object){
			ICommand addToRealm = createRemoveFromRealm(realm, o);
			c.addCommand(addToRealm);
		}
		c.getCommandExecutor().execute(c);
	}

	public static void addElements(IRealm<Object> realm, Collection object) {
		CompositeCommand c=new CompositeCommand();
		for (Object o:object){
			ICommand addToRealm = createAddToRealmCommand(realm, o);
			c.addCommand(addToRealm);
		}
		c.getCommandExecutor().execute(c);
	}

	public static void moveElement(IRealm<Object> realm, Collection<Object> firstElement,
			boolean direction) {
		CompositeCommand c=new CompositeCommand();
		for (Object o:firstElement){
			ICommand addToRealm = createMoveCommand(realm, o,direction);
			c.addCommand(addToRealm);
		}
		c.getCommandExecutor().execute(c);
	}
	
}
