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
package ca.uqac.lif.petitpoucet.function.vector;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.AndNode;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

public class FilterTest
{
	@Test
	public void test1()
	{
		List<?> list1 = VectorTestUtilities.getList(1, 2, 3, 4, 5, 6);
		List<?> list2 = VectorTestUtilities.getList(true, true, false, true, false, true);
		Filter f = new Filter();
		List<?> out_list = (List<?>) f.evaluate(list1, list2)[0];
		assertEquals(4, out_list.size());
		assertEquals(1, ((Number) out_list.get(0)).intValue());
		assertEquals(2, ((Number) out_list.get(1)).intValue());
		assertEquals(4, ((Number) out_list.get(2)).intValue());
		assertEquals(6, ((Number) out_list.get(3)).intValue());
	}

	@Test
	public void testExplain1()
	{
		List<?> list1 = VectorTestUtilities.getList(1, 2, 3, 4, 5, 6);
		List<?> list2 = VectorTestUtilities.getList(true, true, false, true, false, true);
		Filter f = new Filter();
		f.evaluate(list1, list2);
		PartNode root = f.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputArity());
		assertEquals(1, root.getOutputLinks(0).size());
		AndNode and = (AndNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(4, and.getOutputLinks(0).size());
		{
			// 1st output = 1st input
			AndNode in_and = (AndNode) and.getOutputLinks(0).get(0).getNode();
			assertEquals(2, in_and.getOutputLinks(0).size());
			PartNode pn1 = (PartNode) in_and.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new NthElement(0), NthInput.FIRST), pn1.getPart());
			PartNode pn2 = (PartNode) in_and.getOutputLinks(0).get(1).getNode();
			assertEquals(ComposedPart.compose(new NthElement(0), NthInput.SECOND), pn2.getPart());
		}
		{
			// 2nd output = 2nd input
			AndNode in_and = (AndNode) and.getOutputLinks(0).get(1).getNode();
			assertEquals(2, in_and.getOutputLinks(0).size());
			PartNode pn1 = (PartNode) in_and.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new NthElement(1), NthInput.FIRST), pn1.getPart());
			PartNode pn2 = (PartNode) in_and.getOutputLinks(0).get(1).getNode();
			assertEquals(ComposedPart.compose(new NthElement(1), NthInput.SECOND), pn2.getPart());
		}
		{
			// 3rd output = 4th input --look out!
			AndNode in_and = (AndNode) and.getOutputLinks(0).get(2).getNode();
			assertEquals(2, in_and.getOutputLinks(0).size());
			PartNode pn1 = (PartNode) in_and.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new NthElement(3), NthInput.FIRST), pn1.getPart());
			PartNode pn2 = (PartNode) in_and.getOutputLinks(0).get(1).getNode();
			assertEquals(ComposedPart.compose(new NthElement(3), NthInput.SECOND), pn2.getPart());
		}
		{
			// 4th output = 6th input
			AndNode in_and = (AndNode) and.getOutputLinks(0).get(3).getNode();
			assertEquals(2, in_and.getOutputLinks(0).size());
			PartNode pn1 = (PartNode) in_and.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new NthElement(5), NthInput.FIRST), pn1.getPart());
			PartNode pn2 = (PartNode) in_and.getOutputLinks(0).get(1).getNode();
			assertEquals(ComposedPart.compose(new NthElement(5), NthInput.SECOND), pn2.getPart());
		}
	}

	@Test
	public void testExplain2()
	{
		List<?> list1 = VectorTestUtilities.getList(1, 2, 3, 4, 5, 6);
		List<?> list2 = VectorTestUtilities.getList(true, true, false, true, false, true);
		Filter f = new Filter();
		f.evaluate(list1, list2);
		PartNode root = f.getExplanation(ComposedPart.compose(new NthElement(3), NthOutput.FIRST));
		assertEquals(1, root.getOutputArity());
		assertEquals(1, root.getOutputLinks(0).size());
		AndNode in_and = (AndNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(2, in_and.getOutputLinks(0).size());
		PartNode pn1 = (PartNode) in_and.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new NthElement(5), NthInput.FIRST), pn1.getPart());
		PartNode pn2 = (PartNode) in_and.getOutputLinks(0).get(1).getNode();
		assertEquals(ComposedPart.compose(new NthElement(5), NthInput.SECOND), pn2.getPart());
	}
}
