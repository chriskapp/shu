package com.k42b3.shu.definition;

import java.util.ArrayList;

public class Parameters
{
	protected ArrayList<Parameter> parameters = new ArrayList<Parameter>();

	public void add(String name, String typeHint, boolean isRequired)
	{
		Parameter parameter = new Parameter();
		parameter.setName(name);
		parameter.setTypeHint(typeHint);
		parameter.setRequired(isRequired);

		this.parameters.add(parameter);
	}

	public void add(Parameter parameter)
	{
		this.parameters.add(parameter);
	}

	public Parameter get(int position)
	{
		return this.parameters.get(position);
	}

	public int getLength()
	{
		return this.parameters.size();
	}
}
