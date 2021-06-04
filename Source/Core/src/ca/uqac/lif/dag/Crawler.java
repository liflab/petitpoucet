package ca.uqac.lif.dag;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Visits every node in a directed acyclic graph.
 */
public class Crawler
{
	/**
	 * A flag that determines if the crawler is allowed to take forward
	 * connections.
	 */
	protected boolean m_allowForward;
	
	/**
	 * A flag that determines if the crawler is allowed to take backward
	 * connections.
	 */
	protected boolean m_allowBackward;
	
	/**
	 * The starting point of the crawl.
	 */
	protected Node m_start;
	
	/**
	 * Creates a new crawler.
	 * @param start The starting point of the crawl
	 */
	public Crawler(Node start)
	{
		super();
		m_start = start;
		m_allowForward = true;
		m_allowBackward = true;
	}
	
	/**
	 * Visits every node in the graph from the specified starting point.
	 */
	public void crawl()
	{
		Queue<Node> to_visit = new ArrayDeque<Node>();
		Set<Node> visited = new HashSet<Node>();
		to_visit.add(m_start);
		while (!to_visit.isEmpty())
		{
			Node current = to_visit.remove();
			visited.add(current);
			visit(current);
			if (m_allowForward)
			{
				for (int i = 0; i < current.getOutputArity(); i++)
				{
					Collection<Pin<? extends Node>> pins = current.getOutputLinks(i);
					for (Pin<? extends Node> pin : pins)
					{
						Node n = pin.getNode();
						if (!visited.contains(n) && !to_visit.contains(n))
						{
							to_visit.add(n);
						}
					}
				}
			}
			if (m_allowBackward)
			{
				for (int i = 0; i < current.getInputArity(); i++)
				{
					Collection<Pin<? extends Node>> pins = current.getInputLinks(i);
					for (Pin<? extends Node> pin : pins)
					{
						Node n = pin.getNode();
						if (!visited.contains(n) && !to_visit.contains(n))
						{
							to_visit.add(n);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Visits a node in the graph. This method is called exactly once for every
	 * visited node.
	 * @param n The node to visit
	 */
	public void visit(Node n)
	{
		// Do nothing. Override.
	}
}
