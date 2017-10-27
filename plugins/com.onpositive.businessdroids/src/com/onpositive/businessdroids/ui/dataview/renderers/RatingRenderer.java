package com.onpositive.businessdroids.ui.dataview.renderers;

import android.R;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AnalogClock;
import android.widget.RatingBar;

import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.ui.IViewer;

public class RatingRenderer implements IFieldRenderer{

	@Override
	public View renderField(IField column, Object fieldValue, IViewer table,
			Object object) {
		RatingBar rs=new RatingBar(table.getContext(),null,R.attr.ratingBarStyleSmall);		
		rs.setIsIndicator(true);
		
		rs.setFocusable(false);		
		if (fieldValue!=null){
		rs.setRating((((Number)fieldValue)).floatValue());
		}
		return rs;
	}

	@Override
	public void setPropValueToView(View renderedField, IField column,
			Object fieldValue, IViewer table, Object parenObj) {
		RatingBar rs=(RatingBar) renderedField;
		if (fieldValue!=null){
		rs.setRating((((Number)fieldValue)).floatValue());
		}
	}

}
