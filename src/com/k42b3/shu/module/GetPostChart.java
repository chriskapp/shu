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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.k42b3.shu.Index;
import com.k42b3.shu.Metric;
import com.k42b3.shu.definition.File;
import com.k42b3.shu.reference.GetReference;
import com.k42b3.shu.reference.PostReference;

/**
 * GetPostChart
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class GetPostChart extends ModuleAbstract
{
	public String getTitle()
	{
		return "Get/Post Usage";
	}

	public String getDescription()
	{
		return "Shows all $_GET/$_POST usages across all files";
	}
	
	public JComponent getComponent(Metric metric, Index index)
	{
		JPanel panel = new JPanel(new BorderLayout());
		
		XYSeries serieGet = new XYSeries("$_GET");
		XYSeries seriePost = new XYSeries("$_POST");
		List<File> files = index.getFiles();
		ArrayList<Entry> entries = new ArrayList<Entry>();
		
		for(int i = 0; i < files.size(); i++)
		{
			int getCount = index.getReferenceCountByFile(files.get(i), new GetReference());
			int postCount = index.getReferenceCountByFile(files.get(i), new PostReference());
			
			serieGet.add(i, getCount);
			seriePost.add(i, postCount);
			
			entries.add(new Entry(files.get(i), getCount, "GET"));
			entries.add(new Entry(files.get(i), postCount, "POST"));
		}
		
		// top entries
		Collections.sort(entries, new EntryComperator());
		
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("File");
		model.addColumn("Count");
		model.addColumn("Type");

		for(int i = 0; i < entries.size(); i++)
		{
			String[] row = {
				entries.get(i).getFile().getFile().getName(),
				"" + entries.get(i).getCount(),
				entries.get(i).getType()
			};

			model.addRow(row);
		}

		JTable table = new JTable(model);
		table.getColumnModel().getColumn(1).setMaxWidth(40);
		table.getColumnModel().getColumn(2).setMaxWidth(40);

		JScrollPane scp = new JScrollPane(table);
		scp.setPreferredSize(new Dimension(200, 100));

		panel.add(scp, BorderLayout.WEST);

		// chart panel
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(serieGet);
		dataset.addSeries(seriePost);

		NumberAxis xax = new NumberAxis("");
		NumberAxis yax = new NumberAxis("");

		SamplingXYLineRenderer renderer = new SamplingXYLineRenderer();
		XYPlot plot = new XYPlot(dataset, xax, yax, renderer);

		JFreeChart chart = new JFreeChart(plot);
		chart.clearSubtitles();
		
		panel.add(new ChartPanel(chart), BorderLayout.CENTER);
		
		return panel;
	}
	
	private class Entry implements Comparable
	{
		protected File file;
		protected int count;
		protected String type;

		public Entry(File file, int count, String type)
		{
			super();

			this.file = file;
			this.count = count;
			this.type = type;
		}

		public File getFile()
		{
			return file;
		}

		public int getCount()
		{
			return count;
		}

		public String getType()
		{
			return type;
		}

		public int compareTo(Object o)
		{
			if(o instanceof Entry)
			{
				return ((Entry) o).getCount() - getCount();
			}

			return 0;
		}
	}
	
	private class EntryComperator implements Comparator<Entry>
	{
		public int compare(Entry e0, Entry e1)
		{
			return e0.compareTo(e1);
		}
	}
}
