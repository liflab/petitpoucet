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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Crawler that produces a copy of a graph and explodes the contents of any
 * {@link NestedNode} directly into the global graph. This has for effect of
 * removing these nodes, producing a "flat" graph with no nested elements.
 * 
 * @author Sylvain Hallé
 *
 */
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
		m_copies = new HashMap<>();
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
		Set<Node> visited = new HashSet<>();
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