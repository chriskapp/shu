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

package com.k42b3.shu.module;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.k42b3.shu.Index;
import com.k42b3.shu.Metric;
import com.k42b3.shu.definition.File;
import com.k42b3.shu.definition.Function;
import com.k42b3.shu.definition.Method;

/**
 * Definition
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class Definition extends ModuleAbstract
{
	public String getTitle()
	{
		return "Definition";
	}

	public String getDescription()
	{
		return "Displays all scanned classes, methods and functions";
	}
	
	public JComponent getComponent(Metric metric, Index index)
	{
		DefaultTableModel classTableModel = getClassTableModel();
		JTable classTable = new JTable(classTableModel);
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(classTableModel);
		classTable.setRowSorter(sorter);
		classTable.addMouseListener(new MouseListener(){
			
			public void mouseReleased(MouseEvent e)
			{
			}
			
			public void mousePressed(MouseEvent e)
			{
			}
			
			public void mouseExited(MouseEvent e)
			{
			}
			
			public void mouseEntered(MouseEvent e)
			{
			}
			
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() == 2)
				{
					/*
					int row = classTable.getSelectedRow();
					if(row != -1)
					{
						java.io.File file = (java.io.File) classTable.getValueAt(row, 1);
					}
					*/
				}
			}

		});

		DefaultTableModel methodTableModel = getMethodTableModel();
		JTable methodTable = new JTable(methodTableModel);

		DefaultTableModel functionTableModel = getFunctionTableModel();
		JTable functionTable = new JTable(functionTableModel);

		// load
		ArrayList<com.k42b3.shu.definition.Class> classes = index.getClasses();
		for(int i = 0; i < classes.size(); i++)
		{
			Object[] row = {
				classes.get(i).getName(),
				classes.get(i).getFile(),
				classes.get(i).getLine(),
				index.findReferencesByClass(classes.get(i)).size()
			};

			classTableModel.addRow(row);
		}
		
		ArrayList<Method> methods = index.getMethods();
		for(int i = 0; i < methods.size(); i++)
		{
			Object[] row = {
				methods.get(i).getName(),
				methods.get(i).getParent().getName(),
				methods.get(i).getFile(),
				methods.get(i).getLine()
			};

			methodTableModel.addRow(row);
		}
		
		ArrayList<Function> functions = index.getFunctions();
		for(int i = 0; i < functions.size(); i++)
		{
			Object[] row = {
				functions.get(i).getName(),
				functions.get(i).getFile(),
				functions.get(i).getLine()
			};

			functionTableModel.addRow(row);
		}

		// panel
		JTabbedPane tab = new JTabbedPane();
		tab.addTab("Class", new JScrollPane(classTable));
		tab.addTab("Method", new JScrollPane(methodTable));
		tab.addTab("Function", new JScrollPane(functionTable));
		
		return tab;
	}
	
	protected DefaultTableModel getClassTableModel()
	{
		DefaultTableModel classTableModel = new DefaultTableModel(){

			public Class getColumnClass(int col)
			{
				switch(col)
				{
					case 0:
						return String.class;
					case 1:
						return File.class;
					case 2:
					case 3:
						return Integer.class;
				}
				return null;
			}

			public boolean isCellEditable(int row, int col)
			{
				return false;
			}

		};
		classTableModel.addColumn("Name");
		classTableModel.addColumn("File");
		classTableModel.addColumn("Line");
		classTableModel.addColumn("References");
		
		return classTableModel;
	}
	
	private DefaultTableModel getMethodTableModel()
	{
		DefaultTableModel methodTableModel = new DefaultTableModel();
		methodTableModel.addColumn("Name");
		methodTableModel.addColumn("Class");
		methodTableModel.addColumn("File");
		methodTableModel.addColumn("Line");
		
		return methodTableModel;
	}
	
	private DefaultTableModel getFunctionTableModel()
	{
		DefaultTableModel functionTableModel = new DefaultTableModel();
		functionTableModel.addColumn("Name");
		functionTableModel.addColumn("File");
		functionTableModel.addColumn("Line");
		
		return functionTableModel;
	}
}
