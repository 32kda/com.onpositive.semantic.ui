package com.onpositive.semantic.generator.model;

import java.util.ArrayList;

import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;


public class CandidateConfigurationListElement
{
	protected boolean used = true;
	protected UIPropertyCandidate curCandidate;
	protected UIPropertyElementCreator creator;
	
	public static class CreatorRealmProvider implements IRealmProvider
	{

		public IRealm getRealm(IBinding model)
		{
			CandidateConfigurationListElement curElement = (CandidateConfigurationListElement) model.getBinding("creator").getObject();
			ArrayList<UIPropertyElementCreator> creatorList = UIPropertyElementCreatorRegistry.getInstance().getCreatorList(curElement.curCandidate.getTypeName());
			return new Realm<UIPropertyElementCreator>(creatorList);
		}
		
	}
	
	public CandidateConfigurationListElement(UIPropertyElementCreator creator,
			UIPropertyCandidate curCandidate)
	{
		super();
		this.creator = creator;
		this.curCandidate = curCandidate;
		
	}

	/**
	 * @return the used
	 */
	public boolean isUsed()
	{
		return used;
	}
	
	/**
	 * @param used the used to set
	 */
	public void setUsed(boolean used)
	{
		this.used = used;
	}
	
	/**
	 * @return the curCandidate
	 */
	public UIPropertyCandidate getCurCandidate()
	{
		return curCandidate;
	}
	
	/**
	 * @param curCandidate the curCandidate to set
	 */
	public void setCurCandidate(UIPropertyCandidate curCandidate)
	{
		this.curCandidate = curCandidate;
	}
	
	/**
	 * @return the creator
	 */
	public UIPropertyElementCreator getCreator()
	{
		return creator;
	}
	
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(UIPropertyElementCreator creator)
	{
		this.creator = creator;
	}
	
	
	public String toString()
	{
		return "List element created for " + curCandidate.getName() + " Used: " + used;
	}
}
