package com.onpositive.semantic.model.api.property.java.annotations.meta;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;

import com.onpositive.semantic.model.api.access.IExternalizer;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IWritableMeta;

public class MetaContributor {

	public static class AnnotationModel {

		protected BaseMeta fixedMeta = new BaseMeta() ;
		@SuppressWarnings("rawtypes")
		protected CustomHandler handler;
		protected HashMap<String, Class<?>> instances = new HashMap<String, Class<?>>();

		protected HashMap<String, Method> methodMap = new HashMap<String, Method>();
		@SuppressWarnings("rawtypes")
		public HashMap<Class, ArrayList<Method>> serviceMap = new HashMap<Class, ArrayList<Method>>();
		public Class<?> defaultService;
		public Class<?> implService;
		public boolean passPars;

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void contribute(Annotation annotation, IWritableMeta meta) {
			Collection<Object> keys = fixedMeta.keys();
			for (Object o : keys) {
				if (o instanceof String) {
					meta.putMeta((String) o,
							fixedMeta.getSingleValue((String) o, null, null));
				}
			}
			if (defaultService!=null){
				Object newInstance = newInstance(annotation, implService);
				meta.registerService((Class)defaultService, newInstance);
			}
			for (String s : instances.keySet()) {
				Class<?> class1 = instances.get(s);
				Object newInstance = newInstance(annotation, class1);
				meta.putMeta(s, newInstance);

			}
			for (String s : methodMap.keySet()) {
				try {
					Object invoke = methodMap.get(s).invoke(annotation);
					if (invoke instanceof String){
						String sa=(String) invoke;
						if (sa.length()>0&&sa.charAt(0)=='%'){
							IExternalizer service = DefaultMetaKeys.getService(meta, IExternalizer.class);
							if (service!=null){
							invoke=service.externalizeMessage(sa);
							}
						}
					}
					meta.putMeta(s, invoke);
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
			for (Class<?> s : serviceMap.keySet()) {
				ArrayList<Method> arrayList = serviceMap.get(s);
				try {
					for (Method m : arrayList) {
						Object invoke = m.invoke(annotation);
						if (invoke != m.getDefaultValue()) {
							if (invoke instanceof Class<?>) {
								Class c = (Class<?>) invoke;
								meta.registerService((Class) s,
										s.cast(c.newInstance()));
							}
						}
					}
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
			if (handler != null) {
				handler.handle(annotation, meta);
			}
			for (Class<?>m:fixedMeta.services()){
				meta.registerService((Class)m, fixedMeta.getService(m));
			}
			fixedMeta.lock();
		}

		@SuppressWarnings("rawtypes")
		protected Object newInstance(Annotation annotation, Class<?> class1) {
			Object newInstance = null;
			Method[] methods = annotation.annotationType()
					.getDeclaredMethods();
			Class[] args = new Class[methods.length];
			Object[] argsV = new Object[methods.length];
			Arrays.sort(methods,new Comparator<Method>() {

				
				public int compare(Method o1, Method o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			int a = 0;
			try {
				for (Method m : methods) {
					argsV[a] = m.invoke(annotation);
					args[a++] = m.getReturnType();
				}
				newInstance = class1.getConstructor(args)
						.newInstance(argsV);
			} catch (Exception e) {
				throw new IllegalStateException();
			}
			return newInstance;
		}


	}


	static IdentityHashMap<Class<? extends Annotation>, AnnotationModel> models = new IdentityHashMap<Class<? extends Annotation>, MetaContributor.AnnotationModel>();

	public static void contribute(IWritableMeta meta, AnnotatedElement element) {
		Annotation[] annotations = element.getAnnotations();
		for (Annotation a : annotations) {
			contribute(meta, a);
		}
	}

	public static void contribute(IWritableMeta meta, Annotation annotation) {
		Class<? extends Annotation> annotationType = annotation
				.annotationType();
		if (annotationType==null){
			Class<?>[] interfaces = annotation.getClass().getInterfaces();
			annotationType=(Class<? extends Annotation>) interfaces[0];
		}
		AnnotationModel annotationModel = models.get(annotationType);
		if (annotationModel == null) {
			annotationModel = createAnotationModel(annotationType);
			models.put(annotationType, annotationModel);
		}
		annotationModel.contribute(annotation, meta);
	}

	@SuppressWarnings("rawtypes")
	private static AnnotationModel createAnotationModel(
			Class<? extends Annotation> annotationType) {
		Annotation[] annotations = annotationType.getAnnotations();
		AnnotationModel mdl = new AnnotationModel();
		CustomMetaHandler ch = annotationType
				.getAnnotation(CustomMetaHandler.class);
		ProvidesService serv = annotationType
				.getAnnotation(ProvidesService.class);
		if (ch != null) {
			try {
				CustomHandler<?> newInstance = ch.value().newInstance();
				mdl.handler = newInstance;
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		if (serv != null) {
			mdl.defaultService = serv.serviceClass();
			mdl.implService = serv.implClass();
			mdl.passPars = serv.passParameters();
		}
		models.put(annotationType, mdl);
		Method[] methods = annotationType.getMethods();
		for (Method m : methods) {
			MetaProperty annotation = m.getAnnotation(MetaProperty.class);
			if (annotation != null) {
				mdl.methodMap.put(annotation.key(), m);
			}
			serv = m.getAnnotation(ProvidesService.class);
			if (serv != null) {

				HashMap<Class, ArrayList<Method>> serviceMap = mdl.serviceMap;
				ArrayList<Method> arrayList = serviceMap.get(serv.value());
				if (arrayList == null) {
					arrayList = new ArrayList<Method>();
					serviceMap.put(serv.value(), arrayList);
				}
				arrayList.add(m);
			}
		}
		for (Annotation a : annotations) {
			if (a instanceof Retention) {
				continue;
			}
			if (a instanceof MetaProperties) {
				MetaProperties pm = (MetaProperties) a;
				for (MetaProperty m : pm.value()) {
					contribute(mdl, m);
				}
				continue;
			}
			if (a instanceof MetaProperty) {
				MetaProperty q = (MetaProperty) a;
				contribute(mdl, q);
				continue;
			}			
			contribute(mdl.fixedMeta, a);
		}
		return mdl;
	}

	private static void contribute(AnnotationModel mdl, MetaProperty q) {
		String key = q.key();
		
		boolean boolValue = q.boolValue();
		// if (boolValue){
		mdl.fixedMeta.putMeta(key, boolValue);
		// }
		String value = q.value();
		if (value.length() > 0) {
			mdl.fixedMeta.putMeta(key, (Object) value);
		}
		Class<? extends Object> classValue = q.classValue();
		if (classValue != Object.class) {
			if (q.createInstance()) {
				mdl.instances.put(key, classValue);
				return;
			}
			mdl.fixedMeta.putMeta(key, classValue);
		}
	}
}