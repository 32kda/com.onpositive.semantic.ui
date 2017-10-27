package com.onpositive.semantic.ui.android;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmChangeListener;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;

public class RealmBaseAdapter extends BaseAdapter implements Filterable {

	protected IBindable bnd;

	public RealmBaseAdapter(IBindable realm, Context ct) {
		super();
		this.bnd = realm;
		this.context = ct;
	}

	protected Context context;
	protected ArrayList<Object> result;
	private IRealmChangeListener<Object> listener;
	private IRealm<Object> realm;
	private int count;
	private IBindingChangeListener<Object> listener2;

	public Context getContext() {
		return context;
	}

	public int position(Object value) {
		initResult();
		return result.indexOf(value);
	}

	@Override
	public int getCount() {
		initResult();
		return result.size();
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		count++;
		super.registerDataSetObserver(observer);
	}
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		super.unregisterDataSetObserver(observer);
		count--;
		if (count==0){
			if (realm!=null){
				realm.removeRealmChangeListener(listener);
			}
			IBinding binding = bnd.getBinding();
			if (binding!=null){
				binding.removeBindingChangeListener(listener2);
			}
		}
	}

	private void initResult() {
		if (result == null) {
			result = new ArrayList<Object>();
			listener2 = new IBindingChangeListener<Object>() {

				/**
				 * Serial Version UID
				 */
				private static final long serialVersionUID = -5480041192101671138L;

				@Override
				public void valueChanged(ISetDelta<Object> valueElements) {
					//initResult();
				}

				@Override
				public void enablementChanged(boolean isEnabled) {
					
				}

				@Override
				public void changed() {
					initResult();
				}

			};
			bnd.getBinding().addBindingChangeListener(listener2);
			IBinding binding = bnd.getBinding();
			if (binding != null) {
				if (realm != null) {
					realm.removeRealmChangeListener(listener);
				}
				realm = binding.getRealm();
				listener = new IRealmChangeListener<Object>() {

					@Override
					public void realmChanged(IRealm<Object> realmn,
							ISetDelta<Object> delta) {
						result=null;
						initResult();
						notifyDataSetChanged();
					}
				};

				if (realm != null) {
					realm.addRealmChangeListener(listener);
					result.addAll(realm.getContents());
				}
			}
		}
	}

	@Override
	public Object getItem(int position) {
		initResult();
		return result.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout ll = new LinearLayout(context);
		ll.setPadding(5, 5, 5, 5);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		TextView tv = new TextView(context);

		tv.setText(LabelAccess.getLabel(bnd.getBinding(), result.get(position)));
		tv.setCompoundDrawablePadding(15);
		if (parent.isEnabled()){
			tv.setTextColor(Color.BLACK);
		}
		if (position == getCount() - 1) {
			tv.setPadding(0, 0, 0, 4);
		}
		Drawable image = AndroidImageManager.getImage(result.get(position),
				"row", null);
		if (image != null) {
			ImageView child = new ImageView(context);
			child.setScaleType(ScaleType.FIT_CENTER);
			child.setImageDrawable(image);
			int lh = tv.getLineHeight();
			ll.addView(child, lh, lh);
		}
		tv.setGravity(Gravity.CENTER_VERTICAL);
		ll.addView(tv);
		return ll;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				ArrayList<Object> result = new ArrayList<Object>();
				if (bnd.getBinding() != null) {
					IRealm<Object> realm = bnd.getBinding().getRealm();
					if (realm != null) {
						for (Object o : realm) {
							String label = LabelAccess.getLabel(bnd.getBinding(), o);
							if (label != null) {
								if (label.contains(constraint)) {
									result.add(o);
								}
							}
						}
					}
				}
				// String str=constraint.toString();
				FilterResults dd = new FilterResults();
				dd.count = result.size();
				dd.values = result;
				return dd;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				result = (ArrayList<Object>) results.values;
			}

		};
	}
}
