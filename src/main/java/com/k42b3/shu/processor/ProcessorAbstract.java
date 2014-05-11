package com.k42b3.shu.processor;

import com.k42b3.shu.DefinitionListener;
import com.k42b3.shu.Processor;

abstract public class ProcessorAbstract implements Processor
{
	protected DefinitionListener listener;

	public void setListener(DefinitionListener listener)
	{
		this.listener = listener;
	}
}
