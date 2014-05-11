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

package com.k42b3.shu.module;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.Element;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.k42b3.shu.Index;
import com.k42b3.shu.Metric;
import com.k42b3.shu.definition.File;

/**
 * BrowseFile
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class BrowseFile extends ModuleAbstract
{
	protected RSyntaxTextArea text;
	
	protected Index index;
	protected java.io.File selectedFile;
	
	public String getTitle()
	{
		return "Browse Files";
	}

	public String getDescription()
	{
		return "Possibility to browse all files";
	}

	public JComponent getComponent(Metric metric, Index index)
	{
		this.index = index;

		JPanel panel = new JPanel(new BorderLayout());

		// file tree
		JTree tree = new JTree(getTreeNode(index));
		tree.addMouseListener(new TreeMouseListener());
		JScrollPane scp = new JScrollPane(tree);
		scp.setPreferredSize(new Dimension(200, 100));

		panel.add(scp, BorderLayout.WEST);

		// textarea
		text = new RSyntaxTextArea();
		text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP);
		text.addParser(new PhpParser());

		RTextScrollPane textScp = new RTextScrollPane(text);
		//textScp.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		textScp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		textScp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		panel.add(textScp, BorderLayout.CENTER);
		
		return panel;
	}

	protected void readFile(java.io.File file)
	{
		selectedFile = file;
		this.text.setText("");

		try
		{
			BufferedReader reader = Files.newBufferedReader(Paths.get(file.toURI()), StandardCharsets.UTF_8);
			String line = null;
			StringBuilder text = new StringBuilder();

			while((line = reader.readLine()) != null)
			{
				text.append(line + "\n");
			}

			this.text.setText(text.toString());
			this.text.setCaretPosition(0);
		}
		catch(IOException e)
		{
		}
	}

	protected TreeNode getTreeNode(Index index)
	{
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode("Project");
		List<File> files = index.getFiles();
		
		String basePath = index.getDir().getAbsolutePath();

		for(int i = 0; i < files.size(); i++)
		{
			String path = files.get(i).getFile().getAbsolutePath();
			path = path.substring(basePath.length() + 1);

			String regex = System.getProperty("os.name").toLowerCase().indexOf("win") != -1 ? "\\\\" : "/";
			String[] parts = path.split(regex);

			DefaultMutableTreeNode node = parentNode;

			for(int j = 0; j < parts.length; j++)
			{
				// check whether node already exist
				DefaultMutableTreeNode childNode = null;
				for(int k = 0; k < node.getChildCount(); k++)
				{
					if(node.getChildAt(k).toString().equals(parts[j]))
					{
						childNode = (DefaultMutableTreeNode) node.getChildAt(k);
					}
				}

				if(childNode == null)
				{
					childNode = new DefaultMutableTreeNode(parts[j]);
					node.add(childNode);
				}

				node = childNode;
			}
		}

		return parentNode;
	}
	
	class TreeMouseListener implements MouseListener
	{
		public void mouseReleased(MouseEvent e)
		{
		}
		
		public void mousePressed(MouseEvent e)
		{
		}
		
		public void mouseExited(MouseEvent e)
		{
		}
		
		public void mouseEntered(MouseEvent e)
		{
		}
		
		public void mouseClicked(MouseEvent e)
		{
			JTree tree = (JTree) e.getSource();
			TreePath path = tree.getSelectionPath();
			
			if(path != null)
			{
				Object[] parts = path.getPath();
				
				if(parts.length > 1)
				{
					// we skip the first entry because its the Project node
					String realPath = index.getDir().getAbsolutePath();

					for(int i = 1; i < parts.length; i++)
					{
						realPath+= System.getProperty("file.separator") + parts[i].toString();
					}
					
					java.io.File file = new java.io.File(realPath);
					
					if(file.isFile())
					{
						readFile(file);
					}
				}
			}
		}
	}
	
	class PhpParser extends AbstractParser
	{
		private DefaultParseResult result;

		public PhpParser()
		{
			result = new DefaultParseResult(this);
		}

		public ParseResult parse(RSyntaxDocument doc, String style)
		{
			Element root = doc.getDefaultRootElement();
			int lineCount = root.getElementCount();

			if(style == null || SyntaxConstants.SYNTAX_STYLE_NONE.equals(style))
			{
				result.clearNotices();
				result.setParsedLines(0, lineCount - 1);
				return result;
			}

			/*
			try
			{
				File file = index.findFile(selectedFile);
				List<com.k42b3.shu.definition.Definition> defs = file.getDefinitions();
				
				for(int i = 0; i < defs.size(); i++)
				{
					if(defs.get(i) instanceof ClassReference)
					{
						ClassReference ref = (ClassReference) defs.get(i);
						
						ref.getClassName()
					}
				}
				
				String text = doc.getText(0, doc.getLength());
			}
			catch(BadLocationException e)
			{
			}
			*/

			return null;
		}
	}

	class PhpNotice extends DefaultParserNotice
	{
		public PhpNotice(Parser parser, String message, int line, int offs, int length)
		{
			super(parser, message, line, offs, length);
		}
	}
}
