package com.onpositive.businessdroids.ui.actions.presenters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;
import com.onpositive.businessdroids.model.groups.SimpleFieldGroupingCalculator;
import com.onpositive.businessdroids.model.impl.AbstractComputableField;
import com.onpositive.businessdroids.model.impl.BasicFieldComparator;
import com.onpositive.businessdroids.model.impl.BasicTableModel;
import com.onpositive.businessdroids.model.impl.Column;
import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.IFieldImageProvider;
import com.onpositive.businessdroids.ui.IOpenListener;
import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.actions.IExtendedContributionItem;
import com.onpositive.businessdroids.ui.dataview.DataViewFactory;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.dialogs.ContributionItemDialog;

public class TreeContributionPresenter extends AbstractContributionPresenter {

	@SuppressWarnings("unchecked")
	@Override
	protected void doPresent(final IContributionItem item, final IViewer dataView,
			IContributionItem[] enabled, View anchor) {
		Column groupField = new Column(new AbstractComputableField() {

			@Override
			public Object getPropertyValue(Object object) {
				if (object instanceof IExtendedContributionItem) {
					IExtendedContributionItem es = (IExtendedContributionItem) object;
					return es.getGroupId();
				}
				return null;
			}
		});
		groupField.setComparator(new BasicFieldComparator(groupField, false){
			@Override
			public Object getValue(Object object1) {
				Object value = super.getValue(object1);
				if (value!=null){
					return value.toString().toLowerCase();
				}
				return value;
			}
		});
		groupField.setCaption(true);
		IField[] listFields = { new AbstractComputableField() {

			@Override
			public Object getPropertyValue(Object object) {
				if (object == null) {
					return "none";
				}
				String i = ((IContributionItem) object).getText();
				return i;
			}
		} };
		BasicTableModel tm = new BasicTableModel(
				new IColumn[] { DataViewFactory.createMultiColumn(new IFieldImageProvider() {
									
									@Override
									public Bitmap getImage(Context context, Object object, IField field,
											Object fvalue) {
										if (object instanceof IContributionItem){
											IContributionItem z=(IContributionItem) object;
											Drawable icon = z.getIcon();
											if (icon instanceof BitmapDrawable){
												BitmapDrawable bm=(BitmapDrawable) icon;
												return bm.getBitmap();
											}
											return null;
										}
										return null;
									}
								}, listFields) });
		tm.addAll((Collection<Object>) (List) Arrays.asList(enabled));
		if (groupField != null) {
			tm.setCurrentGrouping(null);
			tm.setGroupSortField(groupField);
		}
		StructuredDataView ds = new StructuredDataView(dataView.getContext(), tm);
		ds.setActionBarVisible(false);
		ds.setHeaderVisible(false);
		ds.setFooterVisible(false);
		final StructuredDataView createList = ds;	
		
		IGroupingCalculator currentGroupingCalculator=new SimpleFieldGroupingCalculator(groupField);
		createList.getTableModel().setCurrentGrouping(currentGroupingCalculator);
		View rootView = dataView.getView().getRootView();		
		int width = rootView.getWidth();
		int height = rootView.getHeight();
		rootView.setMinimumHeight(height - 20);
		TableModel tableModel = createList.getTableModel();
		tableModel.sort(tableModel.getColumns()[0],true);
		rootView.setMinimumWidth(width - 20);
		createList.setPadding(5, 5, 5, 5);//FIXME
		WindowManager wm = (WindowManager) rootView.getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		final int heightD=(display.getHeight()*2)/3;
		createList.setBackgroundDrawable(dataView.getCurrentTheme().getRecordBackgroundDrawable((AbstractViewer) dataView, 0, new int[100],heightD));
		final ContributionItemDialog m = new ContributionItemDialog(
				dataView.getContext(), enabled, item.getText(), dataView) {

			protected boolean needScrollView() {
				return false;
			}
			

			@Override
			protected View createContents() {
				return createList;
			}
			protected android.widget.LinearLayout.LayoutParams getRootParam() {
				return new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, heightD);
			}
			protected LinearLayout.LayoutParams doOk() {
				LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				return wrapParams;
			}

		};
		createList.setOpenListener(new IOpenListener() {

			@Override
			public void openElement(View parent, View elementView,
					Object element, int position ) {
				if (element instanceof IContributionItem) {
					IContributionItem i = (IContributionItem) element;
					processSingle(i, elementView, dataView);
				}
				m.dismiss();
			}
		});
		m.show();
	}
}
