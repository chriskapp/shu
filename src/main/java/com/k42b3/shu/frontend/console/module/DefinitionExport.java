
package com.k42b3.shu.frontend.console.module;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.k42b3.shu.Export;
import com.k42b3.shu.Scanner;

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
