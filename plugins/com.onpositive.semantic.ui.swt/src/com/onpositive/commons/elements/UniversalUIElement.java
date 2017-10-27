package com.onpositive.commons.elements;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class UniversalUIElement<T extends Control> extends AbstractUIElement<T> {

	private final transient Constructor<T> constructor;
	private final transient Class<T> clazz;

	private static HashSet<String> captionAsTextNames = new HashSet<String>();

	static {
		captionAsTextNames.add("Label"); //$NON-NLS-1$
		captionAsTextNames.add("CLabel"); //$NON-NLS-1$
		captionAsTextNames.add("Button"); //$NON-NLS-1$
	}

	public UniversalUIElement(Class<T> clazz, int style) {
		try {
			this.constructor = clazz.getConstructor(Composite.class, int.class);
			this.clazz = clazz;
			super.style = style;
		} catch (final SecurityException e) {
			throw new IllegalArgumentException(e);
		} catch (final NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected T createControl(Composite conComposite) {
		try {
			return this.constructor.newInstance(conComposite, this.calcStyle());
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		} catch (final InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (final IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} catch (final InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public boolean needsLabel() {
		if (!super.needsLabel()) {
			return false;
		}
		final String name = this.clazz.getSimpleName();
		return !captionAsTextNames.contains(name);
	}

}
