package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class ObjectContributionHandler extends AbstractActionElementHandler {

	
	protected BindedAction contribute(ActionsSetting parentContext, Context context,
			Element element) {
		// TODO Auto-generated method stub
		return null;
		
	}

//	
//	protected void contribute(IProvidesToolbarManager parentContext,
//			Context context, Element element) {
//		// TODO Auto-generated method stub
//		
//	}

//	private IMenuListener l;
//
//	protected void contribute(ActionsSetting parentContext, Context context,
//			Element element) {
//		parentContext.allowObjectContribution = true;
//		final ListEnumeratedValueSelector<?> as = (ListEnumeratedValueSelector<?>) parentContext
//				.getControl();
//		if (l != null) {
//			parentContext.removeMenuListener(l);
//		}
//		l = new IMenuListener() {
//
//			public void menuAboutToShow(final IMenuManager manager) {
//				final StructuredSelection sel = (StructuredSelection) as
//						.getSelection();
//				ActionManager.getInstance().fillContributionManager(
//						new IContributionManager() {
//
//							public void add(final IContributionItem item) {
//								process(manager, item);
//							}
//						}, new IStructuredSelection() {
//
//							@SuppressWarnings("unchecked")
//							public List<? extends Object> toList() {
//								return sel.toList();
//							}
//						}, as.getRole(), as.getTheme());
//			}
//
//		};
//		parentContext.addMenuListener(l);
//	}
//
//	protected void contribute(IProvidesToolbarManager parentContext,
//			Context context, Element element) {
//		throw new UnsupportedOperationException("Works only with actions tag");
//	}
//
//	protected void process(final IMenuManager manager,
//			final IContributionItem item) {
//		if (item.isSeparator()){
//			Separator p=new Separator(item.getDefinitionId());
//			manager.add(p);
//			return;
//		}
//		if (item.isCompound()){
//			String caption=item.getCaption();
//			String id=item.getDefinitionId();
//			ImageDescriptor des=item.getImage()==null?SWTImageManager.getDescriptor(item.getImage()):null;
//			MenuManager man=new MenuManager(caption,des,id);			
//			manager.add(man);
//			Collection<IContributionItem> ch = item.getChildren();
//			for (IContributionItem m:ch){
//				process(man, m);
//			}
//		}
//		Action action = new Action(item.getCaption()){
//			
//			public void run() {
//				item.run();
//			}
//		};
//		if(item.getImage()!=null){
//			action.setImageDescriptor(SWTImageManager.getDescriptor(item.getImage()));
//		}
//		if(item.getDisabledImage()!=null){
//			action.setDisabledImageDescriptor(SWTImageManager.getDescriptor(item.getDisabledImage()));
//		}
//		action.setActionDefinitionId(item.getDefinitionId());
//		ActionContributionItem m=new ActionContributionItem(action);
//		manager.add(m);
//	}
//		
}
