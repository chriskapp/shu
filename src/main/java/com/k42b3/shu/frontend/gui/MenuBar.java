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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.k42b3.shu.Shu;

/**
 * MenuBar
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class MenuBar extends JMenuBar
{
	protected MenuBarActionListener listener;

	public MenuBar()
	{
		super();

		buildAction();
	}

	public void setActionListener(MenuBarActionListener listener)
	{
		this.listener = listener;
	}

	protected void buildAction()
	{
		JMenu menu = new JMenu("File");

		JMenuItem itemRun = new JMenuItem("Open Project");
		itemRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		itemRun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onActionOpen();
			}

		});
		menu.add(itemRun);
		
		JMenuItem itemAbout = new JMenuItem("About");
		itemAbout.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				String msg = "";
				msg+= "Shu" + "\n";
				msg+= "Version: " + Shu.VERSION + "\n";
				msg+= "Developer: Christoph Kappestein\n";
				msg+= "Website: http://shu.k42b3.com";

				JOptionPane.showMessageDialog(null, msg, "About", JOptionPane.INFORMATION_MESSAGE);
			}

		});
		menu.add(itemAbout);
		
		JMenuItem itemExit = new JMenuItem("Exit");
		itemExit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onActionExit();
			}

		});
		menu.add(itemExit);
		
		this.add(menu);
	}

	public interface MenuBarActionListener
	{
		public void onActionOpen();
		public void onActionExit();
	}
}
