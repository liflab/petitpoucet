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
		Apply f = new Apply(d);
		Connectable.connect(new Constant(Arrays.asList(1, 2, 3)), 0, f, 0);
		Object o = f.compute();
		assertEquals(Arrays.asList(2f, 4f, 6f), o);
	}
	
	@Test
	public void testApplyExplain1() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		Numbers.Double d = new Numbers.Double();
		Apply f = new Apply(d);
		Connectable.connect(new Constant(Arrays.asList(1, 2, 3)), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(CompositePart.compose(new NthElement(1), OutputPart.FIRST));
		Vertex c_e = AbstractVertex.get(e);
		assertNotNull(c_e);
		System.out.println("Received:");
		c_e.render(System.out);
		factory.clear();
		VertexFactory subf = factory.subfactory(f);
		Vertex.tree(subf.getPart(OutputPart.FIRST, d),
				subf.getPart(InputPart.FIRST, d));
		Subgraph sg = subf.subgraph();
		sg.addChild(factory.getPart(CompositePart.compose(new NthElement(1), InputPart.FIRST), f), subf.getPart(InputPart.FIRST, d));
		Vertex expected = tree(factory.getPart(OutputPart.FIRST, f), sg);
		System.out.println("Expected:");
		Vertex expected_root = tree(factory.getPart(CompositePart.compose(new NthElement(1), InputPart.FIRST), f), expected);
		expected_root.render(System.out);
		assertEqualGraphs(expected_root, c_e);
	}
	
	@Test
	public void testApplyExplain2() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		Numbers.Double d = new Numbers.Double();
		Apply f = new Apply(d);
		Connectable.connect(new Constant(Arrays.asList(1, 2, 3)), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(OutputPart.FIRST);
		Vertex c_e = AbstractVertex.get(e);
		factory.clear();
		assertEqualGraphs(c_e, Vertex.tree(factory.getPart(InputPart.FIRST, f)));
	}
	
	@Test
	public void testApplyExplain3() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		Numbers.Double d = new Numbers.Double();
		Apply f = new Apply(d);
		Connectable.connect(new Constant(Arrays.asList(1, 2, 3)), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(CompositePart.compose(new NthElement(10), new NthElement(1), OutputPart.FIRST));
		Vertex c_e = AbstractVertex.get(e);
		factory.clear();
		VertexFactory subf = factory.subfactory(f);
		Vertex.tree(subf.getPart(CompositePart.compose(new NthElement(10), OutputPart.FIRST), d),
				subf.getPart(CompositePart.compose(new NthElement(10), InputPart.FIRST), d));
		Subgraph sg = subf.subgraph();
		sg.addChild(factory.getPart(CompositePart.compose(new NthElement(10), new NthElement(1), InputPart.FIRST), f),
				subf.getPart(CompositePart.compose(new NthElement(10), InputPart.FIRST), d));
		assertEqualGraphs(c_e, sg);
	}
	
	@Test
	public void testWindow1()
	{
		Numbers.Multiplication mul = new Numbers.Multiplication(3);
		Window f = new Window(3, mul);
		Connectable.connect(new Constant(Arrays.asList(1, 2, 3)), 0, f, 0);
		List<?> out = (List<?>) f.compute();
		assertEquals(Arrays.asList(6f), out);
	}
	
	@Test
	public void testWindowExplain1() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		Numbers.Multiplication d = new Numbers.Multiplication(3);
		Window f = new Window(3, d);
		Connectable.connect(new Constant(Arrays.asList(1, 2, 3, 4)), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(CompositePart.compose(new NthElement(1), OutputPart.FIRST));
		Vertex c_e = AbstractVertex.get(e);
		c_e.render(System.out);
		factory.clear();
		VertexFactory subf = factory.subfactory(f);
		Vertex.tree(subf.getPart(OutputPart.FIRST, d),
				Vertex.and(
						subf.getPart(InputPart.FIRST, d),
						subf.getPart(InputPart.SECOND, d),
						subf.getPart(InputPart.THIRD, d)));
		Subgraph sg = subf.subgraph();
		sg.addChild(factory.getPart(CompositePart.compose(new NthElement(1), InputPart.FIRST), f), subf.getPart(InputPart.FIRST, d));
		sg.addChild(factory.getPart(CompositePart.compose(new NthElement(2), InputPart.FIRST), f), subf.getPart(InputPart.SECOND, d));
		sg.addChild(factory.getPart(CompositePart.compose(new NthElement(3), InputPart.FIRST), f), subf.getPart(InputPart.THIRD, d));
		sg.render(System.out);
		assertEqualGraphs(c_e, sg);
	}
	
	@Test
	public void testWindowExplain2() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		Numbers.Multiplication d = new Numbers.Multiplication(3);
		Window f = new Window(3, d);
		Connectable.connect(new Constant(Arrays.asList(1, 0, 0, 0)), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(CompositePart.compose(new NthElement(1), OutputPart.FIRST));
		Vertex c_e = AbstractVertex.get(e);
		factory.clear();
		VertexFactory subf = factory.subfactory(f);
		Vertex.tree(subf.getPart(OutputPart.FIRST, d),
				Vertex.or(
						subf.getPart(InputPart.FIRST, d),
						subf.getPart(InputPart.SECOND, d),
						subf.getPart(InputPart.THIRD, d)));
		Subgraph sg = subf.subgraph();
		sg.addChild(factory.getPart(CompositePart.compose(new NthElement(1), InputPart.FIRST), f), subf.getPart(InputPart.FIRST, d));
		sg.addChild(factory.getPart(CompositePart.compose(new NthElement(2), InputPart.FIRST), f), subf.getPart(InputPart.SECOND, d));
		sg.addChild(factory.getPart(CompositePart.compose(new NthElement(3), InputPart.FIRST), f), subf.getPart(InputPart.THIRD, d));
		assertEqualGraphs(c_e, sg);
	}
	
	@Test
	public void testWindowExplainCut1() throws ExplanationException
	{
		VertexFactory factory = new VertexFactory();
		Numbers.Multiplication d = new Numbers.Multiplication(3);
		Window f = new Window(3, d);
		Connectable.connect(new Constant(Arrays.asList(1, 0, 0, 0)), 0, f, 0);
		f.compute();
		AbstractVertex e = f.explain(CompositePart.compose(new NthElement(1), OutputPart.FIRST), Explainable.CUT);
		Vertex c_e = AbstractVertex.get(e);
		factory.clear();
		VertexFactory subf = factory.subfactory(f);
		Vertex.tree(subf.getPart(OutputPart.FIRST, d),
						subf.getPart(InputPart.FIRST, d));
		Subgraph sg = subf.subgraph();
		sg.addChild(factory.getPart(CompositePart.compose(new NthElement(1), InputPart.FIRST), f), subf.getPart(InputPart.FIRST, d));
		assertEqualGraphs(c_e, sg);
	}
	
}
