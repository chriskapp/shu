package com.k42b3.shu;

import java.io.File;

public interface Processor
{
	public void setListener(DefinitionListener listener);

	public void initialize();

	public void process(File file);
}
