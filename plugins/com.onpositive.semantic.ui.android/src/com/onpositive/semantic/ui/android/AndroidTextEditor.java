package com.onpositive.semantic.ui.android;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.ISingleLineTextElement;
import com.onpositive.semantic.ui.core.Alignment;

public class AndroidTextEditor extends AndroidAbstractTextEditor implements ISingleLineTextElement<View>{

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -6761960308803074596L;
	public AndroidTextEditor() {
		getLayoutHints().setGrabHorizontal(true);
		getLayoutHints().setAlignmentVertical(Alignment.FILL);
	}

	IFunction selector;
	private Button selectorButton;
	@Override
	protected void internalSetBinding(IBinding binding2) {
		if (binding != null) {
			super.internalSetBinding(binding);
			this.selector = binding.getElementFactory();
			if (this.isCreated()) {
				this.installSelector((ViewGroup)(this.getParent().getControl()));
			}
		}
	}
	
	void installSelector(ViewGroup viewGroup) {
		if (selector == null)
			return;
		if (viewGroup instanceof LinearLayout) {
			if (selectorButton == null || selectorButton.getVisibility() == View.GONE) {
				if (selectorButton == null) {
					selectorButton = new Button(widget.getContext());
					ViewGroup.LayoutParams layoutParams;
					if (viewGroup instanceof TableRow) //TODO different possible parents support
						layoutParams = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					else
						layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					viewGroup.addView(selectorButton, layoutParams);
				}
				selectorButton.setText(DefaultMetaKeys.getCaption(selector));
				selectorButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Object value = AndroidTextEditor.this.selector
								.getValue(AndroidTextEditor.this.getBinding()
										.getValue());
						if (value != null) {
							onValue(value);
							final ITextLabelProvider adapter = AndroidTextEditor.this
									.getBinding().getAdapter(
											ITextLabelProvider.class);
							if (adapter != null) {
								value = adapter.getText(meta(), getParentObject(), value);
							}
							AndroidTextEditor.this.setText(value.toString());
						}
						
					}
				});
			} else {
				selectorButton.setVisibility(View.GONE);
			}
		}
	}
	

	@Override
	public IFunction getSelector() {
		return selector;
	}

	@Override
	@HandlesAttributeDirectly("buttonSelector")
	public void setSelector(IFunction selector) {
		this.selector = selector;
		
	}

	@Override
	public void setContentParent(ViewGroup contentParent) {
		super.setContentParent(contentParent);
		installSelector(contentParent);
	}
	
	protected void handleViewAdded(final ViewGroup viewGroup) {
		installSelector(viewGroup);
	}
}
