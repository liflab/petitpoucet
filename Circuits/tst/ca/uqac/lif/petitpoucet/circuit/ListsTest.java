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

import org.junit.Test;

import static ca.uqac.lif.petitpoucet.Assertions.assertEqualGraphs;
import static ca.uqac.lif.petitpoucet.Vertex.tree;

import ca.uqac.lif.petitpoucet.AbstractVertex;
import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.Connectable.InputPart;
import ca.uqac.lif.petitpoucet.Connectable.OutputPart;
import ca.uqac.lif.petitpoucet.Explainable.ExplanationException;
import ca.uqac.lif.petitpoucet.LazyVertex;
import ca.uqac.lif.petitpoucet.Subgraph;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.circuit.Lists.Apply;
import ca.uqac.lif.petitpoucet.circuit.Lists.ElementAt;
import ca.uqac.lif.petitpoucet.circuit.Lists.NthElement;

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
		AbstractVertex e = f.explain(new OutputPart(0));
		assertEqualGraphs(e, tree(factory.getPart(new CompositePart(new NthElement(0), new InputPart(0)), f)));
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
		AbstractVertex e = f.explain(CompositePart.compose(new NthElement(1), new OutputPart(0)));
		assertTrue(e instanceof LazyVertex);
		VertexFactory subf = factory.subfactory(f);
		Vertex.tree(subf.getPart(new OutputPart(0), f.getApplication(1)),
				subf.getPart(new InputPart(0), f.getApplication(1)));
		Subgraph sg = subf.subgraph();
		Vertex root = factory.getPart(CompositePart.compose(new NthElement(1), new InputPart(0)), f);
		root.addChild(sg);
		sg.addChild(factory.getPart(CompositePart.compose(new NthElement(1), new InputPart(0)), f), 0);
		assertEqualGraphs(e, root);
	}
	
}
