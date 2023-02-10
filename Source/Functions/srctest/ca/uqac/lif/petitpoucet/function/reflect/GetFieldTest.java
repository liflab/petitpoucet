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
package ca.uqac.lif.petitpoucet.function.reflect;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.strings.Range;

/**
 * Unit tests for {@link GetField}.
 */
public class GetFieldTest
{
	@Test
	public void test1()
	{
		TestObject o = new TestObject(42, "foo");
		GetField f = new GetField("m_x");
		Object v = f.evaluate(o)[0];
		assertTrue(v instanceof Integer);
		assertEquals(42, v);
		PartNode root = f.getExplanation(NthOutput.FIRST);
		PartNode child = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new Field("m_x"), NthInput.FIRST), child.getPart());
	}
	
	@Test
	public void test2()
	{
		TestObject o = new TestObject(42, "foo");
		GetField f = new GetField("m_message");
		Object v = f.evaluate(o)[0];
		assertTrue(v instanceof String);
		assertEquals("foo", v);
		PartNode root = f.getExplanation(ComposedPart.compose(new Range(0, 1), NthOutput.FIRST));
		PartNode child = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new Range(0, 1), new Field("m_message"), NthInput.FIRST), child.getPart());
	}
	
	/**
	 * A dummy object used to test the function.
	 */
	protected static class TestObject
	{
		public int m_x;
		
		protected String m_message;
		
		public TestObject(int x, String message)
		{
			super();
			m_x = x;
			m_message = message;
		}
	}
}
