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
package ca.uqac.lif.petitpoucet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.dag.FlatteningCrawler;
import ca.uqac.lif.dag.Node;
import ca.uqac.lif.dag.NodeConnector;
import ca.uqac.lif.dag.Pin;

/**
 * Utility methods for transforming lineage graphs.
 * @author Sylvain Hallé
 */
public class GraphUtilities
{
	/**
	 * Simplifies a single-rooted lineage graph. This method is the combined
	 * application of {@link #squash(Node) squash()} and
	 * {@link #flatten(Node) flatten()}.
	 * @param root The root of the original graph
	 * @return The root of the squashed graph
	 */
	public static Node simplify(Node root)
	{
		return squash(flatten(root));
	}
	
	/**
	 * Out of a single-rooted lineage graph, creates another graph where nested
	 * nodes are exploded.
	 * @param root The root of the original graph
	 * @return The root of the simplified graph
	 */
	public static Node flatten(Node root)
	{
		FlatteningCrawler fc = new FlatteningCrawler(root, NodeConnector.instance);
		fc.crawl();
		return fc.getRootCopy();
	}
	
	/**
	 * Out of a single-rooted lineage graph, creates another graph where only
	 * Boolean nodes and leaves are kept.
	 * @param root The root of the original graph
	 * @return The root of the squashed graph
	 */
	public static Node squash(Node root)
	{
		Node new_root = root.duplicate();
		for (int i = 0; i < root.getOutputArity(); i++)
		{
			for (Pin<? extends Node> pin : root.getOutputLinks(i))
			{
				squash(new_root, i, pin, new HashSet<Node>(), new HashMap<Node,Node>());
			}
		}
		return new_root;
	}
	
	protected static void squash(Node parent, int pin_index, Pin<? extends Node> pin, Set<Node> visited, Map<Node,Node> duplicates)
	{
		Node target = pin.getNode();
		Node target_dup = null;
		if (duplicates.containsKey(target))
		{
			target_dup = duplicates.get(target);
		}
		else
		{
			target_dup = target.duplicate();
			duplicates.put(target, target_dup);
		}
		visited.add(parent);
		Node out_parent = parent;
		if (!(target instanceof AndNode) && !(target instanceof OrNode))
		{
			if (isLeaf(target))
			{
				NodeConnector.connect(parent, pin_index, target_dup, pin.getIndex());
				return;
			}
		}
		if (target instanceof AndNode && !(parent instanceof AndNode))
		{
			NodeConnector.connect(parent, pin_index, target_dup, pin.getIndex());
			out_parent = target_dup;
		}
		if (target instanceof OrNode && !(parent instanceof OrNode))
		{
			NodeConnector.connect(parent, pin_index, target_dup, pin.getIndex());
			out_parent = target_dup;
		}
		if (!visited.contains(target))
		{
			for (int i = 0; i < target.getOutputArity(); i++)
			{
				for (Pin<? extends Node> t_pin : target.getOutputLinks(i))
				{
					squash(out_parent, i, t_pin, visited, duplicates);
				}
			}
		}
	}
	
	/**
	 * Determines if a node is a leaf.
	 * @param n The node
	 * @return {@code true} if the node is a leaf, {@code false} otherwise.
	 */
	public static boolean isLeaf(Node n)
	{
		for (int i = 0; i < n.getOutputArity(); i++)
		{
			if (!n.getOutputLinks(i).isEmpty())
			{
				return false;
			}
		}
		return true;
	}
}
