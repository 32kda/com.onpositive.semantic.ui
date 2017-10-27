package com.onpositive.semantic.generator.model;

public abstract class UIElementCandidate
{

	protected String id;
	protected String name;
	protected String typeName;
	protected String caption;

	/**
	 * @return the caption
	 */
	public String getCaption()
	{
		return caption;
	}

	public UIElementCandidate(String caption, String id, String name,
			String typeName)
	{
		super();
		this.caption = caption;
		this.id = id;
		this.name = name;
		this.typeName = typeName;
	}

	/**
	 * @param caption
	 *            the caption to set
	 */
	public void setCaption(String caption)
	{
		this.caption = caption;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName()
	{
		return typeName;
	}

	/**
	 * @param typeName
	 *            the typeName to set
	 */
	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}
}
