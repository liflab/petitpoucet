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
package ca.uqac.lif.petitpoucet.function.number;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.petitpoucet.AndNode;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.number.Addition;
import ca.uqac.lif.petitpoucet.function.number.Multiplication;

public class NumbersTest
{
	@Test
	public void testAddition1()
	{
		Addition add = new Addition(3);
		Object[] out = new Object[1];
		out = add.evaluate(1, 2, 3);
		assertEquals(6, ((Number) out[0]).intValue());
	}
	
	@Test
	public void testAdditionLineage1()
	{
		Addition add = new Addition(3);
		add.evaluate(1, 2, 3);
		Node root = add.getExplanation(new NthOutput(0));
		assertTrue(root instanceof PartNode);
		List<Pin<? extends Node>> links = root.getOutputLinks(0);
		assertEquals(1, links.size());
		Node and = links.get(0).getNode();
		assertTrue(and instanceof AndNode);
		links = and.getOutputLinks(0);
		assertEquals(3, links.size());
		for (int i = 0; i < links.size(); i++)
		{
			Node n = links.get(i).getNode();
			assertTrue(n instanceof PartNode);
			PartNode pn = (PartNode) n;
			Part d = pn.getPart();
			assertTrue(d.equals(new NthInput(i)));
		}
	}
	
	@Test
	public void testMultiplication1()
	{
		Multiplication add = new Multiplication(3);
		Object[] out = new Object[1];
		out = add.evaluate(4, 5, 6);
		assertEquals(120, ((Number) out[0]).intValue());
	}
	
	@Test
	public void testMultiplicationLineage1()
	{
		Multiplication add = new Multiplication(3);
		add.evaluate(4, 5, 6);
		Node root = add.getExplanation(new NthOutput(0));
		assertTrue(root instanceof PartNode);
		List<Pin<? extends Node>> links = root.getOutputLinks(0);
		assertEquals(1, links.size());
		Node and = links.get(0).getNode();
		assertTrue(and instanceof AndNode);
		links = and.getOutputLinks(0);
		assertEquals(3, links.size());
		for (int i = 0; i < links.size(); i++)
		{
			Node n = links.get(i).getNode();
			assertTrue(n instanceof PartNode);
			PartNode pn = (PartNode) n;
			Part d = pn.getPart();
			assertTrue(d.equals(new NthInput(i)));
		}
	}
	
	@Test
	public void testMultiplicationLineage2()
	{
		Multiplication add = new Multiplication(3);
		add.evaluate(5, 0, 6);
		Node root = add.getExplanation(new NthOutput(0));
		assertTrue(root instanceof PartNode);
		List<Pin<? extends Node>> links = root.getOutputLinks(0);
		assertEquals(1, links.size());
		Node n = links.get(0).getNode();
		assertTrue(n instanceof PartNode);
		PartNode pn = (PartNode) n;
		Part d = pn.getPart();
		assertTrue(d.equals(new NthInput(1)));
	}
	
	@Test
	public void testNumberCast1()
	{
		NumberCast nc = new NumberCast();
		Number n = (Number) nc.evaluate(-2)[0];
		assertEquals(-2, n.intValue());
	}
	
	@Test
	public void testNumberCast2()
	{
		NumberCast nc = new NumberCast();
		Number n = (Number) nc.evaluate("-2")[0];
		assertEquals(-2, n.intValue());
	}
	
	@Test
	public void testNumberCast3()
	{
		NumberCast nc = new NumberCast();
		Number n = (Number) nc.evaluate("foobar")[0];
		assertEquals(0, n.intValue());
	}
	
	@Test
	public void testIsGreaterThan1()
	{
		IsGreaterThan f = new IsGreaterThan();
		Boolean b = (Boolean) f.evaluate(-2, 3)[0];
		assertEquals(false, b);
	}
	
	@Test
	public void testIsGreaterThan2()
	{
		IsGreaterThan f = new IsGreaterThan();
		Boolean b = (Boolean) f.evaluate(3, -2)[0];
		assertEquals(true, b);
	}
	
	@Test (expected = InvalidArgumentTypeException.class)
	public void testIsGreaterThan3()
	{
		IsGreaterThan f = new IsGreaterThan();
		f.evaluate(3, "foo");
	}
}
