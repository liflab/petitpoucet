package ca.uqac.lif.dag;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FlatteningCrawler
{
	/**
	 * A map associating original nodes to their corresponding copy.
	 */
	/*@ non_null @*/ protected Map<Node,Node> m_copies;

	/**
	 * A connector used to connect nodes.
	 */
	/*@ non_null @*/ protected NodeConnector m_connector;

	/**
	 * The starting point for the crawl.
	 */
	/*@ non_null @*/ protected Node m_start;

	public FlatteningCrawler(/*@ non_null @*/ Node start, /*@ non_null @*/ NodeConnector connector)
	{
		super();
		m_start = start;
		m_copies = new HashMap<Node,Node>();
		m_connector = connector;
	}
	
	public FlatteningCrawler(/*@ non_null @*/ Node start, /*@ non_null @*/ NodeConnector connector, /*@ null @*/ FlatteningCrawler crawler)
	{
		this(start, connector);
		if (crawler != null)
		{
			m_copies.putAll(crawler.m_copies);
		}
	}

	public Node getRootCopy()
	{
		return m_copies.get(m_start);
	}

	public void crawl()
	{
		Set<Node> visited = new HashSet<Node>();
		crawl(m_start, visited);
	}

	protected Node crawl(Node current, Set<Node> visited)
	{
		if (visited.contains(current))
		{
			return current;
		}
		visited.add(current);
		Node copy_current = handleNode(current, visited);
		if (current instanceof NestedNode)
		{
			for (int i = 0; i < copy_current.getOutputArity(); i++)
			{
				Collection<Pin<? extends Node>> pins = copy_current.getOutputLinks(i);
				for (Pin<? extends Node> pin : pins)
				{
					Node n = pin.getNode();
					crawl(n, visited);
				}
			}
		}
		else
		{
			for (int i = 0; i < current.getOutputArity(); i++)
			{
				Collection<Pin<? extends Node>> pins = current.getOutputLinks(i);
				for (Pin<? extends Node> pin : pins)
				{
					Node n = pin.getNode();
					crawl(n, visited);
					Node copy_target = m_copies.get(n);
					m_connector.connectTo(copy_current, i, copy_target, pin.getIndex());
				}
			}
		}
		return copy_current;
	}

	/*@ non_null @*/ protected Node handleNode(/*@ non_null @*/ Node current, Set<Node> visited)
	{
		if (m_copies.containsKey(current))
		{
			return m_copies.get(current);
		}
		if (!(current instanceof NestedNode))
		{
			Node n_copy = current.duplicate(false);
			m_copies.put(current, n_copy);
			return m_copies.get(current);
		}
		NestedNode nn_current = (NestedNode) current;
		Node in_start = nn_current.getAssociatedInput(0).getNode();
		FlatteningCrawler in_fc = new FlatteningCrawler(in_start, m_connector);
		in_fc.crawl();
		m_copies.putAll(in_fc.m_copies);
		m_copies.put(current, in_fc.getRootCopy());
		for (int i = 0; i < nn_current.getOutputArity(); i++)
		{
			for (Pin<? extends Node> pin : nn_current.getOutputLinks(i))
			{
				Node out_target = pin.getNode();
				Node out_target_copy = crawl(out_target, visited);
				Pin<? extends Node> in_pin = nn_current.getAssociatedOutput(i);
				Node copy_in = in_fc.m_copies.get(in_pin.getNode());
				if (copy_in == null)
				{
					copy_in = crawl(in_pin.getNode(), visited);
				}
				m_connector.connectTo(copy_in, in_pin.getIndex(), out_target_copy, pin.getIndex());
			}
		}
		return in_fc.getRootCopy();
	}
}