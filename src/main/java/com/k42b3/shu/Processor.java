package com.k42b3.shu;

import java.io.File;

public interface Processor
{
	public void setListener(DefinitionListener listener);

	public void start() throws Exception;

	public void close() throws Exception;

	public void process(File file);
}
