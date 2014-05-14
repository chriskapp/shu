package com.k42b3.shu.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.k42b3.shu.definition.Function;
import com.k42b3.shu.definition.Interface;
import com.k42b3.shu.definition.Method;
import com.k42b3.shu.definition.Parameters;
import com.k42b3.shu.reference.ExtendReference;
import com.k42b3.shu.reference.ImplementReference;
import com.k42b3.shu.reference.InstanceofReference;
import com.k42b3.shu.reference.NewReference;
import com.k42b3.shu.reference.StaticReference;
import com.k42b3.shu.reference.TypeHintReference;
import com.k42b3.shu.reference.VariableReference;

public class PhpProcessor extends ProcessorAbstract
{
	protected Process proccess;
	protected BufferedWriter bufferedWriter;
	protected BufferedReader bufferedReader;

	public void start() throws Exception
	{
		if(!this.isPhpProcessorAvailable())
		{
			throw new RuntimeException("No PHP processor was found");
		}

		String phpCode = "";
		phpCode+= "while (false !== ($line = fgets(STDIN))) {";
		phpCode+= " echo json_encode(token_get_all(file_get_contents(trim($line)))) . PHP_EOL;";
		phpCode+= "}";

		proccess = Runtime.getRuntime().exec("php -r \"" + phpCode + "\"");
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(proccess.getOutputStream()));
		bufferedReader = new BufferedReader(new InputStreamReader(proccess.getInputStream()));
	}

	public void close() throws Exception
	{
		bufferedWriter.close();
		bufferedReader.close();
		proccess.destroy();
	}

	public void process(File file)
	{
		Token[] tokens = this.getTokens(file);

		// add file
		com.k42b3.shu.definition.File fileDefinition = new com.k42b3.shu.definition.File();
		fileDefinition.setFile(file);
		fileDefinition.setLine(0);
		fileDefinition.setTokenCount(tokens.length);

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

				this.listener.onDefinition(c);
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

				this.listener.onDefinition(c);
			}
			// function definition
			else if(tokens[i - 2].getType() == Token.T_FUNCTION && tokens[i - 1].getType() == Token.T_WHITESPACE && tokens[i].getType() == Token.T_STRING)
			{
				int line = tokens[i - 2].getLine();
				String name = tokens[i].getValue();
				
				Parameters params = new Parameters();

				i++;
				
				if(tokens[i].getType() == Token.T_WHITESPACE)
				{
					i++;
				}

				if(tokens[i].getValue().equals("("))
				{
					i++;
				}

				if(tokens[i].getType() == Token.T_WHITESPACE)
				{
					i++;
				}

				boolean hasNext;

				do
				{
					hasNext = false;
					String parameterName = null;
					StringBuilder typeHint = new StringBuilder();
					boolean isRequired = true;

					// get type hint
					if(tokens[i].getType() == Token.T_STRING)
					{
						if(uses.containsKey(tokens[i].getValue()))
						{
							typeHint.append(uses.get(tokens[i].getValue()));
						}
						else
						{
							if(namespace != null)
							{
								typeHint.append(namespace);
								typeHint.append("\\");
							}

							typeHint.append(tokens[i].getValue());
						}
					}
					else if(tokens[i].getType() == Token.T_NS_SEPARATOR)
					{
						i++;

						typeHint.append(tokens[i].getValue());
					}
					else if(tokens[i].getType() == Token.T_ARRAY)
					{
						typeHint.append("array");
					}
					else
					{
						i--;
					}

					while(tokens[i + 1].getType() == Token.T_NS_SEPARATOR && tokens[i + 2].getType() == Token.T_STRING)
					{
						typeHint.append("\\");
						typeHint.append(tokens[i + 2].getValue());

						i = i + 2;
					}

					i++;

					if(tokens[i].getType() == Token.T_WHITESPACE)
					{
						i++;
					}

					// variable name
					if(tokens[i].getType() == Token.T_VARIABLE)
					{
						parameterName = tokens[i].getValue();
					}
					
					// check default value
					if(tokens[i + 1].getValue().equals("=") || tokens[i + 1].getType() == Token.T_WHITESPACE && tokens[i + 2].getValue().equals("="))
					{
						isRequired = false;
					}

					// we go to the next , or ) token
					while(!tokens[i].getValue().equals(",") && !tokens[i].getValue().equals(")") && i < tokens.length)
					{
						i++;
					}

					if(tokens[i].getValue().equals(","))
					{
						hasNext = true;
						i++;
					}

					if(tokens[i].getType() == Token.T_WHITESPACE)
					{
						i++;
					}

					if(parameterName != null)
					{
						params.add(parameterName, typeHint.toString().isEmpty() ? null : typeHint.toString(), isRequired);
					}
				}
				while(hasNext);


				if(mainClass != null && level > classLevel)
				{
					Method m = new Method();
					m.setFile(file);
					m.setLine(line);
					m.setName(name);
					//m.setParent(mainClass);
					m.setParameters(params);

					fileDefinition.addDefinition(m);

					mainClass.addMethod(m);
					
					this.listener.onDefinition(m);
				}
				else
				{
					Function f = new Function();
					f.setFile(file);
					f.setLine(line);
					f.setName(name);
					f.setParameters(params);

					fileDefinition.addDefinition(f);
					
					this.listener.onDefinition(f);
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
				
				this.listener.onDefinition(r);
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
				
				this.listener.onDefinition(r);
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

					this.listener.onDefinition(r);
					
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
				
				this.listener.onDefinition(r);
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
					
					this.listener.onDefinition(r);
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
				
				this.listener.onDefinition(r);
			}
			// variable
			else if(tokens[i].getType() == Token.T_VARIABLE)
			{
				/*
				VariableReference r = new VariableReference();
				r.setName(tokens[i].getValue());
				r.setFile(file);
				r.setLine(tokens[i].getLine());

				if(mainClass != null)
				{
					r.setParentClass(mainClass);
				}

				fileDefinition.addDefinition(r);

				this.listener.onDefinition(r);
				*/
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

		listener.onDefinition(fileDefinition);
	}
	

	protected Token[] getTokens(File file)
	{
		try
		{
			Gson gson = new Gson();

			bufferedWriter.write(file.getAbsolutePath() + "\n");
			bufferedWriter.flush();

			String response = bufferedReader.readLine();

			Type collectionType = new TypeToken<Collection<Object>>(){}.getType();
			Collection<Object> result = gson.fromJson(response, collectionType);
			
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

	protected boolean isPhpProcessorAvailable()
	{
		try
		{
			Process p = Runtime.getRuntime().exec("php -v");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = input.readLine();
			input.close();
			
			// line contains the version string we could check for the "PHP" 
			// string but maybe this works also with hhvm
			if(line.length() == 0)
			{
				return false;
			}

			return true;
		}
		catch (IOException e)
		{
		}

		return false;
	}
}
