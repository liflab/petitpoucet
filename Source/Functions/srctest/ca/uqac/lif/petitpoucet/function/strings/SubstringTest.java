/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2021 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.function.strings;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * Unit tests for {@link Substring}.
 */
public class SubstringTest
{
	@Test
	public void test1()
	{
		Substring f = new Substring(1, 4);
		String input = "abcdefg";
		String output = (String) f.evaluate(input)[0];
		assertEquals("bcd", output);
		PartNode root = f.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode child = (PartNode) root.getOutputLinks(0).get(0).getNode();
		Part p = child.getPart();
		assertEquals(new Range(1, 3), p.head());
	}
	
	@Test
	public void test2()
	{
		Substring f = new Substring(0, 7);
		String input = "abcdefg";
		String output = (String) f.evaluate(input)[0];
		assertEquals("abcdefg", output);
		PartNode root = f.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode child = (PartNode) root.getOutputLinks(0).get(0).getNode();
		Part p = child.getPart();
		assertEquals(NthInput.FIRST, p);
	}
	
	@Test
	public void test3()
	{
		Substring f = new Substring(2, 10);
		String input = "abcdefg";
		String output = (String) f.evaluate(input)[0];
		assertEquals("cdefg", output);
		PartNode root = f.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode child = (PartNode) root.getOutputLinks(0).get(0).getNode();
		Part p = child.getPart();
		assertEquals(new Range(2, 6), p.head());
	}
}
