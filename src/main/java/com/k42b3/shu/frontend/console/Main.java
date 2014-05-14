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

package com.k42b3.shu.frontend.console;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import com.k42b3.shu.FrontendInterface;
import com.k42b3.shu.Scanner;
import com.k42b3.shu.Scanner.ScanActionListener;
import com.k42b3.shu.frontend.console.module.DefinitionExport;
import com.k42b3.shu.frontend.console.module.ModuleAbstract;

/**
 * Main
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class Main implements FrontendInterface
{
	protected Scanner scanner;

	protected ArrayList<ModuleAbstract> modules;

	public Main()
	{
		modules = new ArrayList<ModuleAbstract>();
		modules.add(new DefinitionExport());
	}

	public void handle(Scanner scanner, String[] args)
	{
		this.scanner = scanner;

		scanner.setActionListener(new ScanActionListener(){

			public void onScan(File file)
			{
				System.console().format(file.getAbsolutePath() + "\n");
			}

		});

		String path = null;
		String action = null;
		String[] params = new String[args.length];

		if(args.length == 0)
		{
			displayHelp();
			return;
		}
		else if(args.length == 1)
		{
			path = System.getProperty("user.dir");
			action = args[0];
		}
		else if(args.length > 1)
		{
			path = args[0];
			action = args[1];

			if(args.length > 2)
			{
				params = Arrays.copyOfRange(args, 2, args.length);
			}
		}

		if(path != null && action != null)
		{
			File dir = new File(path);
			
			if(!dir.isDirectory() && !dir.isFile())
			{
				System.console().format("Path is not an file or directory\n\n");

				displayHelp();
				return;
			}
			
			if(!isValidAction(action))
			{
				System.console().format("Action is not valid\n\n");

				displayHelp();
				return;
			}

			System.console().format("Scanning path " + dir.getAbsolutePath() + "\n");

			scanner.scan(dir);

			this.getModule(action).onLoad(scanner, params);
		}
		else
		{
			displayHelp();
		}
	}
	
	protected void displayHelp()
	{
		StringBuilder modules = new StringBuilder();

		for(int i = 0; i < this.modules.size(); i++)
		{
			modules.append(this.modules.get(i).getTitle());
			
			if(i < this.modules.size() - 1)
			{
				modules.append("|");
			}
		}

		System.console().format("> shu [path] " + modules.toString() + " [options]\n");
	}

	protected boolean isValidAction(String action)
	{
		return action != null && getModule(action) != null;
	}

	protected ModuleAbstract getModule(String action)
	{
		for(int i = 0; i < modules.size(); i++)
		{
			if(modules.get(i).getTitle().equals(action))
			{
				return modules.get(i);
			}
		}
		return null;
	}
}
