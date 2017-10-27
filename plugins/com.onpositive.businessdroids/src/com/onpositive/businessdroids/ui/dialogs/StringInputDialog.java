package com.onpositive.businessdroids.ui.dialogs;

import com.onpositive.businessdroids.ui.themes.ITheme;
import com.onpositive.businessdroids.utils.PrefUtil;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


public class StringInputDialog extends OkCancelDialog {

	String result = "";
	protected EditText textEdit;

	public StringInputDialog(Context context, int theme, String initialString,
			ITheme dialogTheme) {
		super(context, theme, dialogTheme);
		if (initialString != null) {
			this.result = initialString;
		}
	}

	public StringInputDialog(Context context, String initialString,
			ITheme dialogTheme) {
		this(context, 0, initialString, dialogTheme);
	}

	@Override
	protected View createContents() {
		Context context = this.getContext();
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);

		LayoutParams wrapParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		wrapParams.weight = 0;

		LinearLayout editLayout = new LinearLayout(context);
		editLayout.setOrientation(LinearLayout.HORIZONTAL);
		TextView minLabel = new TextView(context);
		minLabel.setText("Text");
		editLayout.addView(minLabel, new LayoutParams(60,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.textEdit = this.createEditComp(context);
		this.textEdit.setSingleLine();
		LayoutParams gridParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		gridParams.weight = 1;
		LinearLayout ll0 = new LinearLayout(context);
		ll0.addView(this.textEdit, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		editLayout.addView(ll0, gridParams);
		// View existingChooserMin = createExistingChooser(textEdit, context);
		// editLayout.addView(existingChooserMin, wrapParams);
		ImageButton clearButton = PrefUtil.createClearBtn(this.textEdit,
				this.dialogTheme);
		editLayout.addView(clearButton, wrapParams);
		return editLayout;

	}

	protected EditText createEditComp(Context context) {
		EditText editText = new EditText(context);
		editText.setMinimumWidth(200);
		if ((this.result != null) && (this.result.length() > 0)) {
			editText.setText(this.result);
		}
		return editText;
	}

	@Override
	protected void performCancel() {
		this.result = null;
		this.dismiss();
	}

	@Override
	protected void performOk() {
		this.result = this.textEdit.getText().toString();
		this.dismiss();
	}

	public String getResult() {
		return this.result;
	}

}
