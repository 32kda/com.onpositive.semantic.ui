package com.onpositive.businessdroids.mainmenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.onpositive.businessdroids.ui.actions.ActionContribution;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;

public class MainMenuAdapter extends BaseAdapter {
	
	List<ActionContribution> menuItems = new ArrayList<ActionContribution>();
	protected final Context context;
	
	public MainMenuAdapter(Context context, ActionContribution... contributions) {
		this.context = context;
		for (int i = 0; i < contributions.length; i++) {
			menuItems.add(contributions[i]);
		}
	}
	
	public MainMenuAdapter(Context context, Collection<ActionContribution> items) {
		this.context = context;
		menuItems.addAll(items);
	}

	public void addActionContribution(ActionContribution contribution) {
		menuItems.add(contribution);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return menuItems.size();
	}

	@Override
	public Object getItem(int position) {
		return menuItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ActionContribution item = menuItems.get(position);
		MenuButton menuButton = new MenuButton(context,item.getText(),item.getIcon());
		menuButton.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		menuButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				item.onRun();				
			}
		});
		return menuButton;
	}
}
