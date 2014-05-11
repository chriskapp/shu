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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.k42b3.shu.definition.Definition;

/**
 * ScannerTest
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class ScannerTest
{
	@Test
	public void testScan()
	{
		Scanner scanner = new Scanner();
		scanner.scan(new File(getClass().getResource("/test_files/class.php").getFile()));

		com.k42b3.shu.definition.File file = scanner.getIndex().getFiles().get(0);
		List<Definition> defs = file.getDefinitions();
		
		assertEquals("Foo\\Bar\\Test", ((com.k42b3.shu.definition.Class) defs.get(0)).getName());
		assertEquals("Foo\\Bar\\Foo", ((com.k42b3.shu.reference.ExtendReference) defs.get(1)).getClassName());
		assertEquals("Foo\\Bar\\Bar", ((com.k42b3.shu.reference.ImplementReference) defs.get(2)).getClassName());
		assertEquals("__construct", ((com.k42b3.shu.definition.Method) defs.get(3)).getName());
		assertEquals("Foo\\Bar\\Foo", ((com.k42b3.shu.reference.StaticReference) defs.get(4)).getClassName());
		assertEquals("Foo\\Bar\\Bar\\Foo", ((com.k42b3.shu.reference.StaticReference) defs.get(5)).getClassName());
		assertEquals("Bar\\Foo", ((com.k42b3.shu.reference.StaticReference) defs.get(6)).getClassName());
		assertEquals("Blub\\Bla", ((com.k42b3.shu.reference.StaticReference) defs.get(7)).getClassName());
		assertEquals("Blub\\Foo", ((com.k42b3.shu.reference.StaticReference) defs.get(8)).getClassName());
		assertEquals("Foo\\Bar\\Foo", ((com.k42b3.shu.reference.NewReference) defs.get(9)).getClassName());
		assertEquals("Foo\\Bar\\Bar\\Foo", ((com.k42b3.shu.reference.NewReference) defs.get(10)).getClassName());
		assertEquals("Bar\\Foo", ((com.k42b3.shu.reference.NewReference) defs.get(11)).getClassName());
		assertEquals("Blub\\Bla", ((com.k42b3.shu.reference.NewReference) defs.get(12)).getClassName());
		assertEquals("Blub\\Foo", ((com.k42b3.shu.reference.NewReference) defs.get(13)).getClassName());
		assertEquals("Foo\\Bar\\Foo", ((com.k42b3.shu.reference.TypeHintReference) defs.get(14)).getClassName());
		assertEquals("Foo\\Bar\\Bar\\Foo", ((com.k42b3.shu.reference.TypeHintReference) defs.get(15)).getClassName());
		assertEquals("Bar\\Foo", ((com.k42b3.shu.reference.TypeHintReference) defs.get(16)).getClassName());
		assertEquals("Blub\\Bla", ((com.k42b3.shu.reference.TypeHintReference) defs.get(17)).getClassName());
		assertEquals("Blub\\Foo", ((com.k42b3.shu.reference.TypeHintReference) defs.get(18)).getClassName());
		assertEquals("$x", ((com.k42b3.shu.reference.VariableReference) defs.get(19)).getName());
		assertEquals("Foo\\Bar\\Foo", ((com.k42b3.shu.reference.InstanceofReference) defs.get(20)).getClassName());
		assertEquals("$x", ((com.k42b3.shu.reference.VariableReference) defs.get(21)).getName());
		assertEquals("Foo\\Bar\\Bar\\Foo", ((com.k42b3.shu.reference.InstanceofReference) defs.get(22)).getClassName());
		assertEquals("$x", ((com.k42b3.shu.reference.VariableReference) defs.get(23)).getName());
		assertEquals("Bar\\Foo", ((com.k42b3.shu.reference.InstanceofReference) defs.get(24)).getClassName());
		assertEquals("$x", ((com.k42b3.shu.reference.VariableReference) defs.get(25)).getName());
		assertEquals("Blub\\Bla", ((com.k42b3.shu.reference.InstanceofReference) defs.get(26)).getClassName());
		assertEquals("$x", ((com.k42b3.shu.reference.VariableReference) defs.get(27)).getName());
		assertEquals("Blub\\Foo", ((com.k42b3.shu.reference.InstanceofReference) defs.get(28)).getClassName());

		assertEquals("Foo\\Bar\\Bar", ((com.k42b3.shu.definition.Class) defs.get(29)).getName());
		assertEquals("Bar\\Foo", ((com.k42b3.shu.reference.ExtendReference) defs.get(30)).getClassName());
		assertEquals("Foo\\Bar\\Foo", ((com.k42b3.shu.reference.ImplementReference) defs.get(31)).getClassName());
		assertEquals("Foo\\Bar\\Bar\\Foo", ((com.k42b3.shu.reference.ImplementReference) defs.get(32)).getClassName());
		assertEquals("Bar\\Foo", ((com.k42b3.shu.reference.ImplementReference) defs.get(33)).getClassName());
		assertEquals("Blub\\Bla", ((com.k42b3.shu.reference.ImplementReference) defs.get(34)).getClassName());
		assertEquals("Blub\\Foo", ((com.k42b3.shu.reference.ImplementReference) defs.get(35)).getClassName());

		assertEquals("Foo\\Bar\\Foo", ((com.k42b3.shu.definition.Class) defs.get(36)).getName());
		assertEquals("Foo\\Bar\\Bar\\Foo", ((com.k42b3.shu.reference.ExtendReference) defs.get(37)).getClassName());

		assertEquals("Foo\\Bar\\Foo", ((com.k42b3.shu.definition.Class) defs.get(38)).getName());
		
		assertEquals("Foo\\Bar\\Target", ((com.k42b3.shu.definition.Class) defs.get(39)).getName());

		assertEquals("Foo\\Bar\\Blub", ((com.k42b3.shu.definition.Class) defs.get(40)).getName());
		assertEquals("Foo\\Bar\\Blub", ((com.k42b3.shu.definition.Method) defs.get(41)).getParent().getName());
		assertEquals("foo", ((com.k42b3.shu.definition.Method) defs.get(41)).getName());
		assertEquals("bar1", ((com.k42b3.shu.definition.Function) defs.get(42)).getName());
	}
	
	@Test
	public void testScanFinal()
	{
		Scanner scanner = new Scanner();
		scanner.scan(new File(getClass().getResource("/test_files/class-final.php").getFile()));

		com.k42b3.shu.definition.File file = scanner.getIndex().getFiles().get(0);
		List<Definition> defs = file.getDefinitions();
		
		assertEquals("Doctrine\\Common\\Annotations\\Annotation\\Target", ((com.k42b3.shu.definition.Class) defs.get(0)).getName());
	}
	
	@Test
	public void testScanFunctionParameters()
	{
		Scanner scanner = new Scanner();
		scanner.scan(new File(getClass().getResource("/test_files/function-parameters.php").getFile()));

		com.k42b3.shu.definition.File file = scanner.getIndex().getFiles().get(0);
		List<Definition> defs = file.getDefinitions();
		
		assertEquals("foo", ((com.k42b3.shu.definition.Function) defs.get(0)).getName());
		assertEquals("$fooarg1", ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(0).getName());
		assertEquals(null, ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(0).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(0).isRequired());
		assertEquals("$fooarg2", ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(1).getName());
		assertEquals("array", ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(1).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(1).isRequired());
		assertEquals("$fooarg3", ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(2).getName());
		assertEquals("Foo", ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(2).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(2).isRequired());
		assertEquals("$fooarg4", ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(3).getName());
		assertEquals("Bar\\Foo", ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(3).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(3).isRequired());
		assertEquals("$fooarg5", ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(4).getName());
		assertEquals("Bar\\Foo", ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(4).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(0)).getParameters().get(4).isRequired());
		
		assertEquals("bar", ((com.k42b3.shu.definition.Function) defs.get(1)).getName());
		assertEquals("$bararg1", ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(0).getName());
		assertEquals(null, ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(0).getTypeHint());
		assertEquals(false, ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(0).isRequired());
		assertEquals("$bararg2", ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(1).getName());
		assertEquals("array", ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(1).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(1).isRequired());
		assertEquals("$bararg3", ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(2).getName());
		assertEquals("Foo", ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(2).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(2).isRequired());
		assertEquals("$bararg4", ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(3).getName());
		assertEquals("Bar\\Foo", ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(3).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(3).isRequired());
		assertEquals("$bararg5", ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(4).getName());
		assertEquals("Bar\\Foo", ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(4).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(1)).getParameters().get(4).isRequired());

		assertEquals("rev", ((com.k42b3.shu.definition.Function) defs.get(2)).getName());
		assertEquals("$revarg1", ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(0).getName());
		assertEquals(null, ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(0).getTypeHint());
		assertEquals(false, ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(0).isRequired());
		assertEquals("$revarg2", ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(1).getName());
		assertEquals("array", ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(1).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(1).isRequired());
		assertEquals("$revarg3", ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(2).getName());
		assertEquals("Foo", ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(2).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(2).isRequired());
		assertEquals("$revarg4", ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(3).getName());
		assertEquals("Bar\\Foo", ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(3).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(3).isRequired());
		assertEquals("$revarg5", ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(4).getName());
		assertEquals("Bar\\Foo", ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(4).getTypeHint());
		assertEquals(true, ((com.k42b3.shu.definition.Function) defs.get(2)).getParameters().get(4).isRequired());
	}
}

