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
package ca.uqac.lif.dag;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ca.uqac.lif.petitpoucet.AndNode;
import ca.uqac.lif.petitpoucet.GraphUtilities;
import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.OrNode;

public class GraphUtilitiesTest
{
	// A few dummy nodes to create graphs
	protected static LabelledNode root;
	protected static LabelledNode a;
	protected static LabelledNode b;
	protected static LabelledNode c;
	protected static LabelledNode d;
	protected static LabelledNode e;
	
	@Before
	public void setup()
	{
		// We must reset every node at every test
		root = new LabelledNode("root");
		a = new LabelledNode("a");
		b = new LabelledNode("b");
		c = new LabelledNode("c");
		d = new LabelledNode("d");
		e = new LabelledNode("e");	
	}
	
	/*
	 * Squashes the tree:
	 * root
	 * + a
	 * + b
	 *   + c
	 * We expect:
	 * root
	 * + a
	 * + c
	 */
	@Test
	public void testSquash1()
	{
		root.addChild(a);
		root.addChild(b);
		b.addChild(c);
		Node new_root = GraphUtilities.squash(root);
		assertNotEquals(root, new_root);
		assertEquals(2, new_root.getOutputLinks(0).size());
		LabelledNode n1 = (LabelledNode) new_root.getOutputLinks(0).get(0).getNode();
		assertEquals("a", (String) n1.getLabel());
		assertNotEquals(a, n1);
		LabelledNode n2 = (LabelledNode) new_root.getOutputLinks(0).get(1).getNode();
		assertEquals("c", (String) n2.getLabel());
		assertNotEquals(c, n2);
	}
	
	/*
	 * Squashes the tree:
	 * root
	 * + a
	 * + AND
	 *   + c
	 *   + d
	 * We expect: the same
	 */
	@Test
	public void testSquash2()
	{
		NodeFactory factory = new NodeFactory();
		root.addChild(a);
		AndNode and1 = factory.getAndNode();
		root.addChild(and1);
		and1.addChild(c);
		and1.addChild(d);
		Node new_root = GraphUtilities.squash(root);
		assertNotEquals(root, new_root);
		assertEquals(2, new_root.getOutputLinks(0).size());
		LabelledNode n1 = (LabelledNode) new_root.getOutputLinks(0).get(0).getNode();
		assertEquals("a", (String) n1.getLabel());
		assertNotEquals(a, n1);
		AndNode n2 = (AndNode) new_root.getOutputLinks(0).get(1).getNode();
		assertEquals(2, n2.getOutputLinks(0).size());
		LabelledNode n3 = (LabelledNode) n2.getOutputLinks(0).get(0).getNode();
		assertEquals("c", (String) n3.getLabel());
		assertNotEquals(c, n3);
		LabelledNode n4 = (LabelledNode) n2.getOutputLinks(0).get(1).getNode();
		assertEquals("d", (String) n4.getLabel());
		assertNotEquals(d, n4);
	}
	
	/*
	 * Squashes the tree:
	 * root
	 * + a
	 * + AND
	 *   + c
	 *   + OR
	 *     + b
	 *     + e
	 * We expect: the same
	 */
	@Test
	public void testSquash3()
	{
		NodeFactory factory = new NodeFactory();
		root.addChild(a);
		AndNode and1 = factory.getAndNode();
		root.addChild(and1);
		and1.addChild(c);
		OrNode or1 = factory.getOrNode();
		and1.addChild(or1);
		or1.addChild(b);
		or1.addChild(e);
		Node new_root = GraphUtilities.squash(root);
		assertNotEquals(root, new_root);
		assertEquals(2, new_root.getOutputLinks(0).size());
		LabelledNode n1 = (LabelledNode) new_root.getOutputLinks(0).get(0).getNode();
		assertEquals("a", (String) n1.getLabel());
		assertNotEquals(a, n1);
		AndNode n2 = (AndNode) new_root.getOutputLinks(0).get(1).getNode();
		assertEquals(2, n2.getOutputLinks(0).size());
		LabelledNode n3 = (LabelledNode) n2.getOutputLinks(0).get(0).getNode();
		assertEquals("c", (String) n3.getLabel());
		assertNotEquals(c, n3);
		OrNode n4 = (OrNode) n2.getOutputLinks(0).get(1).getNode();
		assertEquals(2, n4.getOutputLinks(0).size());
		LabelledNode n5 = (LabelledNode) n4.getOutputLinks(0).get(0).getNode();
		assertEquals("b", (String) n5.getLabel());
		assertNotEquals(b, n5);
		LabelledNode n6 = (LabelledNode) n4.getOutputLinks(0).get(1).getNode();
		assertEquals("e", (String) n6.getLabel());
		assertNotEquals(e, n6);
	}

	/*
	 * Squashes the tree:
	 * root
	 * + a
	 * + AND
	 *   + c
	 *   + AND
	 *     + b
	 *     + e
	 * We expect:
	 * root
	 * + a
	 * + AND
	 *   + c
	 *   + b
	 *   + e
	 */
	@Test
	public void testSquash4()
	{
		NodeFactory factory = new NodeFactory();
		root.addChild(a);
		AndNode and1 = factory.getAndNode();
		root.addChild(and1);
		and1.addChild(c);
		AndNode or1 = factory.getAndNode();
		and1.addChild(or1);
		or1.addChild(b);
		or1.addChild(e);
		Node new_root = GraphUtilities.squash(root);
		assertNotEquals(root, new_root);
		assertEquals(2, new_root.getOutputLinks(0).size());
		LabelledNode n1 = (LabelledNode) new_root.getOutputLinks(0).get(0).getNode();
		assertEquals("a", (String) n1.getLabel());
		assertNotEquals(a, n1);
		AndNode n2 = (AndNode) new_root.getOutputLinks(0).get(1).getNode();
		assertEquals(3, n2.getOutputLinks(0).size());
		LabelledNode n3 = (LabelledNode) n2.getOutputLinks(0).get(0).getNode();
		assertEquals("c", (String) n3.getLabel());
		assertNotEquals(c, n3);
		LabelledNode n4 = (LabelledNode) n2.getOutputLinks(0).get(1).getNode();
		assertEquals("b", (String) n4.getLabel());
		assertNotEquals(b, n4);
		LabelledNode n5 = (LabelledNode) n2.getOutputLinks(0).get(2).getNode();
		assertEquals("e", (String) n5.getLabel());
		assertNotEquals(e, n5);
	}
	
	/*
	 * Squashes the tree:
	 * root
	 * + a
	 * + OR
	 *   + c
	 *   + OR
	 *     + b
	 *     + e
	 * We expect:
	 * root
	 * + a
	 * + OR
	 *   + c
	 *   + b
	 *   + e
	 */
	@Test
	public void testSquash5()
	{
		NodeFactory factory = new NodeFactory();
		root.addChild(a);
		OrNode and1 = factory.getOrNode();
		root.addChild(and1);
		and1.addChild(c);
		OrNode or1 = factory.getOrNode();
		and1.addChild(or1);
		or1.addChild(b);
		or1.addChild(e);
		Node new_root = GraphUtilities.squash(root);
		assertNotEquals(root, new_root);
		assertEquals(2, new_root.getOutputLinks(0).size());
		LabelledNode n1 = (LabelledNode) new_root.getOutputLinks(0).get(0).getNode();
		assertEquals("a", (String) n1.getLabel());
		assertNotEquals(a, n1);
		OrNode n2 = (OrNode) new_root.getOutputLinks(0).get(1).getNode();
		assertEquals(3, n2.getOutputLinks(0).size());
		LabelledNode n3 = (LabelledNode) n2.getOutputLinks(0).get(0).getNode();
		assertEquals("c", (String) n3.getLabel());
		assertNotEquals(c, n3);
		LabelledNode n4 = (LabelledNode) n2.getOutputLinks(0).get(1).getNode();
		assertEquals("b", (String) n4.getLabel());
		assertNotEquals(b, n4);
		LabelledNode n5 = (LabelledNode) n2.getOutputLinks(0).get(2).getNode();
		assertEquals("e", (String) n5.getLabel());
		assertNotEquals(e, n5);
	}
	
	/*
	 * Squashes the tree:
	 * root
	 * + a
	 * + AND
	 *   + c
	 *     + e
	 *   + d   
	 * We expect:
	 * root
	 * + a
	 * + AND
	 *   + e
	 *   + d
	 */
	@Test
	public void testSquash6()
	{
		NodeFactory factory = new NodeFactory();
		root.addChild(a);
		AndNode and1 = factory.getAndNode();
		root.addChild(and1);
		and1.addChild(c);
		and1.addChild(d);
		c.addChild(e);
		Node new_root = GraphUtilities.squash(root);
		assertNotEquals(root, new_root);
		assertEquals(2, new_root.getOutputLinks(0).size());
		LabelledNode n1 = (LabelledNode) new_root.getOutputLinks(0).get(0).getNode();
		assertEquals("a", (String) n1.getLabel());
		assertNotEquals(a, n1);
		AndNode n2 = (AndNode) new_root.getOutputLinks(0).get(1).getNode();
		assertEquals(2, n2.getOutputLinks(0).size());
		LabelledNode n3 = (LabelledNode) n2.getOutputLinks(0).get(0).getNode();
		assertEquals("e", (String) n3.getLabel());
		assertNotEquals(e, n3);
		LabelledNode n4 = (LabelledNode) n2.getOutputLinks(0).get(1).getNode();
		assertEquals("d", (String) n4.getLabel());
		assertNotEquals(d, n4);
	}
}
