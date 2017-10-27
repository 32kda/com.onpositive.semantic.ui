package com.onpositive.businessdroids.mainmenu;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridView;

public class MenuButton extends Button {

	protected static final int BASE_PADDING = 10;
	private static final int BUTTON_FONT_SIZE = 16;
	private Drawable icon;

	public MenuButton(Context context, String title, Drawable icon) {
		super(context);
		initButton(title, icon);
	}

	public MenuButton(Context context, AttributeSet attrs, int defStyle, String title, Drawable icon) {
		super(context, attrs, defStyle);
		initButton(title, icon);
	}

	public MenuButton(Context context, AttributeSet attrs, String title, Drawable icon) {
		super(context, attrs);
		initButton(title, icon);
	}

	protected void initButton(String title, Drawable icon) {
		this.icon = icon;
		setText(title);
		setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
		setTextSize(TypedValue.COMPLEX_UNIT_DIP,BUTTON_FONT_SIZE);
		setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		setCompoundDrawablePadding(0);
		setCompoundDrawablesWithIntrinsicBounds( null, icon, null, null );
		setPadding(BASE_PADDING,BASE_PADDING,BASE_PADDING,BASE_PADDING);
		setBackgroundDrawable(new MenuButtonDrawable());
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getParent() == null)
			return;
		int parentHeight = ((View)getParent()).getHeight();
		if (parentHeight > 0 && getParent().getParent() instanceof IRowHeightProvider) {
			int rowHeight = ((IRowHeightProvider)getParent().getParent()).getRowHeight();
			setMeasuredDimension(getMeasuredWidth(), rowHeight);
			int iconHeight = icon.getBounds().bottom - icon.getBounds().top;
			int padding = (int) ((rowHeight - iconHeight - getTextSize()) / 2);
			setPadding(BASE_PADDING,padding,BASE_PADDING,BASE_PADDING);
		}
	}

}
