/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2021 Sylvain Hallé

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

import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;

/**
 * Unit tests for {@link ComposedPart}.
 */
public class ComposedPartTest
{
	@Test
	public void test1()
	{
		Part p = ComposedPart.compose(new NthElement(0), null);
		assertEquals(new NthElement(0), p);
	}
	
	@Test
	public void test2()
	{
		Part p = ComposedPart.compose(null, new NthElement(0));
		assertEquals(new NthElement(0), p);
	}
	
	@Test
	public void test3()
	{
		Part p = ComposedPart.compose(new NthElement(0), NthInput.FIRST);
		assertEquals(NthInput.FIRST, p.head());
		assertEquals(new NthElement(0), p.tail());
	}
}
