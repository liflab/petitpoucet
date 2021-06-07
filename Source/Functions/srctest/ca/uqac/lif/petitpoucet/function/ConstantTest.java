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
package ca.uqac.lif.petitpoucet.function;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;

import static ca.uqac.lif.petitpoucet.function.vector.VectorTestUtilities.getList;

public class ConstantTest
{
	@Test
	public void test1()
	{
		Constant c = new Constant("foo");
		Object[] out = c.evaluate();
		assertEquals("foo", out[0]);
	}
	
	@Test
	public void testExplain1()
	{
		Constant c = new Constant("foo");
		c.evaluate();
		PartNode node = c.getExplanation(NthOutput.FIRST);
		assertEquals(1, node.getOutputLinks(0).size());
		Pin<?> pin = node.getOutputLinks(0).get(0);
		PartNode out_node = (PartNode) pin.getNode();
		assertEquals(Part.all, out_node.getPart());
		assertEquals("foo", out_node.getSubject());
	}
	
	@Test
	public void testExplain2()
	{
		List<?> in_list = getList("foo", "bar");
		Constant c = new Constant(in_list);
		c.evaluate();
		PartNode node = c.getExplanation(ComposedPart.create(new NthElement(1), NthOutput.FIRST));
		assertEquals(1, node.getOutputLinks(0).size());
		Pin<?> pin = node.getOutputLinks(0).get(0);
		PartNode out_node = (PartNode) pin.getNode();
		assertEquals(ComposedPart.create(new NthElement(1), Part.all), out_node.getPart());
		assertEquals(in_list, out_node.getSubject());
	}
}
