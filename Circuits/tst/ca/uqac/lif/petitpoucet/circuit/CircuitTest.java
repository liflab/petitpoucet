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

import static ca.uqac.lif.petitpoucet.Assertions.assertEqualGraphs;
import static ca.uqac.lif.petitpoucet.Vertex.and;
import static ca.uqac.lif.petitpoucet.Vertex.or;
import static ca.uqac.lif.petitpoucet.Vertex.tree;

import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.Subgraph;
import ca.uqac.lif.petitpoucet.AbstractVertex;
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
	public void test4() throws ExplanationException
	{
		Circuit in = new Circuit(1, 1, "in");
		Numbers.Double d = new Numbers.Double();
		in.add(d);
		in.associateInput(0, d, 0);
		in.associateOutput(0, d, 0);
		Circuit out = new Circuit(1, 1, "out");
		out.add(in);
		out.associateInput(0, in, 0);
		out.associateOutput(0, in, 0);
		Object o = out.evaluate(2);
		assertEquals(4f, o);
		AbstractVertex e = out.explain(OutputPart.FIRST);
		Vertex e_c = AbstractVertex.get(e);
		e_c.render(System.out);
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
		AbstractVertex e = circ.explain(new Connectable.OutputPart(0));
		factory.clear();
		Vertex expected_inside = 
				tree(factory.getPart(OutputPart.FIRST, add),
						tree(factory.getAnd(),
								factory.getPart(InputPart.FIRST, add),	
								factory.getPart(InputPart.SECOND, add)
								));
		Subgraph sg = factory.subgraph();
		Vertex root = factory.getPart(OutputPart.FIRST, circ);
		root.addChild(sg);
		
		sg.addChild(factory.getPart(InputPart.FIRST, circ), factory.getPart(InputPart.FIRST, add));
		sg.addChild(factory.getPart(InputPart.SECOND, circ), factory.getPart(InputPart.SECOND, add));
		root.render(System.out);
		Vertex e_c = AbstractVertex.get(e);
		assertNotNull(e_c);
		e_c.render(System.out);
		assertEqualGraphs(e_c, root);
	}

	@Test
	public void testExplain2() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory(); 
		Circuit circ = new Circuit(2, 1);
		Numbers.Multiplication mul = new Numbers.Multiplication(2);
		circ.add(mul);
		circ.associateInput(0, mul, 0);
		circ.associateInput(1, mul, 1);
		circ.associateOutput(0, mul, 0);
		Connectable.connect(new Constant(0), 0, circ, 0);
		Connectable.connect(new Constant(3), 0, circ, 1);
		Object o = circ.compute();
		assertEquals(0f, o);
		AbstractVertex e = circ.explain(new Connectable.OutputPart(0));
		Vertex e_c = AbstractVertex.get(e);
		e_c.render(System.out);
		factory.clear();
		Vertex expected = tree(factory.getPart(OutputPart.FIRST, mul),
				factory.getPart(InputPart.FIRST, mul));
		Subgraph sg = factory.subgraph();
		Vertex root = factory.getPart(OutputPart.FIRST, circ);
		root.addChild(sg);
		sg.addChild(factory.getPart(InputPart.FIRST, circ), factory.getPart(InputPart.FIRST, mul));
		assertEqualGraphs(root, e_c);
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
		AbstractVertex e = circ.explain(OutputPart.FIRST);
		//e.print(System.out);
		Vertex expected = tree(factory.getPart(OutputPart.FIRST, mul),
				tree(factory.getAnd(),
						tree(factory.getPart(InputPart.FIRST, mul),
								tree(factory.getPart(OutputPart.FIRST, add),
										tree(factory.getAnd(), 
												tree(factory.getPart(InputPart.FIRST, add)),
												tree(factory.getPart(InputPart.SECOND, add))))),	
						factory.getPart(InputPart.SECOND, mul)));
		Subgraph sg = factory.subgraph();
		Vertex root = factory.getPart(OutputPart.FIRST, circ);
		root.addChild(sg);
		sg.addChild(factory.getPart(InputPart.FIRST, circ), factory.getPart(InputPart.FIRST, add));
		sg.addChild(factory.getPart(InputPart.SECOND, circ), factory.getPart(InputPart.SECOND, add));
		sg.addChild(factory.getPart(InputPart.THIRD, circ), factory.getPart(InputPart.SECOND, mul));
		root.render(System.out);
		assertEqualGraphs(e, root);

	}

}
