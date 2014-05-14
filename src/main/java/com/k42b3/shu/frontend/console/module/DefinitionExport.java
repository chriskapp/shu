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

package com.k42b3.shu.frontend.console.module;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.k42b3.shu.Export;
import com.k42b3.shu.Scanner;

/**
 * DefinitionExport
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class DefinitionExport extends ModuleAbstract
{
	public String getTitle()
	{
		return "export";
	}

	public String getDescription()
	{
		return "Exports all scanned classes, methods and functions into an json file";
	}

	public void onLoad(Scanner scanner, String[] args)
	{
		String result = Export.toJson(scanner.getIndex().getClasses(), scanner.getIndex().getFunctions());

		if(args.length == 0)
		{
			System.out.print(result);
		}
		else
		{
			File file = new File(args[0]);

			try
			{
				if(file.createNewFile())
				{
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					writer.write(result);
					writer.close();

					System.console().format("--\n");
					System.console().format("Definition written to " + file.getAbsolutePath() + "\n");
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
