/*
 * Copyright (C) 2010 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package greendroid.widget;

import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.onpositive.businessdroids.R;

/**
 * Abstraction of a {@link QuickAction} wrapper. A QuickActionWidget is
 * displayed on top of the user interface (it overlaps all UI elements but the
 * notification bar). Clients may listen to user actions using a
 * {@link OnQuickActionClickListener} .
 * 
 * @author Benjamin Fellous
 * @author Cyril Mottier
 */
public abstract class QuickActionWidget extends PopupWindow {

	private static final int MEASURE_AND_LAYOUT_DONE = 1 << 1;

	private final int[] mLocation = new int[2];
	private final Rect mRect = new Rect();

	private int mPrivateFlags;

	private Context mContext;

	private boolean mDismissOnClick;
	private int mArrowOffsetY;

	private int mPopupY;
	private boolean mIsOnTop;

	private int mScreenHeight;
	private int mScreenWidth;
	private boolean mIsDirty;

	private OnQuickActionClickListener mOnQuickActionClickListener;
	private ArrayList<QuickAction> mQuickActions = new ArrayList<QuickAction>();

	/**
	 * Interface that may be used to listen to clicks on quick actions.
	 * 
	 * @author Benjamin Fellous
	 * @author Cyril Mottier
	 */
	public static interface OnQuickActionClickListener {
		/**
		 * Clients may implement this method to be notified of a click on a
		 * particular quick action.
		 * 
		 * @param position
		 *            Position of the quick action that have been clicked.
		 */
		void onQuickActionClicked(QuickActionWidget widget, int position);
	}

	/**
	 * Creates a new QuickActionWidget for the given context.
	 * 
	 * @param context
	 *            The context in which the QuickActionWidget is running in
	 */
	public QuickActionWidget(Context context) {
		super(context);

		this.mContext = context;

		this.initializeDefault();

		this.setFocusable(true);
		this.setTouchable(true);
		this.setOutsideTouchable(true);
		this.setWidth(300);
		this.setHeight(LayoutParams.WRAP_CONTENT);

		final WindowManager windowManager = (WindowManager) this.mContext
				.getSystemService(Context.WINDOW_SERVICE);
		this.mScreenWidth = windowManager.getDefaultDisplay().getWidth();
		this.mScreenHeight = windowManager.getDefaultDisplay().getHeight();
	}

	/**
	 * Equivalent to {@link PopupWindow#setContentView(View)} but with a layout
	 * identifier.
	 * 
	 * @param layoutId
	 *            The layout identifier of the view to use.
	 */
	public void setContentView(int layoutId) {
		this.setContentView(LayoutInflater.from(this.mContext).inflate(
				layoutId, null));
	}

	private void initializeDefault() {
		this.mDismissOnClick = true;
		this.mArrowOffsetY = this.mContext.getResources()
				.getDimensionPixelSize(R.dimen.gd_arrow_offset);
	}

	/**
	 * Returns the arrow offset for the Y axis.
	 * 
	 * @see {@link #setArrowOffsetY(int)}
	 * @return The arrow offset.
	 */
	public int getArrowOffsetY() {
		return this.mArrowOffsetY;
	}

	/**
	 * Sets the arrow offset to a new value. Setting an arrow offset may be
	 * particular useful to warn which view the QuickActionWidget is related to.
	 * By setting a positive offset, the arrow will overlap the view given by
	 * {@link #show(View)}. The default value is 5dp.
	 * 
	 * @param offsetY
	 *            The offset for the Y axis
	 */
	public void setArrowOffsetY(int offsetY) {
		this.mArrowOffsetY = offsetY;
	}

	/**
	 * Returns the width of the screen.
	 * 
	 * @return The width of the screen
	 */
	protected int getScreenWidth() {
		return this.mScreenWidth;
	}

	/**
	 * Returns the height of the screen.
	 * 
	 * @return The height of the screen
	 */
	protected int getScreenHeight() {
		return this.mScreenHeight;
	}

	/**
	 * By default, a {@link QuickActionWidget} is dismissed once the user
	 * clicked on a {@link QuickAction}. This behavior can be changed using this
	 * method.
	 * 
	 * @param dismissOnClick
	 *            True if you want the {@link QuickActionWidget} to be dismissed
	 *            on click else false.
	 */
	public void setDismissOnClick(boolean dismissOnClick) {
		this.mDismissOnClick = dismissOnClick;
	}

	public boolean getDismissOnClick() {
		return this.mDismissOnClick;
	}

	/**
	 * @param listener
	 */
	public void setOnQuickActionClickListener(
			OnQuickActionClickListener listener) {
		this.mOnQuickActionClickListener = listener;
	}

	/**
	 * Add a new QuickAction to this {@link QuickActionWidget}. Adding a new
	 * {@link QuickAction} while the {@link QuickActionWidget} is currently
	 * being shown does nothing. The new {@link QuickAction} will be displayed
	 * on the next call to {@link #show(View)}.
	 * 
	 * @param action
	 *            The new {@link QuickAction} to add
	 */
	public void addQuickAction(QuickAction action) {
		if (action != null) {
			this.mQuickActions.add(action);
			this.mIsDirty = true;
		}
	}

	/**
	 * Removes all {@link QuickAction} from this {@link QuickActionWidget}.
	 */
	public void clearAllQuickActions() {
		if (!this.mQuickActions.isEmpty()) {
			this.mQuickActions.clear();
			this.mIsDirty = true;
		}
	}

	/**
	 * Call that method to display the {@link QuickActionWidget} anchored to the
	 * given view.
	 * 
	 * @param anchor
	 *            The view the {@link QuickActionWidget} will be anchored to.
	 */
	public void show(View anchor) {

		final View contentView = this.getContentView();

		if (contentView == null) {
			throw new IllegalStateException(
					"You need to set the content view using the setContentView method");
		}

		// Replaces the background of the popup with a cleared background
		this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		final int[] loc = this.mLocation;
		anchor.getLocationOnScreen(loc);
		int anchorWidth = anchor.getWidth();
		this.mRect.set(loc[0], loc[1], loc[0] + anchorWidth,
				loc[1] + anchor.getHeight());

		int x = 0;
		if (this.mIsDirty) {
			this.clearQuickActions();
			this.populateQuickActions(this.mQuickActions);
			x = Math.max(0, loc[0] + (anchorWidth - this.getWidth()) / 2);
		}

		this.onMeasureAndLayout(this.mRect, contentView);

		if ((this.mPrivateFlags & QuickActionWidget.MEASURE_AND_LAYOUT_DONE) != QuickActionWidget.MEASURE_AND_LAYOUT_DONE) {
			throw new IllegalStateException(
					"onMeasureAndLayout() did not set the widget specification by calling"
							+ " setWidgetSpecs()");
		}

		this.showArrow(x, loc[0], anchorWidth);
		this.prepareAnimationStyle();
		this.showAtLocation(anchor, Gravity.NO_GRAVITY, x, this.mPopupY);
	}

	protected void clearQuickActions() {
		if (!this.mQuickActions.isEmpty()) {
			this.onClearQuickActions();
		}
	}

	protected void onClearQuickActions() {
	}

	protected abstract void populateQuickActions(List<QuickAction> quickActions);

	protected abstract void onMeasureAndLayout(Rect anchorRect, View contentView);

	protected void setWidgetSpecs(int popupY, boolean isOnTop) {
		this.mPopupY = popupY;
		this.mIsOnTop = isOnTop;

		this.mPrivateFlags |= QuickActionWidget.MEASURE_AND_LAYOUT_DONE;
	}

	private void showArrow(int x, int anchorX, int anchorWidth) {

		final View contentView = this.getContentView();
		final int arrowId = this.mIsOnTop ? R.id.gdi_arrow_down
				: R.id.gdi_arrow_up;
		final View arrow = contentView.findViewById(arrowId);
		final View arrowUp = contentView.findViewById(R.id.gdi_arrow_up);
		final View arrowDown = contentView.findViewById(R.id.gdi_arrow_down);

		if (arrowId == R.id.gdi_arrow_up) {
			arrowUp.setVisibility(View.VISIBLE);
			arrowDown.setVisibility(View.INVISIBLE);
		} else if (arrowId == R.id.gdi_arrow_down) {
			arrowUp.setVisibility(View.INVISIBLE);
			arrowDown.setVisibility(View.VISIBLE);
		}

		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) arrow
				.getLayoutParams();
		// param.leftMargin = mRect.centerX() - (arrow.getMeasuredWidth()) / 2;
		if (x == 0) {
			param.leftMargin = anchorX + anchorWidth / 2
					- (arrow.getMeasuredWidth()) / 2;
		} else if (x + this.getWidth() > this.mScreenWidth) {
			param.leftMargin = this.getWidth() - (this.mScreenWidth - anchorX)
					+ 20;
		} else {
			param.leftMargin = this.getWidth() / 2 - (arrow.getMeasuredWidth())
					/ 2;
		}
	}

	private void prepareAnimationStyle() {

		final int screenWidth = this.mScreenWidth;
		final boolean onTop = this.mIsOnTop;
		final int arrowPointX = this.mRect.centerX();

		if (arrowPointX <= screenWidth / 4) {
			this.setAnimationStyle(onTop ? R.style.GreenDroid_Animation_PopUp_Left
					: R.style.GreenDroid_Animation_PopDown_Left);
		} else if (arrowPointX >= 3 * screenWidth / 4) {
			this.setAnimationStyle(onTop ? R.style.GreenDroid_Animation_PopUp_Right
					: R.style.GreenDroid_Animation_PopDown_Right);
		} else {
			this.setAnimationStyle(onTop ? R.style.GreenDroid_Animation_PopUp_Center
					: R.style.GreenDroid_Animation_PopDown_Center);
		}
	}

	protected Context getContext() {
		return this.mContext;
	}

	protected OnQuickActionClickListener getOnQuickActionClickListener() {
		return this.mOnQuickActionClickListener;
	}
}
