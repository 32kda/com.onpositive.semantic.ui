package com.onpositive.semantic.ui.android.composites;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.FrameLayout.LayoutParams;

import com.onpositive.semantic.model.ui.actions.ContributionManager;
import com.onpositive.semantic.model.ui.actions.ObjectContributionManager;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.ui.android.customwidgets.actionbar.ActionBar;
import com.onpositive.semantic.ui.android.customwidgets.dialogs.ContributionItemDialog;

public class AndroidFormEditor extends AndroidVerticalComposite {

	private static final long serialVersionUID = 2727519400891148359L;
	
	protected ActionBar actionBar;

	protected View contentControl;
	
	protected AndroidFormToolbarManager toolbarManager = new AndroidFormToolbarManager();

	protected ContributionItemDialog customMenuDialog;
	
	@Override
	protected View createControl(ICompositeElement<?, ?> parent) {
		contentControl = new TableLayout(getContext()) {
			@Override
			public boolean onKeyUp(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU) {
					showMenu();
					return true;
				}
				return super.onKeyUp(keyCode, event);
			}
			
		};
		contentControl.setFocusableInTouchMode(true);
		Context context = ((AndroidComposite) parent).getContext();
		actionBar = new ActionBar(context);
		if (parent instanceof AndroidRootComposite) {
			((Activity)context).requestWindowFeature(Window.FEATURE_NO_TITLE);
			actionBar.setTitle(((Activity) context).getTitle());
		}
		if (getCaption() != null && getCaption().length() > 0) {
			actionBar.setTitle(getCaption());
		}
		toolbarManager.setActionBar(actionBar);
		((ViewGroup) contentControl).addView(actionBar, new TableLayout.LayoutParams(android.widget.TableLayout.LayoutParams.FILL_PARENT,android.widget.TableLayout.LayoutParams.WRAP_CONTENT));
		contentControl.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		ScrollView scrollView = new ScrollView(getContext());
		
		scrollView.addView(contentControl);
		scrollView.setFocusableInTouchMode(true);
		return scrollView;
	}
	
	@Override
	protected TableLayout getContentControl() {
		return (TableLayout) contentControl;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <R> R getService(Class<R> clazz) {
		if (IProvidesToolbarManager.class.equals(clazz)) {
			return (R) toolbarManager;
		}
		return super.getService(clazz);
	}

	protected void showMenu() {
		if (customMenuDialog == null) {
			customMenuDialog = new ContributionItemDialog(this.getContext(),getPopupMenuManager().getItems(),"Menu");
		} else {
			customMenuDialog.setItems(getPopupMenuManager().getItems());
		}
		customMenuDialog.show();
	}
	
	@Override
	public boolean needKeystrokePropagation() {
		return true;
	}
	
	@Override
	public boolean propagateKeystroke(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			showMenu();
			return true;
		}
		return false;
	}
	
	@Override
	protected ContributionManager createContributionManager() {
		return new ObjectContributionManager();
	}

}
