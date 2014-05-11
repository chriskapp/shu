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
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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
	protected DefinitionTableModel classTableModel;
	protected DefinitionTableModel methodTableModel;
	protected DefinitionTableModel functionTableModel;

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
		JTabbedPane tab = new JTabbedPane();
		tab.addTab("Class", this.getClassPanel(index));
		tab.addTab("Method", this.getMethodPanel(index));
		tab.addTab("Function", this.getFunctionPanel(index));

		return tab;
	}
	
	protected DefaultTableModel getClassTableModel()
	{
		classTableModel = new DefinitionTableModel();
		classTableModel.addColumn("Name");
		classTableModel.addColumn("File");
		classTableModel.addColumn("Line");
		
		return classTableModel;
	}
	
	protected Component getClassPanel(Index index)
	{
		JPanel classPanel = new JPanel(new BorderLayout());
		
		JTextField classTextField = new JTextField();
		classTextField.addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e)
			{
			}
			
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					JTextField source = (JTextField) e.getSource();

					classTableModel.filter(source.getText());
				}
			}
			
			public void keyPressed(KeyEvent e)
			{
			}
			
		});
	
		classPanel.add(classTextField, BorderLayout.NORTH);
		
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
				}
			}

		});
		
		classPanel.add(new JScrollPane(classTable), BorderLayout.CENTER);
		
		// load
		ArrayList<com.k42b3.shu.definition.Class> classes = index.getClasses();
		for(int i = 0; i < classes.size(); i++)
		{
			Object[] row = {
				classes.get(i).getName(),
				classes.get(i).getFile(),
				classes.get(i).getLine()
			};

			classTableModel.addRow(row);
		}

		return classPanel;
	}
	
	protected DefaultTableModel getMethodTableModel()
	{
		methodTableModel = new DefinitionTableModel();
		methodTableModel.addColumn("Name");
		methodTableModel.addColumn("Class");
		methodTableModel.addColumn("File");
		methodTableModel.addColumn("Line");
		
		return methodTableModel;
	}
	
	protected Component getMethodPanel(Index index)
	{
		JPanel methodPanel = new JPanel(new BorderLayout());
		
		JTextField methodTextField = new JTextField();
		methodTextField.addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e)
			{
			}
			
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					JTextField source = (JTextField) e.getSource();

					methodTableModel.filter(source.getText());
				}
			}
			
			public void keyPressed(KeyEvent e)
			{
			}
			
		});
	
		methodPanel.add(methodTextField, BorderLayout.NORTH);
		
		DefaultTableModel methodTableModel = getMethodTableModel();
		JTable methodTable = new JTable(methodTableModel);
		
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
		
		methodPanel.add(new JScrollPane(methodTable), BorderLayout.CENTER);
		
		return methodPanel;
	}
	
	protected DefaultTableModel getFunctionTableModel()
	{
		functionTableModel = new DefinitionTableModel();
		functionTableModel.addColumn("Name");
		functionTableModel.addColumn("File");
		functionTableModel.addColumn("Line");
		
		return functionTableModel;
	}
	
	protected Component getFunctionPanel(Index index)
	{
		JPanel functionPanel = new JPanel(new BorderLayout());
		
		JTextField methodTextField = new JTextField();
		methodTextField.addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e)
			{
			}
			
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					JTextField source = (JTextField) e.getSource();

					functionTableModel.filter(source.getText());
				}
			}
			
			public void keyPressed(KeyEvent e)
			{
			}
			
		});
	
		functionPanel.add(methodTextField, BorderLayout.NORTH);

		DefaultTableModel functionTableModel = getFunctionTableModel();
		JTable functionTable = new JTable(functionTableModel);

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
		
		functionPanel.add(new JScrollPane(functionTable), BorderLayout.CENTER);
		
		return functionPanel;
	}
	
	class DefinitionTableModel extends DefaultTableModel
	{
		protected ArrayList<Integer> exclude;
		
		public void filter(String filter)
		{
			this.exclude = new ArrayList<Integer>();

			if(filter != null && !filter.isEmpty())
			{
				for(int i = 0; i < super.getRowCount(); i++)
				{
					Object value = super.getValueAt(i, 0);

					if(value != null)
					{
						if(!value.toString().toLowerCase().matches("(.*)" + filter.toLowerCase() + "(.*)"))
						{
							this.exclude.add(i);
						}
					}
				}
			}

			this.fireTableDataChanged();
		}

		public int getRowCount()
		{
			if(this.exclude != null)
			{
				return super.getRowCount() - this.exclude.size();
			}
			else
			{
				return super.getRowCount();
			}
		}

		public Object getValueAt(int row, int column)
		{
			if(this.exclude != null)
			{
				while(this.exclude.contains(row))
				{
					row++;
				}
			}

			return super.getValueAt(row, column);
		}

		public boolean isCellEditable(int row, int col)
		{
			return false;
		}
	}
}
