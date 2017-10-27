package com.onpositive.semantic.ui.android;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.MultiAutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView.Tokenizer;
import android.widget.TextView;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.ui.generic.IContentAssistConfiguration;
import com.onpositive.semantic.model.ui.generic.widgets.ITextElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.TextDelegate;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;

public abstract class AndroidAbstractTextEditor extends SimpleAndroidEditor implements ITextElement<View> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Drawable errorMarkDrawable;
	static BitmapDrawable errorImage = new BitmapDrawable(
				AndroidTextEditor.class.getResourceAsStream("error.gif"));
	TextWatcher textWatcher = new TextWatcher() {
	
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				 delegate.handleChange( AndroidAbstractTextEditor.this, getText());
			}
	
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
	
			}
	
			@Override
			public void afterTextChanged(Editable s) {
			}
		};

	@Override
	protected void status(CodeAndMessage cm) {
		View control = getControl();
		if (control != null) {
			if ((cm.getCode() != CodeAndMessage.OK)) {
				String message = cm.getMessage();
				((TextView) control).setError(message, errorMarkDrawable);
			} else {
				((TextView) control).setError(null);
			}
		}
		super.status(cm);
	}

	@Override
	public void onStatus(CodeAndMessage cm) {
		status(cm);
	}

	@Override
	public String getText() {
		if (widget != null) {
			TextView ts = (TextView) widget;
			return ts.getText().toString();
		}
		return super.getText();
	}

	public void setText(String text) {
			if (widget != null) {
				TextView ts = (TextView) widget;
				ts.setText(text);
				return;
			}
	//		super.setText(text); //Don't need calling super because it will goof up editor caption
		}

	private String contentAssistRole;

	public AndroidAbstractTextEditor() {
		super();
	}

	@Override
	protected void resetValue() {
		TextView t = (TextView) getControl();
	
		if (t != null && binding != null) {
			Object value = binding.getValue();
			String text;
			text = LabelAccess.getLabel(getBinding(),value);
			t.setText(text);			
		}
	}

	@Override
	public boolean needsLabel() {
		return true;
	}

	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		errorMarkDrawable = context.getResources().
		getDrawable(R.drawable.custom_indicator_input_error);
	    errorMarkDrawable.setBounds(0, 0, errorMarkDrawable.getIntrinsicWidth(), errorMarkDrawable.getIntrinsicHeight());
		final MultiAutoCompleteTextView textView = new MultiAutoCompleteTextView(context) {
			@Override
			public boolean enoughToFilter() {
				return getBinding() != null && getBinding().getRealm() != null;
			}
			
			@Override
			public void setError(CharSequence error, Drawable icon) {
				if (error == null) {
					if (getBinding() != null && !CodeAndMessage.OK_MESSAGE.equals( getBinding().getStatus()) && hasFocus())
						return;
				}
				super.setError(error, icon);
			}
	
		};
		configureTextView(textView);
		textView.setTokenizer(new Tokenizer() {
	
			@Override
			public int findTokenStart(CharSequence text, int cursor) {
				return 0;
			}
	
			@Override
			public int findTokenEnd(CharSequence text, int cursor) {
				return text.length();
			}
	
			@Override
			public CharSequence terminateToken(CharSequence text) {
				return text;
			}
	
		});
		textView.addTextChangedListener(textWatcher);
		textView.setDropDownWidth(LayoutParams.WRAP_CONTENT);
		IContentAssistConfiguration configuration = getService(IContentAssistConfiguration.class);
		if (configuration == null) {
			RealmBaseAdapter adapter = new RealmBaseAdapter(this, context);
			textView.setAdapter(adapter);
		} else {
			textView.setAdapter(new ContentAssistAdapter(context, this, configuration));
		}
		textView.setEnabled(isEnabled());
		final ViewGroup viewGroup = (ViewGroup)cm.getControl();
		viewGroup.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
			
			@Override
			public void onChildViewRemoved(View parent, View child) {
			}
			
			@Override
			public void onChildViewAdded(View parent, View child) {
				if (child == textView)
					handleViewAdded(viewGroup);
			}

		});
		return textView;
	}

	protected void configureTextView(final MultiAutoCompleteTextView textView) {
		textView.setSingleLine();
		textView.setMinEms(10);
	}
	
	protected void handleViewAdded(final ViewGroup viewGroup) {
	}

	public final String getSeparatorCharacters() {
		return ((TextDelegate)delegate).getSeparatorCharacters();
	}

	public final void setSeparatorCharacters(String separatorCharacters) {
		((TextDelegate)delegate).setSeparatorCharacters(separatorCharacters);		
	}

	@HandlesAttributeDirectly("useLabelsForNull")
	public final void setUseLabelProviderForNull(boolean useLabelProviderForNull) {
		((TextDelegate)delegate).setUseLabelProviderForNull(useLabelProviderForNull);
	}

	public final boolean isUseLabelProviderForNull() {
		return ((TextDelegate)delegate).isUseLabelProviderForNull();		
	}

	@Override
	public String getContentAssistRole() {
		return contentAssistRole;
	}

	@Override
	@HandlesAttributeDirectly("contentAssistRole")
	public void setContentAssistRole(String contentAssistRole) {
		this.contentAssistRole = contentAssistRole;
	}

	protected void onValue(Object value) {
		
	}

}