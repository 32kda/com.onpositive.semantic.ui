package com.onpositive.businessdroids.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;

import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * TODO SHOULD BE SINGLE SELECT DIALOG, Styling
 * 
 * @author 32kda
 * 
 */
public class StringSelectDialog extends AbstractSelectDialog<Object> {

	public StringSelectDialog(Context context, Collection<Object> items,
			ITheme dialogTheme) {
		this(context, 0, items, dialogTheme);
	}

	public StringSelectDialog(Context context, int theme,
			Collection<Object> items, ITheme dialogTheme) {
		super(context, theme, items, dialogTheme);
		this.setTitle(dialogTheme.getLabelProvider()
				.getStringSelectDialogTitle());
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ArrayAdapter<Object> configureAdapter(final Context context,
			ListView listView) {
		final ArrayAdapter adapter = new ArrayAdapter<String>(context,
				listView.getId(), new ArrayList(this.items)) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					TextView textView = new TextView(context);
					textView.setText(this.getItem(position));
					textView.setPadding(4, 10, 4, 10);
					return textView;
				}
				return super.getView(position, convertView, parent);
			}
		};
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				StringSelectDialog.this.result = adapter.getItem(position);
				StringSelectDialog.this.dismiss();
			}
		});
		return adapter;
	}

}
