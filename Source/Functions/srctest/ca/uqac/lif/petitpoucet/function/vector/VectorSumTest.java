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

import org.junit.Test;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

import static ca.uqac.lif.petitpoucet.function.vector.VectorTestUtilities.getList;
import static org.junit.Assert.*;

import java.util.List;

public class VectorSumTest
{
	@Test
	public void test1()
	{
		List<?> in_list = getList(1, 2, 3);
		VectorSum f = new VectorSum();
		Number n = (Number) f.evaluate(in_list)[0];
		assertEquals(6, n.intValue());
	}
	
	@Test
	public void testExplain1()
	{
		List<?> in_list = getList(1, 2, 3);
		VectorSum f = new VectorSum();
		f.evaluate(in_list);
		Node root = f.getExplanation(NthOutput.FIRST);
		assertTrue(root instanceof PartNode);
		assertEquals(1, root.getOutputLinks(0).size());
		Pin<?> pin = root.getOutputLinks(0).get(0);
		PartNode pn = (PartNode) pin.getNode();
		assertEquals(pn.getPart(), NthInput.FIRST);
		assertEquals(f, pn.getSubject());
	}
}
