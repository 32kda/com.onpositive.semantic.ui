package com.onpositive.businessdroids.gallery;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;

public abstract class ImageAdapter extends BaseAdapter {
	private Context mContext;

	

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null || !(convertView instanceof ImageThumb)) {
			ImageThumb thumb = new ImageThumb(mContext);
			
			byte[] bs = getImageBytes(position);
			if (bs != null)
				thumb.setImageBitmap(BitmapFactory.decodeByteArray(bs, 0, bs.length));
			String text = getCaption(position);
			thumb.setLayoutParams(createItemLayoutParams());
			thumb.setText(text);
			return thumb;
		} else {
			Object value = getItem(position);
			byte[] bs=(byte[]) value;
			if (value!=null){
				((ImageThumb) convertView).setImageBitmap(BitmapFactory.decodeByteArray(bs, 0, bs.length));
			} else {
				((ImageThumb) convertView).setImageBitmap(null);
			}
			return convertView;
		}
	}

	protected LayoutParams createItemLayoutParams() {
		return new GridView.LayoutParams(100, 120);
	}

	protected abstract String getCaption(int position);

	protected byte[] getImageBytes(int position) {
		Object value = getItem(position);
		if (value instanceof byte[]) {
			return (byte[]) value;
		}
		return null;
	}
}