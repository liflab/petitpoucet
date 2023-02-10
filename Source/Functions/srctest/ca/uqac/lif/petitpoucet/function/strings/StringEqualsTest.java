/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2023 Sylvain Hallé

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

import ca.uqac.lif.petitpoucet.AndNode;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.OrNode;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * Unit tests for {@link StringEquals}.
 */
public class StringEqualsTest
{
	@Test
	public void test1()
	{
		StringEquals eq = new StringEquals();
		boolean b = (Boolean) eq.evaluate("abcdefghi", "abCdeFGHi")[0];
		assertFalse(b);
		PartNode root = eq.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		OrNode or = (OrNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(2, or.getOutputLinks(0).size());
		{
			AndNode in_and = (AndNode) or.getOutputLinks(0).get(0).getNode();
			PartNode pn_1 = (PartNode) in_and.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new Range(2, 2), NthInput.FIRST), pn_1.getPart());
			PartNode pn_2 = (PartNode) in_and.getOutputLinks(0).get(1).getNode();
			assertEquals(ComposedPart.compose(new Range(2, 2), NthInput.SECOND), pn_2.getPart());
		}
		{
			AndNode in_and = (AndNode) or.getOutputLinks(0).get(1).getNode();
			PartNode pn_1 = (PartNode) in_and.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new Range(5, 7), NthInput.FIRST), pn_1.getPart());
			PartNode pn_2 = (PartNode) in_and.getOutputLinks(0).get(1).getNode();
			assertEquals(ComposedPart.compose(new Range(5, 7), NthInput.SECOND), pn_2.getPart());
		}
	}
	
	@Test
	public void test2()
	{
		StringEquals eq = new StringEquals();
		boolean b = (Boolean) eq.evaluate("abcdefghi", "abc")[0];
		assertFalse(b);
		PartNode root = eq.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode pn = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new Range(3, 8), NthInput.FIRST), pn.getPart());
	}
	
	@Test
	public void test3()
	{
		StringEquals eq = new StringEquals();
		boolean b = (Boolean) eq.evaluate("abc", "abcdefghi")[0];
		assertFalse(b);
		PartNode root = eq.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode pn = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new Range(3, 8), NthInput.SECOND), pn.getPart());
	}
	
	@Test
	public void test4()
	{
		StringEquals eq = new StringEquals();
		boolean b = (Boolean) eq.evaluate("abcdefghi", "abcdeFGHijkl")[0];
		assertFalse(b);
		PartNode root = eq.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		OrNode or = (OrNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(2, or.getOutputLinks(0).size());
		{
			AndNode in_and = (AndNode) or.getOutputLinks(0).get(0).getNode();
			PartNode pn_1 = (PartNode) in_and.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new Range(5, 7), NthInput.FIRST), pn_1.getPart());
			PartNode pn_2 = (PartNode) in_and.getOutputLinks(0).get(1).getNode();
			assertEquals(ComposedPart.compose(new Range(5, 7), NthInput.SECOND), pn_2.getPart());
		}
		{
			PartNode pn_1 = (PartNode) or.getOutputLinks(0).get(1).getNode();
			assertEquals(ComposedPart.compose(new Range(9, 11), NthInput.SECOND), pn_1.getPart());
		}
	}
}
