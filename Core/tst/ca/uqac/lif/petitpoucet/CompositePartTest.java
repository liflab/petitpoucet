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

/**
 * Unit tests for {@link CompositePart}.
 */
public class CompositePartTest
{
	@Test
	public void test1()
	{
		CompositePart cp = new CompositePart();
		assertEquals(0, cp.size());
		assertNull(cp.head());
		assertNull(cp.tail());
	}
	
	@Test
	public void test2()
	{
		CompositePart cp = new CompositePart(new DummyPart("a"));
		assertEquals(1, cp.size());
		assertEquals(new DummyPart("a"), cp.head());
		assertNull(cp.tail());
	}
	
	@Test
	public void test3()
	{
		CompositePart cp = new CompositePart(new DummyPart("a"), new DummyPart("b"));
		assertEquals(2, cp.size());
		assertEquals(new DummyPart("b"), cp.head());
		assertEquals(new DummyPart("a"), cp.tail());
		assertNotSame(cp, cp.tail());
	}
	
	@Test
	public void test4()
	{
		CompositePart cp = new CompositePart(new DummyPart("a"), new DummyPart("b"),new DummyPart("c"));
		assertEquals(3, cp.size());
		assertEquals(new DummyPart("c"), cp.head());
		assertEquals(new CompositePart(new DummyPart("a"), new DummyPart("b")), cp.tail());
		assertNotSame(new CompositePart(new DummyPart("a"), new DummyPart("b")), cp.tail());
	}
	
	@Test
	public void testEquals1()
	{
		CompositePart cp1 = new CompositePart(new DummyPart("a"), new DummyPart("b"),new DummyPart("c"));
		CompositePart cp2 = new CompositePart(new DummyPart("a"), new DummyPart("b"),new DummyPart("c"));
		assertNotSame(cp1, cp2);
		assertEquals(cp1, cp2);
	}
	
	@Test
	public void testEquals2()
	{
		CompositePart cp1 = new CompositePart(new DummyPart("a"), new DummyPart("b"),new DummyPart("c"));
		CompositePart cp2 = new CompositePart(new DummyPart("a"), new DummyPart("c"),new DummyPart("c"));
		assertNotSame(cp1, cp2);
		assertNotEquals(cp1, cp2);
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
}
