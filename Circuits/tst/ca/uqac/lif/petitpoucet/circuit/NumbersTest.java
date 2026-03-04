/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.circuit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import org.junit.Test;

import static ca.uqac.lif.petitpoucet.Vertex.and;
import static ca.uqac.lif.petitpoucet.Vertex.or;
import static ca.uqac.lif.petitpoucet.Vertex.same;
import static ca.uqac.lif.petitpoucet.Vertex.tree;

import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.Vertex.PartVertex;

/**
 * Unit tests for the functions defined in {@link Numbers}.
 */
@SuppressWarnings("unused")
public class NumbersTest
{
	@Test
	public void testAddition1()
	{
		Numbers.Addition f = new Numbers.Addition(3);
		Connectable.connect(new Constant(2), 0, f, 0);
		Connectable.connect(new Constant(3), 0, f, 1);
		Connectable.connect(new Constant(4), 0, f, 2);
		float v = (Float) f.compute(0);
		assertEquals(9, v, 0.01);
	}
	
	@Test
	public void testMultiplication1()
	{
		Numbers.Multiplication f = new Numbers.Multiplication(3);
		Connectable.connect(new Constant(2), 0, f, 0);
		Connectable.connect(new Constant(3), 0, f, 1);
		Connectable.connect(new Constant(4), 0, f, 2);
		float v = (Float) f.compute(0);
		assertEquals(24, v, 0.01);
	}
	
	@Test
	public void testMultiplication2() throws ExplanationException
	{
		Numbers.Multiplication f = new Numbers.Multiplication(3);
		Connectable.connect(new Constant(2), 0, f, 0);
		Connectable.connect(new Constant(0), 0, f, 1);
		Connectable.connect(new Constant(4), 0, f, 2);
		float v = (Float) f.compute(0);
		assertEquals(0, v, 0.01);
		Vertex e = f.explain(new Connectable.OutputPart(0));
		assertTrue(same(e, tree(new PartVertex(new Connectable.InputPart(1), f))));
	}
	
	@Test
	public void testMultiplication3() throws ExplanationException
	{
		Numbers.Multiplication f = new Numbers.Multiplication(3);
		Connectable.connect(new Constant(0), 0, f, 0);
		Connectable.connect(new Constant(6), 0, f, 1);
		Connectable.connect(new Constant(0), 0, f, 2);
		float v = (Float) f.compute(0);
		assertEquals(0, v, 0.01);
		Vertex e = f.explain(new Connectable.OutputPart(0));
		assertTrue(same(e, tree(or(
				new PartVertex(new Connectable.InputPart(0), f),
				new PartVertex(new Connectable.InputPart(2), f)
				))));
	}
}
