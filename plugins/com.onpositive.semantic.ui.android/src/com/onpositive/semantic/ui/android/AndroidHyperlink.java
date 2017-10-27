package com.onpositive.semantic.ui.android;

import java.util.regex.Pattern;

import android.content.Context;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.generic.HyperlinkEvent;
import com.onpositive.semantic.model.ui.generic.IHyperlinkListener;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;

public class AndroidHyperlink extends AndroidLabel {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -733731906197943957L;
	private IHyperlinkListener hyperlinkListener;
	private String url;
	
	@HandlesAttributeDirectly("hyperLinkListener")
	public void setHyperlinkListener(IHyperlinkListener hyperlinkListener) {
		this.hyperlinkListener = hyperlinkListener;
	}
	
	@HandlesAttributeDirectly("url")
	public void setUrl(String url){
		this.url = url;
		if (widget != null) {
			((TextView) widget).setText(url);
			Linkify.addLinks((TextView) widget,Pattern.compile("*"),"http://");
		}
		if (getCaption() == null || getCaption().length() == 0) {
			setCaption(url);
		}
	}

	
	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		TextView textView = new TextView(context);
		textView.setAutoLinkMask(Linkify.ALL);
		Linkify.addLinks(textView,Pattern.compile(".+"),"http://");
		textView.setLinksClickable(true);
		if (url != null)
			textView.setText(url);
		if (hyperlinkListener != null) {
			textView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					hyperlinkListener.linkActivated(new HyperlinkEvent(url,getCaption()));
				}
			});
		}
		return textView;
	}
	
	
}
