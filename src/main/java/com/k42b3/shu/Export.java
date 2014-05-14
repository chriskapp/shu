/**
 * shu
 * An application wich parses an PHP project and extracts all class and function 
 * definitions. Based on the definition it can build various charts and tables 
 * to give an overview of the code quality
 * 
 * Copyright (c) 2013-2014 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of shu. shu is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * shu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with shu. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.shu;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.k42b3.shu.definition.Function;

/**
 * Export
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
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
