/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2023 Sylvain Hallé

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
package ca.uqac.lif.petitpoucet;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * Unit tests for {@link GraphUtilities}.
 */
public class GraphUtilitiesTest
{
	/**
	 * A dummy object used in tests.
	 */
	protected static final Object OBJECT = new Object();
	
	/**
	 * A dummy test part used in tests.
	 */
	protected static final Part PART_A = new TestPart("a");
	
	/**
	 * A dummy test part used in tests.
	 */
	protected static final Part PART_B = new TestPart("b");
	
	/**
	 * A dummy test part used in tests.
	 */
	protected static final Part PART_C = new TestPart("c");
	
	/**
	 * A dummy test part used in tests.
	 */
	protected static final Part PART_D = new TestPart("d");
	
	/**
	 * A dummy test part used in tests.
	 */
	protected static final Part PART_E = new TestPart("e");
	
	/**
	 * A dummy test part used in tests.
	 */
	protected static final Part PART_F = new TestPart("f");
	
	/**
	 * A dummy test part used in tests.
	 */
	protected static final Part PART_G = new TestPart("g");
	
	@Test
	public void testDnf1()
	{
		NodeFactory factory = NodeFactory.getFactory();
		AndNode and = factory.getAndNode();
		{
			OrNode or = factory.getOrNode();
			{
				AndNode child_and = factory.getAndNode();
				child_and.addChild(getNode(factory, PART_A));
				child_and.addChild(getNode(factory, PART_B));
				or.addChild(child_and);
			}
			{
				AndNode child_and = factory.getAndNode();
				child_and.addChild((getNode(factory, PART_C)));
				child_and.addChild((getNode(factory, PART_D)));
				child_and.addChild((getNode(factory, PART_E)));
				or.addChild(child_and);
			}
			and.addChild(or);
		}
		{
			AndNode child_and = factory.getAndNode();
			child_and.addChild((getNode(factory, PART_F)));
			child_and.addChild((getNode(factory, PART_G)));
			and.addChild(child_and);
		}
		List<Clause> clauses = GraphUtilities.asDnf(and);
		assertEquals(2, clauses.size());
		assertTrue(clauses.contains(getClause(factory, PART_A, PART_B, PART_F, PART_G)));
		assertTrue(clauses.contains(getClause(factory, PART_C, PART_D, PART_E, PART_F, PART_G)));
	}
	
	@Test
	public void testDnf2()
	{
		NodeFactory factory = NodeFactory.getFactory();
		OrNode or = factory.getOrNode();
		{
			AndNode and = factory.getAndNode();
			and.addChild(getNode(factory, PART_A));
			and.addChild(getNode(factory, PART_B));
			or.addChild(and);
		}
		{
			AndNode and = factory.getAndNode();
			and.addChild(getNode(factory, PART_C));
			and.addChild(getNode(factory, PART_D));
			{
				OrNode nested_or = factory.getOrNode();
				nested_or.addChild(getNode(factory, PART_E));
				nested_or.addChild(getNode(factory, PART_F));
				nested_or.addChild(getNode(factory, PART_G));
				and.addChild(nested_or);
			}
			or.addChild(and);
		}
		List<Clause> clauses = GraphUtilities.asDnf(or);
		assertEquals(4, clauses.size());
		assertTrue(clauses.contains(getClause(factory, PART_A, PART_B)));
		assertTrue(clauses.contains(getClause(factory, PART_C, PART_D, PART_E)));
		assertTrue(clauses.contains(getClause(factory, PART_C, PART_D, PART_F)));
		assertTrue(clauses.contains(getClause(factory, PART_C, PART_D, PART_G)));
	}
	
	protected static Clause getClause(NodeFactory factory, Part ... parts)
	{
		Clause c = new Clause();
		for (Part p : parts)
		{
			c.add(getNode(factory, p));
		}
		return c;
	}
	
	protected static PartNode getNode(NodeFactory factory, Part p)
	{
		return factory.getPartNode(p, OBJECT); 
	}
	
	/**
	 * A dummy part used for testing.
	 */
	protected static class TestPart implements Part
	{
		protected final String m_name;
		
		public TestPart(String name)
		{
			super();
			m_name = name;
		}
		
		@Override
		public int hashCode()
		{
			return m_name.hashCode();
		}
		
		@Override
		public boolean equals(Object o)
		{
			return o instanceof TestPart && m_name.compareTo(((TestPart) o).m_name) == 0;
		}
		
		@Override
		public boolean appliesTo(Object o)
		{
			return true;
		}

		@Override
		public Part head()
		{
			return this;
		}

		@Override
		public Part tail()
		{
			return null;
		}
	}
}
