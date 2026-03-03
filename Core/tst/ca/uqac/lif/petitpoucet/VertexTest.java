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
package ca.uqac.lif.petitpoucet;

import static org.junit.Assert.*;

import org.junit.Test;

import static ca.uqac.lif.petitpoucet.Vertex.tree;

import ca.uqac.lif.petitpoucet.CompositePartTest.DummyPart;
import ca.uqac.lif.petitpoucet.Vertex.AndVertex;
import ca.uqac.lif.petitpoucet.Vertex.OrVertex;
import ca.uqac.lif.petitpoucet.Vertex.PartVertex;

/**
 * Unit tests for {@link Vertex}.
 */
public class VertexTest
{
	@Test
	public void test1()
	{
		Object o1 = new Object();
		PartVertex p1 = new PartVertex(new DummyPart("a"), o1);
		PartVertex p2 = new PartVertex(new DummyPart("a"), o1);
		assertTrue(p1.getChildren().isEmpty());
		assertTrue(p1.getParents().isEmpty());
		assertEquals(0, p1.childCount());
		assertEquals(0, p1.parentCount());
		assertNotSame(p1, p2);
		assertEquals(p1, p2);
	}
	
	@Test
	public void test2()
	{
		Object o1 = new Object();
		PartVertex p1 = new PartVertex(new DummyPart("a"), o1);
		PartVertex p2 = new PartVertex(new DummyPart("a"), new Object());
		assertNotSame(p1, p2);
		assertNotEquals(p1, p2);
	}
	
	@Test
	public void test3()
	{
		Object o1 = new Object();
		PartVertex p1 = new PartVertex(new DummyPart("a"), o1);
		PartVertex p2 = new PartVertex(new DummyPart("b"), o1);
		assertNotSame(p1, p2);
		assertNotEquals(p1, p2);
	}
	
	@Test
	public void test4()
	{
		Object o1 = new Object();
		PartVertex p1 = new PartVertex(new DummyPart("a"), o1);
		PartVertex p2 = new PartVertex(new DummyPart("b"), o1);
		p1.addChild(p2);
		assertEquals(1, p1.childCount());
		assertEquals(0, p1.parentCount());
		assertEquals(0, p2.childCount());
		assertEquals(1, p2.parentCount());
		assertEquals(p1, p2.getParents().get(0));
		assertEquals(p2, p1.getChildren().get(0));
	}
	
	@Test
	public void test5()
	{
		Object o1 = new Object();
		PartVertex p1 = new PartVertex(new DummyPart("a"), o1);
		PartVertex p2 = new PartVertex(new DummyPart("b"), o1);
		p1.addChild(p2);
		PartVertex q1 = new PartVertex(new DummyPart("a"), o1);
		PartVertex q2 = new PartVertex(new DummyPart("b"), o1);
		q1.addChild(q2);
		assertTrue(Vertex.same(p1, q1));
	}
	
	@Test
	public void test6()
	{
		Object o1 = new Object();
		PartVertex p1 = new PartVertex(new DummyPart("a"), o1);
		PartVertex p2 = new PartVertex(new DummyPart("b"), o1);
		p1.addChild(p2);
		PartVertex q1 = new PartVertex(new DummyPart("a"), o1);
		PartVertex q2 = new PartVertex(new DummyPart("b"), o1);
		PartVertex q3 = new PartVertex(new DummyPart("c"), o1);
		q1.addChild(q2);
		q1.addChild(q3);
		assertFalse(Vertex.same(p1, q1));
	}
	
	@Test
	public void testSquish1()
	{
		AndVertex a = new AndVertex();
		Object o = new Object();
		Vertex t1 = tree(part("a", o),
				tree(a, 
						tree(part("b", o)),
						tree(part("c", o))
						));
		Vertex.squish(a);
		assertTrue(Vertex.same(t1, tree(part("a", o),
						tree(part("b", o)),
						tree(part("c", o))
						)));
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSquish2()
	{
		Object o = new Object();
		AndVertex a = new AndVertex();
		PartVertex b = part("b", o);
		tree(part("a", o),
				tree(a, 
						tree(b),
						tree(part("c", o))
						));
		Vertex.squish(b);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSquish3()
	{
		Object o = new Object();
		AndVertex a = new AndVertex();
		PartVertex pa = part("a", o);
		PartVertex b = part("b", o);
		tree(pa,
				tree(a, 
						tree(b),
						tree(part("c", o))
						));
		Vertex.squish(pa);
	}
	
	@Test
	public void testSquish4()
	{
		Object o = new Object();
		PartVertex b = part("b", o);
		Vertex t = tree(part("a", o),
				tree(b, 
						tree(part("c", o))
						),
				part("d", o));
		Vertex.squish(b);
		assert(Vertex.same(t, tree(part("a", o),
										part("c", o),
				part("d", o))));
	}
	
	@Test
	public void testSimplify1()
	{
		Object o = new Object();
		Vertex t = tree(part("a", o),
				tree(and(),
						tree(and(),
								part("b", o),
								part("c", o)
								),
						tree(part("d", o))
						));
		Vertex.simplify(t);
		assertTrue(Vertex.same(t, tree(part("a", o),
				tree(and(),
								part("b", o),
								part("c", o),
						tree(part("d", o))
						))));
	}
	
	@Test
	public void testSimplify2()
	{
		Object o = new Object();
		Vertex t = tree(part("a", o),
				tree(and(),
								part("b", o)));
		Vertex.simplify(t);
		assertTrue(Vertex.same(t, tree(part("a", o),
								part("b", o))));
	}
	
	@Test
	public void testSimplify3()
	{
		Object o = new Object();
		Vertex t = tree(part("a", o),
				tree(and(),
						tree(or(),
								part("b", o),
								part("c", o)
								),
						tree(part("d", o))
						));
		Vertex.simplify(t);
		assertTrue(Vertex.same(t, tree(part("a", o),
				tree(and(),
						tree(or(),
								part("b", o),
								part("c", o)
								),
						tree(part("d", o))
						))));
	}
	
	@Test
	public void testSimplify4()
	{
		Object o = new Object();
		Vertex t = tree(part("a", o),
				tree(or(),
						tree(or(),
								part("b", o),
								part("c", o)
								),
						tree(part("d", o))
						));
		Vertex.simplify(t);
		assertTrue(Vertex.same(t, tree(part("a", o),
				tree(or(),
								part("b", o),
								part("c", o),
						tree(part("d", o))
						))));
	}
	
	@Test
	public void testSimplify5()
	{
		Object o = new Object();
		Vertex t = tree(part("a", o),
				tree(or(),
								part("b", o)));
		Vertex.simplify(t);
		assertTrue(Vertex.same(t, tree(part("a", o),
								part("b", o))));
	}
	
	@Test
	public void testSimplify6()
	{
		Object o = new Object();
		Vertex t = tree(part("a", o),
				tree(or(),
						tree(and(),
								part("b", o),
								part("c", o)
								),
						tree(part("d", o))
						));
		Vertex.simplify(t);
		assertTrue(Vertex.same(t, tree(part("a", o),
				tree(or(),
						tree(and(),
								part("b", o),
								part("c", o)
								),
						tree(part("d", o))
						))));
	}
	
	protected static PartVertex part(String label, Object o)
	{
		return new PartVertex(new DummyPart(label), o);
	}
	
	protected static AndVertex and()
	{
		return new AndVertex();
	}
	
	protected static OrVertex or()
	{
		return new OrVertex();
	}
}
