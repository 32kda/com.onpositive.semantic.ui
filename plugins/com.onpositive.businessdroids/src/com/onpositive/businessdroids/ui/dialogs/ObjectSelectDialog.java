package com.onpositive.businessdroids.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import com.onpositive.businessdroids.ui.themes.IIconProvider;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;


public abstract class ObjectSelectDialog extends OkCancelDialog {

	private static final int MAX_ITEMS_TO_MEASURE = 100;
	protected static final int UPDATE_INTERVAL = 2000;
	protected final List<? extends Object> items;
	protected List<? extends Object> fitems;
	protected LinkedHashSet<Object> result = new LinkedHashSet<Object>();
	protected ArrayAdapter<Object> adapter;
	protected String filterString = "";
	protected ListView listView;
	protected ViewGroup toolbarView;
	// protected ImageButton disableFilter;
	protected EditText editText;

	protected long startTime = 0L;
	protected Handler editHandler = new Handler();
	protected Runnable filterUpdateCallbak = new Runnable() {

		@Override
		public void run() {
			ObjectSelectDialog.this
					.setFilterString(ObjectSelectDialog.this.editText.getText()
							.toString());
			ObjectSelectDialog.this.startTime = 0L;
		}
	};

	@SuppressWarnings("unchecked")
	public ObjectSelectDialog(Context context, int theme,
			List<? extends Object> items,
			Collection<? extends Object> initiallySelected, ITheme dialogTheme) {
		super(context, theme, dialogTheme);
		Collections.sort(items, new Comparator<Object>() {

			@Override
			public int compare(Object object1, Object object2) {
				if ((object1 == null) && (object2 == null)) {
					return 0;
				}
				if (object2 == null) {
					return 1;
				}
				if (object1 == null) {
					return -1;
				}
				try {
					if (object1 instanceof Comparable) {
						return ((Comparable<Object>) object1)
								.compareTo(object2);
					}
				} catch (Exception e) {
					// ignore
				}
				return object1.toString().compareTo(object2.toString());
			}
		});
		this.items = items;
		this.result.addAll(initiallySelected);
		this.setTitle(this.getInitialTitle());
	}

	public ObjectSelectDialog(Context context, List<? extends Object> items,
			Collection<? extends Object> initial, ITheme dialogTheme) {
		this(context, 0, items, initial, dialogTheme);
	}

	@Override
	protected int measureDesiredHeight(View contents) {
		int count = this.adapter.getCount();
		int size = 0;
		Display display = ((WindowManager) contents.getContext()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int height = display.getHeight();
		if (count < ObjectSelectDialog.MAX_ITEMS_TO_MEASURE) {

			for (int a = 0; a < count; a++) {
				if (size >= height) {
					break;
				}
				View view = this.adapter.getView(a, null, this.listView);
				view.measure(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				size += view.getMeasuredHeight();
				;
			}
			return size;
		}
		return Integer.MAX_VALUE;
	}

	public String getInitialTitle() {
		return this.dialogTheme.getLabelProvider().getObjectSelectDilaogTitle();
	}

	protected abstract View createView(Object item);

	protected String getLabel(Object item) {
		return item.toString();
	}

	@Override
	protected boolean needScrollView() {
		return false;
	}

	@Override
	protected View createContents() {
		final Context context = this.getContext();
		LinearLayout resLayout = new LinearLayout(context);
		resLayout.setOrientation(LinearLayout.VERTICAL);
		if (this.items.size() > this.dialogTheme.getMaxFilterDialogItemCount()) {
			resLayout.addView(this.createFilterControl(context),
					new LayoutParams(
							android.view.ViewGroup.LayoutParams.FILL_PARENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			// resLayout.addView(createToolButtonBar(context), new
			// LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		}
		// LayoutParams wrapParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);
		// wrapParams.weight = 0;
		this.listView = new ListView(context);

		this.adapter = this
				.configureAdapter(context, this.listView, this.items);
		ViewGroup listGroup;
		listGroup = new LinearLayout(context);
		LayoutParams gridParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		gridParams.weight = 1;
		listGroup.addView(this.listView, gridParams);
		resLayout.addView(listGroup, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		return resLayout;
	}

	protected View createFilterControl(Context context) {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		this.editText = new EditText(context);
		this.editText.setSingleLine();
		this.editText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) { // Enter
							ObjectSelectDialog.this.editHandler
									.removeCallbacks(ObjectSelectDialog.this.filterUpdateCallbak);
							ObjectSelectDialog.this
									.setFilterString(ObjectSelectDialog.this.editText
											.getText().toString());
						}
						return false;
					}
				});
		this.editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Do nothing
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Do nothing
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (ObjectSelectDialog.this.startTime == 0L) {
					ObjectSelectDialog.this.startTime = System
							.currentTimeMillis();
					ObjectSelectDialog.this.editHandler
							.removeCallbacks(ObjectSelectDialog.this.filterUpdateCallbak);
					ObjectSelectDialog.this.editHandler.postDelayed(
							ObjectSelectDialog.this.filterUpdateCallbak,
							ObjectSelectDialog.UPDATE_INTERVAL);
				}
			}
		});
		ImageButton filter = new ImageButton(context);
		filter.setImageDrawable(this.dialogTheme.getIconProvider()
				.getSearchIconBlack(context));
		filter.setPadding(0, 0, 0, 0);
		filter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ObjectSelectDialog.this.editHandler
						.removeCallbacks(ObjectSelectDialog.this.filterUpdateCallbak);
				ObjectSelectDialog.this
						.setFilterString(ObjectSelectDialog.this.editText
								.getText().toString());
			}

		});

		if (Number.class.isAssignableFrom(this.getType())) {
			this.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		LayoutParams textParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		textParams.weight = 2;
		layout.addView(this.editText, textParams);
		layout.addView(filter, new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		addSelectDeselectAll(context, layout);
		// FrameLayout resLayout = new FrameLayout(context);
		// //resLayout.addView(layout, new
		// LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		// resLayout.addView(layout);
		layout.setPadding(4, 0, 0, 0);
		return layout;
	}

	protected void addSelectDeselectAll(Context context, LinearLayout layout) {
		IIconProvider iconProvider = this.dialogTheme.getIconProvider();
		ImageButton selectAll = new ImageButton(context);
		selectAll.setImageDrawable(iconProvider.getExpandIcon(context));
		selectAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ObjectSelectDialog.this.selectAll();
			}
		});
		selectAll.setPadding(0, 0, 0, 0);
		ImageButton deselectAll = new ImageButton(context);
		deselectAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ObjectSelectDialog.this.deselectAll();
			}
		});
		deselectAll.setImageDrawable(iconProvider.getCollapseIcon(context));
		deselectAll.setPadding(0, 0, 0, 0);

		
		layout.addView(selectAll, new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		layout.addView(deselectAll, new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	protected Class<?> getType() {
		return Object.class;
	}

	protected View createToolButtonBar(final Context context) {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		IIconProvider iconProvider = this.dialogTheme.getIconProvider();
		ImageButton selectAll = new ImageButton(context);
		selectAll.setImageDrawable(iconProvider.getExpandIcon(context));
		selectAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ObjectSelectDialog.this.selectAll();
			}
		});
		selectAll.setPadding(0, 0, 0, 0);
		ImageButton deselectAll = new ImageButton(context);
		deselectAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ObjectSelectDialog.this.deselectAll();
			}
		});
		deselectAll.setImageDrawable(iconProvider.getCollapseIcon(context));
		deselectAll.setPadding(0, 0, 0, 0);
		// ImageButton filter = new ImageButton(context);
		// filter.setImageDrawable(iconProvider.getSearchIconBlack(context));
		// filter.setPadding(0,0,0,0);
		// filter.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// final StringInputDialog inputDialog = new
		// StringInputDialog(context,filterString);
		// inputDialog.setOnDismissListener(new OnDismissListener() {
		//
		// @Override
		// public void onDismiss(DialogInterface dialog) {
		// String result2 = inputDialog.getResult();
		// if (result2 != null)
		// setFilterString(result2);
		// }
		//
		// });
		// inputDialog.show();
		// }
		//
		// });
		// disableFilter = new ImageButton(context);
		// disableFilter.setImageDrawable(iconProvider.getDisableSearchIconBlack(context));
		// disableFilter.setPadding(0,0,0,0);
		// disableFilter.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// setFilterString("");
		// }
		// });
		LayoutParams wrapParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		layout.addView(selectAll, wrapParams);
		layout.addView(deselectAll, wrapParams);
		// layout.addView(filter,wrapParams);
		// if (filterString != null && filterString.length() > 0)
		// layout.addView(disableFilter,wrapParams);
		this.toolbarView = layout;
		return layout;
	}

	protected void setFilterString(String result2) {
		if (this.filterString.equals(result2)) {
			return;
		}
		this.filterString = result2;
		this.fitems = this.doFilter(this.filterString);
		if ((result2 == null) || (result2.length() == 0)) {
			this.adapter = this.configureAdapter(this.getContext(),
					this.listView, this.items);
			// toolbarView.removeView(disableFilter);
		} else {
			this.adapter = this.configureAdapter(this.getContext(),
					this.listView, this.fitems);
			// toolbarView.addView(disableFilter, new
			// LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		}
	}

	protected List<? extends Object> doFilter(String filterString) {
		List<Object> result = new ArrayList<Object>();
		filterString = filterString.toLowerCase();
		for (Object item : this.items) {
			if (this.getLabel(item).toLowerCase().contains(filterString)) {
				result.add(item);
			}
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void doSort(List items2) {
		if ((items2.size() > 0) && (items2.get(0) instanceof Comparable)) {
			Collections.sort(items2);
		}

	}

	protected void deselectAll() {
		this.result.clear();
		this.adapter.notifyDataSetChanged();
	}

	protected void selectAll() {
		if (this.fitems != null) {
			this.result.addAll(this.fitems);
		} else {
			this.result.addAll(this.items);
		}
		this.adapter.notifyDataSetChanged();
	}

	@SuppressWarnings("unchecked")
	protected ArrayAdapter<Object> configureAdapter(final Context context,
			ListView listView, List<? extends Object> items) {
		this.doSort(items);
		final ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(context,
				android.R.layout.simple_list_item_multiple_choice,
				listView.getId(), (List<Object>) items) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// if (convertView == null)
				// {
				final Object item = this.getItem(position);
				View renderedField = ObjectSelectDialog.this.createView(item);
				LinearLayout layout = new LinearLayout(context);
				CheckBox box = new CheckBox(context);

				box.setEnabled(ObjectSelectDialog.this.isEnabled(item));
				box.setClickable(true);
				box.setChecked(ObjectSelectDialog.this.isSelected(item));
				layout.addView(box, new LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				layout.addView(renderedField, new LayoutParams(
						android.view.ViewGroup.LayoutParams.FILL_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							ObjectSelectDialog.this.result.add(item);
						} else {
							ObjectSelectDialog.this.result.remove(item);
						}
					}
				});
				renderedField.setPadding(5, 10, 5, 10);
				return layout;
				// }
			}

		};
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setClickable(true);
		return adapter;
	}

	public boolean isSelected(Object item) {
		return this.result.contains(item);
	}

	protected boolean isEnabled(Object item) {
		return true;
	}

	@Override
	protected void performCancel() {
		this.result = null;
		this.dismiss();
	}

	@Override
	protected void performOk() {
		this.dismiss();
	}

	public Collection<Object> getResult() {
		return this.result;
	}

}
