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

package com.k42b3.shu.definition;

import java.util.LinkedList;
import java.util.List;

/**
 * File
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class File extends Definition
{
	protected List<Definition> definitions = new LinkedList<Definition>();
	protected int tokenCount = 0;

	public List<Definition> getDefinitions()
	{
		return definitions;
	}

	public void addDefinition(Definition definition)
	{
		this.definitions.add(definition);
	}

	public int getTokenCount()
	{
		return tokenCount;
	}

	public void setTokenCount(int tokenCount)
	{
		this.tokenCount = tokenCount;
	}
}
