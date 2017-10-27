package com.onpositive.commons.namespace.ide.ui;

import java.util.Collection;
import java.util.HashSet;

import com.onpositive.commons.contentassist.BasicContentProposal;
import com.onpositive.commons.contentassist.RealmProposalProvider;
import com.onpositive.commons.namespace.ide.ui.completion.BasicContentAssistConfiguration;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.ModelElement;
import com.onpositive.semantic.language.model.NameSpaceContributionModel;
import com.onpositive.semantic.language.model.NamespacesModel;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmProvider;
import com.onpositive.semantic.model.api.realm.Realm;
import com.onpositive.semantic.model.ui.generic.IContentAssistConfiguration;
import com.onpositive.semantic.model.ui.generic.IContentProposalProvider;

public class ChildElementContentAsisstProvider extends BasicContentAssistConfiguration implements IContentAssistConfiguration{

	
	public IContentProposalProvider getProposalProvider() {
		Realm<ModelElement> realm = new Realm<ModelElement>(){
			
			public Collection<ModelElement> getContents() {
				return NamespacesModel.getInstance().getAllElements();
			}
			
		};
		return new RealmProposalProvider<String>(realm){
			@Override
			protected BasicContentProposal createProposal(int position,
					String disp, Object o, String string, String description,
					String caption) {
				ElementModel mq=(ElementModel) o;
				NameSpaceContributionModel object = (NameSpaceContributionModel) oneLineTextElement.getBinding().getParent().getObject();
				String url =mq.getModel()!=null?mq.getModel().getUrl():object.getUrl();				
				if (!object.getUrl().equals(url)){
					String url2 = mq.getModel().getUrl();
					if (url2.length()>0){
					if (url2.charAt(url2.length()-1)!='/'){
						url2=url2+'/';
					}
					return new BasicContentProposal(
							url2+caption, url2+caption,
							description, position + string.length()
									- disp.length(), o);
					}
				}
				return new BasicContentProposal(
						caption, caption,
						description, position + string.length()
								- disp.length(), o);
				//return super.createProposal(position, disp, o, string, description, caption);
			}
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			protected Collection<?> basicGetCandidates() {
				IRealmProvider adapter = oneLineTextElement.getBinding().getAdapter(IRealmProvider.class);
				IRealm<Object> realm = adapter.getRealm(oneLineTextElement.getBinding(), oneLineTextElement.getBinding().getObject(), null);
				HashSet l=new HashSet();
				Collection<Object> contents = realm.getContents();
				NameSpaceContributionModel object = (NameSpaceContributionModel) oneLineTextElement.getBinding().getParent().getObject();				
				for (Object o:contents){
					ElementModel ml=new ElementModel();
					object.setOwner(object);
					ml.setName(o.toString());
					l.add(ml);
				}
				Collection<?> basicGetCandidates = super.basicGetCandidates();
				for (Object o:basicGetCandidates){
					if (!contents.contains(o.toString())){
						l.add(o);
					}
				}
				//l.addAll(basicGetCandidates);
				return l;
			}
			
		};
	}
	
	

	
	@Override
	public int getProposalAcceptanceStyle() {
		return com.onpositive.commons.contentassist.ContentProposalAdapter.PROPOSAL_INSERT_REPLACE;
	}




	public ITextLabelProvider getProposalLabelProvider() {
		return null;
	}
	


}
