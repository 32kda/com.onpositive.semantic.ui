package com.onpositive.commons.xml.language;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.w3c.dom.Element;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;

public class Context implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	transient ClassLoader classLoader;

	private final String path;

	private final HashMap<String, Object> objects = new HashMap<String, Object>();

	private int num;

	private IAbstractConfiguration configuration;

	public Object getObject(String id) {
		return this.objects.get(id);
	}

	private final ArrayList<IInitializer> initers = new ArrayList<IInitializer>();

	public void addInitializer(IInitializer initer) {
		this.initers.add(initer);
	}

	void performInit() {
		for (final IInitializer e : this.initers) {
			e.init(this);
		}
	}

	public final IAbstractConfiguration getConfiguration() {
		return this.configuration;
	}

	private ResourceBundle bundle;

	private String id;

	

	public ResourceBundle getBundle() {
		if (this.bundle == null) {
			final int lastIndexOf = this.path.lastIndexOf('/');
			String bName = "messages"; //$NON-NLS-1$
			if (lastIndexOf != -1) {
				bName = this.path.substring(0, lastIndexOf + 1) + bName;
			} else {
				bName = '/' + bName;
			}
			this.bundle = ResourceBundle.getBundle(bName, Locale.getDefault(),
					this.classLoader);
		}
		return this.bundle;
	}

	public Context(String bundlePath) {
		this.path = bundlePath;
	}

	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public String externalize(String substring) {
		if (substring.length() > 0) {
			if (substring.charAt(0) == '%') {
				return substring.substring(1);
			}
			final ResourceBundle bundle2 = this.getBundle();
			if (bundle2 != null) {
				return bundle2.getString(substring);
			}
			return "resource bundle not found for " + this.path; //$NON-NLS-1$
		}
		return substring;
	}

	public void register(String attribute, Object handleElement) {
		this.objects.put(attribute, handleElement);
	}

	public void setConfiguration(IAbstractConfiguration config) {
		this.configuration = config;
	}

	public int getNum() {
		return this.num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Object newInstance(String attribute) {
		try {
			if(attribute.length()==0){
				return null;
			}
			Class<?> loadClass = classLoader.loadClass(attribute);
			return loadClass.newInstance();
		} catch (Exception e) {
			Activator.log(e);
		}
		return null;
	}

	public String getId() {
		return id;
	}

	public final void setId(String id) {
		this.id = id;
	}

	public Object newInstance(Element element, String string) {
		return newInstance(element.getAttribute(string));
	}

	public String getUri() {
		return path;
	}
	
	private void readObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		//FIXME
		classLoader=Context.class.getClassLoader();
	}

	public Class<?> loadClass(String className) throws ClassNotFoundException {
		return classLoader.loadClass(className);
	}
}
