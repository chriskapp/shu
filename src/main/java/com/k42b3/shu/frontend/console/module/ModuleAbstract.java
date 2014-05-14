package com.k42b3.shu.frontend.console.module;

import com.k42b3.shu.Scanner;

abstract public class ModuleAbstract
{
	public String toString()
	{
		return getTitle();
	}

	abstract public String getTitle();
	abstract public String getDescription();
	abstract public void onLoad(Scanner scanner, String[] args);
}
