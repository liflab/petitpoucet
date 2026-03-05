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
import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.Vertex;

/**
 * Unit tests for {@link Node}.
 */
@SuppressWarnings("unused")
public class NodeTest
{
	@Test
	public void test1()
	{
		DummyNode dn = new DummyNode();
		assertEquals("a", dn.compute(0));
		assertEquals(1, dn.m_evaluateCount);
		assertEquals("a", dn.compute(0));
		assertEquals(1, dn.m_evaluateCount);
		dn.reset();
		assertEquals("a", dn.compute(0));
		assertEquals(2, dn.m_evaluateCount);
	}
	
	@Test
	public void test2() throws ExplanationException
	{
		DummyNode dn = new DummyNode();
		assertEquals("a", dn.compute(0));
		Vertex v = dn.explain(new Connectable.OutputPart(0));
		assertEqualGraphs(v, and());
	}
	
	@Test(expected = ExplanationException.class)
	public void test3() throws ExplanationException
	{
		DummyNode dn = new DummyNode();
		assertEquals("a", dn.compute(0));
		dn.explain(new Connectable.OutputPart(1));
	}
	
	@Test(expected = ExplanationException.class)
	public void test4() throws ExplanationException
	{
		DummyNode dn = new DummyNode();
		assertEquals("a", dn.compute(0));
		dn.explain(new Connectable.InputPart(1));
	}
	
	protected static class DummyNode extends Node
	{
		public int m_evaluateCount = 0;
		
		public DummyNode()
		{
			super(0, 1);
		}

		@Override
		public void evaluate(Object[] input, Object[] output)
		{
			output[0] = "a";
			m_evaluateCount++;
		}

		@Override
		public Node duplicate(boolean with_state)
		{
			return new DummyNode();
		}
	}
}
