package com.onpositive.semantic.ui.android;

import java.util.ArrayList;
import java.util.Arrays;

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
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;
import com.onpositive.semantic.model.ui.generic.ContentProposal;
import com.onpositive.semantic.model.ui.generic.IContentAssistConfiguration;
import com.onpositive.semantic.model.ui.generic.IContentProposal;
import com.onpositive.semantic.model.ui.generic.IContentProposalProvider;

public class ContentAssistAdapter extends BaseAdapter implements Filterable {

	protected Context context;
	protected ArrayList<Object> result;
	private int count;
	private IBindingChangeListener<Object> listener2;
	private IBindable bindable;
	private IContentProposalProvider proposalProvider;
	private ITextLabelProvider proposalLabelProvider;

	public ContentAssistAdapter(Context context,
			IBindable bindable,
			IContentAssistConfiguration configuration) {
		this.context = context;
		this.bindable = bindable;
		proposalLabelProvider = configuration.getProposalLabelProvider();
		proposalProvider = configuration.getProposalProvider();
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
			IBinding binding = bindable.getBinding();
			if (binding!=null){
				binding.removeBindingChangeListener(listener2);
			}
		}
	}

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
			IBinding binding = bindable.getBinding();
			if (binding == null)
				throw new IllegalStateException("Can't setup ContentAssist for null binding!");
			binding.addBindingChangeListener(listener2);
			if (binding != null) {
				Object value = binding.getValue();
				String strValue = value == null?"":value.toString();
				result.addAll(Arrays.asList(proposalProvider.getProposals(strValue,0)));
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

		String label;
		if (proposalLabelProvider != null) {
			label = proposalLabelProvider.getText(bindable.getBinding(),null,result.get(position));
		} else {
			label = LabelAccess.getLabel(bindable.getBinding(), result.get(position));
		}
		tv.setText(label);
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
				if (bindable.getBinding() != null) {
					String text = getString(bindable.getBinding().getValue());
					IContentProposal[] proposals = proposalProvider.getProposals(text,text.length());
					if (proposals != null) {
						for (IContentProposal o : proposals) {
							String label;
							if (proposalLabelProvider != null) {
								label = proposalLabelProvider.getText(bindable.getBinding(),null,o);
							} else {
								label = LabelAccess.getLabel(bindable.getBinding(), o);
							}
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
				notifyDataSetChanged();
			}
			
			@Override
			public CharSequence convertResultToString(Object resultValue) {
				if (resultValue instanceof ContentProposal) {
					return ((ContentProposal) resultValue).getContent();
				}
				return super.convertResultToString(resultValue);
			}

		};
	}

	protected String getString(Object value) {
		if (value == null)
			return "";
		return value.toString();
	}

}
