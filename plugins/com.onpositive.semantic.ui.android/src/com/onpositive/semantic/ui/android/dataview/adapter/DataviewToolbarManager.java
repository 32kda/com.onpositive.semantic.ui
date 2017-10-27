package com.onpositive.semantic.ui.android.dataview.adapter;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.onpositive.businessdroids.ui.actionbar.ActionBar;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.BindedAction;
import com.onpositive.semantic.model.ui.roles.ImageManager;
import com.onpositive.semantic.ui.android.AndroidImageManager;

public class DataviewToolbarManager implements IProvidesToolbarManager {
	
	protected ActionBar actionBar;
	protected List<IContributionItem> contributionItems = new ArrayList<IContributionItem>();
	

	@Override
	public void addToToolbar( final com.onpositive.semantic.model.ui.actions.IContributionItem bindedAction) {

		if( bindedAction instanceof Action ){
			String text = bindedAction.id();
			Action action = (Action) bindedAction ;
			Drawable icon = AndroidImageManager.getImageDrawable( action.getImageDescriptor() );
			//XXX START DIRTY HACK
			if (icon == null) {
				if ("com.onpositive.semantic.ui.add".equals(action.getImageId())){
					icon = AndroidImageManager.getImageDrawable(ImageManager.getImageDescriptorByPath(this, "actions/add.png"));
				}
				else if ("com.onpositive.semantic.ui.edit".equals(action.getImageId())){
					icon = AndroidImageManager.getImageDrawable(ImageManager.getImageDescriptorByPath(this, "actions/edit.png"));
				}
				else if ("com.onpositive.semantic.ui.delete".equals(action.getImageId()) || "org.eclipse.ui.edit.delete".equals(action.getImageId())){
					icon = AndroidImageManager.getImageDrawable(ImageManager.getImageDescriptorByPath(this, "actions/delete.png"));
				}
				else if ("com.onpositive.semantic.ui.deleted".equals(action.getImageId())){
					icon = AndroidImageManager.getImageDrawable(ImageManager.getImageDescriptorByPath(this, "actions/delete.png"));
				} else if ("com.onpositive.commons.namespace.ide.ui.rename".equals(action.getImageId())){
					icon = AndroidImageManager.getImageDrawable(ImageManager.getImageDescriptorByPath(this, "actions/edit.png"));
				} else if ("com.onpositive.ide.ui.hierarchy".equals(action.getImageId())){
					icon = AndroidImageManager.getImageDrawable(ImageManager.getImageDescriptorByPath(this, "actions/edit.png"));
				} else if (action.getImageId() != null && action.getImageId().startsWith("checkOn")) {
					icon = AndroidImageManager.getImageDrawable(ImageManager.getImageDescriptorByPath(this, "actions/checkOn.png"));
				}
				else if (action.getImageDescriptor() == null && action.getImageId() != null) {
					icon = AndroidImageManager.getImageDrawable(ImageManager.getImageDescriptorByPath(this, action.getImageId()));
				}
			}
			//XXX END DIRTY HACK
			
			int style = 0 ;			
			if( ((Action) bindedAction).getStyle() == BindedAction.AS_CHECK_BOX )
				style = ActionContribution.AS_CHECKBOX ;
			
			ActionContribution newItem = new ActionContribution( text, icon, style ) {

				@Override
				protected void run() {
					((Action) bindedAction).run() ;
				}
			
			} ;
			contributionItems.add( newItem ) ;			
			return ;
		}
		
		IContributionItem newItem = new IContributionItem() {
			
			@Override
			public Drawable getIcon() {
				if( bindedAction instanceof Action ){
					Action action = (Action) bindedAction ;
					Drawable result = AndroidImageManager.getImageDrawable( action.getImageDescriptor() );
					return result ;
				} else {
					return AndroidImageManager.getImageDrawable(ImageManager.getImageDescriptorByPath(this, "actions/edit.png"));
				}
			}
			
			@Override
			public void removePropertyChangeListener(PropertyChangeListener l) {
				bindedAction.removePropertyChangeListener(l) ;				
			}
			
			@Override
			public boolean isEnabled() {
				return bindedAction.isEnabled();
			}
			
			@Override
			public String getText() {
				return bindedAction.toString() ;
			}
			
			@Override
			public String getId() {
				return bindedAction.id() ;
			}
			
			@Override
			public void addPropertyChangeListener(PropertyChangeListener l) {
				bindedAction.addPropertyChangeListener(l) ;				
			}
		};
		this.contributionItems.add(newItem) ;
	}

	@Override
	public void removeFromToolbar(com.onpositive.semantic.model.ui.actions.IContributionItem action) {
		// TODO Auto-generated method stub

	}
	
	public void setActionBar(ActionBar actionBar) {
//		if (this.actionBar != null) {
//			for (IContributionItem contributionItem : contributionItems) {
//				this.actionBar.removeAction(contributionItem);
//			}
//		}
		this.actionBar = actionBar;
		if (this.actionBar != null) {
			for (IContributionItem contributionItem : contributionItems) {
				this.actionBar.addAction(contributionItem);
			}
		}
	}

	public ActionBar getActionBar() {
		return actionBar;
	}

	public List<IContributionItem> getContributionItems() {
		return contributionItems;
	}

	public void setContributionItems(List<IContributionItem> contributionItems) {
		this.contributionItems = contributionItems;
	}

}
