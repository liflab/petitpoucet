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
package ca.uqac.lif.petitpoucet.render;

import static ca.uqac.lif.petitpoucet.Assertions.assertEqualGraphs;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Subgraph;
import ca.uqac.lif.petitpoucet.ConcreteVertex.PartVertex;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.Vertex.AndVertex;
import ca.uqac.lif.petitpoucet.Vertex.OrVertex;
import ca.uqac.lif.petitpoucet.IdentityVertexFactory;

/**
 * Unit tests for {@link GraphUtilities}.
 */
public class GraphUtilitiesTest
{
	protected static final IdentityVertexFactory s_factory = new IdentityVertexFactory();
	
	@Before
	public void setup()
	{
		s_factory.clear();
	}
	
	@Test
	public void testSquish1()
	{
		AndVertex a = and();
		Object o = new Object();
		Vertex t1 = s_factory.tree(part("a", o),
				s_factory.tree(a, 
						s_factory.tree(part("b", o)),
						s_factory.tree(part("c", o))
						));
		GraphUtilities.squish(a);
		s_factory.clear();
		assertEqualGraphs(t1, s_factory.tree(part("a", o),
						s_factory.tree(part("b", o)),
						s_factory.tree(part("c", o))
						));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSquish2()
	{
		Object o = new Object();
		AndVertex a = and();
		PartVertex b = part("b", o);
		s_factory.tree(part("a", o),
				s_factory.tree(a, 
						s_factory.tree(b),
						s_factory.tree(part("c", o))
						));
		GraphUtilities.squish(b);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSquish3()
	{
		Object o = new Object();
		AndVertex a = and();
		PartVertex pa = part("a", o);
		PartVertex b = part("b", o);
		s_factory.tree(pa,
				s_factory.tree(a, 
						s_factory.tree(b),
						s_factory.tree(part("c", o))
						));
		GraphUtilities.squish(pa);
	}
	
	@Test
	public void testSquish4()
	{
		Object o = new Object();
		PartVertex b = part("b", o);
		Vertex t = s_factory.tree(part("a", o),
				s_factory.tree(b, 
						s_factory.tree(part("c", o))
						),
				part("d", o));
		GraphUtilities.squish(b);
		s_factory.clear();
		assertEqualGraphs(t, s_factory.tree(part("a", o),
										part("c", o),
				part("d", o)));
	}
	
	@Test
	public void testSimplify1()
	{
		Object o = new Object();
		Vertex t = s_factory.tree(part("a", o),
				s_factory.tree(and(),
						s_factory.tree(and(),
								part("b", o),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						));
		GraphUtilities.simplify(t);
		assertEqualGraphs(t, s_factory.tree(part("a", o),
				s_factory.tree(and(),
								part("b", o),
								part("c", o),
						s_factory.tree(part("d", o))
						)));
	}
	
	@Test
	public void testSimplifySubgraph1()
	{
		Object o = new Object();
		s_factory.tree(part("a", o),
				s_factory.tree(and(),
						s_factory.tree(and(),
								part("b", o),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						));
		Subgraph sg = s_factory.subgraph();
		GraphUtilities.simplify(sg);
		s_factory.clear();
		Vertex expected = s_factory.tree(part("a", o),
				s_factory.tree(and(),
						part("b", o),
						part("c", o),
				s_factory.tree(part("d", o))
				));
		//sg.render(System.out);
		//expected.render(System.out);
		assertEqualGraphs(sg.findRoot(), expected);
	}
	
	@Test
	public void testSimplify2()
	{
		Object o = new Object();
		Vertex t = s_factory.tree(part("a", o),
				s_factory.tree(and(),
								part("b", o)));
		GraphUtilities.simplify(t);
		s_factory.clear();
		assertEqualGraphs(t, s_factory.tree(part("a", o),
								part("b", o)));
	}
	
	@Test
	public void testSimplify3()
	{
		Object o = new Object();
		Vertex t = s_factory.tree(part("a", o),
				s_factory.tree(and(),
						s_factory.tree(or(),
								part("b", o),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						));
		GraphUtilities.simplify(t);
		s_factory.clear();
		assertEqualGraphs(t, s_factory.tree(part("a", o),
				s_factory.tree(and(),
						s_factory.tree(or(),
								part("b", o),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						)));
	}
	
	@Test
	public void testSimplify4()
	{
		Object o = new Object();
		Vertex t = s_factory.tree(part("a", o),
				s_factory.tree(or(),
						s_factory.tree(or(),
								part("b", o),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						));
		GraphUtilities.simplify(t);
		s_factory.clear();
		assertEqualGraphs(t, s_factory.tree(part("a", o),
				s_factory.tree(or(),
								part("b", o),
								part("c", o),
						s_factory.tree(part("d", o))
						)));
	}
	
	@Test
	public void testSimplify5()
	{
		Object o = new Object();
		Vertex t = s_factory.tree(part("a", o),
				s_factory.tree(or(),
								part("b", o)));
		GraphUtilities.simplify(t);
		s_factory.clear();
		assertEqualGraphs(t, s_factory.tree(part("a", o),
								part("b", o)));
	}
	
	@Test
	public void testSimplify6()
	{
		Object o = new Object();
		Vertex t = s_factory.tree(part("a", o),
				s_factory.tree(or(),
						s_factory.tree(and(),
								part("b", o),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						));
		GraphUtilities.simplify(t);
		s_factory.clear();
		assertEqualGraphs(t, s_factory.tree(part("a", o),
				s_factory.tree(or(),
						s_factory.tree(and(),
								part("b", o),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						)));
	}
	
	@Test
	public void collapseTest1()
	{
		Object o = new DummyObject("o");
		Vertex t = s_factory.tree(part("a", o),
				s_factory.tree(or(),
						s_factory.tree(and(),
								part("b", o),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						));
		GraphUtilities.collapse(t);
		s_factory.clear();
		assertEqualGraphs(t, s_factory.tree(part("a", o),
				s_factory.tree(or(),
						s_factory.tree(and(),
								part("b", o),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						)));
	}
	
	@Test
	public void collapseTest2()
	{
		Object o = new DummyObject("o");
		Vertex t = s_factory.tree(part("a", o),
				s_factory.tree(or(),
						s_factory.tree(and(),
								s_factory.tree(part("b", o), part("e", o)),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						));
		GraphUtilities.collapse(t);
		s_factory.clear();
		assertEqualGraphs(t, s_factory.tree(part("a", o),
				s_factory.tree(or(),
						s_factory.tree(and(),
								part("e", o),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						)));
	}
	
	@Test
	public void testDnf1()
	{
		Object o = new DummyObject("o");
		Vertex t = s_factory.tree(part("a", o),
				s_factory.tree(or(),
						s_factory.tree(and(),
								part("b", o),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						));
		MathSet<Clause> dnf = GraphUtilities.asDnf(t);
		MathSet<Clause> expected = new MathSet<>();
		expected.add(new Clause(part("d", o)));
		expected.add(new Clause(part("b", o), part("c", o)));
		assertEquals(expected, dnf);
	}
	
	@Test
	public void testDnf2()
	{
		Object o = new DummyObject("o");
		Vertex t = s_factory.tree(part("a", o),
				s_factory.tree(and(),
						s_factory.tree(or(),
								part("b", o),
								part("c", o)
								),
						s_factory.tree(part("d", o))
						));
		MathSet<Clause> dnf = GraphUtilities.asDnf(t);
		MathSet<Clause> expected = new MathSet<>();
		expected.add(new Clause(part("b", o), part("d", o)));
		expected.add(new Clause(part("c", o), part("d", o)));
		assertEquals(expected, dnf);
	}
	
	protected static AndVertex and()
	{
		return s_factory.getAnd();
	}
	
	protected static OrVertex or()
	{
		return s_factory.getOr();
	}
	
	protected static PartVertex part(String name, Object o)
	{
		return s_factory.getPart(new DummyPart(name), o);
	}
	
	/**
	 * A dummy part used for testing.
	 */
	public static class DummyPart implements Part
	{
		protected final String m_label;
		
		public DummyPart(String label)
		{
			super();
			m_label = label;
		}
		
		@Override
		public String toString()
		{
			return m_label;
		}

		@Override
		public Part duplicate(boolean with_state)
		{
			return this;
		}
		
		@Override
		public int hashCode()
		{
			return m_label.hashCode();
		}
		
		@Override
		public boolean equals(Object o)
		{
			return o instanceof DummyPart && ((DummyPart) o).m_label.compareTo(m_label) == 0;
		}
	}
	
	public static class DummyObject
	{
		protected final String m_label;
		
		public DummyObject(String label)
		{
			super();
			m_label = label;
		}
		
		@Override
		public String toString()
		{
			return m_label;
		}
	}
}