package com.onpositive.businessdroids.ui.dataview.renderers;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;


public class BooleanRenderer implements IFieldRenderer, IInPlaceEditor,
		IKnowsLongestValue {

	//FIXME
	private Object record;

	@Override
	public View renderField(IField column, Object fieldValue,
			IViewer table, Object object) {
		this.record = object;
		boolean value = fieldValue instanceof Boolean ? (Boolean) fieldValue
				: false;
		CheckBox checkBox = new CheckBox(table.getContext());
		checkBox.setChecked(value);
		checkBox.setGravity(Gravity.CENTER_HORIZONTAL);
		checkBox.setPadding(0, 0, 0, 0);
		checkBox.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER));
		this.configureInPlaceEditor(checkBox, column, table);
		checkBox.setFocusable(false);
		FrameLayout layout = new FrameLayout(table.getContext());
		// layout.setOrientation(LinearLayout.VERTICAL);
		// android.widget.FrameLayout.LayoutParams layoutParams = new
		// LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.CENTER);
		// layoutParams.gravity = Gravity.CENTER;
		// layout.addView(checkBox,layoutParams);
		layout.addView(checkBox);
		return layout;
	}

	@Override
	public void setPropValueToView(View renderedField, IField column,
			Object propertyValue, IViewer table, Object parenObj) {
		if (propertyValue instanceof Boolean) {
			if (renderedField instanceof CheckBox) {
				((CompoundButton) renderedField)
						.setChecked((Boolean) propertyValue);
				this.configureInPlaceEditor(renderedField, column, table);
			} else if (renderedField instanceof TextView) {
				((TextView) renderedField).setText(propertyValue.toString());
			} else if (renderedField instanceof FrameLayout) {
				this.setPropValueToView(
						((FrameLayout) renderedField).getChildAt(0), column,
						propertyValue, table, parenObj);
			} else {
				throw new IllegalArgumentException();
			}
		}

	}

	protected void configureInPlaceEditor(View view, final IField column,
			final IViewer dataView) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v instanceof CheckBox) {
					IField field = column;
					if (!field.isReadOnly(record)) {
						field.setPropertyValue(BooleanRenderer.this.record,
								((CheckBox) v).isChecked());
					}
				}

			}
		});
	}

	@Override
	public Object longestValue(IColumn column, StructuredDataView model) {
		return true;
	}

}
