/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2022 Sylvain Hallé

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

/**
 * Unit tests for {@link AsList}.
 */
public class AsListTest
{
	@Test
	public void test1()
	{
		AsList f = new AsList(3);
		List<?> list = (List<?>) f.evaluate("foo", 0, false)[0];
		assertEquals(3, list.size());
		assertEquals("foo", list.get(0));
		assertEquals(0, list.get(1));
		assertEquals(false, list.get(2));
		PartNode root = f.getExplanation(ComposedPart.compose(new NthElement(1), NthOutput.FIRST));
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode child = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new NthElement(1), NthInput.FIRST), child.getPart());
	}

	@Test
	public void test2()
	{
		AsList f = new AsList(3);
		f.evaluate("foo", 0, false);
		PartNode root = f.getExplanation(NthOutput.FIRST);
		AndNode and = (AndNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(3, and.getOutputLinks(0).size());
		{
			PartNode child = (PartNode) and.getOutputLinks(0).get(0).getNode();
			assertEquals(new NthInput(0), child.getPart());
		}
		{
			PartNode child = (PartNode) and.getOutputLinks(0).get(1).getNode();
			assertEquals(new NthInput(1), child.getPart());
		}
		{
			PartNode child = (PartNode) and.getOutputLinks(0).get(2).getNode();
			assertEquals(new NthInput(2), child.getPart());
		}
	}
}
