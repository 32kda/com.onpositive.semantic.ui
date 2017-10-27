package com.onpositive.commons.namespace.ide.ui.completion;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeCompletionProvider;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeValidator;


public abstract class AbstractTypeValueProvider implements ITypeCompletionProvider, ITypeValidator
{
	private final String[] standart;
	protected boolean addBraces = false;
	
	public AbstractTypeValueProvider()
	{
		standart=getValues();
	}
	
	protected abstract String[] getValues();
	
	
	public String validate(IProject project, String value,
			DomainEditingModelObject element,String typeSpecialization) {
		return validate(value);
	}
	
	/**
	 * Fills completion proposal list (result)
	 * Simply iterates through possible values and check, which of them starts with startString
	 * @param startString Completion start string
	 * @param result result list to add elements to
	 * @param offset completion call offset
	 * @param fullString full string, before and after cursor, where completion was called. We need this 
	 * because in some cases we need to overwrite by some proposal the rest of word too
	 * @return proposal list
	 */
	protected ArrayList<ICompletionProposal> getResult(String startString, ArrayList<ICompletionProposal> result, int offset, String fullString)
	{	
		
		
		if (startString.startsWith("\"")) startString = startString.substring(1);
		if (result == null) result = new ArrayList<ICompletionProposal>();
		startString = startString.toLowerCase().trim();
		for (int i = 0; i < standart.length; i++)
		{
			if (standart[i].startsWith(startString)) 
			{   
				int replacementLength;
				if (fullString != null && fullString.length() > 0) replacementLength = fullString.length(); 
				else replacementLength = startString.length();
				String replacementString = standart[i];
				if (addBraces) replacementString = "\"" + replacementString + "\""; 
				
				result.add(new TypeCompletionProposal(replacementString, null, standart[i],offset - startString.length(),replacementLength));
			}
		}
		return result;
	}




	public String validate(String value)
	{
		for (int i = 0; i < standart.length; i++)
			if (value.toLowerCase().trim().equals(standart[i])) return null;
		return "Value " + value + " doesn't match attr datatype. Must be one of " + getAllVariantsString();		
	}
	
	protected String getAllVariantsString()
	{
		String str = "'" + standart[0] + "'";
		for (int i = 1; i < standart.length; i++)
		{
			str = str + " ,'" + standart[i] + "'";			
		}
		return str;
	}
	
	public void fillProposals(String attributeName,
			DomainEditingModelObject findElement, ITextViewer viewer,
			int offset, String startString, int lengthCompletion,
			ArrayList<ICompletionProposal> result, String fullString, boolean addBraces,String typeSpec)
	{
		this.addBraces = addBraces;
		getResult(startString, result, offset, fullString);
	}
	
}
