package com.onpositive.businessdroids.ui.dialogs;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.onpositive.businessdroids.model.filters.ComparableFilter;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


public class SimpleDateFilterDialog extends OkCancelDialog {

	static GregorianCalendar z = new GregorianCalendar();
	static int lastYear = SimpleDateFilterDialog.z.get(GregorianCalendar.YEAR);
	static int lastMonth = SimpleDateFilterDialog.z
			.get(GregorianCalendar.MONTH);
	static int week = SimpleDateFilterDialog.z
			.get(GregorianCalendar.WEEK_OF_YEAR);
	static int date = SimpleDateFilterDialog.z
			.get(GregorianCalendar.DAY_OF_WEEK);

	protected final ComparableFilter filter;
	protected final String TODAY_LABEL = "Today";
	protected final String YESTERDAY_LABEL = "Yesterday";
	protected final String TOMORROW_LABEL = "Tomorrow";
	protected final String THIS_WEEK_LABEL = "This week";
	protected final String THIS_MONTH_LABEL = "This month";
	protected final String THIS_YEAR_LABEL = "This year";

	protected android.view.View.OnClickListener buttonClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v instanceof Button) {
				CharSequence text = ((Button) v).getText();
				if (text.equals(SimpleDateFilterDialog.this.TODAY_LABEL)) {
					// GregorianCalendar calendar = new
					// GregorianCalendar(lastYear,lastMonth,z.get(GregorianCalendar.DAY_OF_MONTH),0,0);
					GregorianCalendar calendar = this.getTodayBeginning();
					Date min = calendar.getTime();
					calendar.add(Calendar.HOUR_OF_DAY, 24);
					Date max = calendar.getTime();
					SimpleDateFilterDialog.this.setTimeBounds(min, max);
				} else if (text
						.equals(SimpleDateFilterDialog.this.TOMORROW_LABEL)) {
					GregorianCalendar calendar = this.getTodayBeginning();
					calendar.add(Calendar.HOUR_OF_DAY, 24);
					Date min = calendar.getTime();
					calendar.add(Calendar.HOUR_OF_DAY, 24);
					Date max = calendar.getTime();
					SimpleDateFilterDialog.this.setTimeBounds(min, max);
				} else if (text
						.equals(SimpleDateFilterDialog.this.YESTERDAY_LABEL)) {
					GregorianCalendar calendar = this.getTodayBeginning();
					Date max = calendar.getTime();
					calendar.add(Calendar.HOUR_OF_DAY, -24);
					Date min = calendar.getTime();
					SimpleDateFilterDialog.this.setTimeBounds(min, max);
				} else if (text
						.equals(SimpleDateFilterDialog.this.THIS_WEEK_LABEL)) {
					GregorianCalendar calendar = this.getTodayBeginning();
					calendar.set(Calendar.DAY_OF_WEEK, 0);
					Date min = calendar.getTime();
					calendar.add(Calendar.DAY_OF_WEEK, 7);
					Date max = calendar.getTime();
					SimpleDateFilterDialog.this.setTimeBounds(min, max);
				} else if (text
						.equals(SimpleDateFilterDialog.this.THIS_MONTH_LABEL)) {
					GregorianCalendar calendar = new GregorianCalendar(
							SimpleDateFilterDialog.lastYear,
							SimpleDateFilterDialog.lastMonth, 1);
					Date min = calendar.getTime();
					calendar.add(Calendar.MONTH, 1);
					Date max = calendar.getTime();
					SimpleDateFilterDialog.this.setTimeBounds(min, max);
				} else if (text
						.equals(SimpleDateFilterDialog.this.THIS_YEAR_LABEL)) {
					GregorianCalendar calendar = new GregorianCalendar(
							SimpleDateFilterDialog.lastYear, 0, 1);
					Date min = calendar.getTime();
					calendar.add(Calendar.YEAR, 1);
					Date max = calendar.getTime();
					SimpleDateFilterDialog.this.setTimeBounds(min, max);
				}
			}
		}

		protected GregorianCalendar getTodayBeginning() {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar;
		}
	};

	public SimpleDateFilterDialog(Context context, int theme,
			ComparableFilter filter, ITheme dialogTheme) {
		super(context, theme, dialogTheme);
		this.filter = filter;
	}

	public SimpleDateFilterDialog(Context context, ComparableFilter filter,
			ITheme dialogTheme) {
		super(context, dialogTheme);
		this.filter = filter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setTitle(this.dialogTheme.getLabelProvider()
				.getSimpleDateFilterDialogTitle());
		Context context = this.getContext();
		LayoutParams wrapParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		wrapParams.gravity = Gravity.CENTER;
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);

		View header = this.createHeader();
		if (header != null) {
			layout.addView(header, new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		}

		Button todayButton = this.createButton(this.TODAY_LABEL);
		Button yesterdayButton = this.createButton(this.YESTERDAY_LABEL);
		Button tomorrowButton = this.createButton(this.TOMORROW_LABEL);
		Button thisWeekButton = this.createButton(this.THIS_WEEK_LABEL);
		Button thisMonthButton = this.createButton(this.THIS_MONTH_LABEL);
		Button thisYearButton = this.createButton(this.THIS_YEAR_LABEL);

		layout.addView(todayButton, wrapParams);
		layout.addView(yesterdayButton, wrapParams);
		layout.addView(tomorrowButton, wrapParams);
		layout.addView(thisWeekButton, wrapParams);
		layout.addView(thisMonthButton, wrapParams);
		layout.addView(thisYearButton, wrapParams);

		LinearLayout buttonLayout = this.createButtonBar(context);
		layout.addView(buttonLayout, wrapParams);
		this.setContentView(layout);
	}

	@Override
	protected LinearLayout createButtonBar(Context context) {
		LinearLayout buttonLayout = new LinearLayout(context);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		// okButton = new Button(context);
		// okButton.setOnClickListener(buttonListener);
		// okButton.setText("OK");
		// buttonLayout.addView(okButton,new
		// LayoutParams(150,LayoutParams.WRAP_CONTENT));
		this.cancelButton = new Button(context);
		this.cancelButton.setOnClickListener(this.buttonListener);
		((TextView) this.cancelButton).setText(this.dialogTheme
				.getLabelProvider().getCancelTitle());
		buttonLayout.addView(this.cancelButton, new LayoutParams(150,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		return buttonLayout;
	}

	protected Button createButton(String label) {
		Button result = new Button(this.getContext());
		result.setText(label);
		result.setOnClickListener(this.buttonClickListener);
		return result;
	}

	@Override
	protected void performOk() {

	}

	protected void setTimeBounds(Date min2, Date max2) {
		this.filter.setMin(min2);
		this.filter.setMax(max2);
		this.filter.getTableModel().addFilter(this.filter);
		this.dismiss();
	}

	@Override
	protected View createContents() {
		// TODO Auto-generated method stub
		return null;
	}

}
