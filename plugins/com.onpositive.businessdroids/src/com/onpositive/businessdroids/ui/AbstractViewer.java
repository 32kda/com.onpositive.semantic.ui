package com.onpositive.businessdroids.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.onpositive.businessdroids.ui.actionbar.ActionBar;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItemProvider;
import com.onpositive.businessdroids.ui.dataview.renderers.impl.BgTextDrawable;
import com.onpositive.businessdroids.ui.themes.BlackTheme;
import com.onpositive.businessdroids.ui.themes.ITheme;

public abstract class AbstractViewer extends LinearLayout implements IViewer {

	private static final String THEME_PREF_KEY = "current.theme";
	protected boolean actionBarVisible = true;
	protected ActionBar actionBar;
	protected boolean inited;
	protected ITheme currentTheme = new BlackTheme();
	private String title;

	protected BgTextDrawable bgTextDrawable;
	protected String bgText = "";

	public boolean isActionBarVisible() {
		return actionBarVisible;
	}

	protected boolean inProgress;
	protected boolean actionBarProgress;

	public boolean isActionBarProgress() {
		return actionBarProgress;
	}

	protected void loadThemePrefs() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.getContext());
		String mins = sharedPreferences.getString(THEME_PREF_KEY, null);
		if (mins != null) {
			try {
				ITheme theme = (ITheme) Class.forName(mins).newInstance();
				currentTheme = theme;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void saveThemePref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.getContext());
		Editor edit = sharedPreferences.edit();
		edit.putString(THEME_PREF_KEY, currentTheme.getClass().getName());
		edit.commit();

	}

	protected void setActionBarProgress(boolean actionBarProgress) {
		if (this.actionBarProgress != actionBarProgress) {
			this.actionBarProgress = actionBarProgress;
			if (actionBar != null) {
				actionBar.getProgressBar().setVisibility(
						actionBarProgress ? View.VISIBLE : View.GONE);
			}
		}
	}

	protected void setInProgress(boolean state) {
		inProgress = state;
		if (isInited()) {
			if (state) {
				removeAllViews();
				addActionBar(this);
				LinearLayout ls = new LinearLayout(getContext());
				ProgressBar bs = new ProgressBar(getContext());
				LayoutParams layoutParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.weight = 0;
				layoutParams.gravity = Gravity.CENTER_VERTICAL;
				LayoutParams layoutParams1 = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams1.weight = 1;
				layoutParams1.gravity = Gravity.CENTER;
				doExtraProgressConfig(ls, bs);
				ls.addView(bs, layoutParams);
				addView(ls, layoutParams1);
			} else {
				recreate();
			}
		}
	}

	protected void doExtraProgressConfig(LinearLayout ls, ProgressBar bs) {

	}

	protected void initBGText() {
		this.bgTextDrawable = new BgTextDrawable(
				this.currentTheme.getViewBackgroundColor(), bgText,
				getPrimaryView());
		this.bgTextDrawable.setFontColor(this.currentTheme
				.getViewBackgroundFontColor());
		getPrimaryView().setBackgroundDrawable(this.bgTextDrawable);
	}

	protected ViewGroup getPrimaryView() {
		return this;
	}

	protected void removeEmptyMsg() {
		bgText = "";
		if (bgTextDrawable != null) {
			this.bgTextDrawable.setText("");
		}
	}

	protected void setEmptyMsg() {
		String text = "No items to display";
		bgText = text;
		if (bgTextDrawable != null) {
			this.bgTextDrawable.setText(text);
		}
	}

	protected void setFilteredMsg() {
		String text = "All items filtered out";
		bgText = text;
		if (bgTextDrawable != null) {
			this.bgTextDrawable.setText(text);
		}
	}

	public void setActionBarVisible(boolean actionBarVisible) {
		this.actionBarVisible = actionBarVisible;
		if (isInited()) {
			recreate();
		}
	}

	public boolean isInited() {
		return this.inited;
	}

	public AbstractViewer(Context context) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		loadThemePrefs();
		recreate();
	}

	public AbstractViewer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (shouldRecreateOnResize()) {
			this.recreate();
		}
	}

	protected void recreate() {
		if (inProgress) {
			setInProgress(true);
			return;
		}
		if (inited) {
			bottomBarItems.clear();
			this.removeAllViews();
		}
		this.initView();
	}

	protected abstract void initView();

	protected IContributionItemProvider contributionItemProvider;
	private int minAHeight;

	protected void addActionBar(ViewGroup parent) {
		int width = getWidth();
		
		if( this.actionBar == null )
			this.actionBar = new ActionBar(getContext(), this, this.getTitle());
		if (this.actionBarVisible) {
			LayoutParams params = new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

			// params.height=20;
			params.weight = 0;
			actionBar.getProgressBar().setVisibility(
					actionBarProgress ? View.VISIBLE : View.GONE);
			parent.addView(this.actionBar, 0, params);
			this.actionBar.measure(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			minAWidth = actionBar.getDesiredWidth();
			minAHeight = this.actionBar.getMeasuredHeight();
			resetCommonContributions(contributionItemProvider, width, minAWidth);
		} else {
			this.resetCommonContributions(this.contributionItemProvider, width,
					0);
		}
	}

	protected LinearLayout bottomBar;
	protected boolean showBottomBar;
	protected int minAWidth;

	public int getMinimalActionBarWidth() {
		return minAWidth;
	}

	public int getMinimalActionBarHeight() {
		return minAHeight;
	}

	public boolean isShowBottomBar() {
		return showBottomBar;
	}

	public void setShowBottomBar(boolean showBottomBar) {
		this.showBottomBar = showBottomBar;
		if (isInited()) {
			recreate();
		}
	}

	protected ArrayList<IContributionItem> bottomBarItems = new ArrayList<IContributionItem>();

	protected void addBottomBarIfNeeded() {
		if (bottomBarItems.size() > 0) {
			bottomBar = new LinearLayout(getContext());
			bottomBar.setBackgroundDrawable(getCurrentTheme()
					.getActionBarBackgroundDrawable());
			for (IContributionItem i : bottomBarItems) {
				bottomBar.addView(ActionBar.createActionControl(i, this));
			}
			addView(bottomBar, LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
		} else {
			bottomBar = null;
		}
	}

	protected void hideBottomBar() {
		removeView(bottomBar);
		bottomBar = null;
	}

	public void setContributionItemProvider(
			IContributionItemProvider contributionItemProvider) {
		this.contributionItemProvider = contributionItemProvider;
		this.resetCommonContributions(contributionItemProvider, getWidth(),
				getMinActionBarWidth());
	}

	private int getMinActionBarWidth() {
		return minAWidth;
	}

	public IContributionItemProvider getContributionItemProvider() {
		return contributionItemProvider;
	}

	protected void resetCommonContributions(
			IContributionItemProvider contributionItemProvider, int width,
			int measuredWidth) {
		if (!this.inited) {
			return;
		}
		if (this.actionBar != null) {
			this.actionBar.clearActions();
		}
		if (contributionItemProvider != null) {
			List<IContributionItem> contributionItems = contributionItemProvider
					.getCommonContributionItems();
			int mn = getWidth();
			int hn = getHeight();
			boolean portrait = hn > mn;
			bottomBarItems.clear();
			int ma = width - measuredWidth;

			for (IContributionItem iContributionItem : contributionItems) {
				IContributionItem item = iContributionItem;
				if (item.isEnabled()) {
					int intrinsicWidth = item.getIcon().getIntrinsicWidth();
					ma -= intrinsicWidth;
					if (ma < 0 && portrait) {
						bottomBarItems.add(item);
					} else {
						this.actionBar.addAction(item);
					}
				}
			}
		}
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
		if (this.inited) {
			this.recreate();
		}
	}

	protected boolean shouldRecreateOnResize() {
		return !inited;
	}

	public void setCurrentTheme(ITheme currentTheme) {
		if (currentTheme == null||currentTheme==this.currentTheme) {
			return;
		}
		this.currentTheme = currentTheme;
		saveThemePref();
		this.recreate();
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public ITheme getCurrentTheme() {
		return currentTheme;
	}

	public ActionBar getActionBar() {
		return actionBar;
	}
}