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
package ca.uqac.lif.petitpoucet.function.vector;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.AndNode;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.Equals;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * Unit tests for {@link VectorEquals}.
 */
public class VectorEqualsTest
{
	@Test
	public void test1()
	{
		List<Integer> list1 = Arrays.asList(1, 2, 3);
		List<Integer> list2 = Arrays.asList(1, 2, 4);
		VectorEquals eq = new VectorEquals();
		boolean b = (Boolean) eq.evaluate(list1, list2)[0];
		assertEquals(false, b);
		PartNode root = eq.getExplanation(NthOutput.FIRST);
		PartNode child_1 = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertTrue(child_1.getSubject() instanceof Equals);
		AndNode child_2 = (AndNode) child_1.getOutputLinks(0).get(0).getNode();
		assertEquals(2, child_2.getOutputLinks(0).size());
		{
			PartNode child_3 = (PartNode) child_2.getOutputLinks(0).get(0).getNode();
			assertEquals(NthInput.FIRST, child_3.getPart());
			PartNode child_4 = (PartNode) child_3.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new NthElement(2), NthInput.FIRST), child_4.getPart());
			assertEquals(eq, child_4.getSubject());
		}
		{
			PartNode child_3 = (PartNode) child_2.getOutputLinks(0).get(1).getNode();
			assertEquals(NthInput.SECOND, child_3.getPart());
			PartNode child_4 = (PartNode) child_3.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new NthElement(2), NthInput.SECOND), child_4.getPart());
			assertEquals(eq, child_4.getSubject());
		}
	}
	
	@Test
	public void test2()
	{
		List<Integer> list1 = Arrays.asList(1, 2, 3);
		List<Integer> list2 = Arrays.asList(1, 2, 3, 4);
		VectorEquals eq = new VectorEquals();
		boolean b = (Boolean) eq.evaluate(list1, list2)[0];
		assertEquals(false, b);
		PartNode root = eq.getExplanation(NthOutput.FIRST);
		PartNode child_1 = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new NthElement(3), NthInput.SECOND), child_1.getPart());
		assertEquals(eq, child_1.getSubject());
	}
}
