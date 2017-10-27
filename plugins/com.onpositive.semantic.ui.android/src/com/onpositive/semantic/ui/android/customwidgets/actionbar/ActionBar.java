package com.onpositive.semantic.ui.android.customwidgets.actionbar;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onpositive.semantic.model.ui.actions.ContributionItem;
import com.onpositive.semantic.model.ui.actions.IAction;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.IContributionManager;
import com.onpositive.semantic.ui.android.AndroidImageManager;
import com.onpositive.semantic.ui.android.R;
import com.onpositive.semantic.ui.android.customwidgets.actions.presenters.DialogContributionPresenter;

public class ActionBar extends RelativeLayout {

	protected ImageButton homeBtn;
	protected Class<? extends Activity> homeActivity;
	protected LinearLayout actions;
	protected ProgressBar progressBar;
	protected IAction homeAction = null;

	protected TextView titleTextView;
	protected final String title;
	protected HashMap<IContributionItem, View> actionControls = new HashMap<IContributionItem, View>();

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public void addActionView(View v) {
		actions.addView(v);
	}
	
	public ActionBar(Context context) {
		this(context,"");
	}

	public ActionBar(Context context, String title) {
		super(context);
		this.title = title;
		this.setPadding(0, 0, 0, 2);
		this.createContents(context);
	}

	protected void createContents(Context context) {
		this.homeBtn = new ImageButton(context);
		this.homeBtn.setPadding(0, 0, 0, 0);
		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 0);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		this.homeBtn.setImageDrawable(getHomeIcon());
		this.homeBtn.setId(3344);
		this.homeBtn.setBackgroundDrawable(getActionBarButtonBackgroundDrawable());
		this.addView(this.homeBtn, params);
		homeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doHome(v);
			}
		});
		boolean b = getContext().getClass() == HomeActivityManager.getHomeActivity(getContext());
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
		configureTitle();
		this.addView(this.titleTextView, textLayoutParams);

		this.setTitle(this.title);
		this.setBackgroundDrawable(getActionBarBackgroundDrawable());
	}

	private Drawable getActionBarBackgroundDrawable() {
		return new HatchDrawable();
	}

	protected static Drawable getActionBarButtonBackgroundDrawable() {
		return  new ButtonStateDrawable(
				new ColorDrawable(0), new ColorDrawable(0xaa0055ff), null);
	}

	private Drawable getHomeIcon() {
		return new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.home));
	}

	protected void doHome(View view) {
		((Activity)getContext()).finish();
		if (homeAction != null) {
			homeAction.run();
		} else {
			Class<? extends Activity> homeClass = homeActivity; 
			if (homeClass == null) {
				homeClass = HomeActivityManager.getHomeActivity(view
				.getContext());
			}
			if (homeClass == null) {
				return;
			}
			Intent intent = new Intent(getContext(), homeClass);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			view.getContext().startActivity(intent);
		}
	}

	public int getDesiredWidth() {
		int w0 = homeBtn.getDrawable().getIntrinsicWidth();
		titleTextView.measure(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		return (homeBtn.getVisibility() != GONE ? w0 : 0)
				+ titleTextView.getMeasuredWidth();
	}

	protected void configureTitle() {
		this.titleTextView.setGravity(Gravity.CENTER);
		this.titleTextView.setTextColor(Color.WHITE);
		this.titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
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
			View actionControl = this.createActionControl(contribution);
			this.actions.addView(actionControl, new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			this.actionControls.put(contribution, actionControl);
		}
	}

	public void removeAction(IContributionItem contribution) {

	}

	public View createActionControl(
			final IContributionItem contribution) {
		Drawable icon = null;
		if (contribution instanceof IAction) {
			icon = AndroidImageManager.getImageDrawable(((IAction) contribution).getImageDescriptor());
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
				} else if (event.getPropertyName().equals(ContributionItem.VISIBLE_PROP_ID)) {
					if (vs[0] != null) {
						boolean visible = (Boolean) event.getNewValue();
						vs[0].setVisibility(visible?VISIBLE:GONE);
					}
				} else if (event.getPropertyName().equals(ContributionItem.ENABLED_PROP_ID)) {
					if (vs[0] != null) {
						vs[0].setEnabled((Boolean) event.getNewValue());
					}
				} 
			}
		};

		final ImageButton view = createAppropriateView(contribution, icon,
				l);
		vs[0] = view; 
		contribution.addPropertyChangeListener(l);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((IAction) contribution).getStyle() == IAction.AS_CHECK_BOX) {
					view.setSelected(!view.isSelected());
				}
				if (contribution instanceof IContributionManager) {
					new DialogContributionPresenter(v.getContext()).presentContributionItem(contribution,view);
				} else if (contribution instanceof IAction) {
					((IAction) contribution).run();
				} 
			}
		});
//		if (contribution instanceof IHasStatefulImage) {
//			view.setImageDrawable(((IHasStatefulImage) contribution)
//					.getStateIcon(viewer));
//		}
		view.setBackgroundDrawable(getActionBarButtonBackgroundDrawable());
		view.setEnabled(contribution.isEnabled());
		view.setVisibility(contribution.isVisible()?View.VISIBLE:View.GONE);
		return view;
	}

	private ImageButton createAppropriateView(
			final IContributionItem contribution, Drawable icon,
			final PropertyChangeListener listener) {
		final ImageButton view = new ImageButton(getContext()) {
			@Override
			protected void onDetachedFromWindow() {
				super.onDetachedFromWindow();
				contribution.removePropertyChangeListener(listener);
			}
		};
		view.setPadding(0, 0, 0, 0);
		view.setImageDrawable(icon);
		view.setLayoutParams(new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		if (contribution instanceof IAction) {
			if (((IAction) contribution).getStyle() == IAction.AS_CHECK_BOX) {
				IAction ac = (IAction) contribution;
				view.setSelected(ac.getSelection());
			}
		}
		return view;
	}

	public void clearActions() {
		this.actions.removeAllViews();
		this.actionControls.clear();
	}

	public void updateActionStates() {
//		for (IContributionItem contribution : this.actionControls.keySet()) { //TODO update support
//			View view = this.actionControls.get(contribution);
//			if ((contribution instanceof IHasStatefulImage)
//					&& (view instanceof ImageView)) {
//				((ImageView) view)
//						.setImageDrawable(((IHasStatefulImage) contribution)
//								.getStateIcon(this.dataView));
//			}
//		}

	}

	public Class<? extends Activity> getHomeActivity() {
		return homeActivity;
	}

	public void setHomeActivity(Class<? extends Activity> homeActivity) {
		this.homeActivity = homeActivity;
	}

	public IAction getHomeAction() {
		return homeAction;
	}

	public void setHomeAction(IAction homeAction) {
		this.homeAction = homeAction;
		if (homeAction != null) {
			homeBtn.setImageDrawable(AndroidImageManager.getImageDrawable(homeAction.getImageDescriptor()));
		}
	}

}
