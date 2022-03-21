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
package ca.uqac.lif.petitpoucet.function.reflect;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * Unit tests for {@link Call}.
 */
public class CallTest
{
	@Test
	public void test1()
	{
		Call f = new Call("foo");
		Object o = f.evaluate(new Object() {
			@SuppressWarnings("unused")
			public int foo() {
				return 42;
			}
		})[0];
		assertEquals(42, o);
		PartNode root = f.getExplanation(NthOutput.FIRST);
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode child = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(new ReturnValue("foo"), NthInput.FIRST), child.getPart());
	}
	
	@Test
	public void test2()
	{
		Call f = new Call("bar");
		Object o = f.evaluate(new Object() {
			@SuppressWarnings("unused")
			public String bar() {
				return "The quick brown fox";
			}
		})[0];
		assertEquals("The quick brown fox", o);
		DummyPart dp = new DummyPart();
		PartNode root = f.getExplanation(ComposedPart.compose(dp, NthOutput.FIRST));
		assertEquals(1, root.getOutputLinks(0).size());
		PartNode child = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(ComposedPart.compose(dp, new ReturnValue("bar"), NthInput.FIRST), child.getPart());
	}
	
	@Test(expected = InvalidArgumentTypeException.class)
	public void test3()
	{
		Call f = new Call("foo");
		f.evaluate(new Object() {
			@SuppressWarnings("unused")
			public String bar() {
				return "The quick brown fox";
			}
		});
	}
	
	@Test
	public void test4()
	{
		Call f = new Call("bar");
		Object o = f.evaluate(new Object() {
			@SuppressWarnings("unused")
			public void bar() {
			}
		})[0];
		assertNull(o);
	}
	
	protected static class DummyPart implements Part
	{
		@Override
		public boolean appliesTo(Object arg0)
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Part head()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Part tail()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
