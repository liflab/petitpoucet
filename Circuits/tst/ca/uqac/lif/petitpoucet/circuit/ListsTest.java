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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static ca.uqac.lif.petitpoucet.Assertions.assertEqualGraphs;
import static ca.uqac.lif.petitpoucet.Vertex.tree;

import ca.uqac.lif.petitpoucet.AbstractVertex;
import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.Connectable.InputPart;
import ca.uqac.lif.petitpoucet.Connectable.OutputPart;
import ca.uqac.lif.petitpoucet.Explainable;
import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.Subgraph;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.circuit.Lists.Apply;
import ca.uqac.lif.petitpoucet.circuit.Lists.ElementAt;
import ca.uqac.lif.petitpoucet.circuit.Lists.NthElement;
import ca.uqac.lif.petitpoucet.circuit.Lists.Window;

/**
 * Unit tests for the classes in the {@link Lists} class.
 * @author Sylvain Hallé
 */
public class ListsTest
{
	@Test
	public void testElementAt1()
	{
		ElementAt f = new ElementAt(0);
		Connectable.connect(new Constant(Arrays.asList("a", "b", "c")), 0, f, 0);
		Object o = f.compute();
		assertEquals("a", o);
	}

	@Test
	public void testElementAt2() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		ElementAt f = new ElementAt(0);
		Connectable.connect(new Constant(Arrays.asList("a", "b", "c")), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(OutputPart.FIRST);
		assertEqualGraphs(e, tree(factory.getPart(new CompositePart(new NthElement(0), InputPart.FIRST), f)));
	}

	@Test
	public void testApply1()
	{
		Numbers.Double d = new Numbers.Double();
		Circuit c = getCircuit(d);
		Apply f = new Apply(c);
		Connectable.connect(new Constant(Arrays.asList(1, 2, 3)), 0, f, 0);
		Object o = f.compute();
		assertEquals(Arrays.asList(2f, 4f, 6f), o);
	}

	@Test
	public void testApplyExplain1() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		Numbers.Double d = new Numbers.Double();
		Circuit c = getCircuit(d, "CDouble");
		Apply f = new Apply(c);
		Connectable.connect(new Constant(Arrays.asList(1, 2, 3)), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(CompositePart.compose(new NthElement(1), OutputPart.FIRST));
		Vertex c_e = AbstractVertex.get(e);
		assertNotNull(c_e);
		System.out.println("Received:");
		c_e.render(System.out);
		factory.clear();
		Vertex expected = factory.getPart(CompositePart.compose(new NthElement(1), OutputPart.FIRST), f);
		{
			VertexFactory subf = factory.subfactory(f);
			Vertex v1 = subf.getPart(OutputPart.FIRST, d);
			Vertex v2 = subf.getPart(InputPart.FIRST, d);
			v1.addChild(v2);
			Subgraph sg = subf.subgraph();
			Vertex c_in = factory.getPart(OutputPart.FIRST, c);
			expected.addChild(c_in);
			c_in.addChild(sg);
			sg.addChild(tree(
					factory.getPart(InputPart.FIRST, c), 
					factory.getPart(CompositePart.compose(new NthElement(1), InputPart.FIRST), f)),
					v1);
		}
		System.out.println("Expected:");
		expected.render(System.out);
		assertEqualGraphs(expected, c_e);
	}

	@Test
	public void testApplyExplain2() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		Numbers.Double d = new Numbers.Double();
		Circuit c = getCircuit(d);
		Apply f = new Apply(c);
		Connectable.connect(new Constant(Arrays.asList(1, 2, 3)), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(OutputPart.FIRST);
		Vertex c_e = AbstractVertex.get(e);
		factory.clear();
		assertEqualGraphs(c_e, Vertex.tree(
				factory.getPart(OutputPart.FIRST, f),
				factory.getPart(InputPart.FIRST, f)));
	}

	@Test
	public void testApplyExplain3() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		Numbers.Double d = new Numbers.Double();
		Circuit c = getCircuit(d);
		Apply f = new Apply(c);
		Connectable.connect(new Constant(Arrays.asList(1, 2, 3)), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(CompositePart.compose(new NthElement(10), new NthElement(1), OutputPart.FIRST));
		Vertex c_e = AbstractVertex.get(e);
		c_e.render(System.out);
		factory.clear();
		Vertex expected = factory.getPart(CompositePart.compose(new NthElement(10), new NthElement(1), OutputPart.FIRST), f);
		{
			Vertex root = factory.getPart(CompositePart.compose(new NthElement(10), OutputPart.FIRST), c);
			expected.addChild(root);
			VertexFactory subf = factory.subfactory(f);
			Vertex.tree(subf.getPart(CompositePart.compose(new NthElement(10), OutputPart.FIRST), d),
					subf.getPart(CompositePart.compose(new NthElement(10), InputPart.FIRST), d));
			Subgraph sg = subf.subgraph();
			root.addChild(sg);
			sg.addChild(tree(
					factory.getPart(CompositePart.compose(new NthElement(10), InputPart.FIRST), c),
					subf.getPart(CompositePart.compose(new NthElement(10), new NthElement(1), InputPart.FIRST), f)), subf.getPart(CompositePart.compose(new NthElement(10), InputPart.FIRST), d));
		}
		expected.render(System.out);
		assertEqualGraphs(c_e, expected);
	}

	@Test
	public void testWindow1()
	{
		Numbers.Multiplication mul = new Numbers.Multiplication(3);
		Circuit c = getCircuit(mul);
		Window f = new Window(3, c);
		Connectable.connect(new Constant(Arrays.asList(1, 2, 3)), 0, f, 0);
		List<?> out = (List<?>) f.compute();
		assertEquals(Arrays.asList(6f), out);
	}

	@Test
	public void testWindowExplain1() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		Numbers.Multiplication d = new Numbers.Multiplication(3);
		Circuit c = getCircuit(d, "InWin");
		Window f = new Window(3, c);
		Connectable.connect(new Constant(Arrays.asList(1, 2, 3, 4)), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(CompositePart.compose(new NthElement(1), OutputPart.FIRST));
		Vertex c_e = AbstractVertex.get(e);
		c_e.render(System.out);
		factory.clear();
		Vertex root = factory.getPart(CompositePart.compose(new NthElement(1), OutputPart.FIRST), f);
		{
			Vertex c_root = factory.getPart(OutputPart.FIRST, c);
			root.addChild(c_root);
			VertexFactory subf = factory.subfactory(c);
			{
				VertexFactory subsubf = subf.subfactory(d);
				{
					Vertex mul_root = subsubf.getPart(OutputPart.FIRST, d);
					Vertex and = subsubf.getAnd();
					mul_root.addChild(and);
					and.addChild(subsubf.getPart(InputPart.FIRST, d));
					and.addChild(subsubf.getPart(InputPart.SECOND, d));
					and.addChild(subsubf.getPart(InputPart.THIRD, d));
				}
				Subgraph sg1 = subsubf.subgraph();
				Vertex c1 = subf.getPart(InputPart.FIRST, c);
				Vertex c2 = subf.getPart(InputPart.SECOND, c);
				Vertex c3 = subf.getPart(InputPart.THIRD, c);
				sg1.addChild(c1, subsubf.getPart(InputPart.FIRST, d));
				sg1.addChild(c2, subsubf.getPart(InputPart.SECOND, d));
				sg1.addChild(c3, subsubf.getPart(InputPart.THIRD, d));
				c1.addChild(factory.getPart(CompositePart.compose(new NthElement(1), InputPart.FIRST), f));
				c2.addChild(factory.getPart(CompositePart.compose(new NthElement(2), InputPart.FIRST), f));
				c3.addChild(factory.getPart(CompositePart.compose(new NthElement(3), InputPart.FIRST), f));
				c_root.addChild(sg1);
			}
		}
		root.render(System.out);
		assertEqualGraphs(root, c_e);
	}

	@Test
	public void testWindowExplain2() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		Numbers.Multiplication d = new Numbers.Multiplication(3);
		Circuit c = getCircuit(d);
		Window f = new Window(3, c);
		Connectable.connect(new Constant(Arrays.asList(1, 0, 0, 0)), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(CompositePart.compose(new NthElement(1), OutputPart.FIRST));
		Vertex c_e = AbstractVertex.get(e);
		factory.clear();
		Vertex root = factory.getPart(CompositePart.compose(new NthElement(1), OutputPart.FIRST), f);
		{
			Vertex c_root = factory.getPart(OutputPart.FIRST, c);
			root.addChild(c_root);
			VertexFactory subf = factory.subfactory(c);
			{
				VertexFactory subsubf = subf.subfactory(d);
				{
					Vertex mul_root = subsubf.getPart(OutputPart.FIRST, d);
					Vertex and = subsubf.getOr();
					mul_root.addChild(and);
					and.addChild(subsubf.getPart(InputPart.FIRST, d));
					and.addChild(subsubf.getPart(InputPart.SECOND, d));
					and.addChild(subsubf.getPart(InputPart.THIRD, d));
				}
				Subgraph sg1 = subsubf.subgraph();
				Vertex c1 = subf.getPart(InputPart.FIRST, c);
				Vertex c2 = subf.getPart(InputPart.SECOND, c);
				Vertex c3 = subf.getPart(InputPart.THIRD, c);
				sg1.addChild(c1, subsubf.getPart(InputPart.FIRST, d));
				sg1.addChild(c2, subsubf.getPart(InputPart.SECOND, d));
				sg1.addChild(c3, subsubf.getPart(InputPart.THIRD, d));
				c1.addChild(factory.getPart(CompositePart.compose(new NthElement(1), InputPart.FIRST), f));
				c2.addChild(factory.getPart(CompositePart.compose(new NthElement(2), InputPart.FIRST), f));
				c3.addChild(factory.getPart(CompositePart.compose(new NthElement(3), InputPart.FIRST), f));
				c_root.addChild(sg1);
			}
		}
		assertEqualGraphs(root, c_e);
	}

	@Test
	public void testWindowExplainCut1() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		Numbers.Multiplication d = new Numbers.Multiplication(3);
		Circuit c = getCircuit(d);
		Window f = new Window(3, c);
		Connectable.connect(new Constant(Arrays.asList(1, 0, 0, 0)), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(CompositePart.compose(new NthElement(1), OutputPart.FIRST), Explainable.CUT);
		Vertex c_e = AbstractVertex.get(e);
		factory.clear();
		Vertex root = factory.getPart(CompositePart.compose(new NthElement(1), OutputPart.FIRST), f);
		{
			Vertex c_root = factory.getPart(OutputPart.FIRST, c);
			root.addChild(c_root);
			VertexFactory subf = factory.subfactory(c);
			{
				VertexFactory subsubf = subf.subfactory(d);
				{
					Vertex mul_root = subsubf.getPart(OutputPart.FIRST, d);
					mul_root.addChild(subsubf.getPart(InputPart.FIRST, d));
				}
				Subgraph sg1 = subsubf.subgraph();
				Vertex c1 = subf.getPart(InputPart.FIRST, c);
				sg1.addChild(c1, subsubf.getPart(InputPart.FIRST, d));
				c1.addChild(factory.getPart(CompositePart.compose(new NthElement(1), InputPart.FIRST), f));
				c_root.addChild(sg1);
			}
		}
		c_e.render(System.out);
		assertEqualGraphs(root, c_e);
	}

	protected static Circuit getCircuit(Node n)
	{
		return getCircuit(n, null);
	}

	protected static Circuit getCircuit(Node n, String name)
	{
		Circuit c = new Circuit(n.getInputArity(), n.getOutputArity(), name);
		c.add(n);
		for (int i = 0; i < n.getInputArity(); i++)
		{
			c.associateInput(i, n, i);
		}
		for (int i = 0; i < n.getOutputArity(); i++)
		{
			c.associateOutput(i, n, i);
		}
		return c;
	}

}
