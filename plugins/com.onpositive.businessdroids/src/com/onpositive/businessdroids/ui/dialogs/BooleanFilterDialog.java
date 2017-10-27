package com.onpositive.businessdroids.ui.dialogs;

import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.filters.BooleanFilter;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class BooleanFilterDialog extends OkCancelDialog {

	protected final BooleanFilter filter;
	private RadioButton trueButton;
	private RadioButton falseButton;

	public BooleanFilterDialog(Context context, BooleanFilter filter,
			ITheme dialogTheme) {
		this(context, 0, filter, dialogTheme);
	}

	public BooleanFilterDialog(Context context, int theme,
			BooleanFilter filter, ITheme dialogTheme) {
		super(context, theme, dialogTheme);
		this.filter = filter;
		this.setTitle(dialogTheme.getLabelProvider()
				.getBooleanFilterDialogTitle());
	}

	@Override
	protected View createContents() {
		Context context = this.getContext();
		RadioGroup radioGroup = new RadioGroup(context);
		radioGroup.setOrientation(LinearLayout.VERTICAL);
		this.trueButton = new RadioButton(context);
		this.trueButton.setText(this.dialogTheme.getLabelProvider()
				.getTrueTitle());
		radioGroup.addView(this.trueButton);
		this.falseButton = new RadioButton(context);
		this.falseButton.setText(this.dialogTheme.getLabelProvider()
				.getFalseTitle());
		radioGroup.addView(this.falseButton);
		this.trueButton.setChecked(this.filter.isValue());
		this.falseButton.setChecked(!this.filter.isValue());
		return radioGroup;
	}

	@Override
	protected void performOk() {
		TableModel tableModel = this.filter.getTableModel();
		this.filter.setValue(this.trueButton.isChecked());
		tableModel.addFilter(this.filter);
		this.dismiss();
	}

}
