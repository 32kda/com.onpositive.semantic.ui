package com.onpositive.businessdroids.ui.editors;

import java.util.Collection;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.dialogs.AbstractSelectDialog;
import com.onpositive.businessdroids.ui.dialogs.StringSelectDialog;
import com.onpositive.businessdroids.ui.themes.ITheme;
import com.onpositive.businessdroids.utils.PrefUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public abstract class AbstractFieldEditor implements IFieldEditor {

	protected IColumn column;
	protected Class<?> type;
	protected static final int MAX_LAST_VALUES = 5;
	protected SharedPreferences prefs;
	protected String prefId;
	protected View control;
	protected final ITheme dialogTheme;

	protected static LayoutParams wrapParams = new LayoutParams(
			android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
			android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0);

	protected TextView labelView;
	public TextView getLabelView() {
		return labelView;
	}

	protected LinearLayout composite;

	public AbstractFieldEditor(ViewGroup layout, IColumn column, String label,
			SharedPreferences prefs, String prefId, ITheme dialogTheme) {
		this.prefs = prefs;
		this.prefId = prefId;
		this.dialogTheme = dialogTheme;
		this.type = column != null ? column.getType() : Object.class;
		this.column = column;
		this.loadPrefs(prefs, prefId);
		Context context = layout.getContext();
		composite = this.createEditorLayout(context);
		appendLabel(label, context, composite);
		View createEditComp = this.createEditComp(this.type, context);
		LayoutParams gridParams = this.createContentLayout();
		gridParams.weight = 1;
		LinearLayout ll0 = appendViewToLayout(context, createEditComp);
		composite.addView(ll0, gridParams);
		this.control = createEditComp;
		this.createButtons(column, composite);
		addFieldToPanel(layout, composite);

	}

	protected LinearLayout appendViewToLayout(Context context,
			View createEditComp) {
		LinearLayout ll0 = new LinearLayout(context);
		ll0.addView(createEditComp, this.getLayoutParams());
		return ll0;
	}

	public void updateLabelLayout(LayoutParams params) {
		params.gravity = getEditorGravity();
		composite.updateViewLayout(labelView, params);
	}

	protected void appendLabel(String label, Context context,
			LinearLayout minLayout) {
		labelView = new TextView(context);
		labelView.setText(label);
		labelView.setGravity(getEditorGravity());
		LayoutParams titleParams = new LayoutParams(
		/* (int) (labelView.getTextSize() * 3) */
		android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		titleParams.gravity = getEditorGravity();
		minLayout.addView(labelView, titleParams);
	}

	protected int getEditorGravity() {
		return Gravity.CENTER;
	}

	protected void addFieldToPanel(ViewGroup layout, LinearLayout minLayout) {
		layout.addView(minLayout, new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	protected LinearLayout createEditorLayout(Context context) {
		LinearLayout minLayout = new LinearLayout(context);
		minLayout.setOrientation(this.getOrientation());
		return minLayout;
	}

	protected int getOrientation() {
		return LinearLayout.HORIZONTAL;
	}

	protected LayoutParams createContentLayout() {
		return new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
	}

	protected LayoutParams getLayoutParams() {
		return new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
	}

	protected abstract void loadPrefs(SharedPreferences prefs2, String prefId2);

	protected Context getContext() {
		return this.control.getContext();
	}

	protected void createButtons(IColumn column2, LinearLayout minLayout) {
		this.addChooser(column2, minLayout);
		this.addClearButton(minLayout);
	}

	protected void addClearButton(LinearLayout minLayout) {
		OnClickListener ll = new OnClickListener() {

			@Override
			public void onClick(View v) {
				AbstractFieldEditor.this.setValue(null);
			}
		};
		ImageButton clearButton = PrefUtil.createClearBtn(this.getContext(),
				ll, this.dialogTheme);
		minLayout.addView(clearButton, AbstractFieldEditor.wrapParams);
	}

	protected void addChooser(IColumn column2, LinearLayout minLayout) {
		View existingChooserMin = this.createExistingChooser(column2);
		minLayout.addView(existingChooserMin, AbstractFieldEditor.wrapParams);
	}

	protected abstract View createEditComp(Class<?> type, Context context);

	protected View createExistingChooser(IColumn column2) {
		ImageButton chooserBtn = new ImageButton(this.getContext());
		Drawable icon = this.dialogTheme.getIconProvider().getListIcon(
				this.getContext());
		chooserBtn.setImageDrawable(icon);
		chooserBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Collection<Object> items = AbstractFieldEditor.this
						.getPreviousOptions();
				if (items.size() > 0) {
					final AbstractSelectDialog<Object> selectionDialog = new StringSelectDialog(
							AbstractFieldEditor.this.getContext(), items,
							AbstractFieldEditor.this.dialogTheme);
					selectionDialog
							.setOnDismissListener(new OnDismissListener() {

								@Override
								public void onDismiss(DialogInterface dialog) {
									Object value = selectionDialog.getResult();
									if (value != null) {
										AbstractFieldEditor.this
												.setValue(AbstractFieldEditor.this
														.convertSelectedValue(value));
									}
								}

							});
					selectionDialog.show();
				}
			}
		});
		chooserBtn.setEnabled((this.getPreviousOptions() != null)
				&& !this.getPreviousOptions().isEmpty());
		return chooserBtn;
	}

	protected Object convertSelectedValue(Object value) {
		return value;
	}

	protected abstract Collection<Object> getPreviousOptions();

	public View getView() {
		return control;
	}
}
