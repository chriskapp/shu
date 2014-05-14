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

package com.k42b3.shu;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import javax.swing.UIManager;

/**
 * Entry
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class Entry
{
	public static void main(String[] args) throws Exception
	{
		ArrayList<String> params = new ArrayList<String>();
		for(int i = 0; i < args.length; i++)
		{
			if(!args[i].equals("--console"))
			{
				params.add(args[i]);
			}
		}

		boolean consoleForced = params.size() != args.length;
		args = params.toArray(new String[params.size()]);

		FrontendInterface main;

		if(consoleForced || GraphicsEnvironment.isHeadless())
		{
			main = new com.k42b3.shu.frontend.console.Main();
		}
		else
		{
			String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lookAndFeel);

			main = new com.k42b3.shu.frontend.gui.Main();
		}

		Scanner scanner = new Scanner();

		main.handle(scanner, args);
	}
}
