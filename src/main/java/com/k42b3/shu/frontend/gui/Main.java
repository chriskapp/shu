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

package com.k42b3.shu.frontend.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.k42b3.shu.FrontendInterface;
import com.k42b3.shu.Scanner;
import com.k42b3.shu.Scanner.ScanActionListener;
import com.k42b3.shu.Shu;
import com.k42b3.shu.frontend.gui.MenuBar.MenuBarActionListener;
import com.k42b3.shu.frontend.gui.module.BrowseFile;
import com.k42b3.shu.frontend.gui.module.ClassGraph;
import com.k42b3.shu.frontend.gui.module.ComplexityChart;
import com.k42b3.shu.frontend.gui.module.Definition;
import com.k42b3.shu.frontend.gui.module.DefinitionExport;
import com.k42b3.shu.frontend.gui.module.ModuleAbstract;
import com.k42b3.shu.frontend.gui.module.ReferenceChart;
import com.k42b3.shu.frontend.gui.module.Statistic;
import com.k42b3.shu.frontend.gui.module.TokenChart;

/**
 * Main
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class Main extends JFrame implements FrontendInterface
{
	protected DefaultListModel<ModuleAbstract> navigationModel;
	protected JList<ModuleAbstract> navigation;
	protected ContentPane content;
	protected JLabel footer;

	protected Scanner scanner;
	protected boolean loaded = false;

	public Main()
	{
		// settings
		this.setTitle("Shu (Version: " + Shu.VERSION + ")");
		this.setLocation(100, 100);
		this.setSize(600, 500);
		this.setMinimumSize(this.getSize());
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// set toolbar
		this.setJMenuBar(this.buildMenuBar());

		// navigation
		navigationModel = new DefaultListModel<ModuleAbstract>();
		navigationModel.addElement(new Definition());
		navigationModel.addElement(new BrowseFile());
		navigationModel.addElement(new ReferenceChart());
		navigationModel.addElement(new ComplexityChart());
		navigationModel.addElement(new TokenChart());
		navigationModel.addElement(new DefinitionExport());
		//navigationModel.addElement(new VariableSearch());
		navigationModel.addElement(new ClassGraph());
		navigationModel.addElement(new Statistic());

		navigation = new JList<ModuleAbstract>(navigationModel);
		navigation.setPreferredSize(new Dimension(160, 100));
		navigation.setCellRenderer(new NavigationCellRenderer());
		navigation.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e)
			{
				JList<ModuleAbstract> list = (JList<ModuleAbstract>) e.getSource();
				ModuleAbstract module = (ModuleAbstract) list.getSelectedValue();

				if(module != null && loaded)
				{
					boolean found = false;

					for(int i = 0; i < content.getTabCount(); i++)
					{
						if(content.getComponent(i).getClass().getName().equals(module.getClass().getName()))
						{
							content.setSelectedIndex(i);

							found = true;
							break;
						}
					}

					if(!found)
					{
						module.onLoad(scanner);

						content.addTab(module.getTitle(), module);
					}
				}
			}
			
		});

		this.add(navigation, BorderLayout.WEST);

		// content
		JLabel label = new JLabel("Please open a project (CTRL + O)");
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, 20));
		label.setForeground(Color.GRAY);
		label.setBorder(new EmptyBorder(8, 0, 0, 0));

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(label);

		content = new ContentPane();
		content.addTab("Info", panel);

		this.add(content, BorderLayout.CENTER);
		
		// bottom
		footer = new JLabel("Ready");
		footer.setBorder(new EmptyBorder(4, 4, 4, 4));

		this.add(footer, BorderLayout.SOUTH);
	}

	public void handle(Scanner scanner, String[] args)
	{
		this.scanner = scanner;

		this.pack();
		this.setVisible(true);
	}

	public void onOpen()
	{
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = fc.showOpenDialog(null);
		
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();

			if(file.isDirectory())
			{
				// add loading panel
				content.removeTabAt(0);
				
				JLabel label = new JLabel("Loading ...");
				label.setFont(new Font(label.getFont().getName(), Font.BOLD, 14));
				label.setForeground(Color.GRAY);
				label.setBorder(new EmptyBorder(8, 0, 0, 0));

				JPanel panel = new JPanel(new FlowLayout());
				panel.add(label);
				
				content.addTab("Info", panel);

				// scan
				ScanWorker worker = new ScanWorker(file);
		        worker.execute();
		        
		        //
		        loaded = true;
			}
		}
	}

	public void onExit()
	{
		System.exit(0);
	}

	private JMenuBar buildMenuBar()
	{
		MenuBar menuBar = new MenuBar();
		menuBar.setActionListener(new MenuBarActionListener(){

			public void onActionOpen()
			{
				onOpen();
			}

			public void onActionExit()
			{
				onExit();
			}

		});

		return menuBar;
	}
	
	class ScanWorker extends SwingWorker<Void, File>
	{
		protected File rootDir;

		public ScanWorker(File rootDir)
		{
			this.rootDir = rootDir;
		}

        protected Void doInBackground() throws Exception 
        {
			scanner = new Scanner();
			scanner.setActionListener(new ScanActionListener(){

				public void onScan(File file)
				{
					publish(file);
				}

			});
			scanner.scan(rootDir);

            return null;
        }

        protected void process(List<File> chunks) 
        {
        	footer.setText(chunks.get(chunks.size() - 1).toString());
        }

        protected void done() 
        {
        	content.removeTabAt(0);

        	navigation.clearSelection();
        	navigation.setSelectedIndex(0);
        	
        	footer.setText("Done");
        }
	}

	class NavigationCellRenderer extends JLabel implements ListCellRenderer<ModuleAbstract>
	{
		public NavigationCellRenderer()
		{
			setOpaque(true);
			setBorder(new EmptyBorder(8, 4, 8, 4));
		}

		public Component getListCellRendererComponent(JList<? extends ModuleAbstract> list, ModuleAbstract value, int index, boolean selected, boolean cellHasFocus)
		{
			if(selected)
			{
				setForeground(SystemColor.activeCaptionText);
				setBackground(SystemColor.activeCaption);
			}
			else
			{
				setForeground(SystemColor.textText);
				setBackground(SystemColor.window);
			}

			setText(value.getTitle());
			setToolTipText(value.getDescription());

			return this;
		}
	}
}
