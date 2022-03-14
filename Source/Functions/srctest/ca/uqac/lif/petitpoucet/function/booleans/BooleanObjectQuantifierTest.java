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
package ca.uqac.lif.petitpoucet.function.booleans;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ca.uqac.lif.petitpoucet.AndNode;
import ca.uqac.lif.petitpoucet.ComposedPart;
import ca.uqac.lif.petitpoucet.OrNode;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.NthInput;
import ca.uqac.lif.petitpoucet.function.NthOutput;
import ca.uqac.lif.petitpoucet.function.number.IsEven;
import ca.uqac.lif.petitpoucet.function.number.IsOdd;
import ca.uqac.lif.petitpoucet.function.vector.NthElement;

/**
 * Unit tests for {@link BooleanObjectQuantifier}.
 */
public class BooleanObjectQuantifierTest
{
	@Test
	public void testForAll1()
	{
		AllObjects q = new AllObjects(new IsOdd());
		Boolean b = (Boolean) q.evaluate(getList(1, 2, 3, 4))[0];
		assertFalse(b);
		PartNode root = q.getExplanation(NthOutput.FIRST);
		assertNotNull(root);
		OrNode conn = (OrNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(2, conn.getOutputLinks(0).size());
		{
			PartNode child1 = (PartNode) conn.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(NthOutput.FIRST), child1.getPart());
			PartNode child2 = (PartNode) child1.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(NthInput.FIRST), child2.getPart());
			PartNode child3 = (PartNode) child2.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new NthElement(1), NthInput.FIRST), child3.getPart());
		}
		{
			PartNode child1 = (PartNode) conn.getOutputLinks(0).get(1).getNode();
			assertEquals(ComposedPart.compose(NthOutput.FIRST), child1.getPart());
			PartNode child2 = (PartNode) child1.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(NthInput.FIRST), child2.getPart());
			PartNode child3 = (PartNode) child2.getOutputLinks(0).get(0).getNode();
			assertEquals(ComposedPart.compose(new NthElement(3), NthInput.FIRST), child3.getPart());
		}
	}
	
	@Test
	public void testForAll2()
	{
		AllObjects q = new AllObjects(new IsOdd());
		Boolean b = (Boolean) q.evaluate(getList(1, 3, 5, 7))[0];
		assertTrue(b);
		PartNode root = q.getExplanation(NthOutput.FIRST);
		assertNotNull(root);
		AndNode conn = (AndNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(4, conn.getOutputLinks(0).size());
	}
	
	@Test
	public void testForAll3()
	{
		AllObjects q = new AllObjects(new IsOdd());
		Boolean b = (Boolean) q.evaluate(getList(1))[0];
		assertTrue(b);
		PartNode root = q.getExplanation(NthOutput.FIRST);
		assertNotNull(root);
		PartNode conn = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(1, conn.getOutputLinks(0).size());
	}
	
	@Test
	public void testForAll4()
	{
		AllObjects q = new AllObjects(new IsOdd());
		Boolean b = (Boolean) q.evaluate(getList(2))[0];
		assertFalse(b);
		PartNode root = q.getExplanation(NthOutput.FIRST);
		assertNotNull(root);
		PartNode conn = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(1, conn.getOutputLinks(0).size());
	}
	
	@Test
	public void testExists1()
	{
		SomeObject q = new SomeObject(new IsEven());
		Boolean b = (Boolean) q.evaluate(getList(1, 2, 3, 4))[0];
		assertTrue(b);
		PartNode root = q.getExplanation(NthOutput.FIRST);
		assertNotNull(root);
		OrNode conn = (OrNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(2, conn.getOutputLinks(0).size());
	}
	
	@Test
	public void testExists2()
	{
		SomeObject q = new SomeObject(new IsEven());
		Boolean b = (Boolean) q.evaluate(getList(1, 3, 5, 7))[0];
		assertFalse(b);
		PartNode root = q.getExplanation(NthOutput.FIRST);
		assertNotNull(root);
		AndNode conn = (AndNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(4, conn.getOutputLinks(0).size());
	}
	
	@Test
	public void testExists3()
	{
		SomeObject q = new SomeObject(new IsEven());
		Boolean b = (Boolean) q.evaluate(getList(1))[0];
		assertFalse(b);
		PartNode root = q.getExplanation(NthOutput.FIRST);
		assertNotNull(root);
		PartNode conn = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(1, conn.getOutputLinks(0).size());
	}
	
	@Test
	public void testExists4()
	{
		SomeObject q = new SomeObject(new IsEven());
		Boolean b = (Boolean) q.evaluate(getList(2))[0];
		assertTrue(b);
		PartNode root = q.getExplanation(NthOutput.FIRST);
		assertNotNull(root);
		PartNode conn = (PartNode) root.getOutputLinks(0).get(0).getNode();
		assertEquals(1, conn.getOutputLinks(0).size());
	}
	
	protected static List<Object> getList(Object ... objects)
	{
		return Arrays.asList(objects);
	}
}
