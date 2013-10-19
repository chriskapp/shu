/**
 * shu
 * An application wich parses an PHP project and extracts all class, method and 
 * function definitions. Based on the definition it can build various charts
 * and tables to given an overview of the project
 * 
 * Copyright (c) 2013 Christoph Kappestein <k42b3.x@gmail.com>
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
import java.util.List;

import com.k42b3.shu.definition.Definition;
import com.k42b3.shu.definition.File;
import com.k42b3.shu.definition.Function;
import com.k42b3.shu.definition.Method;
import com.k42b3.shu.reference.ClassReference;
import com.k42b3.shu.reference.ExtendReference;
import com.k42b3.shu.reference.Reference;

/**
 * Index
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class Index
{
	private java.io.File dir;
	private ArrayList<File> files = new ArrayList<File>();
	private ArrayList<com.k42b3.shu.definition.Class> classes = new ArrayList<com.k42b3.shu.definition.Class>();
	private ArrayList<Function> functions = new ArrayList<Function>();
	private ArrayList<Method> methods = new ArrayList<Method>();

	public void setDir(java.io.File dir)
	{
		this.dir = dir;
	}

	public java.io.File getDir()
	{
		return this.dir;
	}

	public void addFile(com.k42b3.shu.definition.File f)
	{
		files.add(f);
	}

	public ArrayList<com.k42b3.shu.definition.File> getFiles()
	{
		return files;
	}

	public void addClass(com.k42b3.shu.definition.Class c)
	{
		classes.add(c);
	}

	public ArrayList<com.k42b3.shu.definition.Class> getClasses()
	{
		return classes;
	}

	public File findFile(java.io.File file)
	{
		for(int i = 0; i < files.size(); i++)
		{
			if(files.get(i).getFile().equals(file))
			{
				return files.get(i);
			}
		}
		return null;
	}

	public com.k42b3.shu.definition.Class findClassByName(String name)
	{
		for(int i = 0; i < classes.size(); i++)
		{
			if(classes.get(i).getName().equals(name))
			{
				return classes.get(i);
			}
		}
		return null;
	}

	public com.k42b3.shu.definition.Class findParentClass(com.k42b3.shu.definition.Class c)
	{
		File file = findFile(c.getFile());
		List<Definition> defs = file.getDefinitions();

		for(int i = 0; i < defs.size(); i++)
		{
			if(defs.get(i) instanceof ExtendReference)
			{
				ExtendReference ref = (ExtendReference) defs.get(i);

				return findClassByName(ref.getClassName());
			}
		}
		return null;
	}

	public int getInheritanceDepthByClass(com.k42b3.shu.definition.Class c)
	{
		ArrayList<com.k42b3.shu.definition.Class> classes = new ArrayList<com.k42b3.shu.definition.Class>();
		com.k42b3.shu.definition.Class parent = c;
		int depth = 0;

		parent = findParentClass(parent);
		while(parent != null)
		{
			if(!classes.contains(parent))
			{
				parent = findParentClass(parent);
				depth++;
			}
			else
			{
				parent = null;
			}
		}

		return depth;
	}

	public ArrayList<ClassReference> findReferencesByClass(com.k42b3.shu.definition.Class c)
	{
		ArrayList<ClassReference> refs = new ArrayList<ClassReference>();
		
		for(int i = 0; i < files.size(); i++)
		{
			List<Definition> definitions = files.get(i).getDefinitions();
			
			for(int j = 0; j < definitions.size(); j++)
			{
				if(definitions.get(j) instanceof ClassReference)
				{
					ClassReference ref = (ClassReference) definitions.get(j);

					if(ref.getClassName().equals(c.getName()))
					{
						refs.add(ref);
					}
				}
			}
		}

		return refs;
	}

	public ArrayList<Method> findMethodsByClass(com.k42b3.shu.definition.Class c)
	{
		ArrayList<Method> methods = new ArrayList<Method>();

		for(int i = 0; i < this.methods.size(); i++)
		{
			if(this.methods.get(i).getParent().getName().equals(c.getName()))
			{
				methods.add(this.methods.get(i));
			}
		}
		
		return methods;
	}
	
	public int getReferenceCountByFile(File file, Reference type)
	{
		int count = 0;

		for(int i = 0; i < this.files.size(); i++)
		{
			if(this.files.get(i).equals(file))
			{
				List<Definition> definitions = this.files.get(i).getDefinitions();

				for(int j = 0; j < definitions.size(); j++)
				{
					if(definitions.get(j).getClass().isInstance(type))
					{
						count++;
					}
				}
			}
		}

		return count;
	}

	public void addFunction(Function f)
	{
		functions.add(f);
	}
	
	public ArrayList<Function> getFunctions()
	{
		return functions;
	}
	
	public void addMethod(Method m)
	{
		methods.add(m);
	}
	
	public ArrayList<Method> getMethods()
	{
		return methods;
	}
}
