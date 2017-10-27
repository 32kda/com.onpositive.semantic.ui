package com.onpositive.businessdroids.ui.forms;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.onpositive.businessdroids.model.EditorWrapper;
import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.dataview.ImageProviderService;
import com.onpositive.businessdroids.ui.dataview.renderers.ViewRendererService;
import com.onpositive.businessdroids.ui.editors.AbstractFieldEditor;
import com.onpositive.businessdroids.ui.themes.ITheme;

public class EditorsFormPanel extends AbstractViewer {

	protected ImageProviderService imageService = new ImageProviderService();

	protected Object target;

	protected IColumn[] columns;
	protected ScrollView layout;

	protected List<EditorWrapper> wrappers;
	protected SharedPreferences sharedPreferences;

	private LinearLayout buttonBar;

	private LinearLayout mainLayout;

	public EditorsFormPanel(Context context, Object target, IColumn[] columns) {
		super(context);
		setOrientation(VERTICAL);
		this.target = target;
		this.columns = columns;
		wrappers = new ArrayList<EditorWrapper>();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this
				.getContext());
		// currentTheme = new BlueTheme();

	}

	protected void initBGText() {
		/*
		 * this.bgTextDrawable = new BgTextDrawable(
		 * this.currentTheme.getRowDividerColor(), bgText, getPrimaryView());
		 * this.bgTextDrawable.setFontColor(this.currentTheme
		 * .getViewBackgroundFontColor());
		 */
		getPrimaryView().setBackgroundDrawable(
				currentTheme.getRecordBackgroundDrawable(this, 0,
						new int[] { LayoutParams.FILL_PARENT },
						LayoutParams.FILL_PARENT));
	}

	@Override
	public ImageProviderService getImageProviderService() {
		return imageService;
	}

	@Override
	public ViewRendererService getViewRendererService() {
		throw new UnsupportedOperationException();
	}

	protected boolean shouldInit() {
		return this.getWidth() != 0;
	}

	@Override
	protected void initView() {
		if (!shouldInit()) {
			return;
		}
		this.inited = true;
		addActionBar(this);
		layout = new ScrollView(getContext());
		layout.setPadding(0, 0, 0, 0);
		layout.setScrollContainer(true);
		layout.setFillViewport(true);

		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mainLayout = new LinearLayout(getContext());
		mainLayout.setOrientation(LinearLayout.VERTICAL);

		buildFieldList(mainLayout);
		buildButtonBar(mainLayout);
		layout.addView(mainLayout);
		addView(layout, lp);
		initBGText();
		// layout.layout(0, 0, getWidth(), getHeight());

	}

	protected void buildFieldList(ViewGroup group) {
		if (columns != null) {
			for (IColumn c : columns) {
				AbstractFieldEditor createEditor = c
						.getEditorCreationFactory()
						.createEditor(group, c, c.getTitle(),
								sharedPreferences, c.getId(), getCurrentTheme());
				configureView(createEditor.getView(), this);
				wrappers.add(new EditorWrapper(createEditor, c, target));
			}
		}
	}

	protected void buildButtonBar(ViewGroup group) {
		buttonBar = new LinearLayout(getContext());
		buttonBar.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

		appendButtons();
		group.addView(buttonBar, lp);
	}

	protected void appendButtons() {
		Button ok = createOkButton();
		if (ok != null) {
			buttonBar.addView(ok);
		}

		Button cancel = createCancelButton();
		if (cancel != null) {
			buttonBar.addView(cancel);
		}
	}

	protected Button createCancelButton() {
		return null;
	}

	protected Button createOkButton() {
		Button ok = new Button(getContext());
		ok.setText("Submit");
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditorsFormPanel.this.onOkPressed();
			}
		});
		return ok;
	}

	public void onOkPressed() {
		if (wrappers != null) {
			for (EditorWrapper w : wrappers) {
				w.commit();
			}
		}
		return;
	}

	public void onCancelPressed() {

	}

	/*
	 * protected void initBGText() { ViewGroup primaryView = getPrimaryView();
	 * int currTheme = this.currentTheme.getViewBackgroundColor();
	 * this.currentTheme.g primaryView.setBackgroundColor(currTheme);
	 * mainLayout.setBackgroundColor(currTheme);
	 * buttonBar.setBackgroundColor(currTheme);
	 * 
	 * }
	 */

	protected int configureView(View renderedField, AbstractViewer view) {
		ITheme currentTheme = view.getCurrentTheme();
		renderedField.setPadding(currentTheme.getBaseLeftPadding(),
				currentTheme.getBaseTopPadding(),
				currentTheme.getBaseRightPadding(),
				currentTheme.getBaseBottomPadding());
		renderedField.measure(
				View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int currWidth = renderedField.getMeasuredWidth();
		return currWidth;
	}

	public Object getTarget() {
		return target;
	}

}
