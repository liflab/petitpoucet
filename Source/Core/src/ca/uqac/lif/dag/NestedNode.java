package ca.uqac.lif.dag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NestedNode extends Node
{
	protected List<Node> m_internalNodes;
	
	protected Map<Integer,Pin<? extends Node>> m_inputAssociations;
	
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
	
	protected static class NestedNodeCrawler extends Crawler
	{
		protected List<Node> m_leaves;
		
		protected List<Node> m_allNodes;
		
		public NestedNodeCrawler(Node start)
		{
			super(start);
			m_leaves = new ArrayList<Node>();
			m_allNodes = new ArrayList<Node>();
		}
		
		@Override
		public void visit(Node n)
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
		
		public List<Node> getLeaves()
		{
			return m_leaves;
		}
		
		public List<Node> getNodes()
		{
			return m_allNodes;
		}
		
	}
}
