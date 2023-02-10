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

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ca.uqac.lif.dag.LabelledNode;
import ca.uqac.lif.dag.NestedNode;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.NodeConnector;
import ca.uqac.lif.petitpoucet.NodeFactory.ObjectPart;

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

	//A few dummy nodes to create graphs
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
		NodeFactory factory = NodeFactory.getFactory();
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
		NodeFactory factory = NodeFactory.getFactory();
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
		NodeFactory factory = NodeFactory.getFactory();
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
		NodeFactory factory = NodeFactory.getFactory();
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
	 *   | + e
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
		NodeFactory factory = NodeFactory.getFactory();
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

	/*
	 * Flattens the tree:
	 * root
	 * + a
	 * | + c
	 * + b
	 *   + d
	 * We expect: the same
	 */
	@Test
	public void testFlatten1()
	{
		root.addChild(a);
		root.addChild(b);
		a.addChild(c);
		b.addChild(d);
		Node new_root = GraphUtilities.flatten(root);
		assertSameLabel(root, new_root);
		assertHasChildren(2, new_root);
		LabelledNode a1 = (LabelledNode) getChild(new_root, 0);
		assertSameLabel(a, a1);
		assertHasChildren(1, a1);
		LabelledNode b1 = (LabelledNode) getChild(new_root, 1);
		assertSameLabel(b, b1);
		assertHasChildren(1, b1);
		LabelledNode c1 = (LabelledNode) getChild(a1, 0);
		assertSameLabel(c, c1);
		LabelledNode d1 = (LabelledNode) getChild(b1, 0);
		assertSameLabel(d, d1);
	}

	/*
	 * Flattens the tree:
	 * root
	 * + [
	 *    a
	 *    + b
	 *    + c
	 *   ]
	 * We expect:
	 * root
	 * + a
	 *   + b
	 *   + c
	 */
	@Test
	public void testFlatten2()
	{
		NestedNode nn = new NestedNode(1, 2);
		a.addChild(b);
		a.addChild(c);
		nn.addNodes(a, b, c);
		nn.associateInput(0, a.getInputPin(0));
		nn.associateOutput(0, b.getInputPin(0));
		nn.associateOutput(1, c.getInputPin(0));
		root.addChild(nn);
		Node new_root = GraphUtilities.flatten(root);
		assertSameLabel(root, new_root);
		assertHasChildren(1, new_root);
		LabelledNode a1 = (LabelledNode) getChild(new_root, 0);
		assertSameLabel(a, a1);
		assertHasChildren(2, a1);
		LabelledNode b1 = (LabelledNode) getChild(a1, 0);
		assertSameLabel(b, b1);
		LabelledNode c1 = (LabelledNode) getChild(a1, 1);
		assertSameLabel(c, c1);
	}

	/*
	 * Flattens the tree:
	 * root
	 * + [
	 *    a
	 *    + b
	 *    + c
	 *   ]
	 *   + d
	 *   + e
	 * We expect:
	 * root
	 * + a
	 *   + b
	 *     + d
	 *   + c
	 *     + e
	 */
	@Test
	public void testFlatten3()
	{
		NestedNode nn = new NestedNode(1, 2);
		a.addChild(b);
		a.addChild(c);
		nn.addNodes(a, b, c);
		nn.associateInput(0, a.getInputPin(0));
		nn.associateOutput(0, b.getInputPin(0));
		nn.associateOutput(1, c.getInputPin(0));
		root.addChild(nn);
		NodeConnector.connect(nn, 0, d, 0);
		NodeConnector.connect(nn, 1, e, 0);
		Node new_root = GraphUtilities.flatten(root);
		assertSameLabel(root, new_root);
		assertHasChildren(1, new_root);
		LabelledNode a1 = (LabelledNode) getChild(new_root, 0);
		assertSameLabel(a, a1);
		assertHasChildren(2, a1);
		LabelledNode b1 = (LabelledNode) getChild(a1, 0);
		assertSameLabel(b, b1);
		LabelledNode c1 = (LabelledNode) getChild(a1, 1);
		assertSameLabel(c, c1);
		assertHasChildren(1, b1);
		LabelledNode d1 = (LabelledNode) getChild(b1, 0);
		assertSameLabel(d, d1);
		assertHasChildren(1, c1);
		LabelledNode e1 = (LabelledNode) getChild(c1, 0);
		assertSameLabel(e, e1);
	}

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
		Set<Clause> clauses = GraphUtilities.asDnf(and);
		assertEquals(2, clauses.size());
		assertTrue(clauses.contains(getClause(PART_A, PART_B, PART_F, PART_G)));
		assertTrue(clauses.contains(getClause(PART_C, PART_D, PART_E, PART_F, PART_G)));
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
		Set<Clause> clauses = GraphUtilities.asDnf(or);
		assertEquals(4, clauses.size());
		assertTrue(clauses.contains(getClause(PART_A, PART_B)));
		assertTrue(clauses.contains(getClause(PART_C, PART_D, PART_E)));
		assertTrue(clauses.contains(getClause(PART_C, PART_D, PART_F)));
		assertTrue(clauses.contains(getClause(PART_C, PART_D, PART_G)));
	}

	/**
	 * Asserts that two nodes are <em>distinct</em> labelled nodes with the
	 * same label.
	 * @param n1 The first node
	 * @param n2 The second node
	 */
	protected static void assertSameLabel(Node n1, Node n2)
	{
		assertTrue(n1 instanceof LabelledNode);
		assertTrue(n2 instanceof LabelledNode);
		assertNotEquals(n1, n2);
		assertEquals(((LabelledNode) n1).getLabel(), ((LabelledNode) n2).getLabel());
	}

	/**
	 * Asserts that a node has a given number of children.
	 * @param num_children The expected number of children
	 * @param n The node
	 */
	protected static void assertHasChildren(int num_children, Node n)
	{
		assertEquals(num_children, n.getOutputLinks(0).size());
	}

	/**
	 * Gets the n-th child node of the first output pin of a node.
	 * @param n The node
	 * @param child_index The child index
	 * @return The child
	 */
	protected static Node getChild(Node n, int child_index)
	{
		return n.getOutputLinks(0).get(child_index).getNode();
	}

	protected static Clause getClause(Part ... parts)
	{
		Clause c = new Clause();
		for (Part p : parts)
		{
			c.add(new ObjectPart(p, OBJECT));
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
