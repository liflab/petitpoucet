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

import org.junit.Test;

import ca.uqac.lif.petitpoucet.function.number.Addition;
import ca.uqac.lif.petitpoucet.function.number.Subtraction;

public class CircuitTest
{
	@Test
	public void test1()
	{
		Circuit c = new Circuit(2, 1);
		Addition a = new Addition(2);
		c.addNodes(a);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateOutput(0, a.getOutputPin(0));
		Object[] out = c.evaluate(2, 3);
		assertEquals(5, ((Number) out[0]).intValue());
	}
	
	@Test
	public void test2()
	{
		Circuit c = new Circuit(2, 1);
		Subtraction a = new Subtraction(2);
		c.addNodes(a);
		c.associateInput(0, a.getInputPin(0));
		c.associateInput(1, a.getInputPin(1));
		c.associateOutput(0, a.getOutputPin(0));
		Object[] out = c.evaluate(2, 3);
		assertEquals(-1, ((Number) out[0]).intValue());
	}
	
	@Test
	public void test3()
	{
		Circuit c = new Circuit(2, 1);
		Subtraction a = new Subtraction(2);
		c.addNodes(a);
		c.associateInput(0, a.getInputPin(1));
		c.associateInput(1, a.getInputPin(0));
		c.associateOutput(0, a.getOutputPin(0));
		Object[] out = c.evaluate(2, 3);
		assertEquals(1, ((Number) out[0]).intValue());
	}
}
