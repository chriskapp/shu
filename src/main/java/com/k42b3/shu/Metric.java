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

/**
 * Metric
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class Metric
{
	private int inspectedFiles = 0;
	private int parsedFiles = 0;
	private int tokenFiles = 0;
	private int noTokenFiles = 0;

	private int classCount = 0;
	private int functionCount = 0;
	private int interfaceCount = 0;
	private int methodCount = 0;

	private int getCount = 0;
	private int postCount = 0;

	public void increaseInspectedFiles()
	{
		inspectedFiles++;
	}
	
	public int getInspectedFiles()
	{
		return inspectedFiles;
	}
	
	public void increaseParsedFiles()
	{
		parsedFiles++;
	}
	
	public int getParsedFiles()
	{
		return parsedFiles;
	}

	public void increaseTokenFiles()
	{
		tokenFiles++;
	}
	
	public int getTokenFiles()
	{
		return tokenFiles;
	}
	
	public void increaseNoTokenFiles()
	{
		noTokenFiles++;
	}
	
	public int getNoTokenFiles()
	{
		return noTokenFiles;
	}
	
	public void increaseClassCount()
	{
		classCount++;
	}

	public int getClassCount()
	{
		return classCount;
	}
	
	public void increaseFunctionCount()
	{
		functionCount++;
	}

	public int getFunctionCount()
	{
		return functionCount;
	}

	public void increaseInterfaceCount()
	{
		interfaceCount++;
	}

	public int getInterfaceCount()
	{
		return interfaceCount;
	}
	
	public void increaseMethodCount()
	{
		methodCount++;
	}

	public int getMethodCount()
	{
		return methodCount;
	}
	
	public void increaseGetCount()
	{
		getCount++;
	}

	public int getGetCount()
	{
		return getCount;
	}
	
	public void increasePostCount()
	{
		postCount++;
	}

	public int getPostCount()
	{
		return postCount;
	}
}

