package com.onpositive.semantic.ui.android;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;


public class AndroidSpinner extends SimpleAndroidEditor{

	/**
	 * Serial Version UID 
	 */
	private static final long serialVersionUID = 5076507120479575003L;
	private RealmBaseAdapter adapter;

	public AndroidSpinner() {
		getLayoutHints().setGrabHorizontal(true);
	}
	
	@Override
	protected void resetValue() {
		Spinner s=(Spinner) getControl();
		if (s != null && binding != null) {
			Object value = binding.getValue();			
			s.setSelection(adapter.position(value));			
		}
	}

	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		Spinner spinner = new Spinner(context);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				commitToBinding(adapter.getItem(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				commitToBinding(null);
			}
		});
		if (getCaption() != null) {
			spinner.setPrompt(getCaption());
		}
		
		adapter = new RealmBaseAdapter(this, context);
		
		
		spinner.setAdapter(adapter);	
		return spinner;
	}
	
	@Override
	public void setEnabled(boolean val) {
		if (isEnabled()!=val){
			super.setEnabled(val);
			if (adapter!=null){
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public boolean needsLabel() {
		return true;
	}
	
	@Override
	public void setCaption(String caption) {
		super.setCaption(caption);
		if (isCreated()) {
			((Spinner) getControl()).setPrompt(caption);
		}
	}
	
	@Override
	public void processValueChange(ISetDelta<?> valueElements) {
		if (!getBinding().allowsMultiValues()) {
			if (!valueElements.getAddedElements().isEmpty()) {
				this.setSelection(valueElements.getAddedElements()
						.iterator().next());
			} else {
				if (!valueElements.getChangedElements().isEmpty()) {
					this.setSelection(valueElements
							.getChangedElements().iterator().next());
				} else {
					if (!valueElements.getRemovedElements().isEmpty()) {
						this.setSelection(null);
					}
				}
			}
		} else {
			this.setSelection(getBinding().getValue());
		}
	}

	protected void setSelection(Object value) {
		Spinner s=(Spinner) getControl();
		if (s != null && value != null) {
			s.setSelection(adapter.position(value));			
		}
		
	}
}
