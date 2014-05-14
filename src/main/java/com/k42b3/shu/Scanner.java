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

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.k42b3.shu.definition.Definition;
import com.k42b3.shu.definition.Function;
import com.k42b3.shu.definition.Interface;
import com.k42b3.shu.definition.Method;
import com.k42b3.shu.processor.PhpProcessor;

/**
 * Scanner
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class Scanner
{
	protected Metric metric;
	protected Index index;
	protected Processor processor;

	protected JLabel label;
	protected JFrame loadingFrame;

	protected ScanActionListener listener;

	public Scanner()
	{
		this.metric = new Metric();
		this.index = new Index();
		
		this.setProcessor(new PhpProcessor());
	}
	
	public void setActionListener(ScanActionListener listener)
	{
		this.listener = listener;
	}

	public void setProcessor(Processor processor)
	{
		this.processor = processor;
		this.processor.setListener(new DefinitionListener() {
			
			public void onDefinition(Definition definition)
			{
				handleDefinition(definition);
			}
			
		});
	}

	public void scan(File dir)
	{
		try
		{
			this.index.setDir(dir);

			this.processor.start();

			this.parse(dir);
			
			this.processor.close();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "Information", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public Metric getMetrics()
	{
		return metric;
	}
	
	public Index getIndex()
	{
		return index;
	}

	protected void parse(File dir)
	{
		if(dir.isFile())
		{
			metric.increaseParsedFiles();

			if(dir.getName().endsWith(".php"))
			{
				if(listener != null)
				{
					listener.onScan(dir);
				}

				this.inspectFile(dir);
			}
		}
		else if(dir.isDirectory())
		{
			File[] files = dir.listFiles();

			for(int i = 0; i < files.length; i++)
			{
				if((files[i].isDirectory() && files[i].getName().startsWith(".")) || files[i].isHidden())
				{
					continue;
				}

				if(files[i].isDirectory())
				{
					this.parse(files[i]);
				}
				else if(files[i].isFile())
				{
					metric.increaseParsedFiles();

					if(files[i].getName().endsWith(".php"))
					{
						if(listener != null)
						{
							listener.onScan(files[i]);
						}

						this.inspectFile(files[i]);
					}
				}
			}
		}
	}

	protected void inspectFile(File file)
	{
		processor.process(file);
	}

	protected void handleDefinition(Definition definition)
	{
		if(definition instanceof Interface)
		{
			metric.increaseInterfaceCount();

			index.addClass((Interface) definition);
		}
		else if(definition instanceof com.k42b3.shu.definition.Class)
		{
			metric.increaseClassCount();
			
			index.addClass((com.k42b3.shu.definition.Class) definition);
		}
		else if(definition instanceof com.k42b3.shu.definition.File)
		{
			metric.increaseInspectedFiles();
			
			index.addFile((com.k42b3.shu.definition.File) definition);
		}
		else if(definition instanceof Function)
		{
			metric.increaseFunctionCount();
			
			index.addFunction((Function) definition);
		}
		else if(definition instanceof Method)
		{
			metric.increaseMethodCount();
			
			index.addMethod((Method) definition);
		}
	}

	public interface ScanActionListener
	{
		public void onScan(File file);
	}
}
