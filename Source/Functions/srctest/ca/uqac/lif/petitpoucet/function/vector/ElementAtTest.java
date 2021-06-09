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

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.FunctionException;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

public class ElementAtTest
{
	@Test
	public void test1()
	{
		List<?> in_list = VectorTestUtilities.getList(3, 1, 4, 1, 6);
		ElementAt ea = new ElementAt(2);
		Object[] out = ea.evaluate(in_list);
		assertEquals(4, ((Number) out[0]).intValue());
	}
	
	@Test(expected = FunctionException.class)
	public void test2()
	{
		List<?> in_list = VectorTestUtilities.getList(3, 1, 4, 1, 6);
		ElementAt ea = new ElementAt(10);
		ea.evaluate(in_list);
	}
	
	@Test(expected = FunctionException.class)
	public void test3()
	{
		List<?> in_list = VectorTestUtilities.getList(3, 1, 4, 1, 6);
		ElementAt ea = new ElementAt(-1);
		ea.evaluate(in_list);
	}
	
	@Test
	public void testExplanation1()
	{
		List<?> in_list = VectorTestUtilities.getList(3, 1, 4, 1, 6);
		ElementAt ea = new ElementAt(2);
		ea.evaluate(in_list);
		PartNode root = ea.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputArity());
		Pin<? extends Node> pin = root.getOutputLinks(0).get(0);
		PartNode leaf = (PartNode) pin.getNode();
		assertEquals(ComposedPart.compose(new NthElement(2), NthInput.FIRST), leaf.getPart());
	}
}
