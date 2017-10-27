package com.onpositive.businessdroids.ui.actionbar;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onpositive.businessdroids.ui.BD;
import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.actions.ICompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.actions.IHasImage;
import com.onpositive.businessdroids.ui.actions.IHasStatefulImage;
import com.onpositive.businessdroids.ui.themes.ITheme;

public class ActionBar extends RelativeLayout {

	protected ImageButton homeBtn;
	protected Class<? extends Activity> homeActivity;
	protected LinearLayout actions;
	protected ProgressBar progressBar;
	protected ActionContribution homeAction = null;

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	protected TextView titleTextView;
	protected IViewer dataView;
	protected final String title;

	protected HashMap<IContributionItem, View> actionControls = new HashMap<IContributionItem, View>();

	public void addActionView(View v) {
		actions.addView(v);
	}

	public ActionBar(Context context, IViewer dataView, String title) {
		super(context);
		this.dataView = dataView;
		this.title = title;
		this.setPadding(0, 0, 0, 2);
		this.createContents(context);
	}

	public void measure() {

	}

	protected void createContents(Context context) {
		ITheme currentTheme = this.dataView.getCurrentTheme();
		this.homeBtn = new ImageButton(context);
		this.homeBtn.setPadding(0, 0, 0, 0);
		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 0);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		this.homeBtn.setImageDrawable(currentTheme.getIconProvider()
				.getHomeIcon(this.getContext()));
		this.homeBtn.setId(3344);
		this.homeBtn.setBackgroundDrawable(currentTheme
				.getActionBarButtonBackgroundDrawable());
		this.addView(this.homeBtn, params);
		homeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doHome(v);
			}
		});
		boolean b = getContext().getClass() == BD.getHomeActivity(getContext());
		if (b) {
			homeBtn.setVisibility(GONE);
		}
		this.actions = new LinearLayout(context);
		this.actions.setPadding(0, 0, 0, 0);
		this.actions.setId(3343);
		LayoutParams actionLayoutParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		actionLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		this.addView(this.actions, actionLayoutParams);

		this.progressBar = new ProgressBar(context);
		this.progressBar.setPadding(0, 0, 7, 0);
		LayoutParams barLayoutParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		barLayoutParams.addRule(RelativeLayout.LEFT_OF, this.actions.getId());
		barLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		this.progressBar.setVisibility(View.GONE);
		this.progressBar.setId(3345);
		this.addView(this.progressBar, barLayoutParams);

		this.titleTextView = new TextView(context);
		titleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		this.titleTextView.setPadding(10, 0, 10, 0);
		LayoutParams textLayoutParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		textLayoutParams.addRule(RelativeLayout.LEFT_OF,
				this.progressBar.getId());
		if (!b) {
			textLayoutParams.addRule(RelativeLayout.RIGHT_OF,
					this.homeBtn.getId());
		} else {
			textLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		}
		textLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		configureTitle(currentTheme);
		this.addView(this.titleTextView, textLayoutParams);

		this.setTitle(this.title);
		this.setBackgroundDrawable(currentTheme
				.getActionBarBackgroundDrawable());
	}

	protected void doHome(View view) {
		((Activity)getContext()).finish();
		if (homeAction != null) {
			homeAction.onRun();
		} else {
			Class<? extends Activity> homeClass = homeActivity; 
			if (homeClass == null)
				homeClass = BD.getHomeActivity(view
				.getContext());
			if (homeClass != null) {
				Intent intent = new Intent(getContext(), homeClass);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				view.getContext().startActivity(intent);
			} else {
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				view.getContext().startActivity(startMain);
			}
		}
	}

	public int getDesiredWidth() {
		int w0 = homeBtn.getDrawable().getIntrinsicWidth();
		titleTextView.measure(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		return (homeBtn.getVisibility() != GONE ? w0 : 0)
				+ titleTextView.getMeasuredWidth();
	}

	protected void configureTitle(ITheme currentTheme) {
		this.titleTextView.setGravity(Gravity.CENTER);
		this.titleTextView.setTextColor(currentTheme.getActionBarTextColor());
		this.titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, currentTheme.getActionBarTextSize());
		this.titleTextView.setEllipsize(TruncateAt.MARQUEE);
		this.titleTextView.setTypeface(null, Typeface.BOLD);
		this.titleTextView.setMarqueeRepeatLimit(-1);
	}

	public void setTitle(CharSequence title) {
		this.titleTextView.setText(title);
	}

	public String getTitle() {
		return this.titleTextView.getText().toString();
	}

	public void addAction(IContributionItem contribution) {
		if (!this.actionControls.containsKey(contribution)) {
			View actionControl = this.createActionControl(contribution,
					dataView);
			this.actions.addView(actionControl, new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			this.actionControls.put(contribution, actionControl);
		}
	}

	public void removeAction(ActionContribution contribution) {

	}

	public static View createActionControl(
			final IContributionItem contribution, final IViewer viewer) {
		Drawable icon = null;
		if (contribution instanceof IHasImage) {
			icon = ((IHasImage) contribution).getIcon();
		}
		if (icon == null) {
			throw new AssertionError("No icon for action "
					+ contribution.toString());
		}
		final ImageButton[] vs = new ImageButton[1];
		final PropertyChangeListener l = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getPropertyName().equals("selected")) {
					if (vs[0] != null) {
						vs[0].setSelected((Boolean) event.getNewValue());
					}
				} else if (event.getPropertyName().equals("icon")) {
					if (vs[0] != null) {
						vs[0].setImageDrawable((Drawable) event.getNewValue());
					}
				} 
			}
		};

		final ImageButton view = createAppropriateView(contribution, viewer,
				icon, l);
		vs[0] = view;
		contribution.addPropertyChangeListener(l);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (contribution instanceof ActionContribution) {
					((ActionContribution) contribution).onRun();
				} else if (contribution instanceof ICompositeContributionItem) {
					viewer.getCurrentTheme()
							.getContributionPresenter(1,
									(ICompositeContributionItem) contribution)
							.presentContributionItem(contribution, viewer, view);
				}
			}
		});
		if (contribution instanceof IHasStatefulImage) {
			view.setImageDrawable(((IHasStatefulImage) contribution)
					.getStateIcon(viewer));
		}
		view.setBackgroundDrawable(viewer.getCurrentTheme()
				.getActionBarButtonBackgroundDrawable());
		return view;
	}

	private static ImageButton createAppropriateView(
			final IContributionItem contribution, final IViewer viewer,
			Drawable icon, final PropertyChangeListener l) {
		final ImageButton view = new ImageButton(viewer.getContext()) {
			@Override
			protected void onDetachedFromWindow() {
				super.onDetachedFromWindow();
				contribution.removePropertyChangeListener(l);
			}
		};
		view.setPadding(0, 0, 0, 0);
		view.setImageDrawable(icon);
		view.setLayoutParams(new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		if (contribution instanceof ActionContribution) {
			if (((ActionContribution) contribution).getStyle() == ActionContribution.AS_CHECKBOX) {
				ActionContribution ac = (ActionContribution) contribution;
				view.setSelected(ac.isSelected());
			}
		}
		return view;
	}

	public void clearActions() {
		this.actions.removeAllViews();
		this.actionControls.clear();
	}

	public void updateActionStates() {
		for (IContributionItem contribution : this.actionControls.keySet()) {
			View view = this.actionControls.get(contribution);
			if ((contribution instanceof IHasStatefulImage)
					&& (view instanceof ImageView)) {
				((ImageView) view)
						.setImageDrawable(((IHasStatefulImage) contribution)
								.getStateIcon(this.dataView));
			}
		}

	}

	public Class<? extends Activity> getHomeActivity() {
		return homeActivity;
	}

	public void setHomeActivity(Class<? extends Activity> homeActivity) {
		this.homeActivity = homeActivity;
	}

	public ActionContribution getHomeAction() {
		return homeAction;
	}

	public void setHomeAction(ActionContribution homeAction) {
		this.homeAction = homeAction;
		if (homeAction != null)
			homeBtn.setImageDrawable(homeAction.getIcon());
	}

}
