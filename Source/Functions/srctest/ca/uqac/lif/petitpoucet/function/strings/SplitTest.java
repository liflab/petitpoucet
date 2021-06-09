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

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;

public class SplitTest
{
	@Test
	public void testSplit1()
	{
		Split s = new Split(",");
		List<?> result = (List<?>) s.evaluate("foo,bar,baz")[0];
		assertEquals(3, result.size());
		assertEquals("foo", result.get(0));
		assertEquals("bar", result.get(1));
		assertEquals("baz", result.get(2));
	}
	
	@Test
	public void testSplitExplanation1()
	{
		Split s = new Split(",");
		s.evaluate("foo,bar,baz");
		PartNode root = s.getExplanation(ComposedPart.compose(new NthElement(0), NthOutput.FIRST));
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode pn1 = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new Range(0, 2), NthInput.FIRST), pn1.getPart());
		assertEquals(s, pn1.getSubject());
	}
	
	@Test
	public void testSplitExplanation2()
	{
		Split s = new Split(",");
		s.evaluate("foo,bar,baz");
		PartNode root = s.getExplanation(ComposedPart.compose(new NthElement(1), NthOutput.FIRST));
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode pn1 = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new Range(4, 6), NthInput.FIRST), pn1.getPart());
		assertEquals(s, pn1.getSubject());
	}
	
	@Test
	public void testSplitExplanation3()
	{
		Split s = new Split("FF");
		s.evaluate("fooFFbarFFbaz");
		PartNode root = s.getExplanation(ComposedPart.compose(new NthElement(2), NthOutput.FIRST));
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode pn1 = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new Range(10, 12), NthInput.FIRST), pn1.getPart());
		assertEquals(s, pn1.getSubject());
	}
}
