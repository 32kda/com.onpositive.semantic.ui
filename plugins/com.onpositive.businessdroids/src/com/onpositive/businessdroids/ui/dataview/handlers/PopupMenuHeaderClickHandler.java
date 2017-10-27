package com.onpositive.businessdroids.ui.dataview.handlers;

import java.util.Collection;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.actions.ICompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.actions.IHasImage;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;


public class PopupMenuHeaderClickHandler extends AbstractCompositeClickHandler
		implements ITablePartClickHandler {

	public PopupMenuHeaderClickHandler(StructuredDataView view) {
		super(view);
	}

	@Override
	public void handleClick(final IColumn column, View source) {
		final Collection<IContributionItem> enabledContributions = this
				.getEnabledContributions(column);
		if (enabledContributions.size() == 0) {
			return;
		}
		if (enabledContributions.size() == 1) {
			IContributionItem iContributionItem = enabledContributions
					.iterator().next();
			if (iContributionItem instanceof ActionContribution) {
				ActionContribution a = (ActionContribution) iContributionItem;
				a.onRun();
				return;
			}
		}
		source.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(final ContextMenu menu,
					final View v, ContextMenuInfo menuInfo) {

				for (IContributionItem i : enabledContributions) {
					this.processItem(menu, v, i);
				}
			}

			protected void processItem(final ContextMenu menu, final View v,
					IContributionItem i) {
				if (i instanceof ICompositeContributionItem) {
					ICompositeContributionItem m = (ICompositeContributionItem) i;
					IContributionItem[] children = m.getChildren();
					if (children.length == 0) {
						return;
					}
					if (children.length == 1) {
						if (children[0].isEnabled()) {
							this.processItem(menu, v, children[0]);
						}
						return;
					}
					SubMenu addSubMenu = menu.addSubMenu(i.getText());
					if (i instanceof IHasImage) {
						IHasImage s = (IHasImage) i;
						addSubMenu.setIcon(s.getIcon());
					}

					for (final IContributionItem z : children) {
						if (z.isEnabled()) {
							MenuItem add = addSubMenu.add(z.getText());
							if (z instanceof IHasImage) {
								IHasImage s = (IHasImage) z;
								addSubMenu.setIcon(s.getIcon());
							}
							add.setOnMenuItemClickListener(new OnMenuItemClickListener() {

								@Override
								public boolean onMenuItemClick(MenuItem item) {
									menu.close();
									if (z instanceof ActionContribution) {
										ActionContribution m = (ActionContribution) z;
										m.onRun();

									} else {
										if (z instanceof ICompositeContributionItem) {
											PopupMenuHeaderClickHandler.this.dataView
													.getCurrentTheme()
													.getContributionPresenter(2,(ICompositeContributionItem) z)
													.presentContributionItem(
															z,
															PopupMenuHeaderClickHandler.this.dataView,
															v);
										}
									}
									return true;
								}
							});
						}
					}
				}
				if (i instanceof ActionContribution) {
					final ActionContribution ac = (ActionContribution) i;
					MenuItem add = menu
							.add(ac instanceof ITopLevelText ? ((ITopLevelText) ac)
									.getTopLevelText() : ac.getText());
					if (ac.isEnabled()) {
						add.setIcon(ac.getIcon());
						add.setOnMenuItemClickListener(new OnMenuItemClickListener() {

							@Override
							public boolean onMenuItemClick(MenuItem item) {
								ac.onRun();
								menu.close();
								return true;
							}
						});
					}
				}
			}
		});
		source.showContextMenu();
	}

}
