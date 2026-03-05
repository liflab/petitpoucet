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

import static org.junit.Assert.*;

import org.junit.Test;

import static ca.uqac.lif.petitpoucet.Vertex.and;
import static ca.uqac.lif.petitpoucet.Vertex.or;
import static ca.uqac.lif.petitpoucet.Vertex.tree;

import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.Connectable.InputPart;
import ca.uqac.lif.petitpoucet.Connectable.OutputPart;

/**
 * Unit tests for {@link Circuit}.
 */
@SuppressWarnings("unused")
public class CircuitTest
{
	@Test
	public void test1()
	{
		Circuit circ = new Circuit(2, 1);
		{
			Numbers.Addition add = new Numbers.Addition(2);
			circ.add(add);
			circ.associateInput(0, add, 0);
			circ.associateInput(1, add, 1);
			circ.associateOutput(0, add, 0);
		}
		Connectable.connect(new Constant(2), 0, circ, 0);
		Connectable.connect(new Constant(3), 0, circ, 1);
		float v = (Float) circ.compute(0);
		assertEquals(5, v, 0.1);
	}

	@Test
	public void test2()
	{
		Circuit circ = new Circuit(3, 1);
		Numbers.Addition add = new Numbers.Addition(2);
		{
			circ.add(add);
			circ.associateInput(0, add, 0);
			circ.associateInput(1, add, 1);
		}
		{
			Numbers.Multiplication mul = new Numbers.Multiplication(2);
			circ.add(mul);
			Connectable.connect(add, 0, mul, 0);
			circ.associateInput(2, mul, 1);
			circ.associateOutput(0, mul, 0);
		}
		Connectable.connect(new Constant(2), 0, circ, 0);
		Connectable.connect(new Constant(3), 0, circ, 1);
		Connectable.connect(new Constant(5), 0, circ, 2);
		float v = (Float) circ.compute(0);
		assertEquals(25, v, 0.1);
	}

	@Test
	public void testDuplicate1()
	{
		Circuit circ = new Circuit(2, 1);
		{
			Numbers.Addition add = new Numbers.Addition(2);
			circ.add(add);
			circ.associateInput(0, add, 0);
			circ.associateInput(1, add, 1);
			circ.associateOutput(0, add, 0);
		}
		Circuit c2 = circ.duplicate(false);
		Connectable.connect(new Constant(2), 0, c2, 0);
		Connectable.connect(new Constant(3), 0, c2, 1);
		float v = (Float) c2.compute(0);
		assertEquals(5, v, 0.1);
	}

	@Test
	public void test3()
	{
		Circuit circ = new Circuit(3, 1);
		Numbers.Addition add = new Numbers.Addition(2);
		{
			circ.add(add);
			circ.associateInput(0, add, 0);
			circ.associateInput(1, add, 1);
		}
		{
			Numbers.Multiplication mul = new Numbers.Multiplication(2);
			circ.add(mul);
			Connectable.connect(add, 0, mul, 0);
			circ.associateInput(2, mul, 1);
			circ.associateOutput(0, mul, 0);
		}
		Circuit circ2 = circ.duplicate(false);
		Connectable.connect(new Constant(2), 0, circ2, 0);
		Connectable.connect(new Constant(3), 0, circ2, 1);
		Connectable.connect(new Constant(5), 0, circ2, 2);
		float v = (Float) circ2.compute(0);
		assertEquals(25, v, 0.1);
	}

	@Test
	public void testExplain1() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory(); 
		Circuit circ = new Circuit(2, 1);
		Numbers.Addition add = new Numbers.Addition(2);
		circ.add(add);
		circ.associateInput(0, add, 0);
		circ.associateInput(1, add, 1);
		circ.associateOutput(0, add, 0);
		Connectable.connect(new Constant(2), 0, circ, 0);
		Connectable.connect(new Constant(3), 0, circ, 1);
		Object o = circ.compute();
		assertEquals(5f, o);
		Vertex e = circ.explain(new Connectable.OutputPart(0));
		//e.print(System.out);
		assertTrue(Vertex.same(e, tree(
				factory.getPart(new OutputPart(0), circ),
				tree(factory.getPart(new OutputPart(0), add),
						tree(factory.getAnd(),
								tree(factory.getPart(new InputPart(0), add), factory.getPart(new InputPart(0), circ)),	
								tree(factory.getPart(new InputPart(1), add), factory.getPart(new InputPart(1), circ))	
								)))));

	}

	@Test
	public void testExplain2() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory(); 
		Circuit circ = new Circuit(2, 1);
		Numbers.Multiplication add = new Numbers.Multiplication(2);
		circ.add(add);
		circ.associateInput(0, add, 0);
		circ.associateInput(1, add, 1);
		circ.associateOutput(0, add, 0);
		Connectable.connect(new Constant(0), 0, circ, 0);
		Connectable.connect(new Constant(3), 0, circ, 1);
		Object o = circ.compute();
		assertEquals(0f, o);
		Vertex e = circ.explain(new Connectable.OutputPart(0));
		//e.print(System.out);
		assertTrue(Vertex.same(e, tree(
				factory.getPart(new OutputPart(0), circ),
				tree(factory.getPart(new OutputPart(0), add),
						tree(factory.getPart(new InputPart(0), add), factory.getPart(new InputPart(0), circ))	
						))));

	}

	@Test
	public void testExplain3() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory(); 
		Circuit circ = new Circuit(3, 1);
		Numbers.Addition add = new Numbers.Addition(2);
		circ.associateInput(0, add, 0);
		circ.associateInput(1, add, 1);
		Numbers.Multiplication mul = new Numbers.Multiplication(2);
		Connectable.connect(add, 0, mul, 0);
		circ.associateInput(2,  mul, 1);
		circ.associateOutput(0, mul, 0);
		circ.add(add, mul);
		Connectable.connect(new Constant(2), 0, circ, 0);
		Connectable.connect(new Constant(3), 0, circ, 1);
		Connectable.connect(new Constant(4), 0, circ, 2);
		Object o = circ.compute();
		assertEquals(20f, o);
		Vertex e = circ.explain(new Connectable.OutputPart(0));
		//e.print(System.out);
		assertTrue(Vertex.same(e, tree(
				factory.getPart(new OutputPart(0), circ),
				tree(factory.getPart(new OutputPart(0), mul),
						tree(factory.getAnd(),
								tree(factory.getPart(new InputPart(0), mul),
										tree(factory.getPart(new OutputPart(0), add),
												tree(factory.getAnd(), 
														tree(factory.getPart(new InputPart(0), add), factory.getPart(new InputPart(0), circ)),
														tree(factory.getPart(new InputPart(1), add), factory.getPart(new InputPart(1), circ))))),	
								tree(factory.getPart(new InputPart(1), mul),
										factory.getPart(new InputPart(2), circ))	
								)))));

	}

}
