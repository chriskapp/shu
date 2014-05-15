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

package com.k42b3.shu.frontend.gui.module;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.k42b3.shu.Index;
import com.k42b3.shu.Metric;

/**
 * Statistic
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class Statistic extends ModuleAbstract
{
	public String getTitle()
	{
		return "Statistic";
	}
	
	public String getDescription()
	{
		return "Displays all available metrics";
	}
	
	public JComponent getComponent(Metric metric, Index index)
	{
		DefaultTableModel tableModel = new DefaultTableModel(){

			public boolean isCellEditable(int row, int column)
			{
				return false;
			}

		};

		tableModel.addColumn("Name");
		tableModel.addColumn("Value");

		Object[][] rows = {
			{"Parsed files", metric.getParsedFiles()},
			{"Inspected files", metric.getInspectedFiles()},
			//{"PHP files", metric.getTokenFiles()},
			//{"Other files", metric.getNoTokenFiles()},
			{"Class count", metric.getClassCount()},
			{"Function count", metric.getFunctionCount()},
			{"Interface count", metric.getInterfaceCount()}
		};

		for(int i = 0; i < rows.length; i++)
		{
			tableModel.addRow(rows[i]);
		}

		// panel
		JTable table = new JTable(tableModel);
		JScrollPane panel = new JScrollPane(table);

		return panel;
	}
}
