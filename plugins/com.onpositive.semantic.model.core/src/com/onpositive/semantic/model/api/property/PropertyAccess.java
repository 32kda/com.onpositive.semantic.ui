package com.onpositive.semantic.model.api.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.DefaultCommandFactory;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.realm.IRealm;

public class PropertyAccess {

	static IPropertyProvider defaultProvider;
	static boolean initDefaultProvider;

	public static IPropertyProvider getPropertyProvider(Object object) {
		if (object == null) {
			return CommonPropertyProvider.INSTANCE;
		}
		if (object instanceof IHasPropertyProvider) {
			IHasPropertyProvider p = (IHasPropertyProvider) object;
			return p.getPropertyProvider();
		}		
		IHasMeta meta = MetaAccess.getMeta(object);
		return DefaultMetaKeys.getService(meta, IPropertyProvider.class);
	}

	public static IProperty getProperty(Object o, String property) {
		IPropertyProvider propertyProvider = getPropertyProvider(o);
		if (propertyProvider != null) {
			return propertyProvider.getProperty(o, property);
		}
		return null;
	}

	public static void setValueSilently(Object target, String prop, Object value) {
		IProperty property = getProperty(target, prop);
		if (property != null) {

			ICommand createSetValueCommand = createSetValueCommand(target,
					value, property);
			// cm.execute(createSetValueCommand);
			IMeta meta = createSetValueCommand.getMeta();
			if (meta instanceof IWritableMeta) {
				IWritableMeta m = (IWritableMeta) meta;
				m.putMeta(ICommand.META_PROPERTY_SILENTLY, true);

			}
			createSetValueCommand.getCommandExecutor().execute(
					createSetValueCommand);
			return;
		}
		throw new IllegalArgumentException("no such property");
	}

	public static ICommand createSetValueCommand(String id, Object target,
			Object value) {
		IProperty p = getProperty(target, id);
		if (p == null) {
			return null;
		}
		ICommand createSetValueCommand = createSetValueCommand(target, value, p);
		return createSetValueCommand;
	}

	public static ICommand createAddValueCommand(Object target, Object value,
			IProperty p) {
		ICommandFactory service = getCommandFactory(p);
		IHasCommandExecutor cm = getCommandExecutor(p);
		ICommand createSetValueCommand = service.createAddValueCommand(cm,
				target, value);
		return createSetValueCommand;
	}
	
	public static ICommand createRemoveValueCommand(Object target, Object value,
			IProperty p) {
		ICommandFactory service = getCommandFactory(p);
		IHasCommandExecutor cm = getCommandExecutor(p);
		ICommand createSetValueCommand = service.createRemoveValueCommand(cm,
				target, value);
		return createSetValueCommand;
	}

	protected static ICommandFactory getCommandFactory(IProperty p) {
		ICommandFactory service = DefaultMetaKeys.getService(p,
				ICommandFactory.class);
		if (service == null) {
			service = DefaultCommandFactory.INSTANCE;
		}
		if (service == null) {
			service = new DefaultCommandFactory();
		}
		return service;
	}

	protected static IHasCommandExecutor getCommandExecutor(IProperty property) {
		IHasCommandExecutor cm = DefaultMetaKeys.getService(property,
				IHasCommandExecutor.class);
		if (cm == null) {
			throw new IllegalStateException("Property " + property.getId()
					+ " did not have command executor");
		}
		return cm;
	}
	
	public static ICommand createSetValueCommand(Object target, Object value,
			IProperty p) {
		ICommandFactory service = getCommandFactory(p);
		IHasCommandExecutor cm = getCommandExecutor(p);
		ICommand createSetValueCommand = service.createSetValueCommand(cm,
				target, value);
		return createSetValueCommand;
	}

	public static void setValue(String prop, Object target, Object value) {
		IProperty property = getProperty(target, prop);
		if (property == null) {
			throw new IllegalArgumentException("no such property");
		}
		setValue(property, target, value);
	}

	public static void setValue(IProperty p, Object target, Object value) {
		ICommand createSetValueCommand = createSetValueCommand(target, value, p);
		createSetValueCommand.getCommandExecutor().execute(
				createSetValueCommand);
	}
	
	public static void addValue(IProperty p, Object target, Object value) {
		ICommand addValueCommand = createAddValueCommand(target, value, p);
		addValueCommand.getCommandExecutor().execute(
				addValueCommand);
	}
	
	public static void removeValue(IProperty p, Object target, Object value) {
		ICommand removeValueCommand = createRemoveValueCommand(target, value, p);
		removeValueCommand.getCommandExecutor().execute(
				removeValueCommand);
	}

	public static void setValueToAll(IProperty p, Object[] targets, Object value) {
		ICommandFactory service = getCommandFactory(p);
		IHasCommandExecutor cm = getCommandExecutor(p);
		CompositeCommand c = new CompositeCommand();
		for (Object o : targets) {
			c.addCommand(service.createSetValueCommand(cm, o, value));
		}
		cm.getCommandExecutor().execute(c);
	}
	
	public static void addValueToAll(IProperty p, Object[] targets, Object value) {
		ICommandFactory service = getCommandFactory(p);
		IHasCommandExecutor cm = getCommandExecutor(p);
		CompositeCommand c = new CompositeCommand();
		for (Object o : targets) {
			c.addCommand(service.createAddValueCommand(cm, o, value));
		}
		cm.getCommandExecutor().execute(c);
	}
	
	public static void removeValueFromAll(IProperty p, Object[] targets, Object value) {
		ICommandFactory service = getCommandFactory(p);
		IHasCommandExecutor cm = getCommandExecutor(p);
		CompositeCommand c = new CompositeCommand();
		for (Object o : targets) {
			c.addCommand(service.createRemoveValueCommand(cm, o, value));
		}
		cm.getCommandExecutor().execute(c);
	}

	public static Iterable<Object> getValues(IProperty prop, Object object) {
		Object value = prop.getValue(object);
		return (ValueUtils.toCollection(value));
	}

	public static Object getValue(String prop, Object object) {
		IPropertyProvider propertyProvider = getPropertyProvider(object);
		if (propertyProvider != null) {
			IProperty property = propertyProvider.getProperty(object, prop);
			if (property != null) {
				return property.getValue(object);
			}
		}
		return null;
	}

	public static Iterable<Object> getValues(String prop, Object object) {
		IPropertyProvider propertyProvider = getPropertyProvider(object);
		if (propertyProvider != null) {
			IProperty property = propertyProvider.getProperty(object, prop);
			if (property==null){
				return Collections.emptySet();
			}
			return getValues(property, object);
		}
		return Collections.emptySet();
	}

	public static void addPropertyStructureListener(IProperty property,
			IValueListener<Object> propListener) {
		ObjectChangeManager.addWeakListener(property, propListener);
	}

	public static void removePropertyStructureListener(IProperty property,
			IValueListener<Object> propListener) {
		ObjectChangeManager.removeWeakListener(property, propListener);
	}

	public static void firePropertyStructureListener(IProperty property) {
		ObjectChangeManager.markChanged(property);
	}

	public static boolean isReadonly(IHasMeta gm, Object object) {
		
		if (object != null) {
			ITargetDependentReadonly service = DefaultMetaKeys.getService(gm,
					ITargetDependentReadonly.class);
			if (service != null) {
				return service.isReadonly(gm, object);
			}
		}
		if (DefaultMetaKeys.isReadonly(gm)) {
			return true;
		}
		else{
			return DefaultMetaKeys.isStatic(gm);
		}		
	}

	public static Iterable<IProperty> getProperties(Object p) {
		IPropertyProvider propertyProvider = getPropertyProvider(p);
		if (propertyProvider!=null){
		return propertyProvider.getProperties(p);
		}
		return Collections.emptySet();
	}
	
	public static LinkedHashSet<IProperty> getPublicProperties(Object p){
		LinkedHashSet<IProperty>pa=new LinkedHashSet<IProperty>();
		for (IProperty z:getProperties(p)){
			if (DefaultMetaKeys.getValue(z, DefaultMetaKeys.PUBLIC_KEY, Boolean.class,false)){
				pa.add(z);
			}
		}
		return pa;		
	}
	public static LinkedHashSet<IProperty> getPrimaryProperties(Object o) {
		Iterable<IProperty> p0 = PropertyAccess.getProperties(o);
		LinkedHashSet<IProperty> p = new LinkedHashSet<IProperty>();
		for (IProperty p1 : p0) {
			if (DefaultMetaKeys.isPrimary(p1)) {
				p.add(p1);
			}
		}
		return p;
	}
	
	public static LinkedHashSet<IProperty> getPublicProperties(Iterable<Object> ip){
		LinkedHashSet<IProperty>pa=new LinkedHashSet<IProperty>();
		for( Object obj: ip )
			for (IProperty z:getProperties(obj)){
				if (DefaultMetaKeys.getValue(z, DefaultMetaKeys.PUBLIC_KEY, Boolean.class,false)){
					pa.add(z);
				}
			}
		
		return pa;		
	}
	
	public static LinkedHashSet<IProperty> getSystemProperties(Object p){
		LinkedHashSet<IProperty>pa=new LinkedHashSet<IProperty>(CommonPropertyProvider.INSTANCE.commons.values());		
		return pa;		
	}
	
	public static LinkedHashSet<IProperty> getEditableProperties(Object p){
		LinkedHashSet<IProperty>pa=new LinkedHashSet<IProperty>();
		for (IProperty z:getProperties(p)){
			if (!isReadonly(z, p)){
				pa.add(z);
			}
		}
		return pa;		
	}
	
	public static Class<?> getSubjectClass(IProperty property) {
		if (property.getMeta() == null)
				return null;
		return property.getMeta().getSingleValue(DefaultMetaKeys.SUBJECT_CLASS_KEY, Class.class, null);
		
	}
	
	public static ICommand createMoveCommand(IProperty realm, Object object,Object child,boolean directoin) {
		IHasCommandExecutor service = DefaultMetaKeys.getService(realm,
				IHasCommandExecutor.class);
		if (service != null) {
			if (directoin){
			ICommand createAddValueCommand = service.getCommandFactory()
					.createUpValueCommand(service, object, child);
			return createAddValueCommand;
			}
			else{
				ICommand createAddValueCommand = service.getCommandFactory()
						.createDownValueCommand(service, object, child);
				return createAddValueCommand;	
			}
		}
		return null;
	}
	
	public static void setParentSilent(Object object, Object parent) {
		Iterable<IProperty> properties = PropertyAccess.getProperties(object);
		for (IProperty p:properties){
			if (DefaultMetaKeys.getValue(p, DefaultMetaKeys.PARENT_KEY)){
				Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(p);
				if (subjectClass.isInstance(parent)){
					CompositeCommand c=new CompositeCommand();
					c.addCommand(PropertyAccess.createSetValueCommand(object, parent,p));
					c.setSilent();
					c.setNoPreprocessors();
					c.execute();
				}
			}
		}
	}
}
