package com.k42b3.shu;

import java.io.File;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.k42b3.shu.definition.Function;

public class Export
{
	protected ArrayList<com.k42b3.shu.definition.Class> classes;
	protected ArrayList<Function> functions;
	
	public Export(ArrayList<com.k42b3.shu.definition.Class> classes, ArrayList<Function> functions)
	{
		this.classes = classes;
		this.functions = functions;
	}

	public ArrayList<com.k42b3.shu.definition.Class> getClasses()
	{
		return classes;
	}

	public void setClasses(ArrayList<com.k42b3.shu.definition.Class> classes)
	{
		this.classes = classes;
	}

	public ArrayList<Function> getFunctions()
	{
		return functions;
	}

	public void setFunctions(ArrayList<Function> functions)
	{
		this.functions = functions;
	}
	
	public static String toJson(ArrayList<com.k42b3.shu.definition.Class> classes, ArrayList<Function> functions)
	{
		Gson gson = new Gson();
		Export export = new Export(classes, functions);

		return gson.toJson(export);
	}
}
