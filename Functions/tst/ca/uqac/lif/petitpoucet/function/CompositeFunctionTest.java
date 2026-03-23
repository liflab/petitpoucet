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
package ca.uqac.lif.petitpoucet.function;

import static org.junit.Assert.*;

import org.junit.Test;

import static ca.uqac.lif.petitpoucet.Assertions.assertEqualGraphs;
import static ca.uqac.lif.petitpoucet.function.ComputableConnector.connect;

import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.circuit.Connectable;
import ca.uqac.lif.petitpoucet.circuit.Connectable.InputPart;
import ca.uqac.lif.petitpoucet.circuit.Connectable.OutputPart;
import ca.uqac.lif.petitpoucet.function.ComputableConnection.ComputableDownstreamConnection;
import ca.uqac.lif.petitpoucet.function.ComputableConnection.ComputableUpstreamConnection;
import ca.uqac.lif.petitpoucet.Subgraph;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.ConcreteVertex;
import ca.uqac.lif.petitpoucet.IdentityVertexFactory;

/**
 * Unit tests for {@link CompositeFunction}.
 */
@SuppressWarnings("unused")
public class CompositeFunctionTest
{
	@Test
	public void test1()
	{
		CompositeFunction circ = new CompositeFunction(2, 1);
		{
			Numbers.Addition add = new Numbers.Addition(2);
			circ.add(add);
			circ.associateInput(0, add, 0);
			circ.associateInput(1, add, 1);
			circ.associateOutput(0, add, 0);
		}
		connect(new Constant(2), 0, circ, 0);
		connect(new Constant(3), 0, circ, 1);
		float v = (Float) circ.compute(0);
		assertEquals(5, v, 0.1);
	}

	@Test
	public void test2()
	{
		CompositeFunction circ = new CompositeFunction(3, 1);
		Numbers.Addition add = new Numbers.Addition(2);
		{
			circ.add(add);
			circ.associateInput(0, add, 0);
			circ.associateInput(1, add, 1);
		}
		{
			Numbers.Multiplication mul = new Numbers.Multiplication(2);
			circ.add(mul);
			connect(add, 0, mul, 0);
			circ.associateInput(2, mul, 1);
			circ.associateOutput(0, mul, 0);
		}
		connect(new Constant(2), 0, circ, 0);
		connect(new Constant(3), 0, circ, 1);
		connect(new Constant(5), 0, circ, 2);
		float v = (Float) circ.compute(0);
		assertEquals(25, v, 0.1);
	}

	@Test
	public void testDuplicate1()
	{
		CompositeFunction circ = new CompositeFunction(2, 1);
		{
			Numbers.Addition add = new Numbers.Addition(2);
			circ.add(add);
			circ.associateInput(0, add, 0);
			circ.associateInput(1, add, 1);
			circ.associateOutput(0, add, 0);
		}
		CompositeFunction c2 = circ.duplicate(false);
		connect(new Constant(2), 0, c2, 0);
		connect(new Constant(3), 0, c2, 1);
		float v = (Float) c2.compute(0);
		assertEquals(5, v, 0.1);
	}

	@Test
	public void test3()
	{
		CompositeFunction circ = new CompositeFunction(3, 1);
		Numbers.Addition add = new Numbers.Addition(2);
		{
			circ.add(add);
			circ.associateInput(0, add, 0);
			circ.associateInput(1, add, 1);
		}
		{
			Numbers.Multiplication mul = new Numbers.Multiplication(2);
			circ.add(mul);
			connect(add, 0, mul, 0);
			circ.associateInput(2, mul, 1);
			circ.associateOutput(0, mul, 0);
		}
		CompositeFunction circ2 = circ.duplicate(false);
		connect(new Constant(2), 0, circ2, 0);
		connect(new Constant(3), 0, circ2, 1);
		connect(new Constant(5), 0, circ2, 2);
		float v = (Float) circ2.compute(0);
		assertEquals(25, v, 0.1);
	}
	
	@Test
	public void test4() throws ExplanationException
	{
		CompositeFunction in = new CompositeFunction(1, 1, "in");
		Numbers.Double d = new Numbers.Double();
		in.add(d);
		in.associateInput(0, d, 0);
		in.associateOutput(0, d, 0);
		CompositeFunction out = new CompositeFunction(1, 1, "out");
		out.add(in);
		out.associateInput(0, in, 0);
		out.associateOutput(0, in, 0);
		Object[] out_v = new Object[1];
		out.evaluate(new Object[] {2}, out_v);
		assertEquals(4f, out_v[0]);
		Vertex e = out.explain(OutputPart.FIRST);
		ConcreteVertex e_c = Vertex.get(e);
		e_c.render(System.out);
	}

	@Test
	public void testExplain1() throws ExplanationException
	{
		IdentityVertexFactory factory = new IdentityVertexFactory(); 
		CompositeFunction circ = new CompositeFunction(2, 1);
		Numbers.Addition add = new Numbers.Addition(2);
		circ.add(add);
		circ.associateInput(0, add, 0);
		circ.associateInput(1, add, 1);
		circ.associateOutput(0, add, 0);
		connect(new Constant(2), 0, circ, 0);
		connect(new Constant(3), 0, circ, 1);
		Object o = circ.compute();
		assertEquals(5f, o);
		Vertex e = circ.explain(new Connectable.OutputPart(0));
		factory.clear();
		Vertex expected_inside = 
				factory.tree(factory.getPart(OutputPart.FIRST, add),
						factory.tree(factory.getAnd(),
								factory.getPart(InputPart.FIRST, add),	
								factory.getPart(InputPart.SECOND, add)
								));
		Subgraph sg = factory.subgraph();
		ConcreteVertex root = factory.getPart(OutputPart.FIRST, circ);
		root.addChild(sg);
		
		sg.addChild(factory.getPart(InputPart.FIRST, circ), factory.getPart(InputPart.FIRST, add));
		sg.addChild(factory.getPart(InputPart.SECOND, circ), factory.getPart(InputPart.SECOND, add));
		root.render(System.out);
		ConcreteVertex e_c = Vertex.get(e);
		assertNotNull(e_c);
		e_c.render(System.out);
		assertEqualGraphs(e_c, root);
	}

	@Test
	public void testExplain2() throws ExplanationException
	{
		IdentityVertexFactory factory = new IdentityVertexFactory(); 
		CompositeFunction circ = new CompositeFunction(2, 1);
		Numbers.Multiplication mul = new Numbers.Multiplication(2);
		circ.add(mul);
		circ.associateInput(0, mul, 0);
		circ.associateInput(1, mul, 1);
		circ.associateOutput(0, mul, 0);
		connect(new Constant(0), 0, circ, 0);
		connect(new Constant(3), 0, circ, 1);
		Object o = circ.compute();
		assertEquals(0f, o);
		Vertex e = circ.explain(new Connectable.OutputPart(0));
		ConcreteVertex e_c = Vertex.get(e);
		e_c.render(System.out);
		factory.clear();
		Vertex expected = factory.tree(factory.getPart(OutputPart.FIRST, mul),
				factory.getPart(InputPart.FIRST, mul));
		Subgraph sg = factory.subgraph();
		ConcreteVertex root = factory.getPart(OutputPart.FIRST, circ);
		root.addChild(sg);
		sg.addChild(factory.getPart(InputPart.FIRST, circ), factory.getPart(InputPart.FIRST, mul));
		assertEqualGraphs(root, e_c);
	}

	@Test
	public void testExplain3() throws ExplanationException
	{
		IdentityVertexFactory factory = new IdentityVertexFactory(); 
		CompositeFunction circ = new CompositeFunction(3, 1);
		Numbers.Addition add = new Numbers.Addition(2);
		circ.associateInput(0, add, 0);
		circ.associateInput(1, add, 1);
		Numbers.Multiplication mul = new Numbers.Multiplication(2);
		connect(add, 0, mul, 0);
		circ.associateInput(2,  mul, 1);
		circ.associateOutput(0, mul, 0);
		circ.add(add, mul);
		connect(new Constant(2), 0, circ, 0);
		connect(new Constant(3), 0, circ, 1);
		connect(new Constant(4), 0, circ, 2);
		Object o = circ.compute();
		assertEquals(20f, o);
		Vertex e = circ.explain(OutputPart.FIRST);
		//e.print(System.out);
		Vertex expected = factory.tree(factory.getPart(OutputPart.FIRST, mul),
				factory.tree(factory.getAnd(),
						factory.tree(factory.getPart(InputPart.FIRST, mul),
								factory.tree(factory.getPart(OutputPart.FIRST, add),
										factory.tree(factory.getAnd(), 
												factory.tree(factory.getPart(InputPart.FIRST, add)),
												factory.tree(factory.getPart(InputPart.SECOND, add))))),	
						factory.getPart(InputPart.SECOND, mul)));
		Subgraph sg = factory.subgraph();
		ConcreteVertex root = factory.getPart(OutputPart.FIRST, circ);
		root.addChild(sg);
		sg.addChild(factory.getPart(InputPart.FIRST, circ), factory.getPart(InputPart.FIRST, add));
		sg.addChild(factory.getPart(InputPart.SECOND, circ), factory.getPart(InputPart.SECOND, add));
		sg.addChild(factory.getPart(InputPart.THIRD, circ), factory.getPart(InputPart.SECOND, mul));
		root.render(System.out);
		assertEqualGraphs(e, root);

	}
}
