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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.k42b3.shu.definition.Function;
import com.k42b3.shu.definition.Interface;
import com.k42b3.shu.definition.Method;
import com.k42b3.shu.reference.ExtendReference;
import com.k42b3.shu.reference.GetReference;
import com.k42b3.shu.reference.ImplementReference;
import com.k42b3.shu.reference.InstanceofReference;
import com.k42b3.shu.reference.NewReference;
import com.k42b3.shu.reference.PostReference;
import com.k42b3.shu.reference.StaticReference;
import com.k42b3.shu.reference.TypeHintReference;
import com.k42b3.shu.token.Token;

/**
 * Scanner
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class Scanner
{
	protected Metric metric;
	protected Index index;

	protected JLabel label;
	protected JFrame loadingFrame;

	protected ScanActionListener listener;

	public Scanner()
	{
		this.metric = new Metric();
		this.index = new Index();
	}
	
	public void setActionListener(ScanActionListener listener)
	{
		this.listener = listener;
	}

	public void scan(File dir)
	{
		index.setDir(dir);

		this.parse(dir);
	}
	
	public Metric getMetrics()
	{
		return metric;
	}
	
	public Index getIndex()
	{
		return index;
	}
	
	protected void parse(File dir)
	{
		File[] files = dir.listFiles();

		for(int i = 0; i < files.length; i++)
		{
			if((files[i].isDirectory() && files[i].getName().startsWith(".")) || files[i].isHidden())
			{
				continue;
			}

			if(files[i].isDirectory())
			{
				this.parse(files[i]);
			}
			else if(files[i].isFile())
			{
				metric.increaseParsedFiles();

				if(files[i].getName().endsWith(".php"))
				{
					if(listener != null)
					{
						listener.onScan(files[i]);
					}

					this.inspectFile(files[i]);
				}
			}
		}
	}

	protected void inspectFile(File file)
	{
		metric.increaseInspectedFiles();

		// load tokens
		Token[] tokens = this.getTokens(file);

		// add file
		com.k42b3.shu.definition.File fileDefinition = new com.k42b3.shu.definition.File();
		fileDefinition.setFile(file);
		fileDefinition.setLine(0);
		fileDefinition.setTokenCount(tokens.length);
		
		if(tokens != null)
		{
			metric.increaseTokenFiles();

			// contains the namespace of the file
			HashMap<String, String> uses = new HashMap<String, String>();
			String namespace = null;
			int level = 0;
			int classLevel = -1;
			com.k42b3.shu.definition.Class mainClass = null;

			for(int i = 2; i < tokens.length; i++)
			{
				// namespace definition
				if(tokens[i - 2].getType() == Token.T_NAMESPACE && tokens[i - 1].getType() == Token.T_WHITESPACE && tokens[i].getType() == Token.T_STRING)
				{
					StringBuilder namespaceName = new StringBuilder();
					namespaceName.append(tokens[i].getValue());

					while(tokens[i + 1].getType() == Token.T_NS_SEPARATOR && tokens[i + 2].getType() == Token.T_STRING)
					{
						namespaceName.append("\\");
						namespaceName.append(tokens[i + 2].getValue());

						i = i + 2;
					}

					namespace = namespaceName.toString();
				}
				// use definition
				else if(tokens[i - 2].getType() == Token.T_USE && tokens[i - 1].getType() == Token.T_WHITESPACE && tokens[i].getType() == Token.T_STRING)
				{
					String shortName;
					StringBuilder namespaceName = new StringBuilder();
					namespaceName.append(tokens[i].getValue());

					while(tokens[i + 1].getType() == Token.T_NS_SEPARATOR && tokens[i + 2].getType() == Token.T_STRING)
					{
						namespaceName.append("\\");
						namespaceName.append(tokens[i + 2].getValue());

						i = i + 2;
					}

					if(tokens[i + 1].getType() == Token.T_WHITESPACE)
					{
						i++;
					}

					i++;
					if(tokens[i].getType() == Token.T_AS && tokens[i + 1].getType() == Token.T_WHITESPACE && tokens[i + 2].getType() == Token.T_STRING)
					{
						shortName = tokens[i + 2].getValue();
					}
					else
					{
						int pos = namespaceName.lastIndexOf("\\");
						shortName = namespaceName.substring(pos + 1);
					}

					uses.put(shortName, namespaceName.toString());
				}
				// class definition
				else if(tokens[i - 2].getType() == Token.T_CLASS && tokens[i - 1].getType() == Token.T_WHITESPACE && tokens[i].getType() == Token.T_STRING)
				{
					int line = tokens[i - 2].getLine();
					String className = tokens[i].getValue();

					if(namespace != null)
					{
						className = namespace + "\\" + className;
					}

					com.k42b3.shu.definition.Class c = new com.k42b3.shu.definition.Class();
					c.setFile(file);
					c.setLine(line);
					c.setName(className);

					mainClass = c;
					classLevel = level;

					fileDefinition.addDefinition(c);

					index.addClass(c);

					metric.increaseClassCount();
				}
				// interface definition
				else if(tokens[i - 2].getType() == Token.T_INTERFACE && tokens[i - 1].getType() == Token.T_WHITESPACE && tokens[i].getType() == Token.T_STRING)
				{
					String interfaceName = tokens[i].getValue();
					if(namespace != null)
					{
						interfaceName = namespace + "\\" + interfaceName;
					}

					Interface c = new Interface();
					c.setFile(file);
					c.setLine(tokens[i - 2].getLine());
					c.setName(interfaceName);

					mainClass = c;
					classLevel = level;

					fileDefinition.addDefinition(c);

					index.addClass(c);

					metric.increaseInterfaceCount();
				}
				// function definition
				else if(tokens[i - 2].getType() == Token.T_FUNCTION && tokens[i - 1].getType() == Token.T_WHITESPACE && tokens[i].getType() == Token.T_STRING)
				{
					if(mainClass != null && level > classLevel)
					{
						Method m = new Method();
						m.setFile(file);
						m.setLine(tokens[i - 2].getLine());
						m.setName(tokens[i].getValue());
						m.setParent(mainClass);

						fileDefinition.addDefinition(m);

						index.addMethod(m);

						metric.increaseMethodCount();
					}
					else
					{
						Function f = new Function();
						f.setFile(file);
						f.setLine(tokens[i - 2].getLine());
						f.setName(tokens[i].getValue());
						
						fileDefinition.addDefinition(f);
						
						index.addFunction(f);

						metric.increaseFunctionCount();
					}
				}
				// exceptions or class calls
				else if(tokens[i - 2].getType() == Token.T_NEW && tokens[i - 1].getType() == Token.T_WHITESPACE && (tokens[i].getType() == Token.T_STRING || tokens[i].getType() == Token.T_NS_SEPARATOR))
				{
					int line = tokens[i - 2].getLine();
					StringBuilder className = new StringBuilder();

					if(tokens[i].getType() == Token.T_NS_SEPARATOR && tokens[i + 1].getType() == Token.T_STRING)
					{
						i++;
						className.append(tokens[i].getValue());
					}
					else if(tokens[i].getType() == Token.T_STRING)
					{
						if(tokens[i].getValue().equals("self") || tokens[i].getValue().equals("static"))
						{
							continue;
						}

						if(uses.containsKey(tokens[i].getValue()))
						{
							className.append(uses.get(tokens[i].getValue()));
						}
						else
						{
							if(namespace != null)
							{
								className.append(namespace);
								className.append("\\");
							}

							className.append(tokens[i].getValue());
						}
					}

					while(tokens[i + 1].getType() == Token.T_NS_SEPARATOR && tokens[i + 2].getType() == Token.T_STRING)
					{
						className.append("\\");
						className.append(tokens[i + 2].getValue());

						i = i + 2;
						break;
					}

					NewReference r = new NewReference();
					r.setFile(file);
					r.setLine(line);
					r.setClassName(className.toString());

					if(mainClass != null)
					{
						r.setParentClass(mainClass);
					}

					fileDefinition.addDefinition(r);
				}
				// extends
				else if(tokens[i - 2].getType() == Token.T_EXTENDS && tokens[i - 1].getType() == Token.T_WHITESPACE && (tokens[i].getType() == Token.T_STRING || tokens[i].getType() == Token.T_NS_SEPARATOR))
				{
					int line = tokens[i - 2].getLine();
					StringBuilder className = new StringBuilder();
					
					if(tokens[i].getType() == Token.T_NS_SEPARATOR && tokens[i + 1].getType() == Token.T_STRING)
					{
						i++;
						className.append(tokens[i].getValue());
					}
					else if(tokens[i].getType() == Token.T_STRING)
					{
						if(uses.containsKey(tokens[i].getValue()))
						{
							className.append(uses.get(tokens[i].getValue()));
						}
						else
						{
							if(namespace != null)
							{
								className.append(namespace);
								className.append("\\");
							}

							className.append(tokens[i].getValue());
						}
					}

					while(tokens[i + 1].getType() == Token.T_NS_SEPARATOR && tokens[i + 2].getType() == Token.T_STRING)
					{
						className.append("\\");
						className.append(tokens[i + 2].getValue());

						i = i + 2;
						break;
					}

					ExtendReference r = new ExtendReference();
					r.setFile(file);
					r.setLine(line);
					r.setClassName(className.toString());
					
					if(mainClass != null)
					{
						r.setParentClass(mainClass);
					}

					fileDefinition.addDefinition(r);
				}
				// implements
				else if(tokens[i - 2].getType() == Token.T_IMPLEMENTS && tokens[i - 1].getType() == Token.T_WHITESPACE && (tokens[i].getType() == Token.T_STRING || tokens[i].getType() == Token.T_NS_SEPARATOR))
				{
					boolean hasNext;
					int line = tokens[i - 2].getLine();

					do
					{
						hasNext = false;
						StringBuilder className = new StringBuilder();

						if(tokens[i].getType() == Token.T_STRING)
						{
							if(uses.containsKey(tokens[i].getValue()))
							{
								className.append(uses.get(tokens[i].getValue()));
							}
							else
							{
								if(namespace != null)
								{
									className.append(namespace);
									className.append("\\");
								}

								className.append(tokens[i].getValue());
							}
						}
						else if(tokens[i].getType() == Token.T_NS_SEPARATOR)
						{
							i++;
							
							className.append(tokens[i].getValue());
						}

						while(tokens[i + 1].getType() == Token.T_NS_SEPARATOR && tokens[i + 2].getType() == Token.T_STRING)
						{
							className.append("\\");
							className.append(tokens[i + 2].getValue());

							i = i + 2;
						}

						ImplementReference r = new ImplementReference();
						r.setFile(file);
						r.setLine(line);
						r.setClassName(className.toString());

						if(mainClass != null)
						{
							r.setParentClass(mainClass);
						}

						fileDefinition.addDefinition(r);
						
						
						// if the next sign is an whitespace or comma we have another interface
						if(tokens[i + 1].getType() == Token.T_WHITESPACE)
						{
							i++;
						}

						if(tokens[i + 1].getValue().equals(","))
						{
							hasNext = true;
							i++;
						}
						else
						{
							hasNext = false;
						}

						if(tokens[i + 1].getType() == Token.T_WHITESPACE)
						{
							i++;
						}
						
						if(hasNext)
						{
							i++;
						}
					}
					while(hasNext);
				}
				// instanceof
				else if(tokens[i - 2].getType() == Token.T_INSTANCEOF && tokens[i - 1].getType() == Token.T_WHITESPACE && (tokens[i].getType() == Token.T_STRING || tokens[i].getType() == Token.T_NS_SEPARATOR))
				{
					int line = tokens[i - 2].getLine();
					StringBuilder className = new StringBuilder();

					if(tokens[i].getType() == Token.T_NS_SEPARATOR && tokens[i + 1].getType() == Token.T_STRING)
					{
						i++;
						className.append(tokens[i].getValue());
					}
					else if(tokens[i].getType() == Token.T_STRING)
					{
						if(uses.containsKey(tokens[i].getValue()))
						{
							className.append(uses.get(tokens[i].getValue()));
						}
						else
						{
							if(namespace != null)
							{
								className.append(namespace);
								className.append("\\");
							}

							className.append(tokens[i].getValue());
						}
					}

					while(tokens[i + 1].getType() == Token.T_NS_SEPARATOR && tokens[i + 2].getType() == Token.T_STRING)
					{
						className.append("\\");
						className.append(tokens[i + 2].getValue());

						i = i + 2;
						break;
					}

					InstanceofReference r = new InstanceofReference();
					r.setFile(file);
					r.setLine(line);
					r.setClassName(className.toString());
					
					if(mainClass != null)
					{
						r.setParentClass(mainClass);
					}
					
					fileDefinition.addDefinition(r);
				}
				// static calls
				else if(tokens[i - 1].getType() == Token.T_STRING && tokens[i].getType() == Token.T_DOUBLE_COLON)
				{
					if(!tokens[i - 1].getValue().equals("self") && !tokens[i - 1].getValue().equals("parent"))
					{
						int line = tokens[i].getLine();
						StringBuilder className = new StringBuilder();

						int j = i;
						i--;

						LinkedList<String> parts = new LinkedList<String>();
						parts.add(tokens[i].getValue());

						while(tokens[i - 1].getType() == Token.T_NS_SEPARATOR && tokens[i - 2].getType() == Token.T_STRING)
						{
							parts.add("\\");
							parts.add(tokens[i - 2].getValue());
							
							i = i - 2;
						}

						if(tokens[i - 1].getType() != Token.T_NS_SEPARATOR)
						{
							// check uses
							if(uses.containsKey(parts.getLast()))
							{
								className = new StringBuilder(uses.get(parts.getLast()));
								Collections.reverse(parts);
								
								if(parts.size() > 1)
								{
									List<String> subParts = parts.subList(1, parts.size() - 1);

									for(int k = 0; k < subParts.size(); k++)
									{
										className.append("\\");
										className.append(subParts.get(k));
									}
								}
							}
							else
							{
								if(namespace != null)
								{
									parts.add("\\");
									parts.add(namespace);
								}
								
								Collections.reverse(parts);
								for(int k = 0; k < parts.size(); k++)
								{
									className.append(parts.get(k));
								}
							}
						}
						else
						{
							Collections.reverse(parts);
							for(int k = 0; k < parts.size(); k++)
							{
								className.append(parts.get(k));
							}
						}
						
						i = j;

						StaticReference r = new StaticReference();
						r.setFile(file);
						r.setLine(line);
						r.setClassName(className.toString());

						if(mainClass != null)
						{
							r.setParentClass(mainClass);
						}
						
						fileDefinition.addDefinition(r);
					}
				}
				// type hinting class in method
				else if(tokens[i - 2].getType() == Token.T_STRING && tokens[i - 1].getType() == Token.T_WHITESPACE && tokens[i].getType() == Token.T_VARIABLE)
				{
					int line = tokens[i].getLine();
					StringBuilder className = new StringBuilder();

					int j = i;
					i = i - 2;

					LinkedList<String> parts = new LinkedList<String>();
					parts.add(tokens[i].getValue());

					while(tokens[i - 1].getType() == Token.T_NS_SEPARATOR && tokens[i - 2].getType() == Token.T_STRING)
					{
						parts.add("\\");
						parts.add(tokens[i - 2].getValue());

						i = i - 2;
					}

					if(tokens[i - 1].getType() != Token.T_NS_SEPARATOR)
					{
						// check uses
						if(uses.containsKey(parts.getLast()))
						{
							className = new StringBuilder(uses.get(parts.getLast()));
							Collections.reverse(parts);
							
							if(parts.size() > 1)
							{
								List<String> subParts = parts.subList(1, parts.size() - 1);

								for(int k = 0; k < subParts.size(); k++)
								{
									className.append("\\");
									className.append(subParts.get(k));
								}
							}
						}
						else
						{
							if(namespace != null)
							{
								parts.add("\\");
								parts.add(namespace);
							}
							
							Collections.reverse(parts);
							for(int k = 0; k < parts.size(); k++)
							{
								className.append(parts.get(k));
							}
						}
					}
					else
					{
						Collections.reverse(parts);
						for(int k = 0; k < parts.size(); k++)
						{
							className.append(parts.get(k));
						}
					}

					i = j;

					TypeHintReference r = new TypeHintReference();
					r.setFile(file);
					r.setLine(line);
					r.setClassName(className.toString());

					if(mainClass != null)
					{
						r.setParentClass(mainClass);
					}
					
					fileDefinition.addDefinition(r);
				}
				// get reference
				else if(tokens[i].getType() == Token.T_VARIABLE && tokens[i].getValue().equals("$_GET"))
				{
					GetReference r = new GetReference();
					r.setFile(file);
					r.setLine(tokens[i].getLine());

					if(mainClass != null)
					{
						r.setParentClass(mainClass);
					}

					fileDefinition.addDefinition(r);
					
					metric.increaseGetCount();
				}
				// post reference
				else if(tokens[i].getType() == Token.T_VARIABLE && tokens[i].getValue().equals("$_POST"))
				{
					PostReference r = new PostReference();
					r.setFile(file);
					r.setLine(tokens[i].getLine());

					if(mainClass != null)
					{
						r.setParentClass(mainClass);
					}

					fileDefinition.addDefinition(r);
					
					metric.increasePostCount();
				}
				// increae decrease level
				else if(tokens[i].getType() == -1 && tokens[i].getValue().equals("{"))
				{
					level++;
				}
				else if(tokens[i].getType() == -1 && tokens[i].getValue().equals("}"))
				{
					level--;
					
					// reset class level if in class 
					if(mainClass != null && classLevel != -1 && classLevel == level)
					{
						level = 0;
						classLevel = -1;
						mainClass = null;
					}
				}
			}
		}
		else
		{
			metric.increaseNoTokenFiles();
		}

		index.addFile(fileDefinition);
	}

	protected Token[] getTokens(File file)
	{
		try
		{
			String line;
			StringBuilder response = new StringBuilder();
			Gson gson = new Gson();

			Process p = Runtime.getRuntime().exec("php -r \"echo json_encode(token_get_all(file_get_contents('" + file.getAbsolutePath() + "')));\"");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while((line = input.readLine()) != null) 
			{
				response.append(line + "\n");
			}
			input.close();

			Type collectionType = new TypeToken<Collection<Object>>(){}.getType();
			Collection<Object> result = gson.fromJson(response.toString(), collectionType);

			
			// convert to tokens
			Object[] rawTokens = result.toArray();
			Token[] tokens = new Token[rawTokens.length];

			for(int i = 0; i < rawTokens.length; i++)
			{
				Object obj = rawTokens[i];
				Token token = new Token();

				if(obj instanceof ArrayList)
				{
					ArrayList<Object> parts = (ArrayList<Object>) obj;
					token.setType((int) Float.parseFloat(parts.get(0).toString()));

					if(parts.get(1) != null)
					{
						token.setValue(parts.get(1).toString());
					}

					if(parts.get(2) != null)
					{
						token.setLine((int) Float.parseFloat(parts.get(2).toString()));
					}
				}
				else if(obj instanceof String)
				{
					token.setType(-1);
					token.setValue(obj.toString());
				}

				tokens[i] = token;
			}

			return tokens;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public interface ScanActionListener
	{
		public void onScan(File file);
	}
}
