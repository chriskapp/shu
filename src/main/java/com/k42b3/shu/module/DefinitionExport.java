/**
 * shu
 * An application wich parses an PHP project and extracts all class, method and 
 * function definitions. Based on the definition it can build various charts
 * and tables to given an overview of the project
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

package com.k42b3.shu.module;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import com.google.gson.Gson;
import com.k42b3.shu.Index;
import com.k42b3.shu.Metric;
import com.k42b3.shu.definition.Function;

/**
 * DefinitionExport
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class DefinitionExport extends ModuleAbstract
{
	public String getTitle()
	{
		return "Definition export";
	}

	public String getDescription()
	{
		return "Exports all scanned classes, methods and functions into an json file";
	}
	
	public JComponent getComponent(Metric metric, final Index index)
	{
		JButton btnExport = new JButton("Export");
		btnExport.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				export(index);
			}
			
		});
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(btnExport, BorderLayout.CENTER);
		
		return panel;
	}
	
	protected void export(Index index)
	{
		final JFileChooser fc = new JFileChooser();
		
		int returnVal = fc.showSaveDialog(this);
		
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			Gson gson = new Gson();
			File file = fc.getSelectedFile();

			String result = gson.toJson(new Export(index.getClasses(), index.getFunctions()));

			try
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writer.write(result);
				writer.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	class Export
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
	}
}
