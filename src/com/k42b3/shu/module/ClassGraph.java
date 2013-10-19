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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.k42b3.shu.Index;
import com.k42b3.shu.Metric;
import com.k42b3.shu.reference.ClassReference;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

/**
 * ClassGraph
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class ClassGraph extends ModuleAbstract
{
	private mxGraphComponent graphComponent;

	public String getTitle()
	{
		return "Class Graph";
	}
	
	public String getDescription()
	{
		return "Displays all class relations in an graph";
	}
	
	public JComponent getComponent(Metric metric, Index index)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		mxGraph graph = new mxGraph();
		//graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));

		try
		{
			Object parent = graph.getDefaultParent();

			graph.getModel().beginUpdate();

			// add classes
			ArrayList<com.k42b3.shu.definition.Class> classes = index.getClasses();

			for(int i = 0; i < classes.size(); i++)
			{
				Object obj = graph.insertVertex(parent, null, classes.get(i).getName(), 20, 20, 120, 30);

				map.put(classes.get(i).getName(), obj);
			}

			for(int i = 0; i < classes.size(); i++)
			{
				ArrayList<ClassReference> refs = index.findReferencesByClass(classes.get(i));

				for(int j = 0; j < refs.size(); j++)
				{
					if(refs.get(j).getParentClass() != null)
					{
						String source = refs.get(j).getParentClass().getName();
						String target = classes.get(i).getName();

						graph.insertEdge(parent, null, "", map.get(source), map.get(target));
					}
				}
			}
		}
		finally
		{
			graph.getModel().endUpdate();
		}

		// define layout
		mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
		layout.execute(graph.getDefaultParent());

		// panel
		JPanel panel = new JPanel(new BorderLayout());

		graphComponent = new mxGraphComponent(graph);

		panel.add(graphComponent, BorderLayout.CENTER);

		// buttons
		JButton btnZoomIn = new JButton("Zoom In");
		btnZoomIn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				graphComponent.zoomIn();
			}

		});
		JButton btnZoomOut = new JButton("Zoom Out");
		btnZoomOut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				graphComponent.zoomOut();
			}

		});

		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(btnZoomIn);
		buttons.add(btnZoomOut);

		panel.add(buttons, BorderLayout.NORTH);
		
		return panel;
	}
}
