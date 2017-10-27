package com.onpositive.businessdroids.ui.dashboard;

import greendroid.widget.LayoutData;
import greendroid.widget.Page;
import greendroid.widget.PageIndicator;
import greendroid.widget.PagedAdapter;
import greendroid.widget.PagedView;
import greendroid.widget.PagedView.OnPagedViewChangeListener;
import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.onpositive.businessdroids.R;
import com.onpositive.businessdroids.model.IArray;
import com.onpositive.businessdroids.ui.themes.ITheme;

public class DashBoardComp extends LinearLayout {

	LayoutData layoutData;
	int maxElementsCount;

	int pageCount;

	int elementsPerPage;

	private IArray contentProvider;
	private IViewLabelProvider labelProvider;

	PagedView pagedView;
	PageIndicator pageIndicator;
	ITheme t;
	DashBoard owner;

	public DashBoardComp(DashBoard owner,Context context, IArray cp,
			IViewLabelProvider lp) {
		super(context);
		this.contentProvider = cp;
		this.owner=owner;
		this.labelProvider = lp;
		refresh();
		
		
	}

	public DashBoardComp(DashBoard ow, Context context, IArray cp, ILabelProvider lp) {
		super(context, null);
		this.owner=ow;
		this.contentProvider = cp;
		this.labelProvider = new DefaultViewLabelProvider(lp);
		refresh();
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		int width = w;
		int height =h;
		int measuredHeight = 0;
		
		layoutData = new LayoutData(width, height - height / 10
				- measuredHeight, 5, 10);
		elementsPerPage = layoutData.rowsCount * layoutData.columnsCount;
		if (elementsPerPage == 0) {
			elementsPerPage = 1;
		}
		
		final int size = contentProvider.getItemCount();

		int count = size / elementsPerPage;
		if ((float) count < ((float) size / (float) elementsPerPage))
			count++;
		pageCount = count;
		pageIndicator.setDotCount(count);
		pageIndicator.setDotSpacing(10);
	}

	

	public void refresh() {
		removeAllViews();
		pagedView = new PagedView(this.getContext());
		pageIndicator = new PageIndicator(this.getContext());
		// pagedView.setBackgroundColor(Color.WHITE);
		setOrientation(VERTICAL);
		final int size = contentProvider.getItemCount();

		PagedAdapter pa = new PagedAdapter() {

			@Override
			public int getCount() {
				return pageCount;
			}

			@Override
			public Object getItem(int position) {
				return contentProvider.getItem(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Page page = new Page(getContext());
				int i1 = position * elementsPerPage;
				int i2 = i1 + elementsPerPage;
				for (int i = i1; i < i2; i++)
					if (i < size)
						page.addView(labelProvider.viewFor(getContext(),
								contentProvider.getItem(i)));
				page.setLayoutData(layoutData);

				return page;
			}
		};

		pagedView.setOnPageChangeListener(new OnPagedViewChangeListener() {

			public void onPageChanged(PagedView pagedView, int previousPage,
					int newPage) {
				pageIndicator.setActiveDot(newPage);

			}

			public void onStartTracking(PagedView pagedView) {

			}

			public void onStopTracking(PagedView pagedView) {

			}
		});

		pagedView.setAdapter(pa);

		pageIndicator.setDotType(PageIndicator.DotType.SINGLE);

		StateListDrawable sld = new StateListDrawable();
		sld.addState(new int[] { android.R.attr.state_selected }, this
				.getResources().getDrawable(R.drawable.dot1));
		sld.addState(new int[] { -android.R.attr.state_selected }, this
				.getResources().getDrawable(R.drawable.dot2));

		pageIndicator.setDotDrawable(sld);

		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.weight=9;
		addView(pagedView,layoutParams);
		LayoutParams layoutParams2 = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams2.weight=1;
		layoutParams2.bottomMargin=5;
		setWeightSum(10);
		addView(pageIndicator,layoutParams2);
	}

}
