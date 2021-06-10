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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

public class VectorProductTest
{
	@Test
	public void testProduct1()
	{
		VectorProduct add = new VectorProduct();
		Number out = (Number) add.evaluate(VectorTestUtilities.getList(1, 2, 3))[0];
		assertEquals(6, out.intValue());
	}
	
	@Test
	public void testProductLineage1()
	{
		VectorProduct add = new VectorProduct();
		add.evaluate(VectorTestUtilities.getList(1, 2, 3));
		Node root = add.getExplanation(new NthOutput(0));
		assertTrue(root instanceof PartNode);
		List<Pin<? extends Node>> links = root.getOutputLinks(0);
		assertEquals(1, links.size());
		PartNode pn = (PartNode) links.get(0).getNode();
		assertEquals(ComposedPart.compose(NthInput.FIRST), pn.getPart());
		assertEquals(add, pn.getSubject());
	}
	
	@Test
	public void testProductLineage2()
	{
		VectorProduct add = new VectorProduct();
		add.evaluate(VectorTestUtilities.getList(5, 0, 6));
		Node root = add.getExplanation(new NthOutput(0));
		assertTrue(root instanceof PartNode);
		List<Pin<? extends Node>> links = root.getOutputLinks(0);
		assertEquals(1, links.size());
		Node n = links.get(0).getNode();
		assertTrue(n instanceof PartNode);
		PartNode pn = (PartNode) n;
		Part d = pn.getPart();
		assertTrue(d.equals(ComposedPart.compose(new NthElement(1), NthInput.FIRST)));
	}
}
