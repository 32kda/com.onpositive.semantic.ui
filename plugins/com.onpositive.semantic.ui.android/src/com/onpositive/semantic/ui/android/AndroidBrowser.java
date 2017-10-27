package com.onpositive.semantic.ui.android;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;

public class AndroidBrowser extends AndroidUIElement {
	
	private static final String ENCODING = "utf-8";

	private static final String MIME_TYPE = "text/html";

	private static final long serialVersionUID = 2020037854018998726L;
	
	private String text = "";

	private String url = "";
	
	@Override
	protected void resetValue() {
		if (isCreated()) {
			IBinding binding2 = getBinding();
			if (binding2 != null && binding2.getValue() instanceof String && binding2.getValue().toString().trim().length() > 0) {
				((WebView) getControl()).loadData(binding2.getValue().toString().trim(),MIME_TYPE,ENCODING);
			} else if (url.length() > 0) {
				((WebView) getControl()).loadUrl(url);
			} else {
				((WebView) getControl()).loadData(text,MIME_TYPE,ENCODING);
			}
		}
	}

	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		WebView webView = new WebView(context);
		return webView;
	}

	public String getText() {
		return text;
	}

	@HandlesAttributeDirectly("text")
	public void setText(String text) {
		this.text = text;
	}
	
	@HandlesAttributeDirectly("url")
	public void setUrl(String url) {
		if (url == null)
			url = "";
		url = url.trim();
		if (url.indexOf(":") == -1) {
			url = "http://" + url;
		}
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

}
