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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A node that itself encloses a directed acyclic graph of other nodes. The
 * input/output pins of some of these nodes may be associated to the
 * input/output pins of the global container node.
 * 
 * @author Sylvain Hallé
 */
public class NestedNode extends Node
{
	/**
	 * The internal nodes contained within this node.
	 */
	protected List<Node> m_internalNodes;
	
	/**
	 * A map storing associations between the node's input pins and an input
	 * pin of some internal node.
	 */
	protected Map<Integer,Pin<? extends Node>> m_inputAssociations;
	
	/**
	 * A map storing associations between the node's output pins and an output
	 * pin of some internal node.
	 */
	protected Map<Integer,Pin<? extends Node>> m_outputAssociations;
	
	/**
	 * Creates a new nested node from a tree of connected nodes.
	 * @param root The root of the tree
	 * @return A new nested node
	 */
	public static NestedNode createFromTree(Node root)
	{
		NestedNodeCrawler c = new NestedNodeCrawler(root);
		c.crawl();
		List<Node> leaves = c.getLeaves();
		int out_arity = leaves.size();
		NestedNode nn = new NestedNode(1, out_arity);
		nn.addNodes(c.getNodes());
		nn.associateInput(0, root.getInputPin(0));
		for (int i = 0; i < out_arity; i++)
		{
			nn.associateOutput(i, leaves.get(i).getOutputPin(0));
		}
		return nn;
	}
	
	/**
	 * Creates a new empty nested node.
	 * @param in_arity The node's input arity
	 * @param out_arity The node's output arity
	 */
	public NestedNode(int in_arity, int out_arity)
	{
		super(in_arity, out_arity);
		m_internalNodes = new ArrayList<Node>();
		m_inputAssociations = new HashMap<Integer,Pin<? extends Node>>();
		for (int i = 0; i < getInputArity(); i++)
		{
			m_inputAssociations.put(i, null);
		}
		m_outputAssociations = new HashMap<Integer,Pin<? extends Node>>();
		for (int i = 0; i < getOutputArity(); i++)
		{
			m_outputAssociations.put(i, null);
		}
	}
	
	/**
	 * Associates an input pin of the nested node to an input pin of one of its
	 * internal nodes.
	 * @param i The index of the node's input pin
	 * @param p The input pin of the internal node
	 */
	public void associateInput(int i, Pin<? extends Node> p)
	{
		m_inputAssociations.put(i, p);
	}
	
	/**
	 * Gets the input pin of the internal node associated to a given input pin
	 * of the nested node.
	 * @param i The index of the input pin of the nested node
	 * @return The input pin of an internal node
	 */
	public Pin<? extends Node> getAssociatedInput(int i)
	{
		return m_inputAssociations.get(i);
	}
	
	/**
	 * Associates an input pin of the nested node to an output pin of one of its
	 * internal nodes.
	 * @param i The index of the node's output pin
	 * @param p The output pin of the internal node
	 */
	public void associateOutput(int i, Pin<? extends Node> p)
	{
		m_outputAssociations.put(i, p);
	}
	
	/**
	 * Gets the output pin of the internal node associated to a given output pin
	 * of the nested node.
	 * @param i The index of the output pin of the nested node
	 * @return The output pin of an internal node
	 */
	public Pin<? extends Node> getAssociatedOutput(int i)
	{
		return m_outputAssociations.get(i);
	}
	
	/**
	 * Adds nodes to the internal nodes of this nested node.
	 * @param nodes The nodes to add
	 */
	public void addNodes(Collection<? extends Node> nodes)
	{
		m_internalNodes.addAll(nodes);
	}
	
	/**
	 * Adds nodes to the internal nodes of this nested node.
	 * @param nodes The nodes to add
	 */
	public void addNodes(Node ... nodes)
	{
		for (int i = 0; i < nodes.length; i++)
		{
			m_internalNodes.add(nodes[i]);
		}
	}
	
	/**
	 * Gets the nested node input to which the n-th input of an inner node
	 * is associated with.
	 * @param node The node
	 * @param index The value of n in the previous description
	 * @return The nested node input number, or -1 of node is not associated
	 * to an input of the nested node 
	 */
	public int getNestedInput(Node node, int index)
	{
		for (Map.Entry<Integer,Pin<? extends Node>> e : m_inputAssociations.entrySet())
		{
			Pin<? extends Node> pin = e.getValue();
			if (pin.getNode().equals(node) && pin.getIndex() == index)
			{
				return e.getKey();
			}
		}
		return -1;
	}
	
	@Override
	public NestedNode duplicate(boolean with_state)
	{
		NestedNode nn = new NestedNode(getInputArity(), getOutputArity());
		copyInto(nn, with_state);
		return nn;
	}
	
	protected void copyInto(NestedNode nn, boolean with_state)
	{
		super.copyInto(nn, with_state);
		if (!m_internalNodes.isEmpty())
		{
			CopyCrawler c = new CopyCrawler(m_internalNodes.get(0), getConnector(), with_state);
			c.crawl();
			c.copyInto(nn);
		}
	}
	
	/**
	 * Gets an instance of node connector. This connector is used to connect
	 * nodes when creating a duplicate of the nested node. Descendants of this
	 * class can override this method if they require their nodes to be connected
	 * by a different connector than the package's basic {@link NodeConnector}.
	 * @return A node connector
	 */
	/*@ non_null @*/ protected NodeConnector getConnector()
	{
		return NodeConnector.instance;
	}
	
	/**
	 * A crawler that creates copies of all visited nodes and connects them in
	 * the same way as the originals.
	 */
	public class CopyCrawler extends Crawler
	{
		/**
		 * A map associating original nodes to their corresponding copy.
		 */
		/*@ non_null @*/ protected Map<Node,Node> m_copies;
		
		/**
		 * The set of nodes that have been expanded (i.e. their output links
		 * have been visited).
		 */
		/*@ non_null @*/ protected Set<Node> m_expanded;
		
		/**
		 * A connector used to connect nodes.
		 */
		/*@ non_null @*/ protected NodeConnector m_connector;
		
		/**
		 * Whether node duplication is stateful.
		 */
		/*@ non_null @*/ protected boolean m_withState;
		
		/**
		 * Creates a new crawler.
		 * @param start The starting point of the crawl
		 * @param connector A connector used to connect nodes
		 * @param with_state Set to {@code true} to make a stateful duplication of
		 * inner nodes, {@code false} otherwise
		 */
		public CopyCrawler(/*@ non_null @*/ Node start, /*@ non_null @*/ NodeConnector connector, boolean with_state)
		{
			super(start);
			m_copies = new HashMap<Node,Node>();
			m_connector = connector;
			m_withState = with_state;
			m_expanded = new HashSet<Node>(m_internalNodes.size());
		}
		
		/**
		 * Copies the content of the cloned nodes into a nested node.
		 * @param nn The node to copy the contents into
		 */
		public void copyInto(NestedNode nn)
		{
			nn.m_internalNodes.addAll(m_copies.values());
			for (Map.Entry<Integer,Pin<? extends Node>> e : m_inputAssociations.entrySet())
			{
				Pin<? extends Node> pin = e.getValue();
				Node target = m_copies.get(pin.getNode());
				nn.m_inputAssociations.put(e.getKey(), target.getInputPin(pin.getIndex()));
			}
			for (Map.Entry<Integer,Pin<? extends Node>> e : m_outputAssociations.entrySet())
			{
				Pin<? extends Node> pin = e.getValue();
				Node target = m_copies.get(pin.getNode());
				nn.m_outputAssociations.put(e.getKey(), target.getOutputPin(pin.getIndex()));
			}
		}
		
		public Node getCopyOf(Node n)
		{
			return m_copies.get(n);
		}
		
		@Override
		public void visit(/*@ non_null @*/ Node n)
		{
			if (!m_copies.containsKey(n))
			{
				Node n_copy = n.duplicate(m_withState);
				m_copies.put(n, n_copy);
			}
			for (int i = 0; i < n.getOutputArity(); i++)
			{
				List<Pin<? extends Node>> pins = n.getOutputLinks(i);
				for (Pin<? extends Node> pin : pins)
				{
					Node target = pin.getNode();
					if (!m_expanded.contains(target))
					{
						m_expanded.add(target);
						Node target_copy = target.duplicate(m_withState);
						m_copies.put(target, target_copy);
						m_connector.connectTo(m_copies.get(n), i, target_copy, pin.getIndex());
					}
				}
			}
		}
	}
	
	/**
	 * A crawler visiting all inner nodes of the nested node. It can then return
	 * the list of all such nodes, or the list of all "leaf" nodes (downstream
	 * nodes that have no output connection).
	 */
	public static class NestedNodeCrawler extends Crawler
	{
		/**
		 * The list of nodes that are leaves.
		 */
		/*@ non_null @*/ protected List<Node> m_leaves;
		
		/**
		 * The list of all visited nodes. A list is used instead of a set, so that
		 * enumerations of the visited elements are always in the same order at
		 * each execution.
		 */
		/*@ non_null @*/ protected List<Node> m_allNodes;
		
		/**
		 * Creates a new crawler.
		 * @param start The starting point of the crawl
		 */
		public NestedNodeCrawler(/*@ non_null @*/ Node start)
		{
			super(start);
			m_leaves = new ArrayList<Node>();
			m_allNodes = new ArrayList<Node>();
		}
		
		@Override
		public void visit(/*@ non_null @*/ Node n)
		{
			if (!m_allNodes.contains(n))
			{
				m_allNodes.add(n);
			}
			boolean is_leaf = true;
			for (int i = 0; i < n.getOutputArity(); i++)
			{
				if (n.getOutputLinks(i).size() > 0)
				{
					is_leaf = false;
					break;
				}
			}
			if (is_leaf && !m_leaves.contains(n))
			{
				m_leaves.add(n);
			}
		}
		
		/**
		 * Gets the list of visited nodes that are leaves.
		 * @return The leaves
		 */
		/*@ pure non_null @*/ public List<Node> getLeaves()
		{
			return m_leaves;
		}
		
		/**
		 * Gets the list of all visited nodes.
		 * @return The nodes
		 */
		/*@ pure non_null @*/ public List<Node> getNodes()
		{
			return m_allNodes;
		}
	}
}
