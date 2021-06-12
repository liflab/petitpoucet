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
package ca.uqac.lif.petitpoucet.function.ltl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;

public class NextTest
{
	@Test
	public void test1()
	{
		Next op = new Next();
		List<?> in_list = Arrays.asList(false, true, true, false, true);
		List<?> result = (List<?>) op.evaluate(in_list)[0];
		assertEquals(4, result.size());
		for (int i = 0; i < result.size(); i++)
		{
			assertEquals(in_list.get(i + 1), result.get(i));
		}
		{
			PartNode root = op.getExplanation(ComposedPart.compose(new NthElement(0), NthOutput.FIRST));
			assertEquals(1, root.getOutputLinks(0).size());
			PartNode leaf = (PartNode) root.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new NthElement(1), NthInput.FIRST), leaf.getPart());
		}
		{
			PartNode root = op.getExplanation(ComposedPart.compose(new NthElement(1), NthOutput.FIRST));
			assertEquals(1, root.getOutputLinks(0).size());
			PartNode leaf = (PartNode) root.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new NthElement(2), NthInput.FIRST), leaf.getPart());
		}
		{
			PartNode root = op.getExplanation(ComposedPart.compose(new NthElement(2), NthOutput.FIRST));
			assertEquals(1, root.getOutputLinks(0).size());
			PartNode leaf = (PartNode) root.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new NthElement(3), NthInput.FIRST), leaf.getPart());
		}
	}
}
