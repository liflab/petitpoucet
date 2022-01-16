/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2022 Sylvain Hallé

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

import org.junit.Test;

/**
 * Unit tests for {@link ComposedPart}.
 */
public class ComposedPartTest
{
	@Test
	public void test1()
	{
		Part p = ComposedPart.compose(new DummyPart(0), null);
		assertEquals(new DummyPart(0), p);
	}
	
	@Test
	public void test2()
	{
		Part p = ComposedPart.compose(null, new DummyPart(0));
		assertEquals(new DummyPart(0), p);
	}
	
	@Test
	public void test3()
	{
		Part p = ComposedPart.compose(new DummyPart(0), new DummyPart(1));
		assertEquals(new DummyPart(1), p.head());
		assertEquals(new DummyPart(0), p.tail());
	}
	
	protected static class DummyPart implements Part
	{
		protected int m_x;
		
		public DummyPart(int x)
		{
			super();
			m_x = x;
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
		
		@Override
		public boolean equals(Object o)
		{
			return ((DummyPart) o).m_x == m_x;
		}
	}
}
