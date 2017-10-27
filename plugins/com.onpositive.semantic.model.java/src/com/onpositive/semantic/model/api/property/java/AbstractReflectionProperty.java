package com.onpositive.semantic.model.api.property.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collection;

import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.access.IExternalizer;
import com.onpositive.semantic.model.api.command.DefaultCommandFactory;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.globals.GlobalAccess;
import com.onpositive.semantic.model.api.globals.IKey;
import com.onpositive.semantic.model.api.globals.ReplacableReference;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.property.AbstractWritableProperty;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.java.annotations.NoMultiValue;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaContributor;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.validation.DefaultValidationContext;
import com.onpositive.semantic.model.api.validation.IHasValidationContext;
import com.onpositive.semantic.model.api.validation.IValidationContext;

public abstract class AbstractReflectionProperty extends AbstractWritableProperty
		implements IClassResolver, IHasCommandExecutor,IExternalizer ,IHasValidationContext{

	Object writeReplace() throws ObjectStreamException{
		return new ReplacableReference(GlobalAccess.keyString(this));  			 
  	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected String name;
	private transient AnnotatedElement aElement;
		

	public abstract Class<?> getOwnerClass();
	
	@Override
	public URL resolveResource(String className) {
		return getOwnerClass().getResource(className);
	}
	
	@Override
	public InputStream openResourceStream(String path) throws IOException {
		return getOwnerClass().getResource(path).openStream();
	}
	

	protected boolean isCollection() {
		return Collection.class.isAssignableFrom(this.type)||IRealm.class.isAssignableFrom(this.type);
	}
	
	protected void initElement(AnnotatedElement el) {
		if (aElement == null) {
			aElement = el;
		}
		
		Class<?> subjectClass = initSubjectClass();
		if (subjectClass.isPrimitive()){
			subjectClass=BeanMetaProvider.mapPrimitive(subjectClass);
		}
		metadata.setParentMeta(BeanMetaProvider.INSTANCE.getMeta(subjectClass));
		if (Modifier.isPublic(modifiers())){
			metadata.putMeta(DefaultMetaKeys.PUBLIC_KEY, true);
		}
		IKey key =(IKey) GlobalAccess.stringToKey("property://"+getOwnerClass().getName()+"-"+getId());
		metadata.putMeta(DefaultMetaKeys.OBJECT_KEY, key);
		metadata.putMeta(DefaultMetaKeys.PERSISTENT, isPersistent());
		metadata.registerService(IProperty.class, this);
		metadata.registerService(IExternalizer.class, this);
		metadata.registerService(IClassResolver.class, this);		
		metadata.putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, subjectClass);
		metadata.registerService(IHasCommandExecutor.class, this);
		metadata.registerService(ICommandFactory.class, DefaultCommandFactory.INSTANCE);		
		
		if (typeisArray||typeIsCollection){
			metadata.putMeta(DefaultMetaKeys.MULTI_VALUE_KEY, true);			
		}
		if (type!=null&&IRealm.class.isAssignableFrom(type)){
			metadata.putMeta(DefaultMetaKeys.MULTI_VALUE_KEY, true);
		}
		MetaContributor.contribute(metadata, el);
		if (metadata.getSingleValue(DefaultMetaKeys.READ_ONLY_KEY, Boolean.class, null)==null){
			metadata.putMeta(DefaultMetaKeys.READ_ONLY_KEY, isReadOnly());
		}
		if (Modifier.isStatic(modifiers())){
			metadata.putMeta(DefaultMetaKeys.STATIC_KEY, true);	
		}
		if (type!=null&&type.isPrimitive()){
			metadata.putMeta(DefaultMetaKeys.REQUIRED_KEY, true);
		}		
		
	}
	
	protected boolean isPersistent() {
		return true;
	}

	protected abstract int modifiers();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<Object> initSubjectClass() {
		
		if (typeIsCollection) {
			if (aElement.getAnnotation(NoMultiValue.class)!=null){
				return (Class<Object>) type;
			}
			//if (Collection.class.isAssignableFrom(type2)) {
				final Type genericType = this.getGenericType();
				if (genericType instanceof ParameterizedType) {
					final ParameterizedType tp = (ParameterizedType) genericType;
					final Type[] actualTypeArguments = tp
							.getActualTypeArguments();
					
					if (actualTypeArguments[0] instanceof Class) {
						return (Class<Object>) actualTypeArguments[0];
					}
				}
				return Object.class;
			//}
		}
		if (typeisArray){
			if (aElement.getAnnotation(NoMultiValue.class)!=null){
				return (Class<Object>) type;
			}
			return (Class<Object>) type.getComponentType();
		}
		return (Class) type;
	}

	protected abstract Type getGenericType();
	
	public Class<?> resolveClass(String className) {
		ClassLoader ls = getClassLoader();
		try {
			return ls.loadClass(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public IMeta getMeta() {
		return metadata;
	}

	protected abstract ClassLoader getClassLoader();
	protected abstract boolean isReadOnly();

	public ICommandFactory getCommandFactory() {
		return DefaultCommandFactory.INSTANCE;
	}



	protected void commit(Object target, @SuppressWarnings("rawtypes") Collection c) {

	}

	public ICommandExecutor getCommandExecutor() {
		return JavaPropertyProvider.instance;
	}

	public AbstractReflectionProperty(String id) {
		super(id);
	}


	

	public String toString() {
		return this.id;
	}
	
	
	public String externalizeMessage(String message) {
		return JavaPropertyProvider.externalizeString(
				((Member) this.aElement).getDeclaringClass(), message);
	}
	
	public IValidationContext getValidationContext() {
		return new DefaultValidationContext(null);
	}

	

}