package com.onpositive.semantic.ui.android.composites;

import java.util.HashMap;

import android.app.Activity;
import android.view.View;

import com.onpositive.commons.platform.configuration.AndroidClassResolver;
import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.ICanBeReadOnly;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.model.ui.roles.WidgetObject;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class AndroidDynamicEditor extends AndroidAbstractStackComposite
		implements ICanBeReadOnly<View> {

	private static final long serialVersionUID = -7845021723778055115L;

	HashMap<WidgetObject, BasicUIElement<?>> map = new HashMap<WidgetObject, BasicUIElement<?>>();

	private BasicUIElement<View> current;

	private boolean autoCommit = true;

	private boolean readonly = false;
	
	private boolean added = false;

	private BasicUIElement<View> onNull;

	@Override
	protected void internalSetBinding(IBinding binding2) {
		super.internalSetBinding(binding2);
//		binding2.addValueListener(new IValueListener<Object>() {
//
//			private static final long serialVersionUID = 1879232188013960570L;
//
//			@Override
//			public void valueChanged(Object oldValue, final Object newValue) {
//				((Activity) getContext()).runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						internalSetValue(newValue);
//					}
//				});
//			}
//		});
	}

	@SuppressWarnings("unchecked")
	protected void internalSetValue(Object newValue) {
		if (newValue == null) {
			setNull();
			return;
		}
		if (newValue != null) {
			IHasMeta newMeta = MetaAccess.getMeta(newValue);
			((BaseMeta) newMeta).registerService(IClassResolver.class,new AndroidClassResolver(getContext(), getClass().getClassLoader()));
		}
		WidgetObject widgetObject = WidgetRegistry.getInstance()
				.getWidgetObject(newValue, getRole(), getTheme());
		if (widgetObject == null) {
			setNull();
			return;
		}
		BasicUIElement<View> uiElement = (BasicUIElement<View>) map.get(widgetObject);
		if (uiElement != null) {
			IBindable ed = (IBindable) uiElement;
			current = uiElement;
			try {
				ed.getBinding().setValue(newValue);
			} finally {
				this.redraw();
			}
			setTopControl(current);
			this.redraw();
			return;
		}
		Binding bnd = new Binding(newValue) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onChildChanged() {
				AbstractBinding binding2 = (AbstractBinding) AndroidDynamicEditor.this
						.getBinding();
				if (binding2 != null) {
					binding2.onChildChanged();
				}
			}
		};
		bnd.setAutoCommit(autoCommit);
		bnd.setReadOnly(readonly);
		IUIElement<?> createdWidget = widgetObject.createWidget(bnd);
		// if (bnd.getUndoContext() == null) {
		// IUndoManager undoRedoChangeManager =
		// UndoRedoSupport.getUndoRedoChangeManager();
		// if (undoRedoChangeManager!=null){
		// bnd.setUndoContext(new ObjectUndoContext(bnd.getRoot()));
		// }
		// }
		current = (BasicUIElement<View>) createdWidget;
		add((BasicUIElement<View>) current);
		map.put(widgetObject, current);
		setTopControl(current);
		this.redraw();
	}
	
	@Override
	protected void processValueChange(final ISetDelta<?> valueElements) {
		((Activity) getContext()).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Object newValue = valueElements.getFirstAddedElement();
				internalSetValue(newValue);
			}
		});
	}

	protected void setNull() {
		if (onNull != null) {
			current = (BasicUIElement<View>) onNull;
			if (!added) {
				add(current);
				added = true;
			}
			setTopControl(current);
			requestLayout();
		}
		else{
			current=null;
			requestLayout();
		}
	}

	protected void requestLayout() {
		if (isCreated()) {
			getRoot().getControl().requestLayout();
		}
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	@Override
	public void setReadOnly(boolean readonly) {
		this.readonly = readonly;
	}

	@Override
	public boolean isReadOnly() {
		return readonly;
	}
	
	public BasicUIElement<?> getOnNull() {
		return onNull;
	}

	public void setOnNull(BasicUIElement<View> onNull) {
		this.onNull = onNull;
		if (this.isCreated() && getBinding() != null) {
			if (getBinding().getValue() == null) {
				setNull();
			}
		}
	}

}
