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
package ca.uqac.lif.dag;

import java.util.HashSet;
import java.util.Set;

import ca.uqac.lif.petitpoucet.PartNode;

/**
 * A crawler that traverses an explanation graph and fetches all the ranges
 * mentioned in leaf nodes. By default, the leaf crawler only traverses edges
 * in the forward direction from its starting point. This can be overridden
 * using {@link #allowBackward(boolean)}.
 */
public abstract class LeafCrawler extends Crawler
{
	/**
	 * Creates a new leaf crawler.
	 * @param start The starting point of the crawl
	 */
	protected LeafCrawler(/*@ non_null @*/ Node start)
	{
		super(start);
		m_allowBackward = false;
	}
	
	@Override
	public final void visit(/*@ non_null @*/ Node n)
	{
		if (isLeaf(n))
		{
			visitLeaf(n);
		}
	}
	
	/**
	 * Determines if a node is a leaf PartNode.
	 * @param n The node
	 * @return <tt>true</tt> if n is a part node and a leaf, <tt>false</tt>
	 * otherwise
	 */
	protected static boolean isLeaf(/*@ non_null @*/ Node n)
	{
		if (!(n instanceof PartNode))
		{
			return false;
		}
		for (int i = 0; i < n.getOutputArity(); i++)
		{
			if (!n.getOutputLinks(i).isEmpty())
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Visits a leaf node of the graph.
	 * @param n The node
	 */
	protected abstract void visitLeaf(Node n);
	
	/**
	 * A simple leaf crawler that fetches the leaves of a DAG.
	 */
	public static class LeafFetcher extends LeafCrawler
	{
		/*@ non_null @*/protected Set<Node> m_leaves;
		
		public LeafFetcher(/*@ non_null @*/ Node start) 
		{
			super(start);
			m_leaves = new HashSet<Node>();
		}

		@Override
		protected void visitLeaf(Node n)
		{
			m_leaves.add(n);
		}
		
		/*@ pure non_null @*/ public Set<Node> getLeaves()
		{
			return m_leaves;
		}
	}
}