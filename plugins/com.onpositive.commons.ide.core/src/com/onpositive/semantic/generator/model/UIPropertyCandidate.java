package com.onpositive.semantic.generator.model;


public class UIPropertyCandidate extends UIElementCandidate
{
	protected boolean readOnly = false;
	protected boolean required = false;

	public UIPropertyCandidate(String caption, String id, String name,
			String typeName)
	{
		super(caption, id, name, typeName);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly()
	{
		return readOnly;
	}

	
	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	
	/**
	 * @return the required
	 */
	public boolean isRequired()
	{
		return required;
	}

	
	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required)
	{
		this.required = required;
	}
	
	
	public String toString()
	{	
		return name + " property candidate";
	}

}
