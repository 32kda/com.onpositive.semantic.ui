package com.onpositive.semantic.ui.android.composites;

import java.util.HashMap;
import java.util.HashSet;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TableLayout;

import com.onpositive.commons.platform.configuration.AndroidPlatform;
import com.onpositive.commons.xml.language.ChildSetter;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.IDisplayable;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.ui.android.customwidgets.OKCancelView;
import com.onpositive.semantic.ui.android.customwidgets.dialogs.OkCancelDialog;

public class AndroidCompositeEditor extends AndroidVerticalComposite implements IPropertyEditor<BasicUIElement<View>>, IDisplayable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object object;


	protected HashMap<String, IPropertyEditor<?>> editors = new HashMap<String, IPropertyEditor<?>>();
	private View contentControl;
	protected HashSet<Runnable> disposeCallbacks = new HashSet<Runnable>();


	public boolean shouldIgnoreChanges() {
		return this.shouldIngoreChanges;
	}

	public boolean isBindingInTitle() {
		return this.bindingInTitle;
	}

	public void setBindingInTitle(boolean bindingInTitle) {
		this.bindingInTitle = bindingInTitle;
	}

	public AndroidCompositeEditor() {
		
	}

	public AndroidCompositeEditor(Object object, boolean autoCommit) {
		final Binding bnd = new Binding(object);
		bnd.setAutoCommit(autoCommit);
		this.setBinding(bnd);
	}

	public AndroidCompositeEditor(IBinding bnd) {		
		this.setBinding(bnd);
	}
	
	@Override
	protected View createControl(ICompositeElement<?, ?> parent) {
		contentControl = super.createControl(parent);
		contentControl.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		//contentControl.setBackgroundColor( Color.YELLOW ) ;
		return contentControl ;
	}
	
	@Override
	protected TableLayout getContentControl() {
		return (TableLayout) contentControl;
	}

	protected void processValueChange(ISetDelta<?> valueElements) {

	}

	
	@Override
	@ChildSetter( value = "uielement" ,
    needCasting = true )
	public void add(BasicUIElement<View> element) {		
		super.add(element);
	}

	protected final void set(Object value, Object newValue) {
		if (newValue == value) {
			return;
		}
		if (value != null) {
			if ((this.object != null) && this.object.equals(newValue)) {
				return;
			}
		}
		
		this.internalSet(newValue);
		
		this.object = newValue;

	}

	protected void internalSet(Object value) {

	}

	public void setObject(Object object) {
		this.binding.setValue(object, null);
	}

	public Object getObject() {
		return this.binding.getValue();
	}

	public void addField(String bindTo) {

	}

	@Override
	protected void onCreate(ICompositeElement<?,?> parent) {
		super.onCreate(parent);
		this.internalSetBinding(this.getBinding());		
	}

	@Override
	public int openWidget() {
		final Context dialogContext = getContext() == null?((AndroidPlatform) Platform.getPlatform()).getContext():getContext();
		int result;
		if (dialogContext instanceof Activity) {
			result = 0;
			openScreen((Activity) dialogContext);
		} else {
			result = openDialog(dialogContext); 
		}
		return result;
	}

	protected int openDialog(final Context dialogContext) {
		OkCancelDialog dialog = new OkCancelDialog(dialogContext) {
			
			@Override
			protected View createContents() {
				AndroidRootComposite rootComposite = new AndroidRootComposite(dialogContext);
				rootComposite.add(AndroidCompositeEditor.this);
				return rootComposite.getControl();
			}
			
			@Override
			protected void performOk() {
				dismiss();
				binding.getRoot().commit();
			}
			
			@Override
			public void dismiss() {
				super.dismiss();
				for (Runnable callback : disposeCallbacks) {
					callback.run();
				}
			}
		};
		dialog.show();
		int result = dialog.getResult();
		return result;
	}
	
	protected void openScreen(final Activity activity) {
		final ViewGroup contentParent = (ViewGroup) activity.findViewById(android.R.id.content);
		final View oldContentView = contentParent.getChildAt(0);
		OKCancelView view = new OKCancelView(activity) {
			
			@Override
			protected void performOk() {
				binding.getRoot().commit();
				closeScreen();
			}
			
			@Override
			protected void performCancel() {
				closeScreen();
			}
			
			protected void closeScreen() {
				for (Runnable callback : disposeCallbacks) {
					callback.run();
				}
				contentParent.removeAllViews();
				activity.setContentView(oldContentView);
				oldContentView.requestFocus();
			}

			@Override
			protected View getContents() {
				AndroidRootComposite rootComposite = new AndroidRootComposite(activity);
				rootComposite.add(AndroidCompositeEditor.this);
				return rootComposite.getControl();
			}
		};
		contentParent.removeAllViews();
		activity.setContentView(view);
	}

	@Override
	public boolean isModal() {
		return false;
	}

	@Override
	public void addDisposeCallback(Runnable r) {
		disposeCallbacks.add(r);
	}

}
