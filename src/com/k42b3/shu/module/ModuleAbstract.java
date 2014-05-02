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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import com.k42b3.shu.Index;
import com.k42b3.shu.Metric;

/**
 * ModuleAbstract
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
abstract public class ModuleAbstract extends JPanel
{
	public ModuleAbstract()
	{
		super(new BorderLayout());
	}

	public String toString()
	{
		return getTitle();
	}

	public void onLoad(Metric metric, Index index)
	{
		JLabel label = new JLabel("Loading ...");
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, 14));
		label.setForeground(Color.GRAY);
		label.setBorder(new EmptyBorder(8, 0, 0, 0));

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(label);

		this.add(panel, BorderLayout.CENTER);

		ModuleWorker worker = new ModuleWorker(metric, index);
		worker.execute();
	}

	abstract public String getTitle();
	abstract public String getDescription();
	abstract public JComponent getComponent(Metric metric, Index index);
	
	class ModuleWorker extends SwingWorker<Void, Exception>
	{
		protected JComponent component;
		protected Metric metric;
		protected Index index;
		protected Exception lastException;

		public ModuleWorker(Metric metric, Index index)
		{
			this.metric = metric;
			this.index = index;
		}

        protected Void doInBackground()
        {
        	try
        	{
        		component = getComponent(metric, index);
        	}
        	catch(Exception e)
        	{
        		publish(e);
        	}

            return null;
        }

        protected void process(List<Exception> chunks) 
        {
        	lastException = chunks.get(chunks.size() - 1);
        }

        protected void done() 
        {
        	removeAll();
        	
        	if(component != null)
        	{
        		add(component, BorderLayout.CENTER);
        	}
        	else if(lastException != null)
        	{
        		// get stacktrace as string
        		StringWriter sw = new StringWriter();
        		PrintWriter pw = new PrintWriter(sw);
        		lastException.printStackTrace(pw);

        		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        		panel.add(new JLabel("<html>" + lastException.getMessage() + "<br /><pre>" + sw.toString() + "</pre></html>"));

        		add(new JScrollPane(panel), BorderLayout.CENTER);
        	}

        	validate();
        }
	}
}
