package com.k42b3.shu.module;

import javax.swing.JComponent;

import com.k42b3.shu.Index;
import com.k42b3.shu.Metric;

public class VariableSearch extends ModuleAbstract
{
	public String getTitle()
	{
		return "Variable search";
	}

	public String getDescription()
	{
		return "Searches for an variable name";
	}

	public JComponent getComponent(Metric metric, Index index)
	{
		return null;
	}
}
