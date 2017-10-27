package com.onpositive.businessdroids.ui.dialogs;

import java.util.Collection;

import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.filters.BasicStringFilter;
import com.onpositive.businessdroids.ui.themes.ITheme;
import com.onpositive.businessdroids.utils.PrefUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


public class StringFilterDialog extends OkCancelDialog {

	protected static final String PREV_VALUES_KEY = "PREV_STRING_FILTER_VALUES";

	protected final BasicStringFilter filter;
	protected EditText textEdit;

	protected Collection<String> prevValues;

	protected final TableModel tableModel;

	public StringFilterDialog(Context context, BasicStringFilter filter,
			TableModel tableModel, ITheme dialogTheme) {
		super(context, dialogTheme);
		this.filter = filter;
		this.tableModel = tableModel;
		this.setTitle(dialogTheme.getLabelProvider().getTextFilterDialogTitle());
	}

	@Override
	protected View createContents() {
		this.loadPrefs();
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
		View existingChooserMin = this.createExistingChooser(this.textEdit,
				context);
		editLayout.addView(existingChooserMin, wrapParams);
		ImageButton clearButton = PrefUtil.createClearBtn(this.textEdit,
				this.dialogTheme);
		editLayout.addView(clearButton, wrapParams);

		return editLayout;
	}

	protected View createExistingChooser(final EditText editView,
			Context context) {
		ImageButton chooserBtn = new ImageButton(editView.getContext());
		Drawable icon = this.dialogTheme.getIconProvider().getListIcon(
				editView.getContext());
		chooserBtn.setImageDrawable(icon);
		chooserBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				@SuppressWarnings("rawtypes")
				Collection items = StringFilterDialog.this.prevValues;
				if (items.size() > 0) {
					@SuppressWarnings("unchecked")
					final AbstractSelectDialog<Object> selectionDialog = new StringSelectDialog(
							StringFilterDialog.this.getContext(), items,
							StringFilterDialog.this.dialogTheme);
					selectionDialog
							.setOnDismissListener(new OnDismissListener() {

								@Override
								public void onDismiss(DialogInterface dialog) {
									String value = (String) selectionDialog
											.getResult();
									if (value != null) {
										editView.setText(value);
									}
								}
							});
					selectionDialog.show();
				}
			}
		});
		return chooserBtn;
	}

	protected EditText createEditComp(Context context) {
		EditText editText = new EditText(context);
		editText.setMinimumWidth(200);
		String string = this.filter.getString();
		if ((string != null) && (string.length() > 0)) {
			editText.setText(string);
		}
		return editText;
	}

	@Override
	protected void performOk() {
		String text = this.textEdit.getText().toString();
		if (text.length() > 0) {
			this.prevValues.add(text);
			this.filter.setText(text);
			this.tableModel.addFilter(this.filter);
		} else {
			this.tableModel.removeFilter(this.filter);
		}
		this.dismiss();
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.savePrefs();
	}

	protected void loadPrefs() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.getContext());
		String mins = sharedPreferences.getString(
				StringFilterDialog.PREV_VALUES_KEY, "");
		this.prevValues = PrefUtil.getSetFromString(mins);
	}

	protected void savePrefs() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.getContext());
		Editor editor = sharedPreferences.edit();
		editor.putString(StringFilterDialog.PREV_VALUES_KEY,
				PrefUtil.getStringFromCollection(this.prevValues));
		editor.commit();
	}

}
