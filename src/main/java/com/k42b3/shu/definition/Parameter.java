package com.k42b3.shu.definition;

public class Parameter
{
	protected String name;
	protected String typeHint;
	protected boolean isRequired;

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getTypeHint()
	{
		return typeHint;
	}
	
	public void setTypeHint(String typeHint)
	{
		this.typeHint = typeHint;
	}
	
	public boolean isRequired()
	{
		return isRequired;
	}
	
	public void setRequired(boolean isRequired)
	{
		this.isRequired = isRequired;
	}
}
