package com.onpositive.businessdroids.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;

import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;


public class PrefUtil {

	public static String getStringFromCollection(
			Collection<? extends Object> collection) {
		StringBuilder builder = new StringBuilder();
		for (Object object : collection) {
			builder.append(object.toString());
			builder.append("\t");
		}
		return builder.toString();
	}

	public static LinkedHashSet<String> getSetFromString(String string) {
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		StringTokenizer tokenizer = new StringTokenizer(string, "\t", false);
		while (tokenizer.hasMoreTokens()) {
			String nextToken = tokenizer.nextToken();
			nextToken = nextToken.replaceAll("\t", "");
			result.add(nextToken);
		}
		return result;
	}

	public static ArrayList<String> getListFromString(String string) {
		ArrayList<String> result = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(string, "\t", false);
		while (tokenizer.hasMoreTokens()) {
			String nextToken = tokenizer.nextToken();
			nextToken = nextToken.replaceAll("\t", "");
			result.add(nextToken);
		}
		return result;
	}

	public static ImageButton createClearBtn(final EditText editView,
			ITheme dialogTheme) {
		return PrefUtil.createClearBtn(editView.getContext(),
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						editView.setText("");
					}
				}, dialogTheme);
	}

	public static ImageButton createClearBtn(Context ct,
			View.OnClickListener listener, ITheme dialogTheme) {
		ImageButton xBtn = new ImageButton(ct);
		Drawable clearIcon = dialogTheme.getIconProvider().getClearIcon(ct);
		xBtn.setImageDrawable(clearIcon);
		xBtn.setOnClickListener(listener);
		return xBtn;
	}
}
