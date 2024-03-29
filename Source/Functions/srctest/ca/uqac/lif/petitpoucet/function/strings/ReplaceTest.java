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

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.strings.RangeMapping.RangePair;

/**
 * Unit tests for {@link Replace}.
 */
public class ReplaceTest
{
	protected static final String CRLF = System.getProperty("line.separator");
	protected static final int CRLF_S = CRLF.length();
	
	@Test
	public void test1()
	{
		Replace f = new Replace("foo", "bar");
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
	public void test2()
	{
		Replace f = new Replace("foo", "bar");
		String input = "abcfoog";
		String output = (String) f.evaluate(input)[0];
		assertEquals("abcbarg", output);
		assertEquals(new RangeMapping(
				new RangePair(0, 2, 0, 2),
				new RangePair(3, 5, 3, 5, false),
				new RangePair(6, 6, 6, 6)
				), 
				f.getMapping());
	}
	
	@Test
	public void test3()
	{
		Replace f = new Replace("foo", "barbaz");
		String input = "abcfoog";
		String output = (String) f.evaluate(input)[0];
		assertEquals("abcbarbazg", output);
		PartNode root = f.getExplanation(ComposedPart.compose(new Range(0, 2), NthOutput.FIRST));
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode child = (PartNode) root.getOutputLinks(0).get(0).getNode();
		Part p = child.getPart();
		assertEquals(new Range(0, 2), p.head());
	}
	
	@Test
	public void test4()
	{
		Replace f = new Replace("foo", "barbaz");
		String input = "abcfoog";
		String output = (String) f.evaluate(input)[0];
		assertEquals("abcbarbazg", output);
		PartNode root = f.getExplanation(ComposedPart.compose(new Range(3, 8), NthOutput.FIRST));
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode child = (PartNode) root.getOutputLinks(0).get(0).getNode();
		Part p = child.getPart();
		assertEquals(new Range(3, 5), p.head());
	}
	
	@Test
	public void test5()
	{
		Replace f = new Replace("foo", "barbaz");
		String input = "abcfoog";
		String output = (String) f.evaluate(input)[0];
		assertEquals("abcbarbazg", output);
		PartNode root = f.getExplanation(ComposedPart.compose(new Range(2, 3), NthOutput.FIRST));
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode child = (PartNode) root.getOutputLinks(0).get(0).getNode();
		Part p = child.getPart();
		assertEquals(new Range(2, 5), p.head());
	}
	
	@Test
	public void testMapping1()
	{
		Replace f = new Replace("foo", "barbaz");
		String input = "abcfoog";
		String output = (String) f.evaluate(input)[0];
		assertEquals("abcbarbazg", output);
		assertEquals(new RangeMapping(
				new RangePair(0, 2, 0, 2),
				new RangePair(3, 5, 3, 8, false),
				new RangePair(6, 6, 9, 9)
				), f.getMapping());
	}
	
	@Test
	public void testMapping2()
	{
		Replace f = new Replace("foo", "");
		String input = "abcfoog";
		String output = (String) f.evaluate(input)[0];
		assertEquals("abcg", output);
		assertEquals(new RangeMapping(
				new RangePair(0, 2, 0, 2),
				new RangePair(6, 6, 3, 3)
				), f.getMapping());
	}
	
	@Test
	public void testMapping3()
	{
		Replace f = new Replace("([a-c]+)", "Z$1Z");
		String input = "abcfoog";
		String output = (String) f.evaluate(input)[0];
		assertEquals("ZabcZfoog", output);
		assertEquals(new RangeMapping(
				new RangePair(0, 2, 0, 0, false),
				new RangePair(0, 2, 1, 3),
				new RangePair(0, 2, 4, 4, false),
				new RangePair(3, 6, 5, 8)
				), f.getMapping());
	}
	
	@Test
	public void testMapping4()
	{
		Replace f = new Replace("abc" + CRLF, "");
		String input = "abc" + CRLF + "def";
		String output = (String) f.evaluate(input)[0];
		assertEquals("def", output);
		assertEquals(new RangeMapping(
				new RangePair(3 + CRLF_S, 5 + CRLF_S, 0, 2)
				), f.getMapping());
	}
	
	@Test
	public void testMapping5()
	{
		Replace f = new Replace("(brown|lazy) (fox|dog)", "$2 $1");
		String input  = "The quick brown fox.";
		String output = (String) f.evaluate(input)[0];
		assertEquals("The quick fox brown.", output);
		RangeMapping expected = new RangeMapping(
				new RangePair(0, 9, 0, 9), // OK
				new RangePair(16, 18, 10, 12), // 16-24 au lieu de 16-18
				new RangePair(10, 18, 13, 13, true), // OK
				new RangePair(10, 14, 14, 18), // OK
				new RangePair(19, 19, 19, 19) // OK
				).sort();
		assertEquals(expected, f.getMapping());
	}
}
