package com.onpositive.businessdroids.ui.dataview.actions;

import java.util.Collection;

import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.dialogs.ColumnsSelectionDialog;
import com.onpositive.businessdroids.ui.dialogs.ObjectSelectDialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.Drawable;


public class ColumnVisibilityActionContribution extends ActionContribution {

	protected final StructuredDataView dataView;

	public ColumnVisibilityActionContribution(Drawable icon,
			StructuredDataView dataView) {
		super("", icon);
		this.dataView = dataView;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void run() {		
		ColumnsSelectionDialog dialog = new ColumnsSelectionDialog(
				this.dataView.getContext(), this.dataView,
				this.dataView.getCurrentTheme());
		dialog.setOnDismissListener(new OnDismissListener() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void onDismiss(DialogInterface dialog) {
				Collection result = ((ObjectSelectDialog) dialog).getResult();
				if (result != null) {
					ColumnVisibilityActionContribution.this.dataView
							.setVisibleColumns(result);
				}
			}
		});
		dialog.show();
	}

}
