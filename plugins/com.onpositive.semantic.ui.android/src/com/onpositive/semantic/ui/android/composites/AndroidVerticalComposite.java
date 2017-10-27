package com.onpositive.semantic.ui.android.composites;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.ui.android.AndroidUIElement;
import com.onpositive.semantic.ui.core.GenericLayoutHints;

public class AndroidVerticalComposite extends AndroidComposite {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5310679687447920975L;

	@Override
	protected View createControl(ICompositeElement<?,?> parent) {
		return new TableLayout(getContext());
	}

	@Override
	protected void adapt(BasicUIElement<View> element) {
		createChild(element);
		View view = element.getControl();
		boolean needsLabel = element.needsLabel();
		TableLayout g = getContentControl();
		GenericLayoutHints layoutHints = element.getLayoutHints();
		int i = layoutHints.getGrabHorizontal() ? LayoutParams.FILL_PARENT
				: LayoutParams.WRAP_CONTENT;
		int j = layoutHints.getGrabVertical() ? LayoutParams.FILL_PARENT
				: LayoutParams.WRAP_CONTENT;
		if (needsLabel) {
			TableRow tableRow = new TableRow(getContext());
			tableRow.setBaselineAligned(false);
			TextView tv = new TextView(getContext());
			tv.setPadding(2, 0, 2, 0);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setText(element.getCaption() + ':');
			tableRow.addView(tv, LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);			
			tableRow.addView(view, i, j);
//			tableRow.setBaselineAlignedChildIndex(1);
			g.setColumnStretchable(1, true);
			if (element instanceof AndroidUIElement)
				((AndroidUIElement) element).setContentParent(tableRow);
			TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			configureLayoutParams(layoutHints, layoutParams);
			g.addView(tableRow, layoutParams);
		} else {
			TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(i, j);
			configureLayoutParams(layoutHints, layoutParams);
			g.addView(view, layoutParams);
//			g.setBaselineAlignedChildIndex(0);
			setBackground( view ) ;
		}				
	}

	protected TableLayout getContentControl() {
		return (TableLayout) getControl();
	}
}
